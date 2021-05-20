package com.mobit.mobit

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.mobit.mobit.data.CoinInfo
import com.mobit.mobit.data.MyViewModel
import com.mobit.mobit.databinding.FragmentTransactionBinding
import java.text.DecimalFormat

/*
코인의 매수/매도 기능이 구현될 Fragment 입니다.
 */
class FragmentTransaction : Fragment() {

    // Fragment 변수 시작
    val fragmentBuy: Fragment = FragmentBuy()
    val fragmentSell: Fragment = FragmentSell()
    val fragmentCoinInfo: Fragment = FragmentCoinInfo()
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
        myViewModel.selectedCoin.observe(viewLifecycleOwner, Observer {
            var coin: CoinInfo? = null
            for (coinInfo in myViewModel.coinInfo.value!!) {
                if (coinInfo.code == myViewModel.selectedCoin.value!!) {
                    coin = coinInfo
                    break
                }
            }
            binding.apply {
                if (coin != null) {
                    val formatter = DecimalFormat("###,###")
                    val changeFormatter = DecimalFormat("###,###.##")

                    coinName.text = "${coin!!.name}(${coin!!.code.split('-')[1]})"
                    coinPrice.text = formatter.format(coin!!.price.realTimePrice)
                    coinRate.text = changeFormatter.format(coin!!.price.changeRate)
                    coinDiff.text = when (coin!!.price.change) {
                        "EVEN" -> ""
                        "RISE" -> "▲"
                        "FALL" -> "▼"
                        else -> ""
                    } + changeFormatter.format(coin!!.price.changePrice)

                    setTextViewColor(coin!!)
                }
            }
        })
        myViewModel.coinInfo.observe(viewLifecycleOwner, Observer {
            var coin: CoinInfo? = null
            for (coinInfo in myViewModel.coinInfo.value!!) {
                if (coinInfo.code == myViewModel.selectedCoin.value!!) {
                    coin = coinInfo
                    break
                }
            }
            binding.apply {
                if (coin != null) {
                    val formatter = DecimalFormat("###,###")
                    val changeFormatter = DecimalFormat("###,###.##")
                    coinPrice.text = formatter.format(coin!!.price.realTimePrice)
                    coinRate.text = changeFormatter.format(coin!!.price.changeRate)
                    coinDiff.text = when (coin!!.price.change) {
                        "EVEN" -> ""
                        "RISE" -> "▲"
                        "FALL" -> "▼"
                        else -> ""
                    } + changeFormatter.format(coin!!.price.changePrice)
                    

                    setTextViewColor(coin!!)
                }
            }
        })

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
                        R.id.coinInfoBtn -> {
                            replaceFragment(fragmentCoinInfo)
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

    fun setTextViewColor(coinInfo: CoinInfo) {
        binding.apply {
            if (coinInfo.price.changeRate > 0) {
                coinPrice.setTextColor(Color.parseColor("#bd4e3a"))
                coinRate.setTextColor(Color.parseColor("#bd4e3a"))
                coinDiff.setTextColor(Color.parseColor("#bd4e3a"))
            } else if (coinInfo.price.changeRate < 0) {
                coinPrice.setTextColor(Color.parseColor("#135fc1"))
                coinRate.setTextColor(Color.parseColor("#135fc1"))
                coinDiff.setTextColor(Color.parseColor("#135fc1"))
            } else {
                coinPrice.setTextColor(Color.parseColor("#FFFFFF"))
                coinRate.setTextColor(Color.parseColor("#FFFFFF"))
                coinDiff.setTextColor(Color.parseColor("#FFFFFF"))
            }
        }
    }

}