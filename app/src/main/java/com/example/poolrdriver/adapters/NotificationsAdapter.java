package com.example.poolrdriver.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.poolrdriver.R;
import com.example.poolrdriver.classes.Notifications;

import java.util.List;


public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.HolderView> {
    private Context mContext;
    private List<Notifications> notifications;

    public NotificationsAdapter(Context mContext, List<Notifications> notifications) {

        this.mContext=mContext;
        this.notifications=notifications;
    }


    @NonNull
    @Override
    public HolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(mContext);
        View view=inflater.inflate(R.layout.notification_card,parent,false);
        return new NotificationsAdapter.HolderView(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HolderView holder, int position) {
       // holder.message.setText(notifications.get(position).getMessage());
        holder.message.setText("Mahalon Requested a carpool from you");

        //set image of user setting the message
        //if (notifications.get(position).getType()!=2)
       Glide.with(holder.itemView).load(notifications.get(position).getImageUri()).into(holder.notificationImage);

    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class HolderView extends RecyclerView.ViewHolder {
        TextView message;
        ImageView notificationImage;
        public HolderView(@NonNull View itemView) {
            super(itemView);
            message=itemView.findViewById(R.id.notifications_message);
            notificationImage=itemView.findViewById(R.id.notification_image);
        }
    }
}
