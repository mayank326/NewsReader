package com.example.mayank.newsreader;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> array;
    public static String articleURL;
    static String arrayUrl[];
    static int value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView lv1=(ListView)findViewById(R.id.listView1);
         array=new ArrayList<String>();
        ArrayAdapter adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,array);
        lv1.setAdapter(adapter);

        DownloadTask task=new DownloadTask();
        try {
            task.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i1=new Intent("com.example.mayank.newsreader.Main2Activity");
                    value=position;
                 startActivity(i1);

            }
        });
    }

    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... params) {

            String result="";
            URL url;
            HttpURLConnection httpURLConnection=null;

            try {
                url = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();

                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {
                    result += (char) data;
                    data = reader.read();

                }


                JSONArray jsonArray=new JSONArray(result);
                int number=20;
                if(number<20)
                {
                    number=jsonArray.length();
                }

                arrayUrl= new String[number];

                for(int i=0;i<number;i++)
                {

                    String articleId=jsonArray.getString(i);

                    url=new URL("https://hacker-news.firebaseio.com/v0/item/"+articleId +".json?print=pretty");
                    httpURLConnection= (HttpURLConnection)url.openConnection();
                    in=httpURLConnection.getInputStream();
                    reader=new InputStreamReader(in);

                    String articleInfo="";
                    int current = reader.read();

                    while (current != -1) {
                        articleInfo += (char) current;
                        current = reader.read();

                    }


                    JSONObject jsonObject=new JSONObject(articleInfo);

                    if(!jsonObject.isNull("title") &&!jsonObject.isNull("url")) {

                        String articleTitle = jsonObject.getString("title");
                         articleURL = jsonObject.getString("url");
                         arrayUrl[i]=articleURL;
                       Log.i("The Url : ",arrayUrl[i]);
                       array.add(articleTitle);


                    }


                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return  null;

        }
    }
}
