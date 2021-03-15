package com.example.credentials;

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
import android.widget.Toast;

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

import java.util.Vector;


public class MainFragment extends Fragment {
    View root;
    private static final String TAG = "MainFragment";

    private FirebaseUser mFirebaseUser = null;
    private FirebaseFirestore db;
    private static CollectionReference coll;
    static Vector<String> entry_id = new Vector<>();
    public static Activity activityContext;

    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter<Entry, EntryHolder> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        coll = db.collection(mFirebaseUser.getUid());
        activityContext =DrawerActivity.fa;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root= inflater.inflate(R.layout.fragment_main, container, false);

        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(DrawerActivity.fa));
        Query query = FirebaseFirestore.getInstance().collection(mFirebaseUser.getUid());

        coll.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult())
                        entry_id.add(document.getId());
                }
            }
        });

        FirestoreRecyclerOptions<Entry> options = new FirestoreRecyclerOptions.Builder<Entry>()
                .setQuery(query, Entry.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Entry, EntryHolder>(options) {
            @NonNull
            @Override
            public EntryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
                return new EntryHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull EntryHolder holder, int position, @NonNull Entry model) {
                holder.setNAME(model.getNAME());
                holder.setURL(model.getURL());
                holder.setPASS(model.getPASS());
                holder.setDelete(model.getDOC_ID());
            }
        };
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab= root.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DrawerActivity.fa,AddEntryActivity.class));
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFirebaseUser == null) {
            startActivity(new Intent(DrawerActivity.fa, WelcomeActivity.class));
            activityContext.finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        coll.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(DrawerActivity.fa, "Error while loading", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onEvent: " + error.toString());
                    return;
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
    }

    public void onBackPressed() {
        activityContext.finish();
    }

    private void signOut() {
        AuthUI.getInstance()
                .signOut(activityContext).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activityContext.getApplicationContext(), "Sign Out Failed", Toast.LENGTH_LONG).show();
            }
        })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(activityContext.getApplicationContext(), "Signed Out", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(activityContext, WelcomeActivity.class));
                        activityContext.finish();
                    }
                });
    }

    public static void delete(String doc_id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.fa);
        builder.setTitle("Confirm Delete?").setMessage("Do you want to delete this entry?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        coll.document(doc_id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(MainActivity.fa, "Entry Deleted", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.fa, "Error. Please try again", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("No", (dialog, which) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();
    }
}