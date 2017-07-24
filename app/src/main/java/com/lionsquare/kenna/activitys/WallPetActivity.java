package com.lionsquare.kenna.activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.lionsquare.kenna.R;
import com.lionsquare.kenna.adapter.BaseRecyclerAdapter;
import com.lionsquare.kenna.holder.SmartViewHolder;
import com.lionsquare.kenna.utils.StatusBarUtil;

import java.util.Arrays;
import java.util.Collection;

public class WallPetActivity extends AppCompatActivity {

    private class Model {
        int imageId;
        int avatarId;
        String name;
        String nickname;
    }

    private BaseRecyclerAdapter<Model> mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall_pet);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        //初始化列表和监听
        View view = findViewById(R.id.aw_rv_pet);
        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter = new BaseRecyclerAdapter<Model>(loadModels(), R.layout.listitem_practive_repast) {
                @Override
                protected void onBindViewHolder(SmartViewHolder holder, Model model, int position) {

                   // holder.text(R.id.name, model.name);
                    //holder.text(R.id.nickname, model.nickname);
                    //holder.image(R.id.image, model.imageId);
                    //holder.image(R.id.avatar, model.avatarId);
                }
            });


        }

        mAdapter.loadmore(loadModels());
        if (mAdapter.getCount() > 12) {
            Toast.makeText(getBaseContext(), "数据全部加载完毕", Toast.LENGTH_SHORT).show();
        }

        //状态栏透明和间距处理
        StatusBarUtil.darkMode(this);
        StatusBarUtil.setPaddingSmart(this, view);
        StatusBarUtil.setPaddingSmart(this, toolbar);
        StatusBarUtil.setPaddingSmart(this, findViewById(R.id.blurview));
        //StatusBarUtil.setMargin(this, findViewById(R.id.gifview));
    }

    /**
     * 模拟数据
     */
    private Collection<Model> loadModels() {
        return Arrays.asList(
                new Model() {{
                    this.name = "但家香酥鸭";
                    this.nickname = "爱过那张脸";
                    this.imageId = R.mipmap.ic_launcher_round;
                    this.avatarId = R.mipmap.ic_launcher_round;
                }}, new Model() {{
                    this.name = "香菇蒸鸟蛋";
                    this.nickname = "淑女算个鸟";
                    this.imageId = R.mipmap.ic_launcher_round;
                    this.avatarId = R.mipmap.ic_launcher_round;
                }}, new Model() {{
                    this.name = "花溪牛肉粉";
                    this.nickname = "性感妩媚";
                    this.imageId = R.mipmap.ic_launcher_round;
                    this.avatarId = R.mipmap.ic_launcher_round;
                }}, new Model() {{
                    this.name = "破酥包";
                    this.nickname = "一丝丝纯真";
                    this.imageId = R.mipmap.ic_launcher_round;
                    this.avatarId = R.mipmap.ic_launcher_round;
                }}, new Model() {{
                    this.name = "盐菜饭";
                    this.nickname = "等着你回来";
                    this.imageId = R.mipmap.ic_launcher_round;
                    this.avatarId = R.mipmap.ic_launcher_round;
                }}, new Model() {{
                    this.name = "米豆腐";
                    this.nickname = "宝宝树人";
                    this.imageId = R.mipmap.ic_launcher_round;
                    this.avatarId = R.mipmap.ic_launcher_round;
                }});
    }

}
