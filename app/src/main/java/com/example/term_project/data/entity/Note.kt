package com.example.term_project.data.entity

import com.google.firebase.Timestamp
import java.time.LocalDate
import java.util.Date

data class Note(
    val id :Int = 0,
    val title : String = "",
<<<<<<< HEAD
    val created_at : String = "",
    val uid : String =""
=======
    val created_at : Date = Date(),
    val uid : String = ""
>>>>>>> b4ab83694ddbfc3dbbbcc25b44ebc52566e15274
)
