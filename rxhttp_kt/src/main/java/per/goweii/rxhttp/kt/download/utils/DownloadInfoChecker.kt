package per.goweii.rxhttp.kt.download.utils

import android.text.TextUtils
import per.goweii.rxhttp.kt.core.RxHttp
import per.goweii.rxhttp.kt.core.getDownloadCacheDir
import per.goweii.rxhttp.kt.download.DownloadInfo
import per.goweii.rxhttp.kt.download.Mode
import per.goweii.rxhttp.kt.download.exception.RangeLengthIsZeroException
import per.goweii.rxhttp.kt.download.exception.SaveFileBrokenPointException
import java.io.File

object DownloadInfoChecker {

    @Throws(SaveFileBrokenPointException::class)
    fun checkDownloadLength(info: DownloadInfo) {
        if (info.downloadLength == 0L) {
            val file = createFile(info.saveDirPath, info.saveFileName)
            if (file != null && file.exists()) {
                if (info.mode == Mode.APPEND) {
                    info.downloadLength = file.length()
                } else if (info.mode == Mode.REPLACE) {
                    file.delete()
                } else {
                    info.saveFileName = renameFileName(info.saveFileName)
                }
            }
        } else {
            val file = createFile(info.saveDirPath, info.saveFileName)
            if (file != null && file.exists()) {
                if (info.downloadLength != file.length()) {
                    throw SaveFileBrokenPointException()
                }
            } else {
                info.downloadLength = 0
            }
        }
    }

    @Throws(RangeLengthIsZeroException::class)
    fun checkContentLength(info: DownloadInfo) {
        if (info.downloadLength > 0 && info.contentLength > 0 && info.contentLength <= info.downloadLength) {
            throw RangeLengthIsZeroException()
        }
    }

    private fun renameFileName(fileName: String?): String? {
        if(fileName == null)return null
        var nameLeft: String
        val nameDivide: String
        val nameRight: String
        val index = fileName.lastIndexOf(".")
        if (index >= 0) {
            nameLeft = fileName.substring(0, index)
            nameDivide = "."
            nameRight = fileName.substring(index + 1, fileName.length)
        } else {
            nameLeft = fileName
            nameDivide = ""
            nameRight = ""
        }
        val k1 = nameLeft.lastIndexOf("(")
        val k2 = nameLeft.lastIndexOf(")")
        var i = 1
        if (k2 + 1 == nameLeft.length && k1 >= 0 && k2 >= 0 && k2 > k1) {
            val num = nameLeft.substring(k1 + 1, k2)
            nameLeft = nameLeft.substring(0, k1)
            try {
                i = num.toInt()
                i += 1
            } catch (ignore: NumberFormatException) {
            }
        }
        return "$nameLeft($i)$nameDivide$nameRight"
    }

    private fun createFile(dirPath: String?, fileName: String?): File? {
        return if (TextUtils.isEmpty(dirPath) || TextUtils.isEmpty(fileName)) {
            null
        } else File(dirPath, fileName)
    }

    fun checkDirPath(info: DownloadInfo) {
        if (TextUtils.isEmpty(info.saveDirPath)) {
            info.saveDirPath = RxHttp.getDownloadSetting().getSaveDirPath()
        }
        if (TextUtils.isEmpty(info.saveDirPath)) {
            info.saveDirPath = getDownloadCacheDir()
        }
    }

    fun checkFileName(info: DownloadInfo) {
        if (TextUtils.isEmpty(info.saveFileName)) {
            info.saveFileName = System.currentTimeMillis().toString() + ".rxdownload"
        }
    }
}