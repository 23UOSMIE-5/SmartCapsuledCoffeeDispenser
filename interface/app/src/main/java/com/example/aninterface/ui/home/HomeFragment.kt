package com.example.aninterface.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.aninterface.R
import com.example.aninterface.adapter.StockcheckItemAdapter
import com.example.aninterface.data.DataSource
import com.example.aninterface.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // recycle view의 item으로 사용할 dataset 불러오기
        val UserDataset = DataSource().loadUserInfo()

        var capsule: TextView = view.findViewById(R.id.capsule)
        var caffeine: TextView = view.findViewById(R.id.caffeine_n)
        var calorie: TextView = view.findViewById(R.id.calorie_n)

        capsule.text = UserDataset.userID["1"]?.capsule + " 개"
        caffeine.text = UserDataset.userID["1"]?.caffeine + " mg"
        calorie.text = UserDataset.userID["1"]?.calorie + " kcal"
    }
}
