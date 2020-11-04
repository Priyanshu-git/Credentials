package com.example.credentials;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MyProfile extends AppCompatActivity {
    private MyProfile THIS = this;
    private TextView name,mail,uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        Toolbar toolbar = findViewById(R.id.t_bar);
        setSupportActionBar(toolbar);
        ActionBar ab=getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Button delete = findViewById(R.id.btn_delete_acc);
        name=findViewById(R.id.label_name);
        mail=findViewById(R.id.label_email);
        uid=findViewById(R.id.label_uid);

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(MyProfile.this,LoginActivity.class));
            finish();
        }

        String displayName=user.getDisplayName();
        String userEmail=user.getEmail();
        String userUid=user.getUid();

        name.setText(displayName);
        mail.setText(userEmail);
        uid.setText(userUid);

        delete.setOnClickListener(v -> {
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Confirm Delete Account?").setMessage("Are you sure you want to delete your Account? This will erase all your saved passwords.")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                delete();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
        AlertDialog alert=builder.create();
        alert.show();
        });

    }

    private void delete() {
      AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        AuthUI.getInstance().signOut(THIS).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(), "Deletion Successful", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(MyProfile.this,LoginActivity.class));
                                finish();
                                MainActivity.fa.finish();
                            }
                        });


                    }
                });
    }
}