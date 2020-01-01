package per.goweii.rxhttp.kt.request.utils

import android.text.TextUtils
import android.webkit.MimeTypeMap
import java.io.File
import java.util.*

/**
 * <p>文件描述：<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2019/12/30 0030 <p>
 * <p>@update 2019/12/30 0030<p>
 * <p>版本号：1<p>
 *
 */
object FileUtils {
    private fun getSuffix(file: File?): String {
        if (file == null || !file.exists() || file.isDirectory) {
            return ""
        }
        val fileName = file.name
        if (fileName.endsWith(".")) {
            return ""
        }
        val index = fileName.lastIndexOf(".")
        return if (index < 0) {
            ""
        } else fileName.substring(index + 1).toLowerCase(Locale.US)
    }

    fun getMimeType(file: File?): String? {
        val suffix = getSuffix(file)
        var mimeType: String? = null
        if (!TextUtils.isEmpty(suffix)) {
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffix)
        }
        if (TextUtils.isEmpty(mimeType)) {
            mimeType = "file/*"
        }
        return mimeType
    }
}