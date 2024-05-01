package com.example.term_project.data.model

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var uid = ""

    fun setUser(uid : String) {
        this.uid = uid
    }
}