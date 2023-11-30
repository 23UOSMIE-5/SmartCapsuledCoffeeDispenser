package com.example.aninterface.data

import com.example.aninterface.model.*


// 기기 데이터셋 1
val device1Info = DeviceInfo(
    deviceName = "집",
    usingId = "happy",
    lineCount = "2",
    coffeeData = mapOf(
        "1" to CoffeeData("아메리카노", "4"),
        "2" to CoffeeData("카페라떼", "7")
    )
)

// 기기 데이터셋 2
val device2Info = DeviceInfo(
    deviceName = "회사",
    usingId = "happy",
    lineCount = "3",
    coffeeData = mapOf(
        "1" to CoffeeData("카푸치노", "12"),
        "2" to CoffeeData("핫초코", "2"),
        "3" to CoffeeData("아메리카노", "5")
    )
)

// 기기 데이터셋 1, 2 통합
class DataSource {
    fun loadstock(): List<DeviceInfo> {
        return listOf(
            device1Info,
            device2Info
        )
    }
    fun loadCoffeeInfo(): CoffeeDatabase {
        return coffeeDatabase
    }
    fun loadUserInfo(): UserStatics {
        return UserDatabase
    }
}

// 커피 데이터셋
val coffee1Info = CoffeeInfo("아메리카노", "20", "12")
val coffee2Info = CoffeeInfo("카페라떼", "18", "11")
val coffee3Info = CoffeeInfo("카푸치노", "16", "10")
val coffee4Info = CoffeeInfo("핫초코", "0", "20")

val coffeeDatabase = CoffeeDatabase(
    coffeeIndex = mapOf(
        "1" to coffee1Info,
        "2" to coffee2Info,
        "3" to coffee3Info,
        "4" to coffee4Info
    )
)

// 사용자 섭취 현황 데이터셋

val day1 = DailyStatics("2023/11/20", "3", "44", "50")
val day2 = DailyStatics("2023/11/19", "4", "60", "70")
val day3 = DailyStatics("2023/11/18", "2", "24", "40")
val day4 = DailyStatics("2023/11/17", "1", "19", "10")

val UserDatabase = UserStatics(
    userID = mapOf(
        "1" to day1,
        "2" to day2,
        "3" to day3,
        "4" to day4
    )
)

