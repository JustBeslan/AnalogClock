package my.general.clockcustomview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.*
import kotlin.math.min

class CustomClockView(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs)  {

    private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val delimitersPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeCap = Paint.Cap.ROUND
    }

    private val numbersPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val handsPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeCap = Paint.Cap.ROUND
    }

    private var widthContent: Int = 500
    private var heightContent: Int = 500
    var clockPointCenter = PointF()

    var ringColor: Int

    private var hourHandStartPoint = PointF(0f, 0f)
    private var hourHandEndPoint = PointF(0f, 0f)
    private var hourHandRotateDegree: Float = 0f
    var hourHandColor: Int

    private var minuteHandStartPoint = PointF(0f, 0f)
    private var minuteHandEndPoint = PointF(0f, 0f)
    private var minuteHandRotateDegree: Float = 0f
    var minuteHandColor: Int

    private var secondHandStartPoint = PointF(0f, 0f)
    private var secondHandEndPoint = PointF(0f, 0f)
    private var secondHandRotateDegree: Float = 0f
    var secondHandColor: Int

    private var numberCenterPoint = PointF(0f, 0f)
    private var numberTextSize: Float = 0f
    var numbersColor: Int

    var circleRadius: Float = 0f
    var circleContentRadius: Float = 0f
    var circleColor: Int

    var enabledSmallDelimiters: Boolean
    private var countDelimiters: Int = 0
    var typeDelimiters: Int
    private var startPointDelimiter = PointF(0f, 0f)
    private var endPointDelimiter = PointF(0f, 0f)
    private var yEndPointMainDelimiter: Float = 0f
    private var yEndPointSmallDelimiter: Float = 0f
    var mainDelimitersColor: Int
    var smallDelimitersColor: Int

    private lateinit var calendar: Calendar

    init {

        val setXmlAttributes = context.obtainStyledAttributes(attrs, R.styleable.CustomClockView)

        ringColor = setXmlAttributes.getColor(R.styleable.CustomClockView_ringColor, Color.BLACK)
        circleColor = setXmlAttributes.getColor(R.styleable.CustomClockView_circleColor, Color.WHITE)
        mainDelimitersColor = setXmlAttributes.getColor(R.styleable.CustomClockView_mainDelimitersColor, Color.BLACK)
        smallDelimitersColor = setXmlAttributes.getColor(R.styleable.CustomClockView_smallDelimitersColor, Color.GRAY)
        numbersColor = setXmlAttributes.getColor(R.styleable.CustomClockView_numbersColor, Color.BLACK)
        hourHandColor = setXmlAttributes.getColor(R.styleable.CustomClockView_hourHandColor, Color.WHITE)
        minuteHandColor = setXmlAttributes.getColor(R.styleable.CustomClockView_minuteHandColor, Color.WHITE)
        secondHandColor = setXmlAttributes.getColor(R.styleable.CustomClockView_secondHandColor, Color.WHITE)

        enabledSmallDelimiters = setXmlAttributes.getBoolean(R.styleable.CustomClockView_enabledSmallDelimiters, true)

        typeDelimiters = setXmlAttributes.getInt(R.styleable.CustomClockView_delimitersType, 0)

        setXmlAttributes.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> min(widthSize, widthContent)
            else -> widthContent
        }

        val height = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> min(heightSize, heightContent)
            else -> heightContent
        }

        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        widthContent = w - paddingStart - paddingEnd
        heightContent = h - paddingTop - paddingBottom

        clockPointCenter = PointF(
            (widthContent / 2 + paddingStart).toFloat(),
            (heightContent / 2 + paddingTop).toFloat()
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        circleRadius = min(
            widthContent / 2,
            heightContent / 2
        ).toFloat()
        circleContentRadius = circleRadius - ringPaint.strokeWidth.toInt()

        countDelimiters = if (enabledSmallDelimiters) 59 else 11
        startPointDelimiter.y = -11*circleContentRadius/12
        yEndPointMainDelimiter = -4*circleContentRadius/5
        yEndPointSmallDelimiter = -6*circleContentRadius/7

        numberCenterPoint.y = if (typeDelimiters == 0) -2*circleContentRadius/3 else -4*circleContentRadius/5
        numberTextSize = circleContentRadius / 6

        hourHandStartPoint.y = circleContentRadius/15
        hourHandEndPoint.y = -circleContentRadius/2

        minuteHandStartPoint.y = circleContentRadius/10
        minuteHandEndPoint.y = -11*circleContentRadius/14

        secondHandStartPoint.y = circleContentRadius/11
        secondHandEndPoint.y = -5*circleContentRadius/7
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.translate(clockPointCenter.x, clockPointCenter.y)

        if (circlePaint.color != circleColor) circlePaint.color = circleColor
        canvas?.drawCircle(0f, 0f, circleContentRadius, circlePaint)

        if (ringPaint.color != ringColor) ringPaint.color = ringColor
        canvas?.drawCircle(0f, 0f, circleContentRadius, ringPaint)

        drawDelimiters(canvas)
        drawNumbers(canvas)
        drawHands(canvas)

        postInvalidateDelayed(500)
        postInvalidate()
    }

    private fun drawDelimiters(canvas: Canvas?) {
        for (i in 0..countDelimiters) {
            if (enabledSmallDelimiters) {
                if (i % 5 == 0) {
                    delimitersPaint.apply {
                        strokeWidth = circleRadius/30f
                        color = mainDelimitersColor
                    }
                }
                else {
                    delimitersPaint.apply {
                        strokeWidth = circleRadius/70f
                        color = smallDelimitersColor
                    }
                }
                endPointDelimiter.y = if (i % 5 == 0) yEndPointMainDelimiter else yEndPointSmallDelimiter
            } else {
                delimitersPaint.apply {
                    strokeWidth = circleRadius/30f
                    color = mainDelimitersColor
                }
                endPointDelimiter.y = yEndPointMainDelimiter
            }

            if (typeDelimiters == 0) {
                canvas?.drawLine(
                    startPointDelimiter.x,
                    startPointDelimiter.y,
                    endPointDelimiter.x,
                    endPointDelimiter.y,
                    delimitersPaint
                )
            } else {
                canvas?.drawPoint(
                    startPointDelimiter.x,
                    startPointDelimiter.y,
                    delimitersPaint
                )
            }
            canvas?.rotate(if (enabledSmallDelimiters) 6f else 30f)
        }
    }

    private fun drawNumbers(canvas: Canvas?) {

        val textBound = Rect()
        numbersPaint.color = numbersColor

        for (i in 12 downTo 1) {
            canvas?.save()
            canvas?.translate(numberCenterPoint.x, numberCenterPoint.y)
            val text = i.toString()

            numbersPaint.getTextBounds(text, 0, text.length, textBound)
            if (i != 12) {
                canvas?.rotate((12 - i) * 30f)
            }

            numbersPaint.textSize = numberTextSize

            canvas?.drawText(
                text,
                (-textBound.width() / 2).toFloat(),
                (textBound.height() / 2).toFloat(),
                numbersPaint)

            canvas?.restore()
            canvas?.rotate(-30f)
        }
    }

    private fun drawHands(canvas: Canvas?) {

        calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)

        hourHandRotateDegree = (hour + minute * 1f/60f + second * 1f/3600f) * 30f
        minuteHandRotateDegree = (minute + second * 1f/60f) * 6f
        secondHandRotateDegree = second * 6f

        canvas?.save()
        handsPaint.strokeWidth = circleRadius/20f
        handsPaint.color = hourHandColor
        canvas?.rotate(hourHandRotateDegree)
        canvas?.drawLine(
            hourHandStartPoint.x,
            hourHandStartPoint.y,
            hourHandEndPoint.x,
            hourHandEndPoint.y,
            handsPaint
        )
        canvas?.restore()

        canvas?.save()
        handsPaint.strokeWidth = circleRadius/25f
        handsPaint.color = minuteHandColor
        canvas?.rotate(minuteHandRotateDegree)
        canvas?.drawLine(
            minuteHandStartPoint.x,
            minuteHandStartPoint.y,
            minuteHandEndPoint.x,
            minuteHandEndPoint.y,
            handsPaint
        )
        canvas?.restore()

        canvas?.save()
        handsPaint.strokeWidth = circleRadius/60f
        handsPaint.color = secondHandColor
        canvas?.rotate(secondHandRotateDegree)
        canvas?.drawLine(
            secondHandStartPoint.x,
            secondHandStartPoint.y,
            secondHandEndPoint.x,
            secondHandEndPoint.y,
            handsPaint
        )
        canvas?.restore()

        circlePaint.color = Color.WHITE
        canvas?.drawCircle(0f, 0f, circleRadius/30, circlePaint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if ((event!!.x - clockPointCenter.x)*(event.x - clockPointCenter.x) +
            (event.y - clockPointCenter.y)*(event.y - clockPointCenter.y) <= circleRadius*circleRadius) {
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                circleContentRadius-=5
            }
            if (event.actionMasked == MotionEvent.ACTION_UP) {
                circleContentRadius+=5
            }
            super.onTouchEvent(event)
        }
        return true
    }
}