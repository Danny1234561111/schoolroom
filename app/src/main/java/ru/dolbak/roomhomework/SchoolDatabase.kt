package ru.dolbak.roomhomework

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        Student::class,
        Subject::class,
        StudentSubjectCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SchoolDatabase : RoomDatabase() {

    abstract val schoolDao: SchoolDao

    companion object {
        @Volatile
        private var INSTANCE: SchoolDatabase? = null

        fun getInstance(context: Context): SchoolDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SchoolDatabase::class.java,
                        "school_database"
                    )
                        .fallbackToDestructiveMigration()  //для прототипа
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}