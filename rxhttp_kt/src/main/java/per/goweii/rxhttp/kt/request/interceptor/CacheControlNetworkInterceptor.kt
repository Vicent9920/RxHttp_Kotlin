package per.goweii.rxhttp.kt.request.interceptor

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import per.goweii.rxhttp.kt.request.Api
import per.goweii.rxhttp.kt.request.utils.NetUtils

/**
 * <p>文件描述：缓存过滤器<p>
 * <p>用于为Response配置缓存策略<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2019/12/30 0030 <p>
 * <p>@update 2019/12/30 0030<p>
 * <p>版本号：1<p>
 *
 */
class CacheControlNetworkInterceptor private constructor(): BaseCacheControlInterceptor() {
    companion object{
        @JvmStatic
        fun addTo( builder: OkHttpClient.Builder) {
            builder.addNetworkInterceptor(CacheControlNetworkInterceptor())
        }
    }



     override fun getCacheRequest(request: Request, age: Int): Request {
        return request.newBuilder()
                .removeHeader(Api.Header.CACHE_ALIVE_SECOND)
                .build()
    }


     override fun getCacheResponse(response: Response, age: Int): Response {
        return if (NetUtils.isConnected()) {
            if (age <= 0) {
                response.newBuilder()
                        .removeHeader("Cache-Control")
                        .build()
            } else {
                response.newBuilder()
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "public, max-age=$age")
                        .build()
            }
        } else {
            response.newBuilder()
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + Int.MAX_VALUE)
                    .build()
        }
    }
}