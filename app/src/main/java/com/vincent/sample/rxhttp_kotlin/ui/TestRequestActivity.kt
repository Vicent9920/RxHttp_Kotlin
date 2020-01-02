package com.vincent.sample.rxhttp_kotlin.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.vincent.sample.rxhttp_kotlin.R
import com.vincent.sample.rxhttp_kotlin.entity.*
import com.vincent.sample.rxhttp_kotlin.net.FreeApi
import kotlinx.android.synthetic.main.activity_test_request.*
import okhttp3.OkHttpClient
import per.goweii.rxhttp.kt.core.RxHttp
import per.goweii.rxhttp.kt.core.RxLife
import per.goweii.rxhttp.kt.request.RequestListener
import per.goweii.rxhttp.kt.request.ResultCallback
import per.goweii.rxhttp.kt.request.exception.ExceptionHandle
import per.goweii.rxhttp.kt.request.setting.DefaultRequestSetting
import per.goweii.rxhttp.kt.request.setting.ParameterGetter
import java.text.SimpleDateFormat
import java.util.*

class TestRequestActivity : AppCompatActivity() {

    private val mRxLife: RxLife by lazy {
        RxLife.create()
    }
    private val reqListener = object : RequestListener {
        private var timeStart: Long = 0
        override fun onStart() {
            tv_log.text = ""
            tv_log.text = "onStart"
            timeStart = System.currentTimeMillis()
        }

        override fun onError(handle: ExceptionHandle?) {
            tv_log.text = "${tv_log.text}\nonError${handle?.msg}"
        }

        @SuppressLint("SetTextI18n")
        override fun onFinish() {
            val cast = System.currentTimeMillis() - timeStart
            tv_log.text = "${tv_log.text}\nonFinish $cast"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_request)
        RxHttp.initRequest(object : DefaultRequestSetting() {
            // baseUrl 设置
            override fun getBaseUrl(): String {
                return FreeApi.Config.BASE_URL
            }

            // Code 判断
            override fun getSuccessCode(): Int {
                return FreeApi.Code.SUCCESS
            }

            // 重定向地址设置
            override fun getRedirectBaseUrl(): Map<String, String> {
                val urls: MutableMap<String, String> =
                    HashMap(1)
                urls[FreeApi.Config.BASE_URL_OTHER_NAME] = FreeApi.Config.BASE_URL_OTHER
                return urls
            }

            // 公共参数设置
            override fun getStaticHeaderParameter(): Map<String, String> {
//                val parameters: MutableMap<String, String> =
//                    HashMap(3)
//                parameters["system"] = "android"
//                parameters["version_code"] = "1"
//                parameters["device_num"] = "666"
//                return parameters
                return super.getStaticHeaderParameter()
            }

            // 设置动态参数
            override fun getDynamicHeaderParameter(): Map<String, ParameterGetter> {
//                val parameters: HashMap<String, ParameterGetter> =
//                    HashMap()
//                val value = object :ParameterGetter{
//                    override fun get(): String {
//                        return "9527"
//                    }
//                }
//                parameters["id"] = value
//                return parameters
                return super.getDynamicHeaderParameter()
            }

            override fun setOkHttpClient(builder: OkHttpClient.Builder) {
                builder.hostnameVerifier { hostname, session ->
                    // 验证主机名是否与服务器的身份验证方案匹配。
                    true
                }
//                super.setOkHttpClient(builder)
            }
        })

        /**************************** 监听事件  *************************************/
        tv_get_celebrities.setOnClickListener {
            getCelebrities()
        }
        tv_get_banner.setOnClickListener {
            getBanner()
        }
        tv_get_register.setOnClickListener {
            register()
        }
        tv_get_singlePoetry.setOnClickListener {
            singlePoetry()
        }

        tv_get_date.setOnClickListener {
            getCurrentDate()
        }
    }

    /**
     * 获取公众号列表
     */
    private fun getCelebrities() {
        try {
            mRxLife.add(RxHttp.request(FreeApi.api().getCelebrities()).listener(reqListener)
                .request(object : ResultCallback<List<Celebrity>> {
                    override fun onSuccess(code: Int, data: List<Celebrity>?) {
                        tv_log.text = "${tv_log.text}\nonSuccess {code-${code} data-${Gson().toJson(data)}}"
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onFailed(code: Int, msg: String?) {
                        tv_log.text = "${tv_log.text}\nonFailed {code-${code} msg-${msg}}"
                    }
                })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * 获取轮播条广告
     */
    private fun getBanner() {
        mRxLife.add(RxHttp.request(FreeApi.api().getBannerList()).listener(reqListener)
            .request(object : ResultCallback<List<Banner>> {
                override fun onSuccess(code: Int, data: List<Banner>?) {
                    tv_log.text = "${tv_log.text}\nonSuccess {code-${code} data-${Gson().toJson(data)}}"
                }

                @SuppressLint("SetTextI18n")
                override fun onFailed(code: Int, msg: String?) {
                    tv_log.text = "${tv_log.text}\nonFailed {code-${code} msg-${msg}}"
                }
            })
        )
    }

    /**
     * 注册
     */
    private fun register() {
        et_userName.text
        mRxLife.add(RxHttp.request(
            FreeApi.api().register(
                et_userName.text.toString(),
                et_password.text.toString()
                , et_password.text.toString()
            )
        ).listener(reqListener)
            .request(object : ResultCallback<RegisterBean> {
                override fun onSuccess(code: Int, data: RegisterBean?) {
                    tv_log.text = "${tv_log.text}\nonSuccess {code-${code} data-${Gson().toJson(data)}}"
                }

                @SuppressLint("SetTextI18n")
                override fun onFailed(code: Int, msg: String?) {
                    tv_log.text = "${tv_log.text}\nonFailed {code-${code} msg-${msg}}"
                }
            })
        )
    }

    /**
     * 重定向
     */
    private fun singlePoetry() {
        mRxLife.add(RxHttp.request(FreeApi.api().singlePoetry()).listener(reqListener)
            .request(object : ResultCallback<SinglePoetryBean> {
                override fun onSuccess(code: Int, data: SinglePoetryBean?) {
                    tv_log.text = "${tv_log.text}\nonSuccess {code-${code} data-${Gson().toJson(data)}}"
                }

                @SuppressLint("SetTextI18n")
                override fun onFailed(code: Int, msg: String?) {
                    tv_log.text = "${tv_log.text}\nonFailed {code-${code} msg-${msg}}"
                }
            })
        )
    }

    /**
     * 获取今日信息
     */
    private fun getCurrentDate() {
        mRxLife.add(RxHttp.request(FreeApi.api().getDate("www.mxnzp.com/api/holiday/single/${getCurrent()}")).listener(
            reqListener
        )
            .request(object : ResultCallback<DateData> {
                override fun onSuccess(code: Int, data: DateData?) {
                    tv_log.text = "${tv_log.text}\nonSuccess {code-${code} data-${Gson().toJson(data)}}"
                }

                @SuppressLint("SetTextI18n")
                override fun onFailed(code: Int, msg: String?) {
                    tv_log.text = "${tv_log.text}\nonFailed {code-${code} msg-${msg}}"
                }
            })
        )
    }

    private fun getCurrent(): String {
        return SimpleDateFormat("yyyy-MM-dd").format(Date())
    }

    override fun onDestroy() {
        super.onDestroy()
        mRxLife.destroy()
    }
}
