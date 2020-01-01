package per.goweii.rxhttp.kt.request.interceptor

import android.text.TextUtils
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import per.goweii.rxhttp.kt.request.Api
import per.goweii.rxhttp.kt.request.utils.NonNullUtils

/**
 * <p>文件描述：缓存过滤器<p>
 * <p>在基类过滤掉非GET请求和未配置{@link Api.Header#CACHE_ALIVE_SECOND}的请求<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2019/12/30 0030 <p>
 * <p>@update 2019/12/30 0030<p>
 * <p>版本号：1<p>
 *
 */
open class BaseCacheControlInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (!TextUtils.equals(request.method(), "GET")) {
            return chain.proceed(request)
        }
        val headers = request.headers(Api.Header.CACHE_ALIVE_SECOND)
        if (!NonNullUtils.check(headers)) {
            return chain.proceed(request)
        }
        val age: Int = getCacheControlAge(headers[0])
        val requestCached: Request = getCacheRequest(request, age)
        val response = chain.proceed(requestCached)
        return getCacheResponse(response, age)
    }
    open fun getCacheRequest(request: Request, age: Int): Request{
        return request
    }
    open fun getCacheResponse(response: Response, age: Int): Response {
        return response
    }
    private fun getCacheControlAge(age: String): Int {
        return try {
            age.toInt()
        } catch (ignore: NumberFormatException) {
            0
        }
    }
}