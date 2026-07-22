package com.preetTractor.galaxyAndroid.utils

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

class SynchronizedRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {

    private var isSyncScrollEnabled = true
    var synchronizedRecyclerView: SynchronizedRecyclerView? = null

    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)
        if (isSyncScrollEnabled) {
            synchronizedRecyclerView?.syncScroll(dx, dy)
        }
    }

    fun syncScroll(dx: Int, dy: Int) {
        isSyncScrollEnabled = false
        scrollBy(dx, dy)
        isSyncScrollEnabled = true
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        synchronizedRecyclerView?.setScrollState(state)
    }

    private fun setScrollState(state: Int) {
        isSyncScrollEnabled = state == SCROLL_STATE_IDLE
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        // Call the super method to let RecyclerView measure itself
        super.onMeasure(widthSpec, heightSpec)

        // Retrieve the measured dimensions from super
        val width = measuredWidth
        val height = measuredHeight

        // Ensure to call setMeasuredDimension with the measured width and height
        setMeasuredDimension(width, height)
    }
}
