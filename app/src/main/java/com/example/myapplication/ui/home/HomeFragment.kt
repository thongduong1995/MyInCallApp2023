package com.example.myapplication.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.MainAdapter
import com.example.myapplication.adapter.NumberItem
import com.example.myapplication.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val recyclerView = binding.recyclerViewNumberPad

        val mAdapter = MainAdapter(createDataSet())
        recyclerView.apply {
            adapter = mAdapter
            Log.i("thong", "createRecycleView")
            layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
           //layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
        }

        val textView: TextView = binding.numberToCall
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createDataSet(): List<NumberItem>{
        val array = ArrayList<NumberItem>().apply {
            add(NumberItem("1", "~"))
            add(NumberItem("2", "ABC"))
            add(NumberItem("3", "DEF"))
            add(NumberItem("4", "GHK"))
            add(NumberItem("5", "~"))
            add(NumberItem("6", "ABC"))
            add(NumberItem("0", "DEF"))
            add(NumberItem("7", "DEF"))
            add(NumberItem("8", "GHK"))
        }
        return array
    }
}