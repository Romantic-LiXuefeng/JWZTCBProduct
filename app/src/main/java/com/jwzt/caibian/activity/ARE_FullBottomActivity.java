package com.jwzt.caibian.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.chinalwb.are.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.chinalwb.are.AREditor;
import com.chinalwb.are.strategies.VideoStrategy;
import com.chinalwb.are.styles.IARE_Style;

//import static com.chinalwb.are.demo.TextViewActivity.HTML_TEXT;


public class ARE_FullBottomActivity extends Activity  {//implements View.OnClickListener
private android.widget.RelativeLayout rl_keybar;
private android.widget.ImageView record_voice;
private android.widget.LinearLayout ll_font;
private android.widget.LinearLayout select_add;
private ImageView editor_font_bottom0;
private ImageView editor_font_bottom1;
private ImageView editor_font_bottom2;
private ImageView editor_font_bottom3;
private ImageView editor_font_bottom4;
private ImageView editor_font_bottom5;
private ImageView editorfont_0;
private ImageView editorfont_1;
private ImageView editorfont_2;
private ImageView editorfont_15;
private ImageView editorfont_16;
private ImageView editorfont_17;
private ImageView editorfont_18;
private ImageView editorfont_19;
private boolean ISSHOWVOICEKEYBAR;//是否显示了语音模块
private boolean ISSHOWFONTKEYBAR;//是否显示了字体设置模块
private boolean ISSHOWADDKEYBAR;//是否显示添加超链模块
private ImageView editorfont_3;
private ImageView editorfont_4;
private ImageView editorfont_5;
private ImageView editorfont_6;
private ImageView editorfont_7;
private ImageView editorfont_9;
private ImageView editorfont_8;
private ImageView editorfont_10;
private ImageView editorfont_11;
private ImageView editorfont_12;
private ImageView editorfont_13;
private ImageView editorfont_14;

@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_are_full);

       // initView();
        }


//private AREditor arEditor;
//
//private VideoStrategy mVideoStrategy = new VideoStrategy() {
//@Override
//public String uploadVideo(Uri uri) {
//        try {
//        Thread.sleep(3000); // Do upload here
//        } catch (Exception e) {
//        e.printStackTrace();
//        }
//        return "http://www.xx.com/x.mp4";
//        }
//
//@Override
//public String uploadVideo(String videoPath) {
//        try {
//        Thread.sleep(3000);
//        } catch (Exception e) {
//        e.printStackTrace();
//        }
//        return "http://www.xx.com/x.mp4";
//        }
//        };
//
//
//
//private void initView() {
//        this.arEditor = this.findViewById(R.id.areditor);
//        this.arEditor.setVideoStrategy(mVideoStrategy);
//        initView1();
//        com.chinalwb.are.styles.toolbar.ARE_Toolbar mToolbar=  arEditor.getToolbar();
//        java.util.List<com.chinalwb.are.styles.IARE_Style> sStylesList =mToolbar.getStylesList();
//        //   0   表情  1  字体大小   3 B    4  I      5  U     6S   7 ————
//        //  10  |   11 颜色   12 背景颜色    13  超链接       15  （1.2.）    17tab
//        //19 左对齐     20  居中   21  右对齐   22  图片 23  视频
//        System.out.println("sStylesList.szie========="+sStylesList.size());
//        IARE_Style iare_style = sStylesList.get(16);//16（.）
//        iare_style.setListenerForImageView(editorfont_0,0,0);
//        IARE_Style iare_style1 = sStylesList.get(15);
//        iare_style1.setListenerForImageView(editorfont_1,0,0);
//        IARE_Style iare_style2 = sStylesList.get(19);
//        iare_style2.setListenerForImageView(editorfont_2,0,0);
//        IARE_Style iare_style3 = sStylesList.get(10);
//        iare_style3.setListenerForImageView(editorfont_3,0,0);
//
//        IARE_Style iare_style4 = sStylesList.get(3);
//
//        iare_style4.setListenerForImageView(editor_font_bottom0,0,0);
//
//        IARE_Style iare_style5 = sStylesList.get(4);
//        iare_style5.setListenerForImageView(editor_font_bottom1,0,0);
//        IARE_Style iare_style6 = sStylesList.get(5);
//        iare_style6.setListenerForImageView(editor_font_bottom2,0,0);
//        IARE_Style iare_style7 = sStylesList.get(19);
//        iare_style7.setListenerForImageView(editor_font_bottom3,0,0);
//
//        IARE_Style iare_style8 = sStylesList.get(20);
//        iare_style8.setListenerForImageView(editor_font_bottom4,0,0);
//
//
//        IARE_Style iare_style9 =sStylesList.get(1);
//        iare_style9.setListenerForImageView(editorfont_15,12,0);
//
//        IARE_Style iare_style10 =  sStylesList.get(1);
//        iare_style10.setListenerForImageView(editorfont_16,14,0);
//
//        IARE_Style iare_style11 = sStylesList.get(1);
//        iare_style11.setListenerForImageView(editorfont_17,16,0);
//
//        IARE_Style iare_style12 = sStylesList.get(1);
//        iare_style12.setListenerForImageView(editorfont_18,18,0);
//
//        IARE_Style iare_style13 =  sStylesList.get(1);
//        iare_style13.setListenerForImageView(editorfont_19,24,0);
//
//        IARE_Style iare_style14 = sStylesList.get(21);
//        iare_style14.setListenerForImageView(editor_font_bottom5,0,0);
//
//        //颜色
//        IARE_Style iare_style15 = sStylesList.get(11);
//        iare_style15.setListenerForImageView(editorfont_10,0,-16777216);
//
//        IARE_Style iare_style16 = sStylesList.get(11);
//        iare_style16.setListenerForImageView(editorfont_11,0,-6381922);
//
//
//        IARE_Style iare_style17 = sStylesList.get(11);
//        iare_style17.setListenerForImageView(editorfont_12,0,-16537100);
//
//        IARE_Style iare_style18 = sStylesList.get(11);
//        iare_style18.setListenerForImageView(editorfont_13,0,-11751600);
//
//        IARE_Style iare_style19 = sStylesList.get(11);
//        iare_style19.setListenerForImageView(editorfont_14,0,-769226);
//        }
///**
// * 获取控件
// */
//private void initView1() {
//        android.widget.ImageView voice = findViewById(R.id.voice);
//
//        android.widget.ImageView font = findViewById(R.id.font);
//        android.widget.ImageView image = findViewById(R.id.image);
//        android.widget.ImageView add = findViewById(R.id.add);
//        android.widget.ImageView keyboard = findViewById(R.id.keyboard);
//        android.widget.ImageView save = findViewById(R.id.save);
//        rl_keybar = findViewById(R.id.rl_keybar);
//        record_voice = findViewById(R.id.record_voice);
//        voice.setOnClickListener(this);
//        font.setOnClickListener(this);
//        image.setOnClickListener(this);
//        add.setOnClickListener(this);
//        keyboard.setOnClickListener(this);
//        save.setOnClickListener(this);
//        ll_font = findViewById(R.id.ll_font);
//        editorfont_0 = findViewById(R.id.editorfont_0);
//        editorfont_1 = findViewById(R.id.editorfont_1);
//        editorfont_2 = findViewById(R.id.editorfont_2);
//        editorfont_3 = findViewById(R.id.editorfont_3);
//        editorfont_4 = findViewById(R.id.editorfont_4);
//        editorfont_5 = findViewById(R.id.editorfont_5);
//        editorfont_6 = findViewById(R.id.editorfont_6);
//        editorfont_7 = findViewById(R.id.editorfont_7);
//        editorfont_8 = findViewById(R.id.editorfont_8);
//        editorfont_9 = findViewById(R.id.editorfont_9);
//        editorfont_10 = findViewById(R.id.editorfont_10);
//        editorfont_11 = findViewById(R.id.editorfont_11);
//        editorfont_12 = findViewById(R.id.editorfont_12);
//        editorfont_13 = findViewById(R.id.editorfont_13);
//        editorfont_14 = findViewById(R.id.editorfont_14);
//        editorfont_15 = findViewById(R.id.editorfont_15);
//        editorfont_16 = findViewById(R.id.editorfont_16);
//        editorfont_17 = findViewById(R.id.editorfont_17);
//        editorfont_18 = findViewById(R.id.editorfont_18);
//        editorfont_19 = findViewById(R.id.editorfont_19);
//        editor_font_bottom5 = findViewById(R.id.editor_font_bottom5);
//        editor_font_bottom4 = findViewById(R.id.editor_font_bottom4);
//        editor_font_bottom3 = findViewById(R.id.editor_font_bottom3);
//        editor_font_bottom2 = findViewById(R.id.editor_font_bottom2);
//        editor_font_bottom1 = findViewById(R.id.editor_font_bottom1);
//        editor_font_bottom0 = findViewById(R.id.editor_font_bottom0);
//
//
//        select_add = findViewById(R.id.select_add);
//        android.widget.ImageView editor_add_voice = findViewById(R.id.editor_add_voice);
//        android.widget.ImageView editor_add_link = findViewById(R.id.editor_add_link);
//        android.widget.ImageView editor_add_location = findViewById(R.id.editor_add_location);
//        android.widget.ImageView editor_add_tag = findViewById(R.id.editor_add_tag);
//
//        editor_add_voice.setOnClickListener(this);
//        editor_add_link.setOnClickListener(this);
//        editor_add_location.setOnClickListener(this);
//        editor_add_tag.setOnClickListener(this);
//
//        }
//
//
//
//@Override
//protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        this.arEditor.onActivityResult(requestCode, resultCode, data);
//        }
//
//@Override
//public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//        }
//
//@Override
//public boolean onOptionsItemSelected(MenuItem item) {
//        int menuId = item.getItemId();
////        if (menuId == com.chinalwb.are.R.id.action_save) {
////        String html = this.arEditor.getHtml();
////        DemoUtil.saveHtml(this, html);
////        return true;
////        }
////        if (menuId == R.id.action_show_tv) {
////        String html = "";
////        html = this.arEditor.getHtml();
////        Intent intent = new Intent(this, TextViewActivity.class);
////        intent.putExtra(HTML_TEXT, html);
////        startActivity(intent);
////        return true;
////        }
//        return super.onOptionsItemSelected(item);
//        }
//
//@Override
//public void onClick(View view) {
//        switch (view.getId()) {
//
//        case R.id.voice://显示语音
//        setKeyBarState();
//        ISSHOWFONTKEYBAR = false;
//        ISSHOWADDKEYBAR = false;
//        if (!ISSHOWVOICEKEYBAR) {
//
//        ISSHOWVOICEKEYBAR = true;
//        record_voice.setVisibility(View.VISIBLE);
//        rl_keybar.setVisibility(View.VISIBLE);
//        } else {
//        ISSHOWVOICEKEYBAR = false;
//
//        record_voice.setVisibility(View.GONE);
//        rl_keybar.setVisibility(View.GONE);
//        }
//        break;
//        case R.id.font://显示字体设置
//        ISSHOWVOICEKEYBAR = false;
//        ISSHOWADDKEYBAR = false;
//        setKeyBarState();
//        if (!ISSHOWFONTKEYBAR) {
//
//        ISSHOWFONTKEYBAR = true;
//        ll_font.setVisibility(View.VISIBLE);
//        rl_keybar.setVisibility(View.VISIBLE);
//        } else {
//        ISSHOWFONTKEYBAR = false;
//        ll_font.setVisibility(View.GONE);
//        rl_keybar.setVisibility(View.GONE);
//        }
//
//        break;
//        case R.id.image://添加图片
//        break;
//        case R.id.add://显示添加超链接
//
//        ISSHOWVOICEKEYBAR = false;
//        ISSHOWFONTKEYBAR = false;
//        setKeyBarState();
//        if (!ISSHOWADDKEYBAR) {
//        ISSHOWADDKEYBAR = true;
//        rl_keybar.setVisibility(View.VISIBLE);
//        select_add.setVisibility(View.VISIBLE);
//        } else {
//        ISSHOWADDKEYBAR = false;
//        rl_keybar.setVisibility(View.GONE);
//        select_add.setVisibility(View.GONE);
//        }
//        break;
//        case R.id.keyboard://关闭键盘输入
//        break;
////            case R.id.editor_font_bottom0://B
////
////                break;
//        case R.id.editor_font_bottom1://I
//        break;
//        case R.id.editor_font_bottom2://U
//        break;
//        case R.id.editor_font_bottom3://向左对齐
//        break;
//        case R.id.editor_font_bottom4://居中对齐
//        break;
//        case R.id.editor_font_bottom5://向右对齐
//        break;
//        case R.id.editorfont_0://
//        break;
//        case R.id.editorfont_1://
//        break;
//        case R.id.editorfont_2://
//        break;
//        case R.id.editorfont_3://
//        break;
//        case R.id.editorfont_4://
//        break;
//        case R.id.editorfont_5://
//        break;
//        case R.id.editorfont_6://仿宋
//        break;
//        case R.id.editorfont_7://苹方
//        break;
//        case R.id.editorfont_8://楷体
//        break;
//        case R.id.editorfont_9://隶书
//        break;
//        case R.id.editorfont_10://黑色
//        break;
//        case R.id.editorfont_11://灰色
//        break;
//        case R.id.editorfont_12://蓝色
//        break;
//        case R.id.editorfont_13://绿色
//        break;
//        case R.id.editorfont_14://红色
//        break;
////            case R.id.editorfont_15://12号字体
////                break;
//        case R.id.editorfont_16://14号字体
//        break;
//        case R.id.editorfont_17://16号字体
//        break;
//        case R.id.editorfont_18://18号字体
//        break;
//        case R.id.editorfont_19://24号字体
//        break;
//
//
//        case R.id.editor_add_voice://添加视频
//        break;
//        case R.id.editor_add_link://添加超链接
//        break;
//        case R.id.editor_add_location://添加定位
//        break;
//        case R.id.editor_add_tag://添加标签
//        break;
//        case R.id.save:
//        String html = this.arEditor.getHtml();
//        DemoUtil.saveHtml(this, html);
//        break;
//
//        }
//        }
//
///**
// * 设置键盘下面显示还是隐藏
// */
//
//private void setKeyBarState() {
////        rl_keybar.setVisibility(View.VISIBLE);//显示下列所有空间的父类
////        select_add.setVisibility(View.GONE);//显示或隐藏超链接
//        ll_font.setVisibility(View.INVISIBLE);//字体设置显示或隐藏
//
//
//        record_voice.setVisibility(View.INVISIBLE);
//        select_add.setVisibility(View.INVISIBLE);
//        }
}
