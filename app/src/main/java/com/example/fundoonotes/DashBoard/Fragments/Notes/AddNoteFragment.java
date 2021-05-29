package com.example.fundoonotes.DashBoard.Fragments.Notes;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.fundoonotes.Firebase.Model.FirebaseNoteModel;
import com.example.fundoonotes.HelperClasses.AddNoteListener;
import com.example.fundoonotes.HelperClasses.CallBack;
import com.example.fundoonotes.Firebase.DataManager.FirebaseNoteManager;
import com.example.fundoonotes.R;
import com.example.fundoonotes.SQLiteDataManager.DatabaseHelper;
import com.example.fundoonotes.SQLiteDataManager.NoteTableManager;
import com.example.fundoonotes.SQLiteDataManager.SQLiteNoteTableManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Objects;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import static com.example.fundoonotes.DashBoard.Activity.HomeActivity.addNote;

public class AddNoteFragment extends Fragment {

    private static final String TAG = "AddNoteFragment";
    private EditText fAddTitleOfNote, fAddDescriptionOfNote;
    private String docID;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    NoteTableManager noteTableManager;
    AddNoteListener addNoteListener;
    DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getContext());

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        addNoteListener = (AddNoteListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        addNote.hide();
        return inflater.inflate(R.layout.fragment_add_note, container, false);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button saveNoteButton = (Button) Objects.requireNonNull(getView()).findViewById(R.id.saveNoteButton);
        fAddTitleOfNote = (EditText) getView().findViewById(R.id.editTextTitle);
        fAddDescriptionOfNote = (EditText) getView().findViewById(R.id.editTextDescription);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        saveNoteButton.setOnClickListener(this::saveToFirebase);
    }

    private void saveToFirebase(View v) {
        String title = fAddTitleOfNote.getText().toString();
        String description = fAddDescriptionOfNote.getText().toString();
        String user = firebaseUser.getDisplayName();
        String userId = firebaseUser.getUid();
        long creationTime = 0;

        if (!title.isEmpty() || !description.isEmpty()) {
            FirebaseNoteManager firebaseNoteManager = new FirebaseNoteManager();
            firebaseNoteManager.addNote(title, description, new CallBack<String>() {

                @Override
                public void onSuccess(String data) {
                    Toast.makeText(getContext(),
                            "Note Created Successfully",
                            Toast.LENGTH_SHORT).show();
                    FirebaseNoteModel note = new FirebaseNoteModel(userId, data, title, description, creationTime);
                    addNoteListener.onNoteAdded(note);
                    docID = data;
                    noteTableManager = new SQLiteNoteTableManager(databaseHelper);
                    noteTableManager.addNote(note);
                    Log.e(TAG, "onSuccess: " + docID );
                    getFragmentManager().popBackStack();
                }

                @Override
                public void onFailure(Exception exception) {
                    Toast.makeText(getContext(),
                            "Failed To Create Note", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "Both fields are Required",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        addNote.show();
    }
}
