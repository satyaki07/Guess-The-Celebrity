package com.satyaki.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebUrls = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();

    int choosenCeleb = 0;
    int locationOfCorrectAns = 0;
    int incorrectAnsLocation = 0;
    String[] Answers = new String[4];

    ImageView imageView;

    Button button0;
    Button button1;
    Button button2;
    Button button3;

    public void celebChoosen(View view) {

        if (view.getTag().toString().equals(Integer.toString(locationOfCorrectAns)))
            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();
        else {

            Toast.makeText(getApplicationContext(), "Wrong! It was " + celebNames.get(choosenCeleb), Toast.LENGTH_SHORT).show();

        }
        createNewQuestion();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);

        button0 = (Button) findViewById(R.id.button1);
        button1 = (Button) findViewById(R.id.button2);
        button2 = (Button) findViewById(R.id.button3);
        button3 = (Button) findViewById(R.id.button4);

        DownloadTask task = new DownloadTask();
        String result = null;

        try {

            result = task.execute("http://www.posh24.se/kandisar").get();

            String[] splitResult = result.split("<div class=\"sidebarContainer\">");

            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while (m.find()) {

                celebUrls.add(m.group(1));
            }

            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);

            while (m.find()) {

                celebNames.add(m.group(1));
            }


            createNewQuestion();

        } catch (InterruptedException e) {

            e.printStackTrace();

        } catch (ExecutionException e) {

            e.printStackTrace();

        }

    }

    public void createNewQuestion() {

        Random rand = new Random();
        choosenCeleb = rand.nextInt(celebUrls.size());

        Log.i("ChoosenCeleb",Integer.toString(choosenCeleb));

        ImageDownloader imageTask = new ImageDownloader();
        Bitmap celebImage;

        try {
            celebImage = imageTask.execute(celebUrls.get(choosenCeleb)).get();

            imageView.setImageBitmap(celebImage);

            locationOfCorrectAns = rand.nextInt(4);
            Log.i("Correct Answer",Integer.toString(locationOfCorrectAns));


            for (int i = 0; i < 4; i++) {

                if (i == locationOfCorrectAns)
                    Answers[i] = celebNames.get(choosenCeleb);
                else {
                    incorrectAnsLocation = rand.nextInt(celebUrls.size());

                    while (incorrectAnsLocation == choosenCeleb)
                        incorrectAnsLocation = rand.nextInt(celebUrls.size());

                    Answers[i] = celebNames.get(incorrectAnsLocation);
                }
            }

            button0.setText(Answers[0]);
            button1.setText(Answers[1]);
            button2.setText(Answers[2]);
            button3.setText(Answers[3]);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... urls) {

            try {

                URL url = new URL(urls[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream inputStream = connection.getInputStream();

                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);

                return myBitmap;

            } catch (MalformedURLException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();

            }

            return null;
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {

                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;

            } catch (MalformedURLException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();
            }
            return null;
        }
    }
}
