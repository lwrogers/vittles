package com.illuminous.vittles;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class GroupActivity extends AppCompatActivity {

    EditText mEditGroup;
    private DatabaseReference mDatabase;
    String groupName;
    String keyword;
    String location;
    String openNow;
    String radius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        mEditGroup = (EditText) findViewById(R.id.edit_find_group);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void searchGroup(View view) {
        groupName = mEditGroup.getText().toString();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(groupName)) {
                    Log.v("key",mDatabase.child(groupName).getKey());
                    keyword = snapshot.child(groupName).child("keyword").getValue().toString();
                    Log.v("KORD YES",keyword);
                    location = snapshot.child(groupName).child("location").getValue().toString();
                    openNow = snapshot.child(groupName).child("openNow").getValue().toString();
                    radius = snapshot.child(groupName).child("radius").getValue().toString();
                    groupFound();
                } else {
                    mDatabase.child(groupName).setValue(groupName);
                    createGroup();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //finish();
//        if (mDatabase.) {
//            Toast.makeText(this, "Group not found. Creating Group.", Toast.LENGTH_SHORT).show();
//
//        } else {
//            Toast.makeText(this, "Group found!", Toast.LENGTH_SHORT).show();
//            Log.v("key",mDatabase.child(groupName).getKey());
//        }
    }

    public void groupFound() {
        Toast.makeText(this, "Group found!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        Bundle extras = new Bundle();
        extras.putString("groupName", groupName);
        extras.putBoolean("groupMode", true);
        extras.putString("keyword",keyword);
        extras.putString("location",location);
        extras.putString("radius",radius);
        extras.putString("openNow", openNow);
        intent.putExtras(extras);
        startActivity(intent);
    }

    public void createGroup() {
        Toast.makeText(this, "Group not found. Creating Group.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, FilterActivity.class);
        Bundle extras = new Bundle();
        extras.putString("groupName",groupName);
        extras.putBoolean("groupMode",true);
        intent.putExtras(extras);
        startActivity(intent);
    }
}
