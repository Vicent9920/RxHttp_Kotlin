package per.goweii.rxhttp.kt.download.exception

import java.lang.RuntimeException

/**
 * <p>文件描述：<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2020/1/1 0001 <p>
 * <p>@update 2020/1/1 0001<p>
 * <p>版本号：1<p>
 *
 */
class SaveFileDirMakeException:RuntimeException("下载保存的文件父文件夹创建失败") {
}