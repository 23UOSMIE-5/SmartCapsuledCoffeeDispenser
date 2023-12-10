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

class DataSource {
    suspend fun loadstock(usingID: String): List<DeviceInfo> {
        return loadStockFromFirebase(usingID)
    }

    suspend fun loadStockFromDeviceID(dispenserID: String): List<DeviceInfo> {
        return withContext(Dispatchers.IO) {
            val db = FirebaseFirestore.getInstance()
            val serialNumberRef = db.collection("SerialNumber")

            val deviceInfoList = mutableListOf<DeviceInfo>()

            try {
                val serialDocument = serialNumberRef.document(dispenserID).get().await()
                val coffeeDataMap = mutableMapOf<String, CoffeeData>()

                for (i in 1..3) {  // Assuming a maximum of 3 lines per device; adjust as needed
                    val coffeeName = serialDocument.getString("#${i} Coffee") ?: ""
                    val coffeeStock = serialDocument.getLong("#${i} Coffee Stock")?.toString() ?: "0"
                    coffeeDataMap["#$i Coffee"] = CoffeeData(coffeeName, coffeeStock)

                    Log.d("FirebaseSuccess", "Line processed- Coffee:$coffeeName, Stock:$coffeeStock")
                }


                deviceInfoList.add(
                    DeviceInfo(
                        deviceName = "NULL",
                        usingId = "mylandy2",
                        lineCount = "3",
                        coffeeData = coffeeDataMap
                    )
                )
            } catch (e: Exception) {
                Log.e("FirebaseError", "Error fetching document", e)
            }

            deviceInfoList
        }
    }

    suspend fun loadCoffeeInfo(): CoffeeDatabase {
        return withContext(Dispatchers.IO) {
            val db = FirebaseFirestore.getInstance()
            val coffeeDBRef = db.collection("CoffeeDB")
            val coffeeIndex = mutableMapOf<String, CoffeeInfo>()

            try {
                val coffeeDocuments = coffeeDBRef.get().await()
                for (document in coffeeDocuments) {
                    val coffeeName = document.getString("CoffeeName") ?: ""
                    val caffeine = document.getLong("Caffeine")?.toString() ?: "0" // 숫자로 읽은 후 문자열로 변환
                    val calories = document.getLong("Calories")?.toString() ?: "0" // 숫자로 읽은 후 문자열로 변환
                    val coffeeInfo = CoffeeInfo(coffeeName, caffeine, calories)

                    // 커피 문서 ID(예: "Coffee_1")에서 숫자 부분 추출
                    val coffeeId = document.id.split("_").lastOrNull() ?: continue
                    Log.d("FirebaseSuccess", "coffeeId: $coffeeId, coffeeName: $coffeeName, Caffeine: $caffeine, Calories: $calories")
                    coffeeIndex[coffeeId] = coffeeInfo
                }
            } catch (e: Exception) {
                Log.e("FirebaseError", "Error fetching documents", e)
                // 오류 처리
            }

            CoffeeDatabase(coffeeIndex)
        }
    }
    suspend fun loadUserInfo(usingID: String): UserStatics? {
        return withContext(Dispatchers.IO) {
            fetchDailyStatics(usingID)
        }
    }
}

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
