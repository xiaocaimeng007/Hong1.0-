package com.hongbao5656.view.guide;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.hongbao5656.R;

import java.util.HashMap;
import java.util.Map;

/**
 * 遮罩层
 *
 * @author momo
 * @Date 2015/1/22
 */
public class GuideView extends RelativeLayout {
    private static final int DEFAULT_BG = 0xAA000000;
    //左边方向
    public static final int LEFT_TOP = 0;
    public static final int LEFT_CENTER = 1;
    public static final int LEFT_BOTTOM = 2;
    //上边方向
    public static final int TOP_LEFT = 3;
    public static final int TOP_CENTER = 4;
    public static final int TOP_RIGHT = 5;
    //右边方向
    public static final int RIGHT_TOP = 6;
    public static final int RIGHT_CENTER = 7;
    public static final int RIGHT_BOTTOM = 8;
    //下边方向
    public static final int BOTTOM_LEFT = 9;
    public static final int BOTTOM_CENTER = 10;
    public static final int BOTTOM_RIGHT = 11;
    //四个边角居中方向
    public static final int LEFT_TOP_CENTER = 12;
    public static final int RIGHT_TOP_CENTER = 13;
    public static final int LEFT_BOTTOM_CENTER = 14;
    public static final int RIGHT_BOTTOM_CENTER = 15;


    @IntDef(value = {LEFT_TOP, LEFT_CENTER, LEFT_BOTTOM,
            TOP_LEFT, TOP_CENTER, TOP_RIGHT,
            RIGHT_TOP, RIGHT_CENTER, RIGHT_BOTTOM,
            BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT,
            LEFT_TOP_CENTER, RIGHT_TOP_CENTER, LEFT_BOTTOM_CENTER, RIGHT_BOTTOM_CENTER})
    public @interface GuideGravity {
    }

    private final HashMap<View, GuideItem> mGuideItems;
    private PorterDuffXfermode mSrcOutXfermodes;
    private Paint mPaint;

    public GuideView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setId(R.id.guide_container);
        setWillNotDraw(false);
        mGuideItems = new HashMap<>();
        mPaint = new Paint();
        mPaint.setFilterBitmap(false);
        mSrcOutXfermodes = new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_OUT);
    }

    public GuideView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GuideView(Context context) {
        this(context, null, 0);
    }

    /**
     * 初始化引导view
     *
     * @param items
     */
    public void setGuideItems(final HashMap<View, GuideItem> items) {
        if (!mGuideItems.isEmpty()) mGuideItems.clear();
        this.mGuideItems.putAll(items);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mGuideItems.isEmpty()) return;
        int sc = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
                | Canvas.CLIP_TO_LAYER_SAVE_FLAG);
        int width = getWidth();
        int height = getHeight();
        Rect outRect = new Rect();
        int[] location = new int[2];
        Resources resources = getResources();
        for (Map.Entry<View, GuideItem> entry : mGuideItems.entrySet()) {
            View view = entry.getKey();
            GuideItem item = entry.getValue();
            view.getLocationInWindow(location);
            view.getWindowVisibleDisplayFrame(outRect);
            int x = location[0];
            int y = location[1];
            int padding = item.padding;
            outRect.left = x - padding;
            outRect.top = y - padding;
            outRect.right = x + (0 != item.width ? item.width : view.getWidth()) + padding;
            outRect.bottom = y + (0 != item.height ? item.height : view.getHeight()) + padding;
            canvas.drawBitmap(BitmapFactory.decodeResource(resources, item.maskRes), null, outRect, mPaint);
            //根据方向画引导图
            drawGuideInfoBitmap(canvas, outRect, item);
        }
        mPaint.setXfermode(mSrcOutXfermodes);
        mPaint.setColor(DEFAULT_BG);
        canvas.drawRect(0, 0, width, height, mPaint);
        mPaint.setXfermode(null);
        canvas.restoreToCount(sc);

        //绘引导信息
        for (Map.Entry<View, GuideItem> entry : mGuideItems.entrySet()) {
            View view = entry.getKey();
            GuideItem item = entry.getValue();
            view.getLocationInWindow(location);
            view.getWindowVisibleDisplayFrame(outRect);
            int x = location[0];
            int y = location[1];
            int padding = item.padding;
            outRect.left = x - padding;
            outRect.top = y - padding;
            outRect.right = x + (0 != item.width ? item.width : view.getWidth()) + padding;
            outRect.bottom = y + (0 != item.height ? item.height : view.getHeight()) + padding;
            //根据方向画引导图
            drawGuideInfoBitmap(canvas, outRect, item);
        }
    }

    /**
     * 绘引导信息图片
     *
     * @param canvas
     * @param item
     */
    private void drawGuideInfoBitmap(Canvas canvas, Rect rectF, GuideItem item) {
        Bitmap guideBitmap = BitmapFactory.decodeResource(getResources(), item.guideRes);
        int width = 0 != item.infoWidth ? item.infoWidth : guideBitmap.getWidth();
        int height = 0 != item.infoHeight ? item.infoHeight : guideBitmap.getHeight();
        int gravity = item.gravity;
        RectF guideRectF = new RectF();
        switch (gravity) {
            case LEFT_TOP:
                guideRectF.left = rectF.left - width;
                guideRectF.top = rectF.top;
                guideRectF.right = rectF.left;
                guideRectF.bottom = rectF.top + height;
                break;
            case LEFT_CENTER:
                guideRectF.left = rectF.left - width;
                guideRectF.top = rectF.top + (rectF.height() - height >> 1);
                guideRectF.right = rectF.left;
                guideRectF.bottom = rectF.top + (rectF.height() - height >> 1) + height;
                break;
            case LEFT_BOTTOM:
                guideRectF.left = rectF.left - width;
                guideRectF.top = rectF.bottom - height;
                guideRectF.right = rectF.left;
                guideRectF.bottom = rectF.bottom;
                break;

            case RIGHT_TOP:
                guideRectF.left = rectF.right;
                guideRectF.top = rectF.top;
                guideRectF.right = rectF.right + width;
                guideRectF.bottom = rectF.top + height;
                break;
            case RIGHT_CENTER:
                guideRectF.left = rectF.right;
                guideRectF.top = rectF.top + (rectF.height() - height >> 1);
                guideRectF.right = rectF.right + width;
                guideRectF.bottom = rectF.top + (rectF.height() - height >> 1) + height;
                break;
            case RIGHT_BOTTOM:
                guideRectF.left = rectF.right;
                guideRectF.top = rectF.bottom - height;
                guideRectF.right = rectF.right + width;
                guideRectF.bottom = rectF.bottom;
                break;

            case TOP_LEFT:
                guideRectF.left = rectF.left;
                guideRectF.top = rectF.top - height;
                guideRectF.right = rectF.left + width;
                guideRectF.bottom = rectF.top;
                break;
            case TOP_CENTER:
                guideRectF.left = rectF.left + (rectF.width() - width >> 1);
                guideRectF.top = rectF.top - height;
                guideRectF.right = rectF.left + (rectF.width() - width >> 1) + width;
                guideRectF.bottom = rectF.top;
                break;
            case TOP_RIGHT:
                guideRectF.left = rectF.right - width;
                guideRectF.top = rectF.top - height;
                guideRectF.right = rectF.right;
                guideRectF.bottom = rectF.top;
                break;

            case BOTTOM_LEFT:
                //以下左为基准
                guideRectF.left = rectF.left;
                guideRectF.top = rectF.bottom;
                guideRectF.right = rectF.left + width;
                guideRectF.bottom = rectF.bottom + height;
                break;
            case BOTTOM_CENTER:
                guideRectF.left = rectF.left + (rectF.width() - width >> 1);
                guideRectF.top = rectF.bottom;
                guideRectF.right = rectF.left + (rectF.width() - width >> 1) + width;
                guideRectF.bottom = rectF.bottom + height;
                break;
            case BOTTOM_RIGHT:
                //以下左为基准
                guideRectF.left = rectF.right - width;
                guideRectF.top = rectF.bottom;
                guideRectF.right = rectF.right;
                guideRectF.bottom = rectF.bottom + height;
                break;

            case LEFT_TOP_CENTER:
                guideRectF.left = rectF.left - width;
                guideRectF.top = rectF.top - height;
                guideRectF.right = rectF.left;
                guideRectF.bottom = rectF.top;
                break;
            case LEFT_BOTTOM_CENTER:
                guideRectF.left = rectF.left - width;
                guideRectF.top = rectF.bottom;
                guideRectF.right = rectF.left;
                guideRectF.bottom = rectF.bottom + height;
                break;
            case RIGHT_TOP_CENTER:
                guideRectF.left = rectF.right;
                guideRectF.top = rectF.top - height;
                guideRectF.right = rectF.right + width;
                guideRectF.bottom = rectF.top;
                break;
            case RIGHT_BOTTOM_CENTER:
                guideRectF.left = rectF.right;
                guideRectF.top = rectF.bottom;
                guideRectF.right = rectF.right + width;
                guideRectF.bottom = rectF.bottom + height;
                break;
        }
        //添加偏移量
        guideRectF.left += item.xOffset;
        guideRectF.right += item.xOffset;
        guideRectF.top += item.yOffset;
        guideRectF.bottom += item.yOffset;
        mPaint.setColor(Color.WHITE);
        canvas.drawBitmap(guideBitmap, null, guideRectF, mPaint);
    }

    @Override
    protected void onDetachedFromWindow() {
        mGuideItems.clear();
        super.onDetachedFromWindow();
    }

    /**
     * TODO 以波浪消失
     */
    public void startRippleDismiss() {
    }

}
