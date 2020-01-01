package per.goweii.rxhttp.kt.request.utils

import org.json.JSONException
import org.json.JSONObject

/**
 * <p>文件描述：<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2019/12/30 0030 <p>
 * <p>@update 2019/12/30 0030<p>
 * <p>版本号：1<p>
 *
 */
class JsonObjUtils {

    val mJsonObject = JSONObject()
    fun add(key: String?, value: Int): JsonObjUtils? {
        try {
            mJsonObject.put(key, value)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return this
    }

    fun add(key: String?, value: Float): JsonObjUtils? {
        try {
            mJsonObject.put(key, value.toDouble())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return this
    }

    fun add(key: String?, value: Double): JsonObjUtils? {
        try {
            mJsonObject.put(key, value)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return this
    }

    fun add(key: String?, value: Boolean): JsonObjUtils? {
        try {
            mJsonObject.put(key, if (value) 1 else 0)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return this
    }

    fun add(key: String?, value: String?): JsonObjUtils? {
        try {
            mJsonObject.put(key, value)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return this
    }

    fun get(): JSONObject? {
        return mJsonObject
    }

    fun toJson(): String? {
        return mJsonObject.toString()
    }
}