#!/usr/bin/env python3
"""
Lightweight MQTT connectivity probe for quick debugging.

It sends a raw MQTT 3.1.1 CONNECT packet and reads CONNACK from:
- tcp://host:1883
- ssl://host:8883

No third-party dependencies required.
"""

from __future__ import annotations

import argparse
import os
import socket
import ssl
import sys
from dataclasses import dataclass
from typing import Optional


RC_TEXT = {
    0: "Connection accepted",
    1: "Unacceptable protocol version",
    2: "Identifier rejected (client id)",
    3: "Server unavailable",
    4: "Bad username or password",
    5: "Not authorized",
}


@dataclass
class ProbeResult:
    endpoint: str
    client_id: str
    ok: bool
    detail: str


def encode_utf8(value: str) -> bytes:
    raw = value.encode("utf-8")
    return len(raw).to_bytes(2, "big") + raw


def encode_remaining_length(length: int) -> bytes:
    out = bytearray()
    x = length
    while True:
        encoded = x % 128
        x //= 128
        if x > 0:
            encoded |= 0x80
        out.append(encoded)
        if x == 0:
            break
    return bytes(out)


def build_connect_packet(client_id: str, keepalive: int = 20) -> bytes:
    # MQTT 3.1.1
    protocol_name = encode_utf8("MQTT")
    protocol_level = b"\x04"
    connect_flags = b"\x02"  # clean session only
    keepalive_bytes = keepalive.to_bytes(2, "big")
    variable_header = protocol_name + protocol_level + connect_flags + keepalive_bytes
    payload = encode_utf8(client_id)
    remaining = encode_remaining_length(len(variable_header) + len(payload))
    return b"\x10" + remaining + variable_header + payload


def recv_exact(sock: socket.socket, n: int) -> bytes:
    data = bytearray()
    while len(data) < n:
        chunk = sock.recv(n - len(data))
        if not chunk:
            break
        data.extend(chunk)
    return bytes(data)


def probe_once(
    host: str,
    port: int,
    tls: bool,
    timeout: float,
    client_id: str,
    insecure_tls: bool = False,
) -> ProbeResult:
    endpoint = f"{'ssl' if tls else 'tcp'}://{host}:{port}"
    sock: Optional[socket.socket] = None
    try:
        base_sock = socket.create_connection((host, port), timeout=timeout)
        if tls:
            if insecure_tls:
                ctx = ssl._create_unverified_context()
            else:
                ctx = ssl.create_default_context()
            sock = ctx.wrap_socket(base_sock, server_hostname=host)
        else:
            sock = base_sock
        sock.settimeout(timeout)

        sock.sendall(build_connect_packet(client_id=client_id))
        header = recv_exact(sock, 1)
        if not header:
            return ProbeResult(endpoint, client_id, False, "No response (EOF)")
        packet_type = header[0] >> 4
        if packet_type != 2:
            return ProbeResult(endpoint, client_id, False, f"Unexpected packet type: {packet_type}")

        rem_len_first = recv_exact(sock, 1)
        if not rem_len_first:
            return ProbeResult(endpoint, client_id, False, "Malformed CONNACK: missing remaining length")
        rem_len = rem_len_first[0]
        body = recv_exact(sock, rem_len)
        if len(body) < 2:
            return ProbeResult(endpoint, client_id, False, "Malformed CONNACK body")

        session_present = body[0] & 0x01
        rc = body[1]
        if rc == 0:
            try:
                sock.sendall(b"\xE0\x00")  # DISCONNECT
            except Exception:
                pass
            return ProbeResult(
                endpoint,
                client_id,
                True,
                f"CONNACK rc=0 ({RC_TEXT[0]}), session_present={session_present}",
            )

        text = RC_TEXT.get(rc, "Unknown return code")
        return ProbeResult(endpoint, client_id, False, f"CONNACK rc={rc} ({text})")
    except Exception as e:
        return ProbeResult(endpoint, client_id, False, f"{type(e).__name__}: {e}")
    finally:
        if sock is not None:
            try:
                sock.close()
            except Exception:
                pass


def default_client_id() -> str:
    return f"MagicAppDbg-{os.urandom(3).hex()}"


def parse_endpoints(raw: str) -> list[tuple[int, bool]]:
    endpoints: list[tuple[int, bool]] = []
    for item in raw.split(","):
        item = item.strip()
        if not item:
            continue
        port = int(item)
        tls = port == 8883
        endpoints.append((port, tls))
    if not endpoints:
        raise ValueError("No endpoints specified")
    return endpoints


def main() -> int:
    parser = argparse.ArgumentParser(description="Probe MQTT broker connectivity.")
    parser.add_argument("--host", default="broker.hivemq.com", help="Broker hostname")
    parser.add_argument(
        "--endpoints",
        default="1883,8883",
        help="Comma-separated ports to test (default: 1883,8883)",
    )
    parser.add_argument(
        "--client-id",
        default=default_client_id(),
        help="Primary MQTT client id to test",
    )
    parser.add_argument(
        "--also-test-short-id",
        action="store_true",
        help="Also test with short fallback client id 'magicdbg'",
    )
    parser.add_argument(
        "--insecure-tls",
        action="store_true",
        help="Disable TLS certificate verification for ssl endpoints (debug only).",
    )
    parser.add_argument("--timeout", type=float, default=8.0, help="Socket timeout in seconds")
    args = parser.parse_args()

    try:
        endpoints = parse_endpoints(args.endpoints)
    except Exception as e:
        print(f"Invalid endpoints: {e}", file=sys.stderr)
        return 2

    ids = [args.client_id]
    if args.also_test_short_id and "magicdbg" not in ids:
        ids.append("magicdbg")

    any_ok = False
    print(
        "MQTT probe "
        f"host={args.host} endpoints={args.endpoints} ids={ids} insecure_tls={args.insecure_tls}"
    )
    for cid in ids:
        for port, tls in endpoints:
            result = probe_once(
                host=args.host,
                port=port,
                tls=tls,
                timeout=args.timeout,
                client_id=cid,
                insecure_tls=args.insecure_tls,
            )
            status = "OK" if result.ok else "FAIL"
            print(f"[{status}] {result.endpoint} client_id='{result.client_id}' -> {result.detail}")
            any_ok = any_ok or result.ok

    return 0 if any_ok else 1


if __name__ == "__main__":
    raise SystemExit(main())
