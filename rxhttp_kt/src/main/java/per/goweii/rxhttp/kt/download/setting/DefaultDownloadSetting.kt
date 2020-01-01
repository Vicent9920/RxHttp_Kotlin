package per.goweii.rxhttp.kt.download.setting

import per.goweii.rxhttp.kt.download.Mode

/**
 * <p>文件描述：<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2020/1/1 0001 <p>
 * <p>@update 2020/1/1 0001<p>
 * <p>版本号：1<p>
 *
 */
class DefaultDownloadSetting:DownloadSetting {
    override fun getBaseUrl(): String {
        return "http://api.rxhttp.download/"
    }

    override fun getTimeout(): Long {
        return 60000
    }

    override fun getConnectTimeout() = 0L

    override fun getReadTimeout() = 0L

    override fun getWriteTimeout() = 0L

    override fun getSaveDirPath(): String? {
        return null
    }

    override fun getDefaultDownloadMode(): Mode {
        return Mode.APPEND
    }
}