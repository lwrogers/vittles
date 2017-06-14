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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import static android.R.attr.radius;
import static android.R.attr.y;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.media.CamcorderProfile.get;
import static android.text.TextUtils.concat;
import static com.illuminous.vittles.R.id.rating_star;
import static com.illuminous.vittles.R.id.rest_winner;


 public class MainActivity extends AppCompatActivity {
    YelpFusionApiFactory apiFactory;    //yelp api github class
    ImageView mimage;   //declaration of the restaurant image view
     ImageView mRatingStar;
    TextView mRestName; //declaration of restaurant name text view
    TextView mPrice;    //restaurant price text view
    TextView mRating;   //restaurant rating text view
    TextView mDistance; //restaurant distance text view
    TextView mLocation; //restaurant location text view
    TextView mWinner;   //restaurant winner text view
    Button mForkYeah;   //button for fork yeah
    Button mEww;        // button for eww
    Button mTryAgain;   // button for tryagain
    Button mStartNewVote;     // button for starting a new vote
    Button mOtherWinners;
    ListView list;
    int businessIndex = 0;  //initialization of the index of the businesses array to be zero
    int winnerIndex;    //declaration of the index of the array of winners
    ArrayList<Business> businesses; // an array list declaration for the businesses array list
    ArrayList<Business> winners;    // an array list declaration for the winners array list
    ArrayList<Business> allWinners;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); //strict mode to allow the initial loading of the api to run on the main thread
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);     //sets the view in layout

        // all preceding assignments link the declaration to the actual view in the resource xml
        mRestName = (TextView) findViewById(R.id.rest_name);
        mPrice = (TextView) findViewById(R.id.rest_price);
        mRating = (TextView) findViewById(R.id.rest_rating);
        mimage = (ImageView) findViewById(R.id.main_image);
        mDistance = (TextView) findViewById(R.id.rest_distance);
        mLocation = (TextView) findViewById(R.id.rest_location);
        mWinner = (TextView) findViewById(rest_winner);
        mForkYeah = (Button) findViewById(R.id.button_fork_yeah);
        mEww = (Button) findViewById(R.id.button_eww);
        mTryAgain = (Button) findViewById(R.id.try_again);
        mStartNewVote = (Button) findViewById(R.id.start_new_vote);
        mOtherWinners = (Button) findViewById(R.id.other_winners);
        mRatingStar = (ImageView) findViewById(R.id.rating_star);
        list=(ListView)findViewById(R.id.list);





        // preceding code here transfers the user inputed keyword, location, and radius variables from our previous filter activity
        Bundle extras = getIntent().getExtras();
        String kword = extras.getString("keyword");
        String location = extras.getString("location");
        String openNow = extras.getString("openNow");
        String radius = extras.getString("radius");
        String radiusString = "";

        if (radius != null) {
            int radiusInt = ((Integer.parseInt(radius)) * 1610);    //parse radius string to an into and convert it from meters to miles
            radiusString = Integer.toString(radiusInt);     //once conversion is done we need to convert the radius back into a string because that's what the API wants for the call
        }

        apiFactory = new YelpFusionApiFactory();    // Used this github directory for easier implementation of yelp at https://github.com/ranga543/yelp-fusion-android.

        try {   //set up a try catch to catch IO exceptions
            YelpFusionApi yelpFusionApi = apiFactory.createAPI("0x2eOuAzs_QARDOGy6UFpw", "lCQaEU3PrlJcCkWS3HS4QHREdRZSY9I6TleyVwFhwV3tf5kW154TSR3CYZSF2qVI");  //accessed yelp api by entering our yelp ID and yelp secret

            Map<String, String> params = new HashMap<>();   //create a hashmap since the api takes in a hashmap of parameters
            params.put("term", kword);  //first parameter is a keyword which we transferred from the user input in filter activity
            params.put("radius", radiusString); //second is radius which we transferred from the user input in filter activity
            params.put("location", location);   //third is your location which we transferred from the user input in filter activity
            params.put("open_now", openNow);

            Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(params);    //here we prep the call
            SearchResponse searchResponse = call.execute().body();  // generate a search response by executing the call and returning the body

            businesses = searchResponse.getBusinesses();    //initialize businesses array list and have it get the businesses from the search response
            winners = new ArrayList<>();    //initialize winners to be an array list
            allWinners = new ArrayList<>();

            Business business = businesses.get(businessIndex);  //here we get our first business in the array
            new DownloadImageTask((ImageView) findViewById(R.id.main_image))    //download the image and set it in our image view
                    .execute(businesses.get(businessIndex).getImageUrl());

            new DownloadImageTask((ImageView) findViewById(R.id.main_background))    //download the image and set it in our image view
                    .execute(businesses.get(businessIndex).getImageUrl());

            //below we fetch all the data from the business and display it in the appropriate views in the layout
            mRestName.setText(business.getName());
            mLocation.setText(business.getLocation().getAddress1() + ", " + business.getLocation().getCity() + ", " + business.getLocation().getState() + " " + business.getLocation().getZipCode());
            mRating.setText(String.valueOf(business.getRating()));
            mPrice.setText(business.getPrice());
            mDistance.setText(String.valueOf(round((business.getDistance() / 1609.34), 2)).concat(" miles"));  //gets the distance and converts it to miles, then rounds it to two decimal places


        } catch (IOException e) {   //catch and print the stack trace for an IO exception
            e.printStackTrace();
        }

    }

    // this method is linked to the fork yeah button and adds the business to the winners array as well as advancing in the businesses array if it hasn't reached it's end. If it has we call choosewinner()
    public void submitYes(View view) {
        winners.add(businesses.get(businessIndex));
        allWinners.add(businesses.get(businessIndex));
        if (businessIndex != businesses.size() - 1) {
            businessIndex++;
            display(businessIndex, businesses);
        } else {

            chooseWinner();
        }
    }

    // this method is linked to the eww... button and advances in the businesses array as long as it hasn't reached the end. If it has it calls chooseWinner
    public void submitNo(View view) {
        if (businessIndex != businesses.size() - 1) {
            businessIndex++;
            display(businessIndex, businesses);
        } else {
            chooseWinner();
        }

    }
    // This method calls choosewinner() as long as the size is greater than 1 and chooses another random winner from the array.
    public void tryAgain(View view) {
        if (winners.size() > 1) {
            winners.remove(winnerIndex);
        }
        chooseWinner();
    }

    public void otherWinners(View view){
        Log.v("button", "button was pressed");
        Log.v("All Winners: ", String.valueOf(allWinners.size()));




        displayAllWinners();
    }

    // This method simply starts a new intent to the filter activity in order to reset the vote and start over
    public void startNewVote(View view) {
        Intent intent = new Intent(this, FilterActivity.class);
        startActivity(intent);
    }

    public  void displayAllWinners(){

        final ArrayList<String> stores = new ArrayList<>();
        final ArrayList<String> storesURL = new ArrayList<>();
        final ArrayList<String> address = new ArrayList<>();

        for(int i=0; i<allWinners.size(); i++){
            stores.add(allWinners.get(i).getName());
            storesURL.add(allWinners.get(i).getImageUrl());
            address.add((allWinners.get(i).getLocation().getAddress1() + ", " + allWinners.get(i).getLocation().getCity()
                    + ", " + allWinners.get(i).getLocation().getState() + " " + allWinners.get(i).getLocation().getZipCode()));
            Log.v("IS THIS CORRECT?: ", storesURL.get(i));
        }

        CustomListAdapter adapter = new CustomListAdapter(this, stores, storesURL, address);

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                if(stores !=null) {
                    String Slecteditem = stores.get(+position);
                    Toast.makeText(getApplicationContext(), Slecteditem, Toast.LENGTH_SHORT).show();
                }

            }
        });

        list.setVisibility(View.VISIBLE);
        mRatingStar.setVisibility(View.GONE);
        mRestName.setVisibility(View.GONE);
        mTryAgain.setVisibility(View.GONE);
        mOtherWinners.setVisibility(View.GONE);
        mPrice.setVisibility(View.GONE);
        mRating.setVisibility(View.GONE);
        mimage.setVisibility(View.GONE);
        mDistance.setVisibility(View.GONE);
        mLocation.setVisibility(View.GONE);
        mWinner.setVisibility(View.GONE);

    }
    //this method is all about updating the screen by displaying the next restaurant and all of it's data.
    public void display(int number, final ArrayList<Business> chosenArray) {

        final Business business = chosenArray.get(number);
        new DownloadImageTask((ImageView) findViewById(R.id.main_image))
                .execute(business.getImageUrl());

        new DownloadImageTask((ImageView) findViewById(R.id.main_background))
                .execute(business.getImageUrl());

        ImageView img = (ImageView) findViewById(R.id.main_image);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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
        mDistance.setText(String.valueOf(round((business.getDistance() / 1609.34), 2)).concat(" miles"));    //divide by 1609.34 to convert meters to miles
    }

    //This method chooses a winner randomly from the list of winners that the person accumulated by answering "fork yeah".
    public void chooseWinner() {
        if (winners.size() < 1) {
            startActivity(new Intent(this, FilterActivity.class));
            finish();
        } else {
            Random rand = new Random();
            winnerIndex = rand.nextInt(winners.size());
            display(winnerIndex, winners);
            mWinner.setVisibility(View.VISIBLE);
            mOtherWinners.setVisibility(View.VISIBLE);
            mEww.setVisibility(View.GONE);
            mForkYeah.setVisibility(View.GONE);
            mTryAgain.setVisibility(View.VISIBLE);
            mStartNewVote.setVisibility(View.VISIBLE);
        }
    }

    //round function from stack overflow
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    // This class downloads the image into our app from the yelp image URL. Found this function on Stack OverFlow.
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
