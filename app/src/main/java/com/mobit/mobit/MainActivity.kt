package com.mobit.mobit

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mobit.mobit.data.*
import com.mobit.mobit.databinding.ActivityMainBinding
import com.mobit.mobit.db.MyDBHelper
import com.mobit.mobit.network.UpbitAPICaller
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    // Fragment 변수 시작
    val fragmentCoinList: Fragment = FragmentCoinList()
    val fragmentChart: Fragment = FragmentChart()
    val fragmentTransaction: Fragment = FragmentTransaction()
    val fragmentInvestment: Fragment = FragmentInvestment()
    val fragmentSetting: Fragment = FragmentSetting()
    // Fragment 변수 끝

    // UI 변수 시작
    lateinit var binding: ActivityMainBinding
    // UI 변수 끝

    val myViewModel: MyViewModel by viewModels<MyViewModel>()
    val upbitAPICaller: UpbitAPICaller = UpbitAPICaller()
    val upbitAPIHandler: UpbitAPIHandler = UpbitAPIHandler()

    // 코인 정보 가져오는 쓰레드
    lateinit var upbitAPIThread: UpbitAPIThread

    // 코인 호가 정보 가져오는 쓰레드
    lateinit var upbitAPIThread2: UpbitAPIThread

    val dbHandler: DBHandler = DBHandler()
    lateinit var dbThread: DBThread

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

    val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.data != null) {
            val krw: Double = it.data!!.getDoubleExtra("krw", 10000000.0)
            myViewModel.asset.value!!.krw = krw
            val thread = object : Thread() {
                override fun run() {
                    myViewModel.myDBHelper!!.setFlag(true)
                    myViewModel.myDBHelper!!.setKRW(krw)
                }
            }
            thread.start()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
//        setTheme(R.style.Theme_Mobit)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDB()
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
        upbitAPIThread2.threadStop(true)
    }

    override fun onRestart() {
        super.onRestart()
        val thread: Thread = object : Thread() {
            override fun run() {
                if (upbitAPIThread.isAlive) {
                    try {
                        upbitAPIThread.join()
                        upbitAPIThread2.join()
                    } catch (e: InterruptedException) {
                        Log.e("OnRestart Error", e.toString())
                    }
                }
                upbitAPIThread = UpbitAPIThread(100, codes)
                upbitAPIThread.start()
                upbitAPIThread2 = UpbitAPIThread(200, codes)
                upbitAPIThread2.start()
            }
        }
        thread.start()
    }

    fun initDB() {
        myViewModel.myDBHelper = MyDBHelper(this)
        dbThread = DBThread()
        dbThread.start()
    }

    fun initData() {
        myViewModel.setSelectedCoin(CoinInfo.BTC_CODE)
        myViewModel.setFavoriteCoinInfo(ArrayList<CoinInfo>())
        myViewModel.setOrderBook(ArrayList<OrderBook>())

        upbitAPIThread = UpbitAPIThread(100, codes)
        upbitAPIThread.start()
        upbitAPIThread2 = UpbitAPIThread(200, codes)
        upbitAPIThread2.start()
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
                    R.id.menu_setting -> {
                        replaceFragment(fragmentSetting)
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
                    val favoriteCoinInfo =
                        bundle.getSerializable("favoriteCoinInfo") as ArrayList<CoinInfo>
                    myViewModel.setFavoriteCoinInfo(favoriteCoinInfo)
                    val asset = bundle.getSerializable("asset") as Asset
                    myViewModel.setAsset(asset)
                } else if (type == 200 && isSuccess) {
                    val orderBook = bundle.getSerializable("orderBook") as ArrayList<OrderBook>
                    myViewModel.setOrderBook(orderBook)
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

                        val favoriteCoinInfo = ArrayList<CoinInfo>()
                        for (favorite in myViewModel.favoriteCoinInfo.value!!) {
                            for (coin in coinInfo) {
                                if (favorite.code == coin.code) {
                                    favoriteCoinInfo.add(coin)
                                    break
                                }
                            }
                        }
                        bundle.putSerializable("favoriteCoinInfo", favoriteCoinInfo)

                        // 임시방편으로 코드를 구현해놓긴 했지만, 나중에 Asset 클래스 구조를 수정해야 할 필요는 있다.
                        val asset = Asset(myViewModel.asset.value!!.krw, ArrayList<CoinAsset>())
                        for (i in myViewModel.asset.value!!.coins.indices) {
                            for (coin in coinInfo) {
                                if (myViewModel.asset.value!!.coins[i].code == coin.code) {
                                    myViewModel.asset.value!!.coins[i].amount =
                                        coin.price.realTimePrice * myViewModel.asset.value!!.coins[i].number
                                    asset.coins.add(myViewModel.asset.value!!.coins[i])
                                    break
                                }
                            }
                        }
                        bundle.putSerializable("asset", asset)
                    } else {
                        bundle.putBoolean("isSuccess", false)
                    }
                }
                // 호가 정보 받아오기
                else if (type == 200) {
                    bundle.putInt("type", type)
                    val orderBook = upbitAPICaller.getOrderbook(myViewModel.selectedCoin.value!!)
                    if (orderBook.isNotEmpty()) {
                        bundle.putSerializable("orderBook", orderBook)
                        bundle.putBoolean("isSuccess", true)
                    } else {
                        bundle.putBoolean("isSuccess", false)
                    }
                }

                message.data = bundle
                upbitAPIHandler.sendMessage(message)
                sleep(300)
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

    inner class DBHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            val bundle: Bundle = msg.data
            if (!bundle.isEmpty) {
                val isFavorites = bundle.getBoolean("isFavorites")
                val isKrw = bundle.getBoolean("isKrw")
                val isCoinAssets = bundle.getBoolean("isCoinAssets")
                val isTransaction = bundle.getBoolean("isTransactions")

                if (isFavorites) {
//                    favoriteCodes.addAll(bundle.getSerializable("favorites") as ArrayList<String>)
                    val favorites = bundle.getSerializable("favorites") as ArrayList<String>
                    val list = ArrayList<CoinInfo>()
                    for (code in favorites) {
                        list.add(
                            CoinInfo(
                                code,
                                code,
                                Price(
                                    0.0,
                                    0.0,
                                    0.0,
                                    0.0,
                                    0.0,
                                    0.0,
                                    "EVEN",
                                    0.0,
                                    0.0,
                                    0.0,
                                    0.0,
                                    0.0,
                                    0.0,
                                    "",
                                    0.0,
                                    ""
                                )
                            )
                        )
                    }
                    myViewModel.setFavoriteCoinInfo(list)
                }
                if (isKrw) {
                    val krw = bundle.getDouble("krw")
                    if (isCoinAssets) {
                        val coinAssets =
                            bundle.getSerializable("coinAssets") as ArrayList<CoinAsset>
                        val asset = Asset(krw, coinAssets)
                        myViewModel.setAsset(asset)
                    } else {
                        val asset = Asset(krw, ArrayList<CoinAsset>())
                        myViewModel.setAsset(asset)
                    }
                    Log.i("isKrw True", krw.toString())
                } else {
                    val asset = Asset(10000000.0, ArrayList<CoinAsset>())
                    myViewModel.setAsset(asset)
                    Log.i("isKrw False", asset.krw.toString())
                }
                if (isTransaction) {
                    val transactions =
                        bundle.getSerializable("transactions") as ArrayList<Transaction>
                    myViewModel.setTransaction(transactions)
                } else {
                    val transactions = ArrayList<Transaction>()
                    myViewModel.setTransaction(transactions)
                }

                val flag = bundle.getBoolean("flag")
                setTheme(R.style.Theme_Mobit)
                if (!flag) {
                    val intent = Intent(this@MainActivity, FirstSettingActivity::class.java)
                    getContent.launch(intent)
                }
            }
        }
    }

    inner class DBThread : Thread() {
        override fun run() {
            val message: Message = dbHandler.obtainMessage()
            val bundle: Bundle = Bundle()

            // DB로부터  favorite 데이터 가져오기
            val favorites = myViewModel.myDBHelper!!.getFavorites()
            if (favorites.isNotEmpty()) {
                bundle.putBoolean("isFavorites", true)
                bundle.putSerializable("favorites", favorites)
            } else {
                bundle.putBoolean("isFavorites", false)
            }

            // DB로부터 KRW 데이터 가져오기
            val krw = myViewModel.myDBHelper!!.getKRW()
            if (krw != null) {
                bundle.putBoolean("isKrw", true)
                bundle.putDouble("krw", krw!!)
            } else {
                bundle.putBoolean("isKrw", false)
            }

            // DB로부터 CoinAsset 데이터 가져오기
            val coinAssets = myViewModel.myDBHelper!!.getCoinAssets()
            if (coinAssets.isNotEmpty()) {
                bundle.putBoolean("isCoinAssets", true)
                bundle.putSerializable("coinAssets", coinAssets)
            } else {
                bundle.putBoolean("isCoinAssets", false)
            }

            // DB로부터 Transaction 데이터 가져오기
            val transactions = myViewModel.myDBHelper!!.getTransactions()
            if (transactions.isNotEmpty()) {
                bundle.putBoolean("isTransactions", true)
                bundle.putSerializable("transactions", transactions)
            } else {
                bundle.putBoolean("isTransactions", false)
            }

            val flag = myViewModel.myDBHelper!!.getFlag()
            bundle.putBoolean("flag", flag)

            message.data = bundle
            dbHandler.sendMessage(message)
        }
    }

}