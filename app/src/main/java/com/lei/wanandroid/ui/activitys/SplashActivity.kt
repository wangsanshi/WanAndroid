package com.lei.wanandroid.ui.activitys

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.airbnb.lottie.LottieAnimationView
import com.blankj.utilcode.util.ActivityUtils
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseActivity
import com.lei.wanandroid.databinding.ActivitySplashBinding
import com.lei.wanandroid.viewmodel.MainViewModel

class SplashActivity : BaseActivity<MainViewModel, ActivitySplashBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_splash
    }

    override fun initView(savedInstanceState: Bundle?) {
        initLottieAnimationView(mBinding.lottieView)
    }

    override fun initData() {
    }

    override fun provideViewModel(): MainViewModel {
        return ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    private fun initLottieAnimationView(view: LottieAnimationView) {
        view.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                startAlphaAnimator(mBinding.tvAppName)
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })
    }

    private fun startAlphaAnimator(view: TextView) {
        ObjectAnimator.ofFloat(view, "alpha", 0.0f, 1.0f).apply {
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {

                }

                override fun onAnimationEnd(animation: Animator?) {
                    ActivityUtils.startActivity(MainActivity::class.java)
                    finish()
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }
            })
            duration = 1000
            start()
        }
    }
}