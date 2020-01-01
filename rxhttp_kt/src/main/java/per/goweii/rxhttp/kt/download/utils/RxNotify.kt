package per.goweii.rxhttp.kt.download.utils

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * <p>文件描述：<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2020/1/1 0001 <p>
 * <p>@update 2020/1/1 0001<p>
 * <p>版本号：1<p>
 *
 */
object RxNotify {

    fun runOnUiThread( action: Action) {
        Observable.empty<Any>()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Any?> {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onNext(o: Any) {}
                    override fun onError(e: Throwable) {}
                    override fun onComplete() {
                        action.run()
                    }
                })
    }

    interface Action {
        fun run()
    }
}