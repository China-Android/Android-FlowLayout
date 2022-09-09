package com.example.androidflowlayout;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.androidflowlayoutlibrary.FlowDataBean;
import com.example.androidflowlayoutlibrary.MyFlowLayout;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private MyFlowLayout fl_layout;
    private List<FlowDataBean> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = new ArrayList<>();
        fl_layout = findViewById(R.id.fl);
        FlowDataBean a = new FlowDataBean("陕西潼关肉夹馍", com.example.androidflowlayoutlibrary.R.drawable.a);
        FlowDataBean b = new FlowDataBean("薯条", com.example.androidflowlayoutlibrary.R.drawable.b);
        FlowDataBean c = new FlowDataBean("鸡腿", com.example.androidflowlayoutlibrary.R.drawable.c);
        FlowDataBean d = new FlowDataBean("豪华鸡腿套餐", com.example.androidflowlayoutlibrary.R.drawable.c);
        list.add(a);
        list.add(b);
        list.add(c);
        list.add(b);
        list.add(c);
        list.add(a);
        list.add(d);
        list.add(a);

//        list.add("苹果");
//        list.add("橘子");
//        list.add("梨");
//        list.add("葡萄");
//        list.add("喀什哈密瓜");
//        list.add("新疆哈密瓜");
//        list.add("奇异果");
//        list.add("山东麒麟西瓜");
//        list.add("江苏冰糖砂糖橘");
        fl_layout.setVerticalMargin(30);
        fl_layout.setTextColor(R.color.purple_500);
        fl_layout.setHorizontalMargin(10);
        fl_layout.setTextList(list);
        fl_layout.setOnClickItemListener((v, text,pos) -> {

        });
    }
}