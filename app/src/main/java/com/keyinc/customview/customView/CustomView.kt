package com.keyinc.customview.customView

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.keyinc.customview.R
import kotlin.math.min

enum class CustomViewState {
    LOW, MEDIUM, HIGH, DISABLED
}

//todo разобраться зачем она аннотирует @JvmOverloads под коробкой
class CustomView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    View(context, attrs) {
    private var state = CustomViewState.LOW
        set(value) {
            field = value
            invalidate()
        }
    private var color: Int = Color.WHITE
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()
    private val filter = PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP)

    private var arrowAngle: Float = 270f
        set(value) {
            field = value
            invalidate()
        }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomView)
        state = CustomViewState.valueOf(
            typedArray.getString(R.styleable.CustomView_state) ?: state.name
        )
        color = typedArray.getColor(R.styleable.CustomView_color, color)
        typedArray.recycle()

        if (state != CustomViewState.DISABLED) {
            val targetAngle = when (state) {
                CustomViewState.LOW -> 300f
                CustomViewState.MEDIUM -> 360f
                CustomViewState.HIGH -> 420f
                CustomViewState.DISABLED -> 0f
            }
            animateArrow(targetAngle)
        }
    }


    fun turnOff() {
        state = CustomViewState.DISABLED
    }

    fun turnOn() {
        state = CustomViewState.MEDIUM
    }

    fun increaseValue() {
        when (state) {
            CustomViewState.LOW -> {
                state = CustomViewState.MEDIUM
                animateArrow(360f)
            }

            CustomViewState.MEDIUM -> {
                state = CustomViewState.HIGH
                animateArrow(420f)
            }

            CustomViewState.HIGH -> {}
            CustomViewState.DISABLED -> {
                state = CustomViewState.LOW
                animateArrow(300f)
            }
        }
    }

    fun decreaseValue() {
        when (state) {
            CustomViewState.LOW -> {}
            CustomViewState.MEDIUM -> {
                state = CustomViewState.LOW
                animateArrow(300f)
            }

            CustomViewState.HIGH -> {
                state = CustomViewState.MEDIUM
                animateArrow(360f)
            }

            CustomViewState.DISABLED -> {
                state = CustomViewState.HIGH
                animateArrow(360f)
            }
        }
    }

    private fun animateArrow(targetAngle: Float) {
        val animator = ObjectAnimator.ofFloat(this, "arrowAngle", arrowAngle, targetAngle)
        animator.duration = 1000
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = min(centerX, centerY) - paint.strokeWidth / 2

        paint.strokeWidth = 100f
        paint.style = Paint.Style.STROKE

        paint.colorFilter = if (state == CustomViewState.DISABLED) filter else null

        drawSpeedometer(canvas, centerX, centerY, radius)
        if (state != CustomViewState.DISABLED) {
            drawArrow(canvas, centerX, centerY, radius)
        }

        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        canvas.drawCircle(centerX, centerY, radius * 0.1f, paint)
    }

    private fun drawSpeedometer(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
        val arcColor = Color.argb(255, Color.red(color), Color.green(color), Color.blue(color))
        val arcAngle = 60f

        for (i in 0 until 3) {
            val startAngle = 180f + i * arcAngle
            path.reset()
            path.arcTo(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius,
                startAngle,
                arcAngle,
                true
            )
            paint.color = when (i) {
                0 -> Color.argb(30, Color.red(color), Color.green(color), Color.blue(color))
                1 -> Color.argb(100, Color.red(color), Color.green(color), Color.blue(color))
                else -> arcColor
            }
            canvas.drawPath(path, paint)
        }
    }

    private fun drawArrow(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
        val arrowHeight = radius * 0.7f
        val arrowHalfBase = radius * 0.1f

        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL

        val arrowPath = Path()
        arrowPath.moveTo(centerX, centerY - arrowHeight)
        arrowPath.lineTo(centerX - arrowHalfBase, centerY)
        arrowPath.lineTo(centerX + arrowHalfBase, centerY)
        arrowPath.lineTo(centerX, centerY - arrowHeight)
        arrowPath.close()

        val matrix = android.graphics.Matrix()
        matrix.setRotate(arrowAngle, centerX, centerY)
        arrowPath.transform(matrix)

        canvas.drawPath(arrowPath, paint)
    }
}