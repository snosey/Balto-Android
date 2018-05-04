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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Snosey on 3/24/2018.
 */

public class Wallet extends Fragment {

    @InjectView(R.id.dueTo)
    TextView dueTo;


    JSONArray walletJsonArray;
    WalletAdapter walletAdapter;
    RecyclerView walletRV;
    @InjectView(R.id.transactionNumber)
    TextView transactionNumber;
    @InjectView(R.id.Pending)
    TextView Pending;
    @InjectView(R.id.Balance)
    TextView Balance;
    @InjectView(R.id.allMoney)
    TextView allMoney;
    @InjectView(R.id.CashReceived)
    TextView CashReceived;
    private GregorianCalendar dueCalendar;
    int currentMonth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wallet, container, false);
        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.back)).setVisibility(View.VISIBLE);
        ((ImageView) getActivity().getWindow().getDecorView().findViewById(R.id.menu)).setVisibility(View.GONE);
        ButterKnife.inject(this, view);

        dueCalendar = new GregorianCalendar();
        currentMonth = dueCalendar.get(Calendar.MONTH);
        if (dueCalendar.get(Calendar.DAY_OF_MONTH) > 15) {
            dueCalendar.add(Calendar.MONTH, 1);
            dueCalendar.set(Calendar.DAY_OF_MONTH, 1);
        } else {
            dueCalendar.set(Calendar.DAY_OF_MONTH, 16);
        }

        String date = new SimpleDateFormat("yyyy-MM-dd").format(dueCalendar.getTime());
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
                walletJsonArray = jsonObject.getJSONArray("allPayment");
                transactionNumber.setText(getActivity().getString(R.string.transactionNumber) + walletJsonArray.length());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar cal = Calendar.getInstance();
                int pending = 0, balance = 0, cashReceived = 0, allInCome = 0, docMoneyMonthly = 0;
                for (int i = 0; i < walletJsonArray.length(); i++) {
                    int docMoney = Integer.parseInt(walletJsonArray.getJSONObject(i).getString(WebService.Payment.doctor_money));
                    if (docMoney < 0) docMoney = docMoney * -1;
                    allInCome += docMoney;
                    try {
                        cal.setTime(sdf.parse(walletJsonArray.getJSONObject(i).getString("created_at").substring(0, 10)));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    if (dueCalendar.get(Calendar.DAY_OF_MONTH) == 16) {
                        if (!(cal.get(Calendar.MONTH) < dueCalendar.get(Calendar.MONTH))) {
                            docMoneyMonthly += docMoney;
                            if (walletJsonArray.getJSONObject(i).getString(WebService.Payment.id_payment_way).equals(WebService.Booking.cash)) {
                                cashReceived += Integer.parseInt(walletJsonArray.getJSONObject(i).getString(WebService.Payment.total_money));
                                pending += Integer.parseInt(walletJsonArray.getJSONObject(i).getString(WebService.Payment.doctor_money));
                            }
                        }
                    } else {
                        if (!(cal.get(Calendar.MONTH) < currentMonth || (cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.DAY_OF_MONTH) < 16))) {
                            docMoneyMonthly += docMoney;
                            if (walletJsonArray.getJSONObject(i).getString(WebService.Payment.id_payment_way).equals(WebService.Booking.cash)) {
                                cashReceived += Integer.parseInt(walletJsonArray.getJSONObject(i).getString(WebService.Payment.total_money));
                                pending += Integer.parseInt(walletJsonArray.getJSONObject(i).getString(WebService.Payment.doctor_money));
                            }
                        }
                    }
                }
                balance = docMoneyMonthly - pending;
                Balance.setText(getActivity().getString(R.string.balance) + balance + " " + getActivity().getString(R.string.egp));
                Pending.setText(getActivity().getString(R.string.pending) + pending + " " + getActivity().getString(R.string.egp));
                CashReceived.setText(getActivity().getString(R.string.cashReceived) + cashReceived + " " + getActivity().getString(R.string.egp));
                allMoney.setText(getActivity().getString(R.string.Settled) + allInCome + " " + getActivity().getString(R.string.egp));

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
                if (payment.getString(WebService.Payment.id_payment_way).equals(WebService.Booking.cash)) {
                    holder.doctorMoney.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.money, 0, 0, 0);
                    holder.cashReceived.setText(
                            getString(R.string.cashReceived) + payment.getString(WebService.Payment.total_money) +
                                    getActivity().getString(R.string.egp));
                    holder.moneyOnYou.setText(
                            getString(R.string.moneyOnYou) + payment.getString(WebService.Payment.admin_money) +
                                    getActivity().getString(R.string.egp));

                } else {
                    holder.moneyOnYou.setText(
                            getString(R.string.moneyOnYou) + 0 +
                                    getActivity().getString(R.string.egp));

                    holder.doctorMoney.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.payment, 0, 0, 0);
                    holder.cashReceived.setText(getString(R.string.cashReceived) + 0 + getActivity().getString(R.string.egp));
                }
                holder.doctorMoney.setText(payment.getString(WebService.Payment.doctor_money).replace("-", "") + getString(R.string.egp));
                holder.date.setText(payment.getString("created_at"));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar cal = Calendar.getInstance();
                try {
                    cal.setTime(sdf.parse(payment.getString("created_at").substring(0, 10)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (dueCalendar.get(Calendar.DAY_OF_MONTH) == 16) {
                    if (cal.get(Calendar.MONTH) < dueCalendar.get(Calendar.MONTH)) {
                        holder.cashReceived.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.done, 0, 0, 0);
                    } else {
                        holder.cashReceived.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.not_paid, 0, 0, 0);
                    }
                } else {
                    if (cal.get(Calendar.MONTH) < currentMonth || (cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.DAY_OF_MONTH) < 16)) {
                        holder.cashReceived.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.done, 0, 0, 0);
                    } else {
                        holder.cashReceived.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.not_paid, 0, 0, 0);
                    }
                }


            } catch (
                    JSONException e)

            {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return (int) walletJsonArray.length();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView date, doctorMoney, cashReceived, moneyOnYou;

        public MyViewHolder(View v) {
            super(v);
            date = (TextView) v.findViewById(R.id.date);
            cashReceived = (TextView) v.findViewById(R.id.cashReceived);
            doctorMoney = (TextView) v.findViewById(R.id.doctorMoney);
            moneyOnYou = (TextView) v.findViewById(R.id.moneyOnYou);
        }
    }


}
