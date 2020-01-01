package per.goweii.rxhttp.kt.request.gson

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import per.goweii.rxhttp.kt.request.base.BaseBean
import java.lang.reflect.Type

/**
 * <p>文件描述：<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2019/12/30 0030 <p>
 * <p>@update 2019/12/30 0030<p>
 * <p>版本号：1<p>
 *
 */
class BaseBeanDeserializer: JsonDeserializer<BaseBean> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): BaseBean ?{
        if(json == null || json.isJsonObject.not() || typeOfT == null || context == null)return null
        val jsonObj = json.asJsonObject
        try {
            val clazz = typeOfT as Class<BaseBean>
            val baseBean = clazz.newInstance()
            for (entry in jsonObj.entrySet()){
                val itemKey = entry.key
                val itemElement = entry.value
                try {
                    val field = clazz.getDeclaredField(itemKey)
                    field.isAccessible = true
                    val item = context.deserialize<Any>(itemElement, field.type)
                    field[baseBean] = item
                } catch (e: NoSuchFieldException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            }
            return baseBean
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }catch (e:InstantiationException){
            e.printStackTrace()
        }
        return null
    }
}