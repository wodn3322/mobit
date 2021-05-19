package com.mobit.mobit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mobit.mobit.data.MyViewModel
import com.mobit.mobit.databinding.FragmentChartBinding

/*
코인 차트 기능이 구현될 Fragment 입니다.

차트 기능 구현할 때 사용할 라이브러리
->  https://github.com/PhilJay/MPAndroidChart
 */
class FragmentChart : Fragment() {

    // UI 변수 시작
    lateinit var binding: FragmentChartBinding
    // UI 변수 끝

    val myViewModel: MyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChartBinding.inflate(layoutInflater)
        return binding.root
    }

}