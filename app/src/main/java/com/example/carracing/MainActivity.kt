package com.example.carracing

import android.content.Intent
import android.graphics.Point
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : AppCompatActivity() {

    private lateinit var soundPool: SoundPool
    private var sound: Int = 0
    private var isMute: Boolean = false
    private lateinit var volCtrl: ImageView

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        volCtrl = findViewById(R.id.volumeCtrl)

        val audioAttributes = AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_GAME).build()
        soundPool = SoundPool.Builder().setAudioAttributes(audioAttributes).build()
        sound = soundPool.load(this, R.raw.mainmusic, 1)
        val prefs = getSharedPreferences("game", MODE_PRIVATE)

        isMute = prefs.getBoolean("isMute", false)

        findViewById<TextView>(R.id.ready).setOnClickListener(View.OnClickListener {
            soundPool.release()
            startActivity(Intent(this, RaceActivity::class.java))
        })

        if (isMute) {
            soundPool.pause(sound)
            volCtrl.setImageResource(R.drawable.baseline_volume_off_24)
        }
        else {
            soundPool.play(sound, 1F, 1F, 0, -1, 1F)
            volCtrl.setImageResource(R.drawable.baseline_volume_up_24)
        }

        volCtrl.setOnClickListener(View.OnClickListener {
            isMute = !isMute
            if (isMute) {
                soundPool.pause(sound)
                volCtrl.setImageResource(R.drawable.baseline_volume_off_24)
            }
            else {
                soundPool.play(sound, 1F, 1F, 0, -1, 1F)
                volCtrl.setImageResource(R.drawable.baseline_volume_up_24)
            }

            val editor = prefs.edit()
            editor.putBoolean("isMute", isMute)
            editor.apply()
        })
        if (!isMute) {
            soundPool.setOnLoadCompleteListener { _, sampleId, status ->
                if (status == 0) {
                    // we could have used media player since this is a long audio.
                    soundPool.play(sampleId, 1F, 1F, 0, -1, 1F)
                }
            }
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onStop() {
        super.onStop()
        soundPool.release()
        this.finish()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    public fun enableEdgeToEdge() {
        window.setDecorFitsSystemWindows(false)
        WindowCompat.getInsetsController(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}