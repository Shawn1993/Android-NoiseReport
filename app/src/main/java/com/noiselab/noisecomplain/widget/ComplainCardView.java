package com.noiselab.noisecomplain.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.noiselab.noisecomplain.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shawn on 29/3/2016.
 */
public class ComplainCardView extends FrameLayout {

    TextView mTitleView;
    ImageView mHeadImageView;
    LinearLayoutCompat mSubtitlesLayout;
    LinearLayoutCompat mCardLayout;
    View mFootView;

    boolean mIsActivated;
    int mActivatedColor;
    int mDeactivatedColor;

    private void initLayout(Context context) {
        LayoutInflater.from(context).inflate(R.layout.complain_cardview, this);
        mTitleView = (TextView) findViewById(R.id.title);
        mSubtitlesLayout = (LinearLayoutCompat) findViewById(R.id.layout);
        mHeadImageView = (ImageView) findViewById(R.id.image);
        mCardLayout = (LinearLayoutCompat) findViewById(R.id.card_layout);
    }

    public ComplainCardView(Context context) {
        super(context);
        initLayout(context);
    }

    public ComplainCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ComplainCardView);

        mHeadImageView.setImageDrawable(a.getDrawable(R.styleable.ComplainCardView_head_image_src));
        mTitleView.setText(a.getText(R.styleable.ComplainCardView_title_text));
        // the default num of subtitles is 1
        int num = a.getInt(R.styleable.ComplainCardView_subtitle_default_num, 1);
        while (num > 1) {
            LayoutInflater.from(context).inflate(R.layout.complain_cardview_subtitle, mSubtitlesLayout);
            num--;
        }
        // must be below the subtitle num
        if (a.getBoolean(R.styleable.ComplainCardView_subtitle_hidden, true)) {
            hideAllSubTitles();
        }
        int layoutId = a.getResourceId(R.styleable.ComplainCardView_foot_view, 0);
        if (layoutId != 0) {
            mFootView = LayoutInflater.from(context).inflate(layoutId, null);
            mCardLayout.addView(mFootView);
        }
        mIsActivated = a.getBoolean(R.styleable.ComplainCardView_activated, false);
        mActivatedColor = a.getColor(R.styleable.ComplainCardView_activated_color, getResources().getColor(R.color.colorAccent));
        mDeactivatedColor = a.getColor(R.styleable.ComplainCardView_deactivated_color, Color.GRAY);
        if (mIsActivated) {
            setThemeColor(mActivatedColor);
        } else {
            setThemeColor(mDeactivatedColor);
        }
    }

    private void setThemeColor(int color) {
        mTitleView.setTextColor(color);
        mHeadImageView.setColorFilter(color);
    }

    public void activate() {
        mIsActivated = true;
        setThemeColor(mActivatedColor);
    }

    public void deActivate() {
        mIsActivated = false;
        setThemeColor(mDeactivatedColor);
    }

    public boolean isActivated() {
        return mIsActivated;
    }


    public void setTitle(String title) {
        if (mTitleView != null) {
            mTitleView.setText(title);
        }
    }

    public void setHeadImage(int resId) {
        if (mHeadImageView != null)
            mHeadImageView.setImageResource(resId);
    }


    // set text to specific subtitle
    public void setSubTitleText(CharSequence subTitle, int position) {
        ((TextView) mSubtitlesLayout.getChildAt(position)).setText(subTitle);
    }

    // get text from specific subtitle
    public CharSequence getSubTitleText(int position) {
        return ((TextView) mSubtitlesLayout.getChildAt(position)).getText();
    }

    public void setSubTitleImage(int resId, int position) {
        if (position > (mSubtitlesLayout.getChildCount() - 1)) {
            return;
        }
    }

    public void addSubTitle(CharSequence text, int resId) {
        TextView view = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.complain_cardview_subtitle, mSubtitlesLayout);
        view.setText(text);
    }

    public void hideAllSubTitles() {
        int length = mSubtitlesLayout.getChildCount();
        for (int i = 0; i < length; i++) {
            mSubtitlesLayout.getChildAt(i).setVisibility(View.GONE);
        }
    }

    public void showAllSubTitles() {
        int length = mSubtitlesLayout.getChildCount();
        for (int i = 0; i < length; i++) {
            mSubtitlesLayout.getChildAt(i).setVisibility(View.VISIBLE);
        }
    }

    public void hideSubTitle(int position) {
        mSubtitlesLayout.getChildAt(position).setVisibility(View.GONE);
    }

    public void showSubTitle(int position) {
        mSubtitlesLayout.getChildAt(position).setVisibility(View.VISIBLE);
    }


    public View getFootView() {
        return mFootView;
    }
}
