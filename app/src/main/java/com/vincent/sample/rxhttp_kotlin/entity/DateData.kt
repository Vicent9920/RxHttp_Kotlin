package com.vincent.sample.rxhttp_kotlin.entity

import per.goweii.rxhttp.kt.request.base.BaseBean

/**
 * <p>文件描述：万年历数据<p>
 * <p>@author 烤鱼<p>
 * <p>@date 2020/1/2 0002 <p>
 * <p>@update 2020/1/2 0002<p>
 * <p>版本号：1<p>
 *
 */
data class DateData(
    val avoid: String,
    val chineseZodiac: String,
    val constellation: String,
    val date: String,
    val dayOfYear: Int,
    val lunarCalendar: String,
    val solarTerms: String,
    val suit: String,
    val type: Int,
    val typeDes: String,
    val weekDay: Int,
    val weekOfYear: Int,
    val yearTips: String
):BaseBean()