package com.gmail.rewheldev.app.activities

import android.app.Activity
import android.graphics.Point
import android.os.Bundle
import com.gmail.rewheldev.PongGame
import timber.log.Timber

class PongActivity : Activity() {
    private lateinit var pongGame: PongGame
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        pongGame = PongGame(this, size.x, size.y)
        setContentView(pongGame)
        Timber.d("In onCreate")
    }

    override fun onResume() {
        super.onResume()
        pongGame.resume()
        Timber.d("In onResume")
    }

    override fun onPause() {
        super.onPause()
        pongGame.pause()
        Timber.d("In onPause")
    }
}