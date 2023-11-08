package com.example.lib_base.db

import android.util.Log

class UserRepository(private val db: AppDatabase) {

    // 测试数据库

    /**
     * 模拟数据
     */


    /**
     * 测试外键的作用
     *
     * 作用如下
     * 将创建一个外键关系，确保在插入或更新 字表(book)中的数据时，userId 字段的值必须是 User 表中存在的主键(uid)值之一。
     *
     * android.database.sqlite.SQLiteConstraintException: FOREIGN KEY constraint failed (code 787 SQLITE_CONSTRAINT_FOREIGNKEY)
     */
    suspend fun insertNoForeign() {
        var book = Book("新华1000", 10 * 0.1)
        db.getBookDao().insert(book)
    }

    var j = 1L;
    suspend fun insert() {

        var user: User

        for (i in 0..3) {
            user = User()
            user.uid = j + i;
            user.age = 20 + i;
            user.city = "北京$i"
            user.name = "虾米$i"

            var id = db.getUserDao().insertUser(user)

            Log.i("豆", user.toString() + "rowid---" + id)

            ///data/data/io.dourl.mvidemo/databases

            var books: MutableList<Book> = mutableListOf()
            for (i in 0..3) {
                var book = Book("新华$i", 10 + i * 0.1)
                book.userId = user.uid
                books.add(book)
            }

            db.getBookDao().insertAll(books)
        }

        j++

    }

    suspend fun getUserBook() {
        val list: List<UserAndBook> = db.getUserDao().getUsersWithBooks()

        Log.i("豆", "1" + list.toString())


        val list2: List<UserAndBook> = db.getUserDao().getUsersWithBooks(2)
        Log.i("豆", "2" + list2.toString())


        val list3: Map<User, List<Book>> = db.getUserDao().getMapUsersWithBooks()
        Log.i("豆", "3" + list3.toString())
    }

    suspend fun deleteByBookId() {
        db.getBookDao().deleteBookWithId(5)
    }

    suspend fun deleteUserByUd() {
        db.getUserDao().deleteById(3);
    }
}