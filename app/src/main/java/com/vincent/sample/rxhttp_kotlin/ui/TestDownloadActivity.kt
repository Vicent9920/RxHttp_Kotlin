package com.vincent.sample.rxhttp_kotlin.ui

import android.os.Bundle
import android.preference.PreferenceManager
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.vincent.sample.rxhttp_kotlin.R
import kotlinx.android.synthetic.main.activity_test_download.*
import per.goweii.rxhttp.kt.download.*
import per.goweii.rxhttp.kt.download.utils.UnitFormatUtils

class TestDownloadActivity : AppCompatActivity() {
    private val url =
        "https://imtt.dd.qq.com/16891/513D2C5324E6EBE77F94C85D7C76EBAE.apk?fsname=com.tencent.mobileqq_7.8.2_926.apk&csr=1bbd"
    private val tag = "TestDownloadActivity"
    private var mRxDownload: RxDownload? = null
    private var isStart = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_download)
        progress_bar.max = 10000
        et_url.setText(url)

        val downloadInfo = getDownloadInfo()
        mRxDownload = if (downloadInfo == null) {
            RxDownload.create(DownloadInfo(et_url.text.toString()))
        } else {
            progress_bar.progress = (downloadInfo.downloadLength.toFloat() / downloadInfo.contentLength.toFloat() * 10000).toInt()
            tv_download_length.text = UnitFormatUtils.formatBytesLength(downloadInfo.downloadLength.toFloat())
            tv_content_length.text = UnitFormatUtils.formatBytesLength(downloadInfo.contentLength.toFloat())
            val info = DownloadInfo(
                downloadInfo.url,
                downloadInfo.saveDirPath, downloadInfo.saveFileName,
                downloadInfo.downloadLength, downloadInfo.contentLength
            )
            RxDownload.create(info)
        }
        mRxDownload?.setDownloadListener(object : DownloadListener {
            override fun onStarting(info: DownloadInfo?) {
                tv_start_stop.text = "暂停下载"
                tv_cancel.text = "取消下载"
            }

            override fun onDownloading(info: DownloadInfo?) {
                tv_start_stop.text = "暂停下载"
            }

            override fun onStopped(info: DownloadInfo?) {
                saveDownloadInfo()
                tv_start_stop.text = "开始下载"
                tv_speed.text = ""
            }

            override fun onCanceled(info: DownloadInfo?) {
                saveDownloadInfo()
                tv_start_stop.text = "开始下载"
                tv_cancel.text = "已取消"
                progress_bar.progress = 0
                tv_speed.text = ""
                tv_download_length.text = ""
                tv_content_length.text = ""
            }

            override fun onCompletion(info: DownloadInfo?) {
                saveDownloadInfo()
                tv_start_stop.text = "下载成功"
                tv_speed.text = ""
            }

            override fun onError(info: DownloadInfo?, e: Throwable?) {
                saveDownloadInfo()
                tv_start_stop.text = "开始下载"
                tv_speed.text = ""
                Log.e(tag,"onError",e)
            }
        })?.setProgressListener(object :ProgressListener{
            override fun onProgress(progress: Float, downloadLength: Long, contentLength: Long) {
                progress_bar.progress = (progress * 10000).toInt()
                tv_download_length.text =
                    UnitFormatUtils.formatBytesLength(downloadLength.toFloat())
                tv_content_length.text = UnitFormatUtils.formatBytesLength(contentLength.toFloat())
            }
        })?.setSpeedListener(object :SpeedListener{
            override fun onSpeedChange(bytesPerSecond: Float, speedFormat: String?) {
                tv_speed.text = speedFormat
            }
        })

        tv_start_stop.setOnClickListener {
            isStart = if (isStart) {
                mRxDownload?.stop()
                false
            } else {
                mRxDownload?.start()
                true
            }
        }
        tv_cancel.setOnClickListener {
            mRxDownload?.cancel()
            isStart = false
        }
        tv_clean.setOnClickListener {
            cleanDownloadInfo()
            tv_download_length.text = ""
            tv_content_length.text = ""
            tv_speed.text = ""
            progress_bar.progress = 0
            tv_start_stop.text = "开始下载"
            tv_cancel.text = "取消下载"
        }


    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (isStart) {
                    mRxDownload?.stop()
                    isStart = false
                    return false
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }
    private fun getDownloadInfo(): DownloadInfo? {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val url:String = sp.getString("url", "")?:""
        val saveDirName = sp.getString("saveDirPath", "")
        val saveFileName = sp.getString("saveFileName", "")
        val downloadLength = sp.getLong("downloadLength", 0)
        val contentLength = sp.getLong("contentLength", 0)
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(saveDirName) || TextUtils.isEmpty(
                saveFileName
            )
        ) {
            return null
        }
        if (downloadLength == 0L) {
            return null
        }
        return if (contentLength < downloadLength) {
            null
        } else DownloadInfo(url, saveDirName, saveFileName, downloadLength, contentLength)
    }

    private fun saveDownloadInfo() {
        val info = mRxDownload?.info
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sp.edit()
        editor.putString("url", info?.url)
        editor.putString("saveDirPath", info?.saveDirPath)
        editor.putString("saveFileName", info?.saveFileName)
        editor.putLong("downloadLength", info?.downloadLength?:0L)
        editor.putLong("contentLength", info?.contentLength?:0L)
        editor.apply()
    }
    private fun cleanDownloadInfo() {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        sp.edit().clear().apply()
    }
}
