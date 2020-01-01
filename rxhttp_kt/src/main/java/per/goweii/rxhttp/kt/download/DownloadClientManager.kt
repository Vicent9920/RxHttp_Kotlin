package per.goweii.rxhttp.kt.download

import okhttp3.OkHttpClient
import per.goweii.rxhttp.kt.core.RxHttp
import per.goweii.rxhttp.kt.core.checkBaseUrl
import per.goweii.rxhttp.kt.core.manager.BaseClientManager
import per.goweii.rxhttp.kt.download.interceptor.RealNameInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit


object DownloadClientManager : BaseClientManager() {
    private val mRetrofit: Retrofit = create()
    override fun create(): Retrofit {
        return Retrofit.Builder()
                .client(createOkHttpClient())
                .baseUrl(checkBaseUrl(RxHttp.getDownloadSetting().getBaseUrl()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    fun getService(): DownloadApi {
        return this.mRetrofit.create(DownloadApi::class.java)
    }

    private fun createOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        val timeout: Long = RxHttp.getDownloadSetting().getTimeout()
        val connectTimeout: Long = RxHttp.getDownloadSetting().getConnectTimeout()
        val readTimeout: Long = RxHttp.getDownloadSetting().getReadTimeout()
        val writeTimeout: Long = RxHttp.getDownloadSetting().getWriteTimeout()
        builder.connectTimeout(if (connectTimeout > 0) connectTimeout else timeout, TimeUnit.MILLISECONDS)
        builder.readTimeout(if (readTimeout > 0) readTimeout else timeout, TimeUnit.MILLISECONDS)
        builder.writeTimeout(if (writeTimeout > 0) writeTimeout else timeout, TimeUnit.MILLISECONDS)
        RealNameInterceptor.addTo(builder)
        return builder.build()
    }
}