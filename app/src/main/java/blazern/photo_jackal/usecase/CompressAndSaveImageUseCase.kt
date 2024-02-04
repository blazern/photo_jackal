package blazern.photo_jackal.usecase

import android.app.Application
import android.net.Uri
import android.util.Size
import blazern.photo_jackal.MyFileProvider
import blazern.photo_jackal.util.compressImage
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class CompressAndSaveImageUseCase @Inject constructor(private val context: Application) {
    suspend fun execute(imageUri: Uri, compressionQuality: Float, finalSize: Size): Uri {
        val fileName = "compressed_image"
        val storageDir = MyFileProvider.getImagesCacheDir(context)
        val compressedFile = File.createTempFile(
            fileName,
            ".jpg",
            storageDir
        )
        val fileOutputStream = FileOutputStream(compressedFile)
        context.compressImage(imageUri, compressionQuality, finalSize, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()
        return Uri.fromFile(compressedFile)
    }
}
