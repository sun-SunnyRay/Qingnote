package org.tasks.injection

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.tasks.data.db.Database
import org.tasks.data.dao.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TasksDatabaseModule {
    private const val TASKS_DATABASE_NAME = "tasks_db"

    @Provides
    @Singleton
    fun provideTasksDatabase(@ApplicationContext context: Context): Database {
        return Room.databaseBuilder(context, Database::class.java, TASKS_DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides fun provideTaskDao(db: Database): TaskDao = db.taskDao()
    @Provides fun provideAlarmDao(db: Database): AlarmDao = db.alarmDao()
    @Provides fun provideTagDao(db: Database): TagDao = db.tagDao()
    @Provides fun provideTagDataDao(db: Database): TagDataDao = db.tagDataDao()
    @Provides fun provideLocationDao(db: Database): LocationDao = db.locationDao()
    @Provides fun provideCaldavDao(db: Database): CaldavDao = db.caldavDao()
    @Provides fun provideTaskAttachmentDao(db: Database): TaskAttachmentDao = db.taskAttachmentDao()
    @Provides fun provideDeletionDao(db: Database): DeletionDao = db.deletionDao()
    @Provides fun provideCompletionDao(db: Database): CompletionDao = db.completionDao()
}
