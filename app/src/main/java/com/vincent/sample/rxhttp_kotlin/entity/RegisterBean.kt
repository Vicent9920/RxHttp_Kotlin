package com.vincent.sample.rxhttp_kotlin.entity

import per.goweii.rxhttp.kt.request.base.BaseBean

/**
 * <p>文件描述：注册的实体<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2020/1/2 0002 <p>
 * <p>@update 2020/1/2 0002<p>
 * <p>版本号：1<p>
 *
 */
data class RegisterBean(
    val admin: Boolean,
    val chapterTops: List<Any>,
    val collectIds: List<Any>,
    val email: String,
    val icon: String,
    val id: Int,
    val nickname: String,
    val password: String,
    val publicName: String,
    val token: String,
    val type: Int,
    val username: String
):BaseBean()