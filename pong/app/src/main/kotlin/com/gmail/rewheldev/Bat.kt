package com.gmail.rewheldev

import android.graphics.RectF


class Bat(sx: Int, sy: Int) {
    val mRect: RectF
    var mScreenX = sx
    var mLength: Float = (mScreenX / 8).toFloat()
    var mXCoord = 0f
    var mBatSpeed = 0f

    // These variables are public and final
    // They can be directly accessed by
    // the instance (in PongGame)
    // because they are part of the same
    // package but cannot be changed
    val STOPPED = 0
    val LEFT = 1
    val RIGHT = 2

    // Keeps track of if and how the ball is moving
    // Starting with STOPPED
    private var mBatMoving = STOPPED

    init {

        // One fortieth the screen height
        val height = (sy / 40).toFloat()
        // Configure the starting location of the bat
// Roughly the middle horizontally

        // Configure the starting location of the bat
// Roughly the middle horizontally
        mXCoord = mScreenX / 2.toFloat()
// The height of the bat
// off the bottom of the screen
// The height of the bat
// off the bottom of the screen
        val mYCoord = sy - height
// Initialize mRect based on the size and position
// Initialize mRect based on the size and position
        mRect = RectF(
            mXCoord, mYCoord,
            mXCoord + mLength,
            mYCoord + height
        )
// Configure the speed of the bat
// This code means the bat can cover the
// width of the screen in 1 second
// Configure the speed of the bat
// This code means the bat can cover the
// width of the screen in 1 second
        mBatSpeed = mScreenX.toFloat()
    }

    // Update the movement state passed
    // in by the onTouchEvent method
    fun setMovementState(state: Int) {
        mBatMoving = state
    }

    // Update the bat- Called each frame/loop
    fun update(fps: Long) {
// Move the bat based on the mBatMoving variable
// and the speed of the previous frame
        if (mBatMoving == LEFT) {
            mXCoord = mXCoord - mBatSpeed / fps
        }
        if (mBatMoving == RIGHT) {
            mXCoord = mXCoord + mBatSpeed / fps
        }
// Stop the bat going off the screen
        if (mXCoord < 0) {
            mXCoord = 0F
        } else if (mXCoord + mLength > mScreenX) {
            mXCoord = mScreenX - mLength
        }
// Update mRect based on the results from
// the previous code in update
        mRect.left = mXCoord
        mRect.right = mXCoord + mLength
    }
}