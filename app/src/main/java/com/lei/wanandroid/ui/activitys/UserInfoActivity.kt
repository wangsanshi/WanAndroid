package com.lei.wanandroid.ui.activitys

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.MergeAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.Utils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseActivity
import com.lei.wanandroid.data.bean.Article
import com.lei.wanandroid.databinding.ActivityUserInfoBinding
import com.lei.wanandroid.jetpack.State
import com.lei.wanandroid.jetpack.livedata.IStateCallback
import com.lei.wanandroid.jetpack.livedata.StateOberver
import com.lei.wanandroid.ui.adapter.LoadMoreAdapter
import com.lei.wanandroid.ui.adapter.ShareArticlePagedListAdapter
import com.lei.wanandroid.ui.helper.articleToRead
import com.lei.wanandroid.ui.helper.getTransparentItemDecoration
import com.lei.wanandroid.ui.helper.modifyArticleCollectStateWithCheckLogin
import com.lei.wanandroid.ui.helper.setSwipeRefreshLayoutStyle
import com.lei.wanandroid.util.hideLoading
import com.lei.wanandroid.util.showLoading
import com.lei.wanandroid.util.showLongToast
import com.lei.wanandroid.util.showShortToast
import com.lei.wanandroid.viewmodel.UserInfoViewModel

class UserInfoActivity : BaseActivity<UserInfoViewModel, ActivityUserInfoBinding>() {
    private lateinit var articleAdapter: ShareArticlePagedListAdapter
    private lateinit var loadMoreAdapter: LoadMoreAdapter

    private var userId: Int = -1

    override fun getLayoutId() = R.layout.activity_user_info

    override fun initView(savedInstanceState: Bundle?) {
        init()
        initToolbar(mBinding.toolbar)
        initRefreshLayout(mBinding.refreshLayout)
        initFloatActionButton(mBinding.fab)
        initRecyclerView()
    }

    private fun init() {
        userId = intent.getIntExtra(KEY_USER_ID, -1)
        viewModel.initUser(userId)
        if (userId == -1) {
            showLongToast("未知用户")
            finish()
        }
        if (viewModel.isLoginUser) mBinding.tvShare.setText(R.string.my_share) else mBinding.tvShare.setText(
            R.string.his_share
        )
    }

    private fun initRefreshLayout(refreshLayout: SwipeRefreshLayout) {
        setSwipeRefreshLayoutStyle(refreshLayout)
        refreshLayout.setOnRefreshListener { viewModel.refreshShareArticles() }
        mBinding.container.errorView?.findViewById<TextView>(R.id.tvRefresh)
            ?.setOnClickListener { viewModel.refreshShareArticles() }
    }

    private fun initFloatActionButton(fab: FloatingActionButton) {
        mBinding.fab.visibility = if (viewModel.isLoginUser) View.VISIBLE else View.GONE
        mBinding.toolbarLayout.title = intent.getStringExtra(KEY_USER_NAME)
        fab.setOnClickListener {
            ActivityUtils.startActivityForResult(
                this@UserInfoActivity,
                ShareArticleActivity::class.java,
                REQUEST_CODE_SHARE_ARTICLE
            )
        }
    }

    private fun initToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.id_menu_rank) {
                showCoinRank()
            } else if (it.itemId == R.id.id_menu_statistical) {
                showCoinStatistical()
            }
            true
        }
    }

    private fun showCoinRank() {
        ActivityUtils.startActivity(RankActivity::class.java)
    }

    private fun showCoinStatistical() {
        ActivityUtils.startActivity(CoinStatisticalActivity::class.java)
    }

    override fun initData() {
        viewModel.coinInfoLiveData.observe(this, Observer {
            mBinding.tvLevel.text = it.level.toString()
            mBinding.tvCoin.text = it.coinCount.toString()
            mBinding.tvRank.text = it.rank
        })
        viewModel.shareArticlePagedList.observe(this, Observer { articleAdapter.submitList(it) })
        viewModel.shareArticleRefreshState.observe(
            this,
            Observer {
                mBinding.refreshLayout.isRefreshing = it == State.Loading
                when (it) {
                    State.Loading -> mBinding.container.showContent()
                    State.SuccessNoData -> mBinding.container.showEmpty()
                    State.Failure -> if (articleAdapter.isEmpty()) mBinding.container.showError()
                    else -> {
                    }
                }
            })
        viewModel.shareArticleLoadMoreState.observe(this, Observer { loadMoreAdapter.setState(it) })
        viewModel.deleteShareArticleStateLiveData.observe(
            this,
            StateOberver(object : IStateCallback<Any> {
                override fun onSuccess(value: Any) {
                    showShortToast("删除成功")
                    hideLoading(mBinding.refreshLayout)
                }

                override fun onLoading() {
                    showLoading(mBinding.refreshLayout)
                }

                override fun onFailure(message: String) {
                    hideLoading(mBinding.refreshLayout)
                    showShortToast(message)
                }
            })
        )

        viewModel.clearShareArticleIDs()
        viewModel.getShareArticleListing(userId)
    }

    private fun initRecyclerView() {
        mBinding.rvList.layoutManager = LinearLayoutManager(this)
        mBinding.rvList.addItemDecoration(getTransparentItemDecoration())
        mBinding.rvList.adapter = getAdapter()
    }

    override fun provideViewModel(): UserInfoViewModel {
        return ViewModelProviders.of(this).get(UserInfoViewModel::class.java)
    }

    private fun getAdapter(): RecyclerView.Adapter<*> {
        articleAdapter = ShareArticlePagedListAdapter(viewModel.isLoginUser)
        articleAdapter.onItemClickListener = { _, _, position ->
            WebViewActivity.toThis(articleToRead(articleAdapter.getItem(position)!!))
        }
        articleAdapter.addChildClickViewIds(R.id.ivCollect, R.id.ivDelete)
        articleAdapter.onItemChildClickListener = { _, view, position ->
            if (view.id == R.id.ivCollect) {
                modifyArticleCollectStateWithCheckLogin(
                    articleAdapter,
                    articleAdapter.getItem(position)!!,
                    position,
                    viewModel::modifyArticleCollectState,
                    true
                )
            } else if (view.id == R.id.ivDelete) {
                articleAdapter.getItem(position)?.let { showDeleteShareArticleDialog(it) }
            }
        }
        loadMoreAdapter = LoadMoreAdapter { viewModel.retryShareArticles() }
        return MergeAdapter(articleAdapter, loadMoreAdapter)
    }

    private fun showDeleteShareArticleDialog(article: Article) {
        MaterialDialog(this).show {
            title(R.string.dialog_delete_share_article)
            positiveButton(R.string.dialog_sure) {
                viewModel.deleteShareArticle(article)
            }
            negativeButton(R.string.dialog_cancel)
            lifecycleOwner(this@UserInfoActivity)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SHARE_ARTICLE && resultCode == Activity.RESULT_OK) viewModel.refreshShareArticles()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (viewModel.isLoginUser) menuInflater.inflate(
            R.menu.menu_userinfo_myself,
            menu
        ) else menuInflater.inflate(R.menu.menu_userinfo_other, menu)
        return true
    }

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val REQUEST_CODE_SHARE_ARTICLE = 0x0010

        fun toThis(userId: Int, userName: String) {
            val intent = Intent(Utils.getApp(), UserInfoActivity::class.java).apply {
                putExtra(KEY_USER_ID, userId)
                putExtra(KEY_USER_NAME, userName)
            }
            ActivityUtils.startActivity(intent)
        }
    }
}