package com.lei.wanandroid.data.source.local

import android.content.Context
import androidx.room.Room
import androidx.room.withTransaction
import com.blankj.utilcode.util.Utils
import com.lei.wanandroid.data.bean.User
import com.lei.wanandroid.data.spref.AppConfigHandler
import com.lei.wanandroid.data.spref.IUserConfig

object LocalDataSource : ILocalDataSource {
    private const val DB_NAME = "wanandroid.db"
    private var database: WanAndroidDatabase? = null
    private val userConfig = AppConfigHandler.create(Utils.getApp(), IUserConfig::class.java)

    private fun createDatabase(context: Context): WanAndroidDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            WanAndroidDatabase::class.java,
            DB_NAME
        ).build()
    }

    private fun getDatabase(): WanAndroidDatabase {
        if (database == null) {
            database = createDatabase(Utils.getApp())
        }
        return database!!
    }

    override suspend fun withTransaction(block: suspend () -> Unit) {
        getDatabase().withTransaction { block.invoke() }
    }

    override fun getBannerDao(): BannerDao {
        return getDatabase().getBannerDao()
    }

    override fun getWXPublicAccountDao(): WXPublicAccountDao {
        return getDatabase().getWXPublicAccountDao()
    }

    override fun getReadArticleDao(): ReadArticleDao {
        return getDatabase().getReadArticleDao()
    }

    override fun getArticleDao(): ArticleDao {
        return getDatabase().getArticleDao()
    }

    override fun getHotWordDao(): HotWordDao {
        return getDatabase().getHotWordDao()
    }

    override fun getTodoDao(): TodoDao {
        return getDatabase().getTodoDao()
    }

    override fun getWebsiteDao(): WebsiteDao {
        return getDatabase().getWebsiteDao()
    }

    override fun getCollectArticleDao(): CollectArticleDao {
        return getDatabase().getCollectArticleDao()
    }

    override fun getProjectDao(): ProjectDao {
        return getDatabase().getProjectDao()
    }

    override fun getNavigationDao(): NavigationDao {
        return getDatabase().getNavigationDao()
    }

    override fun getTreeDao(): TreeDao {
        return getDatabase().getTreeDao()
    }

    override fun getLoginUser(): User? {
        return userConfig.getLoginUser()
    }

    override fun saveLoginUser(user: User) {
        userConfig.setLoginUser(user)
    }

    override fun isNightMode(): Boolean {
        return userConfig.isNightMode()
    }

    override fun setNightMode(value: Boolean) {
        userConfig.setNightMode(value)
    }

}