package com.example.improveyourplank_app.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.improveyourplank_app.R
import com.example.improveyourplank_app.databinding.FragmentLogBinding

class LogFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentLogBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_log, container, false)
        return binding.root
    }
}