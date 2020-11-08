package com.example.dropdart.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dropdart.R;
import com.example.dropdart.models.ModelChats;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AdapterChats extends RecyclerView.Adapter<AdapterChats.MyViewHolder> {
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    Context context;
    List<ModelChats> chatsList;
    String imageUri;
    FirebaseUser user;

    public AdapterChats(Context context, List<ModelChats> chatsList, String imageUri) {
        this.context = context;
        this.chatsList = chatsList;
        this.imageUri = imageUri;

    }

    @NonNull
    @Override
    public AdapterChats.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.user_chat_right, parent, false);
            return new MyViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.user_chat_left, parent, false);
            return new MyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterChats.MyViewHolder holder, final int position) {
        String message = chatsList.get(position).getMessage();
        String timestamp = chatsList.get(position).getTimestamp();

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(timestamp));

        Date date = new Date();
        String stringDate = DateFormat.getTimeInstance().format(date);
        holder.messageTv.setText(message);
        if (stringDate != null){
            holder.timeTv.setText(stringDate);
        }

        try{
            Picasso.get().load(imageUri).placeholder(R.drawable.ic_default_person_white).into(holder.profileIv);
        }
        catch (Exception e) {

        }

        holder.messageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure you want to delete this message");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteMessage(position);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }
        });

        if (position == chatsList.size()-1 ){
            if (chatsList.get(position).isSeen()){
                holder.isSeenTv.setText("Seen");
            }
            else{
                holder.isSeenTv.setText("Delivered");
            }
        }
        else{
            holder.isSeenTv.setVisibility(View.GONE);
        }


    }

    private void deleteMessage(int position) {
        final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        String msgTimeStamp = chatsList.get(position).getTimestamp();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        final Query query = dbRef.orderByChild("timestamp").equalTo(msgTimeStamp);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    if (ds.child("sender").getValue().equals(myUid)){
                        ds.getRef().removeValue();

                        //HashMap<String, Object> hashMap = new HashMap<>();
                        //hashMap.put("message", "This message was deleted...");
                        //ds.getRef().updateChildren(hashMap);

                        Toast.makeText(context, "message deleted", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(context, "you can delete only your messages", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return chatsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (chatsList.get(position).getSender().equals(user.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView profileIv;
        TextView messageTv, timeTv, isSeenTv;
        LinearLayout messageLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profileIv = itemView.findViewById(R.id.profileIv);
            messageTv = itemView.findViewById(R.id.messsageTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            isSeenTv = itemView.findViewById(R.id.seenTv);
            messageLayout = itemView.findViewById(R.id.messageLayout);
        }
    }
}
