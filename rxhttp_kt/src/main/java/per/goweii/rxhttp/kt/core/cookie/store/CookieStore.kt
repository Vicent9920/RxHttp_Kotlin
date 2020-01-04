package per.goweii.rxhttp.kt.core.cookie.store

import okhttp3.Cookie
import okhttp3.HttpUrl

/**
 * <p>文件描述：Cookie仓库<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2020/1/4 0004 <p>
 * <p>@update 2020/1/4 0004<p>
 * <p>版本号：1<p>
 *
 */
interface CookieStore {
    /** 保存url对应所有cookie  */
    fun saveCookie(url: HttpUrl, cookie: MutableList<Cookie>)

    /** 保存url对应所有cookie  */
    fun saveCookie(url: HttpUrl, cookie: Cookie)

    /** 加载url所有的cookie  */
    fun loadCookie(url: HttpUrl): List<Cookie>

    /** 获取当前所有保存的cookie  */
    fun getAllCookie(): List<Cookie>

    /** 获取当前url对应的所有的cookie  */
    fun getCookie(url: HttpUrl): List<Cookie>

    /** 根据url和cookie移除对应的cookie  */
    fun removeCookie(url: HttpUrl, cookie: Cookie): Boolean

    /** 根据url移除所有的cookie  */
    fun removeCookie(url: HttpUrl): Boolean

    /** 移除所有的cookie  */
    fun removeAllCookie(): Boolean
}