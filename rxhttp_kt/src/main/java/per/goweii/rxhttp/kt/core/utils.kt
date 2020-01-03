package per.goweii.rxhttp.kt.core

import android.os.Environment
import java.io.File

/**
 * <p>文件描述：<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2019/12/30 0030 <p>
 * <p>@update 2019/12/30 0030<p>
 * <p>版本号：1<p>
 *
 */

fun checkBaseUrl(url: String): String {
    return if (url.endsWith("/")) {
        url
    } else {
        "$url/"
    }
}

    fun getCacheDir(): String {
        var cacheFile: File? = null
        if (isSDCardAlive()) {
            cacheFile = RxHttp.mAppContext!!.externalCacheDir
        }

        if (cacheFile == null) {
            cacheFile = RxHttp.mAppContext!!.cacheDir
        }
        return cacheFile!!.absolutePath
    }



    fun getDownloadCacheDir(): String {
        var dir: File? = null
        if (isSDCardAlive()) {
            dir = RxHttp.mAppContext!!.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        }
        if(dir == null){
            dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        }
        return dir!!.absolutePath
    }

     fun isSDCardAlive(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }
