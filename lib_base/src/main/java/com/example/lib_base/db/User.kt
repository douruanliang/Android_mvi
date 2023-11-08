package com.example.lib_base.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


//定义外键：foreignKeys，关联的主键是User中的bookId字段，
@Entity(
    tableName = "db_user"
)
//在多表关联时存在多个表用一个主键关联，存在当一个外键的表数据变化时引起主键表数据也要刷新一次，引起性能问题。
//因此需要在表上建立一些索引例如：indices = [Index("uid"), Index("bookId")，将不同表的索引加入后，一张表的数据变化不会引起其他表的数据刷新
class User {
    @PrimaryKey(autoGenerate = true)
    var uid :Long = 0
    @ColumnInfo(name = "uname")
    var name: String? = null
    var city: String? = null
    var age = 0;
    override fun toString(): String {
        return "User(uid=$uid, name=$name, city=$city, age=$age)"
    }


}