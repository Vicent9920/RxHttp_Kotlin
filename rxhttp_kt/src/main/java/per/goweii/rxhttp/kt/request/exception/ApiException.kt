package per.goweii.rxhttp.kt.request.exception

/**
 * <p>文件描述：服务器请求成功，返回失败码时抛出，方便统一处理<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2019/12/30 0030 <p>
 * <p>@update 2019/12/30 0030<p>
 * <p>版本号：1<p>
 *
 */
class ApiException( val code:Int, val msg:String): Exception("$msg(code=$code)") {
}