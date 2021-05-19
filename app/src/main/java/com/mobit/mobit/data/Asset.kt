package com.mobit.mobit.data

class Asset {

    var krw: Double = 0.0   // 보유 KRW 금액
    val coins: ArrayList<CoinAsset> = ArrayList()   // 보유 코인 자산

    // code에 해당하는 코인을 number개 만큼 매수한다.
    fun bidCoin(code: String, number: Double) {

    }

    // code에 해당하는 코인을 number개 만큼 매도한다.
    fun askCoin(code: String, number: Double) {

    }
}