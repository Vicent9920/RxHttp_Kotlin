package per.goweii.rxhttp.kt.request.interceptor

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import per.goweii.rxhttp.kt.core.RxHttp
import per.goweii.rxhttp.kt.request.setting.ParameterGetter
import per.goweii.rxhttp.kt.request.utils.NonNullUtils
import java.io.IOException

/**
 * <p>文件描述：添加公共请求参数<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2019/12/30 0030 <p>
 * <p>@update 2019/12/30 0030<p>
 * <p>版本号：1<p>
 *
 */
class PublicQueryParameterInterceptor private constructor(): Interceptor {
    companion object{
        @JvmStatic
        fun addTo( builder: OkHttpClient.Builder) {
            val staticParameters: Map<String, String>? = RxHttp.getRequestSetting()?.getStaticPublicQueryParameter()
            val dynamicParameters: Map<String, ParameterGetter>? = RxHttp.getRequestSetting()?.getDynamicPublicQueryParameter()
            if (NonNullUtils.check(staticParameters, dynamicParameters)) {
                builder.addInterceptor(PublicQueryParameterInterceptor())
            }
        }
    }


    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.url.newBuilder()
        val staticParameters: Map<String, String>? = RxHttp.getRequestSetting()?.getStaticPublicQueryParameter()
        if (NonNullUtils.check(staticParameters)) {
            for ((key, value) in staticParameters!!) {
                builder.addQueryParameter(key, value)
            }
        }
        val dynamicParameters: Map<String, ParameterGetter>? = RxHttp.getRequestSetting()?.getDynamicPublicQueryParameter()
        if (NonNullUtils.check(dynamicParameters)) {
            for ((key, value) in dynamicParameters!!) {
                builder.addQueryParameter(key, value.get())
            }
        }
        val request = original.newBuilder()
                .method(original.method, original.body)
                .url(builder.build())
                .build()
        return chain.proceed(request)
    }
}