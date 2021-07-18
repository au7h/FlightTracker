package com.nowinski.kamil.flighttracker.Services;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import com.nowinski.kamil.flighttracker.StaticScreenModeActivity;
import com.nowinski.kamil.flighttracker.Utils.AirCraftImageUrlParser;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class DownloadImageTask extends AsyncTask<String, Integer, Drawable> {
    private static final String API_URL = "https://www.flightradar24.com/clickhandler/?version=1.5&flight=";
    private static final String USER_AGENT = "Mozilla//5.0 (Windows NT 10.0; WOW64; rv:65.0) Gecko//20100101 Firefox//65.0";
    private DownloadImageResponse<Drawable> delegate;

    @Override
    protected Drawable doInBackground(String... params) {
        return downloadImage(params[0]);
    }

    private Drawable downloadImage(String aircraftId){
        String urlString = getUrl(aircraftId);
        URL url;
        BufferedOutputStream out;
        InputStream in;
        BufferedInputStream buf;

        if (urlString.isEmpty()){
            return null;
        }

        try {
            url = new URL(urlString);
            in = url.openStream();
            buf = new BufferedInputStream(in);
            Bitmap bMap = BitmapFactory.decodeStream(buf);
            if (in != null) {
                in.close();
            }
            if (buf != null) {
                buf.close();
            }
            return new BitmapDrawable(bMap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private String getUrl(String aircraftId){
        String baseUrl = API_URL + aircraftId;
        Connection.Response response;
        try {
            response = Jsoup.connect(baseUrl)
                    .ignoreContentType(true)
                    .method(Connection.Method.GET)
                    .userAgent(USER_AGENT)
                    .timeout(10000)
                    .execute();
            String json = response.parse().body().text();
            return AirCraftImageUrlParser.findAircraftImageUrl(json);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPostExecute(Drawable result) {
        delegate.downloadImageFinish(result);
    }

    public void setDelegate(DownloadImageResponse<Drawable> delegate){
        this.delegate = delegate;
    }
}
