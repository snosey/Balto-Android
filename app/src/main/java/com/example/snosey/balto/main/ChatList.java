package com.example.snosey.balto.main;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.snosey.balto.BuildConfig;
import com.example.snosey.balto.MainActivity;
import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.CustomTextView;
import com.example.snosey.balto.Support.image.CircleTransform;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;
import com.example.snosey.balto.login.RegistrationActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.example.snosey.balto.Support.webservice.WebService.OnlineConsult.getChatsApi;

public class ChatList extends Fragment {
    @InjectView(R.id.inboxRV)
    RecyclerView inboxRV;
    private JSONArray arrayChat = new JSONArray();
    ChatAdapter chatAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_list, container, false);
        ButterKnife.inject(this, view);
        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.back)).setVisibility(View.VISIBLE);
        ((RelativeLayout) getActivity().getWindow().getDecorView().findViewById(R.id.menuHome)).setVisibility(View.GONE);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        inboxRV.setLayoutManager(layoutManager);
        chatAdapter = new ChatAdapter();
        inboxRV.setAdapter(chatAdapter);
        getChats();
        return view;
    }


    private void getChats() {
        String type = "";
        String type_key = "";
        if (BuildConfig.APPLICATION_ID.contains("doctor")) {
            type = "doctor";
            type_key = "id_doctor";
        } else {
            type = "patient";
            type_key = "id_patient";
        }


        UrlData urlData = new UrlData();
        try {
            urlData.add(type_key, MainActivity.jsonObject.getString("id"));
            urlData.add("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) throws JSONException {
                arrayChat = new JSONObject(output).getJSONArray("chats");
                chatAdapter.notifyDataSetChanged();
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getChatsApi, urlData.get());
    }


    private class ChatAdapter extends RecyclerView.Adapter<MyViewHolder> {


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, final int position) {
            // create a new view
            View chatView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_list_row, parent, false);

            return new MyViewHolder(chatView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            try {
                final JSONObject jsonObject = arrayChat.getJSONObject(position);

                JSONObject userObject;
                if (BuildConfig.APPLICATION_ID.contains("doctor")) {
                    userObject = jsonObject.getJSONObject("patient");
                } else {
                    userObject = jsonObject.getJSONObject("doctor");
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String chatId = null;
                        try {
                            chatId = jsonObject.getString("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        Bundle bundle = new Bundle();
                        bundle.putString("id", chatId);
                        ChatRoom fragment = new ChatRoom();
                        FragmentTransaction ft = fm.beginTransaction();
                        fragment.setArguments(bundle);
                        ft.replace(R.id.fragment, fragment, "ChatRoom");
                        ft.addToBackStack("Chat");
                        ft.commitAllowingStateLoss();
                    }
                });

                holder.text.setText(userObject.getString("first_name_en"));
                Picasso.with(getActivity()).load(WebService.Image.fullPathImage + userObject.getString("image")).transform(new CircleTransform()).into(holder.image);
                if (RegistrationActivity.sharedPreferences.getString("lang", "en").equals("ar"))
                    holder.secondCategory.setText(jsonObject.getJSONObject("category").getString("name_ar"));
                else
                    holder.secondCategory.setText(jsonObject.getJSONObject("category").getString("name_en"));


                if (jsonObject.getJSONObject("message").toString().equals("null")) {
                return;
                }
                else {
                    if (jsonObject.getJSONObject("message").getString("message").equals("null"))
                        holder.msg.setText("Image");
                    else
                        holder.msg.setText(jsonObject.getJSONObject("message").getString("message"));

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    sdf.setTimeZone(TimeZone.getTimeZone("GMT+02"));
                    long time = 0;
                    try {
                        time = sdf.parse(jsonObject.getJSONObject("message").getString("created_at")).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long now = System.currentTimeMillis();

                    CharSequence ago =
                            DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);

                    holder.date.setText(ago);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        @Override
        public int getItemCount() {
            return (int) arrayChat.length();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public CustomTextView text, secondCategory, date, msg;
        public ImageView image;

        public MyViewHolder(View v) {
            super(v);
            msg = (CustomTextView) v.findViewById(R.id.msg);
            text = (CustomTextView) v.findViewById(R.id.name);
            secondCategory = (CustomTextView) v.findViewById(R.id.secondCategory);
            Typeface font = Typeface.createFromAsset(
                    getActivity().getAssets(),
                    "fonts/arial.ttf");
            text.setTypeface(font, Typeface.BOLD);
            date = (CustomTextView) v.findViewById(R.id.date);
            image = (ImageView) v.findViewById(R.id.logo);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
