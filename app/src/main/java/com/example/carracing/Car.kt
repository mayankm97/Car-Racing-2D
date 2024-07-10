package com.example.carracing

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class Car(res: Resources) {
    var x = 0
    var y = 0
    var movingAhead = false

    var carSpeed = 0
    var car1: Bitmap = BitmapFactory.decodeResource(res, R.drawable.car1)
    var car2: Bitmap = BitmapFactory.decodeResource(res, R.drawable.car2)
    var car3: Bitmap = BitmapFactory.decodeResource(res, R.drawable.car3)
    var car4: Bitmap = BitmapFactory.decodeResource(res, R.drawable.car4)
    var car5: Bitmap = BitmapFactory.decodeResource(res, R.drawable.car5)

    var width = car1.width
    var height = car1.height

    init {
        x = 20
        width = (width / 1.5).toInt()
        height = (height / 1.6).toInt()
        car1 = Bitmap.createScaledBitmap(car1, width, height, false)
        car2 = Bitmap.createScaledBitmap(car2, width, height, false)
        car3 = Bitmap.createScaledBitmap(car3, width, height, false)
        car4 = Bitmap.createScaledBitmap(car4, width, height, false)
        car5 = Bitmap.createScaledBitmap(car5, width, height, false)

    }

    fun getCar(i: Int): Bitmap {
        when (i) {
            0 -> return car1
            1 -> return car2
            2 -> return car3
            3 -> return car4
        }
        return car5
    }

    fun getCarColor(i: Int): String {
        when (i) {
            0 -> return "Orange Car"
            1 -> return "Green Car"
            2 -> return "You"
            3 -> return "Blue Car"
        }
        return "Green Truck"
    }
}