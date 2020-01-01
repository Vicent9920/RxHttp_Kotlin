package per.goweii.rxhttp.kt.download.interceptor

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*
import java.io.IOException

/**
 * <p>文件描述：<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2020/1/1 0001 <p>
 * <p>@update 2020/1/1 0001<p>
 * <p>版本号：1<p>
 *
 */
class DownloadResponseBody(val responseBody:ResponseBody): ResponseBody() {
    private var source: BufferedSource? = null
     var realName: String? = null
    override fun contentLength(): Long {
        return this.responseBody.contentLength()
    }

    override fun contentType(): MediaType? {
        return this.responseBody.contentType()
    }

    override fun source(): BufferedSource {
        if (source == null) {
            source = Okio.buffer(source(responseBody.source()))
        }
        return source!!
    }

    /**
     * 读取，回调进度接口
     */
    private fun source(source: Source): Source {
        return object : ForwardingSource(source) {
            @Throws(IOException::class)
            override fun read(sink: Buffer, byteCount: Long): Long {
                return super.read(sink, byteCount)
            }
        }
    }
}