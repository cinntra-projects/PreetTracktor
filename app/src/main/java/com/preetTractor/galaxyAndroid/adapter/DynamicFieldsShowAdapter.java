package com.preetTractor.galaxyAndroid.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.preetTractor.galaxyAndroid.R;

import java.util.List;
import java.util.Map;

public class DynamicFieldsShowAdapter extends RecyclerView.Adapter<DynamicFieldsShowAdapter.KeyValueViewHolder> {

    private List<Map.Entry<String, String>> keyValueList;

    private final float keySize;   // Use `float` as text sizes are typically defined in `float`
    private final float valueSize;
    private final int keySizeHeight;

    public DynamicFieldsShowAdapter(List<Map.Entry<String, String>> keyValueList, float keySize, float valueSize, int keySizeHeight) {
        this.keyValueList = keyValueList;
        this.keySize = keySize;
        this.valueSize = valueSize;
        this.keySizeHeight = keySizeHeight;
    }

    @Override
    public KeyValueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dynamic_fields_show_layout, parent, false);
        return new KeyValueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeyValueViewHolder holder, int position) {
        Map.Entry<String, String> keyValuePair = keyValueList.get(position);

        Log.d("Dynamic-keys", "onBindViewHolder: "+keyValuePair.getKey()+" : "+keyValuePair.getValue());
        holder.keyTextView.setText(keyValuePair.getKey());
        if(keyValuePair.getValue().isEmpty() || keyValuePair.getValue().equals("null")){
            holder.valueTextView.setText("NA");
        }
        else{
            holder.valueTextView.setText(keyValuePair.getValue());
        }

        holder.keyTextView.setTextSize(keySize);
        holder.valueTextView.setTextSize(valueSize);

        // Set the height of keyTextView to 45 dp dynamically
        int heightInPixels = dpToPx(keySizeHeight, holder.keyTextView.getContext());
        ViewGroup.LayoutParams params = holder.keyTextView.getLayoutParams();
        params.height = heightInPixels; // Set height to 45 dp
        holder.keyTextView.setLayoutParams(params);

    }

    @Override
    public int getItemCount() {
        return keyValueList.size();
    }

    public static class KeyValueViewHolder extends RecyclerView.ViewHolder {
        TextView keyTextView;
        TextView valueTextView;

        public KeyValueViewHolder(View itemView) {
            super(itemView);
            keyTextView = itemView.findViewById(R.id.tvDynamic_key_name);
            valueTextView = itemView.findViewById(R.id.tvDynamic_key_value);
        }
    }

    /**
     * Utility method to convert dp to pixels based on screen density.
     */
    private int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}