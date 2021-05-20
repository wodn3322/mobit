package com.mobit.mobit.data

import java.io.Serializable

/*
openPrice : 시가
highPrice : 고가
lowPrice : 저가
endPrice : 종가
prevEndPrice : 전일 종가
change : { ("EVEN", 보합), ("RISE", 상승), ("FALL", 하락) }
changePrice : 부호가 있는 변화액
changeRate : 부호가 있는 변화율
bidTotalSize : 호가 매수 총 잔량
askTotalSize : 호가 매도 총 잔량
bidPrice : 매수 호가 -> 내림차순 정렬되어 있음
bidSize : 매수 잔량
askPrice : 매도 호가 -> 오름차순 정렬되어 있음
askSize : 매도 잔량
totalTradeVolume : 누적 거래량(UTC 0시 기준)
totalTradePrice : 누적 거래대금(UTC 0시 기준)
totalTradePrice24: 24시간 누적 거래량
highestWeekPrice : 52주 신고가
highestWeekDate: 52주 신고가 달성일 "yyyy-MM-dd"
lowestWeekPrice : 52주 신저가
lowestWeekDate: 52주 신저가 달성일 "yyyy-MM-dd"
 */
data class Price(
    var realTimePrice: Double,
    var openPrice: Double,
    var highPrice: Double,
    var lowPrice: Double,
    var endPrice: Double,
    var prevEndPrice: Double,
    var change: String,
    var changePrice: Double,
    var changeRate: Double,
    var bidTotalSize: Double,
    var askTotalSize: Double,
    var bidPrice: ArrayList<Double>,
    var bidSize: ArrayList<Double>,
    var askPrice: ArrayList<Double>,
    var askSize: ArrayList<Double>,
    var totalTradeVolume: Double,
    var totalTradePrice: Double,
    var totalTradePrice24: Double,
    var highestWeekPrice: Double,
    var highestWeekDate: String,
    var lowestWeekPrice: Double,
    var lowestWeekDate: String
) : Serializable
