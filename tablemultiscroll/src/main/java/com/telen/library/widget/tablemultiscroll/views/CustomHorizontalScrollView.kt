package com.telen.library.widget.tablemultiscroll.views

import android.content.Context
import android.util.AttributeSet
import android.widget.HorizontalScrollView

class CustomHorizontalScrollView: HorizontalScrollView {

    interface OnPositionCallback {
        fun onBeginScrolling()
        fun onBackToStart()
    }

    var mirror: CustomHorizontalScrollView? = null

    var customScrollListener: OnPositionCallback? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        isHorizontalScrollBarEnabled = false
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        mirror?.scrollTo(l, 0)
        if(l == 0)
            customScrollListener?.onBackToStart()
        else if(oldl == 0)
            customScrollListener?.onBeginScrolling()
    }
}