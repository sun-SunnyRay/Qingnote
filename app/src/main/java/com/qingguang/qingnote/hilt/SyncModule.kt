package com.qingguang.qingnote.hilt

import android.app.Application
import android.content.Context
import com.qingguang.qingnote.backup.SyncManager
import com.qingguang.qingnote.backup.api.Encryption
import com.qingguang.qingnote.backup.utils.DefaultEncryption
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SyncModule {

    @Provides
    @Singleton
    fun providesSyncManager(
        @ApplicationContext context: Context,
    ) = SyncManager(context)

    @Provides
    fun provideEncryption(): Encryption {
        return DefaultEncryption()
    }

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

}
