package per.goweii.rxhttp.kt.request

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import per.goweii.rxhttp.kt.core.RxHttp
import per.goweii.rxhttp.kt.core.RxLife
import per.goweii.rxhttp.kt.request.base.BaseResponse
import per.goweii.rxhttp.kt.request.exception.ApiException
import per.goweii.rxhttp.kt.request.exception.ExceptionHandle

/**
 * <p>文件描述：网络请求<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2020/1/1 0001 <p>
 * <p>@update 2020/1/1 0001<p>
 * <p>版本号：1<p>
 *
 */
class RxRequest<T, E> where E : BaseResponse<T> {
    private var mCallback: ResultCallback<T>? = null
    private var mListener: RequestListener? = null
    private var mRxLife: RxLife? = null

    companion object {
        @JvmStatic
        fun <T, E : BaseResponse<T>> create(observable: Observable<E>): RxRequest<T, E> {
            return RxRequest(observable)
        }
    }

    private  val mObservable: Observable<E>

    private constructor(observable: Observable<E>) {
        this.mObservable = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 添加请求生命周期的监听
     */
    fun listener(listener: RequestListener): RxRequest<T, E> {
        mListener = listener
        return this
    }

    /**
     * 用于中断请求，管理请求生命周期
     *
     * @param rxLife 详见[RxLife]
     */
    fun autoLife(rxLife: RxLife): RxRequest<T, E> {
        mRxLife = rxLife
        return this
    }

    fun request(callback: ResultCallback<T>): Disposable {
        this.mCallback = callback
        val disposable = mObservable.subscribe(object : Consumer<BaseResponse<T>> {
            override fun accept(bean: BaseResponse<T>?) {
                bean?:return
                if (!isSuccess(bean.getCode())) {
                    throw ApiException(bean.getCode(), bean.getMsg()?:"请求失败")
                }
                mCallback?.onSuccess(bean.getCode(), bean.getData())
            }
        }, Consumer<Throwable> { t ->
            if (t is ApiException) {
                mCallback?.onFailed(t.code,t.msg)
            } else {
                mListener?.let {
                    var handle:ExceptionHandle? = RxHttp.getRequestSetting()?.getExceptionHandle()
                    if(handle == null){
                        handle = ExceptionHandle(t)
                    }
                    mListener?.onError(handle)
                }

            }
            mListener?.onFinish()
        }, Action { mListener?.onFinish() },
                Consumer<Disposable> { mListener?.onStart() })
        mRxLife?.add(disposable)
        return disposable
    }

    private fun isSuccess(code: Int): Boolean {
        if (code == RxHttp.getRequestSetting()?.getSuccessCode()) {
            return true
        }
        val codes = RxHttp.getRequestSetting()?.getMultiSuccessCode()
        if (codes == null || codes.isEmpty()) {
            return false
        }
        for (i in codes) {
            if (code == i) {
                return true
            }
        }
        return false
    }
}

interface ResultCallback<E> {
    fun onSuccess(code: Int, data: E?)
    fun onFailed(code: Int, msg: String?)
}

interface RequestListener {
    fun onStart()
    fun onError(handle: ExceptionHandle?)
    fun onFinish()
}