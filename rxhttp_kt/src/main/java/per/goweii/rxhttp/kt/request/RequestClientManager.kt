package per.goweii.rxhttp.kt.request

import android.text.TextUtils
import com.google.gson.Gson
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import per.goweii.rxhttp.kt.core.RxHttp
import per.goweii.rxhttp.kt.core.checkBaseUrl
import per.goweii.rxhttp.kt.core.cookie.CookieJarImpl
import per.goweii.rxhttp.kt.core.cookie.store.PersistentCookieStore
import per.goweii.rxhttp.kt.core.getCacheDir
import per.goweii.rxhttp.kt.core.manager.BaseClientManager
import per.goweii.rxhttp.kt.request.interceptor.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * <p>文件描述：<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2020/1/1 0001 <p>
 * <p>@update 2020/1/1 0001<p>
 * <p>版本号：1<p>
 *
 */
object RequestClientManager : BaseClientManager() {


    private var mRetrofit: Retrofit = create()
    private val mRetrofitMap = HashMap<Class<*>, Retrofit>()
    private var mOkHttpClient: OkHttpClient? = null
    override fun create(isGson:Boolean): Retrofit {
        return create(RxHttp.getRequestSetting()?.getBaseUrl()!!,isGson)
    }

    private fun create(baseUrl: String,isGson: Boolean): Retrofit {
        if (mOkHttpClient == null) {
            mOkHttpClient = createOkHttpClient()
        }
        val builder: Retrofit.Builder = Retrofit.Builder()
            .client(mOkHttpClient)
            .baseUrl(checkBaseUrl(baseUrl))
        builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        if(isGson){
            val gson = RxHttp.getRequestSetting()?.getGson() ?: Gson()
            builder.addConverterFactory(GsonConverterFactory.create(gson))
        }else{
            builder.addConverterFactory(ScalarsConverterFactory.create())
        }
        return builder.build()
    }
    fun refreshBaseUrl(baseUrl: String){
        mRetrofit = create(baseUrl,true)
    }

    /**
     * 创建Api接口实例
     *
     * @param clazz Api接口类
     * @param <T>   Api接口
     * @return Api接口实例
    </T> */
    internal fun <T> getService(clazz: Class<T>,isGson: Boolean): T {
        return getRetrofit(clazz,isGson).create(clazz)
    }

    private fun getRetrofit(clazz: Class<*>?,isGson: Boolean): Retrofit {

        clazz ?: return mRetrofit
        var retrofit: Retrofit? = null
        if (mRetrofitMap.isNotEmpty()) {
            val iterator = mRetrofitMap.entries.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                if (TextUtils.equals(entry.key.name, clazz.name)) {
                    retrofit = entry.value
                    iterator.remove()
                    break
                }
            }
        }
        if (retrofit != null) {
            return retrofit
        }
        val baseUrlMap = RxHttp.getRequestSetting()?.getServiceBaseUrl()
        if (baseUrlMap.isNullOrEmpty().not()) {
            return mRetrofit
        }
        var baseUrl: String? = null
        for ((key, value) in baseUrlMap!!) {
            if (TextUtils.equals(key.name, clazz.name)) {
                baseUrl = value
                break
            }
        }
        if (baseUrl == null) {
            return mRetrofit
        }
        retrofit = create(baseUrl,isGson)
        mRetrofitMap[clazz] = retrofit
        return retrofit
    }

    /**
     * 创建OkHttpClient实例
     *
     * @return OkHttpClient
     */
    private fun createOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        // 设置调试模式打印日志
        if (RxHttp.getRequestSetting()?.isDebug()?.first == true) {
            val logging = HttpLoggingInterceptor()
            logging.level = RxHttp.getRequestSetting()?.isDebug()!!.second
            builder.addInterceptor(logging)
        }
        builder.cookieJar(CookieJarImpl(PersistentCookieStore(RxHttp.mAppContext!!)))
        // 设置缓存
        builder.cache(createCache())
        // 设置3个超时时长
        val timeout = RxHttp.getRequestSetting()?.getConnectTimeout() ?: 0
        val connectTimeout = RxHttp.getRequestSetting()?.getConnectTimeout() ?: 0
        val readTimeout = RxHttp.getRequestSetting()?.getReadTimeout() ?: 0
        val writeTimeout = RxHttp.getRequestSetting()?.getWriteTimeout() ?: 0

        builder.connectTimeout(if(connectTimeout>0)connectTimeout else timeout ,TimeUnit.MILLISECONDS)
        builder.writeTimeout(if(writeTimeout >0) writeTimeout else timeout,TimeUnit.MILLISECONDS)
        builder.readTimeout(if(readTimeout >0) readTimeout else timeout ,TimeUnit.MILLISECONDS)
        if(writeTimeout>0){

        }
        // 设置应用层拦截器
        BaseUrlRedirectInterceptor.addTo(builder)
        PublicHeadersInterceptor.addTo(builder)
        PublicQueryParameterInterceptor.addTo(builder)
        CacheControlInterceptor.addTo(builder)
        val interceptors = RxHttp.getRequestSetting()!!.getInterceptors()
        if (interceptors.isNullOrEmpty().not()) {
            for (interceptor in interceptors) {
                builder.addInterceptor(interceptor)
            }
        }
        // 设置网络层拦截器
        CacheControlNetworkInterceptor.addTo(builder)
        val networkInterceptors = RxHttp.getRequestSetting()?.getNetworkInterceptors()
        if (networkInterceptors.isNullOrEmpty().not()) {
            for (interceptor in networkInterceptors!!) {
                builder.addNetworkInterceptor(interceptor)
            }
        }
        RxHttp.getRequestSetting()?.setOkHttpClient(builder)
        return builder.build()
    }

    /**
     * 创建缓存
     *
     * @return Cache
     */
    private fun createCache(): Cache {
        val cacheFile = File(getCacheDir(), RxHttp.getRequestSetting()?.getCacheDirName())
        if (!cacheFile.exists()) {
            cacheFile.mkdirs()
        }
        return Cache(cacheFile, RxHttp.getRequestSetting()?.getCacheSize() ?: 0L)
    }

    /**
     * 取消请求
     */
    fun cancelAll(tag: Any?) {
        mOkHttpClient ?: return
        mOkHttpClient!!.dispatcher.queuedCalls().forEach {
            if (tag != null) {
                if (it.request().tag() == tag) {
                    it.cancel()
                }
            } else {
                it.cancel()
            }
        }
        mOkHttpClient!!.dispatcher.runningCalls().forEach {
            if (tag != null) {
                if (it.request().tag() == tag) {
                    it.cancel()
                }
            } else {
                it.cancel()
            }
        }
    }
}