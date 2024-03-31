package com.example.collegealertapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class Admin_user extends AppCompatActivity {

    LinearLayout adminLayout,userLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user);

        userLayout=findViewById(R.id.userLayout);
        adminLayout=findViewById(R.id.adminLayout);

        userLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent myIntent=new Intent(Admin_user.this, Usersign_up.class);
                startActivity(myIntent);
            }
        });

        adminLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent myIntent=new Intent(Admin_user.this, Adminsign_up.class);
                startActivity(myIntent);
            }
        });
    }
}