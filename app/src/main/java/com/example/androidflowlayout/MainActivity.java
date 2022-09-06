package com.example.androidflowlayout;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.androidflowlayoutlibrary.MyFlowLayout;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private MyFlowLayout fl_layout;
    private List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = new ArrayList<>();
        fl_layout = findViewById(R.id.fl);
        list.add("苹果");
        list.add("橘子");
        list.add("梨");
        list.add("葡萄");
        list.add("喀什哈密瓜");
        list.add("新疆哈密瓜");
        list.add("奇异果");
        list.add("山东麒麟西瓜");
        list.add("江苏冰糖砂糖橘");
        fl_layout.setmVerticalMargin(30);
        fl_layout.setTextList(list);
    }
}