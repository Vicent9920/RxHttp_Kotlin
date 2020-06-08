package per.goweii.rxhttp.kt.download

import android.text.TextUtils
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import per.goweii.rxhttp.kt.download.exception.RangeLengthIsZeroException
import per.goweii.rxhttp.kt.download.exception.SaveFileBrokenPointException
import per.goweii.rxhttp.kt.download.exception.SaveFileDirMakeException
import per.goweii.rxhttp.kt.download.exception.SaveFileWriteException
import per.goweii.rxhttp.kt.download.interceptor.DownloadResponseBody
import per.goweii.rxhttp.kt.download.utils.DownloadInfoChecker
import per.goweii.rxhttp.kt.download.utils.RxNotify
import per.goweii.rxhttp.kt.download.utils.UnitFormatUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit

/**
 * <p>文件描述：<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2020/1/1 0001 <p>
 * <p>@update 2020/1/1 0001<p>
 * <p>版本号：1<p>
 *
 */
class RxDownload private constructor( val info: DownloadInfo) {
    companion object{
        @JvmStatic
        fun create(info: DownloadInfo): RxDownload {
            return RxDownload(info)
        }
    }

    private var mDownloadListener: DownloadListener? = null
    private var mProgressListener: ProgressListener? = null
    private var mSpeedListener: SpeedListener? = null
    private var mDisposableDownload: Disposable? = null
    private var mDisposableSpeed: Disposable? = null

    fun setDownloadListener(listener: DownloadListener): RxDownload{
        mDownloadListener = listener
        return this
    }

    fun setProgressListener(listener: ProgressListener): RxDownload{
        mProgressListener = listener
        return this
    }

    fun setSpeedListener(listener: SpeedListener): RxDownload {
        mSpeedListener = listener
        return this
    }



    fun start() {
        if (mDisposableDownload != null && mDisposableDownload?.isDisposed == false) {
            return
        }
        Observable.create<String> { emitter ->
            DownloadInfoChecker.checkDownloadLength(info)
            DownloadInfoChecker.checkContentLength(info)
            emitter.onNext("bytes=" + info.downloadLength.toString() + "-" + if (info.contentLength == 0L) "" else info.contentLength)
        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).flatMap { range -> DownloadClientManager.getService().download(range, info.url) }
                .doOnNext(object : Consumer<ResponseBody?> {
            @Throws(Exception::class)
            override fun accept(responseBody: ResponseBody?) {
                responseBody?:return
                if (info.contentLength == 0L) {
                    info.contentLength = info.downloadLength + responseBody.contentLength()
                } else if (info.downloadLength + responseBody.contentLength() != info.contentLength) {
                    throw SaveFileBrokenPointException()
                }
                DownloadInfoChecker.checkDirPath(info)
                if (TextUtils.isEmpty(info.saveFileName)) {
                    val clazz: Class<*> = responseBody.javaClass
                    val field = clazz.getDeclaredField("delegate")
                    field.isAccessible = true
                    val body: DownloadResponseBody = field[responseBody] as DownloadResponseBody
                    info.saveFileName = body.realName
                }
                DownloadInfoChecker.checkFileName(info)
                info.state = State.DOWNLOADING
                notifyDownloading()
                write(responseBody.byteStream(), createSaveFile(info))
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnDispose { cancelSpeedObserver() }.subscribe(object : Observer<ResponseBody?> {
            override fun onSubscribe(d: Disposable) {
                mDisposableDownload = d
                info.state = State.STARTING
                mDownloadListener?.onStarting(info)
            }

            override fun onNext(responseBody: ResponseBody) {
                info.state = State.COMPLETION
                mDownloadListener?.onCompletion(info)
                mDisposableDownload?.dispose()
                mDisposableDownload = null
            }

            override fun onError(e: Throwable) {
                if (e is RangeLengthIsZeroException) {
                    info.state = State.COMPLETION
                    mDownloadListener?.onCompletion(info)
                } else {
                    info.state = State.ERROR
                    mDownloadListener?.onError(info, e)
                }
                mDisposableDownload?.dispose()
                mDisposableDownload = null
            }

            override fun onComplete() {}
        })
    }

    fun stop() {
        if (mDisposableDownload != null && mDisposableDownload?.isDisposed == false) {
            mDisposableDownload?.dispose()
            mDisposableDownload = null
        }
        info.state = State.STOPPED
        mDownloadListener?.onStopped(info)
    }

    fun cancel() {
        if (mDisposableDownload != null && mDisposableDownload?.isDisposed == false) {
            mDisposableDownload?.dispose()
            mDisposableDownload = null
        }
        deleteSaveFile(info)
        info.state = State.STOPPED
        mDownloadListener?.onCanceled(info)
    }

    @Throws(SaveFileDirMakeException::class)
    private fun createSaveFile(info: DownloadInfo): File {
        val file = File(info.saveDirPath, info.saveFileName)
        if (!file.parentFile.exists()) {
            if (!file.parentFile.mkdirs()) {
                throw SaveFileDirMakeException()
            }
        }
        return file
    }

    private fun deleteSaveFile(info: DownloadInfo) {
        try {
            if (File(info.saveDirPath, info.saveFileName).delete()) {
                info.downloadLength = 0
            }
        } catch (ignore: Exception) {
        }
    }

    @Throws(SaveFileWriteException::class)
    private fun write(`is`: InputStream?, file: File) {
        createSpeedObserver()
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file, true)
            val buffer = ByteArray(2048)
            var len: Int
            while (`is`!!.read(buffer).also { len = it } != -1) {
                fos.write(buffer, 0, len)
                info.downloadLength += len
                notifyProgress()
            }
            fos.flush()
        } catch (e: IOException) {
            throw SaveFileWriteException()
        } finally {
            if (`is` != null) {
                try {
                    `is`.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (fos != null) {
                try {
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun notifyDownloading() {

        mDownloadListener?.let {
            RxNotify.runOnUiThread(object :RxNotify.Action{
                override fun run() {
                    it.onDownloading(info)
                }
            })
        }
    }

    private fun notifyProgress() {
        mProgressListener?.let {
            RxNotify.runOnUiThread(object :RxNotify.Action{
                override fun run() {
                    val progress = info.downloadLength.toFloat() / info.contentLength.toFloat()
                    it.onProgress(progress, info.downloadLength, info.contentLength)
                }
            })
        }
    }

    private fun createSpeedObserver() {
        if (mDisposableSpeed != null && mDisposableDownload?.isDisposed == false) {
            return
        }
        mDisposableSpeed = Observable.interval(1, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.computation())
                .map<Float>(object : Function<Long?, Float?> {
                    private var lastDownloadLength: Long = 0
                    @Throws(Exception::class)
                    override fun apply(ms: Long): Float {
                        val bytesPerSecond: Float = UnitFormatUtils.calculateSpeed(info.downloadLength - lastDownloadLength, 1f)
                        lastDownloadLength = info.downloadLength
                        return bytesPerSecond
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Consumer<Float?> {

                    @Throws(Exception::class)
                    override fun accept(speedPerSecond: Float?) {
                        speedPerSecond?:return
                        mSpeedListener?.onSpeedChange(speedPerSecond, UnitFormatUtils.formatSpeedPerSecond(speedPerSecond))
                    }
                })
    }

    private fun cancelSpeedObserver() {
        if (mDisposableSpeed != null &&  mDisposableDownload?.isDisposed == false) {
            mDisposableSpeed?.dispose()
        }
        mDisposableSpeed = null
    }
}

interface DownloadListener {
    fun onStarting(info: DownloadInfo?)
    fun onDownloading(info: DownloadInfo?)
    fun onStopped(info: DownloadInfo?)
    fun onCanceled(info: DownloadInfo?)
    fun onCompletion(info: DownloadInfo?)
    fun onError(info: DownloadInfo?, e: Throwable?)
}

interface ProgressListener {
    fun onProgress(progress: Float, downloadLength: Long, contentLength: Long)
}

interface SpeedListener {
    fun onSpeedChange(bytesPerSecond: Float, speedFormat: String?)
}