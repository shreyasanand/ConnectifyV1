package com.team5.uta.connectifyv1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.team5.uta.connectifyv1.R;

/**
 * Created by shreyas on 2/20/2015.
 */
public class InterestDataAdapter extends BaseAdapter {

    private Context mContext;
    private final InterestHolder[] interestPool;

    public InterestDataAdapter(Context c, InterestHolder[] interestPool) {
        mContext = c;
        this.interestPool = interestPool;
    }

    @Override
    public int getCount() {
        return interestPool.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            grid = inflater.inflate(R.layout.grid_single, null);
        } else {
            grid = (View) convertView;
        }
        TextView textView = (TextView) grid.findViewById(R.id.grid_text);
        ImageView imageView = (ImageView)grid.findViewById(R.id.grid_image);
        textView.setText(interestPool[position].getInterestText());
        imageView.setImageResource(interestPool[position].getInterestImageId());
        return grid;
    }
}
