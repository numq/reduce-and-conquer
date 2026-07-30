package io.github.numq.reduceandconquer.example.file

import io.github.numq.shared.generated.resources.Res

interface FileProvider {
    suspend fun open(path: String): Result<ByteArray>

    class Implementation : FileProvider {
        override suspend fun open(path: String) = runCatching {
            Res.readBytes(path)
        }
    }
}