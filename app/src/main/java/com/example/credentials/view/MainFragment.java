package com.example.credentials.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.credentials.R;
import com.example.credentials.data.model.Entry;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


public class MainFragment extends Fragment {
    View root;
    private static final String TAG = "MainFragment";

    private FirebaseUser mFirebaseUser = null;
    private FirebaseFirestore db;
    private static CollectionReference coll;
    static Vector<String> entry_id = new Vector<>();
    ArrayList<Entry> entryList = new ArrayList<>();

    private RecyclerView recyclerView;
    LinearLayout failureView;
    Button retryButton;
    private EntryAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        coll = db.collection(mFirebaseUser.getUid());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root= inflater.inflate(R.layout.fragment_main, container, false);

        initiateViews();

        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        udpateMainView();
        fetchData();

        adapter = new EntryAdapter(entryList);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab= root.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(view -> startActivity(new Intent(getContext(), AddEntryActivity.class)));
        return root;
    }

    private void udpateMainView() {
        if (entryList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            failureView.setVisibility(View.VISIBLE);
            retryButton.setOnClickListener(view -> {
                fetchData();
                udpateMainView();
            });
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            failureView.setVisibility(View.GONE);
            updateList();
        }
    }

    private void fetchData() {
        coll.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                entryList.clear();
                for (QueryDocumentSnapshot document : task.getResult()){
                    Map<String, Object> map = document.getData();
                    entryList.add(new Entry(""+map.get("NAME"), ""+map.get("PASS"), ""+map.get("URL"), ""+document.getId()));
                }
                udpateMainView();
            }
        });
    }


    private void initiateViews() {
        recyclerView = root.findViewById(R.id.recyclerView);
        failureView = root.findViewById(R.id.failure_view);
        retryButton = root.findViewById(R.id.btn_retry);
    }



    private void updateList() {
        adapter.updateList(entryList);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFirebaseUser == null) {
            startActivity(new Intent(getContext(), WelcomeActivity.class));
            getActivity().finish();
        }
        fetchData();
    }
}