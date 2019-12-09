package com.example.speedyracer;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class MySpeedView extends View {

    private static final float RADIUS = 30f;
    private static final float X_LEFT = 100f;
    private static final float Y_TOP = 100;
    private static final float X_RIGHT = 900f;
    private static final float Y_BOTTOM = 900f;
    private static final float STROKE_WIDTH = 30f;
    private static final float FONT_SIZE = 64f;
    private static final float START_ANGLE = -180f;
    private static final float MAX_PROGRESS = 200f;
    private static int[] mArcColorArray = new int[]{
            Color.GREEN, Color.YELLOW, Color.RED
    };

    private static final Paint TEXT_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint ARC_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint ARROW_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint CIRCLE_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);


    private static final float MAX_ANGLE = 180;

    private RectF mArcBounds;
    private Rect mTextBounds = new Rect();


    private int mProgress;
    private int mMaxSpeed;
    private int mMinSpeed;

    @ColorInt
    private int mColorArrow;

    public MySpeedView(Context context) {
        this(context, null, 0);


    }

    public MySpeedView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public int getProgress() {
        return mProgress;
    }


    public void setProgress(int mProgress) {
        this.mProgress = mProgress;
        invalidate();
    }

    public MySpeedView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.mProgress = this.mProgress;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.mProgress = savedState.mProgress;
        invalidate();
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {

        setSaveEnabled(true);

        extractAttributes(context, attrs);

        mArcBounds = new RectF(X_LEFT, Y_TOP, X_RIGHT, Y_BOTTOM);

        ARC_PAINT.setShader(createShader());
        ARC_PAINT.setStrokeWidth(STROKE_WIDTH);
        ARC_PAINT.setStyle(Paint.Style.STROKE);
        ARC_PAINT.setStrokeCap(Paint.Cap.ROUND);

        ARROW_PAINT.setColor(mColorArrow);
        ARROW_PAINT.setStrokeWidth(STROKE_WIDTH / 10f);
        ARROW_PAINT.setStyle(Paint.Style.FILL_AND_STROKE);

        TEXT_PAINT.setColor(Color.BLACK);
        TEXT_PAINT.setStyle(Paint.Style.FILL);
        TEXT_PAINT.setTextSize(FONT_SIZE);

        CIRCLE_PAINT.setColor(mColorArrow);
        CIRCLE_PAINT.setStrokeWidth(STROKE_WIDTH / 10f);
        CIRCLE_PAINT.setStyle(Paint.Style.FILL_AND_STROKE);

    }

    public void drawArrow(Canvas canvas) {

        Path arrowPath = new Path();
        float currentAngle = START_ANGLE + (MAX_ANGLE * mProgress / MAX_PROGRESS);
        final float x = mArcBounds.centerX();
        final float y = mArcBounds.centerY();
        arrowPath.moveTo(x, y - STROKE_WIDTH / 5f);
        arrowPath.lineTo(x - X_RIGHT / 3f, y);
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final String maxProgressString = formatString(mProgress);
        getTextBounds(maxProgressString);
        float desiredWidth = Math.max(mTextBounds.width(), getSuggestedMinimumWidth()) + getPaddingLeft() + getPaddingRight();
        float desiredHeight = Math.max(mTextBounds.height(), getSuggestedMinimumHeight()) + getPaddingTop() + getPaddingBottom();
        int desiredSize = (int) (Math.max(desiredHeight, desiredWidth));
        final int resolvedWidth = resolveSize(desiredSize, widthMeasureSpec);
        final int resolvedHeight = resolveSize(desiredSize, heightMeasureSpec);
        setMeasuredDimension(resolvedWidth, resolvedHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        final int size = Math.min(w, h);
        mArcBounds = new RectF(getPaddingLeft() + STROKE_WIDTH / 2, STROKE_WIDTH / 2 + getPaddingTop(), size - STROKE_WIDTH / 2 - getPaddingRight(), size - STROKE_WIDTH / 2 - getPaddingBottom());
        ARC_PAINT.setShader(new LinearGradient(mArcBounds.left, mArcBounds.bottom, mArcBounds.right, mArcBounds.bottom, mArcColorArray, null, Shader.TileMode.CLAMP));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(mArcBounds, START_ANGLE, MAX_ANGLE, false, ARC_PAINT);
        final float cx = mArcBounds.centerX();
        final float cy = mArcBounds.centerY();
        canvas.drawCircle(cx, cy, RADIUS, CIRCLE_PAINT);
        drawArrow(canvas);
        drawTextCurrentProgress(canvas);
        drawTextMin(canvas);
        drawTextMax(canvas);

    }

    private Shader createShader() {
        LinearGradient shader = new LinearGradient(X_LEFT, Y_BOTTOM, X_RIGHT, Y_BOTTOM, mArcColorArray, null, Shader.TileMode.CLAMP);
        return shader;

    }

    private void getTextBounds(@NonNull String progressString) {
        TEXT_PAINT.getTextBounds(progressString, 0, progressString.length(), mTextBounds);
    }

    private void drawTextCurrentProgress(Canvas canvas) {
        final String progressString = formatString(mProgress);
        getTextBounds(progressString);
        float x = mArcBounds.width() / 2f - mTextBounds.width() / 2f - mTextBounds.left + mArcBounds.left;
        float y = mArcBounds.bottom / 2f + mTextBounds.height() + mTextBounds.bottom + mArcBounds.top + RADIUS;
        canvas.drawText(progressString, x, y, TEXT_PAINT);
    }

    private void drawTextMin(Canvas canvas) {
        final String minSpeedText = formatString(mMinSpeed);
        getTextBounds(minSpeedText);
        float x = mArcBounds.left + mTextBounds.width() / 2f - mTextBounds.right / 2f - mArcBounds.left;
        float y = mArcBounds.bottom / 2f + mTextBounds.height() + mTextBounds.bottom + mArcBounds.top;
        canvas.drawText(minSpeedText, x, y, TEXT_PAINT);
    }

    private void drawTextMax(Canvas canvas) {
        final String maxSpeedText = formatString(mMaxSpeed);
        getTextBounds(maxSpeedText);
        float x = mArcBounds.right - mTextBounds.width() / 2f - mTextBounds.right / 2f + mArcBounds.left / 2f;
        float y = mArcBounds.bottom / 2f + mTextBounds.height() + mTextBounds.bottom + mArcBounds.top;
        canvas.drawText(maxSpeedText, x, y, TEXT_PAINT);
    }

    private String formatString(int progress) {
        return String.format("%d km/h", progress);
    }


    private void extractAttributes(@NonNull Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            final Resources.Theme theme = context.getTheme();
            final TypedArray typedArray = theme.obtainStyledAttributes(attrs, R.styleable.MySpeedView, 0, R.style.MySpeedViewDefault);
            try {
                mProgress = typedArray.getInt(R.styleable.MySpeedView_progress, 0);
                mMinSpeed = typedArray.getInt(R.styleable.MySpeedView_minSpeed, 0);
                mMaxSpeed = typedArray.getInt(R.styleable.MySpeedView_maxSpeed, 200);
                mColorArrow = typedArray.getColor(R.styleable.MySpeedView_colorArrow, 0);
                int shaderColors = typedArray.getResourceId(R.styleable.MySpeedView_shaderColors, 0);
                mArcColorArray = typedArray.getResources().getIntArray(shaderColors);
            } finally {
                typedArray.recycle();
            }
        }
    }

    static class SavedState extends BaseSavedState {

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        int mProgress;


        public SavedState(Parcel source) {
            super(source);
            mProgress = source.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mProgress);
        }
    }
}
