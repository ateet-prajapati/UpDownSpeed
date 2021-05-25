package com.example.updown

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Icon
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.updown.databinding.UpdownFragmentBinding
import android.app.PendingIntent
import android.util.Log


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private lateinit var binding: UpdownFragmentBinding
    private val notificationManager by lazy {
        NotificationManagerCompat.from(this)
    }
    private val channel by lazy {
        val name = getString(R.string.app_name)
        val descriptionText = getString(R.string.Speed)
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel("121", name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        notificationManager.createNotificationChannel(channel)
        channel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UpdownFragmentBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //val handlerThread = HandlerThread("data")
        val handler = /*handlerThread.getThreadHandler()*/Handler()
        val data = UpDownData()

        val mBuilder = Notification.Builder(this, channel.id)
            .setOngoing(true)

        handler.post {
            updateData(data, handler, mBuilder)
        }

    }

    private fun updateData(
        data: UpDownData,
        handler: Handler,
        mBuilder: Notification.Builder
    ) {


        data.updateData()

        val up = data.getSpeed(TYPE.TYPE_UP)
        val down = data.getSpeed(TYPE.TYPE_DOWN)
        val total = data.getSpeed(TYPE.TYPE_ALL)


        Log.v(TAG,"$down")
        binding.apply {
            First.text = "Download : ${down.first} ${down.second}"
            Second.text = "Upload : ${up.first} ${up.second}"
        }

        val bitmap = createBitmapFromString("${total.first}",total.second)
        val icon = Icon.createWithBitmap(bitmap)
        mBuilder.setSmallIcon(icon)
        mBuilder.setContentText("${binding.First.text.trim()} \t ${ binding.Second.text.trim()}")
        // mBuilder.setSmallIcon(R.drawable.ic_launcher_background)
        val notification = mBuilder.build()
        notificationManager.notify(121,notification)
        handler.postDelayed({updateData(data, handler, mBuilder)},1000)

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            notificationIntent, 0
        )
    }
    private fun createBitmapFromString(speed: String, units: String): Bitmap? {
        Log.v(TAG,speed)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.textSize = 50F
        paint.textAlign = Paint.Align.CENTER
        val unitsPaint = Paint()
        unitsPaint.isAntiAlias = true
        unitsPaint.textSize = 30F // size is in pixels
        unitsPaint.textAlign =Paint.Align.CENTER
        val textBounds = Rect()
        paint.getTextBounds(speed, 0, speed.length, textBounds)
        val unitsTextBounds = Rect()
        unitsPaint.getTextBounds(units, 0, units.length, unitsTextBounds)
        val width: Int =
            if (textBounds.width() > unitsTextBounds.width()) textBounds.width() else
                unitsTextBounds.width()
        val bitmap = Bitmap.createBitmap(width + 5, 90, Bitmap.Config.ARGB_8888 )
        val canvas = Canvas(bitmap)
        canvas.drawText(speed, (width / 2+3 ).toFloat(), 46F, paint)
        canvas.drawText(units, (width / 2).toFloat(), 80F, unitsPaint)
        return bitmap
    }

}
