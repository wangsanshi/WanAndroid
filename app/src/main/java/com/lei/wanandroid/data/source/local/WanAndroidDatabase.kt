package com.lei.wanandroid.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lei.wanandroid.data.bean.*

@Database(
    entities = arrayOf(
        Article::class,
        PublicAccount::class,
        HotWord::class,
        Todo::class,
        ReadArticle::class,
        FirstPageArticleID::class,
        SquareArticleID::class,
        QuestionArticleID::class,
        WechatArticleID::class,
        ShareArticleID::class,
        ProjectArticleID::class,
        CollectArticle::class,
        WebSite::class,
        ProjectClassification::class,
        BannerBean::class
    ),
    version = 1,
    exportSchema = false
)
abstract class WanAndroidDatabase : RoomDatabase() {
    abstract fun getBannerDao(): BannerDao

    abstract fun getWXPublicAccountDao(): WXPublicAccountDao

    abstract fun getArticleDao(): ArticleDao

    abstract fun getHotWordDao(): HotWordDao

    abstract fun getTodoDao(): TodoDao

    abstract fun getReadArticleDao(): ReadArticleDao

    abstract fun getWebsiteDao(): WebsiteDao

    abstract fun getCollectArticleDao(): CollectArticleDao

    abstract fun getProjectDao(): ProjectDao
}