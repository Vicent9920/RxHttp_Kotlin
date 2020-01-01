package per.goweii.rxhttp.kt.core.exception

/**
 * <p>文件描述：在调用网络请求之前应该先进行初始化，建议在Application中初始化<p>
 * {@link RxHttp#init(android.content.Context)}
 * <p>@author 烤鱼<p>
 * <p>@date 2019/12/30 0030 <p>
 * <p>@update 2019/12/30 0030<p>
 * <p>版本号：1<p>
 *
 */
class RxHttpUninitializedException(message: String = "RxHttp未初始化") : RuntimeException(message) {
}