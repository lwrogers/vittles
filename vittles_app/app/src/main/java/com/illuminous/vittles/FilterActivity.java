package com.illuminous.vittles;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FilterActivity extends AppCompatActivity {
    //editviews to get user input for parameters for the yelp call
    EditText mKeyword;
    EditText mLocation;
    EditText mRadius;
    Button mOpenNow;
    String openNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        //set the variables to the view in the layout
        mKeyword = (EditText) findViewById(R.id.edit_keyword);
        mLocation = (EditText) findViewById(R.id.edit_location);
        mRadius = (EditText) findViewById(R.id.edit_radius);
        mOpenNow = (Button) findViewById(R.id.button_open_now);
        openNow = "true";
    }

    public void searchFood(View view) {
        if(TextUtils.isEmpty(mKeyword.getText().toString()) || TextUtils.isEmpty(mLocation.getText().toString()) || TextUtils.isEmpty(mRadius.getText().toString())) {
            Toast.makeText(this, "Please fill out all parameters.", Toast.LENGTH_SHORT).show();     //if any of the edit views is left empty we pop up a toast to notify the user that they must fill all fields.
        } else {
            //code here fetches the user input from the appropriate fields and converts it to a string
            String keyword = mKeyword.getText().toString();
            String location = mLocation.getText().toString();
            String radius = mRadius.getText().toString();

            //below code transfers the user inputed variables from this activity to the main activity.
            Intent intent = new Intent(this, MainActivity.class);
            Bundle extras = new Bundle();
            extras.putString("keyword",keyword);
            extras.putString("location",location);
            extras.putString("radius",radius);
            extras.putString("openNow", openNow);
            intent.putExtras(extras);
            startActivity(intent);
        }

    }

    public void openNowSwitch (View view) {
        if (openNow.equals("true")) {
            openNow = "false";
            mOpenNow.setText("All");
            mOpenNow.setTextColor(getResources().getColor(R.color.warning));
            mOpenNow.setBackground(getResources().getDrawable(R.drawable.orange_transparent));

        } else {
            openNow = "true";
            mOpenNow.setText("Open Now");
            mOpenNow.setTextColor(getResources().getColor(R.color.green));
            mOpenNow.setBackground(getResources().getDrawable(R.drawable.green_transparent));
        }
    }
}
