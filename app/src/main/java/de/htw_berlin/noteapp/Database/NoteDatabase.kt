package de.htw_berlin.noteapp.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.htw_berlin.noteapp.Converter.ImageConverter
import de.htw_berlin.noteapp.Dao.NoteDao
import de.htw_berlin.noteapp.Model.Notes


@Database(entities = [Notes::class], version = 1, exportSchema = false)
@TypeConverters(ImageConverter::class)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun myNoteDao(): NoteDao

    companion object {
        @Volatile
        var INSTANCE:NoteDatabase?=null

        fun getDatabaseInstance(context: Context):NoteDatabase{

            val tempInstance = INSTANCE
            if (tempInstance != null)
            {
                return tempInstance
            }

            synchronized(this)
            {
                val roomDatabaseInstance = Room.databaseBuilder(context, NoteDatabase::class.java,"Notes").allowMainThreadQueries().build()
                INSTANCE = roomDatabaseInstance
                return roomDatabaseInstance
            }
        }
    }

}