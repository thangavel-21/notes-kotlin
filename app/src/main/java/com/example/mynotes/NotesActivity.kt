package com.example.mynotes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.example.mynotes.fragments.NotesFragment
import com.example.mynotes.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class NotesActivity :AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var bottomNavigationView : BottomNavigationView;
    private lateinit var notesFragment: NotesFragment;
    private lateinit var profileFragment: ProfileFragment;
    private lateinit var fab: FloatingActionButton
    var db = FirebaseFirestore.getInstance();
    var currentFirebaseUser = FirebaseAuth.getInstance().currentUser
    var currentUser = currentFirebaseUser!!.uid
    val docRef =  db.collection("notes").document(currentUser).collection("notes-details").document("txKyasvnVL9KRi6yHGzs");
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_nav)

        notesFragment = NotesFragment();
        profileFragment = ProfileFragment()

        subscribeToRealtimeUpdates();

        fab = findViewById(R.id.add_new_notes);
        fab.setOnClickListener {
            val intent = Intent(this, EditorActivity::class.java);
            startActivity(intent);
        }

        makeCurrentFragment(notesFragment)
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }
    private fun makeCurrentFragment(fragment: Fragment) {
        if (fragment is ProfileFragment) {
            fab.visibility = View.GONE
        }
        else{
            fab.visibility = View.VISIBLE
        }
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frag, fragment)
            commit()
        }
    }

    private fun subscribeToRealtimeUpdates() {
        docRef.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            firebaseFirestoreException?.let {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            querySnapshot?.let {
                Log.e("TAG", "subscribeToRealtimeUpdates: " + (it.data?.get("title") ?: "null"))
            }
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.home -> {
                makeCurrentFragment(notesFragment)
            }
            R.id.profile -> {
                makeCurrentFragment(profileFragment)
            }
        }
        return true
    }
}
