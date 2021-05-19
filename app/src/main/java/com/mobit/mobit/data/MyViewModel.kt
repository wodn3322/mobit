package com.mobit.mobit.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyViewModel : ViewModel() {

    val coinInfo: MutableLiveData<ArrayList<CoinInfo>> = MutableLiveData()
    val asset: MutableLiveData<ArrayList<Asset>> = MutableLiveData()
    val transaction: MutableLiveData<ArrayList<Transaction>> = MutableLiveData()

    fun setCoinInfo(coinInfo: ArrayList<CoinInfo>) {
        this.coinInfo.value = coinInfo
    }

    fun setAsset(asset: ArrayList<Asset>) {
        this.asset.value = asset
    }

    fun setTransaction(transaction: ArrayList<Transaction>) {
        this.transaction.value = transaction
    }

    fun addTransaction(transaction: Transaction) {
        this.transaction.value?.add(transaction)
    }
}