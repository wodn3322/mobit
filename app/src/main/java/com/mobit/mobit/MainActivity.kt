package com.mobit.mobit

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mobit.mobit.data.CoinInfo
import com.mobit.mobit.data.MyViewModel
import com.mobit.mobit.databinding.ActivityMainBinding
import com.mobit.mobit.network.UpbitAPICaller
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    // Fragment 변수 시작
    val fragmentCoinList: Fragment = FragmentCoinList()
    val fragmentChart: Fragment = FragmentChart()
    val fragmentTransaction: Fragment = FragmentTransaction()
    val fragmentInvestment: Fragment = FragmentInvestment()
    // Fragment 변수 끝

    // UI 변수 시작
    lateinit var binding: ActivityMainBinding
    // UI 변수 끝

    val myViewModel: MyViewModel by viewModels<MyViewModel>()
    val upbitAPICaller: UpbitAPICaller = UpbitAPICaller()
    val upbitAPIHandler: UpbitAPIHandler = UpbitAPIHandler()
    lateinit var upbitAPIThread: UpbitAPIThread
    var threadFlag: Boolean = true
    val codes: ArrayList<String> = arrayListOf(
        CoinInfo.BTC_CODE,
        CoinInfo.ETH_CODE,
        CoinInfo.ADA_CODE,
        CoinInfo.DOGE_CODE,
        CoinInfo.XRP_CODE,
        CoinInfo.DOT_CODE,
        CoinInfo.BCH_CODE,
        CoinInfo.LTC_CODE,
        CoinInfo.LINK_CODE,
        CoinInfo.ETC_CODE
    )

    // 뒤로가기 두번 누르면 앱 종료 관련 변수 시작
    val FINISH_INTERVAL_TIME: Long = 2000
    var backPressedTime: Long = 0
    // 뒤로가기 두번 누르면 앱 종료 관련 변수 끝

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Mobit)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        upbitAPIThread = UpbitAPIThread(100, codes)
        upbitAPIThread.start()



        initData()
        init()
    }

    override fun onBackPressed() {
        val tempTime: Long = System.currentTimeMillis()
        val intervalTime = tempTime - backPressedTime

        if (0 <= intervalTime && intervalTime <= FINISH_INTERVAL_TIME) {
            super.onBackPressed()
        } else {
            backPressedTime = tempTime
            val msg: String = "뒤로 가기를 한 번 더 누르면 종료됩니다."
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStop() {
        super.onStop()
        upbitAPIThread.threadStop(true)
    }

    override fun onRestart() {
        super.onRestart()
        upbitAPIThread.threadStop(false)
        val thread: Thread = object : Thread() {
            override fun run() {
                if (upbitAPIThread.isAlive) {
                    try {
                        upbitAPIThread.join()
                    } catch (e: InterruptedException) {
                        Log.e("OnRestart Error", e.toString())
                    }
                }
                upbitAPIThread = UpbitAPIThread(100, codes)
                upbitAPIThread.start()
            }
        }
        thread.start()
    }

    fun initData() {
        myViewModel.setSelectedCoin(CoinInfo.BTC_CODE)
    }

    fun init() {
        replaceFragment(fragmentCoinList)
        binding.apply {
            bottomNavBar.setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_coinlist -> {
                        replaceFragment(fragmentCoinList)
                        return@setOnNavigationItemSelectedListener true
                    }
                    R.id.menu_chart -> {
                        replaceFragment(fragmentChart)
                        return@setOnNavigationItemSelectedListener true
                    }
                    R.id.menu_transaction -> {
                        replaceFragment(fragmentTransaction)
                        return@setOnNavigationItemSelectedListener true
                    }
                    R.id.menu_investment -> {
                        replaceFragment(fragmentInvestment)
                        return@setOnNavigationItemSelectedListener true
                    }
                    else -> {
                        return@setOnNavigationItemSelectedListener false
                    }
                }
            }
        }

        (fragmentCoinList as FragmentCoinList).listener =
            object : FragmentCoinList.OnFragmentInteraction {
                override fun showTransaction() {
                    binding.bottomNavBar.selectedItemId = R.id.menu_transaction
                }
            }
    }

    fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction: androidx.fragment.app.FragmentTransaction =
            supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }

    inner class UpbitAPIHandler() : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            val bundle: Bundle = msg.data
            if (!bundle.isEmpty) {
                val type = bundle.getInt("type")
                val isSuccess = bundle.getBoolean("isSuccess")
                if (type == 100 && isSuccess) {
                    val coinInfo = bundle.getSerializable("coinInfo") as ArrayList<CoinInfo>
                    myViewModel.setCoinInfo(coinInfo)
                } else if (type == 200 && isSuccess) {
                    for (i in 0..codes.size - 1) {
                        myViewModel.coinInfo.value!![i].price.bidTotalSize =
                            upbitAPICaller.bidTotalSize.get(codes[i])!!
                        myViewModel.coinInfo.value!![i].price.askTotalSize =
                            upbitAPICaller.askTotalSize.get(codes[i])!!
                        myViewModel.coinInfo.value!![i].price.bidPrice =
                            upbitAPICaller.bidPrice.get(codes[i])!!
                        myViewModel.coinInfo.value!![i].price.bidSize =
                            upbitAPICaller.bidSize.get(codes[i])!!
                        myViewModel.coinInfo.value!![i].price.askPrice =
                            upbitAPICaller.askPrice.get(codes[i])!!
                        myViewModel.coinInfo.value!![i].price.askSize =
                            upbitAPICaller.askSize.get(codes[i])!!
                    }
                }
            }
        }
    }

    inner class UpbitAPIThread(var type: Int, val codes: ArrayList<String>) : Thread() {

        var stopFlag: Boolean = false

        override fun run() {
            while (!stopFlag) {
                val message: Message = upbitAPIHandler.obtainMessage()
                val bundle: Bundle = Bundle()
                // 코인 정보 받아오기
                if (type == 100) {
                    bundle.putInt("type", type)
                    val prices = upbitAPICaller.getTicker(codes)
                    if (prices.isNotEmpty()) {
                        val coinInfo = ArrayList<CoinInfo>()
                        for (i in prices.indices) {
                            coinInfo.add(CoinInfo(codes[i], getCoinName(codes[i]), prices[i]))
                        }
                        bundle.putSerializable("coinInfo", coinInfo)
                        bundle.putBoolean("isSuccess", true)
                    } else {
                        bundle.putBoolean("isSuccess", false)
                    }
                }
                // 호가 정보 받아오기
                else if (type == 200) {
                    bundle.putInt("type", type)
                    if (upbitAPICaller.getOrderbook(codes)) {
                        bundle.putBoolean("isSuccess", true)
                    } else {
                        bundle.putBoolean("isSuccess", false)
                    }
                }

                message.data = bundle
                upbitAPIHandler.sendMessage(message)
                sleep(200)
            }
        }

        fun getCoinName(code: String): String {
            return when (code) {
                CoinInfo.BTC_CODE -> CoinInfo.BTC_NAME
                CoinInfo.ETH_CODE -> CoinInfo.ETH_NAME
                CoinInfo.ADA_CODE -> CoinInfo.ADA_NAME
                CoinInfo.DOGE_CODE -> CoinInfo.DOGE_NAME
                CoinInfo.XRP_CODE -> CoinInfo.XRP_NAME
                CoinInfo.DOT_CODE -> CoinInfo.DOT_NAME
                CoinInfo.BCH_CODE -> CoinInfo.BCH_NAME
                CoinInfo.LTC_CODE -> CoinInfo.LTC_NAME
                CoinInfo.LINK_CODE -> CoinInfo.LINK_NAME
                CoinInfo.ETC_CODE -> CoinInfo.ETC_NAME
                else -> CoinInfo.BTC_NAME
            }
        }

        fun threadStop(flag: Boolean) {
            this.stopFlag = flag
        }
    }

}