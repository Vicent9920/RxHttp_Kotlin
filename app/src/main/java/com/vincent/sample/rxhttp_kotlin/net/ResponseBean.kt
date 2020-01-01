package com.vincent.sample.rxhttp_kotlin.net

import com.google.gson.annotations.SerializedName
import per.goweii.rxhttp.kt.request.base.BaseResponse

/**
 * <p>文件描述：<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2020/1/1 0001 <p>
 * <p>@update 2020/1/1 0001<p>
 * <p>版本号：1<p>
 *
 */
class ResponseBean<T>: BaseResponse<T> {
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