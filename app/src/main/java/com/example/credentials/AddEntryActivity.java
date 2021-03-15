package com.example.credentials;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class AddEntryActivity extends AppCompatActivity {
    private static final String TAG = "AddEntryActivity";

    private EditText name, url, pass;
    private FirebaseFirestore db;
    final String KEY_NAME = "NAME", KEY_URL = "URL", KEY_PASS = "PASS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        name = findViewById(R.id.editTextName);
        url = findViewById(R.id.editTextURL);
        pass = findViewById(R.id.editTextPassword);

        db = FirebaseFirestore.getInstance();
    }

    public void addEntry() {
        String input_name = name.getText().toString();
        String input_url = url.getText().toString();
        String input_pass = pass.getText().toString();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        HashMap<String, Object> data = new HashMap<>();
        data.put(KEY_NAME, input_name);
        data.put(KEY_URL, input_url);
        data.put(KEY_PASS, input_pass);

        db.collection(uid)
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        documentReference.update("DOC_ID", documentReference.getId());
                        Toast.makeText(AddEntryActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();
                        name.setText("");
                        pass.setText("");
                        url.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        Toast.makeText(AddEntryActivity.this, "Adding Failed", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_new_entry, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.fireb_add) {
            addEntry();
        }
        else
            onBackPressed();
        return true;
    }
}