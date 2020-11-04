package com.example.credentials;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<List<String>> entries;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name,url,pass;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.row_name);
            url=itemView.findViewById(R.id.row_url);
            pass=itemView.findViewById(R.id.row_pass);
        }
    }

    public MyAdapter(List<List<String>> entries) {
        this.entries=entries;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item,parent,false);
        MyViewHolder vh=new MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.name.setText(entries.get(position).get(0));
        holder.url.setText(entries.get(position).get(1));
        holder.pass.setText(entries.get(position).get(2));
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }


}
