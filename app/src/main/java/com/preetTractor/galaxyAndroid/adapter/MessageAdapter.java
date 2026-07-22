package com.preetTractor.galaxyAndroid.adapter;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.preetTractor.galaxyAndroid.R;
import com.preetTractor.galaxyAndroid.data.ChatModel;
import com.preetTractor.galaxyAndroid.helper.Globals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    Context context;
    ArrayList<ChatModel> messagelist;

    public MessageAdapter(Context context, ArrayList<ChatModel> messagelist) {
        this.context = context;
        this.messagelist = messagelist;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.chatter_layout_new, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatModel chatModel = messagelist.get(position);


        holder.message.setText(chatModel.getMessage());
        //  if(chatModel.getSourceType().equalsIgnoreCase("Followup"))

        String date = chatModel.getUpdateDate();
        String convertedDate = convertDateFormat(date); // convert date format YYYY-MM-DD TO DD-MM-YYYY

        if (chatModel.getComm_mode().trim().equalsIgnoreCase("Call")) {
            holder.title.setText("Call - " + convertedDate+ ", " + Globals.convertTimeInHHMMSSA(chatModel.getUpdateTime()));
            holder.priority_dot.setImageResource(R.drawable.phone_call);
        } else if (chatModel.getComm_mode().trim().equalsIgnoreCase("Whatsapp")) {
            holder.title.setText("Whatsapp - " + convertedDate+ ", " + Globals.convertTimeInHHMMSSA(chatModel.getUpdateTime()));
            holder.priority_dot.setImageResource(R.drawable.ic_whatsapp);
        } else if (chatModel.getComm_mode().trim().equalsIgnoreCase("SMS")) {
            holder.title.setText("SMS - " + convertedDate+ ", " + Globals.convertTimeInHHMMSSA(chatModel.getUpdateTime()));
            holder.priority_dot.setImageResource(R.drawable.ic_message);
        } else if (chatModel.getComm_mode().trim().equalsIgnoreCase("Email")) {
            holder.title.setText("Email - " + convertedDate+ ", " + Globals.convertTimeInHHMMSSA(chatModel.getUpdateTime()));
            holder.priority_dot.setImageResource(R.drawable.ic_email_new);
        } else if (chatModel.getComm_mode().trim().equalsIgnoreCase("Visit")) {
            holder.title.setText("Visit - " + convertedDate+ ", " + Globals.convertTimeInHHMMSSA(chatModel.getUpdateTime()));
            holder.priority_dot.setImageResource(R.drawable.ic_blue_dot);
        }
//        else {
//            holder.title.setText("SMS - " + convertedDate+ "," + Globals.convertTimeInHHMMSSA(chatModel.getUpdateTime()));
//            holder.priority_dot.setImageResource(R.drawable.ic_sms);
//        }

        holder.receivertext.setText(chatModel.getMessage());
        holder.receiverdatetime.setText(convertedDate + "," + Globals.convertTimeInHHMMSSA(chatModel.getUpdateTime()));

        if (!chatModel.getEmpName().isEmpty()){
            holder.textimage.setText("" + chatModel.getEmpName().trim().charAt(0));
        }


        //holder.timelineView.setTooltipText(""+chatModel.getEmpName().trim().charAt(0));
        if (chatModel.getEmpId().equalsIgnoreCase(Globals.empCode)) {
            holder.receiver_view.setGravity(Gravity.RIGHT);

            holder.textimage.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            holder.messagelayout.getBackground().setColorFilter(Color.parseColor("#956387DA"), PorterDuff.Mode.SRC_ATOP);
        } else {
            holder.receiver_view.setGravity(Gravity.LEFT);
            holder.textimage.setTextColor(context.getResources().getColor(R.color.green_approved));
            holder.messagelayout.getBackground().setColorFilter(Color.parseColor("#9455BD63"), PorterDuff.Mode.SRC_ATOP);

        }
    }

    @Override
    public int getItemCount() {
        return messagelist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView receivertext, receiverdatetime, textimage, message, title;
        LinearLayout imageicon;
        LinearLayout receiver_view, messagelayout;
        ImageView priority_dot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageicon = itemView.findViewById(R.id.imageicon);
            receivertext = itemView.findViewById(R.id.receivertext);
            receiverdatetime = itemView.findViewById(R.id.receiverdatetime);
            receiver_view = itemView.findViewById(R.id.receiver_view);
            messagelayout = itemView.findViewById(R.id.messagelayout);
            textimage = itemView.findViewById(R.id.textimage);
            priority_dot = itemView.findViewById(R.id.priority_dot);
            message = itemView.findViewById(R.id.message);
            title = itemView.findViewById(R.id.title);


        }
    }

    public static String convertDateFormat(String dateStr) {
        // Define the input and output date formats
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        String formattedDate = "";
        try {
            Date date = inputFormat.parse(dateStr);
            formattedDate = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return formattedDate;
    }
}
