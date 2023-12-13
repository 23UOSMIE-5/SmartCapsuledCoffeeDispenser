package com.example.aninterface.adapter

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aninterface.R
import com.example.aninterface.model.CoffeeDatabase
import com.example.aninterface.model.DeviceInfo
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

private const val TAG = "stockmanagementadapter"

// 재고 확인 리사이클 뷰에 대한 어댑터

class StockmanagementAdapter(
    private val context: Context,
    private val dataset: List<DeviceInfo>,
    private val dataset2: CoffeeDatabase
) : RecyclerView.Adapter<StockmanagementAdapter.ItemViewHolder>() {
    // 리사이클 뷰의 item 구성요소의 아이디를 받아오는 클래스
    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val itemIndex: TextView = view.findViewById(R.id.itemIndex)
        val Button1: Button = view.findViewById(R.id.expandButton)
        val Button2: Button = view.findViewById(R.id.numberButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stockmanagement, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    // 리사이클 뷰의 내용을 데이터 주소 기반으로 수정 (데이터셋의 인덱스 1값을 기준으로 재고 관리 내용 출력)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset.first() // dataset에는 하나의 DeviceInfo 객체만 있음
        Log.d(TAG, "onBindViewHolder - Position: $position, Item: $item")
        val coffeeKey = "#${position + 1} Coffee"

        holder.itemIndex.text = "${position + 1}번"
        holder.Button1.text = item.coffeeData[coffeeKey]?.coffeeName ?: "N/A"
        val coffeeStock = item.coffeeData[coffeeKey]?.coffeeStock?: "N/A"
        holder.Button2.text = coffeeStock

        Log.d(TAG, "CoffeeStock for $coffeeKey: $coffeeStock")

        // 커피 종류 버튼을 누를 때 나오는 커피 종류 리스트
        val options = (1..dataset2.coffeeIndex.size).map { index ->
            dataset2.coffeeIndex[index.toString()]?.coffeeName
        }

        // 커피 종류 버튼을 눌렀을 때 커피 종류 리스트 중 선택한 내용을 업데이트 하는 클릭 함수
        holder.Button1.setOnClickListener {
            showOptionsDialog(context, options.filterNotNull()) { selectedOption ->
                holder.Button1.text = selectedOption
            }
        }

        // 커피 개수 버튼을 눌렀을 때 커피 개수를 입력할 수 있는 창이 출력되는 클릭 함수
        holder.Button2.setOnClickListener {
            showNumberPadDialog(holder.Button2)
        }

    }

    // recycleview의 item 개수를 결정하는 함수 (데이터셋의 인덱스 1값의 linecount 만큼 item을 출력 : 기기 하나에 대한 재고 관리 item이기 때문)
    override fun getItemCount(): Int {
        return dataset.firstOrNull()?.lineCount?.toIntOrNull() ?: 0
    }
}

private fun showOptionsDialog(
    context: Context,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle("커피 종류를 선택해 주세요")
        .setItems(options.toTypedArray()) { _, which ->
            // Call the callback with the selected option
            onOptionSelected(options[which])
        }
    builder.show()
}

private fun showNumberPadDialog(numberButton: Button) {
    val inputEditText = TextInputEditText(numberButton.context)

    inputEditText.inputType = InputType.TYPE_CLASS_NUMBER

    MaterialAlertDialogBuilder(numberButton.context)
        .setTitle("수량을 입력해주세요")
        .setView(inputEditText)
        .setPositiveButton("확인") { _, _ ->
            val enteredNumber = inputEditText.text.toString()
            numberButton.text = enteredNumber
        }
        .setNegativeButton("취소") { dialog, _ ->
            dialog.dismiss()
        }
        .show()
}