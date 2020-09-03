package com.lei.wanandroid.ui.activitys

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.ViewConfiguration
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.Utils
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseActivity
import com.lei.wanandroid.data.bean.ReadArticle
import com.lei.wanandroid.databinding.ActivityWebviewBinding
import com.lei.wanandroid.ui.helper.CustomWebView
import com.lei.wanandroid.ui.helper.setDefaultSettings
import com.lei.wanandroid.util.copyText
import com.lei.wanandroid.util.showShortToast
import com.lei.wanandroid.viewmodel.WebViewModel
import com.tencent.bugly.crashreport.CrashReport

class WebViewActivity : BaseActivity<WebViewModel, ActivityWebviewBinding>() {
    private lateinit var article: ReadArticle

    override fun getLayoutId(): Int {
        return R.layout.activity_webview
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.model = viewModel
        mBinding.activity = this
        initToolbar(mBinding.toolbar)
        initWebView(mBinding.webView)
    }

    private fun initToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { this.onBackPressed() }
        toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.id_menu_more) showMoreDialog()
            true
        }
    }

    private fun initWebView(webView: CustomWebView) {
        webView.setDefaultSettings()
        webView.setScrollCallback { _, t, yVelocity ->
            val minFlingVelocity = ViewConfiguration.get(this).scaledMinimumFlingVelocity
            if (yVelocity > 0 && SizeUtils.px2dp(yVelocity) >= minFlingVelocity && t == 0) {
                //下滑，且滑动到顶部
                if (mBinding.viewSwitcher.displayedChild == 1) {
                    mBinding.viewSwitcher.setOutAnimation(
                        this,
                        R.anim.toolbar_slide_top_out
                    )
                    mBinding.viewSwitcher.setInAnimation(
                        this,
                        R.anim.toolbar_slide_bottom_in
                    )
                    mBinding.viewSwitcher.showPrevious()
                }
            } else if (yVelocity < 0 && SizeUtils.px2dp(Math.abs(yVelocity)) >= minFlingVelocity) {
                //上滑
                if (mBinding.viewSwitcher.displayedChild == 0) {
                    mBinding.viewSwitcher.setOutAnimation(
                        this,
                        R.anim.toolbar_slide_bottom_out
                    )
                    mBinding.viewSwitcher.setInAnimation(this, R.anim.toolbar_slide_top_in)
                    mBinding.viewSwitcher.showNext()
                }
            }
        }
    }

    override fun initData() {
        article = intent.getParcelableExtra(KEY_READ_ARTICLE)
        mBinding.webView.webViewClient = InnerWebViewClient()
        mBinding.webView.webChromeClient = InnerWebChromeClient()
        mBinding.webView.loadUrl(this.article.link)
        viewModel.saveReadArticle(article)
    }

    override fun provideViewModel(): WebViewModel {
        return ViewModelProviders.of(this).get(WebViewModel::class.java)
    }

    override fun onBackPressed() {
        if (mBinding.webView.canGoBack()) {
            mBinding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    inner class InnerWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            request?.url?.scheme?.let {
                return if (HTTP_SCHEME == it || HTTPS_SCHEME == it) {
                    super.shouldOverrideUrlLoading(view, request)
                } else {
                    true
                }
            }
            return super.shouldOverrideUrlLoading(view, request)
        }
    }

    inner class InnerWebChromeClient : WebChromeClient() {
        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
            title?.let {
                viewModel.title.value = it
            }
        }

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            // 增加Javascript异常监控
            CrashReport.setJavascriptMonitor(view, true)
            //页面加载到80%就差不多了
            val rightProgress = (newProgress * 1.25).toInt()
            mBinding.progressBar.setWebProgress(rightProgress)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_webview, menu)
        return true
    }

    private fun showMoreDialog() {
        val dialog = MaterialDialog(this, BottomSheet(LayoutMode.WRAP_CONTENT))
        dialog.customView(R.layout.dialog_more)
        setDialogViewAction(dialog)
        dialog.show {
            lifecycleOwner(this@WebViewActivity)
        }
    }

    private fun setDialogViewAction(dialog: MaterialDialog) {
        with(dialog.getCustomView()) {
            findViewById<TextView>(R.id.tvCancel).setOnClickListener { dialog.dismiss() }
            findViewById<TextView>(R.id.tvShareLink).setOnClickListener {
                copyText(it.context, article.link)
                showShortToast("已复制到剪贴板")
                dialog.dismiss()
            }
            findViewById<TextView>(R.id.tvShareBrowser).setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.link))
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                    dialog.dismiss()
                } else {
                    showShortToast("没有可用的浏览器")
                }
            }
            findViewById<TextView>(R.id.tvShareOther).setOnClickListener {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_TEXT, article.link)
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(intent, ""))
            }
        }
    }

    companion object {
        private const val HTTP_SCHEME = "http"
        private const val HTTPS_SCHEME = "https"
        private const val KEY_READ_ARTICLE = "key_read_article"

        fun toThis(article: ReadArticle) {
            val i = Intent(Utils.getApp(), WebViewActivity::class.java)
            i.putExtra(KEY_READ_ARTICLE, article)
            ActivityUtils.startActivity(i)
        }
    }
}