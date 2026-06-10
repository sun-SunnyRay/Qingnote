package com.qingguang.qingnote.utils

import android.content.Context
import com.qingguang.qingnote.biometric.AppBioMetricManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class BioMetricUtil {

    @Provides
    fun provideAppBioMetricManager(context: Context): AppBioMetricManager {
        return AppBioMetricManager(context)
    }
}
