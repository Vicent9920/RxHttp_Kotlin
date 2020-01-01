package per.goweii.rxhttp.kt.request.gson

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

/**
 * <p>文件描述：<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2019/12/30 0030 <p>
 * <p>@update 2019/12/30 0030<p>
 * <p>版本号：1<p>
 *
 */
class IntDeserializer: JsonDeserializer<Int> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Int {
        if (json == null || !json.isJsonObject) {
            return 0
        }
        try {
            return json.asInt
        } catch (e: Exception) {
        }
        try {
            val d = json.asDouble
            return d.toInt()
        } catch (e: Exception) {
        }
        try {
            val s = json.asString
            return Integer.valueOf(s)
        } catch (e: Exception) {
        }
        return 0
    }
}