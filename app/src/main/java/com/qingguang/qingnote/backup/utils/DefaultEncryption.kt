package com.qingguang.qingnote.backup.utils

import com.qingguang.qingnote.backup.api.Encryption
import javax.inject.Inject


class DefaultEncryption @Inject constructor() : Encryption {

    override fun encode(key: String?): String? {
        return Base64Util.encodeToString(key)
    }

    override fun decode(password: String?): String? {
        return Base64Util.decodeToString(password)
    }
}