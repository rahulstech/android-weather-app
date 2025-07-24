package rahulstech.android.weatherapp.helper

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

interface OnRecyclerViewItemClickListener {

    fun onClickItem(recyclerView: RecyclerView, itemView: View, adapterPosition: Int)
}

class RecyclerViewItemClickHelper(private val recyclerView: RecyclerView) : RecyclerView.SimpleOnItemTouchListener() {

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
        listener.onClickItem(recyclerView, itemView, adapterPosition)
        return true
    }

    private fun getItemViewUnderTouch(recyclerView: RecyclerView, e: MotionEvent): View? {
        val touchX = e.x
        val touchY = e.y
        return recyclerView.findChildViewUnder(touchX, touchY)
    }
}