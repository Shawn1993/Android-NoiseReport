package com.noiselab.noisecomplain.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.noiselab.noisecomplain.R;

/**
 * Created by shawn on 24/3/2016.
 */
public class NoiseMeterView extends RelativeLayout {

    private TextView textView;

    private Bitmap progressBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.noise_meter_light_300dp);
    private Bitmap progressCursorBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.noise_meter_cursor_300dp);
    private Paint mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mCursorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF mProgressBitmapRectf;

    private float mStartAngle = 135;
    private float mEndAngle = 405;

    // in Db
    double mVolume = 0;
    double mMaxVolume = 120;
    double mMinVolume = 0;

    private void loadView(Context context) {
        textView = new TextView(context);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setTextColor(Color.WHITE);
        textView.setText("wait");
        textView.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        addView(textView, params);
    }

    // 构造
    public NoiseMeterView(Context context) {
        super(context);
        loadView(context);
    }

    public NoiseMeterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadView(context);
    }

    public NoiseMeterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadView(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Shader shader1 = new BitmapShader(progressBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Shader shader2 = new BitmapShader(progressCursorBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mProgressPaint.setShader(shader1);
        mCursorPaint.setShader(shader2);
        mProgressBitmapRectf = new RectF(0, 0, w, h);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, w / 3);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (progressBitmap != null) {
            float sweepAngle = (float) ((mEndAngle - mStartAngle) * (mVolume - mMinVolume) / (mMaxVolume - mMinVolume));
            canvas.drawArc(mProgressBitmapRectf, mStartAngle - 45, 45 + sweepAngle, true, mProgressPaint);
            canvas.save();
            canvas.rotate(sweepAngle, mProgressBitmapRectf.centerX(), mProgressBitmapRectf.centerY());
            canvas.drawRect(mProgressBitmapRectf, mCursorPaint);
            canvas.restore();
        }
    }

    int delay = 0;

    public void setValue(double volume) {
        if (volume < mMinVolume) {
            volume = mMinVolume;
        } else if (volume > mMaxVolume) {
            volume = mMaxVolume;
        }
        mVolume = volume;
        if (delay == 0) {
            textView.setText(String.valueOf((int) mVolume));
        }
        delay++;
        delay %= 4;
        invalidate();
    }

    public void setMaxValue(double max) {
        mMaxVolume = max;
    }

    public void setMinValue(double min) {
        mMinVolume = min;
    }

}
