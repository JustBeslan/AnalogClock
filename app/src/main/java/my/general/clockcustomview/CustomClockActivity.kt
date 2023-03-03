package my.general.clockcustomview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

class CustomClockActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_clock_activity)

        val customClock = findViewById<CustomClockView>(R.id.customClockView)

        customClock.setOnLongClickListener {
            Toast.makeText(applicationContext, "long clicked", Toast.LENGTH_SHORT).show()
            true
        }

        customClock.setOnClickListener {
            Toast.makeText(applicationContext, "clicked", Toast.LENGTH_SHORT).show()
        }
    }
}