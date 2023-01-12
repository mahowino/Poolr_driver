package com.example.poolrdriver.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.poolrdriver.classes.models.PlaceApi;

import java.util.ArrayList;

public class AutoSuggestionsAdapter extends ArrayAdapter implements Filterable {

    ArrayList<String> result;
    int resource;
    Context context;

    PlaceApi placeApi=new PlaceApi();

    public AutoSuggestionsAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.resource = resource;
        this.context=context;
    }

    @Override
    public int getCount() {
        return result.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return result.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
      Filter filter=new Filter() {
          @Override
          protected FilterResults performFiltering(CharSequence charSequence) {
              FilterResults filterResults=new FilterResults();
              if (charSequence!=null){
                // Toast.makeText(getContext(),"dsfdsf",Toast.LENGTH_SHORT).show();
                  result=placeApi.Autocomplete(charSequence.toString());
                  filterResults.values=result;
                  filterResults.count=result.size();

              }
              return filterResults;
          }

          @Override
          protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

              if (result!=null && filterResults.count>0){

                  notifyDataSetChanged();
              }
              else {
                  notifyDataSetInvalidated();
              }
          }
      };
      return filter;
    }
}
