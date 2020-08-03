package com.gmail.rewheldev

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import timber.log.Timber


class PongGame(context: Context, x: Int, y: Int) : SurfaceView(context), Runnable {
    // Are we debugging?
    private val DEBUGGING = true
    private val DEBUGGING_DETECT_COLLISIONS = true

    companion object {
        // The number of milliseconds in a second
        private const val MILLIS_IN_SECOND = 1000
    }

    // These objects are needed to do the drawing
    private val mOurHolder: SurfaceHolder = holder
    private var mCanvas: Canvas? = null
    private val mPaint: Paint = Paint()

    // How many frames per second did we get?
    private var mFPS: Long = 1


    // Holds the resolution of the screen
    private val mScreenX = x
    private val mScreenY = y

    // How big will the text be?
    //Шрифт 5% (1/20) от ширины экрана
    private val mFontSize = mScreenX / 20

    //Маржа составляет 1,5% (1/75 от ширины экрана)
    private val mFontMargin = mScreenX / 75

    // The game objects
    private val mBat: Bat = Bat(mScreenX, mScreenY)
    private val mBall: Ball = Ball(mScreenX)

    // The current score and lives remaining
    private var mScore = 0
    private var mLives = 3

    //этот поток будем ипользовать для игры
    private var mGameThread: Thread? = null

    //переменные которые будут сигнализировать нам играет ли игрок
    @Volatile
    private var mPlaying = false
    private var mPaused = false

    init {
        // Everything is ready so start the game
        startNewGame()
        draw()
    }

    private fun startNewGame() {
        // Put the ball back to the starting position
        mBall.reset(mScreenX, mScreenY)
        // Reset the score and the player's chances
        mScore = 0
        mLives = 3
    }

    private fun draw() {
        if (mOurHolder.surface.isValid) {
// Блокируем холст (графическую память) готовым к рисованию
            mCanvas = mOurHolder.lockCanvas()
            //Заполняем экран сплошным цветом
            mCanvas?.drawColor(Color.argb(255, 26, 128, 182))
            // Выберите цвет для рисования
            mPaint.color = Color.argb(255, 255, 255, 255)
            //Нарисовать летучую мышь и мяч
            mCanvas?.let {
                it.drawRect(mBall.mRect, mPaint)
                it.drawRect(mBat.mRect, mPaint)
            }

            //Выберите размер шрифта
            mPaint.textSize = mFontSize.toFloat()
            //рисуем HUD
            mCanvas?.drawText(
                "Score: " + mScore +
                        " Lives : " + mLives, mFontMargin.toFloat(), mFontSize.toFloat(), mPaint
            )

            if (DEBUGGING) {
                printDebuggingText()
            }

// Display the drawing on screen
// unlockCanvasAndPost is a method of SurfaceView
            mOurHolder.unlockCanvasAndPost(mCanvas)
        }
    }

    private fun printDebuggingText() {
        val debugSize = mFontSize / 2
        val debugStart = 150
        mPaint.textSize = debugSize.toFloat()
        mCanvas?.drawText("FPS: $mFPS", 10f, debugStart + debugSize.toFloat(), mPaint)
    }

    override fun run() {
        // mPlaying дает нам более точное управление
        // а не просто полагаться на вызовы для запуска
        // mPlaying должен быть верным И
        // поток, работающий для основного
        // цикл для выполнения
        while (mPlaying) {
            // Сколько сейчас времени в начале цикла?
            val frameStartTime = System.currentTimeMillis()
            // Если игра не остановлена
            // вызвать метод обновления
            if (!mPaused) {
                update()
                // Теперь летучая мышь и мяч находятся в
                // их новые позиции
                // мы можем увидеть, если есть
                // были какие-то столкновения
                detectCollisions()
            }
            // Движение было обработано и столкновения
            // обнаружен теперь мы можем нарисовать сцену
            draw()
            // Сколько времени занимает этот кадр / цикл?
            // Сохраняем ответ в timeThisFrame
            val timeThisFrame =
                System.currentTimeMillis() - frameStartTime
            // Убедитесь, что timeThisFrame составляет не менее 1 миллисекунды
            // потому что случайно деление
            // на ноль вылетает игра
            if (timeThisFrame > 0) {
                // Сохраняем текущую частоту кадров в mFPS
                // готовы перейти к методам обновления
                // mBat и mBall следующий кадр / цикл
                mFPS = MILLIS_IN_SECOND / timeThisFrame
            }
        }
    }

    private fun detectCollisions() {
        // Летучая мышь ударила по мячу?
        if (RectF.intersects(mBat.mRect, mBall.mRect)) {
// Realistic-ish bounce
            mBall.batBounce(mBat.mRect)
            mBall.increaseVelocity()
            mScore++
            //mSP.play(mBeepID, 1, 1, 0, 0, 1)
        }

        // Мяч попал в край экрана

        // Низ
        if (mBall.mRect.bottom > mScreenY) {
            if (DEBUGGING_DETECT_COLLISIONS) {
                Timber.d("mBall.mRect.bottom > mScreenY =  ${mBall.mRect.bottom > mScreenY}")
                Timber.d("mBall.mRect.bottom =  ${mBall.mRect.bottom}")
                Timber.d("mScreenY =  $mScreenY")
            }

            mBall.reverseYVelocity()
            mLives--
            if (mLives == 0) {
                mPaused = true
                startNewGame()
            }
        }

        // Верхний
        if (mBall.mRect.top < 0) {
            if (DEBUGGING_DETECT_COLLISIONS) {
                Timber.d("mBall.mRect.top < 0 =  ${mBall.mRect.top < 0}")
                Timber.d("mBall.mRect.top =  ${mBall.mRect.top}")
                Timber.d("mScreenY =  $mScreenY")
            }
            mBall.reverseYVelocity()
        }
        // Налево
        if (mBall.mRect.left < 0) {
            if (DEBUGGING_DETECT_COLLISIONS) {
                Timber.d("mBall.mRect.left < 0 =  ${mBall.mRect.left < 0}")
                Timber.d("mBall.mRect.left =  ${mBall.mRect.left}")
                Timber.d("mScreenX =  $mScreenX")
            }

            mBall.reverseXVelocity()
        }
        // Направо
        if (mBall.mRect.right > mScreenX) {
            if (DEBUGGING_DETECT_COLLISIONS) {
                Timber.d("mBall.mRect.right > mScreenX =  ${mBall.mRect.right > mScreenX}")
                Timber.d("mBall.mRect.right =  ${mBall.mRect.right}")
                Timber.d("mScreenX =  $mScreenX")
            }
            mBall.reverseXVelocity()
        }
    }

    // Обновляем позицию мяча.
    // Вызывается каждый кадр / цикл
    private fun update() {
        mBall.update(mFPS)
        mBat.update(mFPS)
    }

    fun pause() {
        mPlaying = false
        try {
            mGameThread?.join()
        } catch (e: InterruptedException) {
            Timber.e("Error: joining thread")
        }
    }

    fun resume() {
        mPlaying = true

        mGameThread = Thread(this)
        mGameThread?.start()
    }

    override fun onTouchEvent(motionEvent: MotionEvent?): Boolean {
        when (motionEvent?.action?.and(MotionEvent.ACTION_MASK)) {
            MotionEvent.ACTION_DOWN -> {
                // If the game was paused unpause
                mPaused = false
                // Where did the touch happen
                if (motionEvent.x > mScreenX / 2) {
                    // On the right hand side
                    mBat.setMovementState(mBat.RIGHT)
                } else {
                    // On the left hand side
                    mBat.setMovementState(mBat.LEFT)
                }
            }
            // The player lifted their finger
            // from anywhere on screen.
            // It is possible to create bugs by using
            // multiple fingers. We will use more
            // complicated and robust touch handling
            // in later projects
            MotionEvent.ACTION_UP -> {
                // Stop the bat moving
                mBat.setMovementState(mBat.STOPPED)
            }


        }
        return true
    }
}