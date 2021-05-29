
package com.example.fundoonotes.DashBoard.Fragments.Labels;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fundoonotes.Adapters.LabelAdapter;
import com.example.fundoonotes.Firebase.DataManager.FirebaseLabelManager;
import com.example.fundoonotes.Firebase.DataManager.FirebaseNoteManager;
import com.example.fundoonotes.Firebase.DataManager.LabelManager;
import com.example.fundoonotes.Firebase.Model.FirebaseLabelModel;
import com.example.fundoonotes.Firebase.Model.LabelViewModel;
import com.example.fundoonotes.HelperClasses.CallBack;
import com.example.fundoonotes.HelperClasses.OnLabelListener;
import com.example.fundoonotes.HelperClasses.ViewState;
import com.example.fundoonotes.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class LabelFragment extends Fragment {

    private static final String TAG = "LabelFragment";
    private EditText editLabel;
    private Button saveLabel;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    LabelManager labelManager;
    RecyclerView recyclerView;
    LabelViewModel labelViewModel;
    private AddLabelListener addLabelListener;
    private LabelAdapter labelAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        addLabelListener = (AddLabelListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_label, container, false);
        final StaggeredGridLayoutManager labelLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView = view.findViewById(R.id.recyclerviewLabel);
        recyclerView.setLayoutManager(labelLayoutManager);
        recyclerView.setHasFixedSize(true);
        labelManager = new FirebaseLabelManager();
        labelViewModel = new ViewModelProvider(this).get(LabelViewModel.class);
        saveLabel =(Button) view.findViewById(R.id.save_label_button);
        editLabel = (EditText) view .findViewById(R.id.editTextLabel);
        saveLabel.setOnClickListener(this::saveLabelToFirestore);
        labelManager = new FirebaseLabelManager();

        return view;
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        labelViewModel.labelMutableLiveData.observe(getViewLifecycleOwner(), new Observer<ViewState<ArrayList<FirebaseLabelModel>>>() {
            @Override
            public void onChanged(ViewState<ArrayList<FirebaseLabelModel>> arrayListViewState) {
                if (arrayListViewState instanceof ViewState.Loading) {
                    Toast.makeText(getContext(), "Loading", Toast.LENGTH_SHORT).show();
                } else if (arrayListViewState instanceof ViewState.Success) {
                    ArrayList<FirebaseLabelModel> labels = ((ViewState.Success<ArrayList<FirebaseLabelModel>>)arrayListViewState).getData();
                    labelAdapter = new LabelAdapter(labels, new OnLabelListener() {
                        @Override
                        public void OnLabelClick(int position, View viewHolder) {
                            Toast.makeText(getContext(), "Note Clicked at Position " + position, Toast.LENGTH_SHORT).show();
                            String labelId = labelAdapter.getItem(position).getLabelName();
                            String docID = labelAdapter.getItem(position).getLabelId();
                        }
                    });
                    recyclerView.setAdapter(labelAdapter);
                    labelAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Something went Wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveLabelToFirestore(View v) {
        String label = editLabel.getText().toString();
        if (!label.isEmpty()) {
             labelManager.addLabel(label, new CallBack<String>() {
                    @Override
                    public void onSuccess(String data) {
                        Toast.makeText(getContext(),
                                "Created Label", Toast.LENGTH_SHORT).show();
                        FirebaseLabelModel firebaseLabelModel = new FirebaseLabelModel(label,data);
                        addLabelListener.onLabelAdded(firebaseLabelModel);
                        editLabel.setText(null);
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Toast.makeText(getContext(),
                                "Failed To Create Label", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
            Toast.makeText(getContext(), "Empty Label cannot Created", Toast.LENGTH_SHORT).show();
        }
    }

    public void addLabel(FirebaseLabelModel label) {
        labelAdapter.addLabel(label);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}