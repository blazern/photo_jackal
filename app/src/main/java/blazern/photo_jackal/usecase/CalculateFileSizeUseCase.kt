package blazern.photo_jackal.usecase

import android.app.Application
import android.net.Uri
import blazern.photo_jackal.util.calculateFileSize
import javax.inject.Inject

class CalculateFileSizeUseCase @Inject constructor(private val context: Application) {
    suspend fun execute(file: Uri): Int? {
        return context.calculateFileSize(file)
    }
}
