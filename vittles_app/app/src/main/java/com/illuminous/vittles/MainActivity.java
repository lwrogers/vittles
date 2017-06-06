package com.illuminous.vittles;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
import java.util.logging.Filter;

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
    Button mTryAgain;
    Button mStartNewVote;
    int businessIndex = 0;
    int winnerIndex;
    ArrayList<Business> businesses;
    ArrayList<Business> winners;




    //round function from stack overflow
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

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
        mTryAgain = (Button) findViewById(R.id.try_again);
        mStartNewVote = (Button) findViewById(R.id.start_new_vote);

        Bundle extras = getIntent().getExtras();
        String kword = extras.getString("keyword");
        String location = extras.getString("location");
        String radius = extras.getString("radius");
        String radiusString = "";
        if (radius != null) {
            int radiusInt = ((Integer.parseInt(radius))*1610);
            radiusString = Integer.toString(radiusInt);
        }

        apiFactory = new YelpFusionApiFactory();
        try {
            Map<String, String> params = new HashMap<>();
            YelpFusionApi yelpFusionApi = apiFactory.createAPI("0x2eOuAzs_QARDOGy6UFpw", "lCQaEU3PrlJcCkWS3HS4QHREdRZSY9I6TleyVwFhwV3tf5kW154TSR3CYZSF2qVI");
            params.put("term", kword);
            params.put("radius", radiusString);
            params.put("location", location);
           // params.put("latitude", "36.999848");
           // params.put("longitude", "-122.062926");
            Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(params);
            SearchResponse searchResponse = call.execute().body();

            businesses = searchResponse.getBusinesses();
            winners = new ArrayList<>();


            Business business = businesses.get(businessIndex);
            new DownloadImageTask((ImageView) findViewById(R.id.main_image))
                    .execute(businesses.get(businessIndex).getImageUrl());
            mRestName.setText(business.getName());
            mLocation.setText(business.getLocation().getAddress1() + ", " + business.getLocation().getCity() + ", " + business.getLocation().getState() + " " + business.getLocation().getZipCode());
            mRating.setText(String.valueOf(business.getRating()));
            mPrice.setText(business.getPrice());
            mDistance.setText(String.valueOf(round((business.getDistance() / 1609.34), 2)).concat(" miles"));


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onClick(View v) {
        startActivity(new Intent(this, MainActivity.class));
        finish();

    }

    public void submitYes(View view) {
        winners.add(businesses.get(businessIndex));
        if (businessIndex != businesses.size() - 1) {
            businessIndex++;
            display(businessIndex, businesses);
        } else {
            chooseWinner();
        }
    }

    public void submitNo(View view) {
        if (businessIndex != businesses.size() - 1) {
            businessIndex++;
            display(businessIndex, businesses);
        } else {
            chooseWinner();
        }

    }

    public void tryAgain(View view) {
        if (winners.size()>1) {
            winners.remove(winnerIndex);
        }
        chooseWinner();
    }

    public void startNewVote(View view) {
        Intent intent = new Intent(this, FilterActivity.class);
        startActivity(intent);
    }

    public void display(int number, final ArrayList<Business> chosenArray) {

        final Business business = chosenArray.get(number);
        new DownloadImageTask((ImageView) findViewById(R.id.main_image))
                .execute(chosenArray.get(number).getImageUrl());

        ImageView img = (ImageView)findViewById(R.id.main_image);
        img.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(business.getUrl()));
                startActivity(intent);
            }
        });

        mRestName.setText(business.getName());
        mLocation.setText(business.getLocation().getAddress1() + ", " + business.getLocation().getCity() + ", " + business.getLocation().getState() + " " + business.getLocation().getZipCode());
        mPrice.setText(business.getPrice());
        mRating.setText(String.valueOf(business.getRating()));
        mDistance.setText(String.valueOf(round((business.getDistance() / 1609.34), 2)).concat(" miles"));    //dived by 1609.34 to convert meters to miles
    }

    public void chooseWinner() {
        if (winners.size()<1){
            startActivity(new Intent(this, FilterActivity.class));
            finish();
        } else {
            Random rand = new Random();
            winnerIndex = rand.nextInt(winners.size());
            display(winnerIndex, winners);
            mWinner.setVisibility(View.VISIBLE);
            mEww.setVisibility(View.GONE);
            mForkYeah.setVisibility(View.GONE);
            mTryAgain.setVisibility(View.VISIBLE);
            mStartNewVote.setVisibility(View.VISIBLE);
        }

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
}
