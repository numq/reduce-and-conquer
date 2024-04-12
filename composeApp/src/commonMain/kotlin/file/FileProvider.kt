package file

import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.readResourceBytes

interface FileProvider {
    suspend fun open(path: String): Result<ByteArray>

    @OptIn(InternalResourceApi::class)
    class Implementation : FileProvider {
        override suspend fun open(path: String) = runCatching {
            readResourceBytes(path)
        }
    }
}