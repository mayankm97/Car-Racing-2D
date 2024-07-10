package com.example.carracing

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class Track(screenX: Int, screenY: Int, res: Resources) {
    var x = 0
    var y = 0
    var speed = 20

    var track: Bitmap = BitmapFactory.decodeResource(res, R.drawable.road_0)
    init {
        track = Bitmap.createScaledBitmap(track, screenX, screenY, false)
    }
}