package com.example.fundoonotes.DashBoard.Fragments.Notes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fundoonotes.Adapters.MyViewHolder;
import com.example.fundoonotes.Adapters.PaginationListener;
import com.example.fundoonotes.Firebase.Model.FirebaseNoteModel;
import com.example.fundoonotes.Firebase.DataManager.FirebaseNoteManager;
import com.example.fundoonotes.Adapters.NoteAdapter;
import com.example.fundoonotes.Firebase.Model.MyViewModelFactory;
import com.example.fundoonotes.HelperClasses.CallBack;
import com.example.fundoonotes.HelperClasses.OnNoteListener;
import com.example.fundoonotes.HelperClasses.ViewState;
import com.example.fundoonotes.R;
import java.util.ArrayList;
import com.example.fundoonotes.DashBoard.Activity.HomeActivity;
import com.example.fundoonotes.SQLiteDataManager.DatabaseHelper;
import com.example.fundoonotes.SQLiteDataManager.NoteTableManager;
import com.example.fundoonotes.SQLiteDataManager.SQLiteNoteTableManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import static com.example.fundoonotes.Adapters.PaginationListener.LIMIT;

public class NotesFragment extends Fragment {
    private static final String TAG = "NotesFragment";
    FirebaseNoteManager firebaseNoteManager;
    private RecyclerView recyclerView;
    private NoteAdapter notesAdapter;
    private NotesViewModel notesViewModel;
    private RecyclerView.LayoutManager layoutManager;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    private boolean isLastPage = false;
    private boolean isLoading = false;
    int itemCount = 0;
    private static int TOTAL_NOTES_COUNT = 0;
    private static int CURRENT_NOTES_COUNT = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_notes, container, false);

        final StaggeredGridLayoutManager layoutManager = new
                StaggeredGridLayoutManager(1,
                StaggeredGridLayoutManager.VERTICAL);

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
//                                                     fetchNotes(notesAdapter.getItem(CURRENT_NOTES_COUNT-1).getCreationTime());

                isLoading = true;
                fetchNotes(notesAdapter.getItem(notesAdapter.getItemCount()-2).getCreationTime());
//                                                     fetchNotes(notesAdapter.getItem(notesAdapter.getItemCount()).getCreationTime());
                Log.e(TAG, "loadMoreItems: " + CURRENT_NOTES_COUNT );
//                                                     fetchNotes(0);
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
        firebaseNoteManager = new FirebaseNoteManager();
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getContext());
        NoteTableManager noteTableManager = new SQLiteNoteTableManager(databaseHelper);
        notesViewModel = new ViewModelProvider(this, new MyViewModelFactory(noteTableManager)).get(NotesViewModel.class);

        fetchNotes(0);
        deleteNote();
        return view;
    }

    private void fetchNotes(long timestamp) {
        fetchAllNotesSize(new CallBack<Integer>() {
            @Override
            public void onSuccess(Integer data) {
                TOTAL_NOTES_COUNT = data;
                Log.e(TAG, "onSuccess: total notes count " +  data );
                ArrayList<FirebaseNoteModel> notesList = new ArrayList<FirebaseNoteModel>();
                firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
                String userId =firebaseUser.getUid();
                firebaseFirestore= FirebaseFirestore.getInstance();
                firebaseFirestore.collection("users").document(firebaseUser.getUid())
                        .collection("notes")
                        .orderBy("creationDate")
                        .startAfter(timestamp)
                        .limit(LIMIT)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                int i;
                                for (i=0;i<queryDocumentSnapshots.size();i++) {
                                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(i);
//                                    Log.e(TAG, "onSuccess: " + documentSnapshot);
                                    String title = documentSnapshot.getString("title");
                                    String description = documentSnapshot.getString("description");
                                    String noteID = documentSnapshot.getId();
                                    long timestamp = documentSnapshot.getLong("creationDate");

                                    FirebaseNoteModel note = new FirebaseNoteModel(userId, noteID, title,description, timestamp);
                                    note.setCreationTime(timestamp);
                                    notesList.add(note);
//                                    CURRENT_NOTES_COUNT += i;
                                }

                                if (CURRENT_NOTES_COUNT < TOTAL_NOTES_COUNT ) {
                                    Log.e(TAG, "onSuccess: Current & Total "+ CURRENT_NOTES_COUNT + " : " + TOTAL_NOTES_COUNT );
//                                    notesAdapter.addLoading();
                                } else {
                                    Log.e(TAG, "onSuccess: is last page true " + CURRENT_NOTES_COUNT + " : " + TOTAL_NOTES_COUNT );

                                    isLastPage = true;
                                }
                                isLoading = false;
                                CURRENT_NOTES_COUNT += queryDocumentSnapshots.size() ;
                                notesAdapter.addItems(notesList);
                            }
                        });

                recyclerView.setAdapter(notesAdapter);
                notesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception exception) {

            }
        });

    }

    private void fetchAllNotesSize(CallBack<Integer> countCallBack){
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore= FirebaseFirestore.getInstance();
        firebaseFirestore.collection("users").document(firebaseUser.getUid())
                .collection("notes")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        countCallBack.onSuccess(queryDocumentSnapshots.size());

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        countCallBack.onFailure(e);
                    }
                });
    }

    private void deleteNote() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                try {
                    String noteId = notesAdapter.getItem(position).getNoteID();
                    notesAdapter.removeNote(position);
                    firebaseNoteManager.moveToTrash("Notes","Trash",noteId);
                }catch(IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<FirebaseNoteModel> notes = new ArrayList<>();
        notesAdapter = new NoteAdapter(notes, new OnNoteListener() {
            @Override
            public void onNoteClick(int position, View viewHolder) {

                Toast.makeText(getContext(), "Note Clicked at Position " + position, Toast.LENGTH_SHORT).show();
                String title = notesAdapter.getItem(position).getTitle();
                String description = notesAdapter.getItem(position).getDescription();
                String noteID = notesAdapter.getItem(position).getNoteID();
                //Put the value
                UpdateNoteFragment updateNoteFragment = new UpdateNoteFragment();
                Bundle bundle = new Bundle();

                bundle.putString("title", title);
                bundle.putString("description",
                        description);
                bundle.putString("noteID",
                        noteID);
                updateNoteFragment.setArguments(bundle);

                getFragmentManager().beginTransaction().replace(R.id.fragment_container, updateNoteFragment).addToBackStack(null).commit();

            }
        });
    }


//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        recyclerView = view.findViewById(R.id.recyclerView);
//        setLayoutManager(HomeActivity.IS_LINEAR_LAYOUT);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setHasFixedSize(true);
//        firebaseNoteManager = new FirebaseNoteManager();
//        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getContext());
//        NoteTableManager noteTableManager = new SQLiteNoteTableManager(databaseHelper);
//        notesViewModel = new ViewModelProvider(this, new MyViewModelFactory(noteTableManager))
//                .get(NotesViewModel.class);
//
//        notesViewModel.notesMutableLiveData.observe(getViewLifecycleOwner(),
//                new Observer<ViewState<ArrayList<FirebaseNoteModel>>>() {
//            @Override
//            public void onChanged(ViewState<ArrayList<FirebaseNoteModel>> arrayListViewState) {
//                if(arrayListViewState instanceof ViewState.Loading) {
//                    Toast.makeText(getContext(), "Loading", Toast.LENGTH_SHORT).show();
//                } else if (arrayListViewState instanceof ViewState.Success) {
//                    ArrayList<FirebaseNoteModel> notes = ((ViewState.Success<ArrayList<FirebaseNoteModel>>) arrayListViewState).getData();
//                    Log.e(TAG, "onNoteReceived: " + notes);
//                    notesAdapter = new NoteAdapter(notes, new OnNoteListener() {
//                        @Override
//                        public void onNoteClick(int position, View viewHolder) {
//                            Toast.makeText(getContext(),
//                                    "Note Clicked at Position " + position,
//                                    Toast.LENGTH_SHORT).show();
//                            String title = notesAdapter.getItem(position).getTitle();
//                            String description = notesAdapter.getItem(position).getDescription();
//                            String noteID = notesAdapter.getItem(position).getNoteID();
//                            //Put the value
//                            UpdateNoteFragment updateNoteFragment = new UpdateNoteFragment();
//                            Bundle noteToUpdate = new Bundle();
//
//                            noteToUpdate.putString("title", title);
//                            noteToUpdate.putString("description",
//                                    description);
//                            noteToUpdate.putString("noteID",
//                                    noteID);
//                            updateNoteFragment.setArguments(noteToUpdate);
//                            getFragmentManager().beginTransaction().
//                                    replace(R.id.home_fragment_container,
//                                            updateNoteFragment)
//                                    .addToBackStack(null).commit();
//                        }
//                    });
//                    recyclerView.setAdapter(notesAdapter);
//                    notesAdapter.notifyDataSetChanged();
//                } else {
//                    Toast.makeText(getContext(), "Something went Wrong", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0,
//                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//                int position = viewHolder.getBindingAdapterPosition();
//                try {
//                    String noteId = notesAdapter.getItem(position).getNoteID();
//                    notesAdapter.removeNote(position);
//                    firebaseNoteManager.moveToTrash("Notes","Trash", noteId);
//                }catch(IndexOutOfBoundsException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
//        itemTouchHelper.attachToRecyclerView(recyclerView);
//    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setLayoutManager(boolean isLinear) {
        int spanCount = 1;
        if (isLinear) {
            spanCount = 1;
        } else {
            spanCount = 2;
        }
        layoutManager = new StaggeredGridLayoutManager(spanCount,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
    }

    public void addNote(FirebaseNoteModel note) {
        notesAdapter.addNote(note);
    }

    public void searchText(String newText) {
        notesAdapter.getFilter().filter(newText);
    }
}