# NFC2BT

Unfinished experiment exchanging Bluetooth addresses and ECDH public keys over NFC for out-of-band pairing.

## Details

The code attempts NFC foreground dispatch and NDEF push, then derives a shared key from a received public key. It selects a BC2 provider that the source marks as nonworking, and the bonding code is commented out. The screenshot shows its existing unsupported-hardware dialog on an emulator; NFC exchange and Bluetooth pairing were not verified.

This is a legacy Eclipse/ADT project. Its declared SDK and library references are in [project.properties](project.properties).

## Screenshot

![NFC2BT showing its unsupported-hardware dialog on the emulator](screenshot.png)
