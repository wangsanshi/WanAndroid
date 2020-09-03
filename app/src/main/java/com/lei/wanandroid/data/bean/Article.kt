package com.lei.wanandroid.data.bean

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

const val ARTICLE_LOCAL_TYPE_TOP = 1              //置顶文章
const val ARTICLE_LOCAL_TYPE_FIRST_PAGE = 2       //首页文章
const val ARTICLE_LOCAL_TYPE_SQUARE = 3           //广场文章
const val ARTICLE_LOCAL_TYPE_QUESTION = 4         //问答文章
const val ARTICLE_LOCAL_TYPE_WECHAT = 5           //微信公众号文章

//已读文章
@Parcelize
@Entity(tableName = "read_article")
data class ReadArticle(
    @PrimaryKey val articleId: Int,
    var readRime: Long,
    val title: String,
    val link: String,
    val desc: String,
    val envelopePic: String,
    val author: String
) : Parcelable

@Entity(tableName = "first_page_article_id")
data class FirstPageArticleID(@PrimaryKey val articleId: Int)

@Entity(tableName = "square_article_id")
data class SquareArticleID(@PrimaryKey val articleId: Int)

@Entity(tableName = "question_article_id")
data class QuestionArticleID(@PrimaryKey val articleId: Int)

@Entity(tableName = "wechat_article_id", primaryKeys = ["articleId", "chapterId"])
data class WechatArticleID(val articleId: Int, val chapterId: Int)

@Entity(tableName = "share_article_id")
data class ShareArticleID(@PrimaryKey val articleId: Int)

/**
 * @param classificationId 项目分类ID
 * @param articleId 文章ID
 */
@Entity(tableName = "project_article_id", primaryKeys = ["classificationId", "articleId"])
data class ProjectArticleID(val classificationId: Int, val articleId: Int)

//文章
@Parcelize
@Entity(tableName = "article")
data class Article(
    val apkLink: String,
    val audit: Int,
    val author: String,
    val canEdit: Boolean,
    val chapterId: Int,
    val chapterName: String,
    var collect: Boolean,
    val courseId: Int,
    val desc: String,
    val descMd: String,
    val envelopePic: String,
    val fresh: Boolean,
    @PrimaryKey
    val id: Int,
    val link: String,
    val niceDate: String,
    val niceShareDate: String,
    val origin: String,
    val prefix: String,
    val projectLink: String,
    val publishTime: Long,
    val realSuperChapterId: Int,
    val selfVisible: Int,
    val shareDate: Long,
    val shareUser: String,
    val superChapterId: Int,
    val superChapterName: String,
    val title: String,
    val type: Int,
    val userId: Int,
    val visible: Int,
    val zan: Int
) : Parcelable {
    @IgnoredOnParcel
    @Ignore
    var tags: List<Tag>? = null
}

@Parcelize
data class Tag(
    val name: String,
    val url: String
) : Parcelable

//收藏文章
@Entity(tableName = "collect_article")
data class CollectArticle(
    val author: String,
    val chapterId: Int,
    val chapterName: String,
    val courseId: Int,
    val desc: String,
    val envelopePic: String,
    @PrimaryKey
    val id: Int,
    val link: String,
    val niceDate: String,
    val origin: String,
    val originId: Int,
    val publishTime: Long,
    val title: String,
    val userId: Int,
    val visible: Int,
    val zan: Int
)