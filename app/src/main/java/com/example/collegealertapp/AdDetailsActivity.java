//package com.example.collegealertapp;
//
//import androidx.annotation.ColorRes;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.content.ContextCompat;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.res.Configuration;
//import android.graphics.Color;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.EditText;
//import android.widget.PopupMenu;
//import android.widget.TextView;
//
//import com.bumptech.glide.Glide;
//
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.material.dialog.MaterialAlertDialogBuilder;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Objects;
//import java.util.Random;
//
//public class AdDetailsActivity extends AppCompatActivity {
//    // View Binding
//    private ActivityAdDetailsBinding binding;
//
//    // Log tag for logs in logcat
//    private static final String TAG = "AD_DETAILS_TAG";
//
//    //Context for this fragment class
//    private Context mContext;
//
//    // Firebase Auth for auth related tasks
//    private FirebaseAuth firebaseAuth;
//    DatabaseReference reportRef;
//
//    // Ad id, will get from intent
//    private String adId = "";
//
//    // To load seller info, chat with seller, SMS, and call
//    private String sellerUid = null;
//
//    // List of Ad's images to show in the slider
//    private ArrayList<ModelImageSlider> imageSliderArrayList;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        // Init view binding... activity_ad_details.xml = ActivityAdDetailsBinding
//        binding = ActivityAdDetailsBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        setStatusBarColor(R.color.DarkGreen, R.color.DarkGreen);
//
//
//        // Hide some UI views at the start.
//        // We will show the Edit, Delete option if the user is Ad owner.
//        binding.toolbarEditBtn.setVisibility(View.GONE);
//        binding.toolbarDeleteBtn.setVisibility(View.GONE);
//
//        // Get the id of the Ad (as passed in AdapterAd class while starting this activity)
//        adId = getIntent().getStringExtra("adId");
//        Log.d(TAG, "onCreate: adId: " + adId);
//
//        // Firebase Auth for auth related tasks
//        firebaseAuth = FirebaseAuth.getInstance();
//
//        // Load Ad details, Ad images, and handle toolbar button clicks
//        loadAdDetails();
//        loadAdImages();
//
//
//        // Handle toolbarBackBtn click, go back
//        binding.toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//
//        //Handle toolbarDeleteBtn click, delete Ad
//        binding.toolbarDeleteBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Alert dialog to confirm if the user really wants to delete the Ad
//                MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(AdDetailsActivity.this);
//                materialAlertDialogBuilder.setTitle("Delete Ad")
//                        .setMessage("Are you sure you want to delete this Ad?")
//                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                // Delete Clicked, delete Ad
//                                deleteAd();
//                            }
//                        })
//                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                // Cancel Clicked, dismiss dialog
//                                dialog.dismiss();
//                            }
//                        })
//                        .show();
//            }
//        });
//
//        // Handle toolbarEditBtn click, start AdCreateActivity to edit this Ad
//        binding.toolbarEditBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editOptions();
//            }
//        });
//
//
//    }
//    private void setStatusBarColor(@ColorRes int lightColorRes, @ColorRes int darkColorRes) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//
//            // Check the current theme mode
//            int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
//            int colorRes = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) ? darkColorRes : lightColorRes;
//
//            window.setStatusBarColor(ContextCompat.getColor(this, colorRes));
//
//            //bottom nav bar is always Dark id dark mode on
//            if(nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
//                getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorGray03));
//            }
//        }
//    }
//
//    private void loadAdDetails() {
//        Log.d(TAG, "LoadAdDetails: ");
//        // Ad's db path to get the Ad details. Ads > AdId
//        Log.d(TAG, "adid here "+adId);
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
//        ref.child(adId)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        try {
//                            // Setup model from firebase DataSnapshot
//                            ModelAd modelAd = snapshot.getValue(ModelAd.class);
//
//                            // Get data from the model
//                            //sellerUid = modelAd.getUid();
//                            sellerUid = snapshot.child("vid").getValue(String.class);
//                            String title = modelAd.getTitle();
//                            String description = modelAd.getDescription();
//
//
//                            // Format date time e.g. timestamp to dd/MM/yyyy
//                            String formattedDate = Utils.formatTimestampDate(timestamp);
//
//                            Log.d(TAG, "Uid = " + sellerUid);
//
//
//                            if(firebaseAuth.getCurrentUser() != null) {
//                                DatabaseReference refAdmin = FirebaseDatabase.getInstance().getReference ("Users");
//                                refAdmin.child(firebaseAuth.getUid())
//                                        .addValueEventListener(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                if(snapshot.child("isAdmin").exists() == true) {
//                                                    binding.toolbarDeleteBtn.setVisibility(View.VISIBLE);
//                                                } else if(sellerUid != null && sellerUid.equals(firebaseAuth.getUid()) && Objects.equals(isSold, Utils.AD_STATUS_AVAILABLE)){
//                                                    binding.toolbarDeleteBtn.setVisibility(View.VISIBLE);
//                                                } else {
//                                                    binding.toolbarDeleteBtn.setVisibility(View.GONE);
//                                                    binding.toolbarEditBtn.setVisibility(View.GONE);
//                                                }
//                                            }
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError error) {
//
//                                            }
//                                        });
//
//
//                                // Check if the Ad is by the currently signed-in user
//                                if (sellerUid != null && sellerUid.equals(firebaseAuth.getUid())) {
//                                    // Ad is created by currently signed-in user
//                                    // 1) Should be able to edit and delete Ad
//                                    binding.toolbarEditBtn.setVisibility(View.VISIBLE);
//                                } else {
//                                    // Ad is not created by currently signed-in user
//                                    // 1) Shouldn't be able to edit and delete Ad
//                                    binding.toolbarEditBtn.setVisibility(View.GONE);
//
//                                }
//                            }
//
//
//                            // Set data to UI Views
//                            binding.titleTv.setText(title);
//                            binding.descriptionTv.setText(description);
//                            binding.dateTv.setText(formattedDate);
//
//                        } catch (Exception e) {
//                            Log.e(TAG, "onDataChange_LoadAd: ", e);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                    }
//                });
//    }
//
//    // Method to load images associated with the current ad from the Firebase Realtime Database
//    private void loadAdImages() {
//        Log.d(TAG, "loadAdImages: ");
//
//        // Initialize list before adding data into it
//        imageSliderArrayList = new ArrayList<>();
//
//        // Database path to load ad images: Ads > adId > Images
//        if (adId != null) {
//            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
//            ref.child(adId).child("Images").addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    // Clear the list before adding data into it
//                    imageSliderArrayList.clear();
//
//                    // Loop through each child snapshot to load all images
//                    for (DataSnapshot ds : snapshot.getChildren()) {
//                        // Prepare model (spellings in model class should be the same as in Firebase)
//                        ModelImageSlider modelImageSlider = ds.getValue(ModelImageSlider.class);
//
//                        // Add the prepared model to the list
//                        imageSliderArrayList.add(modelImageSlider);
//                    }
//
//                    // Setup adapter and set it to the ViewPager (imageSliderVp)
//                    AdapterImageSlider adapterImageSlider = new AdapterImageSlider(
//                            AdDetailsActivity.this, imageSliderArrayList);
//                    binding.imageSliderVp.setAdapter(adapterImageSlider);
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    // Handle onCancelled event
//                }
//            });
//        } else {
//            Log.e(TAG, "Ad ID is null");
//        }
//
//    }
//
//    // Method to delete the current ad
//    private void deleteAd() {
//        Log.d(TAG, "deleteAd: ");
//
//        // Database path to delete the ad: Ads > adId
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
//        ref.child(adId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//                // Success: Display a message and finish the activity
//                Log.d(TAG, "onSuccess: Deleted");
//                Utils.toast(AdDetailsActivity.this, "Deleted");
//                //finish activity and go - back
//                finish();
//            }
//
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                // Failure: Display an error message
//                Log.e(TAG, "onFailure: ", e);
//                Utils.toast(AdDetailsActivity.this,
//                        "Failed to delete due to " + e.getMessage());
//            }
//        });
//    }
//}






//
//package com.example.collegealertapp;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.viewpager2.widget.ViewPager2;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.List;
//
//public class AdDetailsActivity extends AppCompatActivity {
//
//    private DatabaseReference mDatabase;
//    private String currentUserId;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_ad_details);
//
//        mDatabase = FirebaseDatabase.getInstance().getReference().child("notices");
//        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//        // Initialize views
//        TextView titleTv = findViewById(R.id.titleTv);
//        TextView dateTv = findViewById(R.id.dateTv);
//        TextView descriptionTv = findViewById(R.id.descriptionTv);
//        ViewPager2 imageSliderVp=findViewById(R.id.imageSliderVp);
//
//        // Fetch and display notice details
//        String noticeId = getIntent().getStringExtra("noticeId"); // Retrieve "noticeId" extra
//        if (noticeId == null) {
//            // Handle case where notice ID is not provided
//            Toast.makeText(this, "Notice ID not provided", Toast.LENGTH_SHORT).show();
//            finish(); // Close activity
//        } else {
//            fetchNoticeDetails(noticeId);
//        }
//    }
//
//    // Method to fetch notice details from the database
//    private void fetchNoticeDetails(String noticeId) {
//        DatabaseReference noticeRef = mDatabase.child(noticeId);
//        noticeRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Notice notice = dataSnapshot.getValue(Notice.class);
//                if (notice != null) {
//                    displayNoticeDetails(notice);
//                    // Check if the current user is the creator of the notice
//                    String creatorId = notice.getCreatorId();
//                    if (creatorId.equals(currentUserId)) {
//                        // Current user is the creator, enable edit and delete buttons
//                        findViewById(R.id.toolbarEditBtn).setVisibility(View.VISIBLE);
//                        findViewById(R.id.toolbarDeleteBtn).setVisibility(View.VISIBLE);
//                    } else {
//                        // Current user is not the creator, disable edit and delete buttons
//                        findViewById(R.id.toolbarEditBtn).setVisibility(View.GONE);
//                        findViewById(R.id.toolbarDeleteBtn).setVisibility(View.GONE);
//                    }
//                } else {
//                    Toast.makeText(AdDetailsActivity.this, "Notice details not found", Toast.LENGTH_SHORT).show();
//                    finish(); // Close activity if notice details not found
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle error
//                Toast.makeText(AdDetailsActivity.this, "Failed to fetch notice details: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                finish(); // Close activity on error
//            }
//        });
//    }
//
//    // Method to handle delete notice operation
//    private void deleteNotice(String noticeId) {
//        DatabaseReference noticeRef = mDatabase.child(noticeId);
//        noticeRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Notice notice = dataSnapshot.getValue(Notice.class);
//                if (notice != null && notice.getCreatorId().equals(currentUserId)) {
//                    // Current user is the creator, delete the notice
//                    noticeRef.removeValue(); // Remove notice from database
//                    Toast.makeText(AdDetailsActivity.this, "Notice deleted successfully", Toast.LENGTH_SHORT).show();
//                    finish(); // Close activity after deletion
//                } else {
//                    // Current user is not the creator, show error message
//                    Toast.makeText(AdDetailsActivity.this, "You don't have permission to delete this notice", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle error
//                Toast.makeText(AdDetailsActivity.this, "Failed to delete notice: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    // Method to handle edit notice operation
//    private void editNotice(Notice notice) {
//        if (notice != null && notice.getCreatorId().equals(currentUserId)) {
//            // Current user is the creator, proceed with edit operation
//            // Implement your edit logic here
//        } else {
//            // Current user is not the creator, show error message
//            Toast.makeText(AdDetailsActivity.this, "You don't have permission to edit this notice", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    // Method to display notice details
//    private void displayNoticeDetails(Notice notice) {
//        TextView titleTv = findViewById(R.id.titleTv);
//        TextView dateTv = findViewById(R.id.dateTv);
//        TextView descriptionTv = findViewById(R.id.descriptionTv);
//        ViewPager2 imageSliderVp=findViewById(R.id.imageSliderVp);
//
//        titleTv.setText(notice.getTitle());
//        dateTv.setText(notice.getDate());
//        descriptionTv.setText(notice.getDescription());
//        // Create and set adapter for ViewPager2
//        ImageSliderAdapter adapter = new ImageSliderAdapter(this, notice.getImageUrls());
//        imageSliderVp.setAdapter(adapter);
//    }
//}



package com.example.collegealertapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AdDetailsActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String currentUserId;
    ImageButton toolbarBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_details);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("notices");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialize views
        TextView titleTv = findViewById(R.id.titleTv);
        TextView dateTv = findViewById(R.id.dateTv);
        TextView descriptionTv = findViewById(R.id.descriptionTv);
        ViewPager2 imageSliderVp = findViewById(R.id.imageSliderVp);
        toolbarBackBtn= findViewById(R.id.toolbarBackBtn);


        toolbarBackBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent myIntent=new Intent(AdDetailsActivity.this, MainActivity.class);
                startActivity(myIntent);
            }
        });

        // Fetch and display notice details
        String noticeId = getIntent().getStringExtra("noticeId");
        if (noticeId == null) {
            // Handle case where notice ID is not provided
            Toast.makeText(this, "Notice ID not provided", Toast.LENGTH_SHORT).show();
            finish(); // Close activity
        } else {
            fetchNoticeDetails(noticeId);
        }

        // Set click listeners for edit and delete buttons
        findViewById(R.id.toolbarEditBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle edit button click
                editNotice(noticeId);
            }
        });

        findViewById(R.id.toolbarDeleteBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle delete button click
                deleteNotice(noticeId);
            }
        });
    }

    // Method to fetch notice details from the database
    private void fetchNoticeDetails(String noticeId) {
        DatabaseReference noticeRef = mDatabase.child(noticeId);
        noticeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Notice notice = dataSnapshot.getValue(Notice.class);
                if (notice != null) {
                    displayNoticeDetails(notice);
                    // Check if the current user is the creator of the notice
                    String creatorId = notice.getCreatorId();
                    if (creatorId != null && creatorId.equals(currentUserId)) {
                        // Current user is the creator, enable edit and delete buttons
                        findViewById(R.id.toolbarEditBtn).setVisibility(View.VISIBLE);
                        findViewById(R.id.toolbarDeleteBtn).setVisibility(View.VISIBLE);
                    } else {
                        // Current user is not the creator, disable edit and delete buttons
                        findViewById(R.id.toolbarEditBtn).setVisibility(View.GONE);
                        findViewById(R.id.toolbarDeleteBtn).setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(AdDetailsActivity.this, "Notice details not found", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity if notice details not found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Toast.makeText(AdDetailsActivity.this, "Failed to fetch notice details: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                finish(); // Close activity on error
            }
        });
    }


    // Method to handle delete notice operation
    private void deleteNotice(String noticeId) {
        DatabaseReference noticeRef = mDatabase.child(noticeId);
        noticeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Notice notice = dataSnapshot.getValue(Notice.class);
                if (notice != null && notice.getCreatorId().equals(currentUserId)) {
                    // Current user is the creator, delete the notice
                    noticeRef.removeValue(); // Remove notice from database
                    Toast.makeText(AdDetailsActivity.this, "Notice deleted successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity after deletion
                } else {
                    // Current user is not the creator, show error message
                    Toast.makeText(AdDetailsActivity.this, "You don't have permission to delete this notice", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Toast.makeText(AdDetailsActivity.this, "Failed to delete notice: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to handle edit notice operation
    private void editNotice(String noticeId) {
        DatabaseReference noticeRef = mDatabase.child(noticeId);
        noticeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Notice notice = dataSnapshot.getValue(Notice.class);
                if (notice != null && notice.getCreatorId().equals(currentUserId)) {
                    // Current user is the creator, proceed with edit operation
                    // Start EditNoticeActivity with notice details
                    Intent intent = new Intent(AdDetailsActivity.this, EditNoticeActivity.class);
                    intent.putExtra("noticeId", noticeId);
                    intent.putExtra("title", notice.getTitle());
                    intent.putExtra("description", notice.getDescription());
                    // Add more notice details if needed
                    startActivity(intent);
                } else {
                    // Current user is not the creator, show error message
                    Toast.makeText(AdDetailsActivity.this, "You don't have permission to edit this notice", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Toast.makeText(AdDetailsActivity.this, "Failed to fetch notice details: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    // Method to display notice details
    private void displayNoticeDetails(Notice notice) {
        TextView titleTv = findViewById(R.id.titleTv);
        TextView dateTv = findViewById(R.id.dateTv);
        TextView descriptionTv = findViewById(R.id.descriptionTv);
        ViewPager2 imageSliderVp = findViewById(R.id.imageSliderVp);

        titleTv.setText(notice.getTitle());
        dateTv.setText(notice.getDate());
        descriptionTv.setText(notice.getDescription());

        // Check if the list of image URLs is not null before creating the adapter
        List<String> imageUrls = notice.getImageUrls();
        if (imageUrls != null) {
            // Create and set adapter for ViewPager2
            ImageSliderAdapter adapter = new ImageSliderAdapter(this, imageUrls);
            imageSliderVp.setAdapter(adapter);
        } else {
            // Handle case where image URLs are null
            Toast.makeText(this, "Image URLs not found", Toast.LENGTH_SHORT).show();
        }
    }

}
