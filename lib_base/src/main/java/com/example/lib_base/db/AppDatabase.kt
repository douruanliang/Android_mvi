package com.example.lib_base.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [User::class, Book::class], version = 1, exportSchema = false,
    views = [TempBean::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun getUserDao(): UserDao
    abstract fun getBookDao(): BookDao

    companion object {
        const val DB_NAME = "user.db"
        @Volatile
        private var instance: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context?): AppDatabase {
            instance?.let { return it }

            return Room.databaseBuilder(
                context!!,
                AppDatabase::class.java,
                DB_NAME
            ).allowMainThreadQueries() // //默认room不允许在主线程操作数据库，这里设置允许
                .build().apply { instance = this }

        }

        private val LOCK = Any()
        operator fun invoke(context: Context) = Companion.instance ?: synchronized(LOCK) {
                   instance?: createDatabase(context).also {
                       instance = it
                   }
        }
        private fun createDatabase(context: Context) = Room.databaseBuilder(
            context!!,
            AppDatabase::class.java,
            DB_NAME
        ).allowMainThreadQueries() // //默认room不允许在主线程操作数据库，这里设置允许
            .build()
    }

}