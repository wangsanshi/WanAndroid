package com.lei.wanandroid.data.repository

import com.lei.wanandroid.data.bean.Navigation
import com.lei.wanandroid.data.bean.Tree
import com.lei.wanandroid.data.repository.base.BaseRepository
import com.lei.wanandroid.jetpack.livedata.StateLiveData
import com.lei.wanandroid.net.DefaultHttpCallback
import kotlinx.coroutines.withContext

class SystemRepository : BaseRepository() {

    //----------------------------------------- 体系相关 -----------------------------------------
    fun getTreeLiveData() = localDataSource.getTreeDao().getTreeLiveData()

    suspend fun refreshTreeList(refreshTreeStateLiveData: StateLiveData<Any>) {
        withContext(ioDispatcher) {
            refreshTreeStateLiveData.postLoading()
            remoteDataSource.getTreeList(object :
                DefaultHttpCallback<List<Tree>>(sErrorCodeHandler) {
                override fun onSuccess(data: List<Tree>?) {
                    if (data != null && data.isNotEmpty()) {
                        launchIO {
                            localDataSource.getTreeDao().saveTrees(data)
                            refreshTreeStateLiveData.postSuccess(Any())
                        }
                    } else {
                        refreshTreeStateLiveData.postSuccess(Any())
                    }
                }

                override fun onFailure(code: Int, message: String) {
                    super.onFailure(code, message)
                    refreshTreeStateLiveData.postFailure(message)
                }
            })
        }
    }

    //----------------------------------------- 导航相关 -----------------------------------------
    fun getNavigationLiveData() = localDataSource.getNavigationDao().getNavigationLiveData()

    suspend fun refreshNavigationList(refreshNavigationStateLiveData: StateLiveData<Any>) {
        withContext(ioDispatcher) {
            refreshNavigationStateLiveData.postLoading()
            remoteDataSource.getNavigationList(object : DefaultHttpCallback<List<Navigation>>(
                sErrorCodeHandler
            ) {
                override fun onSuccess(data: List<Navigation>?) {
                    if (data != null && data.isNotEmpty())
                        launchIO {
                            localDataSource.getNavigationDao().saveNavigations(data)
                            refreshNavigationStateLiveData.postSuccess(Any())
                        }
                    else refreshNavigationStateLiveData.postSuccess(Any())
                }

                override fun onFailure(code: Int, message: String) {
                    super.onFailure(code, message)
                    refreshNavigationStateLiveData.postFailure(message)
                }
            })
        }
    }

}