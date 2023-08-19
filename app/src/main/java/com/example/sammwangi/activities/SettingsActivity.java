package com.example.sammwangi.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.sammwangi.DAOs.ProfileAccount;
import com.example.sammwangi.database_helpers.DataBaseHelper;
import com.example.sammwangi.R;
import com.example.sammwangi.adapters.SettingProfileAdapter;
import com.example.sammwangi.services.PostsService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private ImageButton closeWindow;
    private CardView accountSettings, getReports, notificationsCard, helpCard,reportsWindow;
    private MaterialButton fromDateButton,toDateButton,getReportButton;
    private TextView fromDate,toDate;
    private PostsService postsService;
    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager layoutManager;
    private SettingProfileAdapter settingProfileAdapter;
    private List<ProfileAccount> accountTypeArrayList;
    private EditText fullName,email,subCounty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Accounts & More");

        recyclerView = findViewById(R.id.setting_all_profiles);

        DataBaseHelper dataBaseHelper = new DataBaseHelper(SettingsActivity.this);
        accountTypeArrayList = dataBaseHelper.getAllProfileAccounts();

        settingProfileAdapter = new SettingProfileAdapter(accountTypeArrayList, this);
        layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);

        recyclerView.setAdapter(settingProfileAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        settingProfileAdapter.notifyDataSetChanged();

        accountSettings = findViewById(R.id.account_setting_card);
        accountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AppSettings.class);
                v.getContext().startActivity(intent);
            }
        });


        getReports = findViewById(R.id.reports_card);
        getReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCurrentProfileAccountType().matches( "Admin")) {
                    displayGetReportDialog();
                } else {
                    //open activity
                    Intent intent = new Intent(v.getContext(), GeneralPostTrace.class);
                    v.getContext().startActivity(intent);
                }
            }
        });


        notificationsCard = findViewById(R.id.notifications_card);
        notificationsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), NotificationsActivity.class);
                v.getContext().startActivity(intent);
            }
        });

    }

    private void displayGetReportDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.admin_get_report_layout);

        closeWindow = dialog.findViewById(R.id.close_get_reports_window);

        fromDateButton = dialog.findViewById(R.id.get_reports_datePicker_from);
        toDateButton = dialog.findViewById(R.id.get_reports_datePicker_to);
        fromDate = dialog.findViewById(R.id.date_from);
        toDate = dialog.findViewById(R.id.date_to);
        fullName = dialog.findViewById(R.id.full_name_editext_getReports);
        email = dialog.findViewById(R.id.email_editext_getReports);
        getReportButton = dialog.findViewById(R.id.get_specified_reports_button);
        subCounty = dialog.findViewById(R.id.sub_county_editext);
        fromDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder
                        .datePicker()
                        .setTitleText("Select from date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        String fromDateString = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(selection));
                        fromDate.setText(fromDateString);
                    }
                });
                materialDatePicker.show(getSupportFragmentManager(),"tag");
            }
        });

        toDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder
                        .datePicker()
                        .setTitleText("Select to date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        String fromDateString = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(selection));
                        toDate.setText( fromDateString);
                    }
                });
                materialDatePicker.show(getSupportFragmentManager(),"tag");
            }
        });
        getReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String fullNameString = fullName.getText().toString();
//                String emailString = email.getText().toString();
//                String toDateString = toDate.getText().toString();
//                Log.d("Selected Date","Selected to date: " + toDateString);
//                String fromDateString = fromDate.getText().toString();


                String fromDateString = fromDate.getText().toString();
                String toDateString = toDate.getText().toString();
                String emailString = email.getText().toString();
                String fullNameString = fullName.getText().toString();
                String subCountyString = subCounty.getText().toString();

                // Create an Intent to launch the activity to display the reports
                Intent intent = new Intent(SettingsActivity.this, AdminGeneralReport.class);

                // Check which fields are not null and add them as extras to the intent
                if (!fromDateString.isEmpty()) {
                    intent.putExtra("FROM_DATE", fromDateString);
                }
                if (!toDateString.isEmpty()) {
                    intent.putExtra("TO_DATE", toDateString);
                }
                if (!emailString.isEmpty()) {
                    intent.putExtra("EMAIL", emailString);
                    intent.putExtra("isSpecialSearch",true);
                }
                if (!fullNameString.isEmpty()) {
                    intent.putExtra("FULL_NAME", fullNameString);
                    intent.putExtra("isSpecialSearch",true);
                }
                if (!subCountyString.isEmpty()) {
                    intent.putExtra("SUB_COUNTY", subCountyString);
                    intent.putExtra("isSpecialSearch",true);
                }

                // Start the activity with the intent
                startActivity(intent);

            }
        });

        closeWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             dialog.cancel();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.AdminGetReportAnimation;
        dialog.getWindow().setGravity(Gravity.CENTER);

    }

    public String getCurrentProfileAccountType(){
        SharedPreferences sharedPreferences = this.getSharedPreferences("MyPrefs",MODE_PRIVATE);
        return sharedPreferences.getString("currentProfileAccountType",null);
    }
}