package com.qingguang.qingnote.backup.api

interface OnSyncResultListener {
    fun onSuccess(result: String?)
    fun onError(errorMsg: String?)
}