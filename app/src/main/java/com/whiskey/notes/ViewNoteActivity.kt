@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.whiskey.notes

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.view_note.*
import java.util.*
import kotlin.collections.ArrayList

class ViewNoteActivity : AppCompatActivity() {

    private var date = Calendar.getInstance().time
    private val formatter = SimpleDateFormat("MM/dd/yyyy, hh:mm aaa")
    private val dateText = formatter.format(date).toString()
    private var noteList = ArrayList<NoteModel>()
    private var groupList = ArrayList<String>()

    lateinit var note: String
    lateinit var title: String
    private var boolean: Int = 0
    private var bool: Boolean = false
    private var position: Int = 0
    private var menuVisible = false
    private lateinit var groupName: String
    private lateinit var dateT: String
    private val noteDB = NotesDB(this, null)
    private val groupsDB = GroupsDB(this, null)
    private lateinit var mAdView: AdView
    private var trashDB: TrashDB = TrashDB(this, null)
    private var deleteList = ArrayList<NoteModel>()
    private var frag: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_note)

        //Show Ad
        MobileAds.initialize(this)
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        eDate.text = ("Modified: $dateText").toString()

        note = intent.getStringExtra("note")
        title = intent.getStringExtra("title")
        position = intent.getIntExtra("position", -1)
        noteList = intent.getParcelableArrayListExtra("noteList")
        dateT = intent.getStringExtra("date")
        groupName = intent.getStringExtra("group")
        frag = intent.getIntExtra("frag", -1)
        eTitle.hint = "Note Title"
        eTitle.setHintTextColor(Color.DKGRAY)
        eNote.hint = "Notes"
        Log.d("group", groupName)
        setSupportActionBar(toolbar)
        toolbar.inflateMenu(R.menu.menu)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setTitleTextColor(Color.WHITE)
        boolean = noteDB.getFav(position + 1)

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
        eNote.setOnFocusChangeListener { _, hasFocus ->

            if(hasFocus) {
                eNote.hasFocus()
                eNote.isSelected = true
                eNote.isCursorVisible = true
                menuVisible = true
                invalidateOptionsMenu()
            }
        }
        eTitle.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                eTitle.hasFocus()

                eTitle.isSelected = true
                menuVisible = true
                invalidateOptionsMenu()
            }
        }

//        bullet.setOnCheckedChangeListener { _, isChecked ->
//
//            if(isChecked){
//                val lines: Array<String> = note.split("\n").toTypedArray()
//                for (i in lines.indices){
//                    if(i == 0) {
//                        eNote.lines[i];
//                    } else if (TextUtils.isEmpty(lines[i].trim()) {
//                            eNote.setText(lines[i]);
//                        } else {
//                        pad.append("\n" + "\u2022" + "  " + lines[i]);
//                    }
//                }
//
//            }
//
//
//        }


    }
    private fun share(title: String, note: String){
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "")
        val shareMessage = title + "\n\n" + note

        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        startActivity(Intent.createChooser(shareIntent, "Share with..."))
    }
    private fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun resetView(){

        eTitle.clearFocus()
        eNote.clearFocus()
        menuVisible = false
        hideKeyboard()
        title = eTitle.text.toString()
        note = eNote.text.toString()
        invalidateOptionsMenu()
    }


    @SuppressLint("SetTextI18n")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.itemId

        if (id == R.id.save) {
            //save function
            val date = Calendar.getInstance().time
            val formatter = SimpleDateFormat("MM/dd/yyyy, hh:mm aaa")
            val dateText = formatter.format(date).toString()

            eDate.text = ("Modified: $dateText").toString()
            save()
            resetView()
            return true
        }
        else if(id == R.id.share){

            share(eTitle.text.toString(), eNote.text.toString())

        } else if (id == R.id.Delete) {
            val mainIntent = Intent(this, MainActivity::class.java)
            deleteList = trashDB.getAllNote()
            val noteModel = NoteModel(
                noteList[position].note,
                noteList[position].title,
                noteList[position].date,
                noteList[position].group
            )
            deleteList.add(noteModel)
            trashDB.addNote(
                noteList[position].note,
                noteList[position].title,
                noteList[position].date,
                deleteList.size
            )
            noteDB.deleteItem(position + 1)
            startActivity(mainIntent)

        } else if (id == R.id.fav) {
            bool = !bool
            if(bool) {
                boolean = 1
                item.icon = ContextCompat.getDrawable(this, R.drawable.fav_icon_1)

                noteDB.updateNote(
                    eNote.text.toString(), eTitle.text.toString(),
                    dateText, boolean, groupName, position + 1
                )
            } else {
                boolean = 0
                item.icon = ContextCompat.getDrawable(
                    this,
                    R.drawable.fav_icon_empty
                )
                noteDB.updateNote(
                    eNote.text.toString(), eTitle.text.toString(),
                    dateText, boolean, groupName, position + 1
                )
            }


        } else if (id == R.id.addToGroup) {

            save()

            val groupDB = GroupsDB(this, null)
            groupList = groupDB.getAllGroups()

            val checkedGroupItems = ArrayList<Int>()
            checkedGroupItems.add(position)

            val intent = Intent(this.applicationContext, AddToGroup::class.java)
            intent.putExtra("itemPosition", position)
            intent.putIntegerArrayListExtra("itemPositionList", checkedGroupItems)
            intent.putStringArrayListExtra("groupList", groupList)

            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)

    }
    private fun Activity.hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.findViewById<EditText>(R.id.eNote).windowToken, 0)
    }
    override fun onSupportNavigateUp():Boolean{
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val menuItem = menu?.findItem(R.id.save)
        val favItem = menu?.findItem(R.id.fav)
        menuItem?.isVisible = menuVisible


        if (noteDB.getFav(position + 1) == 1) {
                favItem!!.icon = ContextCompat.getDrawable(
                    this,
                    R.drawable.fav_icon_1
                )
                bool = true

            }

        return true
    }

    override fun onBackPressed() {

        if(eTitle.isFocused || eNote.isFocused){
            resetView()

        }else if(frag != 3){
            val mainIntent = Intent(this, MainActivity::class.java)
            save()
            mainIntent.putExtra("note", note)
            mainIntent.putExtra("title", title)
            mainIntent.putExtra("bool", boolean)
            mainIntent.putExtra("date", dateText)
            mainIntent.putExtra("group", groupName)
            mainIntent.putExtra("position", position)
            Log.d("position2", position.toString())
            setResult(Activity.RESULT_OK, mainIntent)
            finish()
        }
        else{
            save()
            groupList = groupsDB.getAllGroups()

            val mainIntent = Intent(this, GroupItemsList::class.java)
            mainIntent.putExtra("group", groupName)
            Log.d("position232", groupName)

            setResult(Activity.RESULT_OK, mainIntent)
            finish()
        }
        super.onBackPressed()
    }
    private fun save(){


        date = Calendar.getInstance().time
        val groupList = noteDB.getGroup(groupName)
        val notes = noteDB.getAllNote()
        noteDB.updateNote(
            eNote.text.toString(), eTitle.text.toString(),
            dateText, boolean, groupName, notes.indexOf(groupList[position]) + 1
        )
        Log.d("updatePos", (notes.indexOf(groupList[position]) + 1).toString())
//        notedbHandler.updateGroup(groupName, position + 1)
    }
}