# Android-FlowLayout
## 热门标签，搜索记录标签，流式布局，动态计算每一条目的宽度，当标签在本行展示不开自动切换到下一行展示。支持最大行数，元素内最大字数的限制，每个条目之间的间距设置，字体颜色，元素背景，元素文字左边图片标头显示等，体积小，易上手！！！直接上效果


### 引入方式：
<pre><code>
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
	    }
   
dependencies {
	        implementation 'com.github.China-Android:Android-SearchFlowLayout:1.0'
	     }
</code></pre>
![b](https://user-images.githubusercontent.com/65054178/189288873-9cc41b62-2ab0-46c4-aae6-123c08e7cac9.png)
![a](https://user-images.githubusercontent.com/65054178/189288876-b1dc4868-b213-4b81-81c9-baf31161b42b.png)

xml引用控件
 <pre><code>
     < com.example.androidflowlayoutlibrary.MyFlowLayout
        android:id="@+id/fl"
        android:layout_width="match_parent"
        android:layout_height="300dp"
        app:textBackground="@drawable/xxx"
        app:textDrawableLeft="@drawable/xxx"/>
  </code></pre>
  1.设置每个条目之间的间距
  <pre><code>
   fl_layout.setHorizontalMargin(10);
   </code></pre>
  2.设置每一行之间的间距
  <pre><code>
  fl_layout.setVerticalMargin(30);
  </code></pre>
  3.设置字体颜色
  <pre><code>
  fl_layout.setTextColor(R.color.purple_500);
  </code></pre>
  4.设置每一条最大字显示体个数，超过部分截取不显示
  <pre><code>
  fl_layout.setTextMaxLength(10);
  </code></pre>
  5.设置文字左面图片
  <pre><code>
  fl_layout.setTextDrawableLeft(R.drawable.xxx);
  </code></pre>
  6.设置列表数据
  <pre><code>
  fl_layout.setTextList(list);
  </code></pre>
  7.设置点击事件
  <pre><code>
  fl_layout.setOnClickItemListener((v, text) -> {

  });
  </code></pre>
 对应也可在xml中进行相关属性设置
 <pre><code>
  itemHorizontalMargin = "10"
  itemVerticalMargin = "10"
  textMaxLength = "3"
  textColor = "@color/xxx"
  textDrawableLeft="@drawable/c"
 </code></pre>
 
 ## 应用户要求我的流式布局v1.1版本他来了！！！---------------------------------------------------------------------------
 
 # 增加每个条目支持添加不同图片，长按可以删除对应条目
 ![v1 1](https://user-images.githubusercontent.com/65054178/189287992-0488e68c-137c-4cec-a3a7-136a3d2614e1.png)
 ![v1 1_close](https://user-images.githubusercontent.com/65054178/189287995-2fe6ec6c-f891-40fa-b93e-ff85a80adc81.png)
 <br>
数据方法有所改变，接收的是一个FlowDataBean类型的数据集合：itemText代表要显示的文字，drawableId代表要显示图片的id
 </br>
 1.长按删除条目监听
 <pre><code>
 fl_layout.setOnClickLongDelItemListener((v, text, pos) -> {

 });
 </code></pre>
 *注意当在xml单独设置左面图片时，优先级是大于数据集合中类型所设置图片的优先级。
  
