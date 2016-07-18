package com.yellowsoft.radioapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;

import java.util.ArrayList;

/**
 * Created by sriven on 3/18/2016.
 */
public class GalleryViewFragment extends Fragment {

    private SliderLayout mDemoSlider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.image_sliding_gallery, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getView();
        mDemoSlider = (SliderLayout) view.findViewById(R.id.slider);

        ArrayList<String> images = getArguments().getStringArrayList("images");
        ArrayList<String> desc = getArguments().getStringArrayList("desc");
        for (int i = 0; i < images.size(); i++) {
            TextSliderView textSliderView = new TextSliderView(getActivity());
            // initialize a SliderLayout
            textSliderView
                    .description(String.valueOf(Html.fromHtml(desc.get(i))))
                    .image(images.get(i)).setScaleType(BaseSliderView.ScaleType.CenterInside);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", desc.get(i));

            mDemoSlider.addSlider(textSliderView);


        }
        mDemoSlider.setCurrentPosition(getArguments().getInt("position"));
    }

}
