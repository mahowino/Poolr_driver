package com.example.poolrdriver.adapters;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poolrdriver.Interfaces.ItemClickListener;
import com.example.poolrdriver.R;
import com.example.poolrdriver.models.CarTypes;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class CarTypeAdapter extends RecyclerView.Adapter<CarTypeAdapter.HolderView> {
    Context mContext;
    List<CarTypes> carTypes;
    private ItemClickListener onItemClickListener;

    public CarTypeAdapter(Context mContext, List<CarTypes> carTypes,ItemClickListener listener) {
        this.mContext = mContext;
        this.carTypes = carTypes;
        onItemClickListener=listener;
    }

    @NonNull
    @Override
    public HolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HolderView(LayoutInflater.from(mContext).inflate(R.layout.car_type_layout_card,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull HolderView holder, int position) {
        CarTypes carType=carTypes.get(position);

        StorageReference reference= FirebaseStorage.getInstance().getReference()
                .child("cars")
                .child(carType.getCarType()+".png");
        reference.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Log.d(TAG, "onSuccess: "+uri);
                    Picasso.with(mContext)
                      .load(uri)
                            .error(R.drawable.car_logo_placeholder)
                       .into(holder.imageView);
                })
                .addOnFailureListener(e -> Log.e(TAG, "onFailure: ",e.getCause()));


        if(carType.getNumberplate()==null)
        holder.textView.setText(carType.getCarType());
        else
            holder.textView.setText(carType.getCarType() +"  " +carType.getNumberplate().toUpperCase());

        holder.card.setOnClickListener(v -> onItemClickListener.onItemClick(position));

    }

    @Override
    public int getItemCount() {
        return carTypes.size();
    }

    public class HolderView extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        CardView card;
        public HolderView(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imgCarTypePicture);
            textView=itemView.findViewById(R.id.txtCarTypeName);
            card=itemView.findViewById(R.id.carTypeCard);
        }
    }
}
