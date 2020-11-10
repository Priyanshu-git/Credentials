package com.example.credentials;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EntryHolder extends RecyclerView.ViewHolder {
    private View view;
    public EntryHolder(@NonNull View itemView) {
        super(itemView);
        view=itemView;
//        view.findViewById(R.id.cons_layout).setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                MainActivity.delete_entry(v.getVerticalScrollbarPosition());
//                return true;
//            }
//        });
    }

    public void setNAME(String NAME) {
        TextView textView=view.findViewById(R.id.row_name);
        textView.setText(NAME);
    }

    public void setPASS(String PASS) {
        TextView textView=view.findViewById(R.id.row_pass);
        textView.setText(PASS);
    }

    public void setURL(String URL) {
        TextView textView=view.findViewById(R.id.row_url);
        textView.setText(URL);
    }

}
