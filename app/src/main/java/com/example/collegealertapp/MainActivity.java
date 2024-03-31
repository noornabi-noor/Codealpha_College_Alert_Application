package com.example.collegealertapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView noticeRecyclerView;
    private NoticeAdapter noticeAdapter;
    private List<Notice> noticeList;

    ImageButton toolbarBackBtn,toolbarLogoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noticeRecyclerView = findViewById(R.id.noticeRecyclerView);
        noticeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        noticeList = new ArrayList<>();
        noticeAdapter = new NoticeAdapter(noticeList);
        noticeRecyclerView.setAdapter(noticeAdapter);
        toolbarBackBtn = findViewById(R.id.toolbarBackBtn);
        toolbarLogoutBtn = findViewById(R.id.toolbarLogoutBtn);


        // Check if the current user is an admin
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Check if the user is an admin (You need to implement this logic based on your user management)
            // For example, you can check if the user's email is in a specific list of admin emails
            String adminEmail = "admin@gmail.com";
            if (currentUser.getEmail().equals(adminEmail)) {
                // User is an admin, show the toolbarBackBtn
                toolbarBackBtn.setVisibility(View.VISIBLE);
            } else {
                // User is not an admin, hide the toolbarBackBtn
                toolbarBackBtn.setVisibility(View.GONE);
            }
        } else {
            // User is not logged in, hide the toolbarBackBtn
            toolbarBackBtn.setVisibility(View.GONE);
        }

        toolbarBackBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent myIntent=new Intent(MainActivity.this, AdminPage.class);
                startActivity(myIntent);
            }
        });

        // Set OnClickListener for toolbarLogoutBtn
        toolbarLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logout user
                FirebaseAuth.getInstance().signOut();
                // Redirect to login page or any other appropriate action
                Intent intent = new Intent(MainActivity.this, Admin_user.class);
                startActivity(intent);
                finish(); // Close MainActivity
            }
        });

        // Retrieve notices from Firebase Realtime Database
        Query noticesQuery = FirebaseDatabase.getInstance().getReference("notices");
        noticesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noticeList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Notice notice = snapshot.getValue(Notice.class);
                    noticeList.add(notice);
                }
                noticeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    // Adapter for Notice RecyclerView
    private class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder> {

        private List<Notice> noticeList;

        public NoticeAdapter(List<Notice> noticeList) {
            this.noticeList = noticeList;
        }


        @NonNull
        @Override
        public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_ad, parent, false);
            return new NoticeViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {
            Notice notice = noticeList.get(position);
            holder.titleTv.setText(notice.getTitle());
            holder.dateTv.setText(notice.getDate());

            // Check if imageUrls list is not null
            if (notice.getImageUrls() != null && !notice.getImageUrls().isEmpty()) {
                // Load the first image using Picasso library (or any other image loading library of your choice)
                Picasso.get().load(notice.getImageUrls().get(0)).into(holder.imageIv);
            } else {
                // Handle case when imageUrls list is null or empty
                // For example, you can set a placeholder image or hide the ImageView
                holder.imageIv.setImageResource(R.drawable.baseline_image_24); // Replace with your placeholder image
            }
        }



        @Override
        public int getItemCount() {
            return noticeList.size();
        }


        public class NoticeViewHolder extends RecyclerView.ViewHolder {
            ImageView imageIv;
            TextView titleTv, dateTv;
            View itemView;

            public NoticeViewHolder(@NonNull View itemView) {
                super(itemView);
                this.itemView = itemView;
                imageIv = itemView.findViewById(R.id.imageIv);
                titleTv = itemView.findViewById(R.id.titleTv);
                dateTv = itemView.findViewById(R.id.dateTv);

                // Set OnClickListener to handle item click
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get the position of the clicked item
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            // Get the Notice object at the clicked position
                            Notice clickedNotice = noticeList.get(position);

                            // Start the AdDetailsActivity and pass necessary data
                            Intent intent = new Intent(itemView.getContext(), AdDetailsActivity.class);
                            intent.putExtra("noticeId", clickedNotice.getNoticeId()); // Pass the notice ID as "noticeId"
                            itemView.getContext().startActivity(intent);
                        }
                    }
                });

            }
        }

    }
}

