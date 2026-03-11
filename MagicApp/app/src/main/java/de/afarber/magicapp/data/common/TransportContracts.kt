package de.afarber.magicapp.data.common

import kotlinx.coroutines.flow.StateFlow

interface StateHolder<S> {
    val state: StateFlow<S>
}

interface ClearableOutput {
    fun clearOutput()
}

interface ClosableRepo {
    fun close()
}

interface ConnectableRepo<C> {
    suspend fun connect(config: C)

    suspend fun disconnect()

    suspend fun reconnect(config: C) {
        disconnect()
        connect(config)
    }
}
