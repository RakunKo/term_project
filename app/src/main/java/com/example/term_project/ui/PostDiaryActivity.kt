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
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

class PostDiaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostDiaryBinding
    private lateinit var date :String
    private var note = 1
    private val customToast = CustomToast
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        date = intent.getStringExtra("date")!!
        note = intent.getIntExtra("note", 1)
        Log.d("note", note.toString())

        binding.ppostDiaryDateTv.text = date
        binding.postDiaryEt.setText(intent.getStringExtra("content"))
        clickListener()
    }

    private fun clickListener() {
        binding.postDiaryBtn.setOnClickListener {
            val content = binding.postDiaryEt.text.toString()
            val currentDate = LocalDate.parse(date)
            val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            val spf = getSharedPreferences("userInfo", MODE_PRIVATE)

            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                val generativeModel = GenerativeModel(
                    // Use a model that's applicable for your use case (see "Implement basic use cases" below)
                    modelName = "gemini-pro",
                    // Access your API key as a Build Configuration variable (see "Set up your API key" above)
                    apiKey = "AIzaSyCLD79cp3QPElkLOqijLNEuVrSANufPjjw"
                )
                val prompt = content + "오늘 일기에 대한 해결책을 3줄로 친구가 조언하듯이, 위로하듯이 말해줘"
                val response = generativeModel.generateContent(prompt)
                Log.d("response", response.text.toString())

                withContext(Dispatchers.Main) {
                    // 여기에 UI 스레드에서 실행할 작업을 작성하세요
                    inputDiary1(Diary(content, spf.getString("uid", "")!!, formattedDate, note, response.text ))
                }
            }



        }

        binding.signupBackBtn.setOnClickListener {
            finish()
        }
    }

    private fun inputDiary1(diary: Diary?) {
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("diary").document(diary!!.uid + diary.note.toString() + diary.created_at)
        Log.d("exist", documentRef.toString())


        documentRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // 문서가 존재함
                val newData = hashMapOf(
                    "content" to diary.content,
                )
                Log.d("note1", diary!!.uid +  diary.note.toString() + diary.created_at)

                documentRef.set(newData, SetOptions.merge())
                    .addOnSuccessListener {
                        customToast.createToast(this, "일기 작성 완료", 300, true)
                        finish()
                    }
                    .addOnFailureListener {
                        customToast.createToast(this, "일기 작성 실패", 300, false)
                    }
            } else {
                Log.d("note2", diary!!.uid +  diary.note.toString() + diary.created_at)
                // 존재하지 않는 경우, 새로운 문서를 생성합니다.
                FirebaseFirestore.getInstance()
                    .collection("diary")
                    .document(diary.uid + diary.note.toString() + diary.created_at)
                    .set(diary)
                    .addOnSuccessListener {
                        customToast.createToast(this, "일기 작성 완료", 300, true)
                        finish()
                    }
                    .addOnFailureListener {
                        customToast.createToast(this, "일기 작성 실패", 300, false)
                    }
            }
        }.addOnFailureListener { exception ->
            // 문서를 가져오는 동안 오류 발생
        }
    }

}
