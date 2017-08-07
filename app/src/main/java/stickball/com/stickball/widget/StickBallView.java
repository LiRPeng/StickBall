package stickball.com.stickball.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import stickball.com.stickball.R;

/**
 * Created by liruopeng on 2017/8/6.
 */

public class StickBallView extends View {
    private Paint mBallPaint = new Paint();
    private Paint mTextPaint = new Paint();
    private int mBallColor;
    private int mTextColor;
    private int mBallRadius;
    private int mTextSize;
    private String text;
    private Context mContext;
    private float mCireptX;
    private float mCireptY;
    private float mEndCireptX;
    private float mEndCireptY;
    private float textPointX;
    private float textPointY;
    private Path path;
    private boolean isTouch;
    private int radius;
    private boolean isBreak;


    public StickBallView(Context context) {
        this(context, null);
    }

    public StickBallView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickBallView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        setAttribute(attrs, defStyleAttr);
        initPaint();
    }

    private void initCoordinate() {
        float distance = (float) Math.sqrt(Math.pow(mEndCireptY - mCireptY, 2) + Math.pow(mEndCireptX - mCireptX, 2));
        radius = (int) (-distance / 10 + mBallRadius);

        textPointX = mEndCireptX;
        textPointY = mEndCireptY
                + (mTextPaint.getFontMetrics().descent - mTextPaint.getFontMetrics().ascent) / 2
                - mTextPaint.getFontMetrics().bottom;

        float c1x = (mEndCireptX + mCireptX) / 2;
        float c1y = (mEndCireptY + mCireptY) / 2;
        float offsetX = (float) (radius * Math.sin(Math.atan((mCireptY - mEndCireptY) / (mCireptX - mEndCireptX))));
        float offsetY = (float) (radius * Math.cos(Math.atan((mCireptY - mEndCireptY) / (mCireptX - mEndCireptX))));
        float x1 = mCireptX - offsetX;
        float y1 = mCireptY + offsetY;
        float x2 = mEndCireptX - offsetX;
        float y2 = mEndCireptY + offsetY;
        float x3 = mEndCireptX + offsetX;
        float y3 = mEndCireptY - offsetY;
        float x4 = mCireptX + offsetX;
        float y4 = mCireptY - offsetY;
        path = new Path();
        path.moveTo(x1, y1);
        path.quadTo(c1x, c1y, x2, y2);
        path.lineTo(x3, y3);
        path.quadTo(c1x, c1y, x4, y4);
        path.lineTo(x1, y1);
    }

    private void setAttribute(AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = mContext.getTheme().obtainStyledAttributes(attrs, R.styleable.StickBallView, defStyleAttr, 0);
        for (int i = 0; i < ta.getIndexCount(); i++) {
            int attr = ta.getIndex(i);
            switch (attr) {
                case R.styleable.StickBallView_BallColor:
                    mBallColor = ta.getColor(attr, Color.RED);
                    break;
                case R.styleable.StickBallView_TextColor:
                    mTextColor = ta.getColor(attr, Color.WHITE);
                    break;
                case R.styleable.StickBallView_radius:
                    mBallRadius = ta.getDimensionPixelSize(attr, 0);
                    break;
                case R.styleable.StickBallView_text:
                    text = ta.getString(attr);
                    break;
                case R.styleable.StickBallView_TextSize:
                    mTextSize = ta.getDimensionPixelSize(attr, 0);
                    break;
            }
        }
        ta.recycle();
    }

    private void initPaint() {
        Paint paint = mBallPaint;
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(mBallColor);

        paint = mTextPaint;
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setTextSize(mTextSize);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(mTextColor);


    }

    @Override
    public void draw(Canvas canvas) {
        initCoordinate();
        if (!isBreak) {
            canvas.drawCircle(mCireptX, mCireptY, radius, mBallPaint);
            canvas.drawPath(path, mBallPaint);
        }
        canvas.drawCircle(mEndCireptX, mEndCireptY, mBallRadius, mBallPaint);
        canvas.drawText(text, textPointX, textPointY, mTextPaint);
        super.draw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCireptX = getWidth() >> 1;
        mCireptY = getHeight() >> 1;
        mEndCireptX = mCireptX;
        mEndCireptY = mCireptY;
        radius = mBallRadius;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Rect rect = new Rect();
                rect.left = (int) (mCireptX - mBallRadius);
                rect.right = (int) (mCireptX + mBallRadius);
                rect.top = (int) (mCireptY - mBallRadius);
                rect.bottom = (int) (mCireptY + mBallRadius);
                if (rect.contains((int) event.getX(), (int) event.getY())) {
                    isTouch = true;
                }
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                if (isTouch) {
                    mEndCireptX = event.getX();
                    mEndCireptY = event.getY();
                    double d = Math.sqrt((mEndCireptX - mCireptX) * (mEndCireptX - mCireptX)
                            + (mEndCireptY - mCireptY) * (mEndCireptY - mCireptY));
                    if (d > mBallRadius * 6 && !isBreak) {
                        isBreak = true;
                    }
                }
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                isTouch = false;
                isBreak = false;
                mEndCireptX = mCireptX;
                mEndCireptY = mCireptY;
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }
}
