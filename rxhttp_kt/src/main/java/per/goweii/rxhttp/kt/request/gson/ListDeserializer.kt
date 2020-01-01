package per.goweii.rxhttp.kt.request.gson

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*

/**
 * <p>文件描述：<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2019/12/30 0030 <p>
 * <p>@update 2019/12/30 0030<p>
 * <p>版本号：1<p>
 *
 */
class ListDeserializer: JsonDeserializer<List<*>> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): List<*>? {
        if (json == null || !json.isJsonArray || context == null) {
            return null
        }
        val list: MutableList<Any> = ArrayList()
        val array = json.asJsonArray
        val itemType = (typeOfT as ParameterizedType).actualTypeArguments[0]
        for (i in 0 until array.size()) {
            val element = array[i]
            val item = context.deserialize<Any>(element, itemType)
            list.add(item)
        }
        return list
    }
}