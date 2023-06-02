package com.example.sammwangi;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminGeneralReport extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminPostsAdapterInner adminPostsAdapterInner;
    private LinearLayoutManager layoutManager;
    private AdminPostAdapter adminPostsAdapter;
    private PostsService postsService;
    private TextView numTransactions,parameters;
    private String baseUrl;
    private ExtendedFloatingActionButton floatingActionButton;
    private static final int REQUEST_CODE_SAVE_PDF = 1;
    private ActivityResultLauncher<String> savePdfLauncher;

    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_general_report);

        Intent intent = getIntent();
        String receivedFromDate = intent.getStringExtra("fromDate");
        String receivedToDate = intent.getStringExtra("toDate");
        Log.d("Selected Date", "Selected received to date: " + receivedToDate);
        String receivedFullName = intent.getStringExtra("fullName");
        Log.d("Selected fullName", "Selected full name: " + receivedFullName);
        String receivedEmail = intent.getStringExtra("email");

        numTransactions = findViewById(R.id.number_of_transactions);
        parameters = findViewById(R.id.report_name);
        if (receivedFullName != null) {
            Log.d("Selected fullName2", "Selected full name: " + receivedFullName);
            retrievePostsByFullName(receivedFullName);
            parameters.setText(MessageFormat.format("Tis is report for :  {0}", receivedFullName));

        }
        if (receivedEmail != null) {
            retrievePostsByEmail(receivedEmail);
            parameters.setText(MessageFormat.format("Tis is report for :  {0}", receivedEmail));

        }
        if (receivedFromDate != null && receivedToDate != null) {
            baseUrl = getString(R.string.base_url_title);

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl + "/api/posts/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            postsService = retrofit.create(PostsService.class);
            retrievePostsByDateRange(receivedFromDate, receivedToDate);
            parameters.setText(MessageFormat.format("Tis is report for :  {0}", receivedFromDate + " - " + receivedToDate));

        }
        if (receivedEmail != null && receivedFromDate != null && receivedToDate != null) {
            baseUrl = getString(R.string.base_url_title);

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl + "/api/posts/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            postsService = retrofit.create(PostsService.class);
            retrievePostsByEmailAndDateRange(receivedEmail, receivedFromDate, receivedToDate);
        }
        if (receivedFullName != null && receivedFromDate != null && receivedToDate != null) {
            baseUrl = getString(R.string.base_url_title);

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl + "/api/posts/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            postsService = retrofit.create(PostsService.class);
            retrievePostsByFulNameAndDateRange(receivedFullName, receivedFromDate, receivedToDate);
        }

        savePdfLauncher = registerForActivityResult(new ActivityResultContracts.CreateDocument(), uri -> {
            if (uri != null) {
                try {
                    Document document = new Document();
                    OutputStream outputStream = getContentResolver().openOutputStream(uri);
                    if (outputStream != null) {
                        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
                        document.open();

                        // Add a title to the document
                        Paragraph title = new Paragraph("Post Items Report", new Font(Font.FontFamily.HELVETICA, 28, Font.BOLD));
                        title.setAlignment(Element.ALIGN_CENTER);
                        title.setSpacingAfter(10);
                        document.add(title);

                        // Add a subtitle with report parameters
                        Paragraph parametersTitle = new Paragraph("Reports Parameters", new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD));
                        parametersTitle.setAlignment(Element.ALIGN_LEFT);
                        parametersTitle.setSpacingAfter(5);
                        document.add(parametersTitle);

                        String fromDate = getIntent().getStringExtra("fromDate");
                        String toDate = getIntent().getStringExtra("toDate");
                        String fullName = getIntent().getStringExtra("fullName");
                        String email = getIntent().getStringExtra("email");
                        Paragraph parameters = new Paragraph("From: " + fromDate + "     To: " + toDate + "\nFull Name: " + fullName + "\nEmail: " + email);
                        parameters.setSpacingAfter(20);
                        document.add(parameters);
//
//                        PdfPTable summaryTable = new PdfPTable(2);
//                        summaryTable.setWidthPercentage(100);
//                        summaryTable.setWidths(new float[]{3, 1});
//                        summaryTable.addCell(("Total Posts", Font.BOLD));
//                        summaryTable.addCell((String.valueOf(postItems.size()), Font.NORMAL));
//                        document.add(summaryTable);

                        // Retrieve the post items from the adapter
                        Map<String, List<PostItem>> groupedItems = adminPostsAdapter.getGroupedItems();
                        List<PostItem> postItems = new ArrayList<>();

                        // Iterate over the grouped items and add them to a single list
                        for (List<PostItem> itemList : groupedItems.values()) {
                            postItems.addAll(itemList);
                        }
                        // Create a table with the desired number of columns
                        PdfPTable table = new PdfPTable(6);

                        // Set table properties
                        table.setWidthPercentage(100);
                        table.setSpacingBefore(10f);
                        table.setSpacingAfter(10f);

                        // Add table headers
                        addTableHeader(table);

                        // Add table data rows
                        for (PostItem postItem : postItems) {
                            addTableRow(table, postItem);
                        }

                        // Add the table to the document
                        document.add(table);


                        document.close();

                        Toast.makeText(this, "PDF file saved", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException | DocumentException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to save PDF file", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void addTableRow(PdfPTable table, PostItem postItem) {
        double amount = Double.parseDouble(postItem.getPostAmount());
        Locale kenyanLocale = new Locale("sw", "KE");
        Currency kenyanShilling = Currency.getInstance("KES");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(kenyanLocale);
        NumberFormat numberFormat1 = NumberFormat.getNumberInstance();

        numberFormat.setCurrency(kenyanShilling);
        String formattedAmount = numberFormat.format(amount);

        String formattedAmount1 = numberFormat1.format(amount);

        table.addCell(createTableRowCell(formattedAmount1));
        table.addCell(createTableRowCell(postItem.getPostType()));
        table.addCell(createTableRowCell(postItem.getDatePosted()));
        table.addCell(createTableRowCell(postItem.getTransactionId()));
        table.addCell(createTableRowCell(postItem.getTransactionType()));
        table.addCell(createTableRowCell(postItem.getTimePosted()));
    }

    private PdfPCell createTableCell(String text, Font font, BaseColor backgroundColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        cell.setBorderColor(BaseColor.LIGHT_GRAY);
        cell.setBorderWidth(1f);
        return cell;
    }

    private PdfPCell createTableRowCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPaddingRight(5);
//        cell.setBorderColor(BaseColor.LIGHT_GRAY);
        cell.setBorderWidth(1f);
        return cell;
    }

    private void addTableHeader(PdfPTable table) {
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12);
        BaseColor headerBackgroundColor = new BaseColor(79, 129, 189); // Use your desired color

        // Add table headers with different background color and bold text
        PdfPCell headerCell = createTableCell("Post Amount", headerFont, headerBackgroundColor);
        table.addCell(headerCell);
        table.addCell(createTableCell("Post Type", headerFont, headerBackgroundColor));
        table.addCell(createTableCell("Date Posted", headerFont, headerBackgroundColor));
        table.addCell(createTableCell("Transaction ID", headerFont, headerBackgroundColor));
        table.addCell(createTableCell("Transaction Type", headerFont, headerBackgroundColor));
        table.addCell(createTableCell("Time Posted", headerFont, headerBackgroundColor));

    }

    private void retrievePostsByFullName(String receivedFullName) {
        RequestQueue queue = Volley.newRequestQueue(this);

// Define the URL for the request, replacing {referenceNumber} with the actual reference number
        baseUrl = getString(R.string.base_url_title);

        String url = baseUrl + "/api/posts/ByFullName/" + receivedFullName;

// Define a StringRequest to make a GET request to the URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Parse the response JSON string into a list of PostItems
                        List<PostItem> postItems = new Gson().fromJson(response, new TypeToken<List<PostItem>>() {
                        }.getType());

                        // Do something with the list of PostItems
                        // For example, display them in a ListView or RecyclerView
                        Log.d("AdminReportList", "Number of items in postItems: " + postItems.size());

                        Map<String, List<PostItem>> groupedItems = new TreeMap<>(Collections.reverseOrder());

                        for (PostItem postItem : postItems) {
                            if (postItem.getDateTime() != null) {
                                String date = postItem.getDateTime();
                                Date date2 = null;
                                try {
                                    date2 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault()).parse(date);
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }

                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
                                assert date2 != null;
                                String formattedDate = simpleDateFormat.format(date2);

                                // Check if the date is already present in the map
                                if (groupedItems.containsKey(formattedDate)) {
                                    // Date exists, retrieve the existing list of items and add the current item to it
                                    List<PostItem> itemsForDate = groupedItems.get(formattedDate);
                                    itemsForDate.add(postItem);
                                } else {
                                    // Date does not exist, create a new list and add the current item to it
                                    List<PostItem> itemsForDate = new ArrayList<>();
                                    itemsForDate.add(postItem);
                                    groupedItems.put(formattedDate, itemsForDate);
                                }
                            }
                        }
                        numTransactions.setText(MessageFormat.format("Number of transactions received :  {0}", postItems.size()));


                        recyclerView = findViewById(R.id.admin_report_recycler);
                        adminPostsAdapter = new AdminPostAdapter(groupedItems, AdminGeneralReport.this);
                        recyclerView.setAdapter(adminPostsAdapter);
                        layoutManager = new LinearLayoutManager(AdminGeneralReport.this, LinearLayoutManager.VERTICAL, false);
                        recyclerView.setLayoutManager(layoutManager);

                        floatingActionButton = findViewById(R.id.get_statement_fab);
                        floatingActionButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                generatePdfDocument();
                            }
                        });

                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle the error
            }
        });

// Add the request to the RequestQueue
        queue.add(stringRequest);
    }

    private void retrievePostsByEmail(String receivedEmail) {
        RequestQueue queue = Volley.newRequestQueue(this);

// Define the URL for the request, replacing {referenceNumber} with the actual reference number
        baseUrl = getString(R.string.base_url_title);

        String url = baseUrl + "/api/posts/ByEmail/" + receivedEmail;

// Define a StringRequest to make a GET request to the URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Parse the response JSON string into a list of PostItems
                        List<PostItem> postItems = new Gson().fromJson(response, new TypeToken<List<PostItem>>() {
                        }.getType());

                        // Do something with the list of PostItems
                        // For example, display them in a ListView or RecyclerView
                        Log.d("AdminReportList", "Number of items in postItems: " + postItems.size());
                        Map<String, List<PostItem>> groupedItems = new TreeMap<>(Collections.reverseOrder());

                        for (PostItem postItem : postItems) {
                            if (postItem.getDateTime() != null) {
                                String date = postItem.getDateTime();
                                Date date2 = null;
                                try {
                                    date2 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault()).parse(date);
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }

                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
                                assert date2 != null;
                                String formattedDate = simpleDateFormat.format(date2);

                                // Check if the date is already present in the map
                                if (groupedItems.containsKey(formattedDate)) {
                                    // Date exists, retrieve the existing list of items and add the current item to it
                                    List<PostItem> itemsForDate = groupedItems.get(formattedDate);
                                    itemsForDate.add(postItem);
                                } else {
                                    // Date does not exist, create a new list and add the current item to it
                                    List<PostItem> itemsForDate = new ArrayList<>();
                                    itemsForDate.add(postItem);
                                    groupedItems.put(formattedDate, itemsForDate);
                                }
                            }
                        }
                        numTransactions.setText(MessageFormat.format("Number of transactions received :  {0}", postItems.size()));


                        recyclerView = findViewById(R.id.admin_report_recycler);
                        adminPostsAdapter = new AdminPostAdapter(groupedItems, AdminGeneralReport.this);
                        recyclerView.setAdapter(adminPostsAdapter);
                        layoutManager = new LinearLayoutManager(AdminGeneralReport.this, LinearLayoutManager.VERTICAL, false);
                        recyclerView.setLayoutManager(layoutManager);

                        floatingActionButton = findViewById(R.id.get_statement_fab);
                        floatingActionButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                generatePdfDocument();
                            }
                        });

                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle the error
            }
        });

// Add the request to the RequestQueue
        queue.add(stringRequest);
    }

    private void retrievePostsByDateRange(String startDate, String endDate) {
        Call<List<PostItem>> call = postsService.getPostsByDateRange(startDate, endDate);
        call.enqueue(new Callback<List<PostItem>>() {
            @Override
            public void onResponse(Call<List<PostItem>> call, Response<List<PostItem>> response) {
                if (response.isSuccessful()) {
                    List<PostItem> postItemList = response.body();
                    assert postItemList != null;
                    Log.d("Retrieved post", "Number of items in postItemList: " + postItemList.size());
                    // Process the retrieved list of PostItems
                    // Update your RecyclerView or any other UI component with the retrieved data
                    Map<String, List<PostItem>> groupedItems = new TreeMap<>(Collections.reverseOrder());

                    for (PostItem postItem : postItemList) {
                        if (postItem.getDateTime() != null) {
                            String date = postItem.getDateTime();
                            Date date2 = null;
                            try {
                                date2 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault()).parse(date);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
                            assert date2 != null;
                            String formattedDate = simpleDateFormat.format(date2);

                            // Check if the date is already present in the map
                            if (groupedItems.containsKey(formattedDate)) {
                                // Date exists, retrieve the existing list of items and add the current item to it
                                List<PostItem> itemsForDate = groupedItems.get(formattedDate);
                                itemsForDate.add(postItem);
                            } else {
                                // Date does not exist, create a new list and add the current item to it
                                List<PostItem> itemsForDate = new ArrayList<>();
                                itemsForDate.add(postItem);
                                groupedItems.put(formattedDate, itemsForDate);
                            }
                        }
                    }
                    numTransactions.setText(MessageFormat.format("Number of transactions received :  {0}", postItemList.size()));


                    recyclerView = findViewById(R.id.admin_report_recycler);
                    adminPostsAdapter = new AdminPostAdapter(groupedItems, AdminGeneralReport.this);
                    recyclerView.setAdapter(adminPostsAdapter);
                    layoutManager = new LinearLayoutManager(AdminGeneralReport.this, LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(layoutManager);
                    floatingActionButton = findViewById(R.id.get_statement_fab);
                    floatingActionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            generatePdfDocument();
                        }
                    });

                } else {
                    // Handle API call error
                    Log.e("Retrieved posts", "Error retrieving");
                    Toast.makeText(AdminGeneralReport.this, "Failed to retrieve any post", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PostItem>> call, Throwable t) {
                // Handle API call failure
            }
        });
    }

    private void retrievePostsByEmailAndDateRange(String email, String startDate, String endDate) {
        Call<List<PostItem>> call = postsService.getPostsByEmailAndDateRange(email, startDate, endDate);
        call.enqueue(new Callback<List<PostItem>>() {
            @Override
            public void onResponse(Call<List<PostItem>> call, Response<List<PostItem>> response) {
                if (response.isSuccessful()) {
                    List<PostItem> postItemList = response.body();
                    assert postItemList != null;
                    Log.d("Retrieved post", "Number of items in postItemList: " + postItemList.size());
                    // Process the retrieved list of PostItems
                    // Update your RecyclerView or any other UI component with the retrieved data
                    Map<String, List<PostItem>> groupedItems = new TreeMap<>(Collections.reverseOrder());

                    for (PostItem postItem : postItemList) {
                        if (postItem.getDateTime() != null) {
                            String date = postItem.getDateTime();
                            Date date2 = null;
                            try {
                                date2 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault()).parse(date);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
                            assert date2 != null;
                            String formattedDate = simpleDateFormat.format(date2);

                            // Check if the date is already present in the map
                            if (groupedItems.containsKey(formattedDate)) {
                                // Date exists, retrieve the existing list of items and add the current item to it
                                List<PostItem> itemsForDate = groupedItems.get(formattedDate);
                                itemsForDate.add(postItem);
                            } else {
                                // Date does not exist, create a new list and add the current item to it
                                List<PostItem> itemsForDate = new ArrayList<>();
                                itemsForDate.add(postItem);
                                groupedItems.put(formattedDate, itemsForDate);
                            }
                        }
                    }

                    recyclerView = findViewById(R.id.admin_report_recycler);
                    adminPostsAdapter = new AdminPostAdapter(groupedItems, AdminGeneralReport.this);
                    recyclerView.setAdapter(adminPostsAdapter);
                    layoutManager = new LinearLayoutManager(AdminGeneralReport.this, LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(layoutManager);
                    floatingActionButton = findViewById(R.id.get_statement_fab);
                    floatingActionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            generatePdfDocument();
                        }
                    });


                } else {
                    // Handle API call error
                    Log.e("Retrieved posts", "Error retrieving");
                    Toast.makeText(AdminGeneralReport.this, "Failed to retrieve any post", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PostItem>> call, Throwable t) {
                // Handle API call failure
            }
        });
    }

    private void retrievePostsByFulNameAndDateRange(String fullName, String startDate, String endDate) {
        Call<List<PostItem>> call = postsService.getPostsByNameAndDateRange(fullName, startDate, endDate);
        call.enqueue(new Callback<List<PostItem>>() {
            @Override
            public void onResponse(Call<List<PostItem>> call, Response<List<PostItem>> response) {
                if (response.isSuccessful()) {
                    List<PostItem> postItemList = response.body();
                    assert postItemList != null;
                    Log.d("Retrieved post", "Number of items in postItemList: " + postItemList.size());
                    // Process the retrieved list of PostItems
                    // Update your RecyclerView or any other UI component with the retrieved data
                    Map<String, List<PostItem>> groupedItems = new TreeMap<>(Collections.reverseOrder());

                    for (PostItem postItem : postItemList) {
                        if (postItem.getDateTime() != null) {
                            String date = postItem.getDateTime();
                            Date date2 = null;
                            try {
                                date2 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault()).parse(date);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
                            assert date2 != null;
                            String formattedDate = simpleDateFormat.format(date2);

                            // Check if the date is already present in the map
                            if (groupedItems.containsKey(formattedDate)) {
                                // Date exists, retrieve the existing list of items and add the current item to it
                                List<PostItem> itemsForDate = groupedItems.get(formattedDate);
                                itemsForDate.add(postItem);
                            } else {
                                // Date does not exist, create a new list and add the current item to it
                                List<PostItem> itemsForDate = new ArrayList<>();
                                itemsForDate.add(postItem);
                                groupedItems.put(formattedDate, itemsForDate);
                            }
                        }
                    }

                    recyclerView = findViewById(R.id.admin_report_recycler);
                    adminPostsAdapter = new AdminPostAdapter(groupedItems, AdminGeneralReport.this);
                    recyclerView.setAdapter(adminPostsAdapter);
                    layoutManager = new LinearLayoutManager(AdminGeneralReport.this, LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(layoutManager);
                    floatingActionButton = findViewById(R.id.get_statement_fab);
                    floatingActionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            generatePdfDocument();
                        }
                    });


                } else {
                    // Handle API call error
                    Log.e("Retrieved posts", "Error retrieving");
                    Toast.makeText(AdminGeneralReport.this, "Failed to retrieve any post", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PostItem>> call, Throwable t) {
                // Handle API call failure
            }
        });
    }

    private void generatePdfDocument() {
        // Create a file picker intent
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "post_items_report.pdf");

// Start the file picker activity
        savePdfLauncher.launch("post_items_report.pdf");

    }
}