package com.example.fundoonotes.Adapters;

import android.view.View;
import android.widget.TextView;

import com.example.fundoonotes.Firebase.Model.FirebaseNoteModel;
import com.example.fundoonotes.HelperClasses.OnNoteListener;
import com.example.fundoonotes.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends BaseViewHolder implements  View.OnClickListener{
    public TextView noteTitle, noteDescription;
    View view;
    CardView mCardView;
    private final OnNoteListener onNoteListener;

    public MyViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
        super(itemView);
        noteTitle = itemView.findViewById(R.id.note_title);
        noteDescription = itemView.findViewById(R.id.note_description);
        mCardView = itemView.findViewById(R.id.note_card);
        view = itemView;
        this.onNoteListener = onNoteListener;
        itemView.setOnClickListener(this);
    }

    public void onBind(ArrayList<FirebaseNoteModel> notesList, int position) {
        super.onBind(position);
        FirebaseNoteModel item = notesList.get(position);
        noteTitle.setText(item.getTitle());
        noteDescription.setText(item.getDescription());
    }

    @Override
    public void onClick(View v) {
        onNoteListener.onNoteClick(getBindingAdapterPosition(),v);
    }

    @Override
    protected void clear() {

    }
}