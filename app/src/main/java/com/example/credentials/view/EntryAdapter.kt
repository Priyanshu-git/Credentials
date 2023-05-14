package com.example.credentials.view

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.credentials.R
import com.example.credentials.data.model.Entry
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EntryAdapter(var dataset: ArrayList<Entry>) : RecyclerView.Adapter<EntryAdapter.ViewHolder>() {
    var mContext: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false)
        mContext = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = dataset[position].NAME
        holder.url.text = dataset[position].URL
        holder.pass.setText(R.string.string_dummy)
        holder.docID = dataset[position].DOC_ID
        holder.visible.setOnClickListener { v: View? ->
            if (mContext!!.getString(R.string.string_dummy).contentEquals(holder.pass.text))
                holder.pass.text = dataset[position].PASS
            else
                holder.pass.setText(R.string.string_dummy)
        }
        holder.delete.setOnClickListener { v: View? ->
            val index = holder.adapterPosition
            showAlertDialog(index)
        }
    }

    private fun showAlertDialog(index: Int) {
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle("Confirm Delete?").setMessage("Do you want to delete this entry?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog: DialogInterface?, which: Int ->
                    val uid = FirebaseAuth.getInstance().uid
                    val db = FirebaseFirestore.getInstance()
                    val coll = db.collection(uid!!)
                    coll.document(dataset[index].DOC_ID!!)
                            .delete()
                            .addOnCompleteListener { task: Task<Void?>? ->
                                Toast.makeText(mContext, "Entry Deleted", Toast.LENGTH_SHORT).show()
                                dataset.removeAt(index)
                                notifyItemRemoved(index)
                            }
                            .addOnFailureListener { e: Exception? -> Toast.makeText(mContext, "Error. Please try again", Toast.LENGTH_SHORT).show() }
                }
                .setNegativeButton("No") { dialog: DialogInterface, which: Int -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }

    fun updateList(newList: ArrayList<Entry>?) {
        dataset = ArrayList(newList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var url: TextView
        var pass: TextView
        var docID: String? = ""
        var delete: Button
        var visible: Button

        init {
            name = itemView.findViewById(R.id.row_name)
            url = itemView.findViewById(R.id.row_url)
            pass = itemView.findViewById(R.id.row_pass)
            delete = itemView.findViewById(R.id.btn_delete_entry)
            visible = itemView.findViewById(R.id.btn_visible)
        }
    }
}