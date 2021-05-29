package com.example.fundoonotes.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fundoonotes.Firebase.DataManager.FirebaseLabelManager;
import com.example.fundoonotes.Firebase.Model.FirebaseLabelModel;
import com.example.fundoonotes.HelperClasses.OnLabelListener;
import com.example.fundoonotes.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LabelAdapter extends RecyclerView.Adapter<LabelAdapter.LabelViewHolder> {

    private static final String TAG = "LabelAdapter";
    private ArrayList<FirebaseLabelModel> labelList;
    FirebaseLabelManager labelManager;
    private OnLabelListener onLabellistener;

    public LabelAdapter(ArrayList<FirebaseLabelModel> labelList, OnLabelListener onLabellistener) {
        this.labelList = labelList;
        this.onLabellistener = onLabellistener;
    }

    @NonNull
    @Override
    public LabelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.label_view, parent, false);
        Log.e(TAG, "onCreateViewHolder: ");
        return new LabelViewHolder(view, onLabellistener);    }

    @Override
    public void onBindViewHolder(@NonNull LabelViewHolder holder, final int position) {
        FirebaseLabelModel label = labelList.get(position);
        holder.labelView.setText(label.getLabelName());
        Log.e("bhaskar", "onBindViewHolder: " + position);
    }

    @Override
    public int getItemCount() {
        return labelList.size();
    }

    public FirebaseLabelModel getItem(int position) {
        return labelList.get(position);
    }

    public void removeLabel(int position) {
        labelList.remove(position);
        notifyItemRemoved(position);
    }

    public void addLabel(FirebaseLabelModel label) {
        labelList.add(0, label);
        notifyItemInserted(0);
    }

    public static class LabelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView labelView;
        OnLabelListener onLabelListener;

        public LabelViewHolder(View itemView, OnLabelListener onLabelListener) {
            super(itemView);
            labelView = itemView.findViewById(R.id.label_single);
            this.onLabelListener = onLabelListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onLabelListener.OnLabelClick(getBindingAdapterPosition(), v);
        }
    }
}