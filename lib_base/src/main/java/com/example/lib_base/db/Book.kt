package com.example.lib_base.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


/**
 * 外键是键在 从表上的!!
 */
@Entity(tableName = "db_book",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["uid"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )]
   // indices = [Index(value = ["bid"], unique = true),Index(value = ["userId"])]
)
data class Book(
    val name: String,
    val price: Double
) {
    @PrimaryKey(autoGenerate = true)
    var bid: Long = 0
    @ColumnInfo(name="userId", index = true)
    var userId:Long = 0;
}