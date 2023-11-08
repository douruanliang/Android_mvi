package com.example.lib_base.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lib_base.db.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    fun insert() {
        viewModelScope.launch {
            repository.insert()
        }
    }

    fun insertNoForeign() {
        viewModelScope.launch {
            repository.insertNoForeign()
        }
    }


    fun getBooksByUser() {
        viewModelScope.launch {
            val list = repository.getUserBook()
            Log.i("豆", list.toString())
        }
    }

    fun deleteBookWithId(){
        viewModelScope.launch {
            repository.deleteByBookId()
        }
    }

    fun deleteUserWithId(){
        viewModelScope.launch {
            repository.deleteUserByUd()
        }
    }


}