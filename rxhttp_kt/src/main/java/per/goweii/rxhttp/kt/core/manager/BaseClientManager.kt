package per.goweii.rxhttp.kt.core.manager

import retrofit2.Retrofit

/**
 * <p>文件描述：用于管理Retrofit实例<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2019/12/30 0030 <p>
 * <p>@update 2019/12/30 0030<p>
 * <p>版本号：1<p>
 *
 */
abstract class BaseClientManager {
    abstract fun create(isGson:Boolean = true): Retrofit
}