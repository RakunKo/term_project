package com.example.term_project.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.term_project.R
import com.example.term_project.data.entity.Diary
import com.example.term_project.data.entity.UserInfo
import com.example.term_project.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.createDiary.setOnClickListener {
            val intent = Intent(this, PostDiaryActivity::class.java)
            startActivity(intent)
        }
    }
}