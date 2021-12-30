package com.example.mynotes

import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class NotesAdapter (private val mContext: Context , private val arrayList: ArrayList<NotesData>):ArrayAdapter<NotesData>(mContext,
                  R.layout.list_row,arrayList)  {

    var db = FirebaseFirestore.getInstance();
    var currentFirebaseUser = FirebaseAuth.getInstance().currentUser
    var currentUser = currentFirebaseUser!!.uid

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater : LayoutInflater = LayoutInflater.from(mContext)
        val view :View = inflater.inflate(R.layout.list_row,null)
        val title :TextView = view.findViewById(R.id.title)
        val deleteButton: ImageButton = view.findViewById(R.id.delete);

        view.tag = arrayList[position].id;
        deleteButton.tag = arrayList[position].id;
        title.text = arrayList[position].title
        deleteButton.visibility = View.GONE


        view.setOnClickListener {
            var intent = Intent(mContext, EditorActivity::class.java);
            intent.putExtra("id", it.tag.toString());
            mContext.startActivity(intent);
        }

        deleteButton.setOnClickListener {
            deleteDoc(it.tag.toString());
        }

        return view
    }

    private fun deleteDoc(id: String){
        db.collection("notes").document(currentUser).collection("notes-deatils").document(id).delete().addOnCompleteListener {
            Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show();
            onRestart()
        };
    }
    private fun onRestart(){
        super.notifyDataSetChanged();
    }
}