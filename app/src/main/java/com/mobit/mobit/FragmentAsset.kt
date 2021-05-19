package com.mobit.mobit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mobit.mobit.data.MyViewModel
import com.mobit.mobit.databinding.FragmentAssetBinding

/*
보유자산 기능이 구현될 Fragment 입니다.
*/
class FragmentAsset : Fragment() {

    // UI 변수 시작
    lateinit var binding: FragmentAssetBinding
    // UI 변수 끝

    val myViewModel: MyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAssetBinding.inflate(layoutInflater)

        init()

        return binding.root
    }

    fun init() {

    }
}