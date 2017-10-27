package com.example.marcschnaebe.mynacho;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by anthony.fleury on 27.10.2017.
 */

public class MyCustomAdapter extends BaseAdapter {

    private ArrayList<Nachos> mData = new ArrayList<Nachos>();
    private LayoutInflater mInflater;
    private Context context;

    public MyCustomAdapter(Context _context, ArrayList<Nachos> list) {
        context = _context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mData = list;
    }



    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Nachos getItem(int position) {
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
            convertView = mInflater.inflate(R.layout.item_team, null);
            holder = new ViewHolder();
            holder.nachosName = (TextView)convertView.findViewById(R.id.textViewNachosName);
            holder.nachosLevel = (TextView)convertView.findViewById(R.id.textViewLevelValue);
            holder.nachosType = (TextView)convertView.findViewById(R.id.textViewTypeValue);
            holder.imageNachos = (ImageButton)convertView.findViewById(R.id.imageButtonTeam);
            holder.nachosHP = (ProgressBar)convertView.findViewById(R.id.progressBarHP);
            holder.nachosXP = (ProgressBar)convertView.findViewById(R.id.progressBarXP);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.nachosName.setText(mData.get(position).getName());
        holder.nachosLevel.setText(String.valueOf(mData.get(position).getLevel()));
        holder.nachosType.setText(mData.get(position).getType());
        holder.imageNachos.setImageResource(context.getResources().getIdentifier(mData.get(position).getName().toLowerCase(), "drawable", context.getPackageName()));
        holder.nachosHP.setProgress(mData.get(position).getHpPercent());
        holder.nachosXP.setProgress(mData.get(position).getXpPercent());
        return convertView;
    }

    public static class ViewHolder {
        public TextView nachosName;
        public TextView nachosLevel;
        public TextView nachosType;
        public ImageButton imageNachos;
        public ProgressBar nachosHP;
        public ProgressBar nachosXP;
    }
}