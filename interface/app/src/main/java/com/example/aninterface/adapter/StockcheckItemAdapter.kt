package com.example.aninterface.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aninterface.R
import com.example.aninterface.model.DeviceInfo
import com.example.aninterface.model.UserStatics

//import com.example.aninterface.model.Stockcheck

// 재고 확인 리사이클 뷰에 대한 어댑터

class StockcheckItemAdapter(private val context: Context, private val dataset: List<DeviceInfo>): RecyclerView.Adapter<StockcheckItemAdapter.ItemViewHolder>() {
    // 리사이클 뷰의 구성요소 아이디를 받아오는 클래스
    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val DeviceName: TextView = view.findViewById(R.id.deviceName)
        val item_1: TextView = view.findViewById(R.id.item_1)
        val item_2: TextView = view.findViewById(R.id.item_2)
        val item_3: TextView = view.findViewById(R.id.item_3)
        val imageView: ImageView = view.findViewById(R.id.item_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.item_stockcheck, parent, false)

        return ItemViewHolder(adapterLayout)
    }
    // 리사이클 뷰의 내용을 데이터 주소 기반으로 수정
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.DeviceName.text = item.deviceName
        holder.item_1.text = item.coffeeData["coffee1"]?.coffeeName + ": " + item.coffeeData["coffee1"]?.coffeeStock + " 개"
        holder.item_2.text = item.coffeeData["coffee2"]?.coffeeName + ": " + item.coffeeData["coffee2"]?.coffeeStock  + " 개"
        if (item.coffeeData["coffee3"]?.coffeeName != null){
            holder.item_3.text = item.coffeeData["coffee3"]?.coffeeName + ": " + item.coffeeData["coffee3"]?.coffeeStock  + " 개"}
        else{
            holder.item_3.text = ""
        }
        holder.imageView.setImageResource(R.drawable.ic_launcher_foreground)
    }

    override fun getItemCount()=dataset.size
}