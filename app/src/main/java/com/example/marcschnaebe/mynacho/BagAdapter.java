package com.example.marcschnaebe.mynacho;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;


/**
 * Create and generate a grid view containing items
 *
 * @author Fleury Anthony, Hirschi Christophe, Schnaebele Marc
 * @version 12.2017
 */
public class BagAdapter extends BaseAdapter {

    /* -------  Attributes  ------ */

    private ArrayList<Item> mData = new ArrayList<Item>();
    private LayoutInflater mInflater;
    private Context context;

    /* -------  Constructor ------- */

    /**
     * Constructor
     *
     * @param _context application context
     * @param list list of items
     */
    public BagAdapter(Context _context, ArrayList<Item> list) {
        context = _context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mData = list;
    }

    /* -------  Getter & Setter  ------ */

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

    /* -------  Internal Class  ------ */

    /**
     * Internal class that contains view elements to display
     */
    public static class ViewHolder {

        public ImageView imageItem;

    }
}