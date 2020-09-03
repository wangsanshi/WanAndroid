package com.lei.wanandroid.ui.activitys

import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.Menu
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseActivity
import com.lei.wanandroid.databinding.ActivityShareArticleBinding
import com.lei.wanandroid.jetpack.livedata.IStateCallback
import com.lei.wanandroid.jetpack.livedata.StateOberver
import com.lei.wanandroid.ui.helper.initUrlLabelRecyclerView
import com.lei.wanandroid.util.hideLoading
import com.lei.wanandroid.util.showGravityShortToast
import com.lei.wanandroid.util.showLoading
import com.lei.wanandroid.viewmodel.UserInfoViewModel

class ShareArticleActivity : BaseActivity<UserInfoViewModel, ActivityShareArticleBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_share_article
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.llTip.setOnClickListener { showTipDialog() }
        initToolbar(mBinding.toolbar)
        initUrlLabelRecyclerView(mBinding.rvUrlLabel) {
            mBinding.etLink.append(it)
        }
    }

    override fun initData() {
        viewModel.shareArticleLiveData.observe(this, StateOberver(object : IStateCallback<Any> {
            override fun onSuccess(value: Any) {
                showGravityShortToast("分享文章成功", Gravity.CENTER)
                hideLoading(mBinding.progressBar)
                setResult(RESULT_OK)
                finish()
            }

            override fun onLoading() {
                showLoading(mBinding.progressBar)
            }

            override fun onFailure(message: String) {
                hideLoading(mBinding.progressBar)
                showGravityShortToast(message, Gravity.CENTER)
            }
        }))
    }

    override fun provideViewModel(): UserInfoViewModel {
        return ViewModelProviders.of(this).get(UserInfoViewModel::class.java)
    }

    private fun initToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { this.onBackPressed() }
        toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.id_menu_save) shareArticle()
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_save, menu)
        return true
    }

    private fun shareArticle() {
        val title = mBinding.etTitle.text.toString()
        val link = mBinding.etLink.text.toString()
        when {
            title.isBlank() -> showGravityShortToast("标题不能为空", Gravity.CENTER)
            link.isBlank() -> showGravityShortToast("文章链接不能为空", Gravity.CENTER)
            else -> viewModel.shareArticle(title, link)
        }
    }

    private fun showTipDialog() {
        MaterialDialog(this).show {
            cancelOnTouchOutside(true)
            title(R.string.mini_hint)
            message(R.string.share_article_hint_content)
            cornerRadius(res = R.dimen.material_dialog_cornerRadius)
            lifecycleOwner(this@ShareArticleActivity)
        }
    }

    override fun onBackPressed() {
        if (TextUtils.isEmpty(mBinding.etTitle.text) && TextUtils.isEmpty(mBinding.etLink.text))
            super.onBackPressed() else showExitAddDialog()
    }

    private fun showExitAddDialog() {
        MaterialDialog(this).show {
            message(R.string.dialog_exit_add_todo)
            positiveButton(R.string.dialog_sure) {
                finish()
            }
            negativeButton(R.string.dialog_cancel)
            lifecycleOwner(this@ShareArticleActivity)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_TITLE, mBinding.etTitle.text.toString())
        outState.putString(KEY_LINK, mBinding.etLink.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        mBinding.etTitle.setText(savedInstanceState?.getString(KEY_TITLE))
        mBinding.etLink.setText(savedInstanceState?.getString(KEY_LINK))
    }

    companion object {
        private const val KEY_TITLE = "title"
        private const val KEY_LINK = "link"
    }
}