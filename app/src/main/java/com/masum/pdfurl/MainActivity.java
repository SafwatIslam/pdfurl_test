package com.masum.pdfurl;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.IntStream;

public class MainActivity extends AppCompatActivity {

    ProgressBar progress;

    PDFView pdfView;

    public static String assetname = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        pdfView = findViewById(R.id.pdfView);
        progress = findViewById(R.id.progress);

        progress.setVisibility(View.VISIBLE);
        pdfView.setVisibility(View.INVISIBLE);

        new loadPDFurl().execute(assetname);


    }
    public class loadPDFurl extends AsyncTask<String,Void, InputStream>{


        @Override
        protected InputStream doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                        return new BufferedInputStream(httpURLConnection.getInputStream());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            super.onPostExecute(inputStream);
            if (inputStream != null){
                pdfView.fromStream(inputStream)
                        .defaultPage(6)
                        .scrollHandle(new DefaultScrollHandle(MainActivity.this))
                        .onLoad(new OnLoadCompleteListener() {
                            @Override
                            public void loadComplete(int nbPages) {
                                progress.setVisibility(View.INVISIBLE);
                                pdfView.setVisibility(View.VISIBLE);

                            }
                        })
                        .load();
            }else {
                Toast.makeText(MainActivity.this, "pdf load failde", Toast.LENGTH_SHORT).show();
            }
        }
    }
}