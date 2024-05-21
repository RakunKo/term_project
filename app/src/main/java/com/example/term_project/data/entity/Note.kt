package com.example.term_project.data.entity

import com.google.firebase.Timestamp
import java.time.LocalDate
import java.util.Date

data class Note(
    var id :Int = 0,
    var title : String = "",
    var created_at : Date = Date(),
    var uid : String ="",
)
