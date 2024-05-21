package com.example.term_project.ui

import MainViewModel
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.term_project.R
import com.example.term_project.data.entity.Diary
import com.example.term_project.data.entity.Note
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.kizitonwose.calendar.core.yearMonth
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

class CollectionFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var date : LocalDate
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {

            mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
            date = LocalDate.now()
            setContent {
                CollectionView()
            }
        }
    }
    @Preview
    @Composable
    fun CollectionView() {
        val documentsState = mainViewModel._documents.observeAsState()
        val noteListState = mainViewModel._noteList.observeAsState()
        Surface (modifier = Modifier.fillMaxSize()){
            Column (modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)){
                TopBar()
                Spacer(modifier = Modifier.height(20.dp))
                if (!documentsState.value.isNullOrEmpty() && !noteListState.value.isNullOrEmpty()) {
                    Log.d("noteState", noteListState.value.toString())
                    LazyColumn(Modifier.fillMaxSize()) {
                        items(documentsState.value!!) { diary ->
                            DiaryItem(diary = diary, noteListState.value!!)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun TopBar() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "모아보기",
                fontFamily = FontFamily(Font(R.font.npsfont_regula)),
                fontSize = 20.sp
            )
        }

    }

    @Composable
    fun DiaryItem(diary: Diary, note : List<Note>) {
        val stroke = Stroke(width = 5f, pathEffect = PathEffect.dashPathEffect(intervals = floatArrayOf(10f, 10f), phase = 10f))
        Column (
            Modifier
                .drawBehind {
                    drawRoundRect(
                        color = Color.LightGray,
                        style = stroke,
                        cornerRadius = CornerRadius(10.dp.toPx())
                    )
                }
                .fillMaxWidth()
                .clickable {
                    val intent = Intent(requireContext(), PostClickActivity::class.java)
                    intent.putExtra("date", diary.created_at)
                    intent.putExtra("content", diary.content.toString())
                    intent.putExtra("uid", diary.uid)
                    requireActivity().startActivity(intent)
                },
            verticalArrangement = Arrangement.Center){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(10.dp))
                    .padding(15.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = note.find { it.id == diary.note }!!.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.W700,
                            fontFamily = FontFamily(Font(R.font.npsfont_regula))
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = diary.created_at,
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(R.font.npsfont_regula)),
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = diary.content,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(bottom = 5.dp),
                            fontFamily = FontFamily(Font(R.font.npsfont_regula))
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }

}