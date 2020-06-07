package per.goweii.rxhttp.kt.request.setting

import com.google.gson.Gson
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import per.goweii.rxhttp.kt.request.exception.ExceptionHandle

/**
 * <p>文件描述：网络请求设置<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2019/12/30 0030 <p>
 * <p>@update 2019/12/30 0030<p>
 * <p>版本号：1<p>
 *
 */
interface RequestSetting {

    /**
     * 设置默认BaseUrl
     */

    fun getBaseUrl(): String

    /**
     * 用于对不同的请求设置不同的BaseUrl
     * 需要配合Retrofit的@Headers注解使用
     * 如：@Headers({RxHttp.BASE_URL_REDIRECT + ":" + 别名})
     *
     * @return Map 别名,BaseUrl
     */
    fun getRedirectBaseUrl(): Map<String, String>



    /**
     * 用于对一组接口设置BaseUrl
     * 这种设置方法对资源占用较大，实现方式为每组的请求创建不同的Retrofit和OkHttpClient实例，设置均相同，及下面的设置
     * 建议在少数请求需要单独设置BaseUrl时使用{@link #getRedirectBaseUrl()}
     *
     * @return Map 接口类,BaseUrl
     */
    fun getServiceBaseUrl(): Map<Class<*>, String>

    fun getSuccessCode(): Int
    /** 参数错误、token失效等错误码 **/
    fun getMultiHttpCode(): ((code:Int) -> Boolean)

    fun getMultiSuccessCode(): IntArray

    /**
     * 获取默认超时时长，单位为毫秒数
     */
    fun getTimeout(): Long

    /**
     * 获取Connect超时时长，单位为毫秒数
     * 返回0则取getTimeout
     */
    fun getConnectTimeout(): Long

    /**
     * 获取Read超时时长，单位为毫秒数
     * 返回0则取getTimeout
     */
    fun getReadTimeout(): Long

    /**
     * 获取Write超时时长，单位为毫秒数
     * 返回0则取getTimeout
     */
    fun getWriteTimeout(): Long

    /**
     * 获取网络缓存的文件夹名
     */
    fun getCacheDirName(): String

    /**
     * 获取网络缓存的最大值
     */
    fun getCacheSize(): Long

    fun getStaticPublicQueryParameter(): Map<String, String>


    fun getDynamicPublicQueryParameter(): Map<String, ParameterGetter>

    fun getStaticHeaderParameter(): Map<String, String>

    fun getDynamicHeaderParameter(): Map<String, ParameterGetter>

    fun <E : ExceptionHandle> getExceptionHandle(t:Throwable): E?


    fun getInterceptors(): Array<Interceptor>


    fun getNetworkInterceptors(): Array<Interceptor>
//
//    /**
//     * 忽略HTTPS的证书验证
//     * 仅在后台未正确配置且着急调试时可临时置为true
//     *
//     * @return 建议为false
//     */
//    fun ignoreSslForHttps(): Boolean
//
//    /**
//     * android4.4及以下版本默认未开启Tls1.2
//     * 返回true则强制开启
//     */
//    fun enableTls12BelowAndroidKitkat(): Boolean

    /**
     * 在创建OkHttpClient之前调用，及框架完成所有配置后
     */
    fun setOkHttpClient(builder: OkHttpClient.Builder)

    /**
     * 在创建OkHttpClient之前调用，及框架完成所有配置后
     */
    fun getGson(): Gson?

    /**
     * 是否打开调试模式
     */
    fun isDebug(): Pair<Boolean,HttpLoggingInterceptor.Level>
}

