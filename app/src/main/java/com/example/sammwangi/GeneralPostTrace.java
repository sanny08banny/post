package com.example.sammwangi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

public class GeneralPostTrace extends AppCompatActivity {

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private TextView totalBalanceTextView;
    private TextView contributionBalanceTextView;
    private TextView newSharesBalanceTextView,mpesaBalTextView,bankBalTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_post_trace);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Progress");

        totalBalanceTextView = findViewById(R.id.general_report_total_balanca);
        contributionBalanceTextView = findViewById(R.id.general_report_total_contribution_balanca);
        newSharesBalanceTextView = findViewById(R.id.general_report_total_shares_balanca);
        mpesaBalTextView = findViewById(R.id.general_report_total_mpesa_balanca);
        bankBalTextview = findViewById(R.id.general_report_total_bank_balanca);

        getTotalBalanceByEmail(getCurrentAccountEmail(), null);
        getTotalBalanceByEmail(getCurrentAccountEmail(), "Contribution");
        getTotalBalanceByEmail(getCurrentAccountEmail(), "New shares amount");
        getTotalBalanceByEmailForTransactionType(getCurrentAccountEmail(),"M-PESA");
        getTotalBalanceByEmailForTransactionType(getCurrentAccountEmail(),"Direct-Banking");
    }
    private void getTotalBalanceByEmail(String email, String postType) {
        String baseUrl = getString(R.string.base_url_title);
        String url = baseUrl + "/api/posts/balance/" + email;
        if (postType != null) {
            url += "?postType=" + postType;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        int balance = Integer.parseInt(response);
                        updateTextViews(balance, postType);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GeneralPostTrace.this, "Error retrieving balance", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(stringRequest);
    }
    private void getTotalBalanceByEmailForTransactionType(String email, String transactionType) {
        String baseUrl = getString(R.string.base_url_title);
        String url = baseUrl + "/api/posts/balance/" + email;
        if (transactionType != null) {
            url += "?transactionType=" + transactionType;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        int balance = Integer.parseInt(response);
                        updateTransactionTypeTextViews(balance, transactionType);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GeneralPostTrace.this, "Error retrieving balance", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(stringRequest);
    }

    private void updateTextViews(int balance, String postType) {
        if (postType == null) {
            Locale kenyanLocale = new Locale("sw", "KE");
            Currency kenyanShilling = Currency.getInstance("KES");
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance(kenyanLocale);
            numberFormat.setCurrency(kenyanShilling);
            String formattedAmount = numberFormat.format(balance);
            totalBalanceTextView.setText(String.valueOf(formattedAmount));
        } else if (postType.equals("Contribution")) {
            Locale kenyanLocale = new Locale("sw", "KE");
            Currency kenyanShilling = Currency.getInstance("KES");
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance(kenyanLocale);
            numberFormat.setCurrency(kenyanShilling);
            String formattedAmount = numberFormat.format(balance);
            contributionBalanceTextView.setText(String.valueOf(formattedAmount));
        } else if (postType.equals("New shares amount")) {
            Locale kenyanLocale = new Locale("sw", "KE");
            Currency kenyanShilling = Currency.getInstance("KES");
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance(kenyanLocale);
            numberFormat.setCurrency(kenyanShilling);
            String formattedAmount = numberFormat.format(balance);
            newSharesBalanceTextView.setText(String.valueOf(formattedAmount));
        }
    }
    private void updateTransactionTypeTextViews(int balance, String transactionType) {
        if (transactionType != null) {
            if (transactionType.equals("M-PESA")) {
                Locale kenyanLocale = new Locale("sw", "KE");
                Currency kenyanShilling = Currency.getInstance("KES");
                NumberFormat numberFormat = NumberFormat.getCurrencyInstance(kenyanLocale);
                numberFormat.setCurrency(kenyanShilling);
                String formattedAmount = numberFormat.format(balance);
                mpesaBalTextView.setText(String.valueOf(formattedAmount));
            } else if (transactionType.equals("Direct Banking")) {
                Locale kenyanLocale = new Locale("sw", "KE");
                Currency kenyanShilling = Currency.getInstance("KES");
                NumberFormat numberFormat = NumberFormat.getCurrencyInstance(kenyanLocale);
                numberFormat.setCurrency(kenyanShilling);
                String formattedAmount = numberFormat.format(balance);
                bankBalTextview.setText(String.valueOf(formattedAmount));
            }
        }
    }

    public String getCurrentAccountEmail(){
        SharedPreferences sharedPreferences = this.getSharedPreferences("MyPrefs",MODE_PRIVATE);
        return sharedPreferences.getString("currentProfileAccountEmail",null);
    }
}