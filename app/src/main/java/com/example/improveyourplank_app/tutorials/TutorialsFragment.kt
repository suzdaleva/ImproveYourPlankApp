package com.example.improveyourplank_app.tutorials

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.improveyourplank_app.R
import com.example.improveyourplank_app.databinding.FragmentTutorialsBinding

class TutorialsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentTutorialsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_tutorials, container, false)
        return binding.root
    }
}