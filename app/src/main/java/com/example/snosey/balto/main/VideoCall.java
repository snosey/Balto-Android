package com.example.snosey.balto.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.snosey.balto.BuildConfig;
import com.example.snosey.balto.MainActivity;
import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;
import com.twilio.video.CameraCapturer;
import com.twilio.video.ConnectOptions;
import com.twilio.video.LocalAudioTrack;
import com.twilio.video.LocalVideoTrack;
import com.twilio.video.RemoteAudioTrack;
import com.twilio.video.RemoteAudioTrackPublication;
import com.twilio.video.RemoteDataTrack;
import com.twilio.video.RemoteDataTrackPublication;
import com.twilio.video.RemoteParticipant;
import com.twilio.video.RemoteVideoTrack;
import com.twilio.video.RemoteVideoTrackPublication;
import com.twilio.video.Room;
import com.twilio.video.TwilioException;
import com.twilio.video.Video;
import com.twilio.video.VideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Snosey on 3/15/2018.
 */

public class VideoCall extends Fragment {

    private static final int REQUEST_MICROPHONE = 55;
    @InjectView(R.id.firstName)
    TextView firstName;
    @InjectView(R.id.videoViewClient)
    VideoView videoViewClient;
    @InjectView(R.id.timer)
    TextView timer;
    @InjectView(R.id.myVideoView)
    VideoView myVideoView;
    @InjectView(R.id.myVideoViewLL)
    LinearLayout myVideoViewLL;
    @InjectView(R.id.endCall)
    ImageButton endCall;
    @InjectView(R.id.voice)
    ImageButton voice;
    @InjectView(R.id.video)
    ImageButton video;
    @InjectView(R.id.next)
    ImageButton next;
    String booking_id;
    @InjectView(R.id.bottom)
    LinearLayout bottom;
    @InjectView(R.id.waitingImage)
    ImageView waitingImage;
    private Room room;
    private LocalAudioTrack localAudioTrack;
    private LocalVideoTrack localVideoTrack;
    private CameraCapturer cameraCapturer;


    CountDownTimer counter;
    private AudioManager audioManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_call, container, false);
        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.back)).setVisibility(View.VISIBLE);
        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.menu)).setVisibility(View.GONE);

        ButterKnife.inject(this, view);

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (!BuildConfig.APPLICATION_ID.contains("doctor")) {
            next.setVisibility(View.GONE);
        } else {
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString(WebService.Booking.id, booking_id);
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    MedicalReport fragment = new MedicalReport();
                    FragmentTransaction ft = fm.beginTransaction();
                    fragment.setArguments(bundle);
                    ft.add(R.id.fragment, fragment, "MedicalReport");
                    ft.addToBackStack("MedicalReport");
                    ft.commit();
                }
            });
        }
        videoViewClient.setMirror(false);
        myVideoView.setMirror(false);

        booking_id = getArguments().getString(WebService.Booking.id);

        showMe();
        getToken();
        getBooking();
        setSpeaker(true);

        //onViewClicked(view.findViewById(R.id.video));
       // onViewClicked(view.findViewById(R.id.speaker));
        return view;
    }

    void getToken() {
        UrlData urlData = new UrlData();
        urlData.add(WebService.VideoCall.id_booking, booking_id);
        try {
            urlData.add(WebService.VideoCall.id_user, MainActivity.jsonObject.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) throws JSONException {
                JSONObject jsonObject = new JSONObject(output);
                startVideo(jsonObject.getString("video_token"));
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.VideoCall.newRoomApi, urlData.get());
    }

    void stopWatch(long count) {


        counter = new CountDownTimer(count, 1000) {
            public void onTick(long millisUntilFinished) {
                try {
                    if (millisUntilFinished < TimeUnit.MINUTES.toMillis(5))
                        timer.setTextColor(Color.RED);

                    timer.setText(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) + ":" + (
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

                } catch (Exception e) {
                    counter.cancel();
                }
            }

            public void onFinish() {
                getActivity().recreate();
            }

        }.start();

    }

    private void showMe() {

        if ((ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {

            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA}, 55);
            return;

        }
        boolean enable = true;
        localAudioTrack = LocalAudioTrack.create(getContext(), enable);

// A video track requires an implementation of VideoCapturer
        cameraCapturer = new CameraCapturer(getContext(),
                CameraCapturer.CameraSource.FRONT_CAMERA);

// Create a video track
        localVideoTrack = LocalVideoTrack.create(getContext(), enable, cameraCapturer);

// Rendering a local video track requires an implementation of VideoRenderer
// Let's assume we have added a VideoView in our view hierarchy

// Render a local video track to preview your camera
        localVideoTrack.addRenderer(myVideoView);


// Release the audio track to free native memory resources
        //localAudioTrack.release();

// Release the video track to free native memory resources
        //localVideoTrack.release();
    }

    private void getBooking() {
        UrlData urlData = new UrlData();
        urlData.add(WebService.Booking.id_booking, booking_id);
        urlData.add(WebService.Booking.lang, Locale.getDefault().getLanguage());
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) throws JSONException {

                JSONObject jsonObject = new JSONObject(output).getJSONObject("booking");
                if (BuildConfig.APPLICATION_ID.contains("doctor")) {
                    if (jsonObject.getString(WebService.Booking.id_state).equals(WebService.Booking.bookingStateProcessing))
                        updateBooking(jsonObject.getString("id"), jsonObject.getString(WebService.Booking.fcm_token_client));
                } else {
                    if (jsonObject.getString(WebService.Booking.id_state).equals(WebService.Booking.bookingStateStart))
                        updateBooking(jsonObject.getString("id"), jsonObject.getString(WebService.Booking.fcm_token_client));
                }
                Calendar calendar = new GregorianCalendar();
                final long currentTimeMillis = TimeUnit.HOURS.toMillis(calendar.get(Calendar.HOUR_OF_DAY)) + TimeUnit.MINUTES.toMillis(calendar.get(Calendar.MINUTE));

                final long startTimeHour = TimeUnit.HOURS.toMillis(Long.parseLong(jsonObject.getString(WebService.Booking.receive_hour)));
                final long startTimeMin = TimeUnit.MINUTES.toMillis(Long.parseLong(jsonObject.getString(WebService.Booking.receive_minutes)));
                long duration = TimeUnit.MINUTES.toMillis(Long.parseLong(jsonObject.getString(WebService.Booking.duration)));
                long bookTotal = startTimeHour + startTimeMin + duration;

                stopWatch(bookTotal - currentTimeMillis);
                firstName.setText(jsonObject.getString(WebService.Booking.firstName));
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Booking.getBookDataApi, urlData.get());
    }

    private void updateBooking(String bookingId, final String FCM_TOKEN) {
        UrlData urlData = new UrlData();
        if (BuildConfig.APPLICATION_ID.contains("doctor")) {
            urlData.add(WebService.Booking.id_state, WebService.Booking.bookingStateStart);
        } else {
            urlData.add(WebService.Booking.id_state, WebService.Booking.bookingStateWorking);
        }
        urlData.add(WebService.Booking.id, bookingId);
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) throws JSONException {
                if (BuildConfig.APPLICATION_ID.contains("doctor"))
                    sendNotification(FCM_TOKEN);
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Booking.updateBookingApi, urlData.get());
    }

    void sendNotification(String regId) {
        final UrlData urlData = new UrlData();

        urlData.add(WebService.Notification.reg_id, regId);
        urlData.add(WebService.Notification.data, booking_id);
        urlData.add(WebService.Notification.kind, WebService.Notification.Types.video_call);
        urlData.add(WebService.Notification.message, "");
        urlData.add(WebService.Notification.title, "");

        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) {

            }
        }, getActivity(), false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Notification.notificationApi, urlData.get());

    }

    void startVideo(String TOKEN) {
        List<LocalAudioTrack> localAudioTracks = new ArrayList<>();
        localAudioTracks.add(localAudioTrack);

        List<LocalVideoTrack> localVideoTracks = new ArrayList<>();
        localVideoTracks.add(localVideoTrack);
        ConnectOptions connectOptions = null;

        Log.e("video.token:", TOKEN);

        connectOptions = new ConnectOptions.Builder(TOKEN).
                roomName(booking_id)
                .audioTracks(localAudioTracks)
                .videoTracks(localVideoTracks)
                .build();

        room = Video.connect(getActivity(), connectOptions, new Room.Listener() {
            @Override
            public void onConnected(Room room) {
                for (RemoteParticipant participant : room.getRemoteParticipants()) {
                    participant.setListener(new RemoteParticipant.Listener() {


                        @Override
                        public void onAudioTrackPublished(RemoteParticipant remoteParticipant, RemoteAudioTrackPublication remoteAudioTrackPublication) {

                        }

                        @Override
                        public void onAudioTrackUnpublished(RemoteParticipant remoteParticipant, RemoteAudioTrackPublication remoteAudioTrackPublication) {

                        }

                        @Override
                        public void onAudioTrackSubscribed(RemoteParticipant remoteParticipant, RemoteAudioTrackPublication remoteAudioTrackPublication, RemoteAudioTrack remoteAudioTrack) {

                        }

                        @Override
                        public void onAudioTrackSubscriptionFailed(RemoteParticipant remoteParticipant, RemoteAudioTrackPublication remoteAudioTrackPublication, TwilioException twilioException) {

                        }

                        @Override
                        public void onAudioTrackUnsubscribed(RemoteParticipant remoteParticipant, RemoteAudioTrackPublication remoteAudioTrackPublication, RemoteAudioTrack remoteAudioTrack) {

                        }

                        @Override
                        public void onVideoTrackPublished(RemoteParticipant remoteParticipant, RemoteVideoTrackPublication remoteVideoTrackPublication) {

                        }

                        @Override
                        public void onVideoTrackUnpublished(RemoteParticipant remoteParticipant, RemoteVideoTrackPublication remoteVideoTrackPublication) {

                        }

                        @Override
                        public void onVideoTrackSubscribed(RemoteParticipant remoteParticipant, RemoteVideoTrackPublication remoteVideoTrackPublication, RemoteVideoTrack remoteVideoTrack) {
                            remoteVideoTrack.addRenderer(videoViewClient);
                            try {
                                waitingImage.setVisibility(View.GONE);
                            } catch (Exception e) {

                            }
                        }

                        @Override
                        public void onVideoTrackSubscriptionFailed(RemoteParticipant remoteParticipant, RemoteVideoTrackPublication remoteVideoTrackPublication, TwilioException twilioException) {

                        }

                        @Override
                        public void onVideoTrackUnsubscribed(RemoteParticipant remoteParticipant, RemoteVideoTrackPublication remoteVideoTrackPublication, RemoteVideoTrack remoteVideoTrack) {
                            try {
                                waitingImage.setVisibility(View.VISIBLE);
                            } catch (Exception e) {

                            }
                        }

                        @Override
                        public void onDataTrackPublished(RemoteParticipant remoteParticipant, RemoteDataTrackPublication remoteDataTrackPublication) {

                        }

                        @Override
                        public void onDataTrackUnpublished(RemoteParticipant remoteParticipant, RemoteDataTrackPublication remoteDataTrackPublication) {

                        }

                        @Override
                        public void onDataTrackSubscribed(RemoteParticipant remoteParticipant, RemoteDataTrackPublication remoteDataTrackPublication, RemoteDataTrack remoteDataTrack) {

                        }

                        @Override
                        public void onDataTrackSubscriptionFailed(RemoteParticipant remoteParticipant, RemoteDataTrackPublication remoteDataTrackPublication, TwilioException twilioException) {

                        }

                        @Override
                        public void onDataTrackUnsubscribed(RemoteParticipant remoteParticipant, RemoteDataTrackPublication remoteDataTrackPublication, RemoteDataTrack remoteDataTrack) {

                        }

                        @Override
                        public void onAudioTrackEnabled(RemoteParticipant remoteParticipant, RemoteAudioTrackPublication remoteAudioTrackPublication) {

                        }

                        @Override
                        public void onAudioTrackDisabled(RemoteParticipant remoteParticipant, RemoteAudioTrackPublication remoteAudioTrackPublication) {

                        }

                        @Override
                        public void onVideoTrackEnabled(RemoteParticipant remoteParticipant, RemoteVideoTrackPublication remoteVideoTrackPublication) {
                            try {
                                waitingImage.setVisibility(View.VISIBLE);
                            } catch (Exception e) {

                            }
                        }

                        @Override
                        public void onVideoTrackDisabled(RemoteParticipant remoteParticipant, RemoteVideoTrackPublication remoteVideoTrackPublication) {
                            try {
                                waitingImage.setVisibility(View.GONE);
                            } catch (Exception e) {

                            }
                        }
                    });
                    try {
                        waitingImage.setVisibility(View.GONE);
                    } catch (Exception e) {

                    }
                    break;
                }

                try {
                    Toast.makeText(getContext(), "connect", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), getActivity().getString(R.string.SuccessConnected), Toast.LENGTH_LONG).show();
                } catch (Exception e) {

                }
            }

            @Override
            public void onConnectFailure(Room room, TwilioException twilioException) {
                try {
                    twilioException.printStackTrace();
                } catch (Exception e) {

                }
                Toast.makeText(getContext(), getActivity().getString(R.string.FailToConnected), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDisconnected(Room room, TwilioException twilioException) {

                try {
                    waitingImage.setVisibility(View.VISIBLE);
                    twilioException.printStackTrace();
                } catch (Exception e) {

                }
            }

            @Override
            public void onParticipantConnected(Room room, RemoteParticipant participant) {
                participant.setListener(new RemoteParticipant.Listener() {


                    @Override
                    public void onAudioTrackPublished(RemoteParticipant remoteParticipant, RemoteAudioTrackPublication remoteAudioTrackPublication) {

                    }

                    @Override
                    public void onAudioTrackUnpublished(RemoteParticipant remoteParticipant, RemoteAudioTrackPublication remoteAudioTrackPublication) {

                    }

                    @Override
                    public void onAudioTrackSubscribed(RemoteParticipant remoteParticipant, RemoteAudioTrackPublication remoteAudioTrackPublication, RemoteAudioTrack remoteAudioTrack) {

                    }

                    @Override
                    public void onAudioTrackSubscriptionFailed(RemoteParticipant remoteParticipant, RemoteAudioTrackPublication remoteAudioTrackPublication, TwilioException twilioException) {

                    }

                    @Override
                    public void onAudioTrackUnsubscribed(RemoteParticipant remoteParticipant, RemoteAudioTrackPublication remoteAudioTrackPublication, RemoteAudioTrack remoteAudioTrack) {

                    }

                    @Override
                    public void onVideoTrackPublished(RemoteParticipant remoteParticipant, RemoteVideoTrackPublication remoteVideoTrackPublication) {

                    }

                    @Override
                    public void onVideoTrackUnpublished(RemoteParticipant remoteParticipant, RemoteVideoTrackPublication remoteVideoTrackPublication) {

                    }

                    @Override
                    public void onVideoTrackSubscribed(RemoteParticipant remoteParticipant, RemoteVideoTrackPublication remoteVideoTrackPublication, RemoteVideoTrack remoteVideoTrack) {
                        remoteVideoTrack.addRenderer(videoViewClient);
                        try {
                            waitingImage.setVisibility(View.GONE);
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onVideoTrackSubscriptionFailed(RemoteParticipant remoteParticipant, RemoteVideoTrackPublication remoteVideoTrackPublication, TwilioException twilioException) {

                    }

                    @Override
                    public void onVideoTrackUnsubscribed(RemoteParticipant remoteParticipant, RemoteVideoTrackPublication remoteVideoTrackPublication, RemoteVideoTrack remoteVideoTrack) {
                        try {
                            waitingImage.setVisibility(View.VISIBLE);
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onDataTrackPublished(RemoteParticipant remoteParticipant, RemoteDataTrackPublication remoteDataTrackPublication) {

                    }

                    @Override
                    public void onDataTrackUnpublished(RemoteParticipant remoteParticipant, RemoteDataTrackPublication remoteDataTrackPublication) {

                    }

                    @Override
                    public void onDataTrackSubscribed(RemoteParticipant remoteParticipant, RemoteDataTrackPublication remoteDataTrackPublication, RemoteDataTrack remoteDataTrack) {

                    }

                    @Override
                    public void onDataTrackSubscriptionFailed(RemoteParticipant remoteParticipant, RemoteDataTrackPublication remoteDataTrackPublication, TwilioException twilioException) {

                    }

                    @Override
                    public void onDataTrackUnsubscribed(RemoteParticipant remoteParticipant, RemoteDataTrackPublication remoteDataTrackPublication, RemoteDataTrack remoteDataTrack) {

                    }

                    @Override
                    public void onAudioTrackEnabled(RemoteParticipant remoteParticipant, RemoteAudioTrackPublication remoteAudioTrackPublication) {

                    }

                    @Override
                    public void onAudioTrackDisabled(RemoteParticipant remoteParticipant, RemoteAudioTrackPublication remoteAudioTrackPublication) {

                    }

                    @Override
                    public void onVideoTrackEnabled(RemoteParticipant remoteParticipant, RemoteVideoTrackPublication remoteVideoTrackPublication) {
                        try {
                            waitingImage.setVisibility(View.VISIBLE);
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onVideoTrackDisabled(RemoteParticipant remoteParticipant, RemoteVideoTrackPublication remoteVideoTrackPublication) {
                        try {
                            waitingImage.setVisibility(View.GONE);
                        } catch (Exception e) {

                        }
                    }
                });
                Toast.makeText(getContext(), getActivity().getString(R.string.Connected), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onParticipantDisconnected(Room room, RemoteParticipant participant) {
                try {
                    waitingImage.setVisibility(View.GONE);
                } catch (Exception e) {

                }
                Toast.makeText(getContext(), getActivity().getString(R.string.Disconnected), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRecordingStarted(Room room) {

            }

            @Override
            public void onRecordingStopped(Room room) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (audioManager != null)
            setSpeaker(false);
        if (counter != null)
            counter.cancel();
        if (room != null)
            room.disconnect();
        ButterKnife.reset(this);
    }


    @SuppressLint("RestrictedApi")
    @OnClick({R.id.swipe, R.id.endCall, R.id.voice, R.id.video, R.id.speaker})
    public void onViewClicked(View view) {
        switch (view.getId()) {


            case R.id.speaker:
                if (audioManager.isSpeakerphoneOn()) {
                    ((AppCompatImageView) view).setSupportBackgroundTintList(getActivity().getResources().getColorStateList(R.color.silver));
                    setSpeaker(false);
                } else {
                    setSpeaker(true);
                    ((AppCompatImageView) view).setSupportBackgroundTintList(getActivity().getResources().getColorStateList(R.color.white));
                }
                break;

            case R.id.swipe:
                cameraCapturer.switchCamera();
                break;
            case R.id.endCall:
                getActivity().onBackPressed();
                break;
            case R.id.voice:
                if (localAudioTrack.isEnabled()) {
                    audioManager.setMicrophoneMute(false);
                    voice.setImageResource(R.drawable.voice_off);
                    localAudioTrack.enable(false);
                } else {
                    audioManager.setMicrophoneMute(true);
                    voice.setImageResource(R.drawable.open_voice);
                    localAudioTrack.enable(true);
                }
                break;
            case R.id.video:
                if (localVideoTrack.isEnabled()) {
                    video.setImageResource(R.drawable.close_video);
                    localVideoTrack.enable(false);
                    myVideoViewLL.setVisibility(View.GONE);
                } else {
                    video.setImageResource(R.drawable.open_video);
                    localVideoTrack.enable(true);
                    myVideoViewLL.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void setSpeaker(boolean isEnable) {
        if (!isEnable) {
            audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
            //audioManager.setMode(AudioManager.MODE_IN_CALL);
            audioManager.setSpeakerphoneOn(false);
        } else {
            audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
           // audioManager.setMode(AudioManager.MODE_IN_CALL);
            audioManager.setSpeakerphoneOn(true);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 55: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        showMe();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }

    }

}
