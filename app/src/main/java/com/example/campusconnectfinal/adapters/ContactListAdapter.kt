package com.example.campusconnectfinal.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import com.example.campusconnectfinal.R
import com.example.campusconnectfinal.homeactivities.Contact

class ContactListAdapter(context: Context, contacts: List<Contact>) :
    ArrayAdapter<Contact>(context, R.layout.contact_list_item, contacts) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var View = convertView
        if (View == null) {
            View = LayoutInflater.from(context).inflate(R.layout.contact_list_item, parent, false)
        }

        val contact = getItem(position)

        val nameTextView = View?.findViewById<TextView>(R.id.contact_name)
        nameTextView?.text = contact?.name

        val callNowButton = View?.findViewById<Button>(R.id.call_button)
        callNowButton?.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${contact?.phone}")
            context.startActivity(intent)
        }

        return View!!
    }
}