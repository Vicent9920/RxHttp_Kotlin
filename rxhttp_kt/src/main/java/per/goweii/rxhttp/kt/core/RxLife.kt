package per.goweii.rxhttp.kt.core

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class RxLife private constructor(){


    private var mCompositeDisposable = CompositeDisposable()

    fun destroy(){
        if(mCompositeDisposable.isDisposed)return
        mCompositeDisposable.dispose()
    }

    fun add(d: Disposable) {
        if ( mCompositeDisposable.isDisposed) {
            mCompositeDisposable = CompositeDisposable()
        }
        mCompositeDisposable.add(d)
    }

    companion object{
        @JvmStatic
        fun create(): RxLife{
            return RxLife()
        }
    }
}