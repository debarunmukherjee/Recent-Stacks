package com.example.recentstacks.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.recentstacks.models.Item

@Database(entities = [Item::class], version = 1)
@TypeConverters(Converters::class)
abstract class QuestionDatabase : RoomDatabase() {

    abstract fun QuestionsDao() : QuestionsDao

    companion object{
        @Volatile
        private var INSTANCE: QuestionDatabase? = null

        fun getDatabase(context: Context): QuestionDatabase {
            if (INSTANCE == null) {
                synchronized(this){
                    INSTANCE = Room.databaseBuilder(context,
                        QuestionDatabase::class.java,
                        "question_db")
                        .build()
                }
            }
            return INSTANCE!!
        }
    }
}