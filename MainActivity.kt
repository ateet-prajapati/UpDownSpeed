package com.example.updown

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.updown.databinding.UpdownFragmentBinding
import android.app.PendingIntent
import android.graphics.*
import android.os.Build
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
        binding.img.setImageBitmap(bitmap)
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
        val pixels = resources.getDimensionPixelSize(R.dimen.dp_24)
        val paint = Paint()
        //paint.isFakeBoldText = true
        paint.typeface = Typeface.DEFAULT_BOLD//create(Typeface.DEFAULT,800,false)
        paint.isAntiAlias = true
        paint.letterSpacing = -0.1f
        paint.textSize = pixels*0.65F
        paint.textAlign = Paint.Align.CENTER
        val unitsPaint = Paint()
        unitsPaint.isAntiAlias = true
        unitsPaint.textSize = pixels*0.45F // size is in pixels
        unitsPaint.textAlign =Paint.Align.CENTER
        unitsPaint.typeface = Typeface.DEFAULT_BOLD
        val textBounds = Rect()
        paint.getTextBounds(speed, 0, speed.length, textBounds)
        val unitsTextBounds = Rect()
        unitsPaint.getTextBounds(units, 0, units.length, unitsTextBounds)
        val width: Int =
            if (textBounds.width() > unitsTextBounds.width()) textBounds.width() else unitsTextBounds.width()
        val bitmap = Bitmap.createBitmap(pixels, pixels, Bitmap.Config.ARGB_8888 )
        val canvas = Canvas(bitmap)
        //canvas.drawColor(R.color.purple_500)
        canvas.drawText(speed, pixels/2f, pixels*0.6F, paint)
        canvas.drawText(units, pixels/2f, pixels.toFloat(), unitsPaint)

        return bitmap
    }

}
