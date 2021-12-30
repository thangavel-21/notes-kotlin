package com.example.mynotes.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.mynotes.LoginActivity
import com.example.mynotes.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {
    var userLogout = FirebaseAuth.getInstance()
    var userDetails = userLogout.currentUser?.displayName
    var userProfile = userLogout.currentUser?.photoUrl
    private lateinit var googleSignInClient: GoogleSignInClient;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.profile_layout, container, false)
        val logout:Button = view.findViewById(R.id.logout);
        val userName:TextView = view.findViewById(R.id.userName)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        userName.text = userDetails;

        loadImage(view)

        logout.setOnClickListener{
            showDialog()
        }
        return view
    }

    private fun showDialog() {
        val customDialog = Dialog(requireActivity())
        customDialog.setContentView(R.layout.alert_dialog_layout)
        customDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val logoutBtn = customDialog.findViewById(R.id.yes_opt) as Button
        val cancelBtn = customDialog.findViewById(R.id.no_opt) as Button
        logoutBtn.setOnClickListener {
            userLogout.signOut()
            googleSignInClient.signOut().addOnCompleteListener {
                var intent = Intent(requireContext(),LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
                Toast.makeText(requireContext(),"Logout Successfully",Toast.LENGTH_SHORT).show()
            }
            customDialog.dismiss()
        }
        cancelBtn.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }

    fun loadImage(view: View){
        val userImage :ImageView = view.findViewById(R.id.userProfile)
        Glide.with(this).load(userProfile).circleCrop().into(userImage).onLoadFailed(context?.let {
            ContextCompat.getDrawable(
                it, R.drawable.emptyprofile)
        })
    }

}


