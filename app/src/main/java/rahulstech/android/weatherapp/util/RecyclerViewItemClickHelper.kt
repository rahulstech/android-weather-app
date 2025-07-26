package rahulstech.android.weatherapp.helper

import android.graphics.Rect
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

interface OnRecyclerViewItemClickListener {

    fun onClickItem(recyclerView: RecyclerView, itemView: View, adapterPosition: Int)
}

class RecyclerViewItemClickHelper(
    private val recyclerView: RecyclerView,
    val excludedChildViewIds: IntArray = intArrayOf()
) : RecyclerView.SimpleOnItemTouchListener() {

    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean = handleClick(e)
    }

    private val gestureDetector = GestureDetector(recyclerView.context, gestureListener)

    private var clickListenerRef: WeakReference<OnRecyclerViewItemClickListener> = WeakReference<OnRecyclerViewItemClickListener>(null)

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean = gestureDetector.onTouchEvent(e)

    fun addOnRecyclerViewItemClickListener(listener: OnRecyclerViewItemClickListener) {
        clickListenerRef = WeakReference<OnRecyclerViewItemClickListener>(listener)
    }

    private fun handleClick(e: MotionEvent): Boolean {
        val listener = clickListenerRef.get() ?: return false
        val itemView: View = getItemViewUnderTouch(recyclerView, e) ?: return false
        val adapterPosition = recyclerView.getChildAdapterPosition(itemView)
        if (isTouchOnExcludedChild(e,itemView,excludedChildViewIds)) {
            return false
        }
        listener.onClickItem(recyclerView, itemView, adapterPosition)
        return true
    }

    private fun getItemViewUnderTouch(recyclerView: RecyclerView, e: MotionEvent): View? {
        val touchX = e.x
        val touchY = e.y
        return recyclerView.findChildViewUnder(touchX, touchY)
    }

    private fun isTouchOnExcludedChild(e: MotionEvent, itemView: View, excludedChildViewIds: IntArray): Boolean {
        val coordinates = IntArray(2)
        itemView.getLocationOnScreen(coordinates)
        val touchX = e.rawX - coordinates[0]
        val touchyY = e.rawY - coordinates[1]

        return excludedChildViewIds.any { viewId ->
            val view = itemView.findViewById<View>(viewId)
            view?.let {
                val rect = Rect()
                it.getHitRect(rect)
                rect.contains(touchX.toInt(), touchyY.toInt())
            } == true
        }
    }
}