package com.example.term_project.data.entity

import com.google.firebase.Timestamp
import java.time.LocalDate
import java.util.Date

data class Note(
    val id :Int = 0,
    val title : String = "",
    val created_at : Date = Date(),
    val uid : String = ""
)
