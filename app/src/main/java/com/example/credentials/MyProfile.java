package com.example.credentials;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyProfile extends AppCompatActivity {
    private static final String TAG = "MyProfile";

    private MyProfile THIS = this;
    private TextView name, mail, uid;
    static CollectionReference collection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Button delete = findViewById(R.id.btn_delete_acc);
        name = findViewById(R.id.label_name);
        mail = findViewById(R.id.label_email);
        uid = findViewById(R.id.label_uid);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(MyProfile.this, WelcomeActivity.class));
            finish();
        }

        String displayName = user.getDisplayName();
        String userEmail = user.getEmail();
        String userUid = user.getUid();

        name.setText(displayName);
        mail.setText(userEmail);
        uid.setText(userUid);

        delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirm Delete Account?").setMessage("Are you sure you want to delete your Account? This will erase all your saved passwords.")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteDocuments(userUid);
                            deleteAcc();
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
        });

    }

    private void deleteDocuments(String uid){
        List<String> entry_list = new ArrayList<>();
        collection = FirebaseFirestore.getInstance().collection(uid);
        collection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult())
                        entry_list.add(document.getId());

                    for (String path : entry_list) {
                        collection.document(path).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG, "onComplete: Deleted "+path);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "onFailure: Couldn't delete"+path,e );
                            }
                        });
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "onFailure: Document Deletion failed", e);
                Toast.makeText(THIS, "Deletion failed. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteAcc() {
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        AuthUI.getInstance().signOut(THIS).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(), "Deletion Successful", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(MyProfile.this, WelcomeActivity.class));
                                finish();
                                MainActivity.fa.finish();
                            }
                        });


                    }
                });
    }
}