package com.yellowsoft.radioapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sriven on 2/13/2016.
 */
public class NewsDetailFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.news_detail_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = getView();
        TextView page_tittle = (TextView) v.findViewById(R.id.page_title);
        TextView news_description = (TextView) v.findViewById(R.id.news_description);
        page_tittle.setText(Html.fromHtml(getArguments().getString("title")));
        news_description.setText(Html.fromHtml(getArguments().getString("desc")));
        CircleImageView artist_image = (CircleImageView)v.findViewById(R.id.artist_image);
        Picasso.with(getActivity()).load(getArguments().getString("image")).into(artist_image);

    }
}
