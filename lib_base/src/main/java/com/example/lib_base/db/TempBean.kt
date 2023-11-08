package com.example.lib_base.db

import androidx.room.DatabaseView


@DatabaseView("select uname, name from db_user,db_book where uid= 10",
    viewName = "tempBean")
class TempBean {
    var uname =""
    var name=""
    override fun toString(): String {
        return "TempBean(nname='$uname', name='$name')"
    }


}