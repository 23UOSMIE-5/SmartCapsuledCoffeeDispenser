package com.example.aninterface.ui.stockcheck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.aninterface.R
import com.example.aninterface.adapter.StockcheckItemAdapter
import com.example.aninterface.data.DataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StockCheckFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stock_check, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // recycle view의 item으로 사용할 dataset 불러오기
        CoroutineScope(Dispatchers.Main).launch {
            val stockDataset = DataSource().loadstock("mylandy2")

            // stockDataset을 사용하여 UI 업데이트 등의 작업을 수행
            val recyclerView = view.findViewById<RecyclerView>(R.id.StockRecyclerView)

            recyclerView.adapter = StockcheckItemAdapter(requireContext(), stockDataset)
            recyclerView.setHasFixedSize(true)
        }

    }
}

