package com.example.fundoonotes.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.example.fundoonotes.Firebase.Model.FirebaseNoteModel;
import com.example.fundoonotes.HelperClasses.OnNoteListener;
import com.example.fundoonotes.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NoteAdapter extends RecyclerView.Adapter<BaseViewHolder> implements Filterable {

    private static final String TAG = "NoteAdapter";
    private ArrayList<FirebaseNoteModel> notesList;
    private final OnNoteListener onNoteListener;
    private List<FirebaseNoteModel> notesSearch;
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;

    public NoteAdapter(ArrayList<FirebaseNoteModel> list, OnNoteListener onNoteListener ){
        this.notesList = list;
        this.onNoteListener = onNoteListener;
        this.notesSearch = new ArrayList<>(notesList);//(List<FirebaseNoteModel>) notesList.clone();
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new MyViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.note_details, parent, false),onNoteListener);
            case VIEW_TYPE_LOADING:
                return new ProgressHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == notesList.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    public void addItems(ArrayList<FirebaseNoteModel> postItems) {
        notesList.addAll(postItems);
        notifyDataSetChanged();
    }

    public void clear() {
        notesList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        Log.e("Note Adapter", "get Item Count" + notesList.size());
        return notesList.size();
    }

    public void addNote(FirebaseNoteModel note){
        notesList.add(0, note);
        notifyItemInserted(0);
    }

    public void removeNote(int position){
        notesList.remove(position);
        notifyItemRemoved(position);
    }

    public FirebaseNoteModel getItem(int position) {
        return notesList.get(position);
    }

    @Override
    public Filter getFilter() {
        return notesFilter;
    }

    private Filter notesFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<FirebaseNoteModel> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0){
                Log.e(TAG, "performFiltering: " + constraint  + "  " + notesSearch.size());
                filteredList.addAll(notesSearch);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(FirebaseNoteModel note : notesSearch) {
                    if(note.getTitle().toLowerCase().contains(filterPattern)
                        || note.getDescription().toLowerCase().contains(filterPattern)) {
                        filteredList.add(note);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notesList.clear();
            notesList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public static class ProgressHolder extends BaseViewHolder {
        ProgressHolder(View itemView) {
            super(itemView);
        }
        @Override
        protected void clear() {
        }
    }
}
