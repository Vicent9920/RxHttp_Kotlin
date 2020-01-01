package per.goweii.rxhttp.kt.request.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import per.goweii.rxhttp.kt.core.RxHttp

object NetUtils {

    /**
     * 判断是否有网络
     */
    @SuppressLint("MissingPermission")
    fun isConnected(): Boolean {
        val connectivityManager = RxHttp.mAppContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val networkInfo = connectivityManager.activeNetworkInfo
            if (networkInfo != null) {
                return networkInfo.isAvailable
            }
        }
        return false
    }
}