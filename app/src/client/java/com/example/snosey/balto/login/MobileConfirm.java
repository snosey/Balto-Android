package com.example.snosey.balto.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.snosey.balto.BuildConfig;
import com.example.snosey.balto.MainActivity;
import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.image.GetFileName;
import com.example.snosey.balto.Support.image.UploadImage;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;
import com.example.snosey.balto.login.NewAccountObject;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Snosey on 1/31/2018.
 */

public class MobileConfirm extends Fragment {

    boolean send = true;
    @InjectView(R.id.code)
    EditText phoneCode;

    @InjectView(R.id.promoCode)
    EditText code;
    @InjectView(R.id.checkbox)
    CheckBox checkbox;
    @InjectView(R.id.text)
    TextView textAgreement;

    private String couponIdAnother = "";
    private String couponIdCurrent = "";
    private String userIdAnother = "";
    String phone;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String codeConfirm = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_account_mobile_confirm, container, false);
        ButterKnife.inject(this, view);

        sharedPreferences = getActivity().getSharedPreferences("login_client", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        phone = ((NewAccountObject) getArguments().getSerializable("object")).phone;
        sendCode();

        textAgreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "";
                if (Locale.getDefault().getLanguage().equals("ar"))
                    url = "https://drive.google.com/open?id=1eSaJK6ZJS4N8BRCpiwIwki0DZqkJZ6UY";
                else
                    url = "https://drive.google.com/open?id=1EsOM9r5vDV-QYLb_nCvZNbWonv1L6xeA";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });
        return view;
    }

    private void sendCode() {
        if (send) {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phone,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    getActivity(),               // Activity (for callback binding)
                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                            codeConfirm = phoneAuthCredential.getSmsCode();
                            // Log.e("Code:", code);
                            stopResend();
                            phoneCode.addTextChangedListener(new TextWatcher() {
                                public void afterTextChanged(Editable s) {
                                }

                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }

                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    if (s.toString().length() == 6) {
                                        if (code.equals(s.toString()) && checkbox.isChecked()) {
                                            // regNewAccount();
                                        }
                                    }
                                }
                            });
                        }

                        @Override
                        public void onVerificationFailed(FirebaseException e) {
                            Log.e("Error SMS:", e.getMessage());
                        }
                    });        // OnVerificationStateChangedCallbacks

            //   final String codeText = (String.format(Locale.US, "%04d", new Random().nextInt(10000)));
            // Log.e("Code:", codeText);
            //  Toast.makeText(getContext(), codeText, Toast.LENGTH_LONG).show();
            //String smsAddition = "&Mobile=" + phone + "&message=Your confirmation code is:" + codeText;
          /*  new GetData(new GetData.AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    stopResend();
                    phoneCode.addTextChangedListener(new TextWatcher() {
                        public void afterTextChanged(Editable s) {
                        }

                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (s.toString().length() == 4) {
                                if (codeText.equals(s.toString()))
                                    regNewAccount();
                            }
                        }
                    });
                }
            }, getActivity(), true).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, WebService.SMS.fullUrl, smsAddition);
        */
        }


    }

    private void regNewAccount() {
        View keyboard = getActivity().getCurrentFocus();
        if (keyboard != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(keyboard.getWindowToken(), 0);
        }
        final NewAccountObject accountObject = (NewAccountObject) getArguments().getSerializable("object");
        if (!accountObject.logo.equals("") && !accountObject.logo.startsWith("http")) {
            List<Uri> uris = new ArrayList<>();
            uris.add(Uri.parse(accountObject.logo));
            new UploadImage(new UploadImage.AsyncResponse() {
                @Override
                public void processFinish(boolean output) {

                }
            }, getActivity(), uris);
            accountObject.logo = new GetFileName(uris.get(0), getActivity()).FileName();
        }
        UrlData urlData = new UrlData();
        urlData.add(WebService.SignUp.fcm_token, FirebaseInstanceId.getInstance().getToken());
        urlData.add(WebService.SignUp.first_name_ar, accountObject.firstName);
        urlData.add(WebService.SignUp.first_name_en, accountObject.firstName);
        urlData.add(WebService.SignUp.last_name_ar, accountObject.lastName);
        urlData.add(WebService.SignUp.last_name_en, accountObject.lastName);
        urlData.add(WebService.SignUp.id_provider, accountObject.id_provider);
        urlData.add(WebService.SignUp.type, WebService.SignUp.typeClient);
        urlData.add(WebService.SignUp.phone, accountObject.phone);
        urlData.add(WebService.SignUp.image, accountObject.logo);
        urlData.add(WebService.SignUp.provider_kind, accountObject.provider_kind);
        urlData.add(WebService.SignUp.id_gender, accountObject.gender);
        urlData.add(WebService.SignUp.email, accountObject.email);
        urlData.add(WebService.SignUp.password, accountObject.password);
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) {
              //  Toast.makeText(getActivity(), output, Toast.LENGTH_SHORT).show();
                if (output.contains("true")) {
                    try {
                        String userId = new JSONObject(output).getJSONObject("user").getString("id");

                        if (accountObject.id_provider.equals("")) {
                            editor.putString("email", accountObject.email);
                            editor.putString("password", accountObject.password);
                            editor.putString("type", "email");
                        } else {
                            editor.putString("type", "facebook");
                        }
                        editor.commit();

                        startNewActivity(new JSONObject(output).getJSONObject("user").toString());
                        if (!code.isEnabled()) {
                            addPromoForUser(couponIdCurrent, userId);
                            addPromoForUser(couponIdAnother, userIdAnother);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, WebService.SignUp.signUpRequestApi, urlData.get());


    }


    private void startNewActivity(String json) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("userData", json);
        startActivity(intent);
        getActivity().finish();
    }

    void stopResend() {
        if (send) {
            send = false;
            new CountDownTimer(60000, 1000) {

                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    send = true;
                }
            }.start();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick({R.id.resend, R.id.confirm})
    public void onViewClicked(final View view) {
        switch (view.getId()) {
            case R.id.resend:
                sendCode();
                break;
            case R.id.confirm:
                View keyboard = getActivity().getCurrentFocus();
                if (keyboard != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(keyboard.getWindowToken(), 0);
                }
                UrlData urlDataPromo = new UrlData();
                urlDataPromo.add(WebService.PromoCode.code, code.getText().toString());
                new GetData(new GetData.AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                        if (output.contains("false"))
                            Toast.makeText(getActivity(), getActivity().getString(R.string.codeIsInvalid), Toast.LENGTH_SHORT).show();
                        else {
                            try {
                                JSONObject jsonObject = new JSONObject(output).getJSONObject("coupon");
                                Toast.makeText(getActivity(), jsonObject.getString("coupon_text"), Toast.LENGTH_LONG).show();
                                ((Button) view).setVisibility(View.GONE);
                                code.setEnabled(false);
                                userIdAnother = (jsonObject.getString(WebService.PromoCode.added_by));
                                couponIdCurrent = (jsonObject.getString("id"));
                                newPromoCode();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, getActivity(), true).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, WebService.PromoCode.promoCodeCheckApi, urlDataPromo.get());
                break;
        }
    }

    private void newPromoCode() {
        UrlData urlData = new UrlData();
        urlData.add(WebService.PromoCode.added_by, "registration");
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (output.contains("true")) {
                    try {
                        JSONObject jsonObject = new JSONObject(output);
                        couponIdAnother = jsonObject.getString("id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, WebService.PromoCode.newPromoCodeApi, urlData.get());
    }

    private void addPromoForUser(String couponId, String userId) {
        UrlData urlData = new UrlData();
        urlData.add(WebService.PromoCode.id_user, userId);
        urlData.add(WebService.PromoCode.id_coupon, couponId);
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) {

            }
        }, getActivity(), true).
                executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.PromoCode.addPromoCodeToUserApi, urlData.get());
    }

    public String convertToEnglish(String value) {
        String newValue = (((((((((((value + "").replaceAll
                ("١", "1")).replaceAll
                ("٢", "2")).replaceAll
                ("٣", "3")).replaceAll
                ("٤", "4")).replaceAll
                ("٥", "5")).replaceAll
                ("٦", "6")).replaceAll
                ("٧", "7")).replaceAll
                ("٨", "8")).replaceAll
                ("٩", "9")).replaceAll
                ("٠", "0"));
        return newValue;
    }

    @OnClick(R.id.register)
    public void onViewClicked() {
        if (checkbox.isChecked()) {
            if (codeConfirm.equals(phoneCode.getText().toString()))
                regNewAccount();
        } else
            Toast.makeText(getActivity(), getActivity().getString(R.string.agreeConditions), Toast.LENGTH_SHORT).show();
    }
}
