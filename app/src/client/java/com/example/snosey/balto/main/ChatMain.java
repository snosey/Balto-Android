package com.example.snosey.balto.main;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.snosey.balto.MainActivity;
import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.CustomTextView;
import com.example.snosey.balto.Support.image.CircleTransform;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;
import com.example.snosey.balto.login.RegistrationActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ChatMain extends Fragment {


    @InjectView(R.id.secondCategoryList)
    RecyclerView secondCategoryList;
    private JSONArray jsonArray = new JSONArray();
    private ChatAdapter chatAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_main, container, false);


        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.back)).setVisibility(View.VISIBLE);
        ((RelativeLayout) getActivity().getWindow().getDecorView().findViewById(R.id.menuHome)).setVisibility(View.GONE);

        ButterKnife.inject(this, view);
        chatAdapter = new ChatAdapter();
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        secondCategoryList.setLayoutManager(layoutManager);
        secondCategoryList.setAdapter(chatAdapter);

        getData();
        return view;
    }

    private void getData() {
        UrlData urlData = new UrlData();
        urlData.add(WebService.OnlineConsult.type, RegistrationActivity.sharedPreferences.getString("lang", "en"));

        new GetData(new GetData.AsyncResponse() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void processFinish(String output) {
                if (output.length() != 0) {
                    try {
                        final JSONObject jsonObject = new JSONObject(output);
                        jsonArray = jsonObject.getJSONArray("subCategory");
                        chatAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.OnlineConsult.getFilterDataApi, urlData.get());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


    private class ChatAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, final int position) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.second_category_row, parent, false);
            return new MyViewHolder(view);
        }

        @SuppressLint("RestrictedApi")
        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            try {
                final JSONObject jsonObject = jsonArray.getJSONObject(position);
                holder.name.setText(jsonObject.getString("name"));
                Picasso.with(getActivity()).load(WebService.Image.fullPathImage + jsonObject.getString("logo")).transform(new CircleTransform()).into(holder.logo, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        holder.logo.setImageResource(R.drawable.logo_icon);
                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        {

                            UrlData urlData = new UrlData();
                            try {
                                urlData.add(WebService.OnlineConsult.type, RegistrationActivity.sharedPreferences.getString("lang", "en"));
                                urlData.add(WebService.OnlineConsult.id_sub, jsonObject.getString("id"));

                            } catch (Exception e) {
                                return;
                            }
                            new GetData(new GetData.AsyncResponse() {
                                @Override
                                public void processFinish(String output) {
                                    try {
                                        JSONArray doctorJsonArray = new JSONObject(output).getJSONArray("users");
                                        if (doctorJsonArray.length() == 0)
                                            Toast.makeText(getActivity(), getActivity().getString(R.string.noProfissional), Toast.LENGTH_LONG).show();
                                        else {
                                            String id_doctor = doctorJsonArray.getJSONObject(0).getString("id");
                                            String id_patient = MainActivity.jsonObject.getString("id");
                                            String Fk_SecondCategoryProgram = jsonObject.getString("id");
                                            UrlData urlDataCreateChat = new UrlData();
                                            urlDataCreateChat.add("id_doctor", id_doctor);
                                            urlDataCreateChat.add("Fk_SecondCategoryProgram", Fk_SecondCategoryProgram);
                                            urlDataCreateChat.add("id_patient", id_patient);

                                            FragmentManager fm = getActivity().getSupportFragmentManager();
                                            Bundle bundle = new Bundle();
                                            bundle.putString("fcm_token", doctorJsonArray.getJSONObject(0).getString("fcm_token"));
                                            bundle.putString("url", urlDataCreateChat.get());
                                            ChatQuestion fragment = new ChatQuestion();
                                            FragmentTransaction ft = fm.beginTransaction();
                                            fragment.setArguments(bundle);
                                            ft.replace(R.id.fragment, fragment, "ChatQuestion");
                                            ft.addToBackStack("ChatQuestion");
                                            ft.commit();

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.OnlineConsult.getAvailableDoctorToChatApi, urlData.get());

                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return (int) jsonArray.length();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CustomTextView name;
        ImageView logo;

        public MyViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            logo = v.findViewById(R.id.logo);
        }
    }

}