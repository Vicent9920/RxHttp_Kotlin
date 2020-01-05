package per.goweii.rxhttp.kt.core.cookie

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import per.goweii.rxhttp.kt.core.cookie.store.CookieStore

/**
 * <p>文件描述：<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2020/1/4 0004 <p>
 * <p>@update 2020/1/4 0004<p>
 * <p>版本号：1<p>
 *
 */
class CookieJarImpl(private val cookieStore: CookieStore): CookieJar {
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieStore.saveCookie(url, cookies)
    }

    override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
        return cookieStore.loadCookie(url).toMutableList()
    }
}