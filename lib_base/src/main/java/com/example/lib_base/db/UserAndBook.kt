package com.example.lib_base.db

import androidx.room.Embedded
import androidx.room.Relation

data class UserAndBook(
    @Embedded val user: User,
    @Relation(
        parentColumn = "uid",
        entityColumn = "userId"
    )
    val books: List<Book>
)