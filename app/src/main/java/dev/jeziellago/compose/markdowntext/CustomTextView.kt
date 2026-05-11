package dev.jeziellago.compose.markdowntext

import android.content.Context
import android.text.Spannable
import android.text.style.ClickableSpan
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView

class CustomTextView(context: Context) : AppCompatTextView(context) {

    // 移除自定义的 isTextSelectable 属性，直接使用基类的属性

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isTextSelectable) {
            // 在可选择模式下，首先调用 super.onTouchEvent 处理系统自带的选中逻辑（包括长按弹出工具栏）
            val superResult = super.onTouchEvent(event)
            
            // 如果是手指抬起动作，且当前没有文字被选中，则尝试触发链接点击
            if (event.action == MotionEvent.ACTION_UP && !hasSelection()) {
                val link = getClickableSpans(event)
                if (link.isNotEmpty()) {
                    link[0].onClick(this)
                    return true
                }
            }
            return superResult
        } else {
            // 非选择模式下的原有逻辑
            performClick()
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_DOWN) {
                val link = getClickableSpans(event)

                if (link.isNotEmpty()) {
                    if (event.action == MotionEvent.ACTION_UP) {
                        link[0].onClick(this)
                    }
                    return true
                }
            }
            return false
        }
    }

    private fun getClickableSpans(event: MotionEvent): Array<ClickableSpan> {
        val layout = layout ?: return emptyArray()
        var x = event.x.toInt()
        var y = event.y.toInt()

        x -= totalPaddingLeft
        y -= totalPaddingTop

        x += scrollX
        y += scrollY

        val line = layout.getLineForVertical(y)
        if (line < 0 || line >= layout.lineCount) return emptyArray()

        val off = layout.getOffsetForHorizontal(line, x.toFloat())

        val spannable = text as? Spannable ?: return emptyArray()
        return spannable.getSpans(off, off, ClickableSpan::class.java)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }
}
