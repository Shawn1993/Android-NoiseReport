package com.noiselab.noisecomplain.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.noiselab.noisecomplain.R;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by shawn on 8/4/2016.
 */
public class NoiseGraphView extends ImageView {

    private final static int MAX_COUNT = 60;
    private final static int GRID_COLUMN = 5;
    private final static int GRID_ROW = 5;
    private double maxValue = 90;
    private double minValue = 0;
    private RectF mBounds;
    LinkedList<Double> mValues = new LinkedList<>();
    double averageValue = 0;
    private long gridOffset = 0;

    private Bitmap lineBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.noise_graph_line_400dp);
    private Bitmap pointBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.noise_graph_point_24dp);
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mCurvePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path mPath = new Path();

    public NoiseGraphView(Context context) {
        super(context);
    }

    public NoiseGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoiseGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addValue(double value) {
        if (mValues.size() < MAX_COUNT) {
            mValues.offer(value);
            averageValue = averageValue + ((value - averageValue) / mValues.size());
        } else {
            mValues.offer(value);
            double headValue = mValues.poll();
            averageValue = averageValue + ((value - headValue) / MAX_COUNT);
        }
        gridOffset++;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBounds = new RectF(0, 0, w, h);
        mBounds.inset(w * 0.05f, h * 0.05f);
        lineBitmap = Bitmap.createBitmap(lineBitmap, 0, 0, (int) mBounds.width(), lineBitmap.getHeight());

        mCurvePaint.setColor(Color.argb(166, 189, 183, 229));
        mCurvePaint.setStrokeWidth(pointBitmap.getWidth() / 4);
        mCurvePaint.setStyle(Paint.Style.STROKE);
        mCurvePaint.setStrokeCap(Paint.Cap.ROUND);
        mCurvePaint.setStrokeJoin(Paint.Join.ROUND);

        mGridPaint.setColor(Color.argb(128, 152, 105, 163));
        mGridPaint.setStyle(Paint.Style.STROKE);
        mGridPaint.setStrokeWidth(1);

        mTextPaint.setTextAlign(Paint.Align.RIGHT);
        mTextPaint.setColor(Color.argb(166, 189, 183, 229));
        mTextPaint.setTextSize(h * 0.05f);
    }

    private int calY(RectF rect, double value) {
        return (int) (rect.bottom - rect.height() * value / (maxValue - minValue));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw the grid
        float h = mBounds.height();
        float rowStep = h / GRID_ROW;
        for (float i = rowStep / 2; i < h; i += rowStep) {
            canvas.drawLine(mBounds.left, mBounds.top + i, mBounds.right, mBounds.top + i, mGridPaint);
        }
        float w = mBounds.width();
        float columnStep = rowStep;
        for (float i = (gridOffset * mBounds.width() / MAX_COUNT) % columnStep; i < w; i += columnStep) {
            canvas.drawLine(mBounds.left + i, mBounds.top, mBounds.left + i, mBounds.bottom, mGridPaint);
        }
        // draw the values
        if (mValues.size() > 1) {
            // draw the values  curve path
            mPath.reset();
            float x1 = mBounds.left;
            float y1 = calY(mBounds, mValues.getLast());
            float x2, x3, y2, y3;
            float step = mBounds.width() / MAX_COUNT;
            mPath.moveTo(x1, y1);
            for (int i = mValues.size() - 2; i >= 0; i--) {
                x3 = x1 + step;
                y3 = calY(mBounds, mValues.get(i));
                x2 = (x1 + x3) / 2;
                y2 = (y1 + y3) / 2;
                mPath.quadTo(x2, y2, x3, y3);
                x1 = x3;
                y1 = y3;
            }
            canvas.drawPath(mPath, mCurvePaint);
            // draw the average line
            canvas.drawBitmap(lineBitmap, mBounds.left, calY(mBounds, averageValue) - lineBitmap.getHeight() / 2.0f, mPaint);
            // draw the head value point
            canvas.drawBitmap(pointBitmap, mBounds.left - pointBitmap.getWidth() / 2, calY(mBounds, mValues.getLast()) - pointBitmap.getHeight() / 2, mPaint);
            canvas.drawText("平均:" + ((int) averageValue) + "dB", mBounds.right, mBounds.bottom, mTextPaint);

        }


    }
}
