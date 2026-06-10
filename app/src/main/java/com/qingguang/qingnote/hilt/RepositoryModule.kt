package com.qingguang.qingnote.hilt

import com.qingguang.qingnote.db.AppDatabase
import com.qingguang.qingnote.db.repo.TagNoteRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideNoteRepository(
        appDatabase: AppDatabase,
    ) = TagNoteRepo(appDatabase.getNoteDao(), appDatabase.getTagNote(), appDatabase.getTagDao(), appDatabase.getNoteTagCrossRefDao())
}