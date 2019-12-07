package com.example.speedyracer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;


public class MySpeedView extends View {

    private static final float RADIUS = 30f;
    private static final float X_LEFT = 100f;
    private static final float Y_TOP = 100;
    private static final float X_RIGHT = 900f;
    private static final float Y_BOTTOM = 900f;
    private static final float STROKE_WIDTH = 30f;
    private static final float FONT_SIZE = 32f;
    private static final float START_ANGLE = -180f;
    private static final float MAX_PROGRESS = 200f;
    private static final Paint TEXT_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint ARC_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint ARROW_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint CIRCLE_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final int MAX_SPEED = 200;
    private static final int MIN_SPEED = 0;
    private static final float MAX_ANGLE = 180;
    private RectF mArcBounds;
    private RectF mTextBounds;
    private Path mArrowPath;

    private int mProgress;

    public int getProgress() {
        return mProgress;
    }


    public void setProgress(int mProgress) {
        this.mProgress = mProgress;
        invalidate();
    }

    public MySpeedView(Context context) {
        super(context);
        init();
    }

    public MySpeedView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MySpeedView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mArcBounds = new RectF(X_LEFT, Y_TOP, X_RIGHT, Y_BOTTOM);

        ARC_PAINT.setShader(createShader());
        ARC_PAINT.setStrokeWidth(STROKE_WIDTH);
        ARC_PAINT.setStyle(Paint.Style.STROKE);
        ARC_PAINT.setStrokeCap(Paint.Cap.ROUND);

        ARROW_PAINT.setColor(Color.BLACK);
        ARROW_PAINT.setStrokeWidth(STROKE_WIDTH / 10f);
        ARROW_PAINT.setStyle(Paint.Style.FILL_AND_STROKE);

        TEXT_PAINT.setColor(Color.BLACK);
        TEXT_PAINT.setStyle(Paint.Style.FILL);
        TEXT_PAINT.setTextSize(FONT_SIZE);

        CIRCLE_PAINT.setColor(Color.BLACK);
        CIRCLE_PAINT.setStrokeWidth(STROKE_WIDTH / 10f);
        CIRCLE_PAINT.setStyle(Paint.Style.FILL_AND_STROKE);

    }

    public void drawArrow(Canvas canvas) {

        Path arrowPath = new Path();
        float currentAngle = START_ANGLE + (MAX_ANGLE * mProgress / MAX_PROGRESS);
        final float x = mArcBounds.centerX();
        final float y = mArcBounds.centerY();
        arrowPath.moveTo(x, y - STROKE_WIDTH / 5f);
        arrowPath.lineTo(x - 3f * X_LEFT, y);
        arrowPath.lineTo(x, y + STROKE_WIDTH / 5f);
        arrowPath.close();

        Matrix mMatrix = new Matrix();
        RectF bounds = new RectF();
        arrowPath.computeBounds(bounds, true);
        mMatrix.postRotate(currentAngle + START_ANGLE, x, y);
        arrowPath.transform(mMatrix);

        canvas.drawPath(arrowPath, ARROW_PAINT);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(mArcBounds, START_ANGLE, MAX_ANGLE, false, ARC_PAINT);
        final float cx = mArcBounds.centerX();
        final float cy = mArcBounds.centerY();
        canvas.drawCircle(cx, cy, RADIUS, CIRCLE_PAINT);
        drawArrow(canvas);

    }

    private Shader createShader() {
        LinearGradient shader = new LinearGradient(X_LEFT, Y_BOTTOM, X_RIGHT, Y_BOTTOM, new int[]{Color.GREEN, Color.YELLOW, Color.YELLOW, Color.RED}, null, Shader.TileMode.CLAMP);
        return shader;
    }

//    private void getTextBounds(@NonNull String progressString) {
//        TEXT_PAINT.getTextBounds(progressString, 0, progressString.length(), mTextBounds);
//    }


    private void drawText(Canvas canvas) {
        final String progressString = formatString(mProgress);
//        getTextBounds(progressString);
        float x = mArcBounds.width() / 2f - mTextBounds.width() / 2f - mTextBounds.left + mArcBounds.left;
        float y = mArcBounds.height() + mTextBounds.height() / 2f - mTextBounds.bottom + mArcBounds.top;
        canvas.drawText(progressString, x, y, TEXT_PAINT);
    }

    private String formatString(int progress) {
        return String.format("%d km/h", progress);
    }


}
