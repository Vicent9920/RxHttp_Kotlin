package com.vincent.sample.rxhttp_kotlin.net

import com.vincent.sample.rxhttp_kotlin.entity.RecommendPoetryBean
import com.vincent.sample.rxhttp_kotlin.entity.SinglePoetryBean
import io.reactivex.Observable
import per.goweii.rxhttp.kt.request.Api
import per.goweii.rxhttp.kt.request.base.BaseBean
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

/**
 * <p>文件描述：<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2020/1/1 0001 <p>
 * <p>@update 2020/1/1 0001<p>
 * <p>版本号：1<p>
 *
 */
class FreeApi: Api() {

    interface Code {
        companion object {
            const val SUCCESS = 200
        }
    }

    interface Config {
        companion object {
            const val BASE_URL = "http://api.apiopen.top/"
            const val BASE_URL_OTHER_NAME = "other"
            const val BASE_URL_OTHER = "https://wis.qq.com/weather/common?source=xw&weather_type=forecast_1h|forecast_24h|index|alarm|limit|tips"
            const val BASE_URL_ERROR_NAME = "error"
            const val BASE_URL_ERROR = "https://www.apiopen1.top/"
            const val BASE_URL_HTTPS_NAME = "https"
            const val BASE_URL_HTTPS = "https://www.baidu.com/"
        }
    }

    interface Service {
        /**
         * 随机单句诗词推荐
         */
        @Headers(Header.CACHE_ALIVE_SECOND + ":" + 10)
        @GET("singlePoetry")
        fun singlePoetry(): Observable<ResponseBean<SinglePoetryBean>>

        /**
         * 随机一首诗词推荐
         */
        @Headers(Header.CACHE_ALIVE_SECOND + ":" + 0)
        @GET("recommendPoetry")
        fun recommendPoetry(): Observable<ResponseBean<RecommendPoetryBean>>

        /**
         * 获取天气
         */
        @Headers(Header.BASE_URL_REDIRECT + ":" + Config.BASE_URL_OTHER_NAME)
        @GET("weatherApi?")
        fun weather(@Query("city") city: String?): Observable<ResponseBean<BaseBean>>

        /**
         * 错误地址
         */
        @Headers(Header.BASE_URL_REDIRECT + ":" + Config.BASE_URL_ERROR_NAME)
        @GET("weatherApi")
        fun errorHost(): Observable<ResponseBean<BaseBean>>

        /**
         * https
         */
        @Headers(Header.BASE_URL_REDIRECT + ":" + Config.BASE_URL_HTTPS_NAME)
        @GET("s")
        fun https(@Query("wd") wd: String?): Observable<ResponseBean<BaseBean>>
    }

}