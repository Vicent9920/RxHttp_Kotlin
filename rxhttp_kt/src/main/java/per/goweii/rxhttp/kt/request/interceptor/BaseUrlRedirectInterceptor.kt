package per.goweii.rxhttp.kt.request.interceptor

import android.text.TextUtils
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import per.goweii.rxhttp.kt.core.RxHttp
import per.goweii.rxhttp.kt.core.checkBaseUrl
import per.goweii.rxhttp.kt.request.Api
import per.goweii.rxhttp.kt.request.utils.NonNullUtils
import java.io.IOException
import java.util.*

/**
 * <p>文件描述：BaseUrl重定向<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2019/12/30 0030 <p>
 * <p>@update 2019/12/30 0030<p>
 * <p>版本号：1<p>
 *
 */
class BaseUrlRedirectInterceptor private constructor(): Interceptor {
    companion object{
        @JvmStatic
        fun addTo(builder: OkHttpClient.Builder) {
            val urls: Map<String, String>? = RxHttp.getRequestSetting()?.getRedirectBaseUrl()
            if (NonNullUtils.check(urls)) {
                builder.addInterceptor(BaseUrlRedirectInterceptor())
            }
        }
    }



    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val urls: Map<String, String>? = RxHttp.getRequestSetting()?.getRedirectBaseUrl()
        if (!NonNullUtils.check(urls)) {
            return chain.proceed(original)
        }
        val urlNames = original.headers(Api.Header.BASE_URL_REDIRECT)
        if (!NonNullUtils.check(urlNames)) {
            return chain.proceed(original)
        }
        val builder = original.newBuilder()
        builder.removeHeader(Api.Header.BASE_URL_REDIRECT)
        val urlName = urlNames[0]
        val newUrl = urls!![urlName] ?: return chain.proceed(original)

        val newHttpUrl = newUrl.toHttpUrlOrNull()
                ?: return chain.proceed(original)
        val oldHttpUrl = original.url
        val pathSegments: MutableList<String> = ArrayList(oldHttpUrl.pathSegments)
        val oldCount = defaultBaseUrlPathSegmentCount()
        for (i in 0 until oldCount) {
            pathSegments.removeAt(0)
        }
        val newHttpUrlBuilder = oldHttpUrl.newBuilder()
                .scheme(newHttpUrl.scheme)
                .host(newHttpUrl.host)
                .port(newHttpUrl.port)
        val size1 = newHttpUrl.pathSegments.size
        for (i in size1 - 1 downTo 0) {
            val segment = newHttpUrl.pathSegments[i]
            if (TextUtils.isEmpty(segment)) {
                continue
            }
            pathSegments.add(0, segment)
        }
        val size2 = oldHttpUrl.pathSegments.size
        for (i in 0 until size2) {
            newHttpUrlBuilder.removePathSegment(0)
        }
        for (i in pathSegments.indices) {
            newHttpUrlBuilder.addPathSegment(pathSegments[i])
        }
        val newRequest = builder.url(newHttpUrlBuilder.build()).build()
        return chain.proceed(newRequest)
    }

    private fun defaultBaseUrlPathSegmentCount(): Int {
        val oldHttpUrl = checkBaseUrl(RxHttp.getRequestSetting()?.getBaseUrl()!!).toHttpUrlOrNull()
                ?: return 0
        val oldSegments = oldHttpUrl.pathSegments
        if (oldSegments.isNullOrEmpty()) {
            return 0
        }
        var count = oldSegments.size
        if (TextUtils.isEmpty(oldSegments[count - 1])) {
            count--
        }
        return count
    }
}