package com.vincent.sample.rxhttp_kotlin.ui

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.haoge.easyandroid.EasyAndroid
import com.haoge.easyandroid.easy.EasyPermissions
import com.haoge.easyandroid.easy.EasyToast
import com.vincent.sample.rxhttp_kotlin.R
import kotlinx.android.synthetic.main.activity_main.*
import per.goweii.rxhttp.kt.core.RxHttp

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        RxHttp.mAppContext = applicationContext
        tv_request.setOnClickListener {
            startActivity(Intent(this,TestRequestActivity::class.java))
        }
        tv_download.setOnClickListener {
            startActivity(Intent(this,TestDownloadActivity::class.java))
        }
       
    }
}
