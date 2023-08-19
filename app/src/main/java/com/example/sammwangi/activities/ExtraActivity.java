package com.example.sammwangi.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sammwangi.DAOs.ActivityItem;
import com.example.sammwangi.DAOs.ProfileDTO;
import com.example.sammwangi.R;
import com.example.sammwangi.adapters.ActivityListAdapter;
import com.example.sammwangi.adapters.UserAdapter;
import com.example.sammwangi.loaders.ActivityRetrieverLoader;
import com.example.sammwangi.loaders.AdminUserLoader;
import com.example.sammwangi.loaders.AdminsRetrieverLoader;
import com.example.sammwangi.loaders.CreateActivityLoader;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class ExtraActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Response<String>> {
    private static final int PROFILE_IMAGE_REQUEST = 78;
    private static final int GENERATE_ADMIN_USER = 8;
    private TextView selectedExpiryDate;
    private MaterialButton newActivityExpiryButton, addActivity,addAdminUser;
    private TextInputEditText subCountyEditText;
    private ImageButton showExistingButton,showExistingAdmins;
    private ImageView newActivityImage;
    private ArrayList<ActivityItem> activityItems;
    private ListView activitiesList;
    private RecyclerView existingAdmins;
    private UserAdapter userAdapter;
    private List<ProfileDTO> profileDTOS;
    private ActivityListAdapter activityListAdapter;
    private MaterialToolbar toolbar;
    private String selectedExpiry = "This activity will be visible to all users of the app (the whole community)";
    private String selectedDate = ""; // Initialize with an empty string
    private String enteredSubCounty = ""; // Initialize with an empty string
    private String referenceNumber;
    private ProgressBar progressBar;
    private View progressLt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra);
        activitiesList = findViewById(R.id.existing_delivery_options_recycler);
        showExistingButton = findViewById(R.id.show_existing_deliveries_button);
        showExistingAdmins = findViewById(R.id.show_existing_admins_button);
        addActivity = findViewById(R.id.add_delivery_option_button);
        addAdminUser = findViewById(R.id.add_new_admin);
        toolbar = findViewById(R.id.extras_toolbar);
        progressLt = findViewById(R.id.progress_layout);
        existingAdmins = findViewById(R.id.existing_admins_recycler);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle("Extras");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        activityItems = new ArrayList<>();
        activityListAdapter = new ActivityListAdapter(this,activityItems);
        activitiesList.setAdapter(activityListAdapter);

        loadActivityData();

        showExistingButton.setOnClickListener(view1 -> {
            boolean isExpanded = activitiesList.getVisibility() == View.VISIBLE;
            toggleExpansion(activitiesList, isExpanded,showExistingButton);
        });

        addActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayNewActivityDialog();
            }
        });

        profileDTOS = new ArrayList<>();
        userAdapter = new UserAdapter(this,profileDTOS);
        existingAdmins.setLayoutManager(new LinearLayoutManager(this));
        existingAdmins.setAdapter(userAdapter);
        
        loadAdmins();
        showExistingAdmins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isExpanded = existingAdmins.getVisibility() == View.VISIBLE;
                toggleExpansion(existingAdmins, isExpanded,showExistingAdmins);
            }
        });
        addAdminUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordInputDialog();
            }
        });
    }

    private void loadAdmins() {
        AdminsRetrieverLoader adminsRetrieverLoader = new AdminsRetrieverLoader(this);
        adminsRetrieverLoader.forceLoad();
        adminsRetrieverLoader.registerListener(7,new Loader.OnLoadCompleteListener<List<ProfileDTO>>(){
            @Override
            public void onLoadComplete(@NonNull Loader<List<ProfileDTO>> loader, @Nullable List<ProfileDTO> data) {
                if (data != null){
                    userAdapter.setItems(data);
                }
            }
        });
    }

    private void loadActivityData() {
        ActivityRetrieverLoader activityRetrieverLoader = new ActivityRetrieverLoader(this);
        activityRetrieverLoader.forceLoad();
        activityRetrieverLoader.registerListener(0, new Loader.OnLoadCompleteListener<List<ActivityItem>>() {
            @Override
            public void onLoadComplete(Loader<List<ActivityItem>> loader, List<ActivityItem> data) {
                if (data != null) {
                    activityListAdapter.setItems(data);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PROFILE_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            String selectedImagePath = data.getStringExtra("selectedImagePath");

            // Use the selected image path in your logic or update your UI
            if (selectedImagePath != null) {
                // For example, update an ImageView with the selected image using Glide
                Glide.with(this)
                        .load(selectedImagePath)
                        .centerCrop()
                        .override(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT) // Set the desired width and height for resizing
                        .into(newActivityImage);

                // Set the selected image path as a tag on the ImageView
                newActivityImage.setTag(selectedImagePath);
            }
        }
    }
    private void toggleExpansion(View expandLayout, boolean isExpanded,ImageButton imageButton) {
        if (isExpanded) {
            expandLayout.setVisibility(View.GONE);
            imageButton.setImageResource(R.drawable.baseline_keyboard_arrow_down_32);
            ObjectAnimator.ofFloat(expandLayout, "scaleY", 1f, 0f)
                    .setDuration(300)
                    .start();
        } else {
            expandLayout.setVisibility(View.VISIBLE);
            imageButton.setImageResource(R.drawable.baseline_keyboard_arrow_up_32);
            ObjectAnimator.ofFloat(expandLayout, "scaleY", 0f, 1f)
                    .setDuration(300)
                    .start();
        }
    }
    private void displayNewActivityDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.new_activity_dialog);

        newActivityImage = dialog.findViewById(R.id.new_activity_image);
        View newProductGradient = dialog.findViewById(R.id.new_product_gradient);
        TextInputEditText newActivityDescEditText = dialog.findViewById(R.id.new_activity_desc);
        newActivityExpiryButton = dialog.findViewById(R.id.new_activity_expiry_button);
        selectedExpiryDate = dialog.findViewById(R.id.selected_expiry_date);
        LinearLayout advancedSettingsLayout = dialog.findViewById(R.id.advanced_settings);
        subCountyEditText = dialog.findViewById(R.id.sub_county_edittext);
        MaterialButton newActivitySaveButton = dialog.findViewById(R.id.new_activity_save_button);
        ImageButton expandButton = dialog.findViewById(R.id.expand_button);

        // Set the default text for selectedExpiryDate
        selectedExpiryDate.setText(selectedExpiry);

        newActivityExpiryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show DatePicker when "Pick Expiry" button is clicked
                showDatePicker();
            }
        });

        subCountyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Update enteredSubCounty with the entered subcounty
                enteredSubCounty = s.toString();
                updateSelectedExpiryText();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });


        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isExpanded = advancedSettingsLayout.getVisibility() == View.VISIBLE;
                toggleExpansion(advancedSettingsLayout,isExpanded,expandButton);
            }
        });

        newActivityImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchMediaPickerActivity();
            }
        });
        newActivitySaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String desc = newActivityDescEditText.getText().toString();
                String subCounty = subCountyEditText.getText().toString();

                ActivityItem activityItem = new ActivityItem();
                activityItem.setDescription(desc);
                if (!subCounty.isEmpty()) {
                    activityItem.setTitle(subCounty);
                }
                Drawable drawable = newActivityImage.getDrawable();

                if (drawable instanceof BitmapDrawable) {
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                    File imageFile = new File(getCacheDir(), "image.jpg");
                    FileOutputStream outputStream;
                    try {
                        outputStream = new FileOutputStream(imageFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        outputStream.flush();
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    createActivity(activityItem,imageFile);
                }
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationCustom;
        dialog.getWindow().setGravity(Gravity.CENTER);

    }

    private void createActivity(ActivityItem activityItem, File imageFile) {
        CreateActivityLoader activityLoader = new CreateActivityLoader(this,activityItem,imageFile,getCurrentEmail());
        activityLoader.forceLoad();
        activityLoader.registerListener(8, new Loader.OnLoadCompleteListener<ResponseBody>() {
            @Override
            public void onLoadComplete(Loader<ResponseBody> loader, ResponseBody data) {
                if (data != null) {
                    Toast.makeText(ExtraActivity.this, "Activity created successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void launchMediaPickerActivity() {
        Intent intent = new Intent(ExtraActivity.this, MediaPickerActivity.class);
        intent.putExtra("editMode", false);
        startActivityForResult(intent, PROFILE_IMAGE_REQUEST);
    }
    private void showDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Update the selected date
                selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year);
                updateSelectedExpiryText();
            }
        };

        // Get the current date for DatePicker initial display
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Show the DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
        datePickerDialog.show();
    }

    private void updateSelectedExpiryText() {
        if (selectedDate.isEmpty()) {
            // Use the default text when the date is not selected
            selectedExpiry = "This activity will be visible to all users of the app (the whole community)";
        } else {
            // Use the selected date and entered subcounty in the text
            selectedExpiry = "This activity will be visible until " + selectedDate + " in " + enteredSubCounty;
        }
        selectedExpiryDate.setText(selectedExpiry);
    }
    private void showPasswordInputDialog() {
        final Dialog passwordDialog = new Dialog(this);
        passwordDialog.setContentView(R.layout.password_input_dialog);

        final EditText editTextPassword = passwordDialog.findViewById(R.id.editTextPassword);
        MaterialButton btnSubmitPassword = passwordDialog.findViewById(R.id.btnSubmitPassword);
        TextView errorText = passwordDialog.findViewById(R.id.error_text);

        btnSubmitPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = editTextPassword.getText().toString();
                // Validate the password here (replace with your actual validation logic)
                if (password.equals(getCurrentProfilePassword())) {
                    passwordDialog.dismiss();
                    // Start the AsyncTaskLoader
                    generateAdminAccess();
                } else {
                    // Show an error message or handle incorrect password
                    errorText.setVisibility(View.VISIBLE);
                }
            }
        });

        passwordDialog.show();
        passwordDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void generateAdminAccess() {
        showProgressBar();
        LoaderManager.getInstance(this).initLoader(GENERATE_ADMIN_USER,null,this);
    }
    private void showProgressBar() {
        progressLt.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressLt.setVisibility(View.GONE);
    }

    @NonNull
    @Override
    public Loader<Response<String>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AdminUserLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Response<String>> loader, Response<String> data) {
        hideProgressBar();
        referenceNumber = data.body();
        showReferenceNumberDialog();
    }


    @Override
    public void onLoaderReset(@NonNull Loader<Response<String>> loader) {
        // Do nothing
    }

    private void showReferenceNumberDialog() {
        final Dialog referenceNumberDialog = new Dialog(this);
        referenceNumberDialog.setContentView(R.layout.reference_number_dialog);
        referenceNumberDialog.setCancelable(false);

        TextView tvReferenceNumber = referenceNumberDialog.findViewById(R.id.tvReferenceNumber);
        tvReferenceNumber.setText("Generated reference Number: " + referenceNumber);

        MaterialButton btnShareReference = referenceNumberDialog.findViewById(R.id.btnShareReference);
        btnShareReference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareReferenceNumber(referenceNumber);
                referenceNumberDialog.dismiss();
                finish();
            }
        });

        referenceNumberDialog.show();
        referenceNumberDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void shareReferenceNumber(String referenceNumber) {
        // Create a share intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Admin Reference Number");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Your reference number is: " + referenceNumber);

        // Start the share activity
        startActivity(Intent.createChooser(shareIntent, "Share Reference Number"));
    }
    public String getCurrentProfilePassword() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("currentPassword", null);
    }
    public String getCurrentEmail(){
        SharedPreferences sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("currentEmail", null);
    }

}