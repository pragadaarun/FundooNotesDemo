package com.example.fundoonotes.DashBoard.Fragments.Notes;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fundoonotes.DashBoard.Activity.HomeActivity;
import com.example.fundoonotes.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class UpdateNoteFragment extends Fragment {

    private static final String TAG = "UpdateNoteFragment";
    EditText updateNoteTitle, updateNoteDescription;
    Button updateButton;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_note, container, false);
        String title = getArguments().getString("title");
        String description = getArguments().getString("description");
        String noteID = getArguments().getString("noteID");
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        updateNoteTitle = (EditText) view.findViewById(R.id.updateNoteTitle);
        updateNoteDescription = (EditText) view.findViewById(R.id.updateNoteDescription);
        updateButton =  view.findViewById(R.id.updateNoteButton);

        Log.e(TAG, "onCreateView: " + title);

        updateNoteTitle.setText(title);
        updateNoteDescription.setText(description);

        updateButton.setOnClickListener(v -> {
            String newNoteTitle = updateNoteTitle.getText().toString();
            String newNoteDescription = updateNoteDescription.getText().toString();

            if (!newNoteTitle.isEmpty() && !newNoteDescription.isEmpty()) {
                firebaseFirestore=FirebaseFirestore.getInstance();
                DocumentReference documentReference = firebaseFirestore
                        .collection("users")
                        .document(firebaseUser.getUid())
                        .collection("notes").document(noteID);
                Map<String,Object> note=new HashMap<>();
                note.put("title", newNoteTitle);
                note.put("description", newNoteDescription);
                note.put("creationDate", System.currentTimeMillis());
                documentReference.set(note).addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(),"Note is updated",
                            Toast.LENGTH_SHORT).show();
                    getFragmentManager().popBackStack();
                }).addOnFailureListener(e ->
                        Toast.makeText(getContext(),
                                "Failed To update",Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(getContext(),"Both Fields are Required",Toast.LENGTH_SHORT).show();
            }

        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}