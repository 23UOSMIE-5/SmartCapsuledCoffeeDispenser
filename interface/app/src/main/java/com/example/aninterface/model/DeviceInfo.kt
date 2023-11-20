package com.example.aninterface.model

// 기기 데이터 저장 방식
data class DeviceInfo(
    val deviceName: String,
    val usingId: String,
    val lineCount: String,
    val coffeeData: Map<String, CoffeeData>,
)

// 기기 데이터 중 커피 데이터 항목
data class CoffeeData(
    val coffeeName: String,
    val coffeeStock: String
)
//////////////////////////////////////////////////////////////////////

// 커피 데이터셋
data class CoffeeDatabase(
    val coffeeIndex: Map<String, CoffeeInfo>,
)

// 커피 데이터셋의 내용
data class CoffeeInfo(
    val coffeeName: String,
    val caffeine: String,
    val calories: String
)
///////////////////////////////////////////////////////////////////////

// 사용자 섭취 현황

data class UserStatics(
    val userID: Map<String, DailyStatics>
)

// 하루 섭취량
data class DailyStatics(
    val date: String,
    val capsule: String,
    val caffeine: String,
    val calorie: String
)