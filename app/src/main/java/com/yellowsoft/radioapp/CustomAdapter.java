package com.yellowsoft.radioapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter{
    ArrayList<String> result;
    ArrayList<String> menuImage;
    Context context;
    ArrayList<Integer> imageId;
    private static LayoutInflater inflater=null;
    public CustomAdapter(Activity mainActivity, ArrayList<String> prgmNameList, ArrayList<Integer> prgmImages, ArrayList<String> menuImage) {
        // TODO Auto-generated constructor stub
        result=prgmNameList;
        context=mainActivity;
        imageId=prgmImages;
        this.menuImage = menuImage;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView tv;
        ImageView img;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.menu_item, parent,false);
        holder.tv=(TextView) rowView.findViewById(R.id.textView1);
        holder.img=(ImageView) rowView.findViewById(R.id.imageView1);
        holder.tv.setText(result.get(position));
        if(imageId.get(position) == -1)
            Picasso.with(context).load(menuImage.get(position)).into(holder.img);
        else
            holder.img.setImageResource(imageId.get(position));
        return rowView;
    }

}