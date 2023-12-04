package com.example.aninterface.data

import android.util.Log
import com.example.aninterface.model.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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
    suspend fun loadstock(usingID: String): List<DeviceInfo> {
        return loadStockFromFirebase(usingID)
    }
    fun loadCoffeeInfo(): CoffeeDatabase {
        return coffeeDatabase
    }
    suspend fun loadUserInfo(usingID: String): UserStatics? {
        return withContext(Dispatchers.IO) {
            fetchDailyStatics(usingID)
        }
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

suspend fun fetchDailyStatics(usingID: String): UserStatics? {
    return try {
        val db = FirebaseFirestore.getInstance()
        val dailyStaticsRef = db.collection("UserStatics").document(usingID).collection("DailyStatics")

        val documents = dailyStaticsRef.get().await()
        val dailyStaticsMap = hashMapOf<String, DailyStatics>()

        for (document in documents) {
            val date = document.id
            val capsule = document.getLong("Capsules")?.toString() ?: "0"
            val caffeine = document.getLong("Caffeine")?.toString() ?: "0"
            val calorie = document.getLong("Calories")?.toString() ?: "0"

            val dailyStatics = DailyStatics(date, capsule, caffeine, calorie)
            dailyStaticsMap[date] = dailyStatics

            // 각 문서의 처리 성공 로그
            Log.d("FirebaseSuccess", "Document processed: $date, Capsules: $capsule, Caffeine: $caffeine, Calories: $calorie")
        }
        // Firestore 접근이 성공적으로 완료되었음을 나타내는 로그
        Log.d("FirebaseSuccess", "All documents fetched successfully")

        UserStatics(dailyStaticsMap)
    } catch (exception: Exception) {
        Log.e("FirebaseError", "Error fetching documents", exception)
        null
    }
}

suspend fun loadStockFromFirebase(usingId: String): List<DeviceInfo> {
    return withContext(Dispatchers.IO) {
        val db = FirebaseFirestore.getInstance()
        val lastUsedDevicesRef = db.collection("UserStatics").document(usingId).collection("lastUsedDevices")
        val serialNumberRef = db.collection("SerialNumber")

        val deviceInfoDeferred = mutableListOf<Deferred<DeviceInfo>>()

        try {
            val lastUsedDevices = lastUsedDevicesRef.get().await()
            for (deviceDocument in lastUsedDevices) {
                deviceInfoDeferred.add(async {
                    val deviceName = deviceDocument.getString("deviceName") ?: ""
                    val serialNumber = deviceDocument.id
                    Log.d("serialNumber", "serialNumber is $serialNumber")

                    val serialDocument = serialNumberRef.document(serialNumber).get().await()
                    val coffeeDataMap = mutableMapOf<String, CoffeeData>()

                    for (i in 1..3) {  // TODO: 라인 갯수를 현재 3개로 지정했는데, 3개 이외일 때도 무리없이 동작하도록 수정
                        val coffeeName = serialDocument.getString("#${i} Coffee") ?: ""
                        val coffeeStock = serialDocument.getLong("#${i} Coffee Stock")?.toString() ?: "0"
                        coffeeDataMap[i.toString()] = CoffeeData(coffeeName, coffeeStock)

                        Log.d("FirebaseSuccess", "Line processed- Coffee:$coffeeName, Stock:$coffeeStock")
                    }

                    DeviceInfo(
                        deviceName = deviceName,
                        usingId = usingId,
                        lineCount = "3",
                        coffeeData = coffeeDataMap
                    )
                })
            }
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error fetching documents", e)
        }

        deviceInfoDeferred.awaitAll()
    }
}
