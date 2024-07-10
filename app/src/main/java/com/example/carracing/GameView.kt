package com.example.carracing

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceView
import android.widget.Toast
import kotlin.concurrent.thread
import kotlin.random.Random

class GameView(raceActivity: RaceActivity, screenX: Int, screenY: Int): SurfaceView(raceActivity), Runnable {
    private var track1: Track
    private var track2: Track
    var isPlaying: Boolean ?= null
    var thread: Thread ?= null
    val paint = Paint()
    val screenX: Int
    val screenY: Int
    var car: Array<Car>
    private var random: Random
    var count = 0
    var finishCoordinate = 10
    val prefs: SharedPreferences
    private var raceActivity: RaceActivity
    var isGameOver = false
//    lateinit var winnerToast: Toast

    private var mediaPlayer: MediaPlayer

    init {
        this.raceActivity = raceActivity

        mediaPlayer = MediaPlayer.create(raceActivity, R.raw.enginecutnew)
        mediaPlayer.isLooping = true

        track1 = Track(screenX, screenY, resources)
        track2 = Track(screenX, screenY, resources)

        prefs = raceActivity.getSharedPreferences("game", Context.MODE_PRIVATE)

        this.screenX = screenX
        this.screenY = screenY
        track2.y = -screenY
        car = Array(5){Car(resources)}

        paint.setColor(Color.WHITE)
        paint.strokeWidth = 15f

        var k = 0
        for (i in car.indices) {
            k += i + car[i].width
            car[i].x = k - 30
            car[i].y = screenY - car[i].height - 180
            car[i].getCar(i)
        }
        random = Random

        for (i in car.indices) {
            if (i == 2) continue
            car[i].carSpeed = random.nextInt(45)
            if (car[i].carSpeed < 30) car[i].carSpeed = 30
        }
    }
    override fun run() {
        while (isPlaying == true) {
            update()
            draw()
            sleep()
        }
    }

    fun update() {
        if (isGameOver) {
            isPlaying = false
            waitBeforeExit()
            return
        }
        val trackSpeed = if (car[2].movingAhead) track1.speed * 2 else track1.speed
        if (car[2].movingAhead) {
            track1.y += 4*track1.speed
            track2.y += 4*track1.speed

        } else {
            track1.y += 3 * track1.speed
            track2.y += 3 * track1.speed

        }
        if (track1.track.height - track1.y < 0) track1.y = -screenY
        if (track2.track.height - track2.y < 0) {
            track2.y = -screenY
            count++
        }

        for (i in car.indices) {
            if (i == 2) continue
            car[i].y -= (car[i].carSpeed - trackSpeed)
            if (car[i].y == screenY/2 || car[i].y + car[i].height < 0 || car[i].y + car[i].height > screenY) {

                // You can optionally reassign a new speed, but not every update cycle
                car[i].carSpeed = random.nextInt(45)
                if (car[i].carSpeed < 30) car[i].carSpeed = 30
            }
            if (car[i].y < 0 && car[i].y % screenY == 0) count++
        }
    }
    fun draw() {
        if (holder.surface.isValid) {
            val canvas = holder.lockCanvas()
            canvas.drawBitmap(track1.track, track1.x.toFloat(), track1.y.toFloat(), paint)
            canvas.drawBitmap(track2.track, track2.x.toFloat(), track2.y.toFloat(), paint)

            var carOutOfScreen = false
            if (count >= 3) {
                finishCoordinate = track1.y
                for (i in car.indices) {
                    if (car[i].y < 0) carOutOfScreen = true
                }
                if (!carOutOfScreen)
                    canvas.drawLine(105f, track1.y.toFloat(), screenX.toFloat()-105f, track1.y.toFloat(), paint)
                for (i in car.indices) {
                    if (finishCoordinate != 10 && car[i].y <= finishCoordinate) {
                        mediaPlayer.stop()
                        val carColor = car[i].getCarColor(i)
                        // game is over, race ended
                        post {
                            Toast.makeText(context, "" + carColor + " won", Toast.LENGTH_SHORT).show()
                            //Log.d("hii", "" + carColor)
                        }
                        isGameOver = true
                        return
                    }
                }
            }

            var i = 0
            car.forEach {
                canvas.drawBitmap(it.getCar(i), it.x.toFloat(), it.y.toFloat(), paint)
                i++
            }
            holder.unlockCanvasAndPost(canvas)
        }
    }

//    private fun showWinnerToast(winner: String) {
//        winnerToast.cancel()
//        winnerToast = Toast.makeText(context, winner + " won", Toast.LENGTH_SHORT)
//        winnerToast.show()
//    }

    private fun waitBeforeExit() {
        Thread.sleep(10000)
      //  winnerToast.cancel()
        raceActivity.startActivity(Intent(raceActivity, MainActivity::class.java))
        raceActivity.finish()
    }

    fun sleep() {
        Thread.sleep(8)
    }

    fun pause() {
        thread!!.join()
    }
    fun resume() {
        isPlaying = true
        thread = Thread(this)
        thread!!.start()
    }

    // your car -> 3rd one
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN ->
                if (event.y > screenY/2) {
                    if (!prefs.getBoolean("isMute", false)) {
                        mediaPlayer.start()
                    }
                    car[2].movingAhead = true
                }
            MotionEvent.ACTION_UP ->
                if (event.y > screenY/2) {
                    if (!prefs.getBoolean("isMute", false)) {
                        if (mediaPlayer.isPlaying) {
                            mediaPlayer.pause()
                            mediaPlayer.seekTo(0)
                        }
                    }
                    car[2].movingAhead = false
                }
        }
        return true
    }
}