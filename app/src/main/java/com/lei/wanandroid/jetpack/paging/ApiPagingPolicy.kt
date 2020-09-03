package com.lei.wanandroid.jetpack.paging

/**
 * wanandroid api列表请求返回的当前页数的字段有不同的策略
 */
enum class ApiPagingPolicy {
    /*
     * PageData#Page类中的curPage表示当前加载的页数
     */
    DEFAULT,

    /*
     * PageData#Page类中的curPage表示当前加载页数的下一页
     */
    NEXT
}