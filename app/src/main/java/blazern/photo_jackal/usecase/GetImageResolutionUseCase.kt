package blazern.photo_jackal.usecase

import android.app.Application
import android.net.Uri
import android.util.Size
import blazern.photo_jackal.util.getImageResolution
import javax.inject.Inject

class GetImageResolutionUseCase @Inject constructor(private val context: Application) {
    suspend fun execute(imageUri: Uri): Size? {
        return context.getImageResolution(imageUri)
    }
}
