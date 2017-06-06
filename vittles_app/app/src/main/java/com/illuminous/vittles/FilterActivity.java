package com.illuminous.vittles;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

public class FilterActivity extends AppCompatActivity {

    EditText mKeyword;
    EditText mLocation;
    EditText mRadius;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        mKeyword = (EditText) findViewById(R.id.edit_keyword);
        mLocation = (EditText) findViewById(R.id.edit_location);
        mRadius = (EditText) findViewById(R.id.edit_radius);
    }

    public void searchFood(View view) {
        if(TextUtils.isEmpty(mKeyword.getText().toString()) || TextUtils.isEmpty(mLocation.getText().toString()) || TextUtils.isEmpty(mRadius.getText().toString())) {
            Toast.makeText(this, "Please fill out all parameters.", Toast.LENGTH_SHORT).show();
        } else {
            String keyword = mKeyword.getText().toString();
            String location = mLocation.getText().toString();
            String radius = mRadius.getText().toString();
            Intent intent = new Intent(this, MainActivity.class);
            Bundle extras = new Bundle();
            extras.putString("keyword",keyword);
            extras.putString("location",location);
            extras.putString("radius",radius);
            intent.putExtras(extras);
            startActivity(intent);
        }

    }
}
