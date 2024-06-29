package com.example.campusconnectfinal.homeactivities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.campusconnectfinal.R
import com.example.campusconnectfinal.adapters.ContactListAdapter

class Contacts : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<Contact>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        listView = findViewById(R.id.contact_list)

        // create dummy list of contacts
        val contactList = listOf(
            Contact("DSW Office", "8295695527"),
            Contact("COE", "9811675860"),
            Contact("Dean Academics Affair office", "8826350209"),
            Contact("Training &amp Placement Office", "9818187936"),
            Contact("Alumni Affairs", "9818299224"),
            Contact("Account Section", "8287178222"),
            Contact("Affiliation Branch", "9818277724"),
            Contact("Proctor Office", "9873762868"),
            Contact("Chief Hostel warden office", "9312899357"),
            Contact("Library", "9958411443"),
            Contact("PRO Office", "8447141780"),
            Contact("Women Cell", "9953505828"),
            Contact("Workshop Supdt.", "9416338829"),
            Contact("Dept. of Mech.", "9818312533"),
            Contact("Dept. of Comp.", "9953637670")
        )

        // initialize adapter with contact list
        adapter = ContactListAdapter(this, contactList)

        // set adapter to ListView
        listView.adapter = adapter

        // set click listener for list item


        // make call to selected contact's phone number
        listView.setOnItemClickListener { _, _, position, _ ->
            // get selected contact from adapter
            val contact = adapter.getItem(position)
            if (contact != null) {
                // make call to selected contact's phone number
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phone}"))
                startActivity(intent)
            }
        }
    }
}