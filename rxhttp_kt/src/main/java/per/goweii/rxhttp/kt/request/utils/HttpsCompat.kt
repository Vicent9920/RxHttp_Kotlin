package per.goweii.rxhttp.kt.request.utils

import android.os.Build
import okhttp3.OkHttpClient
import java.io.IOException
import java.net.InetAddress
import java.net.Socket
import java.net.UnknownHostException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

/**
 * <p>文件描述：<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2019/12/30 0030 <p>
 * <p>@update 2019/12/30 0030<p>
 * <p>版本号：1<p>
 * -------------------------
 * --- 如果服务器为HTTP请求 ---
 * -------------------------
 * android9.0以上不支持HTTP请求，默认情况下启用网络传输层安全协议 (TLS)，需在AndroidManifest中添加一个XML文件：
 * 如果您的应用以 Android 9 或更高版本为目标平台，则默认情况下 isCleartextTrafficPermitted() 函数返回 false。
 * 如果您的应用需要为特定域名启用明文，您必须在应用的网络安全性配置中针对这些域名将 cleartextTrafficPermitted 显式设置为 true。
 * 具体解决方案共二步
 * 1、在清单文件AndroidManifest.xml的application标签里面设置networkSecurityConfig属性如下:
 * <?xml version="1.0" encoding="utf-8"?>
 * <manifest ... >
 *     <application
 *         android:networkSecurityConfig="@xml/network_security_config">
 *     </application>
 * </manifest>
 * 2、在资源文件夹res/xml下面创建network_security_config.xml如下：
 * <?xml version="1.0" encoding="utf-8"?>
 * <network-security-config>
 *     <base-config cleartextTrafficPermitted="true">
 *         <trust-anchors>
 *             <certificates src="system" />
 *         </trust-anchors>
 *     </base-config>
 * </network-security-config>
 *
 * --------------------------
 * --- 如果服务器为HTTPS请求 ---
 * --------------------------
 * 第一种情况：服务器未配置SSL证书
 * 可以选择忽略证书的验证，这样请求就和HTTP一样，失去了安全保障，不建议使用
 * 第二种情况：服务器正确配置SSL证书
 * 1、服务器打开TLS1.1和TLS1.2
 * 2、在android4.4及以下版本默认不支持TLS1.2，需要开启对TLS1.2的支持
 *
 */
object HttpsCompat {
    fun ignoreSSLForOkHttp(builder: OkHttpClient.Builder) {
        builder.hostnameVerifier(getIgnoreHostnameVerifier())
                .sslSocketFactory(getIgnoreSSLSocketFactory())
    }

    fun enableTls12ForOkHttp(builder: OkHttpClient.Builder) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            val ssl = getEnableTls12SSLSocketFactory()
            if (ssl != null) {
                builder.sslSocketFactory(ssl)
            }
        }
    }

    fun ignoreSSLForHttpsURLConnection() {
        HttpsURLConnection.setDefaultHostnameVerifier(getIgnoreHostnameVerifier())
        HttpsURLConnection.setDefaultSSLSocketFactory(getIgnoreSSLSocketFactory())
    }

    fun enableTls12ForHttpsURLConnection() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            val ssl = getEnableTls12SSLSocketFactory()
            if (ssl != null) {
                HttpsURLConnection.setDefaultSSLSocketFactory(ssl)
            }
        }
    }

    /**
     * 获取开启TLS1.2的SSLSocketFactory
     * 建议在android4.4及以下版本调用
     */
    fun getEnableTls12SSLSocketFactory(): SSLSocketFactory? {
        try {
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, null, null)
            return Tls12SocketFactory(sslContext.socketFactory)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 获取忽略证书的HostnameVerifier
     * 与[.getIgnoreSSLSocketFactory]同时配置使用
     */
    fun getIgnoreHostnameVerifier(): HostnameVerifier? {
        return HostnameVerifier { s, sslSession -> true }
    }

    /**
     * 获取忽略证书的SSLSocketFactory
     * 与[.getIgnoreHostnameVerifier]同时配置使用
     */
    fun getIgnoreSSLSocketFactory(): SSLSocketFactory? {
        return try {
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, getTrustManager(), SecureRandom())
            sslContext.socketFactory
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun getTrustManager(): Array<TrustManager>? {
        return arrayOf(
                object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }
                }
        )
    }

    private class Tls12SocketFactory internal constructor(private val delegate: SSLSocketFactory) : SSLSocketFactory() {
        override fun getDefaultCipherSuites(): Array<String> {
            return delegate.defaultCipherSuites
        }

        override fun getSupportedCipherSuites(): Array<String> {
            return delegate.supportedCipherSuites
        }

        @Throws(IOException::class)
        override fun createSocket(s: Socket, host: String, port: Int, autoClose: Boolean): Socket {
            return patch(delegate.createSocket(s, host, port, autoClose))
        }

        @Throws(IOException::class, UnknownHostException::class)
        override fun createSocket(host: String, port: Int): Socket {
            return patch(delegate.createSocket(host, port))
        }

        @Throws(IOException::class, UnknownHostException::class)
        override fun createSocket(host: String, port: Int, localHost: InetAddress, localPort: Int): Socket {
            return patch(delegate.createSocket(host, port, localHost, localPort))
        }

        @Throws(IOException::class)
        override fun createSocket(host: InetAddress, port: Int): Socket {
            return patch(delegate.createSocket(host, port))
        }

        @Throws(IOException::class)
        override fun createSocket(address: InetAddress, port: Int, localAddress: InetAddress, localPort: Int): Socket {
            return patch(delegate.createSocket(address, port, localAddress, localPort))
        }

        private fun patch(s: Socket): Socket {
            if (s is SSLSocket) {
                s.enabledProtocols = TLS_SUPPORT_VERSION
            }
            return s
        }

        companion object {
            private val TLS_SUPPORT_VERSION = arrayOf("TLSv1.1", "TLSv1.2")
        }

    }
}