package com.example.term_project.ui

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.term_project.R
import com.example.term_project.data.entity.Diary
import com.example.term_project.data.entity.UserInfo
import com.example.term_project.databinding.ActivityMainBinding
import com.example.term_project.databinding.ActivityPostDiaryBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

class PostDiaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostDiaryBinding
    private lateinit var date :String
    private var note = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        date = intent.getStringExtra("date")!!
        note = intent.getIntExtra("note", 1)

        binding.ppostDiaryDateTv.text = date
        clickListener()
    }

    private fun clickListener() {
        binding.postDiaryBtn.setOnClickListener {
            val content = binding.postDiaryEt.text.toString()
            val currentDate = LocalDate.parse(date)
            val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            val spf = getSharedPreferences("userInfo", MODE_PRIVATE)


            inputDiary1(Diary(content, spf.getString("uid", "")!!, formattedDate, note ))
        }

        binding.signupBackBtn.setOnClickListener {
            finish()
        }
    }

    private fun inputDiary1(diary: Diary?) {
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("diary").document(diary!!.uid + diary.created_at)

        documentRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                // 문서가 존재함
                val newData = hashMapOf(
                    "content" to diary.content,
                )

                documentRef.set(newData, SetOptions.merge())
                    .addOnSuccessListener {
                        Toast.makeText(this, "정보 갱신 완료", Toast.LENGTH_LONG).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "정보 갱신 실패", Toast.LENGTH_LONG).show()
                    }
            } else {
                // 존재하지 않는 경우, 새로운 문서를 생성합니다.
                FirebaseFirestore.getInstance()
                    .collection("diary")
                    .document(diary.uid + diary.created_at)
                    .set(diary)
                    .addOnSuccessListener {
                        Toast.makeText(this, "정보 삽입 완료", Toast.LENGTH_LONG).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "정보 삽입 실패", Toast.LENGTH_LONG).show()
                    }
            }
        }.addOnFailureListener { exception ->
            // 문서를 가져오는 동안 오류 발생
        }
    }

}
