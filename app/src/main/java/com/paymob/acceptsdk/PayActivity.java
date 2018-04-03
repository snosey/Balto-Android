package com.paymob.acceptsdk;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import morxander.editcard.EditCard;

/**
 * Created by MTawfik on Aug/15/17.
 */

public class PayActivity extends AppCompatActivity implements OnClickListener, AsyncResponse {

    enum Status {IDLE, PROCESSING}

    ;

    // PayActivity parameters
    JSONObject billingData;
    String paymentKey;
    String token;
    String maskedPanNumber;
    Boolean showAlerts;
    Boolean saveCardDefault;
    Boolean showSaveCard;

    // PayActivity Views
    EditText nameText;
    EditCard cardNumberText;
    EditText monthText;
    EditText yearText;
    EditText cvvText;
    android.support.v7.widget.AppCompatCheckBox saveCardCheckBox;
    TextView saveCardText;
    Button payBtn;

    LinearLayout cardName_linearLayout;
    LinearLayout expiration_linearLayout;
    LinearLayout saveCard_linearLayout;

    ProgressDialog mProgressDialog;

    String verificationActivity_title;
    int themeColor;
    Status status;

    JSONObject payDict;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.snosey.balto.R.layout.activity_card_information);

        resetVariables();
        getAcceptParameters();
        linkViews();
        updateLayout();
    }

    @Override
    public void onBackPressed() {
        onCancelPress();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (token == null) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.scan_card, menu);
        }
        return true;
    }

    public void onClick(View v) {
        if (v.getId() == R.id.pay) {
            handlePayment();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onCancelPress();
        } else if (item.getItemId() == R.id.action_scan) {
            onScanPress();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onScanPress() {
        Intent scanIntent = new Intent(this, CardIOActivity.class);

        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CARDHOLDER_NAME, true);
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true);
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true);

        startActivityForResult(scanIntent, IntentConstants.CARD_IO_SCAN_REQUEST);
    }

    private void onCancelPress() {
        if (status == Status.IDLE) {
            Intent canceledIntent = new Intent();
            setResult(IntentConstants.USER_CANCELED, canceledIntent);
            finish();
        }
    }

    private void handlePayment() {

        String nameString = nameText.getText().toString();
        String numberString = cardNumberText.getCardNumber();
        String monthString = monthText.getText().toString();
        String yearString = yearText.getText().toString();
        String cvvString = cvvText.getText().toString();


        SharedPreferences sharedPreferences = getSharedPreferences("payment", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("nameString", nameString);
        editor.putString("numberString", numberString);
        editor.putString("monthString", monthString);
        editor.putString("yearString", yearString);
        editor.putString("cvvString", cvvString);
        //editor.commit();


        if (!FormChecker.checkCVV(cvvString)) {
            AlertShower.showSweetAlert(this, "Error", "CVV must be 3 digits", SweetAlertDialog.WARNING_TYPE, "Ok");
            return;
        }

        JSONObject cardData = new JSONObject();

        if (token != null) {
            try {
                cardData.put(PayRequestKeys.IDENTIFIER, token);
                cardData.put(PayRequestKeys.SUBTYPE, "TOKEN");
                cardData.put(PayRequestKeys.CVN, cvvString);
            } catch (JSONException J) {
                return;
            }
        } else {
            if (!FormChecker.checkCardName(nameString)) {
                AlertShower.showSweetAlert(this, "Error", "Name can't be empty", SweetAlertDialog.WARNING_TYPE, "Ok");
                return;
            }
            if (!FormChecker.checkCardNumber(numberString)) {
                AlertShower.showSweetAlert(this, "Error", "Card Number must be 16 digits!", SweetAlertDialog.WARNING_TYPE, "Ok");
                return;
            }
            if (!FormChecker.checkDate(monthString, yearString)) {
                AlertShower.showSweetAlert(this, "Error", "Invalid Date!", SweetAlertDialog.WARNING_TYPE, "Ok");
                return;
            }

            try {
                cardData.put(PayRequestKeys.IDENTIFIER, numberString);
                cardData.put(PayRequestKeys.SOURCEHOLDER_NAME, nameString);
                cardData.put(PayRequestKeys.SUBTYPE, "CARD");
                cardData.put(PayRequestKeys.EXPIRY_MONTH, monthString);
                cardData.put(PayRequestKeys.EXPIRY_YEAR, yearString);
                cardData.put(PayRequestKeys.CVN, cvvString);
            } catch (JSONException J) {
                return;
            }
        }
        try {
            payAPIRequest(cardData);
        } catch (JSONException J) {
            if (showAlerts) {
                AlertShower.showSweetAlertAndNotify(this, "Error", "\"An error occured while handling payment response\"", SweetAlertDialog.ERROR_TYPE, "Ok", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        PayActivity.this.notifyErrorTransaction("An error occured while handling payment response");
                    }
                });
            } else {
                notifyErrorTransaction("An error occured while handling payment response");
            }
        }
    }

    void payAPIRequest(JSONObject cardData) throws JSONException {
        JSONObject params = new JSONObject();
        params.put(PayRequestKeys.SOURCE, cardData);
        params.put(PayRequestKeys.BILLING, billingData);
        params.put(PayRequestKeys.PAYMENT_TOKEN, paymentKey);

        HttpConnectionInitiator httpConnectionInitiator = new HttpConnectionInitiator(getApplicationContext());
        httpConnectionInitiator.delegate = this;
        httpConnectionInitiator.execute(ServerUrls.API_NAME_USER_PAYMENT, params.toString());

        showProgressDialog();
    }

    private void open3DSecureView(String url) {
        Intent threeDSecureViewIntent = new Intent(this, ThreeDSecureWebViewActivty.class);
        threeDSecureViewIntent.putExtra(ThreeDSecureIntentKeys.THREE_D_SECURE_URL, url);
        threeDSecureViewIntent.putExtra(ThreeDSecureIntentKeys.THEME_COLOR, themeColor);
        threeDSecureViewIntent.putExtra(ThreeDSecureIntentKeys.THREE_D_SECURE_ACTIVITY_TITLE, verificationActivity_title);
        startActivityForResult(threeDSecureViewIntent, IntentConstants.THREE_D_SECURE_VERIFICATION_REQUEST);
    }

    @Override
    public void processFinish(String apiName, final String output) {
        dismissProgressDialog();

        // In case of card payment API call
        if (apiName.equalsIgnoreCase(ServerUrls.API_NAME_USER_PAYMENT)) {
            try {
                JSONObject jsonResult = new JSONObject(output);
                String direct3dSecure = jsonResult.getString(PayResponseKeys.IS_3D_SECURE);
                if (direct3dSecure != null) {
                    payDict = jsonResult;
                    if (direct3dSecure.equals("true")) {
                        String redirectionURL = jsonResult.getString(PayResponseKeys.REDIRECTION_URL);
                        if (redirectionURL != null) {
                            open3DSecureView(redirectionURL);
                        } else {
                            dismissProgressDialog();
                            if (showAlerts) {
                                AlertShower.showSweetAlertAndNotify(this, "Error",
                                        "An error occured while reading the 3dsecure redirection URL",
                                        SweetAlertDialog.ERROR_TYPE, "Ok",
                                        new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismiss();
                                                PayActivity.this.notifyErrorTransaction("An error occured while reading the 3dsecure redirection URL");
                                            }
                                        });
                            } else {
                                notifyErrorTransaction("An error occured while reading the 3dsecure redirection URL");
                            }
                        }
                    } else {
                        paymentInquiry();
                    }
                } else {
                    dismissProgressDialog();
                    if (showAlerts) {
                        AlertShower.showSweetAlertAndNotify(this, "Error", "An error occured when checking if the card is 3d secure", SweetAlertDialog.ERROR_TYPE, "Ok", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                                PayActivity.this.notifyErrorTransaction("An error occured while checking if the card is 3d secure");
                            }
                        });
                    } else {
                        notifyErrorTransaction("An error occured while checking if the card is 3d secure");
                    }
                }
            } catch (Exception ex) {
                dismissProgressDialog();
                if (showAlerts) {
                    AlertShower.showSweetAlertAndNotify(this, "Error", ex.getMessage(), SweetAlertDialog.ERROR_TYPE, "Ok", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                            PayActivity.this.notifyErrorTransaction("An error occured while parsing payment response");

                        }
                    });
                } else {
                    // notifyErrorTransaction("An error occured while parsing payment response");
                    notifyErrorTransaction(ex.getMessage());
                }
            }
        } else if (apiName.equalsIgnoreCase(ServerUrls.API_NAME_TOKENIZE_CARD)) {
            dismissProgressDialog();
            if (showAlerts) {
                AlertShower.showSweetAlertAndNotify(this, "Transaction successful", null, SweetAlertDialog.SUCCESS_TYPE, "Ok", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        PayActivity.this.notifySuccessfulTransactionSaveCard(output);
                    }
                });
            } else {
                notifySuccessfulTransactionSaveCard(output);
            }
        }
    }

    private void paymentInquiry() {
        try {
            // Log.d("Accept - payDict", payDict.toString());
            String success = payDict.getString(PayResponseKeys.SUCCESS);
            if (success.equals("true")) {
                if (saveCardCheckBox.isChecked()) {
                    JSONObject cardData = new JSONObject();

                    cardData.put(SaveCardRequestKeys.PAN, cardNumberText.getCardNumber());
                    cardData.put(SaveCardRequestKeys.CARDHOLDER_NAME, nameText.getText().toString());
                    cardData.put(SaveCardRequestKeys.EXPIRY_MONTH, monthText.getText().toString());
                    cardData.put(SaveCardRequestKeys.EXPIRY_YEAR, yearText.getText().toString());
                    cardData.put(SaveCardRequestKeys.CVN, cvvText.getText().toString());

                    HttpConnectionInitiator httpConnectionInitiator = new HttpConnectionInitiator(getApplicationContext());
                    httpConnectionInitiator.delegate = this;
                    httpConnectionInitiator.execute(ServerUrls.API_NAME_TOKENIZE_CARD, cardData.toString(), paymentKey);
                } else {
                    dismissProgressDialog();
                    if (showAlerts) {
                        AlertShower.showSweetAlertAndNotify(this, "Transaction successful", null, SweetAlertDialog.SUCCESS_TYPE, "Ok", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                                PayActivity.this.notifySuccesfulTransaction();
                            }
                        });
                    } else {
                        notifySuccesfulTransaction();
                    }
                }
            } else {
                dismissProgressDialog();
                if (showAlerts) {
                    AlertShower.showSweetAlertAndNotify(this, "Error", payDict.getString(PayResponseKeys.DATA_MESSAGE), SweetAlertDialog.ERROR_TYPE, "Ok", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                            PayActivity.this.notifyRejectedTransaction();
                        }
                    });
                } else {
                    notifyRejectedTransaction();
                }
            }
        } catch (JSONException J) {
            if (showAlerts) {
                AlertShower.showSweetAlertAndNotify(this, "Error", "Error occured while reading returned message", SweetAlertDialog.ERROR_TYPE, "Ok", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        PayActivity.this.notifyErrorTransaction("An error occured while reading returned message");
                    }
                });
            } else {
                notifyErrorTransaction("An error occured while reading returned message");
            }
        }
    }

    private void notifySuccessfulTransactionSaveCard(String saveCardData) {
        Intent successIntent = new Intent();

        try {
            JSONObject savedCardDict = new JSONObject(saveCardData);
            putSaveCardData(successIntent, savedCardDict);
            putPayDataInIntent(successIntent);
        } catch (JSONException J) {
            notifySuccesfulTransaction();
            return;
        }
        setResult(IntentConstants.TRANSACTION_SUCCESSFUL_CARD_SAVED, successIntent);
        finish();
    }

    private void putSaveCardData(Intent intent, JSONObject saveCardData) {
        for (int i = 0; i < SaveCardResponseKeys.SAVE_CARD_DICT_KEYS.length; i++) {
            try {
                intent.putExtra(SaveCardResponseKeys.SAVE_CARD_DICT_KEYS[i],
                        saveCardData.getString(SaveCardResponseKeys.SAVE_CARD_DICT_KEYS[i]));
            } catch (JSONException ignored) {

            }
        }
    }

    private void notifyErrorTransaction(String reason) {
        Intent errorIntent = new Intent();
        errorIntent.putExtra(IntentConstants.TRANSACTION_ERROR_REASON, reason);
        setResult(IntentConstants.TRANSACTION_ERROR, errorIntent);
        finish();
    }

    private void notifyCancel3dSecure() {
        Intent cancel3dSecureIntent = new Intent();
        try {
            putPayDataInIntent(cancel3dSecureIntent);
            setResult(IntentConstants.USER_CANCELED_3D_SECURE_VERIFICATION, cancel3dSecureIntent);
        } catch (JSONException J) {
            cancel3dSecureIntent.putExtra(IntentConstants.RAW_PAY_RESPONSE, payDict.toString());
            setResult(IntentConstants.USER_CANCELED_3D_SECURE_VERIFICATION_PARSING_ISSUE, cancel3dSecureIntent);
        }
        finish();
    }

    private void notifyRejectedTransaction() {
        Intent rejectIntent = new Intent();
        try {
            putPayDataInIntent(rejectIntent);
            setResult(IntentConstants.TRANSACTION_REJECTED, rejectIntent);
        } catch (JSONException J) {
            rejectIntent.putExtra(IntentConstants.RAW_PAY_RESPONSE, payDict.toString());
            setResult(IntentConstants.TRANSACTION_REJECTED_PARSING_ISSUE, rejectIntent);
        }
        finish();
    }

    private void notifySuccesfulTransaction() {
        Intent successIntent = new Intent();
        try {
            putPayDataInIntent(successIntent);
            setResult(IntentConstants.TRANSACTION_SUCCESSFUL, successIntent);
            finish();
        } catch (JSONException J) {
            notifySuccesfulTransactionParsingIssue(payDict.toString());
        }
    }

    private void notifySuccesfulTransactionParsingIssue(String raw_pay_response) {
        Intent successIntent = new Intent();
        successIntent.putExtra(IntentConstants.RAW_PAY_RESPONSE, raw_pay_response);
        setResult(IntentConstants.TRANSACTION_SUCCESSFUL_PARSING_ISSUE, successIntent);
        finish();
    }

    private void putPayDataInIntent(Intent intent) throws JSONException {
        for (int i = 0; i < PayResponseKeys.PAY_DICT_KEYS.length; i++) {
            intent.putExtra(PayResponseKeys.PAY_DICT_KEYS[i], payDict.getString(PayResponseKeys.PAY_DICT_KEYS[i]));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IntentConstants.CARD_IO_SCAN_REQUEST) {
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                cardNumberText.setText(scanResult.cardNumber);
                cardNumberText.setText(StringEditor.insertPeriodically(scanResult.cardNumber, "-", 4));
                nameText.setText(scanResult.cardholderName);
                if (scanResult.isExpiryValid()) {
                    monthText.setText(StringEditor.monthString(scanResult.expiryMonth));
                    yearText.setText(StringEditor.yearString(scanResult.expiryYear));
                }
                if (scanResult.cvv != null) {
                    cvvText.setText(scanResult.cvv);
                }
            }
        } else if (requestCode == IntentConstants.THREE_D_SECURE_VERIFICATION_REQUEST) {
            if (resultCode == IntentConstants.MISSING_ARGUMENT) {
                notifyCancel3dSecure();
            } else if (resultCode == IntentConstants.USER_CANCELED) {
                notifyCancel3dSecure();
            } else if (resultCode == IntentConstants.USER_FINISHED_3D_VERIFICATION) {
                String raw_pay_response = data.getStringExtra(ThreeDSecureResponseKeys.RAW_PAY_RESPONSE);
                // Log.d("Accept3dsecure", raw_pay_response);
                try {
                    payDict = new JSONObject(raw_pay_response);
                    //ToastMaker.displayLongToast(this, "Parsing pay response success");
                    // Log.d("Accept3dsecure", "Parsing pay response success");
                    paymentInquiry();
                } catch (Exception E) {
                    // ToastMaker.displayLongToast(this, "Exception thrown while parsing raw pay response");
                    // Log.d("Accept3dsecure", "Exception thrown while parsing raw pay response");
                    notifySuccesfulTransactionParsingIssue(raw_pay_response);
                }
            }
        }
    }

    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Processing payment...");
        mProgressDialog.setMessage("Please wait.");
        mProgressDialog.setCancelable(false);
        status = Status.PROCESSING;
        mProgressDialog.show();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        status = Status.IDLE;
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void linkViews() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ColorDrawable colorDrawable = new ColorDrawable(themeColor);
        actionBar.setBackgroundDrawable(colorDrawable);

        nameText = findViewById(R.id.cardName);

        cardNumberText = findViewById(R.id.cardNumber);

        monthText = findViewById(R.id.expiryMonth);
        monthText.setFilters(new InputFilter[]{new ExpiryMonthInputFilter()});

        yearText = findViewById(R.id.expiryYear);
        yearText.setFilters(new InputFilter[]{new ExpiryYearInputFilter()});

        cvvText = findViewById(R.id.cvv);

        saveCardCheckBox = findViewById(R.id.saveCardCheckBox);
        saveCardText = findViewById(R.id.saveCardText);

        payBtn = findViewById(R.id.pay);
        payBtn.setText("Confirm");
        payBtn.setOnClickListener(this);
        ((GradientDrawable) payBtn.getBackground()).setColor(themeColor);

        cardName_linearLayout = findViewById(R.id.cardName_linearLayout);
        expiration_linearLayout = findViewById(R.id.expiration_linearLayout);
        saveCard_linearLayout = findViewById(R.id.saveCard_linearLayout);


        SharedPreferences sharedPreferences = getSharedPreferences("payment", Context.MODE_PRIVATE);
        nameText.setText(sharedPreferences.getString("nameString", ""));
        cardNumberText.setText(sharedPreferences.getString("numberString", ""));
        monthText.setText(sharedPreferences.getString("monthString", ""));
        yearText.setText(sharedPreferences.getString("yearString", ""));
        cvvText.setText(sharedPreferences.getString("cvvString", ""));
    }

    private void getAcceptParameters() {
        Intent intent = getIntent();
        billingData = new JSONObject();
        readBillingData(intent);
        paymentKey = intent.getStringExtra(PayActivityIntentKeys.PAYMENT_KEY);
        checkIfPassed(PayActivityIntentKeys.PAYMENT_KEY, paymentKey);
        token = intent.getStringExtra(PayActivityIntentKeys.TOKEN);
        maskedPanNumber = intent.getStringExtra(PayActivityIntentKeys.MASKED_PAN_NUMBER);
        if (token != null) {
            checkIfPassed(PayActivityIntentKeys.MASKED_PAN_NUMBER, maskedPanNumber);
        }
        showAlerts = intent.getBooleanExtra(PayActivityIntentKeys.SHOW_ALERTS, true);
        saveCardDefault = intent.getBooleanExtra(PayActivityIntentKeys.SAVE_CARD_DEFAULT, false);
        showSaveCard = intent.getBooleanExtra(PayActivityIntentKeys.SHOW_SAVE_CARD, true);
        themeColor = intent.getIntExtra(PayActivityIntentKeys.THEME_COLOR, getResources().getColor(R.color.colorDefaultButton));
        verificationActivity_title = intent.getStringExtra(PayActivityIntentKeys.THREE_D_SECURE_ACTIVITY_TITLE);
        if (verificationActivity_title == null) {
            verificationActivity_title = "3d-secure Verification";
        }
    }

    private void readBillingData(Intent intent) {
        try {
            readBillingValue(intent, PayActivityIntentKeys.FIRST_NAME);
            readBillingValue(intent, PayActivityIntentKeys.LAST_NAME);
            readBillingValue(intent, PayActivityIntentKeys.BUILDING);
            readBillingValue(intent, PayActivityIntentKeys.FLOOR);
            readBillingValue(intent, PayActivityIntentKeys.APARTMENT);
            readBillingValue(intent, PayActivityIntentKeys.CITY);
            readBillingValue(intent, PayActivityIntentKeys.STATE);
            readBillingValue(intent, PayActivityIntentKeys.COUNTRY);
            readBillingValue(intent, PayActivityIntentKeys.EMAIL);
            readBillingValue(intent, PayActivityIntentKeys.PHONE_NUMBER);
            readBillingValue(intent, PayActivityIntentKeys.POSTAL_CODE);
        } catch (JSONException J) {
        }
    }

    private void readBillingValue(Intent intent, String key) throws JSONException {
        String value = intent.getStringExtra(key);
        checkIfPassed(key, value);
        billingData.put(key, value);
    }

    private void checkIfPassed(String key, String value) {
        if (value == null) {
            abortForNotPassed(key);
        }
    }

    private void abortForNotPassed(String key) {
        Intent errorIntent = new Intent();
        errorIntent.putExtra(IntentConstants.MISSING_ARGUMENT_VALUE, key);
        setResult(IntentConstants.MISSING_ARGUMENT, errorIntent);
        finish();
    }

    private void resetVariables() {
        billingData = null;
        paymentKey = null;
        token = null;
        maskedPanNumber = null;
        showAlerts = true;
        showSaveCard = true;
        saveCardDefault = false;
        mProgressDialog = null;
        payDict = null;
        verificationActivity_title = null;
        status = Status.IDLE;
    }

    private void updateLayout() {
        saveCardCheckBox.setChecked(saveCardDefault);
        saveCardCheckBox.setClickable(showSaveCard);
        ColorEditor.setAppCompatCheckBoxColors(saveCardCheckBox, 0x80808080, themeColor);
        if (!showSaveCard) {
            saveCardCheckBox.setVisibility(View.GONE);
            if (saveCardDefault)
                saveCardText.setText("Your card will be saved for future use");
            else
                saveCard_linearLayout.setVisibility(View.GONE);
        }
        if (token != null) {
            invalidateOptionsMenu();
            cardName_linearLayout.setVisibility(View.GONE);
            expiration_linearLayout.setVisibility(View.GONE);
            saveCardCheckBox.setChecked(false);
            saveCard_linearLayout.setVisibility(View.GONE);
            cardNumberText.setHint(maskedPanNumber);
            cardNumberText.setHintTextColor(getResources().getColor(R.color.colorText));
            cardNumberText.setEnabled(false);
            cardNumberText.setFocusable(false);

        }
    }
}
