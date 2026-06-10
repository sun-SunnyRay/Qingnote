package com.qingguang.qingnote.backup.api

interface  Encryption {
    //加密
    fun encode(key: String?): String?

    //解密
    fun decode(password: String?): String?
}