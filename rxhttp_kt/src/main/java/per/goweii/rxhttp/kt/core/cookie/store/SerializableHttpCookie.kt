package per.goweii.rxhttp.kt.core.cookie.store

import okhttp3.Cookie
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

/**
 * <p>文件描述：<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2020/1/4 0004 <p>
 * <p>@update 2020/1/4 0004<p>
 * <p>版本号：1<p>
 *
 */
class SerializableHttpCookie(@Transient val cookie: Cookie): Serializable {
    private val serialVersionUID = 6374381323722046732L
    @Transient
     var clientCookie: Cookie? = null
        private set
        get() = cookie


    @Throws(IOException::class)
    private fun writeObject(out: ObjectOutputStream) {
        out.writeObject(cookie.name)
        out.writeObject(cookie.value)
        out.writeLong(cookie.expiresAt)
        out.writeObject(cookie.domain)
        out.writeObject(cookie.path)
        out.writeBoolean(cookie.secure)
        out.writeBoolean(cookie.httpOnly)
        out.writeBoolean(cookie.hostOnly)
        out.writeBoolean(cookie.persistent)
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    private fun readObject(`in`: ObjectInputStream) {
        val name = `in`.readObject() as String
        val value = `in`.readObject() as String
        val expiresAt = `in`.readLong()
        val domain = `in`.readObject() as String
        val path = `in`.readObject() as String
        val secure = `in`.readBoolean()
        val httpOnly = `in`.readBoolean()
        val hostOnly = `in`.readBoolean()
        val persistent = `in`.readBoolean()
        var builder = Cookie.Builder()
        builder = builder.name(name)
        builder = builder.value(value)
        builder = builder.expiresAt(expiresAt)
        builder = if (hostOnly) builder.hostOnlyDomain(domain) else builder.domain(domain)
        builder = builder.path(path)
        builder = if (secure) builder.secure() else builder
        builder = if (httpOnly) builder.httpOnly() else builder
        clientCookie = builder.build()
    }



}