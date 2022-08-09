package com.example.poolrdriver.models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PlaceApi {
    public ArrayList<String> Autocomplete(String input){
        ArrayList <String> arrayList=new ArrayList<>();
        HttpURLConnection connection=null;
        StringBuilder jsonResult=new StringBuilder();

        try {
            StringBuilder sb=new StringBuilder("https://maps.googleapis.com/maps/api/place/autocomplete/json?");
            sb.append("input="+input);
            sb.append("&key=AIzaSyAJnDBJM8t5SL3tzrSfucXgvYsIAcSZJJE");
            URL url =new URL(sb.toString());
            connection=(HttpURLConnection)url.openConnection();
            InputStreamReader inputStreamReader=new InputStreamReader(connection.getInputStream());

            int read;

            char[] buff=new char[1024];
            while ((read=inputStreamReader.read(buff))!=-1){

                jsonResult.append(buff,0,read);
            }

        }
        catch (MalformedURLException e){

            e.printStackTrace();
        }
        catch (IOException e){

            e.printStackTrace();
        }
        finally {
            connection.disconnect();
        }

        try {
            JSONObject jsonObject=new JSONObject(jsonResult.toString());
            JSONArray predictions=jsonObject.getJSONArray("predictions");

            for (int x=0; x<predictions.length();x++){
                arrayList.add(predictions.getJSONObject(x).getString("description"));

            }
        }
        catch (Exception e){

            e.printStackTrace();
        }
        return arrayList;
    }
}
