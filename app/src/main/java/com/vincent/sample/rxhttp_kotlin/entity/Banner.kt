package com.vincent.sample.rxhttp_kotlin.entity

import per.goweii.rxhttp.kt.request.base.BaseBean

/**
 * <p>文件描述：头部Banner实体<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2020/1/2 0002 <p>
 * <p>@update 2020/1/2 0002<p>
 * <p>版本号：1<p>
 *
 */
data class Banner(
    val desc: String,
    val id: Int,
    val imagePath: String,
    val isVisible: Int,
    val order: Int,
    val title: String,
    val type: Int,
    val url: String
):BaseBean()