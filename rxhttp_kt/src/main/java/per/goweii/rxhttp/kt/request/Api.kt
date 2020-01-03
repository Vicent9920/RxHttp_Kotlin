package per.goweii.rxhttp.kt.request

/**
 * <p>文件描述：子类继承，用于创建一个API接口实例<p>
 * <p>新写一个无参静态方法调用{@link #api(Class)}去创建一个接口实例<p>
 * <p>方法{@link #api(Class)}的参数为ServiceInterface，建议为内部类<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2019/12/30 0030 <p>
 * <p>@update 2019/12/30 0030<p>
 * <p>版本号：1<p>
 *
 */
open class Api {

    interface Header {

        companion object {
            /**
             * 添加以这个为名的Header可以让这个Request使用另一个BaseUrl
             * [RequestSetting.getRedirectBaseUrl]
             */
            const val BASE_URL_REDIRECT = "RxHttp-BaseUrl-Redirect"
            /**
             * 添加以这个为名的Header可以让这个Request支持缓存（有网联网获取，无网读取缓存）
             * 如//@Headers({Header.CACHE_ALIVE_SECOND + ":" + 10})
             */
            const val CACHE_ALIVE_SECOND = "RxHttp-Cache-Alive-Second"
        }
    }

    /**
     * 创建一个接口实例
     *
     * @param clazz Retrofit的ServiceInterface，建议定义为子类的内部接口
     * @param <T>   ServiceInterface的名字
     * @return 接口实例
    </T> */
     open fun <T> api(clazz: Class<T>): T {
        return RequestClientManager.getService(clazz)
    }
}