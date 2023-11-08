package com.example.lib_base.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface  BookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg books: Book?) //支持可变参数

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(books: List<Book>?) //支持可变参数

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(book:Book)

    @Query("delete from db_book where bid = :id")
    fun deleteBookWithId(id:Long?)
}