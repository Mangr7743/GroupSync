package com.example.groupsync

import AuthenticationViewModel
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.groupsync.databinding.ActivityMainBinding
import com.example.groupsync.ui.auth.LoginActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: AuthenticationViewModel

    private lateinit var mFirestoreRef: CollectionReference
    private var mFirebaseUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mFirestoreRef = FirebaseFirestore.getInstance().collection("events")
        mFirebaseUserId = FirebaseAuth.getInstance().currentUser?.uid

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, // Existing top-level destination
                R.id.nav_calendar,
                R.id.nav_availability
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        binding.appBarMain.fab.setOnClickListener {
            navController.navigate(R.id.nav_newevent)
        }

        navController.addOnDestinationChangedListener { controller, destination, args ->
            if (destination.label.toString() == "New Event") {
                binding.appBarMain.fab.hide()
            } else {
                binding.appBarMain.fab.show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel = ViewModelProvider(this)[AuthenticationViewModel::class.java]
        return when (item.itemId) {
            R.id.action_settings -> {
                // Handle action_settings menu item click
                // You can implement the sign-out logic here
                viewModel.logoutUser()
                startActivity(Intent(this, LoginActivity::class.java))
                true
            }
            R.id.action_join_event -> {
                openJoinDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun openJoinDialog() {
        val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(this,R.style.MyDialogStyle)

        builder.setTitle("Join Event")

        val input = EditText(this)
        input.setInputType(InputType.TYPE_CLASS_TEXT)
        input.background = ResourcesCompat.getDrawable(resources , R.drawable.round_bg_edittext , null)
        builder.setView(input)
        input.setTextColor(ResourcesCompat.getColor(resources , R.color.black , null))
        input.height = 125
        input.textCursorDrawable = null
        Handler(Looper.getMainLooper()).postDelayed({
            val layoutParams = (input.layoutParams as? ViewGroup.MarginLayoutParams)
            layoutParams?.setMargins(40, 40, 40, 40)
            input.layoutParams = layoutParams
            input.setPadding(20,0,20,0)
        },200)

        builder.setPositiveButton("Join",
            DialogInterface.OnClickListener { dialog, which ->
                val code = input.getText().toString()

                // add user id to event
                mFirebaseUserId?.let { uid ->
                    FirebaseFirestore.getInstance().collection("events")
                        .whereEqualTo("inviteCode", code)
                        .get()
                        .addOnSuccessListener { result ->
                            val document = result.documents[0] // should only be one

                            var usersList = document.get("users") as MutableList<String>


                            if (usersList.contains(uid)) {
                                Toast.makeText(this, "You already joined this event!", Toast.LENGTH_SHORT).show()
                                return@addOnSuccessListener
                            }
                            usersList.add(uid)

                            document.reference.update("users", usersList).addOnSuccessListener { recreate() }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("HELP", "Error getting documents: ", exception)
                        }
                }
            })
        builder.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        builder.show()
    }
}