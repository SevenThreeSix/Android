package com.example.user.helpeachother.dummy;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.user.helpeachother.R;

/**
 * Created by User on 2018/3/22.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration{

    private int space;
    private Drawable mDivider;

    public SpaceItemDecoration(int space,Context context) {
        //this.mDivider = ContextCompat.getDrawable(context,R.drawable.ic_launcher_background);
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        if(parent.getChildPosition(view) != 0)
            outRect.top = space;
    }
}
