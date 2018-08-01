package amulp.com.tomatotimer.utils

import amulp.com.tomatotimer.R
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.lifecycle.Observer

class CircleTimer: View {

    private val ARC_START_ANGLE:Float = 270f
    private val THICKNESS_SCALE:Float = 0.06f

    private lateinit var mBitmap: Bitmap
    private lateinit var mCanvas: Canvas

    private lateinit var mCircleOuterBounds: RectF
    private lateinit var mCircleInnerBounds: RectF

    private var mCirclePaint: Paint = Paint()
    private var mEraserPaint: Paint = Paint()

    private var mCircleSweepAngle: Float = 0.toFloat()

    private var mTimerAnimator: ValueAnimator? = null

    var timerObserver:Observer<Long> = Observer { newTime ->
        start(newTime)
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        var circleColor = Color.RED

        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.CircleTimer)
            if (ta != null) {
                circleColor = ta.getColor(R.styleable.CircleTimer_circleColor, circleColor)
                ta.recycle()
            }
        }

        mCirclePaint = Paint()
        mCirclePaint.isAntiAlias = true
        mCirclePaint.color = circleColor

        mEraserPaint = Paint()
        mEraserPaint.isAntiAlias = true
        mEraserPaint.color = Color.TRANSPARENT
        mEraserPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (w != oldw || h != oldh) {
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            mBitmap.eraseColor(Color.TRANSPARENT)
            mCanvas = Canvas(mBitmap)
        }

        super.onSizeChanged(w, h, oldw, oldh)
        updateBounds()
    }

    fun getRemainingTime() : Long{
        return mTimerAnimator!!.currentPlayTime
    }

    override fun onDraw(canvas: Canvas) {
        mCanvas.drawColor(0, PorterDuff.Mode.CLEAR)

        if (mCircleSweepAngle > 0f) {
            mCanvas.drawArc(mCircleOuterBounds, ARC_START_ANGLE, mCircleSweepAngle, true, mCirclePaint)
            mCanvas.drawOval(mCircleInnerBounds, mEraserPaint)
        }

        canvas.drawBitmap(mBitmap, 0f, 0f, null)
    }

    fun start(milliSecs: Long) {
        stop()

        mTimerAnimator = ValueAnimator.ofFloat(0f, 1f)
        mTimerAnimator!!.duration = milliSecs
        mTimerAnimator!!.interpolator = LinearInterpolator()
        mTimerAnimator!!.addUpdateListener { animation -> drawProgress(animation.animatedValue as Float) }
        mTimerAnimator!!.start()
    }

    fun stop() {
        if (mTimerAnimator != null && mTimerAnimator!!.isRunning) {
            mTimerAnimator!!.cancel()

            drawProgress(0f)
        }
    }

    private fun drawProgress(progress: Float) {
        mCircleSweepAngle = 360 * progress

        invalidate()
    }

    private fun updateBounds() {
        val thickness = width * THICKNESS_SCALE

        mCircleOuterBounds = RectF(0f, 0f, width.toFloat(), height.toFloat())
        mCircleInnerBounds = RectF(
                mCircleOuterBounds.left + thickness,
                mCircleOuterBounds.top + thickness,
                mCircleOuterBounds.right - thickness,
                mCircleOuterBounds.bottom - thickness)

        invalidate()
    }


}