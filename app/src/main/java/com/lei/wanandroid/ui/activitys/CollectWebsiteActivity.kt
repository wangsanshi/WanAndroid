package com.lei.wanandroid.ui.activitys

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.RegexUtils
import com.blankj.utilcode.util.Utils
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseActivity
import com.lei.wanandroid.data.bean.WebSite
import com.lei.wanandroid.databinding.ActivityCollectWebsiteBinding
import com.lei.wanandroid.jetpack.livedata.IStateCallback
import com.lei.wanandroid.jetpack.livedata.StateOberver
import com.lei.wanandroid.ui.helper.initUrlLabelRecyclerView
import com.lei.wanandroid.util.hideLoading
import com.lei.wanandroid.util.showGravityShortToast
import com.lei.wanandroid.util.showLoading
import com.lei.wanandroid.viewmodel.MyViewModel

class CollectWebsiteActivity : BaseActivity<MyViewModel, ActivityCollectWebsiteBinding>() {
    private var website: WebSite? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_collect_website
    }

    override fun initView(savedInstanceState: Bundle?) {
        initToobar(mBinding.toolbar)
        initUrlLabelRecyclerView(mBinding.rvUrlLabel) {
            mBinding.etLink.append(it)
        }
    }

    override fun initData() {
        website = intent.getParcelableExtra(KEY_WEBSITE)
        if (website != null) {
            mBinding.tvTitle.setText(R.string.edit_website)
            mBinding.etName.setText(website!!.name)
            mBinding.etLink.setText(website!!.link)
        } else {
            mBinding.tvTitle.setText(R.string.collect_website)
        }
        viewModel.collectWebsiteStateLiveData.observe(
            this,
            StateOberver(object : IStateCallback<Any> {
                override fun onSuccess(value: Any) {
                    showGravityShortToast("收藏网站成功", Gravity.CENTER)
                    hideLoading(mBinding.progressBar)
                    finish()
                }

                override fun onLoading() {
                    showLoading(mBinding.progressBar)
                }

                override fun onFailure(message: String) {
                    showGravityShortToast(message, Gravity.CENTER)
                    hideLoading(mBinding.progressBar)
                }
            })
        )
        viewModel.updateWebsiteStateLiveData.observe(
            this,
            StateOberver(object : IStateCallback<Any> {
                override fun onSuccess(value: Any) {
                    showGravityShortToast("更新网站成功", Gravity.CENTER)
                    hideLoading(mBinding.progressBar)
                    finish()
                }

                override fun onLoading() {
                    showLoading(mBinding.progressBar)
                }

                override fun onFailure(message: String) {
                    showGravityShortToast(message, Gravity.CENTER)
                    hideLoading(mBinding.progressBar)
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

    private fun initToobar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.id_menu_save -> saveWebsite()
            }
            true
        }
    }

    private fun saveWebsite() {
        val name = mBinding.etName.text
        val link = mBinding.etLink.text
        if (name.isNullOrBlank()) {
            showGravityShortToast("名称不能为空", Gravity.CENTER)
        } else if (link.isNullOrBlank()) {
            showGravityShortToast("链接不能为空", Gravity.CENTER)
        } else if (!RegexUtils.isURL(link)) {
            showGravityShortToast("链接不合法", Gravity.CENTER)
        } else if (website == null) {
            viewModel.collectWebsite(name.toString(), link.toString())
        } else if (name.toString() == website!!.name && link.toString() == website!!.link) {
            showGravityShortToast("未做任何修改哦", Gravity.CENTER)
        } else {
            viewModel.updateWebsite(website!!.id, name.toString(), link.toString())
        }
    }

    private fun showExitEditDialog() {
        MaterialDialog(this).show {
            message(R.string.dialog_exit_edit_website)
            positiveButton(R.string.dialog_sure) {
                finish()
            }
            negativeButton(R.string.dialog_cancel)
            lifecycleOwner(this@CollectWebsiteActivity)
        }
    }

    private fun showExitAddDialog() {
        MaterialDialog(this).show {
            message(R.string.dialog_exit_add_website)
            positiveButton(R.string.dialog_sure) {
                finish()
            }
            negativeButton(R.string.dialog_cancel)
            lifecycleOwner(this@CollectWebsiteActivity)
        }
    }


    override fun onBackPressed() {
        if (website != null && (mBinding.etName.text.toString() != website!!.name || mBinding.etLink.text.toString() != website!!.link)) {
            showExitEditDialog()
        } else if (website == null && mBinding.etName.text.toString()
                .isNotBlank() && mBinding.etLink.text.toString()
                .isNotBlank()
        ) {
            showExitAddDialog()
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        private const val KEY_WEBSITE = "website"

        fun toThis(website: WebSite? = null) {
            val i = Intent(Utils.getApp(), CollectWebsiteActivity::class.java)
            i.putExtra(KEY_WEBSITE, website)
            ActivityUtils.startActivity(i)
        }
    }
}