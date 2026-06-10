package com.qingguang.qingnote.biometric

interface BiometricAuthListener {
    fun onBiometricAuthSuccess()
    fun onUserCancelled()
    fun onErrorOccurred()
}
