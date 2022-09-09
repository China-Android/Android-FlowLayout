package com.example.androidflowlayoutlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collection;
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
    private int textBackground;
    private int textDrawableLeft;
    private List<FlowDataBean> mData = new ArrayList<>();
    private List<List<View>> mLines = new ArrayList<>();//代表我们的行

    public MyFlowLayout(Context context) {
        this(context, null);
    }

    public MyFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        DEFAULT_HORIZONTAL_MARGIN = dptopx.dip2px(context, 5f);//元素之间的水平间距
        DEFAULT_VERTICAL_MARGIN = dptopx.dip2px(context, 5f);//元素之间的竖直间距
        DEFAULT_TEXT_MAX_LENGTH = -1;//元素里面的字体长度限制
        DEFAULT_BORDER_RADIUS = dptopx.dip2px(context, 5f);//元素外面的边框弧度
        //获取属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        mMaxLines = a.getInt(R.styleable.FlowLayout_maxLines, DEFAULT_LINE);
        if (mMaxLines != -1 && mMaxLines < 1) {
            throw new IllegalArgumentException("行数不能小于1");
        }
        mHorizontalMargin = a.getDimension(R.styleable.FlowLayout_itemHorizontalMargin, DEFAULT_HORIZONTAL_MARGIN);
        mVerticalMargin = a.getDimension(R.styleable.FlowLayout_itemVerticalMargin, DEFAULT_VERTICAL_MARGIN);
        mTextMaxLength = a.getInt(R.styleable.FlowLayout_textMaxLength, DEFAULT_TEXT_MAX_LENGTH);
        textBackground = a.getResourceId(R.styleable.FlowLayout_textBackground, 0);
        textDrawableLeft = a.getResourceId(R.styleable.FlowLayout_textDrawableLeft, 0);
        if (mTextMaxLength < 1 && mTextMaxLength != DEFAULT_TEXT_MAX_LENGTH) {
            throw new IllegalArgumentException("字数不能小于0");
        }
        mTextColor = a.getColor(R.styleable.FlowLayout_textColor, getResources().getColor(R.color.text_grey));
//        mBorderColor = a.getColor(R.styleable.FlowLayout_boderColor, getResources().getColor(R.color.text_grey));
//        mBorderRadius = a.getDimension(R.styleable.FlowLayout_borderRadio, DEFAULT_BORDER_RADIUS);
        a.recycle();
    }



    /**
     * 1.onMeasure属于测量第一阶段
     * 这两个值来源于父控件，也就是最外层的布局 拆分是包含一个是值一个是模式，其实里面是类型用高2位来表示模式
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

    /**
     * 1.属于第二阶段，用来测试量每一个item里面孩子的位置
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildAt(0)==null){
            return;
        }
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

    public void setTextList(Collection data) {
        this.mData.clear();
        this.mData.addAll(data);
        //根据数据创建子View并且添加进来
        setUpChildren();
    }

    private void setUpChildren() {
        //清空原有的内容
        removeAllViews();
        //添加子View进来
        for (int i = 0; i < mData.size(); i++) {
            int finalI = i;
            FrameLayout fl = (FrameLayout) LayoutInflater.from(getContext()).inflate(R.layout.item_flow_text, this, false);
            TextView textView = fl.findViewById(R.id.tv_item);
            TextView tvClose = fl.findViewById(R.id.tv_close);
            if (mTextColor > 0) {
                textView.setTextColor(mTextColor);
            }
            if (textBackground > 0) {
                textView.setBackgroundResource(textBackground);
            }

            if (textDrawableLeft > 0) {
                Drawable drawable = ContextCompat.getDrawable(getContext(), textDrawableLeft);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//设置drawable显示在（left, top) 和（right, bottom)构成的矩形中
                textView.setCompoundDrawablePadding(10);
                textView.setCompoundDrawables(drawable, null, null, null);
            }else if (mData.get(finalI).getDrawableId()>0){
                Drawable drawable = ContextCompat.getDrawable(getContext(), mData.get(finalI).getDrawableId());
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//设置drawable显示在（left, top) 和（right, bottom)构成的矩形中
                textView.setCompoundDrawablePadding(10);
                textView.setCompoundDrawables(drawable, null, null, null);
            }

            if (mTextMaxLength != DEFAULT_TEXT_MAX_LENGTH) {
                //设置TextView的最长内容
                textView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mTextMaxLength)});
            }
            String item = mData.get(i).getItemText();
            textView.setText(item);


            //条目点击事件
            textView.setOnClickListener(v -> {
                if (mOnClickItemListener != null)
                    mOnClickItemListener.onItemClick(v, item, finalI);
            });

            //删除按钮点击事件
            tvClose.setOnClickListener(view -> {
                //更新数据，重新渲染
                removeAllViews();
                mData.remove(finalI);
                setUpChildren();
            });

            //条目长按事件
            textView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //展示当前x，关闭其他条目显示x
                    tvClose.setVisibility(VISIBLE);
                    for (int j = 0; j < mData.size(); j++) {
                        if (j!=finalI){
                            FrameLayout flTemp = (FrameLayout) getChildAt(j);
                            flTemp.findViewById(R.id.tv_close).setVisibility(GONE);
                        }
                    }

                    return false;
                }
            });
            //设置子View的相关属性....
            addView(fl);
        }
    }

    private OnClickItemListener mOnClickItemListener;
    private OnClickLongDelItemListener mOnClickLongDelItemListener;

    /**
     * 设置每个条目的点击事件
     *
     * @param listener
     */
    public void setOnClickItemListener(OnClickItemListener listener) {
        this.mOnClickItemListener = listener;
    }

    public void OnClickLongDelItemListener(OnClickLongDelItemListener longListener) {
        this.mOnClickLongDelItemListener = longListener;
    }
    public interface OnClickItemListener {
        void onItemClick(View v, String text, int pos);
    }

    public interface OnClickLongDelItemListener {
        void onItemDelClick(View v,String text,int pos);
    }

    /**
     * 获取显示控件的最大行数
     *
     * @return
     */
    public int getMaxLines() {
        return mMaxLines;
    }

    /**
     * 设置显示控件的最大行数
     *
     * @param mMaxLines
     */
    public void setMaxLines(int mMaxLines) {
        this.mMaxLines = mMaxLines;
    }

    /**
     * 获取每个条目之间的间距
     */
    public float getHorizontalMargin() {
        return mHorizontalMargin;
    }

    /**
     * 设置每个条目之间的间距
     *
     * @param mHorizontalMargin
     */
    public void setHorizontalMargin(float mHorizontalMargin) {
        this.mHorizontalMargin = mHorizontalMargin;
    }

    /**
     * 获取每行之间的上下间距
     *
     * @return
     */
    public float getVerticalMargin() {
        return mVerticalMargin;
    }

    /**
     * 设置每行之间的上下间距
     *
     * @param mVerticalMargin
     */
    public void setVerticalMargin(float mVerticalMargin) {
        this.mVerticalMargin = mVerticalMargin;
    }

    /**
     * 获取字体颜色
     *
     * @return
     */
    public int getTextColor() {
        return mTextColor;
    }

    /**
     * 设置字体颜色
     *
     * @param mTextColor
     */
    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
    }


    public int getTextMaxLength() {
        return mTextMaxLength;
    }

    /**
     * 字体最大长度
     *
     * @param mTextMaxLength
     */
    public void setTextMaxLength(int mTextMaxLength) {
        this.mTextMaxLength = mTextMaxLength;
    }

    public int getTextBackground() {
        return textBackground;
    }

    /**
     * 设置条目背景
     *
     * @param textBackground
     */
    public void setTextBackground(int textBackground) {
        this.textBackground = textBackground;
    }

    /**
     * 获取文字左面图片
     * @return
     */
    public int getTextDrawableLeft() {
        return textDrawableLeft;
    }

    /**
     * 设置文字左面图片
     *
     * @param textDrawableLeft
     */
    public void setTextDrawableLeft(int textDrawableLeft) {
        this.textDrawableLeft = textDrawableLeft;
    }
}