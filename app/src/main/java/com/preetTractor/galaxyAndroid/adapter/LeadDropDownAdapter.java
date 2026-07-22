package com.preetTractor.galaxyAndroid.adapter;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.preetTractor.galaxyAndroid.R;
import com.preetTractor.galaxyAndroid.data.LeadTypeData;

import java.util.ArrayList;

public class LeadDropDownAdapter extends ArrayAdapter<LeadTypeData> {

    private ArrayList<LeadTypeData> originalList;
    private ArrayList<LeadTypeData> filteredList;

    public LeadDropDownAdapter(
            Context context,
            int resourceId,
            ArrayList<LeadTypeData> items
    ) {
        super(context, resourceId, items);

        this.originalList = new ArrayList<>(items);
        this.filteredList = items;
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Nullable
    @Override
    public LeadTypeData getItem(int position) {
        return filteredList.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {

        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();

                if (constraint == null || constraint.length() == 0) {

                    results.values = originalList;
                    results.count = originalList.size();

                } else {

                    ArrayList<LeadTypeData> tempList = new ArrayList<>();

                    String search =
                            constraint.toString().toLowerCase().trim();

                    for (LeadTypeData item : originalList) {

                        if (item.getName()
                                .toLowerCase()
                                .contains(search)) {

                            tempList.add(item);
                        }
                    }

                    results.values = tempList;
                    results.count = tempList.size();
                }

                return results;
            }

            @Override
            protected void publishResults(
                    CharSequence constraint,
                    FilterResults results
            ) {

                filteredList =
                        (ArrayList<LeadTypeData>) results.values;

                clear();
                addAll(filteredList);
                notifyDataSetChanged();
            }
        };
    }
}


