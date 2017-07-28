package com.hongbao5656.view.guide;

import android.support.annotation.DrawableRes;

/**
 * Created by cz on 15/9/17.
 * 引导条目
 */
public class GuideItem {
    public int id;//依附的控件id
    public int list;//列表id
    public int child;//子孩子位置
    public int width;//显示的框宽
    public int height;//显示框高
    public int infoWidth;//引导信息宽
    public int infoHeight;//引导信息高
    @DrawableRes
    public int maskRes;
    @DrawableRes
    public int guideRes;
    public int gravity;
    public int padding;
    public int xOffset;//x轴偏移量
    public int yOffset;//y轴偏移量

    public GuideItem() {
    }
}
