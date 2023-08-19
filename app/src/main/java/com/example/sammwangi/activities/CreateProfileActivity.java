package com.example.sammwangi.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sammwangi.DAOs.ProfileAccount;
import com.example.sammwangi.R;
import com.example.sammwangi.loaders.CreateProfileLoader;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateProfileActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ResponseBody> {
    private static final int CREATE_PROFILE_LOADER = 49;
    private static final int PROFILE_IMAGE_REQUEST = 47;
    private static final String ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private EditText fullName, email, passwordEditText, repeatPassword, referenceNumber,subCounty, profileName;
    private MaterialButton addDetailsButton,signInButton;
    private ImageView selectedImageView;
    private Retrofit retrofit;
    private MaterialCheckBox mAdminCheck,mSubCountyHeadCheck,mMemberCheck;
    private String accountType;
    private String baseUrl;
    private TextInputLayout referenceLt;
    private TextView accountExisting,repeatPasswordError,passwordErrorText;
    private Handler handler;
    private boolean isPrimaryCall = false;
    private String generatedReferenceNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        FirebaseApp.initializeApp(this);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create new profile");

        isPrimaryCall = getIntent().getBooleanExtra("primaryCall",false);

        signInButton = findViewById(R.id.go_to_log_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateProfileActivity.this, SignInActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in,R.anim.slide_out_left);
            }
        });


        repeatPasswordError = findViewById(R.id.repeat_password_error_text);
        passwordErrorText = findViewById(R.id.password_error_text);
        profileName = findViewById(R.id.profile_name_editext);
        fullName = findViewById(R.id.full_name_editext);
        email = findViewById(R.id.email_editext);
        referenceNumber = findViewById(R.id.reference_number_editext);
        subCounty = findViewById(R.id.sub_county_editext);
        passwordEditText = findViewById(R.id.password_editext);
        selectedImageView = findViewById(R.id.profile_image);
        referenceLt = findViewById(R.id.reference_number_layout);

        selectedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchMediaPickerActivity();
            }
        });

        String passwordRegex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#])[A-Za-z\\d@#]{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Matcher matcher = pattern.matcher(s.toString());
                if (!matcher.matches()){
                    passwordErrorText.setVisibility(View.VISIBLE);
                }else {
                    passwordErrorText.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        repeatPassword = findViewById(R.id.repeat_password_editext);
//        selectProfileImage = findViewById(R.id.create_image_profile);
//        profileImage = findViewById(R.id.new_profile_image);

        accountExisting = findViewById(R.id.account_created_already);
        accountExisting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateProfileActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        mAdminCheck = findViewById(R.id.check_admin);
        mSubCountyHeadCheck = findViewById(R.id.check_sub_county_head);
        mMemberCheck = findViewById(R.id.check_member);

        mAdminCheck.setOnCheckedChangeListener(checkboxChangeListener);
        mSubCountyHeadCheck.setOnCheckedChangeListener(checkboxChangeListener);
        mMemberCheck.setOnCheckedChangeListener(checkboxChangeListener);

        Gson gson = new GsonBuilder().setLenient().create();

        baseUrl = getString(R.string.base_url_title);

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl + "/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        addDetailsButton = findViewById(R.id.add_account_details_button);

        addDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account_type = accountType;
                String name = fullName.getText().toString();
                String password = passwordEditText.getText().toString();
                String emailAddress = email.getText().toString();
                String reference_number = referenceNumber.getText().toString();
                String sub_county = subCounty.getText().toString();
                String repeat_password = repeatPassword.getText().toString();
                int profileImage;
                if (repeat_password.equals(password)){
                    repeatPasswordError.setVisibility(View.GONE);
                }else {
                    repeatPasswordError.setVisibility(View.VISIBLE);
                }
                String user_name = profileName.getText().toString();

                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String tokenId = task.getResult();
                                Log.e("Tag", "Token Id: " + tokenId);

                                if (fullName.length()==0 || emailAddress.length()==0 ||
                                        password.length()==0 || repeat_password.length()==0 || user_name.length()==0 ||
                                        sub_county.length()==0) {
                                    Toast.makeText(CreateProfileActivity.this,"Please fill all the fields",Toast.LENGTH_SHORT).show();
                                }else {
                                    Drawable drawable = selectedImageView.getDrawable();

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
                                        Uri imageUri = Uri.fromFile(imageFile);
                                        ProfileAccount profileAccount = new ProfileAccount();
                                        profileAccount.setEmail(emailAddress);
                                        profileAccount.setAccountType(accountType);
                                        profileAccount.setFullName(name);
                                        profileAccount.setUserName(user_name);
                                        profileAccount.setPassword(password);
                                        profileAccount.setTokenId(tokenId);
                                        profileAccount.setSubCounty(sub_county);

                                        saveUserToServer(profileAccount,imageFile);
                                        if (accountType.equals("Admin") || accountType.equals("Sub-county Head")) {
                                            // The account type is admin or sub-county head, use the entered reference number
                                            referenceNumber.setVisibility(View.VISIBLE);
                                            profileAccount.setReferenceNumber(reference_number);
                                        saveUserToServer(profileAccount, imageFile);
                                        } else {
                                            // The account type is member, generate a unique reference number
                                            referenceNumber.setVisibility(View.GONE);
                                            generatedReferenceNumber = generateRandomString(12); // You can change the length as needed (e.g., 12-16 characters)
                                            profileAccount.setReferenceNumber(generatedReferenceNumber);
                                            saveUserToServer(profileAccount, imageFile);
                                        }
                                    }
                                }
                            }else {
                                Log.e("Tag", "Error getting token: " + task.getException());
                            }
                        });
            }
        });
    }

    private void launchMediaPickerActivity() {
        Intent intent = new Intent(CreateProfileActivity.this, MediaPickerActivity.class);
        intent.putExtra("editMode", false);
        startActivityForResult(intent, PROFILE_IMAGE_REQUEST);
    }
    private void saveUserToServer(ProfileAccount profile, File file) {
        if (file != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("profile",profile);
            bundle.putSerializable("image",file);
            LoaderManager.getInstance(CreateProfileActivity.this).initLoader(CREATE_PROFILE_LOADER,bundle,this);
        } else {
            Toast.makeText(CreateProfileActivity.this, "Oops!", Toast.LENGTH_SHORT).show();
        }
    }
    private String generateRandomString(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomCharIndex = random.nextInt(ALLOWED_CHARACTERS.length());
            sb.append(ALLOWED_CHARACTERS.charAt(randomCharIndex));
        }
        return sb.toString();
    }

    private final CompoundButton.OnCheckedChangeListener checkboxChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                // Uncheck other checkboxes
                mAdminCheck.setChecked(buttonView.getId() == R.id.check_admin);
                mSubCountyHeadCheck.setChecked(buttonView.getId() == R.id.check_sub_county_head);
                mMemberCheck.setChecked(buttonView.getId() == R.id.check_member);

                // Set the value of the selected checkbox to the string variable
                if (buttonView.getId() == R.id.check_admin) {
                    accountType = mAdminCheck.getText().toString();
                    referenceLt.setVisibility(View.VISIBLE); // Show the reference number input layout
                } else if (buttonView.getId() == R.id.check_sub_county_head) {
                    accountType = mSubCountyHeadCheck.getText().toString();
                    referenceLt.setVisibility(View.VISIBLE); // Show the reference number input layout
                } else if (buttonView.getId() == R.id.check_member) {
                    accountType = mMemberCheck.getText().toString();
                    referenceLt.setVisibility(View.GONE); // Hide the reference number input layout
                }
            }
        }
    };

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
                        .into(selectedImageView);

                // Set the selected image path as a tag on the ImageView
                selectedImageView.setTag(selectedImagePath);
            }
        }
    }
    @NonNull
    @Override
    public Loader<ResponseBody> onCreateLoader(int id, @Nullable Bundle args) {
        if (id == CREATE_PROFILE_LOADER) {
            assert args != null;
            ProfileAccount profile1 = args.getParcelable("profile");
            File file = (File) args.getSerializable("image");
            return new CreateProfileLoader(this,profile1,file);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ResponseBody> loader, ResponseBody data) {
        if (data != null) {
            String response = String.valueOf(data);
            Toast.makeText(CreateProfileActivity.this,response,Toast.LENGTH_SHORT).show();
            // User saved successfully, navigate to another activity
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isPrimaryCall) {
                        Intent intent = new Intent(CreateProfileActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }else {
                        boolean accountCreatedSuccessfully = true; // Set this based on the result of adding the account

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("account_created", accountCreatedSuccessfully);
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }
                }
            },1000);
        } else {
            // User saving failed, handle the error if needed
            Toast.makeText(CreateProfileActivity.this, "Oops!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ResponseBody> loader) {

    }
}

