package com.qingguang.qingnote.utils

import com.qingguang.qingnote.bean.Tag
import java.util.regex.Matcher
import java.util.regex.Pattern

object TopicUtils {

    // 修改正则：允许 # 开头，后面跟着汉字、字母、数字或 /
    // 注意：我们将原本匹配结尾的 [\\w\\s] 去掉，改为匹配连续的标签字符，直到遇到空格或结束
    private val inputReg = "\\#[\u4e00-\u9fa5a-zA-Z0-9/]+"
    val pattern: Pattern = Pattern.compile(inputReg)

    /**
     * 解析文本中的标签。
     * 支持多级标签，例如 #深圳/海边 会被拆解为两个标签：
     * 1. #深圳
     * 2. #深圳/海边
     * 这样在数据库中，这条笔记会同时关联到“深圳”和“深圳/海边”两个标签。
     */
    fun getTopicListByString(text: String): List<Tag> {
        val tagSet = mutableSetOf<String>()
        val matcher: Matcher = pattern.matcher(text)
        while (matcher.find()) {
            val fullTag = matcher.group().trim()
            
            if (fullTag.contains("/")) {
                // 处理多级标签，如 #A/B/C -> 产生 #A, #A/B, #A/B/C
                val parts = fullTag.split("/")
                var currentPath = ""
                parts.forEachIndexed { index, part ->
                    currentPath = if (index == 0) part else "$currentPath/$part"
                    if (currentPath.isNotBlank() && currentPath != "#") {
                        tagSet.add(currentPath)
                    }
                }
            } else {
                if (fullTag.isNotBlank() && fullTag != "#") {
                    tagSet.add(fullTag)
                }
            }
        }
        return tagSet.map { Tag(tag = it) }
    }
}

object CityRegexUtils {
    fun getCityByString(input: String): Pair<String, String>? {
        val lastIndex = input.lastIndexOf('@')

        if (lastIndex != -1 && lastIndex < input.length - 1) {
            val beforeLastAt = input.substring(0, lastIndex)
            val afterLastAt = input.substring(lastIndex + 1)
            return Pair(beforeLastAt, afterLastAt)
        }

        return Pair(input, "")
    }
}
