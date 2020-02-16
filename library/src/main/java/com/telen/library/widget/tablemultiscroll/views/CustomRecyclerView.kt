package com.telen.library.widget.tablemultiscroll.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class CustomRecyclerView: RecyclerView {

    var mirror: CustomRecyclerView? = null

    private var mTouchedRvTag: String? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        addOnItemTouchListener(object: OnItemTouchListener {
            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                mTouchedRvTag = this@CustomRecyclerView.tag.toString()
                mirror?.mTouchedRvTag = mTouchedRvTag
                return false
            }
        })
    }

    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)
        mTouchedRvTag?.let {
            if(it == tag.toString()) {
                mirror?.scrollBy(0, dy)
            }
        }
    }
}