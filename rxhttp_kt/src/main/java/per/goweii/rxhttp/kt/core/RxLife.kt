package per.goweii.rxhttp.kt.core

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

interface RxLife {


    fun destroy()

    fun add(d: Disposable)

    companion object {

        fun create(): RxLife {
            return RxLifeImp(CompositeDisposable())
        }
    }
}

class RxLifeImp(private val mCompositeDisposable: CompositeDisposable) : RxLife {

    override fun destroy() {
        if (mCompositeDisposable.isDisposed) return
        mCompositeDisposable.dispose()
    }

    override fun add(d: Disposable) {
        mCompositeDisposable.add(d)
    }
}