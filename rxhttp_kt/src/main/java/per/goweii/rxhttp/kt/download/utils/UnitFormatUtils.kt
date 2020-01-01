package per.goweii.rxhttp.kt.download.utils

import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

object UnitFormatUtils {

   private val TWO = DecimalFormat("#.##")

    fun calculateSpeed(increment: Long, duration: Float): Float {
        return increment.toFloat() / duration
    }

    fun formatSpeedPerSecond(bytePerSecond: Float): String? {
        return formatSpeed(bytePerSecond, TimeUnit.SECONDS)
    }

    fun formatSpeed(speedBytes: Float,  timeUnit: TimeUnit): String {
        return formatBytesLength(speedBytes) + "/" + formatTimeUnit(timeUnit)
    }

    fun formatBytesLength(bytes: Float): String {
        val length: Float
        val unit: String
        if (bytes < 1024L) { // 0B~1KB
            unit = "B"
            length = bytes
        } else if (bytes < 1024L * 1024L) { // 1KB~1MB
            unit = "KB"
            length = bytes / 1024L
        } else if (bytes < 1024L * 1024L * 1024L) { // 1MB~1GB
            unit = "MB"
            length = bytes / (1024L * 1024L)
        } else if (bytes < 1024L * 1024L * 1024L * 1024L) { // 1GB~1TB
            unit = "GB"
            length = bytes / (1024L * 1024L * 1024L)
        } else { // 1TB~
            unit = "TB"
            length = bytes / (1024L * 1024L * 1024L * 1024L)
        }
        return TWO.format(length.toDouble()) + unit
    }

    fun formatTimeUnit(timeUnit: TimeUnit?): String {
        if (timeUnit == null) {
            return "-"
        }
        return if (timeUnit == TimeUnit.NANOSECONDS) {
            "ns"
        } else if (timeUnit == TimeUnit.MICROSECONDS) {
            "us"
        } else if (timeUnit == TimeUnit.MILLISECONDS) {
            "ms"
        } else if (timeUnit == TimeUnit.SECONDS) {
            "s"
        } else if (timeUnit == TimeUnit.MINUTES) {
            "m"
        } else if (timeUnit == TimeUnit.HOURS) {
            "h"
        } else if (timeUnit == TimeUnit.DAYS) {
            "d"
        } else {
            "-"
        }
    }
}