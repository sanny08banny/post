package com.example.sammwangi;

import static java.text.DateFormat.getDateInstance;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private FloatingActionButton addPostButton, addSummary, sendNotification, addPostSecondary, cancelAdminButton;
    private final List<PostItem> postItemList = new ArrayList<>();
    private ViewPager viewPager;
    private MainFragment mainFragment;
    private TabLayout mainTabs;
    private NotificationService notificationService;
    private Toolbar toolbar;
    private PostAdapter postAdapter;
    private PagerAdapter pagerAdapter;
    private TextView currentDate, addSummaryText, addPostSecondaryText, sendNotificationText,currentTotalBalance;
    private static final String CHANNEL_ID = "sam_mwangi";
    private static final String CHANNEL_NAME = "SamMwangi";
    private static final String CHANNEL_DESC = "New Post Notification";
    private String baseUrl;


    //    private PostAdapter subsPostAdapter;
    private  Retrofit retrofit;
    private AppCompatSpinner spinner,transactionSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String tokenId = task.getResult();
                        Log.e("Tag", "Token Id: " + tokenId);

                        TokenUpdateAsyncTaskLoader loader = new TokenUpdateAsyncTaskLoader(this, getCurrentAccountEmail(),tokenId);
                        loader.forceLoad();

                        DataBaseHelper dbHelper = new DataBaseHelper(this);
                        List<ProfileAccount> accounts = dbHelper.getAllAccounts();

                        for (ProfileAccount account : accounts) {
                            String accountTokenId = account.getTokenId();
                            if (accountTokenId != null && !accountTokenId.equals(tokenId)) {
                                dbHelper.updateTokenIfNotMatch(account.getEmail(), tokenId);
                            } else if (accountTokenId == null) {
                                dbHelper.updateTokenIfNotMatch(account.getEmail(), tokenId);
                            }
                        }
                    } else {
                        Log.e("Tag", "Error getting token: " + task.getException());
                    }
                });

        toolbar = findViewById(R.id.main_toolbar);
        this.setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }

        ImageButton settingsButton = findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SettingsActivity.class);
                v.getContext().startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

        currentTotalBalance =findViewById(R.id.current_balance);
        fetchTotalBalance();


            currentDate = findViewById(R.id.current_date);
        Calendar calendar = Calendar.getInstance();
        Date current_date = calendar.getTime();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault(Locale.Category.FORMAT));
            String formattedDate = dateFormat.format(current_date);
            String current_date_before = getString(R.string.as_at_annotation, formattedDate);

            currentDate.setText(current_date_before);
        }

        viewPager = findViewById(R.id.viewPagerMain);
        mainTabs = findViewById(R.id.main_tabs);

        View customTabView = LayoutInflater.from(mainTabs.getContext()).inflate(R.layout.tab_title,null);
        TextView tabTitleTextView = customTabView.findViewById(R.id.textView1);

        View customTabView2 = LayoutInflater.from(mainTabs.getContext()).inflate(R.layout.tab_title2,null);
        TextView tabTitleTextView2 = customTabView2.findViewById(R.id.textView2);

        View customTabView3 = LayoutInflater.from(mainTabs.getContext()).inflate(R.layout.tab_title3,null);
        TextView tabTitleTextView3 = customTabView3.findViewById(R.id.textView3);

        tabTitleTextView.setText("All Posts");
        TabLayout.Tab tab1 = mainTabs.newTab().setCustomView(customTabView);

        tabTitleTextView2.setText("My Posts");
        TabLayout.Tab tab2 = mainTabs.newTab().setCustomView(customTabView2);

        tabTitleTextView3.setText("My Sub-county");
        TabLayout.Tab tab3 = mainTabs.newTab().setCustomView(customTabView3);

        mainTabs.addTab(tab1);
        mainTabs.addTab(tab2);
        mainTabs.addTab(tab3);

        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), mainTabs.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mainTabs));
        mainTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        baseUrl = getString(R.string.base_url_title);

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl + "/api/posts/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        addPostButton = findViewById(R.id.add_post_button);
        addSummary = findViewById(R.id.add_summaries);
        sendNotification = findViewById(R.id.send_notification);
        addPostSecondary = findViewById(R.id.create_post_button_secondary);
        cancelAdminButton = findViewById(R.id.cancel_admin_button);

        addSummaryText = findViewById(R.id.add_summaries_textView);
        sendNotificationText = findViewById(R.id.send_notification_textView);
        addPostSecondaryText = findViewById(R.id.create_post_button_secondary_textView);

        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCurrentProfileAccountType().matches("member")) {
                    showAddPostDialog();
                } else {
//                    Toast.makeText(MainActivity.this, "This is an admin", Toast.LENGTH_SHORT).show();
                    addPostButton.setVisibility(View.GONE);

                    addSummary.setVisibility(View.VISIBLE);
                    addSummaryText.setVisibility(View.VISIBLE);
                    addPostSecondary.setVisibility(View.VISIBLE);
                    addPostSecondaryText.setVisibility(View.VISIBLE);
                    sendNotification.setVisibility(View.VISIBLE);
                    sendNotificationText.setVisibility(View.VISIBLE);
                    cancelAdminButton.setVisibility(View.VISIBLE);
                }

            }
        });

        cancelAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPostButton.setVisibility(View.VISIBLE);

                addSummary.setVisibility(View.GONE);
                addSummaryText.setVisibility(View.GONE);
                addPostSecondary.setVisibility(View.GONE);
                addPostSecondaryText.setVisibility(View.GONE);
                sendNotification.setVisibility(View.GONE);
                sendNotificationText.setVisibility(View.GONE);
                cancelAdminButton.setVisibility(View.GONE);
            }
        });

        addPostSecondary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddPostDialog();
                addPostButton.setVisibility(View.VISIBLE);
                addSummary.setVisibility(View.GONE);
                addSummaryText.setVisibility(View.GONE);
                addPostSecondary.setVisibility(View.GONE);
                addPostSecondaryText.setVisibility(View.GONE);
                sendNotification.setVisibility(View.GONE);
                sendNotificationText.setVisibility(View.GONE);
                cancelAdminButton.setVisibility(View.GONE);

            }
        });

        sendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySendNotificationDialog();
                addPostButton.setVisibility(View.VISIBLE);
                addSummary.setVisibility(View.GONE);
                addSummaryText.setVisibility(View.GONE);
                addPostSecondary.setVisibility(View.GONE);
                addPostSecondaryText.setVisibility(View.GONE);
                sendNotification.setVisibility(View.GONE);
                sendNotificationText.setVisibility(View.GONE);
                cancelAdminButton.setVisibility(View.GONE);
            }
        });
    }

    private void displaySendNotificationDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);

        TextInputEditText notificationTitle = dialog.findViewById(R.id.notification_dialog_title_textField);
        TextInputEditText messageBody = dialog.findViewById(R.id.notification_dialog_body_textField);
        TextInputEditText customNotEditText = dialog.findViewById(R.id.custom_notification_ediText);
        MaterialButton sendToAllMembersButton = dialog.findViewById(R.id.send_notification_to_allMember);
        MaterialButton sendToSubCountyHeadsButton = dialog.findViewById(R.id.send_notification_to_subCountyHeads);
        MaterialButton sendCustomNotButton = dialog.findViewById(R.id.send_custom_notification_button);


//        String title = Objects.requireNonNull(notificationTitle.getText()).toString();
//        String body = Objects.requireNonNull(messageBody.getText()).toString();

        sendToAllMembersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = Objects.requireNonNull(notificationTitle.getText()).toString();
                String body = Objects.requireNonNull(messageBody.getText()).toString();
                NotificationRequest notificationMessage = new NotificationRequest();
                notificationMessage.setTitle(title);
                notificationMessage.setBody(body);
                notificationMessage.setNotificationType("Administrative");

                // Start the loader to send the notification
                SendNotificationLoader loader = new SendNotificationLoader(MainActivity.this, notificationMessage);
                loader.forceLoad();
                Toast.makeText(MainActivity.this, "Sent to all members", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });


        sendToSubCountyHeadsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = Objects.requireNonNull(notificationTitle.getText()).toString();
                String body = Objects.requireNonNull(messageBody.getText()).toString();
                String accountType = "Sub-County head";
                NotificationRequest notificationMessage = new NotificationRequest();
                notificationMessage.setTitle(title);
                notificationMessage.setBody(body);
                notificationMessage.setNotificationType("Administrative");

                NotificationHeaderTaskLoader loader = new NotificationHeaderTaskLoader(MainActivity.this, accountType, notificationMessage);
                loader.forceLoad();
                Toast.makeText(MainActivity.this, "Sent to sub-county heads", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
        sendCustomNotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = Objects.requireNonNull(notificationTitle.getText()).toString();
                String body = Objects.requireNonNull(messageBody.getText()).toString();
                String subCounty = Objects.requireNonNull(customNotEditText.getText().toString());
                NotificationRequest notificationMessage = new NotificationRequest();
                notificationMessage.setTitle(title);
                notificationMessage.setBody(body);
                notificationMessage.setNotificationType("Administrative");

                NotificationTaskLoader loader = new NotificationTaskLoader(MainActivity.this, subCounty, notificationMessage);
                loader.forceLoad();
//                Toast.makeText(MainActivity.this, "Sending notification...", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void showAddPostDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_post, null);

        EditText newPostAmountEdit = dialogView.findViewById(R.id.new_post_amount);
        EditText transaction_IdEditext = dialogView.findViewById(R.id.transaction_id);
        spinner = dialogView.findViewById(R.id.spinner_post_type);
        transactionSpinner = dialogView.findViewById(R.id.spinner_transaction_type);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.transaction_type_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        transactionSpinner.setAdapter(adapter1);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.post_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Post");
        builder.setView(dialogView);
        builder.setPositiveButton("Post", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String newPostAmount = newPostAmountEdit.getText().toString();
//                int postAmount = Integer.parseInt(newPostAmount);
                String transactionId = transaction_IdEditext.getText().toString();
                String transactionTypeString = (String) transactionSpinner.getSelectedItem();

                String postType = (String) spinner.getSelectedItem();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String formattedDate = simpleDateFormat.format(new Date());
                String dateTime = String.valueOf(new Date());

                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                String formattedTime = sdf.format(new Date());

                String tokenId = FCMTokenManager.getToken(getApplicationContext());


                PostItem newPost = new PostItem(newPostAmount, postType, formattedDate, getCurrentAccountFullName(), transactionId, transactionTypeString, formattedTime, tokenId, dateTime, getCurrentProfileReference());
                savePostToDatabase(newPost);
//                mainFragment.subsPostAdapter.addItem(newPost);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void displayNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_link_24)
                .setContentTitle("New Post from .....")
                .setContentText("100,000")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManagerCompat.notify(1, builder.build());
    }

    private List<PostItem> getAllPostsFromEndpoint(){
        List<PostItem> postItems = new ArrayList<>();

        baseUrl = getString(R.string.base_url_title);

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl + "/api/posts/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PostsService postsService = retrofit.create(PostsService.class);

        Call<List<PostItem>> call = postsService.getAllPosts();

        try {
            retrofit2.Response<List<PostItem>> response = call.execute();
            if (response.isSuccessful()){
                postItems = response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return postItems;
    }
    private Integer getAllPostsBalanceEndpoint(){

        Integer currentTotal = null;
        baseUrl = getString(R.string.base_url_title);

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl + "/api/posts/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PostsService postsService = retrofit.create(PostsService.class);

        Call<Integer> call = postsService.getTotalBalance();

        try {
            retrofit2.Response<Integer> response = call.execute();
            if (response.isSuccessful()){
                currentTotal = response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return currentTotal;
    }
    private void savePostToDatabase(PostItem postItem) {
        baseUrl = getString(R.string.base_url_title);

        String url = baseUrl +"/api/posts/" + getCurrentProfileReference();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, postItemToJson(postItem),
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(MainActivity.this, "Post Saved", Toast.LENGTH_SHORT).show();

                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
        Volley.newRequestQueue(this).add(request);

    }

    private JSONObject postItemToJson(PostItem postItem) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("referenceNumber", getCurrentProfileReference());
            jsonObject.put("transactionId",postItem.getTransactionId());
            jsonObject.put("transactionType",postItem.getTransactionType());
            jsonObject.put("postAmount", postItem.getPostAmount());
            jsonObject.put("postType", postItem.getPostType());
            jsonObject.put("datePosted", postItem.getDatePosted());
            jsonObject.put("timePosted",postItem.getTimePosted());
            jsonObject.put("dateTime",postItem.getDateTime());
            jsonObject.put("fullName",postItem.getFullName());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    private void fetchTotalBalance() {
        baseUrl = getString(R.string.base_url_title);

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl + "/api/posts/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PostsService postsService = retrofit.create(PostsService.class);
        Call<Integer> call = postsService.getTotalBalance();
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    Integer totalBalance = response.body();
                    double amount = totalBalance;
                    Locale kenyanLocale = new Locale("sw", "KE");
                    Currency kenyanShilling = Currency.getInstance("KES");
                    NumberFormat numberFormat = NumberFormat.getCurrencyInstance(kenyanLocale);
                    numberFormat.setCurrency(kenyanShilling);
                    String formattedAmount = numberFormat.format(amount);
                    // Update your TextView with the obtained value
                    currentTotalBalance.setText(String.format("Balance: %s", formattedAmount));
                } else {
                    // Handle error
                    // Display an error message or take appropriate action
                    currentTotalBalance.setText(R.string.balance);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                // Handle failure
                // Display an error message or take appropriate action
            }
        });
    }


    public String getCurrentProfileReference() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("currentProfileAccountRef", null);
    }

    public String getCurrentProfileAccountType(){
        SharedPreferences sharedPreferences = this.getSharedPreferences("MyPrefs",MODE_PRIVATE);
        return sharedPreferences.getString("currentProfileAccountType",null);
    }

    public String getCurrentAccountFullName(){
        SharedPreferences sharedPreferences = this.getSharedPreferences("MyPrefs",MODE_PRIVATE);
        return sharedPreferences.getString("currentAccountFullName",null);
    }
    public String getCurrentAccountEmail(){
        SharedPreferences sharedPreferences = this.getSharedPreferences("MyPrefs",MODE_PRIVATE);
        return sharedPreferences.getString("currentProfileAccountEmail",null);
    }

}


