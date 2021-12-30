package com.example.mynotes.fragments

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.mynotes.*
import com.example.mynotes.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.DocumentChange.*
import com.google.firebase.firestore.DocumentChange.Type.*
import io.grpc.internal.LogExceptionRunnable
import com.google.firebase.firestore.FirebaseFirestoreException

import com.google.firebase.firestore.QuerySnapshot

class NotesFragment : Fragment() {

    lateinit var fbn : FloatingActionButton;
    var db = FirebaseFirestore.getInstance();
    var currentFirebaseUser = FirebaseAuth.getInstance().currentUser
    var currentUser = currentFirebaseUser!!.uid
    lateinit var process: RelativeLayout;
    val docRef: CollectionReference =  db.collection("notes").document(currentUser).collection("notes-details");

    private val TAG = "NotesFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.listview, container, false)
        process = view.findViewById(R.id.progress);
        getdata(view)
        docRef.addSnapshotListener { queryDocumentSnapshots, e ->
            //Here use queryDocumentSnapshot to retrieve every document data
            queryDocumentSnapshots?.let {
                for(document in it){
                    Log.e(TAG, "onStart: " + document.id )
                }
            }
        }
        docRef.addSnapshotListener { snapshots, e ->

            if (e != null) {
            Log.w(TAG, "listen:error", e)
            return@addSnapshotListener
        }
    }
        return view;
    }

    override fun onResume() {
        super.onResume()
        view?.let { getdata(it) }
    }

    private fun getdata(view: View) {
        var arrayList : ArrayList<NotesData> = arrayListOf();
        var listView:ListView = view.findViewById(R.id.listview)
        var show : RelativeLayout = view.findViewById(R.id.emptyContainer)
        db.collection("notes").addSnapshotListener { value, error -> db.collection("notes").document(currentUser).collection("notes-deatils").orderBy("title").get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    listView.visibility = View.VISIBLE
                    show.visibility = View.GONE
                    for (document in result) {
                        arrayList.add(
                            NotesData(
                                document.getString("title").toString(),
                                document.getString("description").toString(),
                                document.id
                            )
                        );
                        listView.adapter = NotesAdapter(requireContext(), arrayList)
                        process.visibility = View.GONE;
                    }

                } else {
                    listView.visibility= View.INVISIBLE
                    show.visibility = View.VISIBLE
                    process.visibility = View.GONE;
                }
            }
                .addOnFailureListener { exception ->
                    Log.w("TAG", "Error getting documents: ", exception)
                }
        }
    }

}