package com.example.mynotes

import android.content.Intent
import android.icu.text.CaseMap
import android.media.Image
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.google.firebase.firestore.*
import kotlin.collections.HashMap
import kotlin.math.log


class EditorActivity : AppCompatActivity() {
    lateinit var materialToolbar: MaterialToolbar;
    var db = FirebaseFirestore.getInstance();
    lateinit var id: String;
    lateinit var title: EditText;
    lateinit var description: EditText;
    private val TAG: String = EditorActivity::class.java.simpleName;
    private var currentFirebaseUser = FirebaseAuth.getInstance().currentUser
    var initialTitle : String = ""
    var initialDes : String = ""
    var isModified : Boolean = false
    var currentUser = currentFirebaseUser!!.uid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_notes);

        id = intent.getStringExtra("id").toString() ?: "null"

        materialToolbar = findViewById(R.id.materialToolbar)
        title = findViewById(R.id.title)
        description = findViewById(R.id.description)
        val delete: ImageButton = findViewById(R.id.delete);
        delete.setOnClickListener {
                deleteDoc();
        };

        title.doOnTextChanged { text, start, before, count ->
            isModified = title.text.toString() != initialTitle
        }
        description.doOnTextChanged { text, start, before, count ->
            isModified = description.text.toString() != initialDes
        }
        if(id != "null"){
            db.collection("notes").document(currentUser).collection("notes-deatils").document(id).get()
                .addOnSuccessListener { document ->
                    title.text = Editable.Factory.getInstance().newEditable(
                        document.data?.get("title").toString());
                    initialTitle = document.data?.get("title").toString();
                    description.text = Editable.Factory.getInstance().newEditable(
                        document.data?.get("description").toString());
                    initialDes = document.data?.get("description").toString()
                    isModified = false
                }
                .addOnFailureListener { exception ->
                    Log.w("TAG", "Error getting documents: ", exception)
                }
        }
        else {
            delete.visibility = View.GONE;
        }
        materialToolbar.setNavigationOnClickListener{
            super.onBackPressed()
        }
        materialToolbar?.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.save-> {
                    saveDoc();
                }
                R.id.delete -> {
                    deleteDoc();
                }
            }
            return@setOnMenuItemClickListener false
        }
    }

    override fun onBackPressed() {
        val titleText : String = title.text.toString();
        val descText : String = description.text.toString();
        if (!titleText.isNullOrEmpty() && !descText.isNullOrEmpty()){
            saveDoc()
        }
        super.onBackPressed()
    }

    private fun deleteDoc(){
        if(id != "null"){
            db.collection("notes").document(currentUser).collection("notes-deatils").document(id).delete().addOnCompleteListener {
                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                finish();
            };
        }
    }

    private fun saveDoc(){
        val titleText : String = title.text.toString();
        val descText : String = description.text.toString();
        val notesModal = NotesData(titleText,descText, currentUser);
        if (!titleText.isNullOrEmpty() && !descText.isNullOrBlank()) {
            if(id == "null"){
                db.collection("notes").document(currentUser).collection("notes-deatils")
                    .add(notesModal)
                    .addOnSuccessListener {
                        Handler().postDelayed({
                            finish()
                        }, 500)

                        Toast.makeText(this, "Successfully Added", Toast.LENGTH_LONG).show()
                    }
            }
            else {
                    if(isModified) {
                        var data: MutableMap<String, String> = HashMap();
                        data["title"] = titleText
                        data["description"] = descText
                        db.collection("notes").document(currentUser).collection("notes-deatils")
                            .document(id)
                            .update(data as Map<String, Any>)
                        finish();
                        Toast.makeText(this, "Successfully Updated", Toast.LENGTH_LONG).show()
                    }
                else{
                    finish();
                }
            }

        } else if (titleText.isEmpty() && descText.isEmpty()){
            title.error = "error";
            description.error = "error";
            Toast.makeText(this, "Title And Description Required", Toast.LENGTH_SHORT).show()
        } else if (descText.isEmpty()){
            description.error = "error";
            Toast.makeText(this, "Description is Required", Toast.LENGTH_SHORT).show()
        } else {
            title.error = "error";
            Toast.makeText(this, "Title is Required", Toast.LENGTH_SHORT).show()
        }
    }
}


