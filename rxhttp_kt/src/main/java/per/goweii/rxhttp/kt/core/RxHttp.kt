package per.goweii.rxhttp.kt.core

import android.content.Context
import io.reactivex.Observable
import per.goweii.rxhttp.kt.R
import per.goweii.rxhttp.kt.core.exception.RxHttpUninitializedException
import per.goweii.rxhttp.kt.download.DownloadInfo
import per.goweii.rxhttp.kt.download.RxDownload
import per.goweii.rxhttp.kt.download.setting.DefaultDownloadSetting
import per.goweii.rxhttp.kt.download.setting.DownloadSetting
import per.goweii.rxhttp.kt.request.RxRequest
import per.goweii.rxhttp.kt.request.base.BaseResponse
import per.goweii.rxhttp.kt.request.setting.RequestSetting
import per.goweii.rxhttp.kt.request.exception.NullRequestSettingException

object RxHttp {

      var mAppContext: Context? = null

    private var mRequestSetting: RequestSetting? = null
    private var mDownloadSetting: DownloadSetting? = null


    private fun checkInit() {
        if(mAppContext == null){
            throw RxHttpUninitializedException()
        }
    }

    fun initRequest( setting: RequestSetting) {
        this.mRequestSetting = setting
    }

    fun initDownload( setting: DownloadSetting) {
        this.mDownloadSetting = setting
    }

    fun getRequestSetting(): RequestSetting? {
        return this.mRequestSetting
                ?: throw NullRequestSettingException()
    }

    fun getDownloadSetting(): DownloadSetting {

        if (mDownloadSetting == null) {
            return DefaultDownloadSetting()
        }
        return mDownloadSetting!!
    }

    fun <T, R : BaseResponse<T>> request(observable: Observable<R>): RxRequest<T, R> {
        return RxRequest.create(observable)
    }

    fun <T> customRequest(observable: Observable<T>): RxRequest<T,  BaseResponse<T>> {
        return RxRequest.createCustom(observable)
    }

    fun download( info: DownloadInfo): RxDownload {
        return RxDownload.create(info)
    }

}