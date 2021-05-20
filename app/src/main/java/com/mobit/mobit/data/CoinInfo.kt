package com.mobit.mobit.data

import java.io.Serializable

data class CoinInfo(
    val code: String,       // 코인 코드
    val name: String,       // 코인 이름
    val price: Price        // 코인 현재가 정보
) : Serializable {
    companion object {
        val BTC_CODE = "KRW-BTC"        // 비트코인
        val BTC_NAME = "비트코인"
        val ETH_CODE = "KRW-ETH"        // 이더리움
        val ETH_NAME = "이더리움"
        val ADA_CODE = "KRW-ADA"        // 에이다
        val ADA_NAME = "에이다"
        val DOGE_CODE = "KRW-DOGE"      // 도지코인
        val DOGE_NAME = "도지코인"
        val XRP_CODE = "KRW-XRP"        // 리플
        val XRP_NAME = "리플"
        val DOT_CODE = "KRW-DOT"        // 폴카닷
        val DOT_NAME = "폴카닷"
        val BCH_CODE = "KRW-BCH"        // 비트코인캐시
        val BCH_NAME = "비트코인캐시"
        val LTC_CODE = "KRW-LTC"        // 라이트코인
        val LTC_NAME = "라이트코인"
        val LINK_CODE = "KRW-LINK"      // 체인링크
        val LINK_NAME = "체인링크"
        val ETC_CODE = "KRW-ETC"        // 이더리움클래식
        val ETC_NAME = "이더리움클래식"
    }
}