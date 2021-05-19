package com.mobit.mobit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mobit.mobit.data.MyViewModel
import com.mobit.mobit.databinding.FragmentSellBinding

/*
코인 매도 기능이 구현될 Fragment 입니다.
*/
class FragmentSell : Fragment() {

    // UI 변수 시작
    lateinit var binding: FragmentSellBinding
    // UI 변수 끝

    val myViewModel: MyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSellBinding.inflate(layoutInflater)

        init()

        return binding.root
    }

    fun init() {
        // spinner 아이템을 보여주는 view를 커스텀하기 위해서 adapter를 만들어준다
        val spinnerAdapter = ArrayAdapter<String>(
            context!!,
            R.layout.spinner_item,
            resources.getStringArray(R.array.orderCountSpinner)
        )

        binding.apply {
            orderCountSpinner.adapter = spinnerAdapter
            orderCountSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
        }
    }

}