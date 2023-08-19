package com.example.sammwangi.activities;

import static java.text.DateFormat.getDateInstance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sammwangi.DAOs.ProfileAccount;
import com.example.sammwangi.SendNotificationLoader;
import com.example.sammwangi.database_helpers.DataBaseHelper;
import com.example.sammwangi.FCMTokenManager;
import com.example.sammwangi.NotificationHeaderTaskLoader;
import com.example.sammwangi.NotificationRequest;
import com.example.sammwangi.NotificationService;
import com.example.sammwangi.NotificationTaskLoader;
import com.example.sammwangi.pagers.PagerAdapter;
import com.example.sammwangi.adapters.PostAdapter;
import com.example.sammwangi.DAOs.PostItem;
import com.example.sammwangi.loaders.SavePostWithSettingsLoader;
import com.example.sammwangi.services.PostsService;
import com.example.sammwangi.R;
import com.example.sammwangi.TokenUpdateAsyncTaskLoader;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

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

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<PostItem> {
    private static final int POST_UPLOAD_LOADER = 48;
    private FloatingActionButton addPostButton, addSummary, sendNotification, addPostSecondary, cancelAdminButton;
    private ViewPager viewPager;
    private MaterialButton proceedButton;
    private TabLayout mainTabs;
    private PagerAdapter pagerAdapter;
    private TextView currentDate, currentTotalBalance;
    private static final String CHANNEL_ID = "sam_mwangi";
    private static final String CHANNEL_NAME = "SamMwangi";
    private static final String CHANNEL_DESC = "New Post Notification";
    private String baseUrl;


    //    private PostAdapter subsPostAdapter;
    private Retrofit retrofit;
    private AppCompatSpinner spinner, transactionSpinner, spinnerVisibility;
    private PostItem postItem;
    private Dialog dialogView;
    private final int notificationId = 93;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        postItem = new PostItem();
        FirebaseApp.initializeApp(this);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String tokenId = task.getResult();
                        Log.e("Tag", "Token Id: " + tokenId);

                        TokenUpdateAsyncTaskLoader loader = new TokenUpdateAsyncTaskLoader(this, getCurrentAccountEmail(), tokenId);
                        loader.forceLoad();

                        DataBaseHelper dbHelper = new DataBaseHelper(this);
                        List<ProfileAccount> accounts = dbHelper.getAllProfileAccounts();

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
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
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        currentTotalBalance = findViewById(R.id.current_balance);
        fetchTotalBalance();


        currentDate = findViewById(R.id.current_date);
        Calendar calendar = Calendar.getInstance();
        Date current_date = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(current_date);
        String current_date_before = getString(R.string.as_at_annotation, formattedDate);

        currentDate.setText(current_date_before);

        viewPager = findViewById(R.id.viewPagerMain);
        mainTabs = findViewById(R.id.main_tabs);

        View customTabView = LayoutInflater.from(mainTabs.getContext()).inflate(R.layout.tab_title, null);
        TextView tabTitleTextView = customTabView.findViewById(R.id.textView1);

        View customTabView2 = LayoutInflater.from(mainTabs.getContext()).inflate(R.layout.tab_title2, null);
        TextView tabTitleTextView2 = customTabView2.findViewById(R.id.textView2);

        View customTabView3 = LayoutInflater.from(mainTabs.getContext()).inflate(R.layout.tab_title3, null);
        TextView tabTitleTextView3 = customTabView3.findViewById(R.id.textView3);

        tabTitleTextView.setText("All Posts");
        TabLayout.Tab tab1 = mainTabs.newTab().setCustomView(customTabView);

        tabTitleTextView2.setText("Activities");
        TabLayout.Tab tab2 = mainTabs.newTab().setCustomView(customTabView2);

        tabTitleTextView3.setText("My sub-county");
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

        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCurrentProfileAccountType().matches("member")) {
                    showAddPostDialog();
                } else {
//                    Toast.makeText(MainActivity.this, "This is an admin", Toast.LENGTH_SHORT).show();
                    addPostButton.setVisibility(View.GONE);

                    addSummary.setVisibility(View.VISIBLE);
                    addPostSecondary.setVisibility(View.VISIBLE);
                    sendNotification.setVisibility(View.VISIBLE);
                    cancelAdminButton.setVisibility(View.VISIBLE);
                }

            }
        });

        cancelAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideExtras();
            }
        });

        addPostSecondary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddPostDialog();
                hideExtras();

            }
        });

        sendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySendNotificationDialog();
                hideExtras();
            }
        });
        addSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openExtrasActivity();
                hideExtras();
            }
        });
    }

    private void openExtrasActivity() {
        Intent intent = new Intent(MainActivity.this, ExtraActivity.class);
        startActivity(intent);
    }

    private void hideExtras() {
        addPostButton.setVisibility(View.VISIBLE);
        addSummary.setVisibility(View.GONE);
        addPostSecondary.setVisibility(View.GONE);
        sendNotification.setVisibility(View.GONE);
        cancelAdminButton.setVisibility(View.GONE);
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
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void showNotification(String title, String message, String notificationType) {
        String channelId = "sam_mwangi";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.baseline_link_24)
                .setContentTitle(title)
                .setContentText(message);

        // Set large icon (optional)
        Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.untitled_design_5);
        builder.setLargeIcon(largeIconBitmap);

        // Set image (optional)
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.untitle_design_4);
        builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(imageBitmap));

        // Set trailing icon (optional)
        int trailingIconResId = R.drawable.ic_launcher_post_foreground;
        NotificationCompat.Action trailingAction = new NotificationCompat.Action.Builder(
                trailingIconResId, "Trailing Icon Action", null).build();
        builder.addAction(trailingAction);

        // Set collapse behavior (optional)
        builder.setGroup("my_notification_group");
        builder.setGroupSummary(true);

//        // Set dynamic buttons based on notification type (optional)
//        if (notificationType == 1) {
//            // Type 1-specific actions
//            NotificationCompat.Action action1 = new NotificationCompat.Action.Builder(
//                    R.drawable.button_icon1, "Action 1", null).build();
//            builder.addAction(action1);
//        } else if (notificationType == 2) {
//            // Type 2-specific actions
//            NotificationCompat.Action action2 = new NotificationCompat.Action.Builder(
//                    R.drawable.button_icon2, "Action 2", null).build();
//            builder.addAction(action2);
//        }

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(notificationId, builder.build());
    }

    private void showAddPostDialog() {
        dialogView = new Dialog(this);
        dialogView.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogView.setContentView(R.layout.dialog_add_post);

        TextInputEditText newPostAmountEdit = dialogView.findViewById(R.id.new_post_amount);
        TextInputEditText transaction_IdEditext = dialogView.findViewById(R.id.transaction_id);
        spinner = dialogView.findViewById(R.id.spinner_post_type);
        ImageButton expandButton = dialogView.findViewById(R.id.expand_button);
        View advancedSettings = dialogView.findViewById(R.id.advanced_settings);
        transactionSpinner = dialogView.findViewById(R.id.spinner_transaction_type);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.transaction_type_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        transactionSpinner.setAdapter(adapter1);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.post_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinnerVisibility = dialogView.findViewById(R.id.spinnerVisibility);

        // Set up the ArrayAdapter for the Spinner with the visibility options
        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(
                this,
                R.array.visibility_options,
                R.layout.spinner_item_layout // Use the custom layout for Spinner items
        );
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVisibility.setAdapter(adapter4);

        // Set a listener to handle item selection in the Spinner
        spinnerVisibility.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected visibility from the Spinner
                String selectedVisibility = parent.getItemAtPosition(position).toString();
                // Set the visibility in the PostItem
                postItem.setVisibility(selectedVisibility);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // If nothing is selected, set the default visibility to PUBLIC
                postItem.setVisibility("PUBLIC");
            }
        });
        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isExpanded = advancedSettings.getVisibility() == View.VISIBLE;
                toggleExpansion(advancedSettings,isExpanded);
            }
        });


        proceedButton = dialogView.findViewById(R.id.proceed_button);

        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                postItem.setPostAmount(Integer.valueOf(newPostAmount));
                postItem.setPostType(postType);
                postItem.setDatePosted(formattedDate);
                postItem.setTimePosted(formattedTime);
                postItem.setTransactionId(transactionId);
                postItem.setTransactionType(transactionTypeString);
                postItem.setDateTime(dateTime);
                postItem.setReferenceNumber(getCurrentProfileReference());

                if (newPostAmount.length()==0 || transactionTypeString.length()==0 ||transactionId.length()==0 ||
                        postType.length()==0 || tokenId.length()==0) {
                    Toast.makeText(MainActivity.this,"Please fill all the fields",Toast.LENGTH_SHORT).show();
                }else {
                    savePostToDatabase(postItem, dialogView);
                }
            }
        });
//                mainFragment.subsPostAdapter.addItem(newPost);

        dialogView.show();
        dialogView.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialogView.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogView.getWindow().getAttributes().windowAnimations = R.style.NewPostAnimation;
        dialogView.getWindow().setGravity(Gravity.CENTER);
    }
    private void toggleExpansion(View expandLayout, boolean isExpanded) {
        if (isExpanded) {
            expandLayout.setVisibility(View.GONE);
            ObjectAnimator.ofFloat(expandLayout, "scaleY", 1f, 0f)
                    .setDuration(300)
                    .start();
        } else {
            expandLayout.setVisibility(View.VISIBLE);
            ObjectAnimator.ofFloat(expandLayout, "scaleY", 0f, 1f)
                    .setDuration(300)
                    .start();
        }
    }

    private void savePostToDatabase(PostItem postItem, Dialog dialogView) {
        proceedButton.setText("Please wait...");
        LoaderManager.getInstance(MainActivity.this).initLoader(POST_UPLOAD_LOADER,null,this);
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

    public String getCurrentProfileAccountType() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("currentProfileAccountType", null);
    }

    public String getCurrentAccountFullName() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("currentAccountFullName", null);
    }

    public String getCurrentAccountEmail() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("currentProfileAccountEmail", null);
    }

    @NonNull
    @Override
    public Loader<PostItem> onCreateLoader(int id, @Nullable Bundle args) {
        return new SavePostWithSettingsLoader(MainActivity.this,getCurrentProfileReference(),postItem);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<PostItem> loader, PostItem data) {
        if (data != null){
            Toast.makeText(MainActivity.this,"Upload successful",Toast.LENGTH_SHORT).show();
            dialogView.dismiss();
        }else {
            Toast.makeText(MainActivity.this,"Upload unsuccessful",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<PostItem> loader) {

    }
}


