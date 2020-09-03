package com.lei.wanandroid.data.source.local

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Room
import com.blankj.utilcode.util.Utils
import com.lei.wanandroid.data.bean.*
import com.lei.wanandroid.data.spref.AppConfigHandler
import com.lei.wanandroid.data.spref.IUserConfig

object LocalDataSource : ILocalDataSource {
    private const val DB_NAME = "wanandroid.db"
    private var database: WanAndroidDatabase? = null
    private val userConfig = AppConfigHandler.create(Utils.getApp(), IUserConfig::class.java)

    private fun getDatabase(): WanAndroidDatabase {
        if (database == null) {
            database = createDatabase(Utils.getApp())
        }

        return database!!
    }

    private fun createDatabase(context: Context): WanAndroidDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            WanAndroidDatabase::class.java,
            DB_NAME
        ).build()
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

    override fun getArticlesDataSourceFactory(articleType: Int): DataSource.Factory<Int, Article> {
        return when (articleType) {
            ARTICLE_LOCAL_TYPE_FIRST_PAGE -> getDatabase().getArticleDao()
                .getFirstPageArticleDataSourceFactory()
            ARTICLE_LOCAL_TYPE_QUESTION -> getDatabase().getArticleDao()
                .getQuestionArticleDataSourceFactory()
            ARTICLE_LOCAL_TYPE_SQUARE -> getDatabase().getArticleDao()
                .getSquareArticleDataSourceFactory()
            else -> throw IllegalArgumentException("error articleType.")
        }
    }

    override suspend fun saveArticleID(articles: List<Article>, articleType: Int) {
        when (articleType) {
            ARTICLE_LOCAL_TYPE_FIRST_PAGE -> getDatabase().getArticleDao()
                .saveFirstPageArticleID(articles.map { FirstPageArticleID(it.id) })
            ARTICLE_LOCAL_TYPE_QUESTION -> getDatabase().getArticleDao()
                .saveQuestionArticleID(articles.map { QuestionArticleID(it.id) })
            ARTICLE_LOCAL_TYPE_SQUARE -> getDatabase().getArticleDao()
                .saveSquareArticleID(articles.map { SquareArticleID(it.id) })
            ARTICLE_LOCAL_TYPE_WECHAT -> getDatabase().getArticleDao()
                .saveWechatArticleID(articles.map { WechatArticleID(it.id, it.chapterId) })
            else -> throw IllegalArgumentException("error articleType.")
        }
    }

    override fun getReadArticlesHistoryDataSourceFactory(): DataSource.Factory<Int, ReadArticle> {
        return getDatabase().getReadArticleDao().getReadArticlesHistory()
    }

    override fun getWechatArticlesDataSourceFactory(chapterId: Int): DataSource.Factory<Int, Article> {
        return getDatabase().getArticleDao().getWechatArticleDataSourceFactory(chapterId)
    }

    override suspend fun updateArticleCollectState(articleId: Int, isCollect: Boolean) {
        getDatabase().getArticleDao().updateArticleCollectState(articleId, isCollect)
    }

    override fun getTodosFactoryByFinishDate(
        todoStatus: Int,
        todoTypes: List<Int>,
        todoprioritys: List<Int>
    ): DataSource.Factory<Int, Todo> {
        return getDatabase().getTodoDao().getTodosByFinishDate(todoStatus, todoTypes, todoprioritys)
    }

    override fun getTodosFactoryByFinishDateReverse(
        todoStatus: Int,
        todoTypes: List<Int>,
        todoprioritys: List<Int>
    ): DataSource.Factory<Int, Todo> {
        return getDatabase().getTodoDao()
            .getTodosByFinishDateReverse(todoStatus, todoTypes, todoprioritys)
    }

    override fun getTodosFactoryByCreateDate(
        todoStatus: Int,
        todoTypes: List<Int>,
        todoprioritys: List<Int>
    ): DataSource.Factory<Int, Todo> {
        return getDatabase().getTodoDao().getTodosByCreateDate(todoStatus, todoTypes, todoprioritys)
    }

    override fun getTodosFactoryByCreateDateReverse(
        todoStatus: Int,
        todoTypes: List<Int>,
        todoprioritys: List<Int>
    ): DataSource.Factory<Int, Todo> {
        return getDatabase().getTodoDao()
            .getTodosByCreateDateReverse(todoStatus, todoTypes, todoprioritys)
    }

    override fun getReadArticlesCountLiveData(): LiveData<Int> {
        return getDatabase().getReadArticleDao().getReadArticlesCountLiveData()
    }

    override fun getBannerLiveData(): LiveData<List<BannerBean>> {
        return getDatabase().getBannerDao().getBannerLiveData()
    }

    override fun getTopArticleLiveData(): LiveData<List<Article>> {
        return getDatabase().getArticleDao().getTopArticleLiveData()
    }

    override suspend fun saveBanners(bannerBeans: List<BannerBean>) {
        getDatabase().getBannerDao().saveBanners(bannerBeans)
    }

    override suspend fun saveReadArticle(article: ReadArticle) {
        getDatabase().getReadArticleDao().saveReadArticle(article)
    }

    override suspend fun saveTodos(todos: List<Todo>) {
        getDatabase().getTodoDao().saveTodos(todos)
    }

    override suspend fun deleteTodo(todoId: Int) {
        getDatabase().getTodoDao().deleteTodo(todoId)
    }

    override suspend fun saveWXPublicAccounts(accounts: List<PublicAccount>) {
        getDatabase().getWXPublicAccountDao().saveWXPublicAccounts(accounts)
    }

    override suspend fun getWXPublicAccountList(): List<PublicAccount> {
        return getDatabase().getWXPublicAccountDao().getWXPublicAccountList()
    }

    override suspend fun saveArticles(articles: List<Article>) {
        getDatabase().getArticleDao().saveArticles(articles)
    }

    override suspend fun updateArticle(article: Article) {
        getDatabase().getArticleDao().updateArticle(article)
    }

    override suspend fun deleteAllArticles() {
        getDatabase().getArticleDao().clearArticles()
    }

    override suspend fun getHotWords(): List<HotWord> {
        return getDatabase().getHotWordDao().getHotWords()
    }

    override suspend fun saveHotWords(hotwords: List<HotWord>) {
        getDatabase().getHotWordDao().saveHotWords(hotwords)
    }

    override fun getWebsitesLiveData(): LiveData<List<WebSite>> {
        return getDatabase().getWebsiteDao().getWebsitesLiveData()
    }

    override suspend fun saveWebsites(datas: List<WebSite>) {
        getDatabase().getWebsiteDao().saveWebsites(datas)
    }

    override suspend fun deleteWebsite(id: Int) {
        getDatabase().getWebsiteDao().deleteWebsite(id)
    }

    override suspend fun clearWebsites() {
        getDatabase().getWebsiteDao().clearWebsites()
    }

    override fun getCollectArticleDataSourceFactory(): DataSource.Factory<Int, CollectArticle> {
        return getDatabase().getCollectArticleDao().getCollectArticleDataSourceFactory()
    }

    override suspend fun saveCollectArticles(datas: List<CollectArticle>) {
        getDatabase().getCollectArticleDao().saveCollectArticles(datas)
    }

    override suspend fun deleteCollectArticleById(id: Int) {
        getDatabase().getCollectArticleDao().deleteCollectArticleById(id)
    }

    override suspend fun clearCollectArticles() {
        getDatabase().getCollectArticleDao().clearCollectArticles()
    }

    override fun getShareArticleDataSourceFactory(): DataSource.Factory<Int, Article> {
        return getDatabase().getArticleDao().getShareArticleDataSourceFactory()
    }

    override suspend fun saveShareArticleID(datas: List<ShareArticleID>) {
        getDatabase().getArticleDao().saveShareArticleID(datas)
    }

    override suspend fun clearShareArticleIDs() {
        getDatabase().getArticleDao().clearShareArticleIDs()
    }

    override suspend fun removeShareArticleID(articleId: Int) {
        getDatabase().getArticleDao().removeShareArticleID(articleId)
    }

    override fun getProjectClassificationLiveData(): LiveData<List<ProjectClassification>> {
        return getDatabase().getProjectDao().getProjectClassificationLiveData()
    }

    override suspend fun saveProjectClassification(datas: List<ProjectClassification>) {
        getDatabase().getProjectDao().saveProjectClassification(datas)
    }

    override fun getProjectArticleDataSourceFactory(classificationId: Int): DataSource.Factory<Int, Article> {
        return getDatabase().getProjectDao().getProjectArticleDataSourceFactory(classificationId)
    }

    override suspend fun saveProjectArticleID(datas: List<ProjectArticleID>) {
        getDatabase().getProjectDao().saveProjectArticleID(datas)
    }

}