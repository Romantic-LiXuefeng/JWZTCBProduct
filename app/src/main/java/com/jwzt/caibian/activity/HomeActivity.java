package com.jwzt.caibian.activity;

import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jwzt.caibian.adapter.MyGridViewAdapter;
import com.jwzt.caibian.adapter.MyHomeViewPagerAdapter;
import com.jwzt.caibian.adapter.MyViewPagerAdapter;
import com.jwzt.caibian.bean.ProductBean;
import com.jwzt.cb.product.R;
import com.sunfusheng.marqueeview.IMarqueeItem;
import com.sunfusheng.marqueeview.MarqueeView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends Activity {

    private MarqueeView marqueeView;
    private ViewPager viewPager;//轮播滑动
    private LinearLayout group;//圆点指示
    private ArrayList listData;//列表数据

    private ImageView[] ivPoints;//圆点图片集合
    private int totalPage;//总页数
    private int mPageSize = 4;//每页显示的最大的数量
    private String[] proName = {"美食", "甜品饮料", "商店超市", "预定早餐",
            "果蔬生鲜", "新店特惠", "准时达", "简餐",
            "土豪推荐", "鲜花蛋糕", "汉堡", "日韩料理",
            "麻辣烫", "披萨意面", "川湘菜", "包子粥店"};
    private int[] proImg = {R.drawable.shen2,R.drawable.shen2,R.drawable.shen2,R.drawable.shen2,
            R.drawable.shen2,R.drawable.shen2,R.drawable.shen2,R.drawable.shen2,
            R.drawable.shen2,R.drawable.shen2,R.drawable.shen2,R.drawable.shen2,
            R.drawable.shen2,R.drawable.shen2,R.drawable.shen2,R.drawable.shen2};
    private ArrayList viewPagerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
    }

    private void initView() {

        marqueeView = (MarqueeView) findViewById(R.id.marqueeView);

        List<String> messages = new ArrayList<>();
        messages.add("1. 大家好，我是孙福生。");
        messages.add("2. 欢迎大家关注我哦！");
        messages.add("3. GitHub帐号：sunfusheng");
        messages.add("4. 新浪微博：孙福生微博");
        messages.add("5. 个人博客：sunfusheng.com");
        messages.add("6. 微信公众号：孙福生");
        marqueeView.startWithList(messages);



       // List<CustomModel> messages = new ArrayList<>();
        marqueeView.startWithList(messages);

// 在代码里设置自己的动画
        marqueeView.startWithList(messages, R.anim.anim_bottom_in, R.anim.anim_top_out);
        marqueeView.setOnItemClickListener(new MarqueeView.OnItemClickListener() {
            @Override
            public void onItemClick(int position, TextView textView) {
                Toast.makeText(getApplicationContext(), String.valueOf(marqueeView.getPosition()) + ". " + textView.getText(), Toast.LENGTH_SHORT).show();
            }
        });


        viewPager = findViewById(R.id.viewpager);
        group = (LinearLayout) findViewById(R.id.points);
        listData = new ArrayList<ProductBean>();
        for (int i = 0; i < proName.length; i++) {
            listData.add(new ProductBean(proName[i], proImg[i]));
        }
        initData();
    }

    private void initData() {
        //总的页数向上取整
        totalPage = (int) Math.ceil(listData.size() * 1.0 / mPageSize);
        viewPagerList = new ArrayList<View>();
        for (int i = 0; i < totalPage; i++) {
            //每个页面都是inflate出一个新实例
            final GridView gridView = (GridView) View.inflate(this, R.layout.item_gridview, null);
            gridView.setAdapter(new MyGridViewAdapter(this, listData, i, mPageSize));
            //添加item点击监听
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    Object obj = gridView.getItemAtPosition(position);
                    if (obj != null && obj instanceof ProductBean) {
                        System.out.println(obj);
                        Toast.makeText(HomeActivity.this, ((ProductBean) obj).getName(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            //每一个GridView作为一个View对象添加到ViewPager集合中
            viewPagerList.add(gridView);
        }
        //设置ViewPager适配器
        viewPager.setAdapter(new MyHomeViewPagerAdapter(viewPagerList));

        //添加小圆点
        ivPoints = new ImageView[totalPage];
        for (int i = 0; i < totalPage; i++) {
            //循坏加入点点图片组
            ivPoints[i] = new ImageView(this);
            if (i == 0) {
                ivPoints[i].setImageResource(R.mipmap.page_focuese);
            } else {
                ivPoints[i].setImageResource(R.mipmap.page_unfocused);
            }
            ivPoints[i].setPadding(8, 8, 8, 8);
            group.addView(ivPoints[i]);
        }
        //设置ViewPager的滑动监听，主要是设置点点的背景颜色的改变
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < totalPage; i++) {
                    if (i == position) {
                        ivPoints[i].setImageResource(R.mipmap.page_focuese);
                    } else {
                        ivPoints[i].setImageResource(R.mipmap.page_unfocused);
                    }
                }
            }
        });
    }



    // 或者设置自定义的Model数据类型
    public class CustomModel implements IMarqueeItem {
        @Override
        public CharSequence marqueeMessage() {
            return "...";
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        marqueeView.startFlipping();
    }
    @Override
    public void onStop() {
        super.onStop();
        marqueeView.stopFlipping();
    }
}
