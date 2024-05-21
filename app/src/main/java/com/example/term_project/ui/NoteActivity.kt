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
import com.google.firebase.firestore.FirebaseFirestore

class NoteActivity: AppCompatActivity()  {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var uid : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        val spf = getSharedPreferences("userInfo", MODE_PRIVATE)
        uid = spf.getString("uid", "")!!
        mainViewModel.getAllNote(uid)

        setContent {
            NoteListScreen()
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun NoteListScreen() {
        val bottomSheetState = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden
        )
        val noteListState = mainViewModel._noteList.observeAsState()
        var mode by remember { mutableStateOf(1) } // mode를 remember로 변경
        var clickNote by remember { mutableStateOf(Note(0,"", Date(), "")) } // mode를 remember로 변경
        var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }

        if(!bottomSheetState.isVisible) textFieldValue = TextFieldValue("")

        ModalBottomSheetLayout(
            sheetState = bottomSheetState,
            sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            sheetContent = {
                BottomSheet(bottomSheetState, mode, clickNote, textFieldValue) { newValue ->
                    textFieldValue = newValue
                } // mode를 BottomSheet에 전달
            },
            sheetBackgroundColor = colorResource(id = R.color.main_grey),
            content = {
                Surface(modifier = Modifier.fillMaxSize()) {
                    if (!noteListState.value.isNullOrEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(30.dp)
                        ) {
                            NoteTopBar(Modifier, bottomSheetState ) { newMode ->
                                mode = newMode
                            } // mode를 NoteTopBar에 전달
                            NoteList(noteList = noteListState.value!!, bottomSheetState,
                                onModeChange = {newMode ->
                                    mode = newMode
                                },
                                onClickNote = {note->
                                    clickNote = note
                                })
                        }
                    }
                }
            },
        )
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun BottomSheet(state: ModalBottomSheetState, mode: Int, note : Note, textFieldValue: TextFieldValue, onTextFieldValueChange: (TextFieldValue) -> Unit) { // mode 추가
        val coroutineScope = rememberCoroutineScope()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .height(170.dp)
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Image(
                painter = painterResource(id = R.drawable.btm_sheet_top),
                contentDescription = null,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            OutlinedTextField(
                value = textFieldValue,
                onValueChange = { newValue -> onTextFieldValueChange(newValue) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(if(mode == 1)"일기 제목"
                else note.title) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = colorResource(id = R.color.dark_gray),
                    unfocusedBorderColor = colorResource(id = R.color.main_grey)
                )
            )
            Row(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .padding(end = 4.dp)
                        .height(35.dp)
                        .weight(1f)
                        .background(
                            color = colorResource(id = R.color.main_grey),
                            shape = RoundedCornerShape(10.dp),
                        )
                        .border(
                            1.dp,
                            colorResource(id = R.color.main_stroke),
                            RoundedCornerShape(10.dp)
                        )
                        .clickable(onClick = {
                            val note = Note(
                                uid = uid,
                                id = mainViewModel._noteList.value!!.size + 1,
                                created_at = Date(),
                                title = textFieldValue.text
                            )
                            note.let {
                                FirebaseFirestore
                                    .getInstance()
                                    .collection("note")
                                    .document(note.uid + note.id)
                                    .set(it)
                                    .addOnSuccessListener {
                                        Log.d("정보", "성공")
                                        mainViewModel.getAllNote(uid)
                                        coroutineScope.launch {
                                            state.hide()
                                            onTextFieldValueChange(TextFieldValue(""))
                                        }
                                    }
                                    .addOnFailureListener {
                                        Log.d("정보", "실패")
                                    }
                            }
                        }),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "확인",
                        fontFamily = FontFamily(Font(R.font.npsfont_regula)),
                        fontSize = 15.sp)
                }
                Box(
                    Modifier
                        .padding(start = 4.dp)
                        .height(35.dp)
                        .weight(1f)
                        .background(
                            color = colorResource(id = R.color.grapefruit),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable(onClick = {
                            if(mode == 1) {
                                coroutineScope.launch {
                                    state.hide()
                                    onTextFieldValueChange(TextFieldValue(""))
                                }
                            }else {     //삭제하는 코드

                            }
                        }),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = if(mode ==1 )"취소"
                    else "삭제",
                        fontFamily = FontFamily(Font(R.font.npsfont_regula)),
                        fontSize = 15.sp,
                        color = colorResource(id = R.color.white))
                }
            }
        }
    }
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun NoteTopBar(modifier: Modifier, state : ModalBottomSheetState, onModeChange: (Int) -> Unit) { // mode 추가
        val coroutineScope = rememberCoroutineScope()
        Row(modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Image(
                painter = painterResource(id = R.drawable.arrow_back_ios),
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .clickable(onClick = {
                        finish()
                    })
            )
            Image(
                painter = painterResource(id = R.drawable.ic_plus),
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .clickable(onClick = {
                        coroutineScope.launch {
                            onModeChange(1)
                            state.show()
                        }
                    })
            )
        }
        Spacer(modifier = modifier.height(30.dp))
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun NoteList(noteList : List<Note>, state : ModalBottomSheetState, onModeChange: (Int) -> Unit, onClickNote:(Note) -> Unit) { // mode 추가
        LazyColumn (modifier = Modifier.fillMaxWidth()){
            items(noteList) { noteEntity ->
                NoteItem(noteEntity, state, onModeChange, onClickNote) // mode를 NoteItem에 전달
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun NoteItem(note:Note, state : ModalBottomSheetState, onModeChange: (Int) -> Unit, onClickNote:(Note) -> Unit) { // mode 추가
        val coroutineScope = rememberCoroutineScope()
        Row(Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween){
            Text(text = note.title,
                fontFamily = FontFamily(Font(R.font.npsfont_regula)),
                fontSize = 15.sp
            )
            Image(painter = painterResource(id = R.drawable.arrow_forward_ios),
                contentDescription = null,
                Modifier.size(15.dp)
                    .clickable (onClick = {
                        coroutineScope.launch {
                            onModeChange(2)
                            onClickNote(note)
                            state.show()
                        }
                    }))
        }
        Image(painter = painterResource(id = R.drawable.dotted_line),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 10.dp, bottom = 20.dp)
                .fillMaxWidth())
    }
}
