package com.wisekrakr.david.lonesome.app.clickhandlers;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.wisekrakr.david.lonesome.R;
import com.wisekrakr.david.lonesome.app.activities.auth.LoginActivity;
import com.wisekrakr.david.lonesome.app.activities.auth.RegisterActivity;

public class AuthenticationClick implements AuthenticationClickContext {

    // views
    private Button registerBtn, loginBtn;

    private Activity app;

    public AuthenticationClick(Activity app) {
         this.app = app;
    }


    @Override
    public void init() {
        //init views
        registerBtn = app.findViewById(R.id.register_btn);
        loginBtn = app.findViewById(R.id.login_btn);
    }

    @Override
    public void login() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start LoginActivity
                app.startActivity(new Intent(app, LoginActivity.class));
            }
        });
    }

    @Override
    public void register() {
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start RegisterActivity
                app.startActivity(new Intent(app, RegisterActivity.class));
            }
        });
    }
}
