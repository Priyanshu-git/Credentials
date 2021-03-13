package com.example.credentials;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private FirebaseUser mFirebaseUser = null;
    private FirebaseFirestore db;
    private static CollectionReference coll;
    static Vector<String> entry_id = new Vector<>();
    final String KEY_NAME = "NAME", KEY_URL = "URL", KEY_PASS = "PASS";
    public static Activity fa;

    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter<Entry, EntryHolder> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fa = this;
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.t_bar);
        setSupportActionBar(toolbar);

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        coll = db.collection(mFirebaseUser.getUid());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mFirebaseUser == null) {
            startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        coll.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(MainActivity.this, "Error while loading", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onEvent: " + error.toString());
                    return;
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.opt_my_profile:
                startActivity(new Intent(this, MyProfile.class));
                break;
            case R.id.opt_sign_out:
                signOut();
                break;
            case R.id.opt_add:
                startActivity(new Intent(getApplicationContext(), AddEntryActivity.class));
                break;
        }
        return true;
    }

    public static void delete_entry(int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.fa);
        builder.setTitle("Delete Entry?").setMessage("Are you sure you want to delete this credential? It can't be undone.")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyProfile.collection.document(entry_id.get(pos)).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(MainActivity.fa, "Credential Deleted", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.fa, "Unable to Delete. Please test your internet connection.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void signOut() {
        AuthUI.getInstance()
                .signOut(this).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Sign Out Failed", Toast.LENGTH_LONG).show();
            }
        })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Signed Out", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
                        finish();
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