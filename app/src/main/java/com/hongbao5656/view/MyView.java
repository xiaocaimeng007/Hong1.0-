package com.hongbao5656.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.hongbao5656.R;

/**
 * 自定义组件的方式
 * 组合现有组件：继承ViewGroup或其子Layout类等布局类进行组合
 * 调整先有组件：继承View的子类具体类
 * 完全自定义：继承View基类，里面界面及事件完全由自己控制
 *
 * 实现自定义组件的方式：
 * 1 配合XML属性资源文件的方式
 * 1.1 values/attrs.xml
 * 将自定义组件类里需要外界传入的属性定义成一个属性文件
 * attr子元素定义具体的属性 format定义该属性的值类型
 * reference 参考指定theme中资源ID 意思就是你传的值可以是引用资源
 * string 字符串 如果你想别人可以既能直接写值也可以用类似“@string/test”引用资源的方式
 * 那么你可以写成 format="string|reference"
 * color 颜色
 * boolean 布尔值
 * dimension 尺寸值
 * float 浮点型
 * integer 整型
 * fraction 百分数
 * enum 枚举
 * <attr name="language">
 * 		<enum name=“china” value="1"/>
 * 	    <enum name="English" value="2"/>
 * </attr>
 * flag 位或运算
 *
 * declare-styleable子元素
 * 每定义一个styleable对象就是一组attr属性的集合
 * 这里的name属性并不是一定要和自定义类名相同
 * 只是为了区分对应类的属性而已
 *
 *上面的属性资源文件定义了该属性后，至于到底是哪一个自定义View组件中来使用该属性
 * 该属性到底能发挥什么作用
 * 就不归改属性资源文件管了
 * 也就是说这个属性资源文件是公共的
 * 大家都可以用 但是为了方便管理
 * 一般都是一个自定义View里的属性写成一个declare-styleable集合
 * 属性资源文件所定义的属性到底可以返回什么作用
 * 取决于自定义组件代码的实现
 *
 * 在自定义类里引用attrs文件里定义的属性为自己的属性赋值
 */
public class MyView extends View {
    private int textColor;
    private float textSize;
    private String text;
    private Paint paint;//画笔

    public MyView(Context context) {//没有属性使用该方法
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {//属性集合   这个多用
        super(context, attrs);
        paint = new Paint();
        //获取资源配置文件中的属性值
        TypedArray arry = context.obtainStyledAttributes(attrs, R.styleable.MyView);
        textColor = arry.getColor(R.styleable.MyView_textColor,0xffffff);
        textSize = arry.getDimension(R.styleable.MyView_textSize,24);
        text = arry.getString(R.styleable.MyView_text);

    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    //视图的绘制事件方法
    @Override
    protected void onDraw(Canvas canvas) {//画布
        super.onDraw(canvas);
        paint.setColor(textColor);
        paint.setTextSize(textSize);

        canvas.drawText("测试",50,50,paint);//x y 方向的偏移量
    }

}
