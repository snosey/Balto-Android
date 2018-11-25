package com.example.snosey.balto.main;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.snosey.balto.MainActivity;
import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.CustomTextView;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;
import com.example.snosey.balto.login.RegistrationActivity;
import com.example.snosey.balto.payment.PaymentSlider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Snosey on 9/17/2018.
 */

public class Wallet extends Fragment {

    @InjectView(R.id.Balance)
    CustomTextView balance;
    @InjectView(R.id.totalOutstanding)
    CustomTextView totalOutstanding;
    @InjectView(R.id.totalDirectCredit)
    CustomTextView totalDirectCredit;
    @InjectView(R.id.transactionNumber)
    CustomTextView transactionNumber;
    @InjectView(R.id.walletRV)
    RecyclerView walletRV;
    @InjectView(R.id.addToWallet)
    AppCompatButton addToWallet;
    JSONArray jsonArray;
    private WalletAdapter walletAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wallet, container, false);
        ButterKnife.inject(this, view);
        jsonArray = new JSONArray();
        walletAdapter = new WalletAdapter();
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        walletRV.setLayoutManager(layoutManager);
        walletRV.setAdapter(walletAdapter);

        getTransactions();
        addToWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                PaymentSlider fragment = new PaymentSlider();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragment, fragment, "PaymentSlider");
                ft.addToBackStack("PaymentSlider");
                ft.commit();
            }
        });
        return view;
    }

    private void getTransactions() {
        UrlData urlData = new UrlData();
        try {
            urlData.add(WebService.Payment.id, MainActivity.jsonObject.getString(WebService.Payment.id));
            urlData.add(WebService.Payment.lng, RegistrationActivity.sharedPreferences.getString("lang", "en"));
            new GetData(new GetData.AsyncResponse() {
                @Override
                public void processFinish(String output) throws JSONException {
                    Log.e("transaction output", output);
                    JSONObject jsonObject = new JSONObject(output);
                    if (jsonObject.getString(WebService.Payment.status).equals("0")) {
                        Toast.makeText(getActivity(), jsonObject.getString(WebService.Payment.error), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    jsonArray = jsonObject.getJSONArray(WebService.Payment.data);
                    transactionNumber.setText(getActivity().getString(R.string.transactionNumber) + jsonArray.length());
                    balance.setText(getActivity().getString(R.string.Settled) + jsonObject.getString(WebService.Payment.total_amount) + " " + getActivity().getString(R.string.egp));
                    totalOutstanding.setText(getActivity().getString(R.string.TotalOutstanding) + jsonObject.getString(WebService.Payment.total_outstanding) + " " + getActivity().getString(R.string.egp));
                    totalDirectCredit.setText(getActivity().getString(R.string.TotalPaidCredit) + jsonObject.getString(WebService.Payment.total_credit) + " " + getActivity().getString(R.string.egp));
                    walletAdapter.notifyDataSetChanged();
                }
            }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Payment.selectUserTransactionApi, urlData.get());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


    private class WalletAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, final int position) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.wallet_row, parent, false);
            return new MyViewHolder(view);
        }

        @SuppressLint("RestrictedApi")
        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(position);
                holder.amount.setText(getActivity().getString(R.string.Amount) + jsonObject.getString(WebService.Payment.amount) + " " + getActivity().getString(R.string.egp));
                holder.paymentWay.setText(getActivity().getString(R.string.paymentWay) + " " + jsonObject.getString(WebService.Payment.paymentName));
                holder.date.setText(jsonObject.getString(WebService.Payment.created_at));
                holder.state.setText(getActivity().getString(R.string.state) + " " + jsonObject.getString(WebService.Payment.state));
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
        CustomTextView date, state, amount, paymentWay;

        public MyViewHolder(View v) {
            super(v);
            date = (CustomTextView) v.findViewById(R.id.date);
            state = (CustomTextView) v.findViewById(R.id.state);
            paymentWay = (CustomTextView) v.findViewById(R.id.paymentWay);
            amount = (CustomTextView) v.findViewById(R.id.amount);
        }
    }

}
