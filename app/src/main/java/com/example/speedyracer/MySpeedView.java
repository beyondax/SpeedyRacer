package com.example.speedyracer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;


public class MySpeedView extends View {

    private Paint arcPaint;
    private RectF arcRectF;

    private Paint pathPaint;
    private Path path;


    private Paint circlePaint;



    private int mProgress;
    private int maxSpeed;
    private int minSpeed;
    private int currentSpeed;


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

        arcRectF = new RectF(100f, 100f, 900f, 900f);
        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setShader(createShader());
        arcPaint.setStrokeWidth(30);
        arcPaint.setStyle(Paint.Style.STROKE);

        pathPaint = new Paint();
        pathPaint.setAntiAlias(true);
        pathPaint.setStrokeWidth(5);
        pathPaint.setStyle(Paint.Style.STROKE);

        path = new Path();
        path.moveTo(490f, 490f);
        path.lineTo(100f, 880f);
        path.lineTo(510f, 510f);
        path.close();

        pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathPaint.setColor(Color.MAGENTA);
        pathPaint.setStrokeWidth(5);
        pathPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(Color.MAGENTA);
        circlePaint.setStrokeWidth(1);
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);



    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc (arcRectF, 180f, 180f, false, arcPaint);
        canvas.drawCircle(500f, 500f, 20f, circlePaint);

    }

    private Shader createShader() {
        LinearGradient shader = new LinearGradient(100f, 900f, 900f, 900f, new int[] {Color.GREEN, Color.YELLOW, Color.RED}, null, Shader.TileMode.MIRROR);
        return shader;
    }

}
