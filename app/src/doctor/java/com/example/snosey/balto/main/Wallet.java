package com.example.snosey.balto.main;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.snosey.balto.MainActivity;
import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Snosey on 3/24/2018.
 */

public class Wallet extends Fragment {

    @InjectView(R.id.moneyOnYou)
    TextView moneyOnYou;
    @InjectView(R.id.moneyForYou)
    TextView moneyForYou;

    @InjectView(R.id.dueTo)
    TextView dueTo;


    JSONArray walletJsonArray;
    WalletAdapter walletAdapter;
    RecyclerView walletRV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wallet, container, false);
        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.back)).setVisibility(View.VISIBLE);
        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.menu)).setVisibility(View.GONE);
        ButterKnife.inject(this, view);

        Calendar calendar = new GregorianCalendar();
        if (calendar.get(Calendar.DAY_OF_MONTH) > 15) {
            calendar.add(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
        } else {
            calendar.set(Calendar.DAY_OF_MONTH, 16);
        }

        String date = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        dueTo.setText(getActivity().getString(R.string.dueTo) + date);
        walletJsonArray = new JSONArray();
        walletAdapter = new WalletAdapter();
        walletRV = (RecyclerView) view.findViewById(R.id.walletRV);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        walletRV.setLayoutManager(layoutManager);
        walletRV.setAdapter(walletAdapter);


        try {
            setData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    private void setData() throws JSONException {
        UrlData urlData = new UrlData();
        urlData.add(WebService.Payment.id_user, MainActivity.jsonObject.getString("id"));
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) throws JSONException {
                JSONObject jsonObject = new JSONObject(output);

                String moneyForDoctor = "0";
                if (jsonObject.getString(WebService.Payment.allPaymentTodoctor).contains("-"))
                    moneyForDoctor = jsonObject.getString(WebService.Payment.allPaymentTodoctor).replace("-", "");

                String moneyOnDoctor = "0";
                if (jsonObject.getString(WebService.Payment.allPaymentToadmin).contains("-"))
                    moneyOnDoctor = jsonObject.getString(WebService.Payment.allPaymentToadmin).replace("-", "");

                moneyForYou.setText(getActivity().getString(R.string.moneyForYou) + " " + moneyForDoctor + " " + getActivity().getString(R.string.egp));
                moneyOnYou.setText(getActivity().getString(R.string.moneyOnYou) + " " + moneyOnDoctor + " " + getActivity().getString(R.string.egp));
                walletJsonArray = jsonObject.getJSONArray("allPayment");
                walletAdapter.notifyDataSetChanged();
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Payment.getDoctorPayment, urlData.get());
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

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            try {
                JSONObject payment = walletJsonArray.getJSONObject(position);
                if (payment.getString(WebService.Payment.id_payment_way).equals(WebService.Booking.cash))
                    holder.payment.setText(getActivity().getString(R.string.cash));
                else
                    holder.payment.setText(getActivity().getString(R.string.credit));

                holder.you.setText(payment.getString(WebService.Payment.doctor_money));
                holder.admin.setText(payment.getString(WebService.Payment.admin_money));
                holder.date.setText(payment.getString("created_at").substring(0, 9));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return (int) walletJsonArray.length();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView date, admin, you, payment;

        public MyViewHolder(View v) {
            super(v);
            date = (TextView) v.findViewById(R.id.date);
            you = (TextView) v.findViewById(R.id.doctor);
            admin = (TextView) v.findViewById(R.id.admin);
            payment = (TextView) v.findViewById(R.id.paymentWay);
        }
    }


}
