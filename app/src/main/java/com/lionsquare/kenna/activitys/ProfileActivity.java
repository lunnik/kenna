package com.lionsquare.kenna.activitys;


import android.databinding.DataBindingUtil;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.lionsquare.kenna.R;
import com.lionsquare.kenna.databinding.ActivityProfileBinding;
import com.lionsquare.kenna.utils.Preferences;

public class ProfileActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener, View.OnClickListener {

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    ActivityProfileBinding binding;
    private Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        preferences = new Preferences(this);
        bindActivity();

        binding.appbar.addOnOffsetChangedListener(this);

        //mToolbar.inflateMenu(R.menu.menu_main);
        startAlphaAnimation(binding.titleTwo, 0, View.INVISIBLE);

    }

    private void bindActivity() {

        if (URLUtil.isValidUrl(preferences.getImagePerfil()))

            Glide.with(this).load(preferences.getImagePerfil()).into(binding.imageProfile);
        else
            Glide.with(this).load(R.drawable.user).into(binding.imageProfile);


        if (URLUtil.isValidUrl(preferences.getCover()))
            Glide.with(this).load(preferences.getCover()).into(binding.cover);
        else
            Glide.with(this).load(R.drawable.back_login).into(binding.cover);


        binding.apTvName.setText(preferences.getName());
        binding.apTvEmail.setText(preferences.getEmail());
        binding.titleTwo.setText("Perfil");
        binding.included.logaout.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {
                startAlphaAnimation(binding.titleTwo, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(binding.titleTwo, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(binding.mainLinearlayoutTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(binding.mainLinearlayoutTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }


    @Override
    public void onClick(View v) {


    }
}
