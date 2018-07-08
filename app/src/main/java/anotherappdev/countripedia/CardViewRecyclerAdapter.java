package anotherappdev.countripedia;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Aditya on 04-06-2018.
 */

//Recycler View adapter for References Activity Card View
public class CardViewRecyclerAdapter extends RecyclerView.Adapter<CardViewRecyclerAdapter.CardViewHolder> {

    private ArrayList<ReferenceObject> referenceObjects;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        public TextView nameText;
        public TextView linkText;

        public CardViewHolder(View itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);
            nameText = itemView.findViewById(R.id.textView);
            linkText = itemView.findViewById(R.id.textView2);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public CardViewRecyclerAdapter(ArrayList<ReferenceObject> referenceObjects) {
        this.referenceObjects = referenceObjects;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_layout, parent, false);
        CardViewHolder cardViewHolder = new CardViewHolder(v, onItemClickListener);
        return cardViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        ReferenceObject referenceObject = referenceObjects.get(position);

        holder.nameText.setText(referenceObject.getmText1());
        holder.linkText.setText((referenceObject.getmText2()));
    }

    @Override
    public int getItemCount() {
        return referenceObjects.size();
    }
}