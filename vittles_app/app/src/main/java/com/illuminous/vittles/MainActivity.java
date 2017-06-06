package com.illuminous.vittles;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.SearchResponse;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.R.attr.buttonStyleInset;
import static android.R.attr.y;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.media.CamcorderProfile.get;
import static android.text.TextUtils.concat;

public class MainActivity extends AppCompatActivity {
    YelpFusionApiFactory apiFactory;
    ImageView mimage;
    TextView mRestName;
    TextView mPrice;
    TextView mRating;
    TextView mDistance;
    TextView mLocation;
    TextView mWinner;
    Button mForkYeah;
    Button mEww;
    int businessIndex = 0;
    int[] votes;
    ArrayList<Business> businesses;
    ArrayList<Business> winners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRestName = (TextView) findViewById(R.id.rest_name);
        mPrice = (TextView) findViewById(R.id.rest_price);
        mRating = (TextView) findViewById(R.id.rest_rating);
        mimage = (ImageView) findViewById(R.id.main_image);
        mDistance = (TextView) findViewById(R.id.rest_distance);
        mLocation = (TextView) findViewById(R.id.rest_location);
        mWinner = (TextView) findViewById(R.id.rest_winner);
        mForkYeah = (Button) findViewById(R.id.button_fork_yeah);
        mEww = (Button) findViewById(R.id.button_eww);

            apiFactory = new YelpFusionApiFactory();
            try {
                Map<String, String> params = new HashMap<>();
                YelpFusionApi yelpFusionApi = apiFactory.createAPI("0x2eOuAzs_QARDOGy6UFpw", "lCQaEU3PrlJcCkWS3HS4QHREdRZSY9I6TleyVwFhwV3tf5kW154TSR3CYZSF2qVI");
                params.put("term", "Asian food");
                params.put("latitude", "36.999848");
                params.put("longitude", "-122.062926");
                Call<SearchResponse> call= yelpFusionApi.getBusinessSearch(params);
                SearchResponse searchResponse = call.execute().body();

                businesses = searchResponse.getBusinesses();
                winners = new ArrayList<>();
                votes = new int[businesses.size()];
                for (int i=0; i<votes.length; i++) {
                    votes[i] = 0;
                }

                Business business = businesses.get(businessIndex);
                new DownloadImageTask((ImageView) findViewById(R.id.main_image))
                        .execute(businesses.get(businessIndex).getImageUrl());
                mRestName.setText(business.getName());
                mLocation.setText(business.getLocation().getAddress1() + ", "  + business.getLocation().getCity() + ", " + business.getLocation().getState() + " " + business.getLocation().getZipCode());
                mRating.setText(String.valueOf(business.getRating()));
                mPrice.setText(business.getPrice());
                mDistance.setText(String.valueOf(round((business.getDistance()/1609.34) ,2)).concat(" miles"));


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onClick(View v) {
        startActivity(new Intent(this, MainActivity.class));
        finish();

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public void submitYes(View view) {
        winners.add(businesses.get(businessIndex));
        if (businessIndex != businesses.size()-1) {
            businessIndex++;
            display(businessIndex);
        } else {
            chooseWinner();
        }
    }

    public void submitNo(View view) {
        if (businessIndex != businesses.size()-1) {
            businessIndex++;
            display(businessIndex);
        } else {
            chooseWinner();
        }

    }

    public void display(int number) {

        Business business = businesses.get(number);
        new DownloadImageTask((ImageView) findViewById(R.id.main_image))
                .execute(businesses.get(number).getImageUrl());
        mRestName.setText(business.getName());
        mLocation.setText(business.getLocation().getAddress1() + ", "  + business.getLocation().getCity() + ", " + business.getLocation().getState() + " " + business.getLocation().getZipCode());
        mPrice.setText(business.getPrice());
        mRating.setText(String.valueOf(business.getRating()));
        mDistance.setText(String.valueOf(round((business.getDistance()/1609.34),2)).concat(" miles"));    //dived by 1609.34 to convert meters to miles
    }
    //round function from stack overflow
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void chooseWinner(){
        Random rand = new Random();
        int winnerIndex = rand.nextInt(winners.size());
        display(winnerIndex);
        mWinner.setVisibility(View.VISIBLE);
        mEww.setVisibility(View.INVISIBLE);
        mForkYeah.setVisibility(View.INVISIBLE);
    }
}
