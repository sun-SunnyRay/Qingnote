package com.qingguang.qingnote.hilt

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.qingguang.qingnote.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRoomDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase {
        return buildDatabase(context)
    }

    private const val DATABASE_NAME = "ssndb"

    private fun buildDatabase(context: Context) =
        Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DATABASE_NAME)
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .fallbackToDestructiveMigration()
            .addCallback(CALLBACK)
            .build()

    private val CALLBACK = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
//                db.execSQL(
//                    "CREATE TRIGGER IF NOT EXISTS  Trigger_AfterDelete_Note AFTER DELETE on Note FOR EACH ROW \n" +
//                            "BEGIN  \n" +
//                            "DELETE FROM NoteTagCrossRef WHERE note_id=OLD.note_id;\n" +
//                            "END"
//                )
            db.execSQL(
                "CREATE TRIGGER IF NOT EXISTS  Trigger_AfterDelete_Note_Tag AFTER DELETE on NoteTagCrossRef WHEN (SELECT count(*) FROM NoteTagCrossRef WHERE tag=OLD.tag)=0 \n" + "BEGIN  \n" + "DELETE FROM Tag WHERE tag=OLD.tag;\n" + "END"
            )

            db.execSQL(
                "CREATE TRIGGER IF NOT EXISTS  Trigger_AfterDelete_Tag_Note AFTER DELETE on NoteTagCrossRef WHEN (SELECT count(*) FROM NoteTagCrossRef WHERE note_id=OLD.note_id)=0 \n" + "BEGIN  \n" + "DELETE FROM Note WHERE note_id=OLD.note_id;\n" + "END"
            )
        }
    }
}
