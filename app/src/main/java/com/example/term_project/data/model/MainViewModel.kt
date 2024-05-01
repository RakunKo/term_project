package com.example.term_project.data.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var _uid = MutableLiveData<String>("")

    fun setUser(uid : String) {
        _uid.value = uid
    }
}