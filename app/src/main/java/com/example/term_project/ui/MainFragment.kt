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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
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
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

class MainFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

            setContent {
                MainFragmentContent()
            }
        }
    }
    @Preview
    @Composable
    fun MainFragmentContent() {
        val currentMonth = remember { mutableStateOf<YearMonth>(YearMonth.now()) }
        val startMonth = remember { currentMonth.value.minusMonths(100) } // Adjust as needed
        val endMonth = remember { currentMonth.value.plusMonths(100) } // Adjust as needed
        val daysOfWeek = remember { daysOfWeek() }
        val onDay = remember { mutableStateOf<LocalDate>(LocalDate.now()) }
        val coroutineScope = rememberCoroutineScope()

        val documentsState = mainViewModel._documents.observeAsState()
        val noteState = mainViewModel._note.observeAsState()
        val noteListState = mainViewModel._noteList.observeAsState()




        val state = rememberCalendarState(
            startMonth = startMonth,
            endMonth = endMonth,
            firstVisibleMonth = currentMonth.value,
            firstDayOfWeek = daysOfWeek.first()
        )

        Surface (modifier = Modifier.fillMaxSize()){
            Column (modifier = Modifier.padding(30.dp)){
                if (noteState.value != null && noteListState.value != null) {
                    MainTopBar(noteState.value!!,
                        noteListState.value!!,
                        openMenu = {

                        },
                        goBack = {
                            coroutineScope.launch {
                                state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.previousMonth)
                            }
                        },
                        goForward = {
                            coroutineScope.launch {
                                state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.nextMonth)
                            }
                        })
                }
                MainDateBar(state.firstVisibleMonth.yearMonth)
                DaysOfWeekTitle(daysOfWeek = daysOfWeek)
                HorizontalCalendar(
                    state = state,
                    dayContent = { Day(it,onDay.value,
                        onClick = { clickedDay ->
                            Log.d("MyComposeContent", "Clicked day: ${clickedDay.date}")
                            onDay.value = clickedDay.date // 클릭한 날짜를 상태에 업데이트
                        }) },
                )

                if (noteState.value != null) {
                    DiaryTextField(
                        note = noteState.value!!,
                        clickedDate = onDay.value,
                        goWriteDairy = {
                            val intent = Intent(requireContext(), PostDiaryActivity::class.java)
                            intent.putExtra("date", onDay.value.toString())
                            intent.putExtra("note", mainViewModel._note.value!!.id)
                            startActivity(intent)
                        },
                        document = documentsState.value ?: emptyList()
                    )
                }
            }
        }

    }
    @Composable
    fun Day(day: CalendarDay, selectedDay: LocalDate?,onClick: (CalendarDay) -> Unit) {

        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .clickable(
                    enabled = day.position == DayPosition.MonthDate,
                    onClick = {
                        onClick(day)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (day.date == selectedDay) {
                // 동그라미를 그립니다.
                Image(painter = painterResource(id = R.drawable.calender_circle), contentDescription = null)
            }
            Text(
                text = day.date.dayOfMonth.toString(),
                fontFamily = FontFamily(Font(R.font.npsfont_regula)),
                color = if (day.position == DayPosition.MonthDate) Color.Black else Color.Gray,
                fontSize = 15.sp
            )

        }
    }



    @Composable
    fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
        Row(modifier = Modifier.fillMaxWidth()) {
            for (dayOfWeek in daysOfWeek) {
                Text(
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    fontFamily = FontFamily(Font(R.font.npsfont_regula)),
                    fontSize = 15.sp
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    @Composable
    fun DiaryTextField(note : Note, clickedDate : LocalDate?, goWriteDairy:()->Unit, document : List<Diary>) {
        Column (modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .clickable {
                goWriteDairy()
            }){
            Image(painter = painterResource(id = R.drawable.dotted_line), contentDescription = null)
            Spacer(modifier = Modifier.height(20.dp))
            if (document.isNotEmpty()) {
                val filteredDiary = document.filter { it.created_at == clickedDate.toString()&&
                                                        it.note == note.id}
                Log.d("filter", filteredDiary.toString())
                if (filteredDiary.isNotEmpty()) {
                    Text(
                        text = filteredDiary[0].content, // 여기서 content는 Diary 객체의 내용을 나타내는 필드입니다.
                        maxLines = 20,
                        fontFamily = FontFamily(Font(R.font.npsfont_regula)),
                        fontSize = 15.sp
                    )
                }else {
                    Text(
                        text = "일기를 작성해주세요", // 여기서 content는 Diary 객체의 내용을 나타내는 필드입니다.
                        maxLines = 20,
                        fontFamily = FontFamily(Font(R.font.npsfont_regula)),
                        fontSize = 15.sp
                    )
                }
            }
        }
    }

    @Composable
    fun MainTopBar(note : Note?, noteList: List<Note>?, openMenu:() ->Unit, goBack: () -> Unit, goForward:()->Unit) {
        var isMenuExpanded by remember { mutableStateOf(false) }

        Column {
            Row (modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .size(20.dp),
                verticalAlignment = Alignment.CenterVertically){
                Image(painter = painterResource(id = R.drawable.ic_diary_detail),
                    contentDescription =null,
                    modifier = Modifier.clickable {
                        isMenuExpanded = true
                        openMenu() })
                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false },
                    modifier = Modifier.wrapContentSize()
                ) {
                    noteList?.forEach { noteItem ->
                        val isSelected = noteItem.id == note!!.id
                        DropdownMenuItem(
                            onClick = {
                                mainViewModel._note.value = noteItem
                            },
                            modifier = Modifier.background(if (isSelected) colorResource(id = R.color.light_gray) else Color.White),
                        ) {
                            Text(
                                text = noteItem.title,
                                fontFamily = FontFamily(Font(R.font.npsfont_regula)),
                                fontSize = 15.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = note!!.title,
                    fontFamily = FontFamily(Font(R.font.npsfont_regula)),
                    fontSize = 15.sp
                )
                Row (modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End){
                    Image(painter = painterResource(id = R.drawable.arrow_back_ios),
                        contentDescription = null,
                        modifier = Modifier.clickable { goBack() })
                    Spacer(modifier = Modifier.width(20.dp))
                    Image(painter = painterResource(id = R.drawable.arrow_forward_ios),
                        contentDescription = null,
                        modifier = Modifier.clickable { goForward() })
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
    @Composable
    fun MainDateBar(yearmonth : YearMonth) {
        Row (modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically){
            Image(painter = painterResource(id = R.drawable.dotted_line),
                contentDescription = null,
                modifier = Modifier.weight(0.6f))
            Text(text = yearmonth.toString().replace('-', '.'),
                fontFamily = FontFamily(Font(R.font.npsfont_regula)),
                fontSize = 15.sp,
            )
            Image(painter = painterResource(id = R.drawable.dotted_line),
                contentDescription = null,
                modifier = Modifier.weight(0.2f))
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}