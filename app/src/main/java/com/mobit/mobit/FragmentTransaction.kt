package com.mobit.mobit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mobit.mobit.data.MyViewModel
import com.mobit.mobit.databinding.FragmentTransactionBinding

/*
코인의 매수/매도 기능이 구현될 Fragment 입니다.
 */
class FragmentTransaction : Fragment() {

    // Fragment 변수 시작
    val fragmentBuy: Fragment = FragmentBuy()
    val fragmentSell: Fragment = FragmentSell()
    // Fragment 변수 끝

    // UI 변수 시작
    lateinit var binding: FragmentTransactionBinding
    // UI 변수 끝

    val myViewModel: MyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTransactionBinding.inflate(layoutInflater)

        init()

        return binding.root
    }

    fun init() {
        replaceFragment(fragmentBuy)
        binding.apply {
            buyAndSellGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
                override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                    when (checkedId) {
                        R.id.coinBuyBtn -> {
                            replaceFragment(fragmentBuy)
                        }
                        R.id.coinSellBtn -> {
                            replaceFragment(fragmentSell)
                        }
                        else -> {
                            Log.e("FragmentTransaction", "Radio Group Error")
                        }
                    }
                }
            })
        }
    }

    fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction: androidx.fragment.app.FragmentTransaction =
            childFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }

}