package com.example.sammwangi.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.sammwangi.R;
import com.example.sammwangi.adapters.MediaAdapter;
import com.google.android.material.button.MaterialButton;

import java.io.ByteArrayOutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Random;

public class MediaPickerActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 2;
    private static final int REQUEST_CODE_ADD_DETAILS = 3;
    private static final int REQUEST_CODE_PHOTO_EDITOR = 101;

    private GridView gridView;
    private MediaAdapter mediaAdapter;
    private ImageView selectedPreviewImageView;
    private VideoView previewVideo;
    private MaterialButton continueButton;
    private ArrayList<String> selectedItems;
    private Uri uri;
    private boolean isMultiSelectEnabled = false;
    private boolean isEditMode;
    private int selectedItemsInt = 0;

    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_picker);
        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.media_picker_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Pick an image for your stori");
        }

        // Initialize views
        gridView = findViewById(R.id.grid_view);
        selectedPreviewImageView = findViewById(R.id.selectedPreviewImageView);
        continueButton = findViewById(R.id.media_picker_continue_button);
        previewVideo = findViewById(R.id.selectedPreviewVideoView);

        // Request permission to read external storage if not granted
        selectedItems = new ArrayList<>();

        checkInternalStoragePermissions();

        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("editMode", false);

        if (!isEditMode) {
            continueButton.setText("Proceed");
            continueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("selectedImagePath", selectedItems.get(0));
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            });
        }
    }

    private void checkInternalStoragePermissions() {
        // Check and request permission to access external storage
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            loadMediaFiles();
        }
    }

    private void showPermissionDeniedMessage() {
        Toast.makeText(this, "Permission denied. The app won't be able to load media files.", Toast.LENGTH_LONG).show();
        // Alternatively, you can show a dialog to inform the user:
        // AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // builder.setTitle("Permission Denied");
        // builder.setMessage("The app won't be able to load media files without the required permission.");
        // builder.setPositiveButton("OK", null);
        // builder.show();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMediaFiles() {
        Uri mediaUri = MediaStore.Files.getContentUri("external");
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

        String[] projection = {MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.MEDIA_TYPE};
        String sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";

        Cursor cursor = getContentResolver().query(mediaUri, projection, selection, null, sortOrder);

        if (cursor != null) {
            ArrayList<String> filePaths = new ArrayList<>();
            int columnIndexId = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
            int columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
            while (cursor.moveToNext()) {
                String filePath = cursor.getString(columnIndexData);
                int mediaType = cursor.getInt(columnIndexData);
                filePaths.add(filePath);
            }
            cursor.close();

            // Set up GridView adapter
            mediaAdapter = new MediaAdapter(this, filePaths);
            gridView.setAdapter(mediaAdapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (isMultiSelectEnabled) {
                        mediaAdapter.toggleItemSelection(position);
                        selectedItems = mediaAdapter.getSelectedItems();
                        Log.d("Media picker activity", "selected items list size: " + selectedItems.size());
                        selectedItemsInt = selectedItems.size();
                        if (selectedItems.isEmpty()) {
                            selectedPreviewImageView.setVisibility(View.GONE);
                        } else {
                            updatePreview(selectedItems.get(selectedItems.size() - 1));
                            selectedPreviewImageView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        String selectedItem = mediaAdapter.getItem(position);
                        selectedItems.clear();
                        selectedItems.add(selectedItem);
                        mediaAdapter.notifyDataSetChanged();
                        updatePreview(selectedItem);
                        selectedPreviewImageView.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }
    private void updatePreview(String filePath) {
        if (filePath.endsWith(".mp4") || filePath.endsWith(".3gp") || filePath.endsWith(".mkv")) {
            // Video file selected
            // Show video preview
            selectedPreviewImageView.setVisibility(View.GONE);
            previewVideo.setVideoPath(filePath);
            previewVideo.start();
            previewVideo.setVisibility(View.VISIBLE);
        } else {
            // Image file selected
            // Show image preview
            previewVideo.setVisibility(View.GONE);
            Glide.with(this)
                    .load(filePath)
                    .into(selectedPreviewImageView);
            selectedPreviewImageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void backToStorisActivity() {
        Intent intent = new Intent();
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        backToStorisActivity();
        super.onBackPressed();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted, load media files
                loadMediaFiles();
            } else {
                // Permission has been denied, show a message to the user
                showPermissionDeniedMessage();
                // Alternatively, you can take further actions like disabling related functionality.
            }
        }
    }
}

