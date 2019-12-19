@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.whiskey.notes

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.view_note.*
import java.util.*
import kotlin.collections.ArrayList

class ViewNoteActivity : AppCompatActivity() {
    var notes = ArrayList<String>()
    var titles = ArrayList<String>()
    var dates = ArrayList<String>()
    lateinit var note: String
    lateinit var title: String

    var position: Int = 0
    var menuVisible = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_note)

        dates = intent.getStringArrayListExtra("dates")
        notes = intent.getStringArrayListExtra("notes")
        titles = intent.getStringArrayListExtra("titles")
        note = intent.getStringExtra("note")
        title = intent.getStringExtra("title")
        position = intent.getIntExtra("position", position)

        intent.putStringArrayListExtra("notes", notes)
        intent.putStringArrayListExtra("titles", titles)
        intent.putStringArrayListExtra("dates", dates)
            eTitle.hint = "No Title"
            eTitle.setHintTextColor(Color.DKGRAY)


            eNote.hint = "Notes"

        toolbar.setTitleTextColor(Color.BLACK)
        toolbar.setBackgroundColor(Color.parseColor("#07C9FA"))
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        eNote.clearFocus()
        eTitle.clearFocus()
        eTitle.setText(title)
        eNote.setText(note)
        rConst.setOnClickListener {
            eNote.hasFocus()
            eNote.isSelected = true
            eNote.isCursorVisible = true
            menuVisible = true
            eNote.requestFocus()
            invalidateOptionsMenu()
            showSoftKeyboard(this.findViewById(R.id.eNote))

        }
        eNote.setOnFocusChangeListener { v, hasFocus ->
            eNote.hasFocus()
            eNote.isSelected = true
            eNote.isCursorVisible = true
            menuVisible = true
            invalidateOptionsMenu()

        }
        eTitle.setOnFocusChangeListener { v, hasFocus ->
            eTitle.hasFocus()

            eTitle.isSelected = true
            menuVisible = true
            invalidateOptionsMenu()

        }
    }
    fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun ResetView(){
        eTitle.hint = "No Title"
        eTitle.setHintTextColor(Color.DKGRAY)


        eNote.hint = "Notes"
        eTitle.clearFocus()
        eNote.clearFocus()
        menuVisible = false
        hideKeyboard()
        title = eTitle.text.toString()
        note = eNote.text.toString()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.getItemId()

        if (id == R.id.save) {
            //save function
            ResetView()
            return true
        }

        return super.onOptionsItemSelected(item)

    }
    fun Activity.hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.findViewById<EditText>(R.id.eNote).getWindowToken(), 0)
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onSupportNavigateUp():Boolean{
        onBackPressed()
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {

        val menuItem = menu?.findItem(R.id.save)

        menuItem?.isVisible = menuVisible
        return true
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBackPressed() {
        if(eTitle.isFocused || eNote.isFocused){
            ResetView()

        }else{
            val mainIntent = Intent(this, MainActivity::class.java)
            val noteText = note
            val noteTitle = title

            val date = Calendar.getInstance().time
            val formatter = SimpleDateFormat("MM/dd/yyyy @ hh:mm aaa")
            val dateText = formatter.format(date).toString()

            mainIntent.putExtra("note", noteText)
            mainIntent.putExtra("title", noteTitle)
            mainIntent.putStringArrayListExtra("notes", notes)
            mainIntent.putStringArrayListExtra("titles", titles)
            mainIntent.putStringArrayListExtra("dates", dates)
            mainIntent.putExtra("date", dateText)
            mainIntent.putExtra("position", position)

            startActivity(mainIntent)
        }

    }
}