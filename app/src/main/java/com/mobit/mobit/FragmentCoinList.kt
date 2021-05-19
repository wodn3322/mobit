package com.mobit.mobit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mobit.mobit.data.MyViewModel
import com.mobit.mobit.databinding.FragmentCoinListBinding

/*
가상화폐 목록과 정보 확인 기능이 구현될 Fragment 입니다.
*/
class FragmentCoinList : Fragment() {

    // UI 변수 시작
    lateinit var binding: FragmentCoinListBinding
    // UI 변수 끝

    val myViewModel: MyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCoinListBinding.inflate(layoutInflater)

        init()

        return binding.root
    }

    fun init() {

    }

}