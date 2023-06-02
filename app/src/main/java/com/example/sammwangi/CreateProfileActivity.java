package com.example.sammwangi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateProfileActivity extends AppCompatActivity {
    private EditText fullName, email, passwordEditText, repeatPassword, referenceNumber,subCounty, profileName;
    private ViewPager2 viewPager2;
    private MaterialButton addDetailsButton,signInButton;
    private ShapeableImageView selectedImageView;
    private Retrofit retrofit;
    private MaterialCheckBox mAdminCheck,mSubCountyHeadCheck,mMemberCheck;
    private String accountType;
    private String baseUrl;

    private TextView accountExisting,repeatPasswordError,passwordErrorText;
    private final Handler sliderHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        FirebaseApp.initializeApp(this);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create new profile");

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

        String passwordRegex = "^(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$";
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
                Intent intent = new Intent(CreateProfileActivity.this,LoginActivity.class);
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
                int id = 0;

                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String tokenId = task.getResult();
                                Log.e("Tag", "Token Id: " + tokenId);

                                if (fullName.length()==0 || emailAddress.length()==0 ||reference_number.length()==0 || password.length()==0
                                        || repeat_password.length()==0 || user_name.length()==0 || sub_county.length()==0) {
                                    Toast.makeText(CreateProfileActivity.this,"Please fill all the fields",Toast.LENGTH_SHORT).show();
                                }else {
                                    ProfileAccount profileAccount = new ProfileAccount(id, account_type, user_name, name, emailAddress, reference_number, sub_county, password, repeat_password, tokenId);
                                    saveProfileToDatabase(profileAccount);
                                }

                            }else {
                                Log.e("Tag", "Error getting token: " + task.getException());
                            }
                        });

//                Log.d("CreateProfile TokeniId","Token Id: " + tokenId);

            }
        });
    }

    private void saveProfileToDatabase(ProfileAccount profileAccount) {
        String url = baseUrl + "/api/profiles/new";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, profileAccountToJson(profileAccount),
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(CreateProfileActivity.this, "Profile Saved", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CreateProfileActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CreateProfileActivity.this, "This email has been taken", Toast.LENGTH_SHORT).show();
                    }
                });
        Volley.newRequestQueue(this).add(request);

       DataBaseHelper dataBaseHelper = new DataBaseHelper(CreateProfileActivity.this);
       boolean accountSavedLocally = dataBaseHelper.addProfile(profileAccount);

        Toast.makeText(CreateProfileActivity.this, "Profile saved here " + accountSavedLocally, Toast.LENGTH_SHORT).show();

    }

    private JSONObject profileAccountToJson(ProfileAccount profileAccount) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fullName", profileAccount.getFullName());
            jsonObject.put("accountType", profileAccount.getAccountType());
            jsonObject.put("userName",profileAccount.getUserName());
            jsonObject.put("email", profileAccount.getEmail());
            jsonObject.put("password", profileAccount.getPassword());
            jsonObject.put("repeatPassword", profileAccount.getRepeatPassword());
            jsonObject.put("referenceNumber", profileAccount.getReferenceNumber());
            jsonObject.put("subCounty",profileAccount.getSubCounty());
            jsonObject.put("token_id",profileAccount.getTokenId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };

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
                } else if (buttonView.getId() == R.id.check_sub_county_head) {
                    accountType = mSubCountyHeadCheck.getText().toString();
                } else if (buttonView.getId() == R.id.check_member) {
                    accountType = mMemberCheck.getText().toString();
                }
            }
        }
    };


    }

