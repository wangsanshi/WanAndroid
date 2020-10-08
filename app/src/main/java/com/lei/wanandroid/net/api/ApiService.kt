package com.lei.wanandroid.net.api

import com.lei.wanandroid.data.bean.*
import retrofit2.http.*

interface ApiService {
    companion object {
        const val BASE_URL_WAN_ANDROID = "https://www.wanandroid.com/"
    }

    @FormUrlEncoded
    @POST("user/register")
    suspend fun register(@FieldMap params: @JvmSuppressWildcards Map<String, Any>): ApiResponse<User>

    @FormUrlEncoded
    @POST("user/login")
    suspend fun login(@FieldMap params: @JvmSuppressWildcards Map<String, Any>): ApiResponse<User>

    @GET("banner/json")
    suspend fun getBanner(): ApiResponse<List<BannerBean>>

    @GET("article/list/{page}/json")
    suspend fun getArticles(@Path("page") page: Int): ApiResponse<Page<Article>>

    @GET("user_article/list/{page}/json")
    suspend fun getSquareArticles(@Path("page") page: Int): ApiResponse<Page<Article>>

    @GET("wenda/list/{page}/json")
    suspend fun getQuestions(@Path("page") page: Int): ApiResponse<Page<Article>>

    @GET("article/top/json")
    suspend fun getTopArticles(): ApiResponse<List<Article>>

    /**
     * 获取所有公众号
     */
    @GET("wxarticle/chapters/json")
    suspend fun getWXPublicAccounts(): ApiResponse<List<PublicAccount>>

    /**
     * 获取某个公众号历史数据
     * @param accountId 公众号id
     * @param page 页数
     */
    @GET("wxarticle/list/{accountId}/{page}/json")
    suspend fun getWechatArticles(
        @Path("accountId") accountId: Int,
        @Path("page") page: Int
    ): ApiResponse<Page<Article>>

    @GET("hotkey/json")
    suspend fun getHotWords(): ApiResponse<List<HotWord>>

    @POST("article/query/{page}/json")
    suspend fun searchByKeyWord(
        @Path("page") page: Int,
        @Query("k") keyword: String
    ): ApiResponse<Page<Article>>

    @GET("article/list/{page}/json")
    suspend fun searchByAuthor(
        @Path("page") page: Int,
        @Query("author") author: String
    ): ApiResponse<Page<Article>>

    @GET("wxarticle/list/{accountId}/{page}/json")
    suspend fun searchByWechat(
        @Path("accountId") accountId: Int,
        @Path("page") page: Int,
        @Query("k") keyword: String?
    ): ApiResponse<Page<Article>>

    @GET("lg/todo/v2/list/{page}/json")
    suspend fun getTodos(
        @Path("page") page: Int,
        @QueryMap params: @JvmSuppressWildcards Map<String, Any>
    ): ApiResponse<Page<Todo>>

    @POST("lg/todo/add/json")
    suspend fun addTodo(@QueryMap params: @JvmSuppressWildcards Map<String, Any>): ApiResponse<Todo>

    @POST("lg/todo/update/{id}/json")
    suspend fun updateTodo(
        @Path("id") id: Int,
        @QueryMap params: @JvmSuppressWildcards Map<String, Any>
    ): ApiResponse<Todo>

    @POST("lg/todo/done/{id}/json")
    suspend fun updateTodoStatus(
        @Path("id") id: Int,
        @Query("status") status: Int
    ): ApiResponse<Todo>

    @POST("lg/todo/delete/{id}/json")
    suspend fun deleteTodo(@Path("id") id: Int): ApiResponse<Any?>

    @POST("lg/user_article/add/json")
    suspend fun shareArticle(
        @Query("title") title: String,
        @Query("link") link: String
    ): ApiResponse<Any?>

    @POST("lg/user_article/delete/{id}/json")
    suspend fun deleteShareArticle(@Path("id") articleId: Int): ApiResponse<Any?>

    @GET("user/lg/private_articles/{page}/json")
    suspend fun getMyShareArticles(@Path("page") page: Int): ApiResponse<ShareArticle>

    @GET("user/{id}/share_articles/{page}/json")
    suspend fun getShareArticlesByUserId(
        @Path("id") userId: Int,
        @Path("page") page: Int
    ): ApiResponse<ShareArticle>

    @GET("coin/rank/{page}/json")
    suspend fun getCoinRankList(@Path("page") page: Int): ApiResponse<Page<CoinInfo>>

    @GET("lg/coin/userinfo/json")
    suspend fun getMyCoinInfo(): ApiResponse<CoinInfo>

    @GET("lg/coin/list/{page}/json")
    suspend fun getCoinSourceList(@Path("page") page: Int): ApiResponse<Page<CoinSource>>

    /**
     * 收藏文章列表
     */
    @GET("lg/collect/list/{page}/json")
    suspend fun getCollectArticles(@Path("page") page: Int): ApiResponse<Page<CollectArticle>>

    /**
     * 收藏站内文章
     */
    @POST("lg/collect/{id}/json")
    suspend fun collectInnerArticle(@Path("id") id: Int): ApiResponse<Any?>

    /**
     * 收藏站外文章
     */
    @POST("lg/collect/add/json")
    suspend fun collectOuterArticle(
        @Query("title") title: String,
        @Query("author") author: String,
        @Query("link") link: String
    ): ApiResponse<CollectArticle>

    /**
     * 文章列表取消收藏
     */
    @POST("lg/uncollect_originId/{id}/json")
    suspend fun cancelCollectFromArticleList(@Path("id") id: Int): ApiResponse<Any?>

    /**
     * 我的收藏页面取消收藏
     */
    @POST("lg/uncollect/{id}/json")
    suspend fun cancelCollectFromMy(
        @Path("id") id: Int,
        @Query("originId") originId: Int
    ): ApiResponse<Any?>

    /**
     * 删除收藏网站
     */
    @POST("lg/collect/deletetool/json")
    suspend fun deleteCollectWebsite(@Query("id") id: Int): ApiResponse<Any?>

    /**
     * 收藏网站列表
     */
    @GET("lg/collect/usertools/json")
    suspend fun getCollectWebsiteList(): ApiResponse<List<WebSite>>

    /**
     * 收藏网站
     */
    @POST("lg/collect/addtool/json")
    suspend fun collectWebsite(
        @Query("name") name: String,
        @Query("link") link: String
    ): ApiResponse<WebSite>

    /**
     * 编辑收藏网站
     */
    @POST("lg/collect/updatetool/json")
    suspend fun updateWebsite(
        @Query("id") id: Int,
        @Query("name") newName: String,
        @Query("link") newLink: String
    ): ApiResponse<WebSite>

    /**
     * 获取项目分类
     */
    @GET("project/tree/json")
    suspend fun getProjectClassification(): ApiResponse<List<ProjectClassification>>

    /**
     * 获取项目列表
     */
    @GET("project/list/{page}/json")
    suspend fun getProjectArticleList(
        @Path("page") page: Int,
        @Query("cid") cid: Int
    ): ApiResponse<Page<Article>>

    /**
     * 获取导航数据
     */
    @GET("navi/json")
    suspend fun getNavigationList(): ApiResponse<List<Navigation>>

    /**
     * 获取体系数据
     */
    @GET("tree/json")
    suspend fun getTreeList(): ApiResponse<List<Tree>>
}