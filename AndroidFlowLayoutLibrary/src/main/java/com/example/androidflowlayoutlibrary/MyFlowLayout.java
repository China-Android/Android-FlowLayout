package com.example.androidflowlayoutlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HL
 * @Date 2022/9/6
 */
public class MyFlowLayout extends ViewGroup {

    public static final int DEFAULT_LINE = -1;
    public static int DEFAULT_HORIZONTAL_MARGIN;
    public static int DEFAULT_VERTICAL_MARGIN;
    public static int DEFAULT_TEXT_MAX_LENGTH;
    public static int DEFAULT_BORDER_RADIUS;
    private int mMaxLines;
    private float mHorizontalMargin;
    private float mVerticalMargin;
    private int mTextColor;
    private int mBorderColor;
    private int mTextMaxLength;
    private float mBorderRadius;
    private List<String> mData = new ArrayList<>();
    private List<List<View>> mLines = new ArrayList<>();//代表我们的行

    public MyFlowLayout(Context context) {
        this(context, null);
    }

    public MyFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        DEFAULT_HORIZONTAL_MARGIN = dptopx.dip2px(context,5f);//元素之间的水平间距
        DEFAULT_VERTICAL_MARGIN = dptopx.dip2px(context,5f);//元素之间的竖直间距
        DEFAULT_TEXT_MAX_LENGTH = -1;//元素里面的字体长度限制
        DEFAULT_BORDER_RADIUS = dptopx.dip2px(context,5f);//元素外面的边框弧度
        //获取属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        mMaxLines = a.getInt(R.styleable.FlowLayout_maxLines, DEFAULT_LINE);
        if (mMaxLines != -1 && mMaxLines < 1) {
            throw new IllegalArgumentException("行数不能小于1");
        }
        mHorizontalMargin = a.getDimension(R.styleable.FlowLayout_itemHorizontalMargin, DEFAULT_HORIZONTAL_MARGIN);
        mVerticalMargin = a.getDimension(R.styleable.FlowLayout_itemVerticalMargin, DEFAULT_VERTICAL_MARGIN);
        mTextMaxLength = a.getInt(R.styleable.FlowLayout_textMaxLength, DEFAULT_TEXT_MAX_LENGTH);
        if (mTextMaxLength < 1 && mTextMaxLength != DEFAULT_TEXT_MAX_LENGTH) {
            throw new IllegalArgumentException("字数不能小于0");
        }
        mTextColor = a.getColor(R.styleable.FlowLayout_textColor, getResources().getColor(R.color.text_grey));
        mBorderColor = a.getColor(R.styleable.FlowLayout_boderColor, getResources().getColor(R.color.text_grey));
        mBorderRadius = a.getDimension(R.styleable.FlowLayout_borderRadio, DEFAULT_BORDER_RADIUS);
        a.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        View firstChild = getChildAt(0);
        int currentLeft = (int) mHorizontalMargin + getPaddingLeft();
        int currentTop = (int) mVerticalMargin + getPaddingTop();
        int currentRight = (int) mHorizontalMargin + getPaddingLeft();
        int currentBottom = firstChild.getMeasuredHeight() + (int) mVerticalMargin + getPaddingTop();

        for (List<View> line : mLines) {
            for (View view : line) {
                //布局每一行
                int Width = view.getMeasuredWidth();
                currentRight += Width;
                //判断最右面边界是否超出屏幕
                if (currentRight > getMeasuredWidth() - mHorizontalMargin) {
                    currentRight = getMeasuredWidth() - (int) mHorizontalMargin;
                }
                view.layout(currentLeft, currentTop, currentRight, currentBottom);
                currentLeft = currentRight + (int) mHorizontalMargin;
                currentRight += (int) mHorizontalMargin;
            }
            //换行后左边距清零
            currentLeft = (int) mHorizontalMargin + getPaddingLeft();
            currentRight = (int) mHorizontalMargin + getPaddingLeft();
            currentBottom += firstChild.getMeasuredHeight() + (int) mVerticalMargin;//这里是上下间距都 添加了
            currentTop += firstChild.getMeasuredHeight() + (int) mVerticalMargin;
        }
    }

    /**
     * 这两个值来源于父控件，也就是最外层的线性布局 拆分是包含一个是值一个是模式，其实里面是类型用高2位来表示模式
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);//测量的一个模式
        int parentWidthsize = MeasureSpec.getSize(widthMeasureSpec);//得到一个我们的一个具体数值宽
        int parentHeightsize = MeasureSpec.getSize(heightMeasureSpec);

        //拿到所有孩子的个数
        int childCount = getChildCount();
        if (childCount == 0) {
            return;
        }
        mLines.clear();
        //添加默认行
        List<View> line = new ArrayList<>();
        mLines.add(line);
        int childWidthSpace = MeasureSpec.makeMeasureSpec(parentWidthsize, MeasureSpec.AT_MOST);//孩子的宽 方法里第一个参数代表最大是多少，第二个参数代表孩子的测量模式，表示不超过最大
        int childHeighSpace = MeasureSpec.makeMeasureSpec(parentHeightsize, MeasureSpec.AT_MOST);
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != VISIBLE) {
                continue;
            }
            //测量自己的孩子,后面有两个值 都会走到孩子的 measure方法，放什么view进来就会调用对应view的measure测量方法
            //首先是最外层的线性布局，然后在是里面我们自定义的这个viewgroup，然后又是我们自定义的这个viewgroup里面的TextView
            measureChild(child, childWidthSpace, childHeighSpace);
            if (line.size() == 0) {
                //可以添加,第一个肯定可以添加
                line.add(child);
            } else {
                //不是第一个再判断是否可以继续添加到当前行
                boolean canBeAdd = chackChildCanBeAdd(line, child, parentWidthsize);
                if (!canBeAdd) {
                    //如果行数大于我们设置的最大值，我们就不添加了
                    if (mMaxLines != -1 && mLines.size() >= mMaxLines) {
                        break;
                    }
                    line = new ArrayList<>();
                    mLines.add(line);
                }
                line.add(child);
            }
        }

        //根据尺寸计算行高
        View child = getChildAt(0);
        int childMeasuredHeight = child.getMeasuredHeight();
        int parentHeightTargetSize = mLines.size() * childMeasuredHeight
                + (mLines.size() + 1) * (int) mVerticalMargin
                + getPaddingTop() + getPaddingBottom();//总共的高度
        //然后就是自定义的这个控件的测量自己
        setMeasuredDimension(parentWidthsize, parentHeightTargetSize);
    }

    private boolean chackChildCanBeAdd(List<View> line, View child, int parentWidthsize) {
        //拿到孩子尺寸
        int measuredWidth = child.getMeasuredWidth();
        int totalWidth = (int) mHorizontalMargin + getPaddingLeft();//保存当前行的总宽度
        for (View view : line) {
            totalWidth += view.getMeasuredWidth() + mHorizontalMargin;
        }
        totalWidth += measuredWidth + mHorizontalMargin + getPaddingRight();
        return totalWidth <= parentWidthsize;
    }

    public void setTextList(List<String> data) {
        this.mData.clear();
        this.mData.addAll(data);
        //根据数据创建子View并且添加进来
        setUpChildren();
    }

    private void setUpChildren() {
        //清空原有的内容
        removeAllViews();
        //添加子View进来
        for (final String datum : mData) {
            TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_flow_text, this, false);
            if (mTextMaxLength != DEFAULT_TEXT_MAX_LENGTH) {
                //设置TextView的最长内容
                textView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mTextMaxLength)});
            }
            textView.setText(datum);
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickItemListenr != null)
                        mOnClickItemListenr.onItemClick(v, datum);
                }
            });
            //设置子View的相关属性....
            addView(textView);
        }
    }

    private OnClickItemListenr mOnClickItemListenr;

    public void setOnClickItemListenr(OnClickItemListenr listenr) {
        this.mOnClickItemListenr = listenr;
    }

    public interface OnClickItemListenr {
        void onItemClick(View v, String text);
    }

    public int getmMaxLines() {
        return mMaxLines;
    }

    public void setmMaxLines(int mMaxLines) {
        this.mMaxLines = mMaxLines;
    }

    public float getmHorizontalMargin() {
        return mHorizontalMargin;
    }

    public void setmHorizontalMargin(float mHorizontalMargin) {
        this.mHorizontalMargin = mHorizontalMargin;
    }

    public float getmVerticalMargin() {
        return mVerticalMargin;
    }

    public void setmVerticalMargin(float mVerticalMargin) {
        this.mVerticalMargin = mVerticalMargin;
    }

    public int getmTextColor() {
        return mTextColor;
    }

    public void setmTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
    }

    public int getmBorderColor() {
        return mBorderColor;
    }

    public void setmBorderColor(int mBorderColor) {
        this.mBorderColor = mBorderColor;
    }

    public float getmBorderRadius() {
        return mBorderRadius;
    }

    public void setmBorderRadius(float mBorderRadius) {
        this.mBorderRadius = mBorderRadius;
    }
}