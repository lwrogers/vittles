package com.illuminous.vittles;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String>   bName;
    private final ArrayList<String> imgUrl;
    private final ArrayList<String> address;

    public CustomListAdapter(Activity context, ArrayList<String> bName, ArrayList<String> imgUrl,
                             ArrayList<String> address){
        super(context, R.layout.mylist, bName);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.bName=bName;
        this.imgUrl=imgUrl;
        this.address = address;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView addressLine = (TextView) rowView.findViewById(R.id.textView1);

        new DownloadImageTask((ImageView) rowView.findViewById(R.id.icon))
                .execute(imgUrl.get(position));




        txtTitle.setText(bName.get(position));
        addressLine.setText(address.get(position));
        return rowView;

    };

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