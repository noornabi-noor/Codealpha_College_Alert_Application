package com.example.collegealertapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AdminPage extends AppCompatActivity {
    TextView notification;
    ImageButton toolbarForwardBtn;
    LinearLayout noticeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        //notification=findViewById(R.id.notification);

        noticeLayout=findViewById(R.id.noticeLayout);
        toolbarForwardBtn=findViewById(R.id.toolbarForwardBtn);

        noticeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent myIntent=new Intent(AdminPage.this, CreateNotice.class);
                startActivity(myIntent);
            }
        });

        toolbarForwardBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent myIntent=new Intent(AdminPage.this, MainActivity.class);
                startActivity(myIntent);
            }
        });

    }
}