package com.example.credentials.view

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.credentials.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddEntryActivity : AppCompatActivity() {
    private var name: EditText? = null
    private var url: EditText? = null
    private var pass: EditText? = null
    private var db: FirebaseFirestore? = null
    val KEY_NAME = "NAME"
    val KEY_URL = "URL"
    val KEY_PASS = "PASS"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_entry)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val ab = supportActionBar
        ab!!.setDisplayHomeAsUpEnabled(true)
        name = findViewById(R.id.editTextName)
        url = findViewById(R.id.editTextURL)
        pass = findViewById(R.id.editTextPassword)
        db = FirebaseFirestore.getInstance()
    }

    fun addEntry() {
        val input_name = name!!.text.toString()
        val input_url = url!!.text.toString()
        val input_pass = pass!!.text.toString()
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val data = HashMap<String, Any>()
        data[KEY_NAME] = input_name
        data[KEY_URL] = input_url
        data[KEY_PASS] = input_pass
        db!!.collection(uid)
                .add(data)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.id)
                    documentReference.update("DOC_ID", documentReference.id)
                    Toast.makeText(this@AddEntryActivity, "Added Successfully", Toast.LENGTH_SHORT).show()
                    name!!.setText("")
                    pass!!.setText("")
                    url!!.setText("")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                    Toast.makeText(this@AddEntryActivity, "Adding Failed", Toast.LENGTH_SHORT).show()
                }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_new_entry, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.fireb_add) {
            addEntry()
        } else onBackPressed()
        return true
    }

    companion object {
        private const val TAG = "AddEntryActivity"
    }
}