package com.vincent.sample.rxhttp_kotlin.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.haoge.easyandroid.easy.EasyLog
import com.vincent.sample.rxhttp_kotlin.R
import com.vincent.sample.rxhttp_kotlin.entity.Banner
import com.vincent.sample.rxhttp_kotlin.entity.Celebrity
import com.vincent.sample.rxhttp_kotlin.entity.RegisterBean
import com.vincent.sample.rxhttp_kotlin.entity.UploadImgBean
import com.vincent.sample.rxhttp_kotlin.net.FreeApi
import com.vincent.sample.rxhttp_kotlin.net.MessageApi
import com.vincent.sample.rxhttp_kotlin.net.Service
import kotlinx.android.synthetic.main.activity_test_request.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import per.goweii.rxhttp.kt.core.RxHttp
import per.goweii.rxhttp.kt.core.RxLife
import per.goweii.rxhttp.kt.request.RequestClientManager
import per.goweii.rxhttp.kt.request.RequestListener
import per.goweii.rxhttp.kt.request.ResultCallback
import per.goweii.rxhttp.kt.request.exception.ExceptionHandle
import per.goweii.rxhttp.kt.request.setting.DefaultRequestSetting
import per.goweii.rxhttp.kt.request.setting.ParameterGetter
import per.goweii.rxhttp.kt.request.utils.RequestBodyUtils.builder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession
import kotlin.collections.HashMap


class TestRequestActivity : AppCompatActivity() {

    private val mRxLife: RxLife by lazy {
        RxLife.create()
    }
    private val reqListener = object : RequestListener {
        private var timeStart: Long = 0
        @SuppressLint("SetTextI18n")
        override fun onStart() {
            tv_log.text = ""
            tv_log.text = "onStart"
            timeStart = System.currentTimeMillis()
        }

        @SuppressLint("SetTextI18n")
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
                return 0
            }



            // 重定向地址设置
            override fun getRedirectBaseUrl(): Map<String, String> {
                val urls: MutableMap<String, String> = HashMap(1)
                urls[FreeApi.Config.BASE_URL_OTHER_NAME] = FreeApi.Config.BASE_URL_OTHER
                return urls
            }

            override fun getServiceBaseUrl(): Map<Class<*>, String> {
                return mapOf(Pair(Service::class.java,"http://47.99.221.176:8927/"))
            }

            override fun getWriteTimeout(): Long {
                return 50*1000
            }
            override fun isDebug(): Pair<Boolean, HttpLoggingInterceptor.Level> {
                return Pair(true,HttpLoggingInterceptor.Level.BODY)
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
        mRxLife.add(
            RxHttp.request(FreeApi.api().getCelebrities("wxarticle/chapters/json")).listener(reqListener)
                .request(object : ResultCallback<List<Celebrity>> {
                    @SuppressLint("SetTextI18n")
                    override fun onSuccess(code: Int, data: List<Celebrity>?) {
                        tv_log.text =
                            "${tv_log.text}\nonSuccess {code-${code} data-${Gson().toJson(data)}}"
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onFailed(code: Int, msg: String?) {
                        tv_log.text = "${tv_log.text}\nonFailed {code-${code} msg-${msg}}"
                        Toast.makeText(this@TestRequestActivity,"看看源码的接口地址注释就明白了",Toast.LENGTH_SHORT).show()
                    }
                })
        )
    }


    /**
     * 获取轮播条广告
     */
    private fun getBanner() {
        Log.i("TAG","getBanner")
        mRxLife.add(
            RxHttp.request(FreeApi.api().getBannerList()).listener(reqListener)
                .request(object : ResultCallback<List<Banner>> {
                    @SuppressLint("SetTextI18n")
                    override fun onSuccess(code: Int, data: List<Banner>?) {
                        tv_log.text =
                            "${tv_log.text}\nonSuccess {code-${code} data-${Gson().toJson(data)}}"
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
        mRxLife.add(
            RxHttp.request(
                FreeApi.api().register(
                    et_userName.text.toString(),
                    et_password.text.toString()
                    , et_password.text.toString()
                )
            ).listener(reqListener)
                .request(object : ResultCallback<RegisterBean> {
                    @SuppressLint("SetTextI18n")
                    override fun onSuccess(code: Int, data: RegisterBean?) {
                        tv_log.text =
                            "${tv_log.text}\nonSuccess {code-${code} data-${Gson().toJson(data)}}"
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
     * 数据实体非标准实体 ，直接返回的是{@link RegisterBean}
     */
    @SuppressLint("SetTextI18n")
    private fun singlePoetry() {
        Log.w("TAG","singlePoetry")
        mRxLife.add(RxHttp.customRequest(FreeApi.api().singlePoetry())
            .listener(reqListener).customEntityRequest {
                tv_log.text = "${tv_log.text}\nonSuccess {${Gson().toJson(it)}}}"
            })
    }


    /**
     * 获取今日信息
     * 由于SuccessCode 非标准Code,因此返回整个数据实体自定义
     */
    @SuppressLint("SetTextI18n")
    private fun getCurrentDate() {
        val map = HashMap<String, Any>()
        map["clid"] = "246"
        map["jqid"] = "807f796f0b52645f12430b5d187c179f"
        map["gisX"] = 1.0
        map["gisY"] = 2.0
        map["clzt"] = "0304"
        mRxLife.add(
            RxHttp.customRequest(
                MessageApi.api(false)
                    .test(
                        "admin","admin"))
                .listener(reqListener)
                .customEntityRequest {
                    EasyLog.DEFAULT.e(it)
                })
//        FreeApi.api().test("admin","admin","http://47.99.221.176:8927/app/array").enqueue(object :
//            Callback<ResponseBody>{
//            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                EasyLog.DEFAULT.e(response.body()?.string())
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                EasyLog.DEFAULT.e(t.message)
//            }
//        })
//        mRxLife.add(
//            RxHttp.request(
//                FreeApi.api()
//                    .getDate("http://www.mxnzp.com/api/holiday/single/${getCurrent()}")
//            )
//                .listener(reqListener)
//                .customRequest {
//                    // 访问成功
//                    if (it.getCode() == 1) {
//                        tv_log.text =
//                            "${tv_log.text}\nonSuccess {code-${it.getCode()} data-${Gson().toJson(it.getData())}}"
//                    } else {
//                        tv_log.text =
//                            "${tv_log.text}\nonFailed {code-${it.getCode()} msg-${it.getMsg()}}"
//                    }
//                })
    }

    private fun uploadImg(content: String, imgFile: File) {
        val map = builder()
            .add<Any>("content", content)
            .add<Any>("img", imgFile)
            .build()
        mRxLife.add(
            RxHttp.request(FreeApi.api().uploadImg(map)).listener(reqListener)
                .request(object : ResultCallback<UploadImgBean> {
                    @SuppressLint("SetTextI18n")
                    override fun onSuccess(code: Int, data: UploadImgBean?) {
                        tv_log.text =
                            "${tv_log.text}\nonSuccess {code-${code} data-${Gson().toJson(data)}}"
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onFailed(code: Int, msg: String?) {
                        tv_log.text = "${tv_log.text}\nonFailed {code-${code} msg-${msg}}"
                    }
                })
        )
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCurrent(): String {
        return SimpleDateFormat("yyyyMMdd").format(Date())
    }

    override fun onDestroy() {
        super.onDestroy()
        mRxLife.destroy()
    }
}
