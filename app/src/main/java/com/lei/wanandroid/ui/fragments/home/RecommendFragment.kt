package com.lei.wanandroid.ui.fragments.home

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseFragment
import com.lei.wanandroid.data.bean.BannerBean
import com.lei.wanandroid.databinding.FragmentRecommendBinding
import com.lei.wanandroid.viewmodel.HomeViewModel

class RecommendFragment : BaseFragment<HomeViewModel, FragmentRecommendBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
//        setSwipeRefreshLayoutStyle(mBinding.refreshLayout)
//        viewModel.bannerLiveData.observe(this, Observer {
//            initBanner(it)
//        })
//        viewModel.refreshBannerStateLiveData.observe(
//            this,
//            StateOberver(object : IStateCallback<Any> {
//                override fun onSuccess(value: Any) {
//                    mBinding.pbBanner.hide()
//                }
//
//                override fun onLoading() {
//                    mBinding.pbBanner.show()
//                }
//
//                override fun onFailure(message: String) {
//                    mBinding.pbBanner.hide()
//                    showShortToast(message)
//                }
//            })
//        )
//        viewModel.refreshBanners()
    }

    private fun initBanner(banners: List<BannerBean>) {
//        mBinding.banner.addBannerLifecycleObserver(this)
//            .setAdapter(object : BannerImageAdapter<BannerBean>(banners) {
//                override fun onBindView(
//                    holder: BannerImageHolder?,
//                    data: BannerBean?,
//                    position: Int,
//                    size: Int
//                ) {
//                    (holder?.itemView as ImageView).load(data?.imagePath) {
//                        this.listener(
//                            onStart = { mBinding.pbBanner.show() },
//                            onSuccess = { _, _ -> mBinding.pbBanner.hide() },
//                            onError = { _, _ -> mBinding.pbBanner.hide() })
//                    }
//                }
//            })
//            .setIndicator(CircleIndicator(requireContext()))
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_recommend
    }

    override fun provideViewModel(): HomeViewModel {
        return ViewModelProviders.of(requireActivity()).get(HomeViewModel::class.java)
    }

}