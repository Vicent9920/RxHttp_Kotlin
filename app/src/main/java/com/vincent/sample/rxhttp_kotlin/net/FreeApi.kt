package com.vincent.sample.rxhttp_kotlin.net

import com.vincent.sample.rxhttp_kotlin.entity.*
import io.reactivex.Observable
import okhttp3.RequestBody
import per.goweii.rxhttp.kt.request.Api
import retrofit2.http.*


/**
 * <p>文件描述：接口常量配置<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2020/1/1 0001 <p>
 * <p>@update 2020/1/1 0001<p>
 * <p>版本号：1<p>
 *
 */
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

        /**
         * 键		值
         * img		File
         * content	String
         */
        @Multipart
        @POST("public/img")
        fun uploadImg(@PartMap img: Map<String, RequestBody>): Observable<ResponseBean<UploadImgBean>>
    }

    fun api(): Service {
        return api(Service::class.java)
    }

}