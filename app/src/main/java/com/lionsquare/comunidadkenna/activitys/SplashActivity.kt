package com.lionsquare.comunidadkenna.activitys

import android.animation.ValueAnimator
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView

import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.lionsquare.comunidadkenna.R

import java.util.concurrent.TimeUnit


import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1

class SplashActivity : AppCompatActivity() {
    internal lateinit var logoOuterIv: ImageView
    internal lateinit var logoInnerIv: ImageView
    internal lateinit var appName: TextView
    internal var isShowingRubberEffect = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        getIds()
        initAnimation()
    }

    private fun getIds() {
        logoOuterIv = findViewById(R.id.logo_outer_iv) as ImageView
        logoInnerIv = findViewById(R.id.logo_inner_iv) as ImageView
        appName = findViewById(R.id.appName) as TextView
    }

    private fun initAnimation() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.anim_top_in)
        logoInnerIv.startAnimation(animation)

        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.duration = 1000
        valueAnimator.addUpdateListener { animation ->
            val fraction = animation.animatedFraction
            if (fraction >= 0.8 && !isShowingRubberEffect) {
                isShowingRubberEffect = true
                startLogoOuter()
                startShowAppName()
                finishActivity()
            } else if (fraction >= 0.95) {
                valueAnimator.cancel()
                startLogoInner2()
            }
        }
        valueAnimator.start()

    }

    private fun startLogoOuter() {
        YoYo.with(Techniques.RubberBand).duration(1000).playOn(logoOuterIv)
    }

    private fun startShowAppName() {
        YoYo.with(Techniques.FadeIn).duration(1000).playOn(appName)
    }

    private fun startLogoInner2() {
        YoYo.with(Techniques.Bounce).duration(1000).playOn(logoInnerIv)
    }

    private fun finishActivity() {
        rx.Observable.timer(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(rx.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val menu = Intent(this@SplashActivity, LoginActivity::class.java)
                    startActivity(menu)
                    finish()
                }
    }

}
