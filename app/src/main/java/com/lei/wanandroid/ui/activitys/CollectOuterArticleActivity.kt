package com.lei.wanandroid.ui.activitys

import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.RegexUtils
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseActivity
import com.lei.wanandroid.databinding.ActivityCollectOuterArticleBinding
import com.lei.wanandroid.jetpack.livedata.IStateCallback
import com.lei.wanandroid.jetpack.livedata.StateOberver
import com.lei.wanandroid.ui.helper.initUrlLabelRecyclerView
import com.lei.wanandroid.util.hideLoading
import com.lei.wanandroid.util.showGravityShortToast
import com.lei.wanandroid.util.showLoading
import com.lei.wanandroid.viewmodel.MyViewModel

class CollectOuterArticleActivity :
    BaseActivity<MyViewModel, ActivityCollectOuterArticleBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_collect_outer_article
    }

    override fun initView(savedInstanceState: Bundle?) {
        initToolbar(mBinding.toolbar)
        initUrlLabelRecyclerView(mBinding.rvUrlLabel) {
            mBinding.etLink.append(it)
        }
    }

    override fun initData() {
        viewModel.collectOuterArticleStateLiveData.observe(
            this,
            StateOberver(object : IStateCallback<Any> {
                override fun onSuccess(value: Any) {
                    hideLoading(mBinding.progressBar)
                    showGravityShortToast("收藏站外文章成功", Gravity.CENTER)
                    finish()
                }

                override fun onLoading() {
                    showLoading(mBinding.progressBar)
                }

                override fun onFailure(message: String) {
                    hideLoading(mBinding.progressBar)
                    showGravityShortToast(message, Gravity.CENTER)
                }
            })
        )
    }

    override fun provideViewModel(): MyViewModel {
        return ViewModelProviders.of(this).get(MyViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_save, menu)
        return true
    }

    private fun initToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.id_menu_save) collectOuterArticle()
            true
        }
    }

    private fun collectOuterArticle() {
        val title = mBinding.etTitle.text
        val author = mBinding.etAuthor.text
        val link = mBinding.etLink.text
        when {
            title.isNullOrBlank() -> showGravityShortToast("标题不能为空", Gravity.CENTER)
            author.isNullOrBlank() -> showGravityShortToast("作者不能为空", Gravity.CENTER)
            link.isNullOrBlank() -> showGravityShortToast("链接不能为空", Gravity.CENTER)
            !RegexUtils.isURL(link) -> showGravityShortToast("链接不合法", Gravity.CENTER)
            else -> {
                viewModel.collectOuterArticle(
                    title.toString(),
                    author.toString(),
                    link.toString()
                )
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_TITLE, mBinding.etTitle.text.toString())
        outState.putString(KEY_AUTHOR, mBinding.etAuthor.text.toString())
        outState.putString(KEY_LINK, mBinding.etLink.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState?.let {
            mBinding.etTitle.setText(it.getString(KEY_TITLE, ""))
            mBinding.etAuthor.setText(it.getString(KEY_AUTHOR, ""))
            mBinding.etLink.setText(it.getString(KEY_LINK, ""))
        }
    }

    companion object {
        private const val KEY_TITLE = "title"
        private const val KEY_AUTHOR = "author"
        private const val KEY_LINK = "link"

        fun toThis() = ActivityUtils.startActivity(CollectOuterArticleActivity::class.java)
    }
}