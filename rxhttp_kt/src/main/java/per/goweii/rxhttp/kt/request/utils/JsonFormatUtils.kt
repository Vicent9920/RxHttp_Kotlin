package per.goweii.rxhttp.kt.request.utils

object JsonFormatUtils {
    /**
     * 对json字符串格式化输出
     */
    fun format(jsonStr: String?): String {
        if(jsonStr.isNullOrEmpty())return ""
        val sb = StringBuilder()
        var last = '\u0000'
        var current = '\u0000'
        var indent = 0
        for (i in jsonStr) {
            last = current
            when (current) {
                '{', '[' -> {
                    sb.append(current)
                    sb.append('\n')
                    indent++
                    addIndentBlank(sb, indent)
                }
                '}', ']' -> {
                    sb.append('\n')
                    indent--
                    addIndentBlank(sb, indent)
                    sb.append(current)
                }
                ',' -> {
                    sb.append(current)
                    if (last != '\\') {
                        sb.append('\n')
                        addIndentBlank(sb, indent)
                    }
                }
                else -> sb.append(current)
            }
        }
        return sb.toString()
    }

    /**
     * 添加space
     */
    private fun addIndentBlank(sb: StringBuilder, indent: Int) {
        for (i in 0 until indent) {
            sb.append('\t')
        }
    }
}