package com.example.lib_base.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface UserDao {
    //查询所有数据，若返回liveData则为 LiveData<List<User>>
    @Query(value = "select * from db_user")
    fun getAll(): List<User?>?

    @Query("SELECT * FROM db_user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray?): List<User?>? //根据uid查询

    @Query(
        "SELECT * FROM db_user WHERE uname LIKE :name AND "
                + "age LIKE :age LIMIT 1"
    )
    fun findByName(name: String?, age: Int): User?

    @Query("select * from db_user where uid like :id")
    fun getUserById(id: Int): User?

    @Insert
    fun insertAll(vararg users: User?) //支持可变参数

    @Insert
    fun insertUser(user: User?):Long

    @Delete
    fun delete(user: User?) //删除指定的user

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(user: User?) //更新，若出现冲突，则使用替换策略，还有其他策略可选择

  /*  @Query("select * from tempBean")
    fun queryUserBook(): List<TempBean>*/

    @Transaction
    @Query("select * from DB_USER")
    fun getUsersWithBooks(): List<UserAndBook>


    @Transaction
    @Query("select * from DB_USER where uid = :id")
    fun getUsersWithBooks(id:Long): List<UserAndBook>

    @Query("select * from db_user join db_book on db_user.uid = db_book.userId")
    fun getMapUsersWithBooks():Map<User,List<Book>>



    @Query("delete from db_user where uid =:id")
    fun deleteById(id: Long) //删除指定的user

}