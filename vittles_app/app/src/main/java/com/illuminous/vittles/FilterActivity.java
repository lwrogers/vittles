package com.illuminous.vittles;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.location.Location;
import android.location.LocationManager;
import android.widget.NumberPicker;
import java.util.List;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FilterActivity extends AppCompatActivity {
    //editviews to get user input for parameters for the yelp call
    EditText mKeyword;
    EditText mLocation;
    //EditText mRadius;
    NumberPicker mNumberPicker;
    Button mOpenNow;
    String openNow;
    String longitude;
    String latitude;
    String radius;
    String groupName;
    Boolean groupMode;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        //set the variables to the view in the layout
        mKeyword = (EditText) findViewById(R.id.edit_keyword);
        mLocation = (EditText) findViewById(R.id.edit_location);
        //mRadius = (EditText) findViewById(R.id.edit_radius);
        mNumberPicker = (NumberPicker) findViewById(R.id.number_picker);
        mNumberPicker.setMinValue(1);
        mNumberPicker.setMaxValue(50);
        mOpenNow = (Button) findViewById(R.id.button_open_now);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        openNow = "true";
        radius = "0";
        Bundle extras = getIntent().getExtras();
        groupName = extras.getString("groupName");
        groupMode = extras.getBoolean("groupMode");

        mNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //Display the newly selected number from picker
                radius = Integer.toString(newVal);
            }
        });

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                99);

        // A reference to the location manager. The LocationManager has already
        // been set up in MyService, we're just getting a reference here.
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);
        Location l;
        // Go through the location providers starting with GPS, stop as soon
        // as we find one.
        for (int i = providers.size() - 1; i >= 0; i--) {
            //checkPermission();
            l = lm.getLastKnownLocation(providers.get(i));
            longitude = (String.format("%.6f", (l.getLongitude())));
            latitude = (String.format("%.6f", (l.getLatitude())));
            if (l != null) break;
        }
    }

    public void searchFood(View view) {
        //if(TextUtils.isEmpty(mKeyword.getText().toString())/*TextUtils.isEmpty(mLocation.getText().toString()) || TextUtils.isEmpty(mRadius.getText().toString())*/) {
            //Toast.makeText(this, "Please fill out all parameters.", Toast.LENGTH_SHORT).show();     //if any of the edit views is left empty we pop up a toast to notify the user that they must fill all fields.
        /*} else  */
        if(TextUtils.isEmpty(mLocation.getText().toString()) ) {
            String keyword = mKeyword.getText().toString();
            //String radius = mRadius.getText().toString();
            String location = "none";
            if (groupMode) {
                mDatabase.child(groupName).child("keyword").setValue(keyword);
                mDatabase.child(groupName).child("longitude").setValue(longitude);
                mDatabase.child(groupName).child("latitude").setValue(latitude);
                mDatabase.child(groupName).child("radius").setValue(radius);
                mDatabase.child(groupName).child("openNow").setValue(openNow);
                mDatabase.child(groupName).child("location").setValue(location);
            }

            Intent intent = new Intent(this, MainActivity.class);
            Bundle extras = new Bundle();
            extras.putString("keyword",keyword);
            extras.putString("longitude", longitude);
            extras.putString("latitude", latitude);
            extras.putString("radius",radius);
            extras.putString("openNow", openNow);
            extras.putString("location", location);
            extras.putBoolean("groupMode", groupMode);
            extras.putString("groupName", groupName);
            intent.putExtras(extras);
            startActivity(intent);
        } else {
            //code here fetches the user input from the appropriate fields and converts it to a string
            String keyword = mKeyword.getText().toString();
            String location = mLocation.getText().toString();
            //String radius = mRadius.getText().toString();

            if (groupMode) {
                mDatabase.child(groupName).child("keyword").setValue(keyword);
                mDatabase.child(groupName).child("longitude").setValue(longitude);
                mDatabase.child(groupName).child("latitude").setValue(latitude);
                mDatabase.child(groupName).child("radius").setValue(radius);
                mDatabase.child(groupName).child("openNow").setValue(openNow);
                mDatabase.child(groupName).child("location").setValue(location);
            }

            //below code transfers the user inputed variables from this activity to the main activity.
            Intent intent = new Intent(this, MainActivity.class);
            Bundle extras = new Bundle();
            extras.putString("keyword",keyword);
            extras.putString("location",location);
            extras.putString("longitude", longitude);
            extras.putString("latitude", latitude);
            extras.putString("radius",radius);
            extras.putString("openNow", openNow);
            extras.putBoolean("groupMode", groupMode);
            extras.putString("groupName", groupName);
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
