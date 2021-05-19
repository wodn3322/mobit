package com.mobit.mobit

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mobit.mobit.data.MyViewModel
import com.mobit.mobit.databinding.ActivityMainBinding

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

    // 뒤로가기 두번 누르면 앱 종료 관련 변수 시작
    val FINISH_INTERVAL_TIME: Long = 2000
    var backPressedTime: Long = 0
    // 뒤로가기 두번 누르면 앱 종료 관련 변수 끝

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
    }

    fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction: androidx.fragment.app.FragmentTransaction =
            supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}