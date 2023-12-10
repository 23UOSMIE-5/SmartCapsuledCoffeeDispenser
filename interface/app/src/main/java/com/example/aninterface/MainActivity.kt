package com.example.aninterface

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.aninterface.databinding.ActivityMainBinding
import com.jakewharton.threetenabp.AndroidThreeTen


private const val NFC_REQUEST_CODE = 1
private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentDispenserID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        Log.d(TAG, "onCreate 호출됨")

        // 액션바 숨기기
        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        Log.d(TAG, "NavController 초기화됨")
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_stock_check, R.id.navigation_stock_management
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d(TAG, "Destination 변경됨: ${destination.id}")
            if (destination.id == R.id.navigation_stock_management) {
                Log.d(TAG, "navigation_stock_management로 이동 시도")
                val intent = Intent("nfc_MainActivity")
                intent.setPackage("tae.aop.part2.nfc")
                startActivityForResult(intent, NFC_REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult 호출됨 - requestCode: $requestCode, resultCode: $resultCode")

        if (requestCode == NFC_REQUEST_CODE) {
            Log.d(TAG, "NFC_REQUEST_CODE 매칭됨")
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "결과 OK")
                currentDispenserID = data?.getStringExtra("DispenserID")
                Log.d(TAG, "받은 DispenserID: $currentDispenserID")

                // StockManagementFragment에 dispenserID 전달
                try {
                    val navController = findNavController(R.id.nav_host_fragment_activity_main)
                    Log.d(TAG, "NavController 준비됨")

                    navController.navigate(R.id.navigation_stock_management, Bundle().apply {
                        putString("DispenserID", currentDispenserID)
                    })
                    Log.d(TAG, "navigation_stock_management로 이동 시도")
                } catch (e: Exception) {
                    Log.e(TAG, "NavController를 사용한 이동 중 오류 발생", e)
                }
            } else {
                Log.d(TAG, "결과가 OK가 아님")
            }
        } else {
            Log.d(TAG, "NFC_REQUEST_CODE와 매칭되지 않음")
        }
    }

    companion object {
        private const val NFC_REQUEST_CODE = 1
    }
}