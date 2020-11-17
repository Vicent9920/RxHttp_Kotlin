package per.goweii.rxhttp.kt.request.setting

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import per.goweii.rxhttp.kt.request.exception.ExceptionHandle

/**
 * <p>文件描述：<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2019/12/30 0030 <p>
 * <p>@update 2019/12/30 0030<p>
 * <p>版本号：1.01<p>
 *
 */
abstract class DefaultRequestSetting : RequestSetting{


    override fun getRedirectBaseUrl(): Map<String, String>{
        return HashMap()
    }

    override fun getServiceBaseUrl(): Map<Class<*>, String> {
        return HashMap()
    }

    override fun getMultiHttpCode(): (code: Int) -> Boolean {
        return {
            false
        }
    }
    override fun getMultiSuccessCode(): IntArray {
        return intArrayOf()
    }

    override fun getTimeout(): Long {
        return 1000*10
    }

    override fun getConnectTimeout(): Long {
        return 0
    }

    override fun getReadTimeout(): Long {
        return 0
    }

    override fun getWriteTimeout(): Long {
        return 0
    }

    override fun getCacheDirName(): String {
        return "rxhttp_cache"
    }

    override fun getCacheSize(): Long {
        return 10*1024*1024
    }

    override fun getStaticPublicQueryParameter(): Map<String, String>{
        return HashMap()
    }

    override fun getDynamicPublicQueryParameter(): Map<String, ParameterGetter> {
        return HashMap()
    }

    override fun getStaticHeaderParameter(): Map<String, String> {
        return HashMap()
    }

    override fun getDynamicHeaderParameter(): Map<String, ParameterGetter> {
        return HashMap()
    }

    override fun <E : ExceptionHandle> getExceptionHandle(t:Throwable): E? {
        return null
    }

    override fun getRealErrorMsg() = false

    override fun getInterceptors(): Array<Interceptor> {
        return arrayOf()
    }

    override fun getNetworkInterceptors(): Array<Interceptor> {
        return arrayOf()
    }

//    override fun ignoreSslForHttps(): Boolean {
//        return false
//    }
//
//    override fun enableTls12BelowAndroidKitkat(): Boolean {
//        return true
//    }

    override fun setOkHttpClient(builder: OkHttpClient.Builder) {

    }

    override fun getGson(): Gson? {
        return null
    }

    override fun isDebug(): Pair<Boolean,HttpLoggingInterceptor.Level> {
        return Pair(false,HttpLoggingInterceptor.Level.BODY)
    }
}