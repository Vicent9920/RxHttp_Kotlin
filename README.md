# RxHttp_Kotlin

之前在`GitHub`上面看见一个不错的网络请求库，功能比较完善，代码也比较简洁，于是我自动把该项目转成了`Kotlin`，并且在使用过程中进行了一些功能完善，后来自己在使用过程中依然遇到很多问题，后来再次查看代码，于是有了本文“说明书”。

本文目录：
 * 项目介绍
 * 添加依赖
 * 使用步骤
 * 异常反馈
 * 鸣谢

## 项目介绍
该项目使用`RxJava`+`Retrofit`+`OkHttp3`进行封装，实现接口请求和文件下载功能。在使用开发过程中，也有研究`Kotlin.coroutines`知识和`OkGo`、`okhttp-RxHttp`等框架源码，后来觉得还是目前这个框架使用起来更顺手。当然，这个库也有一些不完善的地方，希望大家提出，我们一起学习成长！各位也可以跳过本文，直接看源码 [传送门](https://github.com/Vicent9920/RxHttp_Kotlin)
### 功能简介
- 网络请求（`RxRequest`）
  - 支持监听请求声明周期，如开始结束和网络错误
  - 支持多`BaseUrl`，可针对不同请求重定向
  - 支持针对不同请求设置不同缓存策略，如无网强制获取缓存，有网缓存有效`10`秒
  - 支持添加公共请求参数
  - 支持自定义异常处理和异常提示消息
  - 支持网络请求的任意实体（**原框架是不允许**）
  - 支持解析过程自定义（如`responseBody` \ `SuccessCode` 等自定义处理）
  - 支持Cookie管理
  - 增加网络取消

- 文件下载（`RxDownload`）
  - 支持断点续传
  - 支持下载进度回调
  - 支持下载速度回调
  - 支持下载过程状态监听
  - 支持在仅保存下载路径未保存进度时自动恢复断点续传
  - 支持自动获取真实文件名

  ### 已集成框架

```
// Retrofit2
api 'com.squareup.retrofit2:retrofit:2.7.1'
api 'com.squareup.retrofit2:adapter-rxjava2:2.7.1'
api 'com.squareup.retrofit2:converter-gson:2.7.1'

// OkHttp
api 'com.squareup.okhttp3:logging-interceptor:4.3.0'
api 'com.squareup.okhttp3:okhttp:4.7.2'
api 'com.squareup.okio:okio:2.6.0'

// RxJava2
api 'io.reactivex.rxjava2:rxjava:2.2.13'
api 'io.reactivex.rxjava2:rxandroid:2.1.1'
```

### `Gradle`依赖
添加`jitpack`仓库依赖

```
maven { url 'https://jitpack.io' }
```
添加依赖

```
implementation 'com.github.Vicent9920:RxHttp_Kotlin:1.0.9'
```
## 使用说明
### 网络请求
#### 1、初始化

```
RxHttp.mAppContext = applicationContext
// 下个版本修改为RxHttp.init(application)
```
#### 2、设置配置信息

```
RxHttp.initRequest(object : DefaultRequestSetting() {
        // baseUrl 设置
        override fun getBaseUrl(): String {
            return "https://wanandroid.com/"
        }


        // Code 判断
        override fun getSuccessCode(): Int {
            return 200
        }
}
```
#### 3、请求接口

```
// 接口设置
object FreeApi: Api() {

    interface Service {
        /**
         * 微信公众号列表
         */
        @GET("wxarticle/chapters/json")
        fun getCelebrities(): Observable<ResponseBean<List<Celebrity>>>
    }

    fun api(): Service {
        return api(Service::class.java)
    }

}
...
RxHttp.request(FreeApi.api().getCelebrities())
    .request(object : ResultCallback<List<Celebrity>> {
        override fun onSuccess(code: Int, data: List<Celebrity>?) {
            val msg = "${tv_log.text}\nonSuccess {code-${code} data-${Gson().toJson(data)}}"
			Log.e("request",msg)
        }

        @SuppressLint("SetTextI18n")
        override fun onFailed(code: Int, msg: String?) {
            val msg = "${tv_log.text}\nonFailed {code-${code} msg-${msg}}"
			Log.e("request",msg)
        }
    })
```
### 个性化设置

#### 请求接口返回对象自定义处理
其实这个标题描述不是很准确，标准的返回数据结构要求继承自一个抽象类，如下：

```
/**
 * <p>文件描述：网络接口返回json格式对应的实体类<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2019/12/30 0030 <p>
 * <p>@update 2019/12/30 0030<p>
 * <p>版本号：1<p>
 *
 */
abstract class BaseResponse<T>{

    abstract fun getCode(): Int

    abstract fun setCode(code: Int)

    abstract fun getData(): T?

    abstract fun setData(data: T?)

    abstract fun getMsg(): String?

    abstract fun setMsg(msg: String?)

}
```
自定义返回的数据结构，可以同时对一个属性解析的时候使用不同的命名，我们继续看示例，重点关注`SerializedName`注解

```
/**
 * <p>文件描述：网络请求返回实体<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2020/1/1 0001 <p>
 * <p>@update 2020/1/1 0001<p>
 * <p>版本号：1<p>
 *
 */
open class ResponseBean<T>: BaseResponse<T>() {
    @SerializedName(value = "code", alternate = ["status"])
    private var code = 0
    @SerializedName(value = "data", alternate = ["result"])
    private var data: T? = null
    @SerializedName(value = "errorMsg", alternate = ["message"])
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
我们一般使用`data`、`code`、`msg`来做基本的数据结构，但是这几个字段可以通过`SerializedName`注解来实现属性重命名，如示例。接下来再说一下我们平时请求的问题：
* 标准数据结构
这个很好理解，就是返回的数据类型是上面`ResponseBean<Any>`类型，返回成功的时候也是根据我们配置的来。示例如下：

```
interface Service {
    ...
    /**
     * 微信公众号列表
     */
    @GET("wxarticle/chapters/json")
    fun getCelebrities(): Observable<ResponseBean<List<Celebrity>>>
}
使用：
RxHttp.request(FreeApi.api().getCelebrities())
    .request(object : ResultCallback<List<Celebrity>> {
        @SuppressLint("SetTextI18n")
        override fun onSuccess(code: Int, data: List<Celebrity>?) {
            // TODO 刷新页面
        }

        @SuppressLint("SetTextI18n")
        override fun onFailed(code: Int, msg: String?) {
            // TODO 提示错误
        }
    })
```
* 标准数据结构，但成功的时候返回的不是统一的`successCode`

这个时候我们期望可以直接在这里处理，而不是配置通过`getMultiSuccessCode`（后面有介绍）方法，示例如下：

```
RxHttp.request(FreeApi.api().uploadImg(map)).customRequest { result:ResponseBean<List<Celebrity> ->
    // 连ResponseBean一起返回
}
```
* 其它类型的数据结构
如果我们项目里面有一些不同平台提供的接口，这个时候不仅`successCode`不一样了，甚至连数据结构都不一样了，这个时候又应该如何处理呢？和上面类似，示例如下：

```
 interface Service {
    ...
    /**
	 * 获取诗词
	 */
	@Headers(Header.BASE_URL_REDIRECT + ":" + Config.BASE_URL_OTHER_NAME)
	@GET("all.json")
	fun singlePoetry(): Observable<SinglePoetryBean>
}
...
RxHttp.customRequest(FreeApi.api().singlePoetry()).customEntityRequest {result:SinglePoetryBean ->
	// 自行判断数据
}
```


#### 打开网络日志
使用的是`okhttp3.logging.HttpLoggingInterceptor`,默认日志级别为`HttpLoggingInterceptor.Level.BODY`，使用方式如下：

```
RxHttp.initRequest(object : DefaultRequestSetting() {
    ...
    // 下个版本支持修改日志级别
    override fun isDebug(): Pair<Boolean, HttpLoggingInterceptor.Level> {
        return Pair(true,HttpLoggingInterceptor.Level.NONE)
    }
}
```
#### 自定义`Gson`
示例如下：

```
/**
 * JSON中时间格式转换，消除后台返回时间格式不定引发的转换问题
 */
@SuppressLint("SimpleDateFormat")
class DateDeserializer : JsonDeserializer<Date> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Date? {
        return try {
            // 年月日 时分秒格式
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(json.asJsonPrimitive.asString, ParsePosition(0))
        } catch (e: Exception) {
            // 毫秒数格式
            Date(json.asJsonPrimitive.asLong)
        }
    }
}
...
RxHttp.initRequest(object : DefaultRequestSetting() {
    ...
    override fun getGson(): Gson? {
		val builder = GsonBuilder()
		builder.registerTypeAdapter(Date::class.java, DateDeserializer()).setDateFormat("yyyy-MM-dd HH:mm:ss").create()
		builder.registerTypeAdapter(Date::class.java, DateSerializer()).setDateFormat("yyyy-MM-dd HH:mm:ss").create()
		return builder.create()
	}
}
```
#### 设置`OkHttpClient`

```
RxHttp.initRequest(object : DefaultRequestSetting() {
    ...
    override fun setOkHttpClient(builder: OkHttpClient.Builder) {

	}
}
```
#### 自定义`Interceptor`
拦截器分为两个模块，一个是缓存相关，一个是网络相关。当然，你直接一骨碌写在一起也没有问题！网络拦截器（`NetworkInterceptor`）应用于日志打印、刷新`Token`等；缓存策略主要是自定义缓存策略，比如无网获取历史记录之类。每个模块的拦截也支持多个，具体见示例。

**网络拦截器示例**
```
RxHttp.initRequest(object : DefaultRequestSetting() {
    ...
    override fun getNetworkInterceptors(): Array<Interceptor> {
    	return arrayOf()
    }
}
```
**缓存策略拦截器示例**
```
RxHttp.initRequest(object : DefaultRequestSetting() {
    ...
    override fun getInterceptors(): Array<Interceptor> {
    	return arrayOf()
    }
}
```

#### 自定义请求异常工具类
默认异常工具类如下,支持重写自定义
```
const val UNKNOWN = -1
const val NET = 0
const val TIMEOUT = 1
const val JSON = 2
const val HTTP = 3
const val HOST = 4
const val SSL = 5

open class ExceptionHandle constructor(val e: Throwable) {

    val code: Int by lazy {
        onGetCode(e)
    }
    val msg: String by lazy {
        onGetMsg(code)
    }

    /**
     * 重写该方法去返回异常对应的错误码
     *
     * @param e Throwable
     * @return 错误码
     */
    private fun onGetCode(e: Throwable?): Int {
        return if (!NetUtils.isConnected()) {
            NET
        } else {
            if (e is SocketTimeoutException) {
                TIMEOUT
            } else if (e is HttpException) {
                HTTP
            } else if (e is UnknownHostException || e is ConnectException) {
                HOST
            } else if (e is JsonParseException || e is ParseException || e is JSONException) {
                JSON
            } else if (e is SSLException) {
                SSL
            } else {
                UNKNOWN
            }
        }
    }

    /**
     * 重写该方法去返回错误码对应的错误信息
     *
     * @param code 错误码
     * @return 错误信息
     */
    private fun onGetMsg(code: Int): String {
        return when (code) {
            NET -> "网络连接失败，请检查网络设置"
            TIMEOUT -> "网络状况不稳定，请稍后重试"
            JSON -> "JSON解析异常"
            HTTP -> "请求错误，请稍后重试"
            HOST -> "服务器连接失败，请检查网络设置"
            SSL -> "证书验证失败"
            else -> "未知错误，请稍后重试"
        }
    }

}
...
RxHttp.initRequest(object : DefaultRequestSetting() {
    ...
    override fun <E : ExceptionHandle> getExceptionHandle(): E? {
        return super.getExceptionHandle()
    }
}
```
#### 设置公共`Header`
公共即每个请求均需携带该`Header`，这种情况分为静态值和动态值，**静态，即`Header value`无变化；动态，即`Header value`有变化**。公共`Header`默认为空的`HashMap`

**静态公共`Header`示例**
```
RxHttp.initRequest(object : DefaultRequestSetting() {
    ...
    override fun getStaticHeaderParameter(): Map<String, String> {
        val parameters: MutableMap<String, String> =
            HashMap(3)
        parameters["system"] = "android"
        parameters["version_code"] = "1"
        parameters["device_num"] = "666"
        return parameters
    }
}
```
**动态公共`Header`示例**
```
RxHttp.initRequest(object : DefaultRequestSetting() {
    ...
    override fun getDynamicHeaderParameter(): Map<String, ParameterGetter> {
        val parameters: HashMap<String, ParameterGetter> =
            HashMap(1)
        val value = object :ParameterGetter{
            override fun get(): String {
                if(登录){
                    return "登录ID 9527"
                }else{
                    return ""
                }

            }
        }
        parameters["id"] = value
        return parameters
    }
}
```
#### 设置公共`Param`
不知道大家有没有遇到这种情况，即每个接口在参数上要求携带手机的厂商、系统、版本号？我以前遇到过，这个接口就是针对这种情况进行的封装，与上面相似，也分为静态参数和动态参数

**静态公共参数示例**
```
RxHttp.initRequest(object : DefaultRequestSetting() {
    ...
    override fun getStaticPublicQueryParameter(): Map<String, String> {
        val parameters: MutableMap<String, String> =
            HashMap(3)
        parameters["system"] = "android"
        parameters["version_code"] = "1"
        parameters["device_num"] = "666"
        return parameters
    }
}
```
**动态公共参数示例**
```
RxHttp.initRequest(object : DefaultRequestSetting() {
    ...
    override fun getDynamicHeaderParameter(): Map<String, ParameterGetter> {
        val parameters: HashMap<String, ParameterGetter> =
            HashMap(1)
        val value = object :ParameterGetter{
            override fun get(): String {
                if(登录){
                    return "登录ID 9527"
                }else{
                    return ""
                }

            }
        }
        parameters["id"] = value
        return parameters
    }
}
```

#### 自定义缓存相关
* 自定义缓存大小

如果支持缓存的话，文件大小默认为`10*1024*1024`,当然你可以修改为自己想要的大小，如下
```
RxHttp.initRequest(object : DefaultRequestSetting() {
    ...
    override fun getCacheSize(): Long {
        return 1024*1024*1024
    }
}
```
* 自定义缓存文件名称

除此以外，你还可以修改应用缓存区的缓存文件名称，其默认为“`rxhttp_cache`”,修改如下
```
RxHttp.initRequest(object : DefaultRequestSetting() {
    ...
     override fun getCacheDirName(): String {
        return "Vincent"
    }
}
```
* 自定义缓存时间
对于某个接口，你期望直接获取`3`秒以内的数据，否则还是请求网络，这个也是可以的，只需要在接口的地方增加一个`Header`就行(因为此处是通过`Header`设置`Cache-Control:max-age`来实现)

```
/**
 * 微信公众号列表
 */
@Headers(Header.CACHE_ALIVE_SECOND + ":" + 3)
@GET("wxarticle/chapters/json")
fun getCelebrities(): Observable<ResponseBean<List<Celebrity>>>
```

#### 自定义请求相关时间
请求的时间有三个指标，如下
* 连接时间
* 读时间
* 写时间
上面这些时间`okhttp3`默认是均`10`秒，框架默认也是`10`秒，当然你也可以修改，修改的时间为毫秒值。

**一个方法修改三个时间**
```
RxHttp.initRequest(object : DefaultRequestSetting() {
    ...
    override fun getTimeout(): Long {
        return 60*1000
    }
}
```
**单独修改连接超时时间**
```
RxHttp.initRequest(object : DefaultRequestSetting() {
    ...
    override fun getConnectTimeout(): Long {
        return 50*1000
    }
}
```
**单独修改读取超时时间**
```
RxHttp.initRequest(object : DefaultRequestSetting() {
    ...
    override fun getReadTimeout(): Long {
        return 40*1000
    }
}
```
**单独修改写入超时时间**
```
RxHttp.initRequest(object : DefaultRequestSetting() {
    ...
    override fun getWriteTimeout(): Long {
        return 30*1000
    }
}
```
#### 返回码设置
正常情况下，接口返回的时候`code`一般用`200`代表成功，我们初始化的时候通过`getSuccessCode`方法就设置好了。但是如果我们有多个代表成功的code，这个时候我们也可以通过一个数组来表示（好像一般情况下用不到）。还有一种情况，如果请求失败了，我们需要针对某个特定的code来获取ErrorBody的内容，这个时候还可以使用`getMultiHttpCode`方法来处理。
说起来比较麻烦，我们来看示例：

**默认成功返回码**

```
RxHttp.initRequest(object : DefaultRequestSetting() {
    ...
    override fun getSuccessCode(): Int {
        return 200
    }
}
```

**兼容其他成功返回码**

```
RxHttp.initRequest(object : DefaultRequestSetting() {
    ...
    override fun getMultiSuccessCode(): IntArray {
       // return intArrayOf()默认值空数组
       return intArrayOf(0) // 当请求的Bean返回码等于200或者0，都将回调访问成功的方法
    }
}
```
**`Http` 状态码处理**

此处为请求失败，根据状态码处理不同业务，如果返回true，则代表消费当次异常，不会走异常回调（但是会尝试回调请求结束的生命周期方法，如果你有监听的话），否则会回调请求失败的方法。
```
override fun getMultiHttpCode(): (code: Int) -> Boolean {
    return {
            when(it){
                407 -> {
                    //TODO 刷新Token
                    true
                }
                409 -> {
                    //TODO 强制下线
                    true
                }
                else -> false
            }
        }
}
```
#### **`baseUrl`设置**
**`BaseUrl`默认是全局只有一个**，但是如果你使用组件化开发的话，每个模块的`BaseUrl`可能都不一样，这个时候就需要**支持多个`BaseUrl`**。当然，你说你只有一个组件，但是你也需要设置多个BaseUrl，这个时候我们**提供了重定向方法**，就是把需要重定向的地址配置好，请求的时候携带特定的`Header`就会去切换相应的`BaseUrl`。另外，开发过程中有测试环境和开发环境的切换，这个框架也支持动态的切换默认的`BaseUrl`，具体设置见下面示例。
* 默认全局`BaseUrl`

```
RxHttp.initRequest(object : DefaultRequestSetting() {
    ...
    override fun getBaseUrl(): String {
        return "https://wanandroid.com/"
    }
}
```
* 多个`BaseUrl`（使用`class`为`key`）

```
 override fun getServiceBaseUrl(): Map<Class<*>, String> {
    val map = HashMap<Class<*>, String>(2).apply {
        this[Class.forName("com.vincent.sample.rxhttp_kotlin.net.MyService")] = "https://www.baidu.com"
        this[Class.forName("com.vincent.sample.rxhttp_kotlin.net.YourService")] = "http://www.baidu.com"
    }
    return map
}
```
* 请求地址重定向

```
 override fun getServiceBaseUrl(): Map<Class<*>, String> {
    ...
    // 重定向地址设置
    override fun getRedirectBaseUrl(): Map<String, String> {
        val urls: MutableMap<String, String> = HashMap(1)
        urls[FreeApi.Config.BASE_URL_OTHER_NAME] = FreeApi.Config.BASE_URL_OTHER
        return urls
    }
}
...
interface Service {
...
    /**
     * 重定向
     * 相当于修改了BaseUrl
     */
    @Headers(Header.BASE_URL_REDIRECT + ":" + Config.BASE_URL_OTHER_NAME)
    @GET("all.json")
    fun singlePoetry(): Observable<SinglePoetryBean>
}

```
上面这个示例的请求地址将从`https://www.wanandroid.com/all.json`转移到`https://v1.jinrishici.com/`了。

> - ![](https://user-gold-cdn.xitu.io/2020/6/7/1728de1248ad7faa?w=534&h=505&f=png&s=31750)
* 开发时动态切换`BaseUrl`

```
RequestClientManager.refreshBaseUrl("http://www.google.com/")
```

#### 请求生命周期监听
监听网络请求开始/错误/结束，错误或者结束只会调用其中一个，示例如下：

```
private val reqListener = object : RequestListener {
        private var timeStart: Long = 0
        @SuppressLint("SetTextI18n")
        override fun onStart() {
            // TODO 开启弹窗
        }

        override fun onError(handle: ExceptionHandle?) {
            // TODO 遇到错误，关闭弹窗并提示错误信息
        }

        override fun onFinish() {
            // TODO 请求结束 关闭弹窗
        }
    }
```
既然是生命周期，那一定也得有页面生命周期的监听，示例如下：

```
open class BaseViewModel:ViewModel(),RxLife {
     val mCompositeDisposable = CompositeDisposable()
	 override fun destroy() {
        if (mCompositeDisposable.isDisposed) return
        mCompositeDisposable.dispose()
    }

    override fun add(d: Disposable) {
        mCompositeDisposable.add(d)
    }
    override fun onCleared() {
        super.onCleared()
        mCompositeDisposable.dispose()
    }
}
...
class LoginViewModel : BaseViewModel() {

    fun login(userName: String, password: String) {
        RxHttp.autoLife(this).customRequest(
                FreeApi.api().login(userName,password)).customEntityRequest {
                // TODO 处理业务逻辑

            }

    }

}
//或者这样
class LoginViewModel : ViewModel(),RxLife {

	val mCompositeDisposable = CompositeDisposable()
    fun login(userName: String, password: String) {
        val disposable = RxHttp.customRequest(
                FreeApi.api().login(userName,password)).customEntityRequest {
                // TODO 处理业务逻辑

            }
		addDispose(disposable)
    }


    override fun add(d: Disposable) {
        mCompositeDisposable.add(d)
    }
    override fun onCleared() {
        super.onCleared()
        if (mCompositeDisposable.isDisposed) return
        mCompositeDisposable.dispose()
    }

}

```
#### 其它工具类

*  `JsonObjUtils`
帮助生成`JSON`对象，具体可以查看源码 [`JsonObjUtils`](https://github.com/Vicent9920/RxHttp_Kotlin/blob/aa50a6155dfddf200ea4321fcffbb9183eb90e61/rxhttp_kt/src/main/java/per/goweii/rxhttp/kt/request/utils/JsonObjUtils.kt)
*  `RequestBodyUtils`
帮助生成`RequestBody`,使用很简单，可以查看源码[`RequestBodyUtils`](https://github.com/Vicent9920/RxHttp_Kotlin/blob/03cd5d5d188d064aa451eb975afc4026c153bf02/rxhttp_kt/src/main/java/per/goweii/rxhttp/kt/request/utils/RequestBodyUtils.kt)
*  `HttpsCompat`
用于`Https`实现证书忽略和开启`Android4.4`及以下对`TLS1.2`的支持。具体可以查看源码[`HttpsCompat`](https://github.com/Vicent9920/RxHttp_Kotlin/blob/8f2cf4abbda6c228514ede2e88aef1cc11c09c65/rxhttp_kt/src/main/java/per/goweii/rxhttp/kt/request/utils/HttpsCompat.kt)

### 文件下载
#### 初始化

```
RxHttp.init(application)
```
#### 配置信息

```
RxHttp.initDownload(DefaultDownloadSetting())
```
上面这个`DefaultDownloadSetting`支持重写，可以设置`BaseUrl`（默认设置`http://api.rxhttp.download/`为`Retrofit`的`BaseUrl`）、连接超时、读取超时、写入超时（同网络请求一样）、存储文件地址（默认存在应用文件夹`android/data/**/files/Download/`）、下载模式（追加、替换、重命名，默认为追加）

```
RxHttp.initDownload(object :DefaultDownloadSetting(){
    override fun getBaseUrl(): String {
        return "https://wanandroid.com"
    }

    override fun getTimeout(): Long {
        return 60*1000
    }

    override fun getSaveDirPath(): String? {
        // 自定义保存文件路径需要自行适配Android P ,以及进行动态权限申请
        return super.getSaveDirPath()
    }
})
```
最后说下载模式之前，得先进行说明：

```
/**
 * 如果需要断点续传下载，请设置 APPEND 默认值为 APPEND
 */
enum class Mode {
    /**
     * 追加
     * 用于断点续传下载，下载完成会直接回调完成下载
     */
    APPEND,
    /**
     * 替换
     * 删除重新下载
     */
    REPLACE,
    /**
     * 重命名
     * 重新下载文件，并对文件名称进行重命名
     */
    RENAME
}
```
最后设置也是在上面这个对象里面：

```
RxHttp.initDownload(object :DefaultDownloadSetting(){
    override fun getDefaultDownloadMode(): Mode {
        return Mode.APPEND
    }
})
```
#### 开始下载文件


* 创建下载任务

```
RxDownload.create(DownloadInfo(et_url.text.toString()))
...
data class DownloadInfo @JvmOverloads constructor(var url:String,
  var saveDirPath:String? = null,
  var saveFileName:String? = null,
  var downloadLength:Long = 0L,
  var contentLength: Long = 0)
```
上面这个`DownloadInfo`对象的`url`是`Retrofit`的`url`注解使用;`saveDirPath`为文件下载保存地址；`saveFileName1`保存文件名称；`downloadLength`已下载长度；`contentLength`是文件长度
* 下载任务状态修改
  - `RxDownload.start()` 开始下载
  - `RxDownload.stop()` 暂停下载
  - `RxDownload.cancel()` 取消下载
* `DownloadListener`下载状态监听
* `ProgressListener`下载进度更新
* `SpeedListener`下载速度更新
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


## 异常反馈
本人邮箱——weixing9920@163.com

本人微信——904993060

## 传送门 [源码](https://github.com/Vicent9920/RxHttp_Kotlin)





