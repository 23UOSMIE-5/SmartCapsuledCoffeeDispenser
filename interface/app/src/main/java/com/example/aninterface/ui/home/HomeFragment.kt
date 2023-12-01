package com.example.aninterface.ui.home

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.aninterface.R
import com.example.aninterface.data.DataSource
import com.example.aninterface.databinding.FragmentHomeBinding
import com.example.aninterface.model.UserStatics
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        // 막대 그래프 그리기
        initBarChart(binding.barChart)
        setData(binding.barChart)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // recycle view의 item으로 사용할 dataset 불러오기
        var UserDataset: UserStatics = UserStatics(mapOf()) // 빈 맵으로 초기화

        CoroutineScope(Dispatchers.Main).launch {
            val userStatics = DataSource().loadUserInfo("mylandy2") ?: UserStatics(mapOf()) // 널일 경우 대체 값
            UserDataset = userStatics

            val capsule: TextView = view.findViewById(R.id.capsule)
            val caffeine: TextView = view.findViewById(R.id.caffeine_n)
            val calorie: TextView = view.findViewById(R.id.calorie_n)

            val currentDate = LocalDate.now()
            val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            capsule.text = (UserDataset.userID[formattedDate]?.capsule ?: "0") + " 개"
            caffeine.text = (UserDataset.userID[formattedDate]?.caffeine ?: "0") + " mg"
            calorie.text = (UserDataset.userID[formattedDate]?.calorie ?: "0") + " kcal"

            // 로그 찍기
            Log.d("DataCheck", "Capsule Text: ${capsule.text}")
            Log.d("DataCheck", "Caffeine Text: ${caffeine.text}")
            Log.d("DataCheck", "Calorie Text: ${calorie.text}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initBarChart(barChart: BarChart) {

        val description = Description()
        description.text = "" // 라벨을 빈 문자열로 설정
        barChart.description = description

        // X 축 설정
        val xAxis: XAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.BLACK
        xAxis.textSize = 20f // X 축 텍스트 크기 설정
        xAxis.typeface = Typeface.DEFAULT_BOLD
        xAxis.granularity = 1f // 바 간의 간격 설정
        xAxis.yOffset = -5f // 조절 가능한 여백 크기

        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)

        // Y 축 설정 (왼쪽 축만 사용)
        val leftAxis: YAxis = barChart.axisLeft
        leftAxis.setDrawAxisLine(false)
        leftAxis.textColor = Color.BLUE
        leftAxis.setDrawAxisLine(false)
        leftAxis.setDrawLabels(false)
        leftAxis.setDrawZeroLine(true)
        leftAxis.zeroLineWidth = 2f // 0부분의 gridline 두께 설정
        leftAxis.zeroLineColor = ContextCompat.getColor(
            requireContext(),
            R.color.green
        )// 0부분의 gridline 색상 설정// 0부분의 gridline 활성화
        leftAxis.gridColor = Color.TRANSPARENT // 나머지 gridline은 투명으로 설정

        // 우측 Y 축 숨기기
        val rightAxis: YAxis = barChart.axisRight
        rightAxis.setDrawAxisLine(false)
        rightAxis.setDrawLabels(false) // Y 축 레이블 숨기기
        rightAxis.setDrawGridLines(false)

        // 차트 타이틀 설정
        val legend: Legend = barChart.legend
        legend.form = Legend.LegendForm.DEFAULT
        legend.textSize = 16f
        legend.typeface = Typeface.DEFAULT_BOLD
        legend.textColor = Color.BLACK
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.setDrawInside(true)

        barChart.setExtraOffsets(40f, 0f, 40f, 0f)
    }

    private fun setData(barChart: BarChart) {
        // Zoom In / Out 가능 여부 설정
        barChart.setScaleEnabled(false)

        var usingID = "mylandy2" // TODO: usingID를 실제 값으로 변경

        var UserDataset: UserStatics = UserStatics(mapOf()) // 빈 맵으로 초기화

        CoroutineScope(Dispatchers.Main).launch {
            val userStatics = DataSource().loadUserInfo(usingID) ?: UserStatics(mapOf()) // 널일 경우 대체 값
            UserDataset = userStatics

            val (avgCapsule, avgCaffeine, avgCalorie) = calculateAverageForLastWeek(UserDataset)

            val currentDate = LocalDate.now()
            val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val todayValues = floatArrayOf(
                UserDataset.userID[formattedDate]?.capsule?.toFloat() ?: 0f,
                UserDataset.userID[formattedDate]?.caffeine?.toFloat() ?: 0f,
                UserDataset.userID[formattedDate]?.calorie?.toFloat() ?: 0f
            ) // 오늘의 커피 캡슐 개수, 카페인 섭취량, 칼로리 섭취량

            val weeklyAverageValues = floatArrayOf(
                avgCapsule,
                avgCaffeine,
                avgCalorie
            ) // 일주일 평균의 커피 캡슐 개수, 카페인 섭취량, 칼로리 섭취량

            val barWidth = 0.35f // 바 너비

            // 오늘 데이터
            val todayEntries = ArrayList<BarEntry>()
            for (i in todayValues.indices) {
                todayEntries.add(BarEntry(i.toFloat() - barWidth / 2, todayValues[i]))
            }

            // 일주일 평균 데이터
            val weeklyAverageEntries = ArrayList<BarEntry>()
            for (i in weeklyAverageValues.indices) {
                weeklyAverageEntries.add(BarEntry(i.toFloat() + barWidth / 2, weeklyAverageValues[i]))
            }

            // 데이터셋 생성
            val todayDataSet = BarDataSet(todayEntries, "오늘")
            todayDataSet.color = Color.rgb(138, 219, 83)
            todayDataSet.valueTextSize = 18f
            todayDataSet.valueTypeface = Typeface.DEFAULT_BOLD

            val weeklyAverageDataSet = BarDataSet(weeklyAverageEntries, "일주일 평균")
            weeklyAverageDataSet.color = Color.rgb(93, 194, 119)
            weeklyAverageDataSet.valueTextSize = 18f
            weeklyAverageDataSet.valueTypeface = Typeface.DEFAULT_BOLD

            // 데이터셋 리스트에 추가
            val dataSetList = listOf(todayDataSet, weeklyAverageDataSet)

            // BarData에 데이터셋 리스트 설정
            val data = BarData(dataSetList)
            data.barWidth = barWidth // 바 너비 설정
            barChart.data = data

            val xAxis = barChart.xAxis
            xAxis.valueFormatter = MyXAxisValueFormatter(listOf("커피 캡슐", "카페인", "칼로리"))

            barChart.groupBars(-0.5f, 0.1f, 0.1f) // 그룹 바 설정
            barChart.invalidate()
        }


    }

    class MyXAxisValueFormatter(private val labels: List<String>) : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val index = value.toInt()
            return if (index >= 0 && index < labels.size) {
                labels[index]
            } else {
                "" // 범위를 벗어나면 빈 문자열 반환
            }
        }
    }

    // 평균 구하는 함수
    private fun calculateAverageForLastWeek(userDataset: UserStatics): Triple<Float, Float, Float> {
        var sumCapsule = 0f
        var sumCaffeine = 0f
        var sumCalorie = 0f

        val endDate = LocalDate.now()
        val startDate = endDate.minusDays(6) // 일주일 전 날짜

        val valuesInRange = userDataset.userID.filterKeys { date ->
            val dateAsLocalDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            !dateAsLocalDate.isBefore(startDate) && !dateAsLocalDate.isAfter(endDate)
        }.values

        valuesInRange.forEach { dailyStatics ->
            sumCapsule += dailyStatics.capsule.toFloat()
            sumCaffeine += dailyStatics.caffeine.toFloat()
            sumCalorie += dailyStatics.calorie.toFloat()
        }

        Log.d("CalculateAverage", "Sum of Capsules: $sumCapsule")
        Log.d("CalculateAverage", "Sum of Caffeine: $sumCaffeine")
        Log.d("CalculateAverage", "Sum of Calories: $sumCalorie")

        val numOfDays = valuesInRange.size
        val avgCapsule = if (numOfDays > 0) sumCapsule / numOfDays else 0f
        val avgCaffeine = if (numOfDays > 0) sumCaffeine / numOfDays else 0f
        val avgCalorie = if (numOfDays > 0) sumCalorie / numOfDays else 0f

        return Triple(avgCapsule, avgCaffeine, avgCalorie)
    }


}

