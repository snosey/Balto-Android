package com.example.snosey.balto.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.example.snosey.balto.BuildConfig;
import com.example.snosey.balto.MainActivity;
import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.CustomTextView;
import com.example.snosey.balto.Support.image.FullScreen;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;
import com.example.snosey.balto.Support.webservice.WebServiceVolley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static com.example.snosey.balto.Support.webservice.WebService.OnlineConsult.getMessagesApi;
import static com.example.snosey.balto.Support.webservice.WebService.OnlineConsult.sendMessagesApi;
import static com.example.snosey.balto.Support.webservice.WebService.OnlineConsult.updateChatApi;

/**
 * Created by Snosey on 3/20/2018.
 */

public class ChatRoom extends Fragment {
    private static final int STORAGE_PERM = 211;
    private static final int PICK_IMAGE = 112;
    public String chatId = "";
    @InjectView(R.id.medicalReportText)
    CustomTextView medicalReportText;
    @InjectView(R.id.sendText)
    ImageView sendText;
    @InjectView(R.id.sendImage)
    ImageView sendImage;
    @InjectView(R.id.chatBottom)
    LinearLayout chatBottom;
    @InjectView(R.id.messagesRV)
    RecyclerView messagesRV;
    @InjectView(R.id.messageText)
    AppCompatEditText messageText;
    @InjectView(R.id.sendLayout)
    RelativeLayout sendLayout;
    @InjectView(R.id.patientNo)
    AppCompatButton patientNo;
    @InjectView(R.id.patientYes)
    AppCompatButton patientYes;
    @InjectView(R.id.changeStatePatient)
    LinearLayout changeStatePatient;
    @InjectView(R.id.drNo)
    AppCompatButton drNo;
    @InjectView(R.id.drYes)
    AppCompatButton drYes;
    @InjectView(R.id.changeStateDoctor)
    LinearLayout changeStateDoctor;
    private JSONArray arrayMessage = new JSONArray();
    MessageAdapter messageAdapter = new MessageAdapter();
    String fcm_token = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_room, container, false);
        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.back)).setVisibility(View.VISIBLE);
        ((RelativeLayout) getActivity().getWindow().getDecorView().findViewById(R.id.menuHome)).setVisibility(View.GONE);
        ButterKnife.inject(this, view);

        chatId = getArguments().getString("id");
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        messagesRV.setLayoutManager(layoutManager);
        messagesRV.setAdapter(messageAdapter);


        getMessages();

        sendImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                {
                    if (Objects.requireNonNull(getActivity()).checkCallingOrSelfPermission(READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(
                                new String[]{READ_EXTERNAL_STORAGE},
                                STORAGE_PERM);

                        return;
                    }


                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, PICK_IMAGE);
                }

            }
        });
        sendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (messageText.getText().toString().trim().length() == 0) {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.emptyMsg), Toast.LENGTH_SHORT).show();
                    return;
                } else sendMsg("");
            }
        });

        drYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeStateDoctor.setVisibility(View.GONE);
                changeStateChat("0");
            }
        });
        patientYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeStatePatient.setVisibility(View.GONE);
                changeStateChat("-1");
            }
        });

        drNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeStateDoctor.setVisibility(View.GONE);
            }
        });
        patientNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeStateChat("1");
                changeStatePatient.setVisibility(View.GONE);
                sendLayout.setVisibility(View.VISIBLE);
            }
        });
        return view;
    }

    private void sendMsg(String image) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", MainActivity.jsonObject.getString("id"));
            jsonObject.put("id_chat", getArguments().getString("id"));
            if (!image.equals("")) {
                jsonObject.put("file_type", WebServiceVolley.getMimeType(new File(image)));
                jsonObject.put("file_name", WebServiceVolley.convertFile(new File(image)));
            } else
                jsonObject.put("message", messageText.getText().toString());
            messageText.setText("");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new WebServiceVolley(getActivity(), Request.Method.POST, sendMessagesApi, true,
                "Error, please try again", jsonObject, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject newMsg = new JSONObject(response).getJSONObject("message");
                    try {
                        newMsg.put("message", jsonObject.getString("message"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    newMsg(newMsg);
                    sendNotification(newMsg.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            private void sendNotification(String response) {
                if (fcm_token.equals(""))
                    return;

                final UrlData urlData = new UrlData();
                urlData.add(WebService.Notification.reg_id, fcm_token);
                urlData.add(WebService.Notification.data, response);
                urlData.add(WebService.Notification.kind, WebService.Notification.Types.newMsg);
                urlData.add(WebService.Notification.message, " ");
                urlData.add(WebService.Notification.title, getActivity().getString(R.string.newMsg));

                new GetData(new GetData.AsyncResponse() {
                    @Override
                    public void processFinish(String output) {

                    }
                }, getActivity(), false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Notification.notificationApi, urlData.get());

            }
        }).sendRequest();
    }

    public void newMsg(final JSONObject newMsg) {
        try {
            arrayMessage.put(newMsg);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String userId = newMsg.getString("user_id");
                        if (BuildConfig.APPLICATION_ID.contains("doctor")) {
                            if (userId.equals(MainActivity.jsonObject.getString("id"))) {
                                changeStatePatient.setVisibility(View.GONE);
                                changeStateDoctor.setVisibility(View.VISIBLE);
                                sendLayout.setVisibility(View.VISIBLE);
                            } else if (newMsg.getString("isActive").equals("1")) {
                                changeStatePatient.setVisibility(View.GONE);
                                changeStateDoctor.setVisibility(View.GONE);
                                sendLayout.setVisibility(View.GONE);
                            } else {
                                sendLayout.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if (newMsg.getString("isActive").equals("0") && !userId.equals(MainActivity.jsonObject.getString("id"))) {
                                changeStatePatient.setVisibility(View.VISIBLE);
                                changeStateDoctor.setVisibility(View.GONE);
                                sendLayout.setVisibility(View.GONE);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    messageAdapter.notifyItemInserted(arrayMessage.length() - 1);
                    messagesRV.scrollToPosition(arrayMessage.length() - 1);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getMessages() {
        UrlData urlData = new UrlData();
        urlData.add("id_chat", getArguments().getString("id"));
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) throws JSONException {
                JSONObject jsonObject = new JSONObject(output).getJSONObject("chats");
                arrayMessage = jsonObject.getJSONArray("messages");

                if (BuildConfig.APPLICATION_ID.contains("doctor"))
                    fcm_token = jsonObject.getJSONObject("patient").getString("fcm_token");
                else
                    fcm_token = jsonObject.getJSONObject("doctor").getString("fcm_token");

                if (jsonObject.getString("isActive").equals("1")) {
                    changeStatePatient.setVisibility(View.GONE);
                    changeStateDoctor.setVisibility(View.GONE);
                    sendLayout.setVisibility(View.VISIBLE);
                } else if (jsonObject.getString("isActive").equals("0")) {
                    sendLayout.setVisibility(View.GONE);
                    changeStateDoctor.setVisibility(View.GONE);
                    if (!BuildConfig.APPLICATION_ID.contains("doctor")) {
                        changeStatePatient.setVisibility(View.VISIBLE);
                    }
                } else if (jsonObject.getString("isActive").equals("-1")) {
                    changeStatePatient.setVisibility(View.GONE);
                    changeStateDoctor.setVisibility(View.GONE);
                    sendLayout.setVisibility(View.GONE);
                }
                messageAdapter.notifyDataSetChanged();
                messagesRV.scrollToPosition(arrayMessage.length() - 1);
            }


        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getMessagesApi, urlData.get());
    }

    private void changeStateChat(final String state) {
        UrlData urlDataState = new UrlData();
        urlDataState.add("isActive", state);
        urlDataState.add("id", getArguments().getString("id"));
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) throws JSONException {
                getMessages();
                if (state.equals("0"))
                    messageText.setText(getActivity().getString(R.string.finshChatPatient));
                sendMsg("");
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, updateChatApi, urlDataState.get());
    }

    private class MessageAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public int getItemViewType(int position) {
            int type = 0;
            try {
                JSONObject jsonObject = arrayMessage.getJSONObject(position);
                if (jsonObject.getString("user_id").equals(MainActivity.jsonObject.getString("id")))
                    type = 0;
                else
                    type = 1;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return type;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, final int position) {
            // create a new view
            View viewSender = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_row_sender, parent, false);

            View viewReciver = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_row_reciver, parent, false);
            if (position == 0)
                return new MyViewHolder(viewSender);
            else
                return new MyViewHolder(viewReciver);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            try {
                JSONObject jsonObject = arrayMessage.getJSONObject(position);
                if (!jsonObject.getString("message").equals("null")) {
                    holder.text.setVisibility(View.VISIBLE);
                    holder.image.setVisibility(View.GONE);
                    holder.text.setText(jsonObject.getString("message"));
                } else {
                    holder.image.setVisibility(View.VISIBLE);
                    holder.text.setVisibility(View.GONE);
                    final String url = "http://haseboty.com/doctor/public/uploads/chats/" + jsonObject.getString("file_name");
                    Picasso.with(getActivity()).load(url).into(holder.image);
                    holder.image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                new FullScreen(getActivity(), url);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        @Override
        public int getItemCount() {
            return (int) arrayMessage.length();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public CustomTextView text;
        public ImageView image;

        public MyViewHolder(View v) {
            super(v);
            text = (CustomTextView) v.findViewById(R.id.text);
            image = (ImageView) v.findViewById(R.id.image);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Toast.makeText(getActivity(), "Error, please try again!", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                String imagePath = GetRealFilePath.getRealPathFromURI(data.getData(), getActivity());
                sendMsg(imagePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }


        // TODO do something with the bitmap
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case STORAGE_PERM:
                if (getActivity().checkCallingOrSelfPermission(READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{READ_EXTERNAL_STORAGE},
                            STORAGE_PERM);
                    return;
                } else sendImage.callOnClick();
                break;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    static class GetRealFilePath {
        public static String getRealPathFromURI(Uri contentURI, Context context) {
            String realPath;
            // SDK < API11
            if (Build.VERSION.SDK_INT < 11)
                realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(context, contentURI);

                // SDK >= 11 && SDK < 19
            else if (Build.VERSION.SDK_INT < 19)
                realPath = RealPathUtil.getRealPathFromURI_API11to18(context, contentURI);

                // SDK > 19 (Android 4.4)
            else
                realPath = RealPathUtil.getRealPathFromURI_API19(context, contentURI);

            return realPath;
        }

        static class RealPathUtil {

            @SuppressLint("NewApi")
            public static String getRealPathFromURI_API19(Context context, Uri uri) {
                String filePath = "";
                String wholeID = DocumentsContract.getDocumentId(uri);

                // Split at colon, use second item in the array
                String id = wholeID.split(":")[1];

                String[] column = {MediaStore.Images.Media.DATA};

                // where id is equal to
                String sel = MediaStore.Images.Media._ID + "=?";

                Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);

                int columnIndex = cursor.getColumnIndex(column[0]);

                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();
                return filePath;
            }


            @SuppressLint("NewApi")
            private static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
                String[] proj = {MediaStore.Images.Media.DATA};
                String result = null;

                CursorLoader cursorLoader = new CursorLoader(
                        context,
                        contentUri, proj, null, null, null);
                Cursor cursor = cursorLoader.loadInBackground();

                if (cursor != null) {
                    int column_index =
                            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    result = cursor.getString(column_index);
                }
                return result;
            }

            private static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri) {
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
                int column_index
                        = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            }
        }
    }
}
