package com.example.marcschnaebe.mynacho;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by anthony.fleury on 27.10.2017.
 */

public class BagAdapter extends BaseAdapter {

    private ArrayList<Item> mData = new ArrayList<Item>();
    private LayoutInflater mInflater;
    private Context context;

    public BagAdapter(Context _context, ArrayList<Item> list) {
        context = _context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mData = list;
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Item getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("view", "getView " + position + " " + convertView);

        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_bag, null);
            holder = new ViewHolder();
            holder.imageItem = (ImageView)convertView.findViewById(R.id.imageItem);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.imageItem.setImageResource(context.getResources().getIdentifier(mData.get(position).getName().toLowerCase(), "drawable", context.getPackageName()));

        return convertView;
    }

    public static class ViewHolder {

        public ImageView imageItem;

    }
}