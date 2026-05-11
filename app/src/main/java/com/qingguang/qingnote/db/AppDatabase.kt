package com.qingguang.qingnote.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.qingguang.qingnote.bean.*
import com.qingguang.qingnote.db.dao.NoteDao
import com.qingguang.qingnote.db.dao.NoteTagCrossRefDao
import com.qingguang.qingnote.db.dao.TagDao
import com.qingguang.qingnote.db.dao.TagNoteDao

@Database(
    entities = [
        Note::class,
        Tag::class,
        NoteTagCrossRef::class,
        Comment::class,
        Reminder::class,
    ], version = 2, exportSchema = false
)
@TypeConverters(DatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {
    //创建DAO的抽象类
    abstract fun getNoteDao(): NoteDao
    abstract fun getTagDao(): TagDao
    abstract fun getTagNote(): TagNoteDao
    abstract fun getNoteTagCrossRefDao(): NoteTagCrossRefDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE Note ADD COLUMN parent_note_id INTEGER DEFAULT NULL")
            }
        }
    }
}
