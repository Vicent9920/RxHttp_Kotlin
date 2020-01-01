package per.goweii.rxhttp.kt.request.interceptor

import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.Request
import per.goweii.rxhttp.kt.request.utils.NetUtils
import java.util.concurrent.TimeUnit

/**
 * <p>文件描述：缓存过滤器<p>
 * <p>用于为Request配置缓存策略<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2019/12/30 0030 <p>
 * <p>@update 2019/12/30 0030<p>
 * <p>版本号：1<p>
 *
 */
class CacheControlInterceptor private constructor(): BaseCacheControlInterceptor() {
    companion object{
        @JvmStatic
        fun addTo(builder: OkHttpClient.Builder) {
            builder.addInterceptor(CacheControlInterceptor())
        }
    }




     override fun getCacheRequest(request: Request, age: Int): Request {
        return if (NetUtils.isConnected()) {
            if (age <= 0) {
                request.newBuilder()
                        .cacheControl(CacheControl.FORCE_NETWORK)
                        .build()
            } else {
                request.newBuilder()
                        .cacheControl(CacheControl.Builder().maxAge(age, TimeUnit.SECONDS).build())
                        .build()
            }
        } else {
            request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build()
        }
    }
}