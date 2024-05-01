package com.example.term_project.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.term_project.R
import com.example.term_project.data.entity.Diary
import com.example.term_project.data.entity.UserInfo
import com.example.term_project.data.model.MainViewModel
import com.example.term_project.databinding.ActivityMainBinding
import com.example.term_project.databinding.ActivityPostDiaryBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

class PostDiaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostDiaryBinding
    private lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDiaryBinding.inflate(layoutInflater)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        setContentView(binding.root)

        clickListener()
    }

    private fun clickListener() {
        binding.postDiaryBtn.setOnClickListener {
            val content = binding.postDiaryEt.text.toString()
            val currentDate = LocalDate.now()
            val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            inputDiary(Diary(content, mainViewModel.uid, formattedDate ))
        }

        binding.signupBackBtn.setOnClickListener {
            finish()
        }
    }
    private fun inputDiary(diary: Diary?) {
        diary?.let {
            FirebaseFirestore.getInstance()
                .collection("diary")
                .document()
                .set(it)
                .addOnSuccessListener {
                    Toast.makeText(this, "정보 삽입 완료", Toast.LENGTH_LONG).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "정보 삽입 실패", Toast.LENGTH_LONG).show()
                }
        }
    }
}
