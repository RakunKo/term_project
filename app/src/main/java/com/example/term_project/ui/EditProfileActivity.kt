package com.example.term_project.ui

import MainViewModel
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.term_project.R
import com.example.term_project.data.entity.Note
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.term_project.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import java.util.Date
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.example.term_project.data.entity.Diary
import com.example.term_project.data.entity.UserInfo
import com.example.term_project.databinding.ActivityProfileEditBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.text.TextComponent
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import com.patrykandpatrick.vico.views.chart.column.columnChart
import com.patrykandpatrick.vico.views.chart.line.lineChart

class EditProfileActivity: AppCompatActivity()  {
    private lateinit var uid : String
    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding : ActivityProfileEditBinding
    private val customToast = CustomToast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val spf = getSharedPreferences("userInfo", MODE_PRIVATE)
        uid = spf.getString("uid", "")!!

        getUser(uid)

        binding.profileEditBackBtn.setOnClickListener { finish() }
        binding.profileEditBtn.setOnClickListener {
            inputData(binding.profileEditNameEt.text.toString(), binding.profileEditInfoEt.text.toString())
        }
    }

    private fun getUser(uid: String) {
        db.collection("clients")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { documents ->
                val diaryList = mutableListOf<UserInfo>()
                for (document in documents) {
                    val diary = document.toObject(UserInfo::class.java)
                    diaryList.add(diary)
                }
                Log.d("MainViewModel", diaryList.toString())
                binding.profileEditNameEt.setText(diaryList[0].name.toString())
                binding.profileEditInfoEt.setText(diaryList[0].info.toString())
            }
            .addOnFailureListener { exception ->
                println("쿼리 실패: $exception")
            }
    }

    private fun inputData(name : String, info : String) {
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("clients").document(uid)

        documentRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                // 문서가 존재함
                val newData = hashMapOf(
                    "name" to name,
                    "info" to info
                )

                documentRef.set(newData, SetOptions.merge())
                    .addOnSuccessListener {
                        customToast.createToast(this, "정보 수정 완료", 300, true)
                        finish()
                    }
                    .addOnFailureListener {
                        customToast.createToast(this, "정보 수정 수정", 300, false)
                    }
            }
        }.addOnFailureListener { exception ->
            // 문서를 가져오는 동안 오류 발생
        }
    }
}
