package com.example.snosey.balto.login.create_account;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.image.CompressImage;
import com.example.snosey.balto.login.NewAccountObject;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import in.myinnos.awesomeimagepicker.activities.AlbumSelectActivity;
import in.myinnos.awesomeimagepicker.helpers.ConstantsCustomGallery;
import in.myinnos.awesomeimagepicker.models.Image;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Snosey on 1/31/2018.
 */

public class ProfilePic extends Fragment {
    ImageButton next;
    @InjectView(R.id.logo)
    ImageView logo;
    private String uri = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_account_profile_pic, container, false);
        ButterKnife.inject(this, view);

        next = (ImageButton) view.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                NewAccountObject accountObject = (NewAccountObject) getArguments().getSerializable("object");
                accountObject.logo = uri;
                bundle.putSerializable("object", accountObject);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Email fragment = new Email();
                fragment.setArguments(bundle);
                ft.add(R.id.fragment, fragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.upload)
    public void onViewClicked() {
        Intent intent = new Intent(getActivity(), AlbumSelectActivity.class);
        try {
            intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, 1); // set limit for image selection
            startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstantsCustomGallery.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //The array list has the image paths of the selected images
            ArrayList<Image> images = data.getParcelableArrayListExtra(ConstantsCustomGallery.INTENT_EXTRA_IMAGES);
            CompressImage compressImage = new CompressImage();
            for (int i = 0; i < images.size(); i++) {
                uri = Uri.fromFile(compressImage.compress(new File(images.get(i).path))).toString();
                Picasso.with(getContext()).load(uri).fit().centerCrop().into(logo);
            }
        }
    }
}