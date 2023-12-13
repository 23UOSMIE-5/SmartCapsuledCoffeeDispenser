package com.example.aninterface.ui.stockmanagement

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.aninterface.R
import com.example.aninterface.adapter.StockmanagementAdapter
import com.example.aninterface.data.DataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StockManagementFragment : Fragment() {
    private val TAG = "StockManagementFragment"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView 호출됨")
        return inflater.inflate(R.layout.fragment_stock_management, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // TODO: 1. NFC를 찍으면  2.해당 document에 해당하는 재고 정보를 불러오도록 해야 함
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated 호출됨")

        val dispenserID = arguments?.getString("DispenserID") ?: "NULL"
        Log.d(TAG, "받은 DispenserID: $dispenserID")

        // recycle view의 item으로 사용할 dataset 불러오기
        CoroutineScope(Dispatchers.Main).launch {
            Log.d(TAG, "데이터 로딩 시작")
            val stockDataset = DataSource().loadStockFromDeviceID(dispenserID)
            Log.d(TAG, "재고 데이터셋 로드됨: $stockDataset")
            // stockDataset을 사용하여 UI 업데이트 등의 작업을 수행

            val coffeeDataset = DataSource().loadCoffeeInfo()
            Log.d(TAG, "커피 데이터셋 로드됨: $coffeeDataset")
            // coffeeDatabase를 사용하여 UI 업데이트 등의 작업을 수행

            val recyclerView = view.findViewById<RecyclerView>(R.id.StockManagementRecyclerView)

            recyclerView.adapter = StockmanagementAdapter(requireContext(), stockDataset, coffeeDataset, dispenserID)
            recyclerView.setHasFixedSize(true)
            Log.d(TAG, "RecyclerView 설정 완료")
        }

    }
}