package com.example.aninterface.ui.stockmanagement

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.aninterface.R
import com.example.aninterface.adapter.StockmanagementAdapter
import com.example.aninterface.data.DataSource
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class StockManagementFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stock_management, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // recycle view의 item으로 사용할 dataset 불러오기
        val stockDataset = DataSource().loadstock()
        val coffeeDataset = DataSource().loadCoffeeInfo()

        val recyclerView = view.findViewById<RecyclerView>(R.id.StockManagementRecyclerView)

        recyclerView.adapter = StockmanagementAdapter(requireContext(), stockDataset, coffeeDataset)
        recyclerView.setHasFixedSize(true)
    }
}