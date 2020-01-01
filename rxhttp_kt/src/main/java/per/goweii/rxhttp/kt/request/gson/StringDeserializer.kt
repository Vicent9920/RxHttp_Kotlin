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
class StringDeserializer: JsonDeserializer<String> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): String {
        if (json == null || !json.isJsonObject) {
            return ""
        }
        try {
            return json.asString
        } catch (e: Exception) {
        }
        try {
            val l = json.asLong
            return l.toString()
        } catch (e: Exception) {
        }
        try {
            val d = json.asDouble
            return d.toString()
        } catch (e: Exception) {
        }
        return ""
    }
}