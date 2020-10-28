package com.vincent.sample.rxhttp_kotlin.net

import io.reactivex.Observable
import per.goweii.rxhttp.kt.request.Api
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

object MessageApi : Api() {

    fun api(isGson: Boolean = true): Service {
        return api(Service::class.java, isGson)
    }
}

interface Service {
    /**
     * http 请求 // http://www.mxnzp.com/api/holiday/single/20200102
     */
    @POST("app/array")
    @FormUrlEncoded
    fun test(
        @Field("uid") uid: String,
        @Field("pwd") pwd: String
    ): Observable<String>  //Call<ResponseBody>
}