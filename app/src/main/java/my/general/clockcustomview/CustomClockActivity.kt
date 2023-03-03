package my.general.clockcustomview

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CustomClockActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_clock_activity)

        val frameLayout = findViewById<FrameLayout>(R.id.frameLayout)

        val customClock1 = findViewById<CustomClockView>(R.id.customClockView1)
        customClock1.setOnLongClickListener {
            Toast.makeText(applicationContext, "long clicked Custom Clock View 1", Toast.LENGTH_SHORT).show()
            true
        }
        customClock1.setOnClickListener {
            Toast.makeText(applicationContext, "clicked Custom Clock View 1", Toast.LENGTH_SHORT).show()
        }

        val customClock2 = findViewById<CustomClockView>(R.id.customClockView2)
        customClock2.setOnLongClickListener {
            Toast.makeText(applicationContext, "long clicked Custom Clock View 2", Toast.LENGTH_SHORT).show()
            true
        }
        customClock2.setOnClickListener {
            Toast.makeText(applicationContext, "clicked Custom Clock View 2", Toast.LENGTH_SHORT).show()
        }

        val customClock3 = CustomClockView(this)
        val customClock3LayoutParams = FrameLayout.LayoutParams(600, 600)
        customClock3LayoutParams.apply {
            gravity = Gravity.END + Gravity.BOTTOM
            setMargins(20,20,20,20)
        }
        customClock3.apply {
            setPadding(10, 10, 10, 10)
            typeDelimiters = CustomClockView.TypeDelimiters.POINT.ordinal
            ringColor = Color.CYAN
            circleColor = Color.GRAY
            hourHandColor = Color.MAGENTA
            minuteHandColor = Color.MAGENTA
            secondHandColor = Color.BLUE
            enabledSmallDelimiters = false
            layoutParams = customClock3LayoutParams
        }
        frameLayout.addView(customClock3)

        customClock3.setOnLongClickListener {
            Toast.makeText(applicationContext, "long clicked Custom Clock View 3", Toast.LENGTH_SHORT).show()
            true
        }
        customClock3.setOnClickListener {
            Toast.makeText(applicationContext, "clicked Custom Clock View 3", Toast.LENGTH_SHORT).show()
        }
    }
}