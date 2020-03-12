# RxHttp_Kotlin

对RxJava2+Retrofit2+OkHttp3的封装，优雅实现接口请求和文件下载（根据Java版本修改）

[GitHub主页](https://github.com/Vicent9920)

[Demo下载](https://github.com/Vicent9920/RxHttp_Kotlin/raw/master/app/release/app-release.apk)

[Java版本](https://github.com/goweii/RxHttp)

## 添加依赖 ##

 添加`jitpack`仓库依赖
 
```
maven { url 'https://jitpack.io' }
```
添加依赖

```
implementation 'com.github.Vicent9920:RxHttp_Kotlin:1.0.2'
```

# 一、功能简介

- 网络请求（`RxRequest`）
  - 支持监听请求声明周期，如开始结束和网络错误
  - 支持多`BaseUrl`，可针对不同请求重定向
  - 支持针对不同请求设置不同缓存策略，如无网强制获取缓存，有网缓存有效`10`秒
  - 支持添加公共请求参数
  - 支持自定义异常处理和异常提示消息
  - 支持网络请求的任意实体（**原框架是不允许**）
  - 支持解析过程自定义（如`responseBody` \ `SuccessCode` 等自定义处理）
  - 支持Cookie管理
- 文件下载（`RxDownload`）
  - 支持断点续传
  - 支持下载进度回调
  - 支持下载速度回调
  - 支持下载过程状态监听
  - 支持在仅保存下载路径未保存进度时自动恢复断点续传
  - 支持自动获取真实文件名




# 二、发起请求之`RxRequest`

## 使用说明

### 一、初始化

1. 在`Application`或者引导页初始化

```
RxHttp.mAppContext = applicationContext
```

2. 初始化网络请求配置类继承`RequestSetting`或`DefaultRequestSetting`，并复写部分方法。

```
RxHttp.initRequest(object : DefaultRequestSetting() {
            // baseUrl 设置
            override fun getBaseUrl(): String {
                return FreeApi.Config.BASE_URL
            }

            // Code 判断
            override fun getSuccessCode(): Int {
                return FreeApi.Code.SUCCESS
            }

             /**
             * 根据后端返回的错误进行处理
             * （标准模式下有效，因为自定义实体请求无失败回调，但是可以通过网络生命周期的异常回调来处理）
             * 未处理的返回false，会进入请求失败的回调
             * 返回true 意味着消费当前事件，不会进入请求成功或者失败的回调
             */
            override fun getMultiHttpCode(): (code: Int) -> Boolean {
                return {
                    when(it){
                        404 -> {
                            true
                        }
                        500 -> {
                            true
                        }
                        else -> false
                    }
                }
            }

            // 重定向地址设置
            override fun getRedirectBaseUrl(): Map<String, String> {
                val urls: MutableMap<String, String> =
                    HashMap(1)
                urls[FreeApi.Config.BASE_URL_OTHER_NAME] = FreeApi.Config.BASE_URL_OTHER
                return urls
            }

            // 公共参数设置
            override fun getStaticHeaderParameter(): Map<String, String> {
//                val parameters: MutableMap<String, String> =
//                    HashMap(3)
//                parameters["system"] = "android"
//                parameters["version_code"] = "1"
//                parameters["device_num"] = "666"
//                return parameters
                return super.getStaticHeaderParameter()
            }

            // 设置动态参数
            override fun getDynamicHeaderParameter(): Map<String, ParameterGetter> {
//                val parameters: HashMap<String, ParameterGetter> =
//                    HashMap()
//                val value = object :ParameterGetter{
//                    override fun get(): String {
//                        return "9527"
//                    }
//                }
//                parameters["id"] = value
//                return parameters
                return super.getDynamicHeaderParameter()
            }

            override fun setOkHttpClient(builder: OkHttpClient.Builder) {
                builder.hostnameVerifier { hostname, session ->
                    // 验证主机名是否与服务器的身份验证方案匹配。
                    true
                }
//                super.setOkHttpClient(builder)
            }
        })
```



### 二、使用示例

#### 网络请求返回实体
`value` :  返回字段名称
`alternate` : 备用字段名称
```
open class ResponseBean<T>: BaseResponse<T> {
    @SerializedName(value = "code", alternate = ["status"])
    private var code = 0
    @SerializedName(value = "data", alternate = ["result"])
    private var data: T? = null
    @SerializedName(value = "msg", alternate = ["message"])
    private var message: String? = null
    override fun getCode(): Int {
        return this.code
    }

    override fun setCode(code: Int) {
        this.code = code
    }

    override fun getData(): T? {
        return this.data
    }

    override fun setData(data: T?) {
        this.data = data
    }

    override fun getMsg(): String? {
        return this.message
    }

    override fun setMsg(msg: String?) {
        this.message = msg
    }


}
```


#### 服务端常量配置

```
object FreeApi: Api() {

    interface Code {
        companion object {
            const val SUCCESS = 0
        }
    }

    interface Config {
        companion object {
            const val BASE_URL = "https://www.wanandroid.com/"
            const val BASE_URL_OTHER_NAME = "other"
            const val BASE_URL_OTHER = "https://v1.jinrishici.com/"
        }
    }

    interface Service {
        /**
         * 微信公众号列表
         */
        @Headers(Header.CACHE_ALIVE_SECOND + ":" + 10)
        @GET("wxarticle/chapters/json")
        fun getCelebrities(): Observable<ResponseBean<List<Celebrity>>>

        /**
         * banner内容
         */
        @Headers(Header.CACHE_ALIVE_SECOND + ":" + 0)
        @GET("banner/json")
        fun getBannerList(): Observable<ResponseBean<List<Banner>>>

        /**
         * 注册账号
         */
        @POST("user/register")
        @FormUrlEncoded
        fun register(@Field("username") username: String,
                     @Field("password")password:String,
                     @Field("repassword")repassword:String): Observable<ResponseBean<RegisterBean>>

        /**
         * 重定向
         * 相当于修改了BaseUrl
         */
        @Headers(Header.BASE_URL_REDIRECT + ":" + Config.BASE_URL_OTHER_NAME)
        @GET("all.json")
        fun singlePoetry(): Observable<SinglePoetryBean>

        /**
         * http 请求 // http://www.mxnzp.com/api/holiday/single/20200102
         */
        @GET
        fun getDate(@Url path: String): Observable<ResponseBean<DateData>>
    }

    fun api(): Service {
        return api(Service::class.java)
    }

}
```

#### 常规请求
```
// 请求生命周期接口回调
private val reqListener = object : RequestListener {
        override fun onStart() {
           // 开始网络请求
        }

        override fun onError(handle: ExceptionHandle?) {
            // 网络请求错误
        }

        override fun onFinish() {
            // 网络请求结束
        }
  }
    
  // 生命周期绑定 防止内存泄露
 private val mRxLife: RxLife by lazy {
        RxLife.create()
   }
   
   // 获取公众号列表
mRxLife.add(
                RxHttp.request(FreeApi.api().getCelebrities()).listener(reqListener)
                .request(object : ResultCallback<List<Celebrity>> {
                    // 请求成功
                    override fun onSuccess(code: Int, data: List<Celebrity>?) {
                        
                    }

                    // 请求成功，但是返回失败（根据SuccessCode判断）
                    override fun onFailed(code: Int, msg: String?) {
                       
                    }
                })
            )
    
// 注册
mRxLife.add(
    RxHttp.request(
    FreeApi.api().register(
        et_userName.text.toString(),
        et_password.text.toString(),
        et_password.text.toString()
    )
).listener(reqListener)
    .request(object : ResultCallback<RegisterBean> {
        // 请求成功
        override fun onSuccess(code: Int, data: RegisterBean?) {
           
        }
        // 请求成功，但是返回失败（根据SuccessCode判断）
        override fun onFailed(code: Int, msg: String?) {
           
        }
    })
)
```
#### 非默认`SuccessCode`的请求
```
mRxLife.add(
            RxHttp.request(FreeApi.api()
            .getDate("http://www.mxnzp.com/api/holiday/single/${getCurrent()}"))
            .listener(reqListener)
            .customRequest {
                // 网络请求访问成功
                if(it.getCode() == 1){ // 上面网络请求配置默认SuccessCode为200
                   // 接口访问成功 有预期内容
                }else{
                    // 接口返回失败 无预期内容
                }
            })

```
#### 非默认`ResponseBean`的请求
> 即返回类型并非`ResponseBean<T>`的请求结果
```

/**
 * 重定向
 * 相当于修改了BaseUrl,请求地址为 Config.BASE_URL_OTHER_NAME+all.json
 */
@Headers(Header.BASE_URL_REDIRECT + ":" + Config.BASE_URL_OTHER_NAME)
@GET("all.json")
fun singlePoetry(): Observable<SinglePoetryBean>

mRxLife.add(RxHttp.customRequest(FreeApi.api().singlePoetry())
            .listener(reqListener).customEntityRequest {
                // 请求成功 返回的是SinglePoetryBean
        })
```

## `API`

### `JsonObjUtils`

> 创建JSONObject对象并生成Json字符串。

### `RequestBodyUtils`

> 创建`RequestBody`，针对`POST`请求。

如图片上传接口

```
/**
   * 键		值
   * img		File
   * content	String
   */
  @Multipart
  @POST("public/img")
  fun uploadImg(@PartMap img: Map<String, RequestBody>): Observable<ResponseBean<UploadImgBean>>
```

发起请求如下

```
private fun uploadImg(content: String, imgFile: File) {
        val map = builder()
            .add<Any>("content", content)
            .add<Any>("img", imgFile)
            .build()
        mRxLife.add(
            RxHttp.request(FreeApi.api().uploadImg(map)).listener(reqListener)
                .request(object : ResultCallback<UploadImgBean> {
                    @SuppressLint("SetTextI18n")
                    override fun onSuccess(code: Int, data: UploadImgBean?) {
                        tv_log.text =
                            "${tv_log.text}\nonSuccess {code-${code} data-${Gson().toJson(data)}}"
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onFailed(code: Int, msg: String?) {
                        tv_log.text = "${tv_log.text}\nonFailed {code-${code} msg-${msg}}"
                    }
                })
        )
    }
```

### `HttpsCompat`

主要提供7个静态方法，用于实现证书忽略和开启`Android4.4`及以下对`TLS1.2`的支持。

```
/**
 * 忽略证书的验证，这样请求就和HTTP一样，失去了安全保障，不建议使用
 */
fun ignoreSSLForOkHttp(OkHttpClient.Builder,X509TrustManager) 
    
/**
 * 开启HttpsURLConnection对TLS1.2的支持
 */
fun enableTls12ForOkHttp( OkHttpClient.Builder,X509TrustManager)
    
/**
 * 忽略证书的验证，这样请求就和HTTP一样，失去了安全保障，不建议使用
 * 应在使用HttpsURLConnection之前调用，建议在application中
 */
fun ignoreSSLForHttpsURLConnection() 
    
/**
 * 开启HttpsURLConnection对TLS1.2的支持
 * 应在使用HttpsURLConnection之前调用，建议在application中
 */
fun enableTls12ForHttpsURLConnection()
    
/**
 * 获取开启TLS1.2的SSLSocketFactory
 * 建议在android4.4及以下版本调用
 */
fun getEnableTls12SSLSocketFactory(): SSLSocketFactory?
    
/**
 * 获取忽略证书的HostnameVerifier
 * 与{@link #getIgnoreSSLSocketFactory()}同时配置使用
 */
fun getIgnoreHostnameVerifier(): HostnameVerifier?
    
/**
 * 获取忽略证书的SSLSocketFactory
 * 与{@link #getIgnoreHostnameVerifier()}同时配置使用
 */
fun getIgnoreSSLSocketFactory(): SSLSocketFactory? 
```



## 常见问题

### 在`Android9.0`及以上系统`HTTP`请求无响应

官方资料在框架安全性变更提及，如果应用以 `Android 9 `或更高版本为目标平台则默认情况下启用网络传输层安全协议 (TLS)，即 `isCleartextTrafficPermitted()` 函数返回 `false`。 如果您的应用需要为特定域名启用明文，您必须在应用的网络安全性配置中针对这些域名将 `cleartextTrafficPermitted` 显式设置为` true`。

因此解决办法有2种：

第一种，启用`HTTP`，允许明文传输（不建议采用）
1. 在资源文件夹`res/xml`下面创建`network_config.xml`（资源文件已创建）

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true" />
</network-security-config>
```

2. 在清单文件`AndroidManifest.xml的application`标签里面设置`networkSecurityConfig`属性引用。

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest ... >
    <application
        android:networkSecurityConfig="@xml/network_security_config">
    </application>
</manifest>
```

第二种，所有接口采用`HTTPS`协议（建议采用）

此方法需确保后台正确配置，如配置后仍有无法访问，且提示证书异常，请检查后台配置。

### `HTTPS`请求访问时提示证书异常

该情况一般为后台未正确配置证书。请检查后台配置。

在测试时，我们可以暂时选择忽略证书，这样请求就和`HTTP`一样，但会失去安全保障，不允许在正式发布时使用。

**可直接使用`HttpsCompat`工具类。**

实现代码如下：

```java
public static void ignoreSSLForOkHttp(OkHttpClient.Builder builder) {
    builder.hostnameVerifier(getIgnoreHostnameVerifier())
            .sslSocketFactory(getIgnoreSSLSocketFactory());
}

public static void ignoreSSLForHttpsURLConnection() {
    HttpsURLConnection.setDefaultHostnameVerifier(getIgnoreHostnameVerifier());
    HttpsURLConnection.setDefaultSSLSocketFactory(getIgnoreSSLSocketFactory());
}

/**
 * 获取忽略证书的HostnameVerifier
 * 与{@link #getIgnoreSSLSocketFactory()}同时配置使用
 */
private static HostnameVerifier getIgnoreHostnameVerifier() {
    return new HostnameVerifier() {
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    };
}

/**
 * 获取忽略证书的SSLSocketFactory
 * 与{@link #getIgnoreHostnameVerifier()}同时配置使用
 */
private static SSLSocketFactory getIgnoreSSLSocketFactory() {
    try {
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, getTrustManager(), new SecureRandom());
        return sslContext.getSocketFactory();
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}

private static TrustManager[] getTrustManager() {
    return new TrustManager[]{
        new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }
        }
    };
}
```

### `HTTPS`请求在`Android4.4`及以下无法访问

服务器已正确配置`SSL`证书，且已打开`TLS1.1`和`TLS1.2`，但是在`Android4.4`及以下无法访问网络。是因为在`Android4.4`及以下版本默认不支持`TLS1.2`，需要开启对`TLS1.2`的支持。代码如下：

```java
public static void enableTls12ForHttpsURLConnection() {
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
        SSLSocketFactory ssl = getEnableTls12SSLSocketFactory();
        if (ssl != null) {
            HttpsURLConnection.setDefaultSSLSocketFactory(ssl);
        }
    }
}

public static void enableTls12ForOkHttp(OkHttpClient.Builder builder) {
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
        SSLSocketFactory ssl = HttpsCompat.getEnableTls12SSLSocketFactory();
        if (ssl != null) {
            builder.sslSocketFactory(ssl);
        }
    }
}

public static SSLSocketFactory getEnableTls12SSLSocketFactory() {
    try {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, null, null);
        return new Tls12SocketFactory(sslContext.getSocketFactory());
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}

private static class Tls12SocketFactory extends SSLSocketFactory {
    private static final String[] TLS_SUPPORT_VERSION = {"TLSv1.1", "TLSv1.2"};

    private final SSLSocketFactory delegate;

    private Tls12SocketFactory(SSLSocketFactory base) {
        this.delegate = base;
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return delegate.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return delegate.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        return patch(delegate.createSocket(s, host, port, autoClose));
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return patch(delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        return patch(delegate.createSocket(host, port, localHost, localPort));
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return patch(delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return patch(delegate.createSocket(address, port, localAddress, localPort));
    }

    private Socket patch(Socket s) {
        if (s instanceof SSLSocket) {
            ((SSLSocket) s).setEnabledProtocols(TLS_SUPPORT_VERSION);
        }
        return s;
    }
}
```

### `Glide`在`Android4.4`及以下图片加载失败

原因同上，需要自定义`Glide`的`AppGlideModule`，传入支持`TLS1.2`的`OkHttpClient`。

```java
@GlideModule
public class CustomAppGlideModule extends AppGlideModule {

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(getOkHttpClient()));
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    private static OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpsCompat.enableTls12ForOkHttp(builder);
        return builder.build();
    }
}
```



# 三、文件下载之`RxDownload`

## 使用方法

### 初始化

初始化操作可在Application中也可在应用启动页中进行

```
RxHttp.init(this)
// 可选，未配置设置将自动采用DefaultDownloadSetting
// 可不设置，使用DefaultDownloadSetting
RxHttp.initDownload(object : DefaultDownloadSetting() {
    override fun getTimeout(): Long {
        return 60000
    }
})
```

### 调用

```
RxDownload.create(DownloadInfo(et_url.text.toString()))
    .setDownloadListener(object : DownloadListener {
            override fun onStarting(info: DownloadInfo?) {
                tv_start_stop.text = "暂停下载"
                tv_cancel.text = "取消下载"
            }

            override fun onDownloading(info: DownloadInfo?) {
                tv_start_stop.text = "暂停下载"
            }

            override fun onStopped(info: DownloadInfo?) {
                saveDownloadInfo()
                tv_start_stop.text = "开始下载"
                tv_speed.text = ""
            }

            override fun onCanceled(info: DownloadInfo?) {
                saveDownloadInfo()
                tv_start_stop.text = "开始下载"
                tv_cancel.text = "已取消"
                progress_bar.progress = 0
                tv_speed.text = ""
                tv_download_length.text = ""
                tv_content_length.text = ""
            }

            override fun onCompletion(info: DownloadInfo?) {
                saveDownloadInfo()
                tv_start_stop.text = "下载成功"
                tv_speed.text = ""
            }

            override fun onError(info: DownloadInfo?, e: Throwable?) {
                saveDownloadInfo()
                tv_start_stop.text = "开始下载"
                tv_speed.text = ""
                Log.e(tag,"onError",e)
            }
        })?.setProgressListener(object :ProgressListener{
            override fun onProgress(progress: Float, downloadLength: Long, contentLength: Long) {
                progress_bar.progress = (progress * 10000).toInt()
                tv_download_length.text =
                    UnitFormatUtils.formatBytesLength(downloadLength.toFloat())
                tv_content_length.text = UnitFormatUtils.formatBytesLength(contentLength.toFloat())
            }
        })?.setSpeedListener(object :SpeedListener{
            override fun onSpeedChange(bytesPerSecond: Float, speedFormat: String?) {
                tv_speed.text = speedFormat
            }
        })
```



