package com.example.credentials.view

import android.app.AlertDialog
import android.content.*
import android.content.Context.CLIPBOARD_SERVICE
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.credentials.data.model.Entry
import com.example.credentials.databinding.RowItemBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EntryAdapter(var dataset: ArrayList<Entry>) : RecyclerView.Adapter<EntryAdapter.ViewHolder>() {
    var mContext: Context? = null
    var binding: RowItemBinding? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = RowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        mContext = parent.context
        return ViewHolder(binding!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = dataset[position].NAME
        holder.username.text = dataset[position].URL
        holder.docID = dataset[position].DOC_ID
        holder.userTile.text = dataset[position].NAME!![0].toString()

        setListeners(holder, position)

    }

    private fun setListeners(holder: ViewHolder, position: Int) {
        holder.mainLayout.setOnClickListener {
            if (holder.subMenu.tag == null || holder.subMenu.tag == "hidden") {
                holder.subMenu.tag = "visible"
                holder.subMenu.visibility = View.VISIBLE
                holder.divider.visibility = View.VISIBLE
            } else {
                holder.subMenu.tag = "hidden"
                holder.subMenu.visibility = View.GONE
                holder.divider.visibility = View.GONE
            }
        }
        holder.lCopyUsername.setOnClickListener {
            // copy username to clipboard
            val clipboardManager = mContext?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("label", dataset[position].NAME)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(mContext, "Username Copied", Toast.LENGTH_SHORT).show()
        }
        holder.llOpenUrl.setOnClickListener {
            // open url in browser
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(dataset[position].URL)
            mContext?.startActivity(intent)
        }
        holder.llShowPwd.setOnClickListener {
            // show password in dialog
            val builder = AlertDialog.Builder(mContext)
            builder.setTitle("Show Password").setMessage(dataset[position].PASS)
                    .setCancelable(false)
                    .setPositiveButton("Copy") { dialog: DialogInterface?, which: Int ->
                        // copy password to clipboard
                        val clipboardManager = mContext?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                        val clipData = ClipData.newPlainText("label", dataset[position].PASS)
                        clipboardManager.setPrimaryClip(clipData)
                        Toast.makeText(mContext, "Password Copied", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Dismiss") { dialog: DialogInterface, _: Int -> dialog.cancel() }
            val alert = builder.create()
            alert.show()
        }
        holder.delete.setOnClickListener{
            showAlertDialog(position)
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
                .setNegativeButton("No") { dialog: DialogInterface, _: Int -> dialog.cancel() }
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

    inner class ViewHolder(itemBinding: RowItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        val item = itemBinding.itemLayout
        val mainLayout = itemBinding.mainLayout
        val divider = itemBinding.divider
        val subMenu = itemBinding.subMenu

        val name = itemBinding.txtName
        val username = itemBinding.txtUsername
        val userTile = itemBinding.userTile

        val llShowPwd = itemBinding.llShowPwd
        val lCopyUsername = itemBinding.llCopyUsername
        val llOpenUrl = itemBinding.llOpenUrl
        val delete = itemBinding.llDelete

        var docID: String? = ""

    }
}