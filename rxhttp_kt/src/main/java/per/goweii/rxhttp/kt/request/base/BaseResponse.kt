package per.goweii.rxhttp.kt.request.base

/**
 * <p>文件描述：网络接口返回json格式对应的实体类<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2019/12/30 0030 <p>
 * <p>@update 2019/12/30 0030<p>
 * <p>版本号：1<p>
 *
 */
interface BaseResponse<T>{
    fun getCode(): Int

    fun setCode(code: Int)

    fun getData(): T?

    fun setData(data: T?)

    fun getMsg(): String?

    fun setMsg(msg: String?)
}