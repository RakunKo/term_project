package com.example.term_project.data.entity

import java.time.LocalDate
import java.util.Date

data class Diary(
    val content :String = "",
    val uid : String = "",
    val created_at : String = "",
    val note : Int = 1,
    val ai : String? = ""
)
