package per.goweii.rxhttp.kt.download.setting

import per.goweii.rxhttp.kt.download.DownloadInfo
import per.goweii.rxhttp.kt.download.Mode

/**
 * <p>文件描述：<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2020/1/1 0001 <p>
 * <p>@update 2020/1/1 0001<p>
 * <p>版本号：1<p>
 *
 */
interface DownloadSetting {

    fun getBaseUrl(): String

    /**
     * 获取默认超时时长，单位为毫秒数
     */
    fun getTimeout(): Long

    /**
     * 获取Connect超时时长，单位为毫秒数
     * 返回0则去getTimeout
     */
    fun getConnectTimeout(): Long

    /**
     * 获取Read超时时长，单位为毫秒数
     * 返回0则去getTimeout
     */
    fun getReadTimeout(): Long

    /**
     * 获取Write超时时长，单位为毫秒数
     * 返回0则去getTimeout
     */
    fun getWriteTimeout(): Long


    fun getSaveDirPath(): String?


    fun getDefaultDownloadMode(): Mode
}