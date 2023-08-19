package com.example.sammwangi.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sammwangi.adapters.AdminPostAdapter;
import com.example.sammwangi.adapters.AdminPostsAdapterInner;
import com.example.sammwangi.DAOs.PostItem;
import com.example.sammwangi.loaders.PostItemsLoader;
import com.example.sammwangi.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
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
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class AdminGeneralReport extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<PostItem>> {

    private static final int REPORT_LOADER = 94;
    private RecyclerView recyclerView;
    private AdminPostsAdapterInner adminPostsAdapterInner;
    private LinearLayoutManager layoutManager;
    private AdminPostAdapter adminPostsAdapter;
    private TextView numTransactions, parameters;
    private Toolbar toolbar;
    private ExtendedFloatingActionButton floatingActionButton;
    private ActivityResultLauncher<String> savePdfLauncher;
    private Map<String, List<PostItem>> postItems;
    private String receivedFromDate, receivedToDate, receivedEmail, receivedFullName, subCounty;
    private boolean isSpecialSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_general_report);
        toolbar = findViewById(R.id.admin_report_toolbar);
        setSupportActionBar(toolbar);
        parameters = findViewById(R.id.report_name);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        receivedFromDate = intent.getStringExtra("FROM_DATE");
        receivedToDate = intent.getStringExtra("TO_DATE");
        Log.d("Selected Date", "Selected received to date: " + receivedToDate);
        receivedFullName = intent.getStringExtra("FULL_NAME");
        Log.d("Selected fullName", "Selected full name: " + receivedFullName);
        receivedEmail = intent.getStringExtra("EMAIL");
        subCounty = intent.getStringExtra("SUB_COUNTY");

        isSpecialSearch = intent.getBooleanExtra("isSpecialSearch",false);

        String reportsText = "Reports fetched with the following filters:\n";
        if (receivedFromDate != null) {
            reportsText += "From Date: " + receivedFromDate + "\n";
        }
        if (receivedToDate != null) {
            reportsText += "To Date: " + receivedToDate + "\n";
        }
        if (receivedEmail != null) {
            reportsText += "Email: " + receivedEmail + "\n";
        }
        if (receivedFullName != null) {
            reportsText += "Full Name: " + receivedFullName + "\n";
        }
        if (subCounty != null) {
            reportsText += "Sub County: " + subCounty + "\n";
        }

        // Display the reports
        parameters.setText(reportsText);


        numTransactions = findViewById(R.id.number_of_transactions);
        postItems = new TreeMap<>();

        recyclerView = findViewById(R.id.admin_report_recycler);
        adminPostsAdapter = new AdminPostAdapter(postItems, AdminGeneralReport.this);
        recyclerView.setAdapter(adminPostsAdapter);
        layoutManager = new LinearLayoutManager(AdminGeneralReport.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        loadData();

        floatingActionButton = findViewById(R.id.get_statement_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePdfDocument();
            }
        });

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

                        String fromDate = receivedFromDate;
                        String toDate = receivedToDate;
                        String fullName = receivedFullName;
                        String email = receivedEmail;
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

    private void loadData() {
        LoaderManager.getInstance(AdminGeneralReport.this).initLoader(REPORT_LOADER,
                null,AdminGeneralReport.this);
    }

    private void addTableRow(PdfPTable table, PostItem postItem) {
        double amount = Double.parseDouble(String.valueOf(postItem.getPostAmount()));
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

    private void generatePdfDocument() {
        // Create a file picker intent
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "post_items_report.pdf");

// Start the file picker activity
        savePdfLauncher.launch("post_items_report.pdf");

    }

    @NonNull
    @Override
    public Loader<List<PostItem>> onCreateLoader(int id, @Nullable Bundle args) {
        if (!isSpecialSearch) {
            return new PostItemsLoader(AdminGeneralReport.this, receivedFromDate, receivedToDate,null);
        }else {
            if (receivedFullName != null){
                return new PostItemsLoader(AdminGeneralReport.this, receivedFromDate, receivedToDate,receivedFullName);
            } else if (receivedEmail != null) {
                return new PostItemsLoader(AdminGeneralReport.this, receivedFromDate, receivedToDate,receivedEmail);
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<PostItem>> loader, List<PostItem> data) {
        if (data != null) {
            postItems.clear();
            Log.d("AdminReportList", "Number of items in postItems: " + data.size());
            for (PostItem postItem : data) {
                if (postItem.getDateTime() != null) {
                    String date = postItem.getDateTime();
                    Date date2 = null;
                    try {
                        date2 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",
                                Locale.getDefault()).parse(date);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
                    assert date2 != null;
                    String formattedDate = simpleDateFormat.format(date2);

                    // Check if the date is already present in the map
                    if (postItems.containsKey(formattedDate)) {
                        // Date exists, retrieve the existing list of items and add the current item to it
                        List<PostItem> itemsForDate = postItems.get(formattedDate);
                        itemsForDate.add(postItem);
                    } else {
                        // Date does not exist, create a new list and add the current item to it
                        List<PostItem> itemsForDate = new ArrayList<>();
                        itemsForDate.add(postItem);
                        this.postItems.put(formattedDate, itemsForDate);
                    }
                }
            }
            numTransactions.setText(MessageFormat.format("Number of transactions received :  {0}", this.postItems.size()));
            adminPostsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<PostItem>> loader) {

    }
}