package com.example.credentials.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.credentials.R;
import com.example.credentials.data.model.Entry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.ViewHolder> {
    ArrayList<Entry> dataset;

    public EntryAdapter(ArrayList<Entry> dataset) {
        this.dataset = dataset;
    }

    Context mContext;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
        mContext = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EntryAdapter.ViewHolder holder, int position) {
        holder.name.setText(dataset.get(position).getNAME());
        holder.url.setText(dataset.get(position).getURL());
        holder.pass.setText(R.string.string_dummy);
        holder.docID = dataset.get(position).getDOC_ID();

        holder.visible.setOnClickListener(v -> {
            if (mContext.getString(R.string.string_dummy).contentEquals(holder.pass.getText()))
                holder.pass.setText(dataset.get(position).getPASS());
            else
                holder.pass.setText(R.string.string_dummy);
        });

        holder.delete.setOnClickListener(v -> {
            int index = holder.getAdapterPosition();
            showAlertDialog(index);
        });
    }

    private void showAlertDialog(int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Confirm Delete?").setMessage("Do you want to delete this entry?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> {
                    String uid = FirebaseAuth.getInstance().getUid();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    CollectionReference coll = db.collection(uid);
                    coll.document(dataset.get(index).getDOC_ID())
                            .delete()
                            .addOnCompleteListener(task -> {
                                Toast.makeText(mContext, "Entry Deleted", Toast.LENGTH_SHORT).show();
                                dataset.remove(index);
                                notifyItemRemoved(index);
                            })
                            .addOnFailureListener(e -> Toast.makeText(mContext, "Error. Please try again", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("No", (dialog, which) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void updateList(ArrayList<Entry> newList) {
        dataset = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, url, pass;
        String docID = "";

        Button delete, visible;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.row_name);
            url = itemView.findViewById(R.id.row_url);
            pass = itemView.findViewById(R.id.row_pass);
            delete = itemView.findViewById(R.id.btn_delete_entry);
            visible = itemView.findViewById(R.id.btn_visible);
        }
    }
}
