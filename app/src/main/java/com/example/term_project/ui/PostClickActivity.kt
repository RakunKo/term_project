package com.example.term_project.ui

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModelProvider
import com.example.term_project.R
import com.example.term_project.data.entity.Diary
import com.example.term_project.data.entity.UserInfo
import com.example.term_project.databinding.ActivityDiaryBinding
import com.example.term_project.databinding.ActivityMainBinding
import com.example.term_project.databinding.ActivityPostDiaryBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

class PostClickActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDiaryBinding
    private lateinit var date :String
    private var content = ""
    private var uid = ""
    private val customToast = CustomToast
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        date = intent.getStringExtra("date")!!
        content = intent.getStringExtra("content")!!
        uid = intent.getStringExtra("uid")!!

        binding.diaryTv.text = content
        binding.diaryDate.setText(date)
        clickListener()
    }

    private fun clickListener() {
        binding.diaryDeleteBtn.setOnClickListener {
            FirebaseFirestore
                .getInstance()
                .collection("diary")
                .document(uid + date)
                .delete()
                .addOnSuccessListener {
                    Log.d("정보삭제", "성공")
                    customToast.createToast(this, "일기 삭제 완료", 300, true)
                    finish()
                }
                .addOnFailureListener {
                    customToast.createToast(this, "일기 삭제 실패", 300, false)
                    Log.d("정보", "실패")
                }
        }

        binding.diaryBackBtn.setOnClickListener {
            finish()
        }
    }
}
