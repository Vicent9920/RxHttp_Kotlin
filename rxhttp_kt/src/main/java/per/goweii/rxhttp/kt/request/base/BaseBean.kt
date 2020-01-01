package per.goweii.rxhttp.kt.request.base

import com.google.gson.Gson
import per.goweii.rxhttp.kt.request.utils.JsonFormatUtils
import java.io.Serializable

/**
 * <p>文件描述：网络请求的实体类基类<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2019/12/30 0030 <p>
 * <p>@update 2019/12/30 0030<p>
 * <p>版本号：1<p>
 *
 */
open class BaseBean : Serializable{
    fun toJson(): String {
        return Gson().toJson(this)
    }

    fun toFormatJson(): String {
        return JsonFormatUtils.format(toJson())
    }
}