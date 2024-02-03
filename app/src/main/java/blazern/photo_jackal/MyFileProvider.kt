package blazern.photo_jackal

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import java.io.File

class MyFileProvider : FileProvider(
    R.xml.filepaths
) {
    companion object {
        fun getImagesCacheDir(context: Context) =
            File(context.cacheDir, "images").apply { mkdirs() }

        fun getImageUri(context: Context): Uri {
            val directory = getImagesCacheDir(context)
            val file = File.createTempFile(
                "selected_image",
                ".jpg",
                directory,
            )
            return getUriForFile(
                context,
                file,
            )
        }

        fun getUriForFile(
            context: Context,
            file: File,
        ): Uri = getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        )
    }
}