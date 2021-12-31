package com.example.improveyourplank_app.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.improveyourplank_app.R
import com.example.improveyourplank_app.databinding.FragmentStatisticsBinding

class StatisticsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentStatisticsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_statistics, container, false)
        return binding.root
    }
}