package com.example.snosey.balto.main.online_consultation;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.image.CircleTransform;
import com.example.snosey.balto.Support.webservice.WebService;
import com.example.snosey.balto.main.DoctorProfile;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.example.snosey.balto.main.online_consultation.Main.dateLl;
import static com.example.snosey.balto.main.online_consultation.Main.genderLL;
import static com.example.snosey.balto.main.online_consultation.Main.languageLL;

/**
 * Created by Snosey on 3/11/2018.
 */

public class DoctorList extends Fragment {
    JSONArray doctorsArray;
    @InjectView(R.id.doctorsRV)
    RecyclerView doctorsRV;
    DoctorAdapter doctorAdapter;
    private PopupMenu popup;
    ArrayList<JSONObject> arrayDoctor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.online_consultation_doctor_list, container, false);
        ButterKnife.inject(this, view);


        arrayDoctor = new ArrayList<JSONObject>();

        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.back)).setVisibility(View.VISIBLE);
        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.menu)).setVisibility(View.GONE);
        try {
            doctorsArray = new JSONArray(getArguments().getString("json"));
            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            doctorsRV.setLayoutManager(layoutManager);
            doctorAdapter = new DoctorAdapter();
            doctorsRV.setAdapter(doctorAdapter);
            for (int i = 0; i < doctorsArray.length(); i++) {
                try {
                    arrayDoctor.add(doctorsArray.getJSONObject(i));
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            doctorAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // to implement on click event on items of menu
        popup = new PopupMenu(getActivity(), view.findViewById(R.id.sort));
        popup.getMenuInflater().inflate(R.menu.sort, popup.getMenu());
        return view;
    }


    private class DoctorAdapter extends RecyclerView.Adapter<DoctorList.MyViewHolder> {

        @Override
        public DoctorList.MyViewHolder onCreateViewHolder(ViewGroup parent, final int position) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.online_consultation_doctor_list_row, parent, false);
            return new DoctorList.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final DoctorList.MyViewHolder holder, final int position) {
            try {
                final JSONObject doctorObject = arrayDoctor.get(position);
                holder.book.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        Bundle bundle = new Bundle();
                        bundle.putString("json", doctorObject.toString());
                        Agenda fragment = new Agenda();
                        FragmentTransaction ft = fm.beginTransaction();
                        fragment.setArguments(bundle);
                        ft.add(R.id.fragment, fragment, "Agenda");
                        ft.addToBackStack("Agenda");
                        ft.commit();
                    }
                });

                holder.name.setText(doctorObject.getString(WebService.OnlineConsult.firstName) + " " +
                        doctorObject.getString(WebService.OnlineConsult.lastName));


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        try {
                            bundle.putString(WebService.HomeVisit.id_user, doctorObject.getString("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        DoctorProfile fragment = new DoctorProfile();
                        FragmentTransaction ft = fm.beginTransaction();
                        fragment.setArguments(bundle);
                        ft.replace(R.id.fragment, fragment, "DoctorProfile");
                        ft.addToBackStack("DoctorProfile");
                        ft.commit();
                    }
                });

                holder.type.setText(doctorObject.getString(WebService.OnlineConsult.subName));

                holder.price.setText(doctorObject.getJSONObject(WebService.OnlineConsult.price).getString(WebService.OnlineConsult.price)
                        + " " + getActivity().getString(R.string.egp));

                if (!doctorObject.getString("image").equals("")) {
                    String imageLink = doctorObject.getString("image");
                    if (!imageLink.startsWith("https://"))
                        imageLink = WebService.Image.fullPathImage + imageLink;
                    Picasso.with(getContext()).load(imageLink).transform(new CircleTransform()).into(holder.logo);
                }
                else
                    holder.logo.setImageResource(R.drawable.logo_profile);
                if (!doctorObject.getString(WebService.Slider.total_rate).equals("null") && !doctorObject.getString(WebService.Slider.total_rate).equals("0"))
                    holder.rate.setText(doctorObject.getString(WebService.Slider.total_rate));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        @Override
        public int getItemCount() {
            return (int) arrayDoctor.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView name, type, rate, price;
        public ImageView logo;
        public Button book;

        public MyViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.name);
            Typeface font = Typeface.createFromAsset(
                    getActivity().getAssets(),
                    "fonts/arial.ttf");
            name.setTypeface(font, Typeface.BOLD);

            type = (TextView) v.findViewById(R.id.type);
            rate = (TextView) v.findViewById(R.id.rate);
            price = (TextView) v.findViewById(R.id.price);
            logo = (ImageView) v.findViewById(R.id.logo);
            book = (Button) v.findViewById(R.id.book);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick({R.id.filter, R.id.sort})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.filter:
                languageLL.setVisibility(View.VISIBLE);
                dateLl.setVisibility(View.VISIBLE);
                genderLL.setVisibility(View.VISIBLE);
                getActivity().onBackPressed();
                break;
            case R.id.sort: {
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        item.setChecked(true);
                        if (item.getItemId() == R.id.nameAsc)
                            sort(WebService.OnlineConsult.firstName, false);
                        else if (item.getItemId() == R.id.nameDesc)
                            sort(WebService.OnlineConsult.firstName, true);
                        else if (item.getItemId() == R.id.rateSort)
                            sort(WebService.Slider.total_rate, false);
                        return false;
                    }
                });
                popup.show();
            }
            break;
        }
    }

    void sort(final String filter, boolean reverse) {
        arrayDoctor.clear();
        for (int i = 0; i < doctorsArray.length(); i++) {
            try {
                arrayDoctor.add(doctorsArray.getJSONObject(i));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        Collections.sort(arrayDoctor, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {
                // TODO Auto-generated method stub

                try {
                    return (lhs.getString(filter).toLowerCase().compareTo(rhs.getString(filter).toLowerCase()));
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return 0;
                }
            }
        });
        if (reverse)
            Collections.reverse(arrayDoctor);

        doctorAdapter.notifyDataSetChanged();
    }
}
