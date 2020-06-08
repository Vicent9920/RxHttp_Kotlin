package per.goweii.rxhttp.kt.download

import per.goweii.rxhttp.kt.core.RxHttp

/**
 * <p>文件描述：<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2020/1/1 0001 <p>
 * <p>@update 2020/1/1 0001<p>
 * <p>版本号：1<p>
 *
 */
data class DownloadInfo @JvmOverloads constructor(var url:String,
                                                  var saveDirPath:String? = null,
                                                  var saveFileName:String? = null,
                                                  var downloadLength:Long = 0L,
                                                  var contentLength: Long = 0) {

    @JvmField
    var state: State = State.STOPPED
    @JvmField
    var mode: Mode = RxHttp.getDownloadSetting().getDefaultDownloadMode()


}

/**
 * 如果需要断点续传下载，请设置 APPEND 默认值为 APPEND
 */
enum class Mode {
    /**
     * 追加
     */
    APPEND,
    /**
     * 替换
     */
    REPLACE,
    /**
     * 重命名
     */
    RENAME
}

enum class State {
    /**
     * 正在开始
     */
    STARTING,
    /**
     * 正在下载
     */
    DOWNLOADING,
    /**
     * 已停止
     */
    STOPPED,
    /**
     * 下载出错
     */
    ERROR,
    /**
     * 下载完成
     */
    COMPLETION
}