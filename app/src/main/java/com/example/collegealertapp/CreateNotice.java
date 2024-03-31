package com.example.collegealertapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CreateNotice extends AppCompatActivity {

    private EditText titleEt, descriptionEt,dateEt;
    private MaterialButton postAdBtn;
    private DatabaseReference noticesRef;

    ImageButton toolbarAddImageBtn,toolbarBackBtn;


    private ActivityResultLauncher<String[]> requestCameraPermission;
    private ActivityResultLauncher<String> requestStoragePermission;

    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int GALLERY_REQUEST_CODE = 102;

    private RecyclerView imagesRv;
    private AdapterImagesPicked adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notice);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Initialize Firebase Database reference
        noticesRef = FirebaseDatabase.getInstance().getReference("notices");

        titleEt = findViewById(R.id.titleEt);
        dateEt = findViewById(R.id.dateEt);
        descriptionEt = findViewById(R.id.descriptionEt);
        postAdBtn = findViewById(R.id.postAdBtn);
        toolbarAddImageBtn = findViewById(R.id.toolbarAddImageBtn);
        imagesRv = findViewById(R.id.imagesRv);
        toolbarBackBtn = findViewById(R.id.toolbarBackBtn);

        // Initialize RecyclerView adapter
        adapter = new AdapterImagesPicked(this);
        imagesRv.setAdapter(adapter);


        toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(CreateNotice.this, AdminPage.class);
                startActivity(myIntent);
            }
        });

        postAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String currentDate = dateFormat.format(new Date());
                dateEt.setText(currentDate);
                createNotice();
            }
        });

        toolbarAddImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickOptions();
            }
        });

        // Request permission contracts
        requestCameraPermission = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            if (result.containsKey(Manifest.permission.CAMERA) && result.get(Manifest.permission.CAMERA)) {
                // Camera permission granted
                // Launch camera intent
                launchCamera();
            } else {
                // Camera permission denied
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        });

        requestStoragePermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            if (result) {
                // Storage permission granted
                // Launch gallery intent
                pickImageGallery();
            } else {
                // Storage permission denied
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void createNotice() {
        String date = dateEt.getText().toString().trim();
        String title = titleEt.getText().toString().trim();
        String description = descriptionEt.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique key for the notice
        String noticeId = noticesRef.push().getKey();

        // Get current date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = dateFormat.format(new Date());

        // Get the current user's ID using Firebase Authentication
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String creatorId = mAuth.getCurrentUser().getUid();

        // Create Notice object with current date and creator ID
        Notice notice = new Notice(noticeId, currentDate, title, description, adapter.getImageUrls(), creatorId);

        // Add notice to Firebase Realtime Database
        noticesRef.child(noticeId).setValue(notice)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(CreateNotice.this, "Notice created successfully", Toast.LENGTH_SHORT).show();
                    // Clear EditText fields after successful submission
                    dateEt.setText("");
                    titleEt.setText("");
                    descriptionEt.setText("");

                    // Add intent to go to MainActivity
                    Intent intent = new Intent(CreateNotice.this, MainActivity.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to create notice: " + e.getMessage());
                    Toast.makeText(CreateNotice.this, "Failed to create notice: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showImagePickOptions() {
        Log.d(TAG, "showImagePickOptions: ");
        //init the PopupMenu. Param 1 is context. Param 2 is Anchor view for this popup. The popup will appear below the anchor if there is room, or above it if there is not enough room
        PopupMenu popupMenu = new PopupMenu(this, toolbarAddImageBtn);

        //add menu items to our popup menu Param#1 is GroupID, Param#2 is ItemID, Param#3 is OrderID, Param#4 is Menu Item Title
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Camera");
        popupMenu.getMenu().add(Menu.NONE, 2, 2, "Gallery");
        //Show Popup Menu
        popupMenu.show();

        //handle popup menu item click
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //get the id of the item clicked in popup menu
                int itemId = item.getItemId();
                //check which item id is clicked from popup menu. 1=Camera. 2=Gallery as we defined
                if (itemId == 1) {
                    //Camera is clicked we need to check if we have permission of Camera, Storage before Launching Camera to Capture image
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        //Device version is TIRAMISU or above. We only need Camera permission
                        String[] cameraPermissions = new String[]{Manifest.permission.CAMERA};
                        requestCameraPermission.launch(cameraPermissions);
                    } else {
                        //Device version is below TIRAMISU. We need Camera & Storage permissions
                        String[] cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestCameraPermission.launch(cameraPermissions);
                    }
                } else if (itemId == 2) {
                    //Gallery is clicked we need to check if we have permission of Storage before launching Gallery to Pick image
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        //Device version is TIRAMISU or above. We don't need Storage permission to launch Gallery
                        pickImageGallery(); // Call the method directly here
                    } else {
                        //Device version is below TIRAMISU. We need Storage permission to launch Gallery
                        String storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                        requestStoragePermission.launch(storagePermission);
                    }
                }
                return true;
            }
        });
    }

    private void launchCamera() {
        // Launch camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void pickImageGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Handle the selected image from the gallery
            Uri selectedImageUri = data.getData();
            // Now, you can use this URI to display the selected image in your RecyclerView
            if (selectedImageUri != null) {
                // Pass the Uri directly to addImage method
                adapter.addImage(selectedImageUri);
            }
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Handle the captured image from the camera
            Bundle extras = data.getExtras();
            if (extras != null) {
                // Extract the bitmap from the extras
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                // Convert bitmap to URI
                Uri imageUri = getImageUri(this, imageBitmap);
                // Now, you can use this URI to display the captured image in your RecyclerView
                if (imageUri != null) {
                    // Pass the Uri directly to addImage method
                    adapter.addImage(imageUri);
                }
            }
        }
    }


    // Helper method to convert Bitmap to URI
    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }
}
