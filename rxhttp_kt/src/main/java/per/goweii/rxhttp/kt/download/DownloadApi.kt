package per.goweii.rxhttp.kt.download

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * <p>文件描述：<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2020/1/1 0001 <p>
 * <p>@update 2020/1/1 0001<p>
 * <p>版本号：1<p>
 *
 */
interface DownloadApi {
    @Streaming
    @GET
    fun download(@Header("RANGE") range: String, @Url url: String): Observable<ResponseBody>
}