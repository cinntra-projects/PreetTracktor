package com.preetTractor.galaxyAndroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.preetTractor.galaxyAndroid.R;
import com.preetTractor.galaxyAndroid.data.EmployeeValue;
import com.preetTractor.galaxyAndroid.helper.Globals;
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh;

import java.util.ArrayList;

public class BottomSheetAdapter extends RecyclerView.Adapter<BottomSheetAdapter.ViewHolder> {
    Context context;
    ArrayList<EmployeeValue> teamList;
    ItemListener itemListener;

 public BottomSheetAdapter(Context context, ArrayList<EmployeeValue> teamList, ItemListener itemListener)
      {
  this.context      = context;
  this.teamList     = teamList;
  this.itemListener = itemListener;
       }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View rootView = LayoutInflater.from(context).inflate(R.layout.database_list_item,parent,false);

    return new ViewHolder(rootView);
      }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
      {
  holder.title.setText(teamList.get(position).getFirstName()+" "+teamList.get(position).getLastName());
  if(teamList.get(position).getSalesEmployeeName().equalsIgnoreCase(PrefsByShubh.mPrefs.getString(Globals.EMP_NAME,""))){
      holder.check.setVisibility(View.VISIBLE);
  }else {
      holder.check.setVisibility(View.GONE);
  }
  if(teamList.get(position).getUserName().isEmpty()){
      holder.card_view.setVisibility(View.GONE);
  }
      }

    @Override
    public int getItemCount()
      {
    return teamList.size();
      }



    class ViewHolder extends RecyclerView.ViewHolder {
    private TextView title;
    private CardView card_view;
    private ImageView check;




    public ViewHolder(@NonNull View itemView) {
    super(itemView);
    title = itemView.findViewById(R.id.title);
     card_view = itemView.findViewById(R.id.card_view);
        check = itemView.findViewById(R.id.check);
    itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        itemListener.onClickAt(getAdapterPosition());
         }
       });
      }
    }

    public interface ItemListener { // create an interface
        void onClickAt(int position); // create callback function
    }
}
