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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import com.example.term_project.data.entity.Diary
import com.google.firebase.firestore.FirebaseFirestore
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

class AnalyzeActivity: AppCompatActivity()  {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var uid : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        val spf = getSharedPreferences("userInfo", MODE_PRIVATE)
        uid = spf.getString("uid", "")!!
        mainViewModel.getAllDiary(uid)

        setContent {
            AnalyzeView(mainViewModel._documents.value ?: emptyList())
        }
    }

    @Composable
    fun AnalyzeView(progressData: List<Diary>) {
        val documentsState = mainViewModel._documents.observeAsState()
        Surface (modifier = Modifier
            .fillMaxSize()){
            TopBar()
        }
    }

    @Composable
    fun TopBar() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "분석",
                fontFamily = FontFamily(Font(R.font.npsfont_regula)),
                fontSize = 20.sp
            )
        }
    }

    @Composable
    fun AnalyzeChart(
        progressData: List<Diary>,
        maxValue: Int
    ) {
        val density = LocalDensity.current
        val chartWidth = 300.dp
        val chartHeight = 203.dp
        val chartWidthPx = with(density) { chartWidth.roundToPx().toFloat() }
        val chartHeightPx = with(density) { chartHeight.roundToPx().toFloat() }
        val pointRadius = 4.dp
        val pointRadiusPx = with(density) { pointRadius.roundToPx().toFloat() }
        val lineStrokeWidth = 2.dp
        val lineStrokeWidthPx = with(density) { lineStrokeWidth.roundToPx().toFloat() }
        val grayLineStrokeWidth = 1.dp
        val grayLineStrokeWidthPx = with(density) { grayLineStrokeWidth.roundToPx().toFloat() }

        Box(
            modifier = Modifier
                .width(chartWidth)
                .height(chartHeight)
                .background(Color.White)
        ) {
            val points = remember(progressData) {
                progressData.mapIndexed { index, diary ->
                    val x = index / (progressData.size - 1).toFloat() * chartWidthPx
                    val y = (1 - (progressData.size.toFloat() / maxValue)) * chartHeightPx
                    Offset(x, y)
                }
            }

            // Draw horizontal gray lines and Y-axis labels
            (0..maxValue step 2).forEachIndexed { index, value ->
                val y = (1 - value / maxValue.toFloat()) * chartHeightPx
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawLine(
                        color = if (index== 0) Color.Black else Color.Gray,
                        start = Offset(0f, y),
                        end = Offset(chartWidthPx, y),
                        strokeWidth = if (index == 0) grayLineStrokeWidthPx * 2 else grayLineStrokeWidthPx
                    )
                }
                Text(
                    text = value.toString(),
                    modifier = Modifier.offset(x = with(density) { -20.dp }, y = with(density) { y.toDp() - 6.dp }),
                    fontSize = 12.sp,
                    textAlign = TextAlign.End,  // Align text to the right
                    color = Color.Black
                )
            }

            // Draw vertical gray dotted lines and X-axis labels
            points.forEachIndexed { index, point ->
                if (index != 0) {  // Exclude the first vertical line (x-axis)
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawLine(
                            start = Offset(point.x, 0f),
                            end = Offset(point.x, chartHeightPx),
                            color = Color.Gray,
                            strokeWidth = grayLineStrokeWidthPx,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                        )
                    }
                }
                Text(
                    text = progressData[index].created_at,
                    modifier = Modifier.offset(x = with(density) { point.x.toDp() - 10.dp }, y = with(density) { chartHeightPx.toDp() + 10.dp }),
                    fontSize = 12.sp,
                    color = Color.Black
                )
            }

            // Draw line
            points.zipWithNext().forEach { (start, end) ->
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawLine(
                        color = Color.Black,
                        start = start,
                        end = end,
                        strokeWidth = lineStrokeWidthPx
                    )
                }
            }

            // Draw circle at the last point
            val lastPoint = points.last()
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color.Black,
                    center = lastPoint,
                    radius = pointRadiusPx
                )
            }
        }
    }
}
