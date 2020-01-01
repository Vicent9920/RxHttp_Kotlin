package com.vincent.sample.rxhttp_kotlin.entity

import per.goweii.rxhttp.kt.request.base.BaseBean

/**
 * <p>文件描述：<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2020/1/1 0001 <p>
 * <p>@update 2020/1/1 0001<p>
 * <p>版本号：1<p>
 *
 */
data class RecommendPoetryBean(val title:String,val content:String,val authors:String):BaseBean() {

}