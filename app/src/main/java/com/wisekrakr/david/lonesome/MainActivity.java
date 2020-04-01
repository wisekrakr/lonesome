package com.wisekrakr.david.lonesome;

import androidx.appcompat.app.AppCompatActivity;
import com.wisekrakr.david.lonesome.app.clickhandlers.AuthenticationClick;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private AuthenticationClick authenticationClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        //init authentication
        authenticationClick = new AuthenticationClick(this);
        authenticationClick.init();
        authenticationClick.login();
        authenticationClick.register();

    }

}
