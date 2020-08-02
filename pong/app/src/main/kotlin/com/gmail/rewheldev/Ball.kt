package com.gmail.rewheldev

import android.graphics.RectF
import timber.log.Timber
import kotlin.math.abs


class Ball(screenX: Int) {
    private var mXVelocity: Float = 0f
    private var mYVelocity: Float = 0f
    private val mBallWidth: Float
    private val mBallHeight: Float
    val mRect: RectF

    init {
        mBallWidth = (screenX / 100).toFloat()
        mBallHeight = (screenX / 100).toFloat()
        mRect = RectF()
    }

    fun update(fps: Long) {
        if (fps <= 0) return
        // Переместить шар на основе
        // горизонтальный (mXVelocity) и
        // вертикальная (mYVelocity) скорость
        // и текущая частота кадров (кадр / с)

        // Move the top left corner
        mRect.left = mRect.left + mXVelocity / fps
        mRect.top = mRect.top +  mYVelocity / fps
// Match up the bottom right corner
// based on the size of the ball
        mRect.right = mRect.left + mBallWidth
        mRect.bottom = mRect.top + mBallHeight

       /* Timber.d(
            "mRect.left %s, mRect.top %s, mRect.right %s, mRect.bottom %s",
            mRect.left,
            mRect.top,
            mRect.right,
            mRect.bottom
        )*/
    }

    // Reverse the vertical direction of travel
    fun reverseYVelocity() {
        mYVelocity = -mYVelocity
    }

    // Reverse the horizontal direction of travel
    fun reverseXVelocity() {
        mXVelocity = -mXVelocity
    }

    fun reset(x: Int, y: Int) {
// Initialise the four points of
// the rectangle which defines the ball
        mRect.left = x / 2.toFloat()
        mRect.top = 0f
        mRect.right = x / 2 + mBallWidth
        mRect.bottom = mBallHeight
        // How fast will the ball travel
// You could vary this to suit
// You could even increase it as the game progresses
// to make it harder
        mYVelocity = -(y / 3).toFloat()
        mXVelocity = (y / 3).toFloat()
    }

    fun increaseVelocity() {
// increase the speed by 10%
        mXVelocity *= 1.1f
        mYVelocity *= 1.1f
    }

    // Отскок мяча назад на основе
    // попадает ли он в левую или правую сторону
    fun batBounce(batPosition: RectF) {
        // Обнаружить центр летучей мыши
        val batCenter = batPosition.left +
                batPosition.width() / 2

        // определить центр шара
        val ballCenter = mRect.left +
                mBallWidth / 2
        // Где на летучей мыши ударил мяч?
        val relativeIntersect = batCenter - ballCenter

        // Выберите направление отскока
        mXVelocity = if (relativeIntersect < 0) {
            // Иди направо
            abs(mXVelocity)
            // Math.abs - это статический метод, который
            // удаляет любые отрицательные значения из значения.
            // Таким образом, -1 становится 1, а 1 остается как 1
        } else {
            //Иди налево
            -abs(mXVelocity)
        }
        // Подсчитав влево или вправо для
        // горизонтальное направление просто перевернуть
        // вертикальное направление для возврата
        // экран
        reverseYVelocity()
    }
}