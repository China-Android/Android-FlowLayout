# Android-SearchFlowLayout
Android搜索标签流式布局，支持最大行数，元素内最大字数的限制，每个条目之间的间距设置，字体颜色，元素边框等，体积小，易上手！！！
直接上效果
![image](https://user-images.githubusercontent.com/65054178/188769198-ba297d3b-1d79-4bc1-bfca-474ca9b51bd8.png)
引入方式：
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
  dependencies {
	        implementation 'com.github.China-Android:Android-SearchFlowLayout:1.0'
	}
  
  //设置每个条目之间的间距
  fl_layout.setHorizontalMargin(10);
  //设置每一行之间的间距
  fl_layout.setVerticalMargin(30);
  //设置字体颜色
  fl_layout.setTextColor(R.color.purple_500);
  //设置列表数据
  fl_layout.setTextList(list);
  //设置每一条最大字显示体个数，超过部分截取不显示
  fl_layout.setTextMaxLength(10);
  //设置点击事件
  fl_layout.setOnClickItemListener((v, text) -> {

  });
  
  对应也可在xml中进行相关属性设置
  
  <attr name="itemHorizontalMargin" format="dimension"/>
  <attr name="itemVerticalMargin" format="dimension"/>
  <attr name="textMaxLength" format="integer"/>
  <attr name="textColor" format="color"/>
  
