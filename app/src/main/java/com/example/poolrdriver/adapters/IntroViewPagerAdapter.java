package com.example.poolrdriver.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.poolrdriver.R;
import com.example.poolrdriver.classes.other.screenItem;

import java.util.List;

public class IntroViewPagerAdapter extends PagerAdapter {
    Context mContext;
    List<screenItem> mListScreen;

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater= (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.layout_walkthrough_screen,null);

        //image view
        ImageView imageView=(ImageView)view.findViewById(R.id.walkthroughImageView);

        //textview
        TextView titleText=(TextView)view.findViewById(R.id.textViewTitle);
        TextView messageText=(TextView)view.findViewById(R.id.textViewMessage);

        //set texts
        titleText.setText(mListScreen.get(position).getTitle());
        messageText.setText(mListScreen.get(position).getDescription());

        //images
        imageView.setImageResource(mListScreen.get(position).getImage());

        //set views
        container.addView(view);

        return view;
    }

    public IntroViewPagerAdapter(Context mContext, List<screenItem> mListScreen) {

        this.mContext = mContext;
        this.mListScreen = mListScreen;
    }

    @Override
    public int getCount() {
        return mListScreen.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull  View view, @NonNull Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(@NonNull  ViewGroup container, int position, @NonNull  Object object) {
        container.removeView((View)object);
    }
}