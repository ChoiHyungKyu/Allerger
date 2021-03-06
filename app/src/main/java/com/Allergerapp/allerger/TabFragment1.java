package com.Allergerapp.allerger;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

public class TabFragment1 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout tab1 = (LinearLayout) inflater.inflate(R.layout.tab_fragment_1, container, false);
        ImageView fra1 = (ImageView)tab1.findViewById(R.id.frag1_main);
        Glide.with(this).load(R.drawable.fra1).into(fra1);
        return tab1;
    }
}