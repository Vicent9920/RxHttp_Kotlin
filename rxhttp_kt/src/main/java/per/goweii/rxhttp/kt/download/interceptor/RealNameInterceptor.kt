package per.goweii.rxhttp.kt.download.interceptor

import android.text.TextUtils
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException

/**
 * <p>文件描述：<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2020/1/1 0001 <p>
 * <p>@update 2020/1/1 0001<p>
 * <p>版本号：1<p>
 *
 */
class RealNameInterceptor: Interceptor {

    companion object{
        @JvmStatic
        fun addTo(builder: OkHttpClient.Builder) {
            builder.addInterceptor(RealNameInterceptor())
        }
    }


    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val disposition = response.header("Content-Disposition")
        if (!TextUtils.isEmpty(disposition)) {
            val index = disposition!!.indexOf("filename=")
            if (index >= 0) {
                var name = disposition.substring(index + 9, disposition.length)
                name = name.replace("UTF-8", "")
                name = name.replace("\"", "")
                if (!TextUtils.isEmpty(name)) {
                    val responseBody = DownloadResponseBody(response.body()!!)
                    responseBody.realName = name
                    return response.newBuilder()
                            .body(responseBody)
                            .build()
                }
            }
        }
        return response
    }
}