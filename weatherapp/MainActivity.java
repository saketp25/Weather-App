package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView resultTextView;

    public void onClick(View view){

        DownloadTask task = new DownloadTask();

        if(editText.getText().toString().isEmpty())
        {
            Toast.makeText(this,"Please enter a valid City name",Toast.LENGTH_SHORT).show();
        }
        else{

            ///Log.i("enterd city ",editText.getText().toString());
            try
            {
                String encodedCityName = URLEncoder.encode(editText.getText().toString()," UTF-8 ");
                task.execute("https://api.openweathermap.org/data/2.5/weather?q="+editText.getText().toString()+"&appid=26e022dfc951ca28daddfe53f5b4af52").get();

            } catch (Exception e)
            {
                e.printStackTrace();
            }

            InputMethodManager mgr =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editText.getWindowToken(),0);

        }

    }

    public class DownloadTask extends AsyncTask<String , Void ,String>{

        @Override
        protected String doInBackground(String... urls) {

            String result="";
            URL url;
            try {

                url = new URL(urls[0]);
                HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlconnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while(data!=-1){

                    char current =(char) data;
                    result += current;

                    data = reader.read();
                }
                return result;

            }catch (Exception e){
                e.printStackTrace();
                return  "ERROR";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {

///                Log.i("weather info ",s);
                String message = "";

                JSONObject jsonObject = new JSONObject(s);

                String tempINfo = jsonObject.getString("main");
                JSONObject jsonObject1 = new JSONObject(tempINfo);
                String temp = jsonObject1.getString("temp");

                float f =  Float.parseFloat(temp);
                f = f-273;
                String temp2 =String.format("%.2f",f);
                message += "Temperature :" + temp2 ;

                String weatherInfo = jsonObject.getString("weather");
                JSONArray jsonArray = new JSONArray(weatherInfo);

                for(int i=0;i<jsonArray.length();i++){

                        JSONObject jsonPart = jsonArray.getJSONObject(i);

                        Log.i("main",jsonPart.getString("main"));
                        Log.i("Description",jsonPart.getString("description"));
                        ///resultTextView.setText("Main : "+jsonPart.getString("main")+"\n"+"Description : "+jsonPart.getString("description"));
                        message += "\n" + "Main : " + jsonPart.getString("main");
                        message += "\n" + "Description : " + jsonPart.getString("description");
                }
                resultTextView.setText(message);

            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not able to find weather information ", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        resultTextView = findViewById(R.id.resultTextView);

        ImageView imageView =(ImageView) findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.imageforweatherapp);

    }
}