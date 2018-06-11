package com.illuminous.vittles;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static android.R.attr.button;

public class ModeSelectionActivity extends AppCompatActivity {

    Button mGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_selection);

        mGroup = (Button) findViewById(R.id.button_group);

    }

    public void lonerSelection(View view) {
        Intent intent = new Intent(this, FilterActivity.class);
        Bundle extras = new Bundle();
        extras.putBoolean("groupMode",false);
        extras.putString("groupName", "");
        intent.putExtras(extras);
        startActivity(intent);
    }

    public void groupSelection(View view) {
        Intent intent = new Intent(this, GroupActivity.class);
        startActivity(intent);
    }

}
