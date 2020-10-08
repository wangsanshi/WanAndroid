package com.lei.wanandroid.data.source.local

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lei.wanandroid.data.bean.*

@Dao
interface BannerDao {
    @Query("SELECT * FROM banner ORDER BY `order` ASC")
    fun getBannerLiveData(): LiveData<List<BannerBean>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBanners(bannerBeans: List<BannerBean>)
}

@Dao
interface WXPublicAccountDao {
    @Query("SELECT * FROM wx_public_account ORDER BY `order`")
    suspend fun getWXPublicAccountList(): List<PublicAccount>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWXPublicAccounts(accounts: List<PublicAccount>)
}

@Dao
interface ReadArticleDao {
    @Query("SELECT * FROM read_article ORDER BY readRime DESC")
    fun getReadArticlesHistoryDataSourceFactory(): DataSource.Factory<Int, ReadArticle>

    @Query("SELECT COUNT(*) FROM read_article ")
    fun getReadArticlesCountLiveData(): LiveData<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveReadArticle(readArticle: ReadArticle)
}

@Dao
interface ArticleDao {
    @Query("SELECT * FROM article INNER JOIN first_page_article_id ON first_page_article_id.articleId=article.id ORDER BY type DESC,publishTime DESC")
    fun getFirstPageArticleDataSourceFactory(): DataSource.Factory<Int, Article>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveFirstPageArticleID(datas: List<FirstPageArticleID>)

    @Query("DELETE FROM first_page_article_id")
    suspend fun clearFirstPageArticleID()

    @Query("SELECT * FROM article INNER JOIN square_article_id ON square_article_id.articleId=article.id ORDER BY publishTime DESC")
    fun getSquareArticleDataSourceFactory(): DataSource.Factory<Int, Article>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSquareArticleID(datas: List<SquareArticleID>)

    @Query("SELECT * FROM article INNER JOIN question_article_id ON question_article_id.articleId=article.id ORDER BY publishTime DESC")
    fun getQuestionArticleDataSourceFactory(): DataSource.Factory<Int, Article>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveQuestionArticleID(datas: List<QuestionArticleID>)

    @Query("SELECT * FROM article INNER JOIN wechat_article_id ON wechat_article_id.articleId=article.id AND wechat_article_id.chapterId=:chapterId ORDER BY publishTime DESC")
    fun getWechatArticleDataSourceFactory(chapterId: Int): DataSource.Factory<Int, Article>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWechatArticleID(datas: List<WechatArticleID>)

    @Query("SELECT * FROM article INNER JOIN share_article_id ON share_article_id.articleId=article.id ORDER BY publishTime DESC")
    fun getShareArticleDataSourceFactory(): DataSource.Factory<Int, Article>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveShareArticleID(datas: List<ShareArticleID>)

    @Query("DELETE FROM share_article_id")
    suspend fun clearShareArticleIDs()

    @Query("DELETE FROM share_article_id WHERE articleId=:id")
    suspend fun removeShareArticleID(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveArticles(articles: List<Article>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateArticle(article: Article)

    @Query("UPDATE article SET collect=:isCollect WHERE id=:articleId")
    suspend fun updateArticleCollectState(articleId: Int, isCollect: Boolean)

    @Query("DELETE FROM article")
    suspend fun clearArticles()

    @Query("SELECT * FROM article WHERE type=1 ORDER BY publishTime DESC")
    fun getTopArticleLiveData(): LiveData<List<Article>>
}

@Dao
interface HotWordDao {
    @Query("SELECT * FROM hotword ORDER BY `order`")
    suspend fun getHotWords(): List<HotWord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveHotWords(hotwords: List<HotWord>)
}

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo WHERE status=:todoStatus AND type IN (:todoTypes) AND priority IN (:todoprioritys) ORDER BY completeDate DESC")
    fun getTodoDataSourceByFinishDate(
        todoStatus: Int,
        todoTypes: List<Int>,
        todoprioritys: List<Int>
    ): DataSource.Factory<Int, Todo>

    @Query("SELECT * FROM todo WHERE status=:todoStatus AND type IN (:todoTypes) AND priority IN (:todoprioritys) ORDER BY completeDate ASC")
    fun getTodoDataSourceByFinishDateReverse(
        todoStatus: Int,
        todoTypes: List<Int>,
        todoprioritys: List<Int>
    ): DataSource.Factory<Int, Todo>

    @Query("SELECT * FROM todo WHERE status=:todoStatus AND type IN (:todoTypes) AND priority IN (:todoprioritys) ORDER BY date DESC")
    fun getTodoDataSourceByCreateDate(
        todoStatus: Int,
        todoTypes: List<Int>,
        todoprioritys: List<Int>
    ): DataSource.Factory<Int, Todo>

    @Query("SELECT * FROM todo WHERE status=:todoStatus AND type IN (:todoTypes) AND priority IN (:todoprioritys) ORDER BY date ASC")
    fun getTodoDataSourceByCreateDateReverse(
        todoStatus: Int,
        todoTypes: List<Int>,
        todoprioritys: List<Int>
    ): DataSource.Factory<Int, Todo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTodos(todos: List<Todo>)

    @Query("DELETE FROM todo WHERE id=:todoId")
    suspend fun deleteTodo(todoId: Int)
}

@Dao
interface WebsiteDao {
    @Query("SELECT * FROM website ORDER BY `order` ASC")
    fun getWebsitesLiveData(): LiveData<List<WebSite>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWebsites(datas: List<WebSite>)

    @Query("DELETE FROM website WHERE id=:id")
    suspend fun deleteWebsite(id: Int)

    @Query("DELETE FROM website")
    suspend fun clearWebsites()
}

@Dao
interface CollectArticleDao {
    @Query("SELECT * FROM collect_article ORDER BY publishTime DESC")
    fun getCollectArticleDataSourceFactory(): DataSource.Factory<Int, CollectArticle>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCollectArticles(datas: List<CollectArticle>)

    @Query("DELETE FROM collect_article WHERE id=:id")
    suspend fun deleteCollectArticleById(id: Int)

    @Query("DELETE FROM collect_article")
    suspend fun clearCollectArticles()
}

@Dao
interface ProjectDao {
    @Query("SELECT * FROM project_classification ORDER BY `order` ASC")
    fun getProjectClassificationLiveData(): LiveData<List<ProjectClassification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProjectClassification(datas: List<ProjectClassification>)

    @Query("SELECT * FROM article INNER JOIN project_article_id ON project_article_id.classificationId=:classificationId AND project_article_id.articleId=article.id ORDER BY publishTime DESC")
    fun getProjectArticleDataSourceFactory(classificationId: Int): DataSource.Factory<Int, Article>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProjectArticleID(datas: List<ProjectArticleID>)
}

@Dao
interface NavigationDao {
    @Query("SELECT * FROM navigation")
    fun getNavigationLiveData(): LiveData<List<Navigation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveNavigations(datas: List<Navigation>)
}

@Dao
interface TreeDao {
    @Query("SELECT * FROM tree")
    fun getTreeLiveData(): LiveData<List<Tree>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTrees(datas: List<Tree>)
}