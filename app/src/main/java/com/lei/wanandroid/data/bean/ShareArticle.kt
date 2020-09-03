package com.lei.wanandroid.data.bean

data class ShareArticle(
    val coinInfo: CoinInfo,
    val shareArticles: Page<Article>
)

//积分信息
data class CoinInfo(
    val coinCount: Int,
    val level: Int,
    val rank: String,
    val userId: Int,
    val username: String
)

//积分来源
data class CoinSource(
    val coinCount: Int,
    val date: Long,
    val desc: String,
    val id: Int,
    val reason: String,
    val type: Int,
    val userId: Int,
    val userName: String
)