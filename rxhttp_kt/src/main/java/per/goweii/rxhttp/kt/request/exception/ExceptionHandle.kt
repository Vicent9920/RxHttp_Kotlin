package per.goweii.rxhttp.kt.request.exception

import com.google.gson.JsonParseException
import org.json.JSONException
import per.goweii.rxhttp.kt.request.utils.NetUtils
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException
import javax.net.ssl.SSLException

/**
 * <p>文件描述：集中处理请求中异常<p>
 * <p>可通过继承自定义，在{@link RequestSetting#getExceptionHandle()}中返回<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2019/12/30 0030 <p>
 * <p>@update 2019/12/30 0030<p>
 * <p>版本号：1<p>
 *
 */
const val UNKNOWN = -1
const val NET = 0
const val TIMEOUT = 1
const val JSON = 2
const val HTTP = 3
const val HOST = 4
const val SSL = 5

class ExceptionHandle constructor(val e: Throwable) {

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