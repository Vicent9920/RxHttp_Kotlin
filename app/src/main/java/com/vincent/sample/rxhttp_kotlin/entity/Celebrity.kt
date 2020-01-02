package com.vincent.sample.rxhttp_kotlin.entity

import per.goweii.rxhttp.kt.request.base.BaseBean

/**
 * <p>文件描述：公众号实体<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2020/1/2 0002 <p>
 * <p>@update 2020/1/2 0002<p>
 * <p>版本号：1<p>
 *
 */
 data class Celebrity(
    val courseId: Int,
    val id: Int,
    val name: String,
    val order: Int,
    val parentChapterId: Int,
    val userControlSetTop: Boolean,
    val visible: Int
):BaseBean()