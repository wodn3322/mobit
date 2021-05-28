package com.mobit.mobit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mobit.mobit.data.CoinInfo
import com.mobit.mobit.data.MyViewModel
import com.mobit.mobit.databinding.FragmentCoinInfoBinding

class FragmentCoinInfo : Fragment() {

    // UI 변수 시작
    lateinit var binding: FragmentCoinInfoBinding
    // UI 변수 끝

    val myViewModel: MyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCoinInfoBinding.inflate(layoutInflater)

        init()

        return binding.root
    }

    fun init() {
        binding.coinInfo.text = when (myViewModel.selectedCoin.value!!) {
            CoinInfo.BTC_CODE -> getString(R.string.BTC)
            CoinInfo.ETH_CODE -> getString(R.string.ETH)
            CoinInfo.ADA_CODE -> getString(R.string.ADA)
            CoinInfo.DOGE_CODE -> getString(R.string.DOGE)
            CoinInfo.XRP_CODE -> getString(R.string.XRP)
            CoinInfo.DOT_CODE -> getString(R.string.DOT)
            CoinInfo.BCH_CODE -> getString(R.string.BCH)
            CoinInfo.LTC_CODE -> getString(R.string.LTC)
            CoinInfo.LINK_CODE -> getString(R.string.LINK)
            CoinInfo.ETC_CODE -> getString(R.string.ETC)
            else -> ""
        }
    }

}