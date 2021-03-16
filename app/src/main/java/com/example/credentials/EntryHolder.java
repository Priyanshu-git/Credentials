package com.example.credentials;

import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

public class EntryHolder extends RecyclerView.ViewHolder {
    private View view;
    private String password = "...";

    public EntryHolder(@NonNull View itemView) {
        super(itemView);
        view = itemView;
    }

    public void setNAME(String NAME) {
        TextView textView = view.findViewById(R.id.row_name);
        textView.setText(NAME);
    }

    public void setPASS(String PASS) {
        password = PASS;
        Button vis = view.findViewById(R.id.btn_visible);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vis.setTooltipText("Show/Hide Password");
        }
        Button del = view.findViewById(R.id.btn_delete_entry);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            del.setTooltipText("Delete this Entry");
        }
        TextView t = view.findViewById(R.id.row_pass);
        vis.setOnClickListener(v1 -> {
            if ("XXXXX".contentEquals(t.getText()))
                t.setText(password);
            else
                t.setText(R.string.string_dummy);
        });
    }

    public void setURL(String URL) {
        TextView textView = view.findViewById(R.id.row_url);
        textView.setText(URL);
    }

    public void setDelete(String doc_id) {
        Button del = view.findViewById(R.id.btn_delete_entry);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerActivity.delete(doc_id);
            }
        });
    }
}
