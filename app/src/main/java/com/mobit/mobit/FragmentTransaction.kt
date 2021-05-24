package com.mobit.mobit

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobit.mobit.adapter.FragmentTransactionAdapter
import com.mobit.mobit.data.CoinInfo
import com.mobit.mobit.data.MyViewModel
import com.mobit.mobit.data.OrderBook
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
    lateinit var adapter: FragmentTransactionAdapter
    var selectedCoin: CoinInfo? = null
    val orderBook: ArrayList<OrderBook> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTransactionBinding.inflate(layoutInflater)

        init()

        return binding.root
    }

    fun init() {
        myViewModel.coinInfo.observe(viewLifecycleOwner, Observer {
            for (coinInfo in myViewModel.coinInfo.value!!) {
                if (coinInfo.code == myViewModel.selectedCoin.value!!) {
                    selectedCoin = coinInfo
                    break
                }
            }
            binding.apply {
                if (selectedCoin != null) {
                    val formatter = DecimalFormat("###,###")
                    val changeFormatter = DecimalFormat("###,###.##")
                    coinName.text = "${selectedCoin!!.name}(${selectedCoin!!.code.split('-')[1]})"
                    coinPrice.text = formatter.format(selectedCoin!!.price.realTimePrice)
                    coinRate.text = changeFormatter.format(selectedCoin!!.price.changeRate)
                    coinDiff.text = when (selectedCoin!!.price.change) {
                        "EVEN" -> ""
                        "RISE" -> "▲"
                        "FALL" -> "▼"
                        else -> ""
                    } + changeFormatter.format(selectedCoin!!.price.changePrice)

                    setTextViewColor(selectedCoin!!)
                }
            }
        })
        myViewModel.orderBook.observe(viewLifecycleOwner, Observer {
            orderBook.clear()
            orderBook.addAll(myViewModel.orderBook.value!!)
            adapter.notifyDataSetChanged()
        })

        for (coinInfo in myViewModel.coinInfo.value!!) {
            if (coinInfo.code == myViewModel.selectedCoin.value!!) {
                selectedCoin = coinInfo
                break
            }
        }
        adapter = FragmentTransactionAdapter(orderBook, selectedCoin!!.price.openPrice)
        adapter.listener = object : FragmentTransactionAdapter.OnItemClickListener {
            override fun onItemClicked(view: View, price: Double) {
                // FragmentBuy와 FragmentSell의 현재가를 출력하는 TextView의 text를 price로 설정해야 한다.
            }
        }

        replaceFragment(fragmentBuy)
        binding.apply {
            recyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            recyclerView.adapter = adapter
            // recyclerview에 있는 item들 중에서 가운데에 위치한 아이템이 화면의 중앙에 위치하도록 하고 싶은데 방법을 모르겠다.
            recyclerView.scrollToPosition(6)

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

            // 코인이 관심목록에 등록되어 있는 경우에는 ImageButton을 채워진 별로 변경해야 한다.
            if (myViewModel.favoriteCoinInfo.value!!.contains(selectedCoin)) {
                favoriteBtn.setImageResource(R.drawable.ic_round_star_24)
            }

            favoriteBtn.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    // 즐겨찾기에 이미 추가되어 있는 경우
                    if (myViewModel.favoriteCoinInfo.value!!.contains(selectedCoin)) {
                        if (myViewModel.removeFavoriteCoinInfo(selectedCoin!!)) {
                            val thread = object : Thread() {
                                override fun run() {
                                    val flag = myViewModel.myDBHelper!!.deleteFavorite(selectedCoin!!.code)
                                    Log.e("favorite delete", flag.toString())
                                }
                            }
                            thread.start()
                            favoriteBtn.setImageResource(R.drawable.ic_round_star_border_24)
                            Toast.makeText(context, "관심코인에서 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    // 즐겨찾기에 추가되어 있지 않은 경우
                    else {
                        if (myViewModel.addFavoriteCoinInfo(selectedCoin!!)) {
                            val thread = object : Thread() {
                                override fun run() {
                                    val flag = myViewModel.myDBHelper!!.insertFavoirte(selectedCoin!!.code)
                                    Log.e("favorite insert", flag.toString())
                                }
                            }
                            thread.start()
                            favoriteBtn.setImageResource(R.drawable.ic_round_star_24)
                            Toast.makeText(context, "관심코인으로 등록되었습니다.", Toast.LENGTH_SHORT).show()
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