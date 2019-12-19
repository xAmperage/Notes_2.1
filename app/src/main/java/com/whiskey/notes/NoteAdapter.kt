package com.whiskey.notes


import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.whiskey.notes.com.whiskey.notes.NotesDbHelper
import com.whiskey.notes.com.whiskey.notes.TitlesDbHelper
import com.whiskey.notes.com.whiskey.notes.dateDbHelper
import kotlinx.android.synthetic.main.note_row_item.view.*
import kotlin.collections.ArrayList
@RequiresApi(Build.VERSION_CODES.N)
class NoteAdapter(
    var notes: ArrayList<String>,
    var titles: ArrayList<String>,
    var bDelete: Button,
    var deleteAll: CheckBox,
    var buttonLayout: ConstraintLayout,
    var fab: FloatingActionButton,
    var dates: ArrayList<String>,
    var context: Context,
    var notedbHandler: NotesDbHelper,
    var titleDbHandler: TitlesDbHelper,
    var dateDbHandler: dateDbHelper
)
    : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
    var checkedItems= ArrayList<Int>()
    private var checkedVisible = false
    private var isAllChecked = false

    override fun getItemCount() = notes.size

    fun HideItems(){
        checkedVisible = false
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val customView = layoutInflater.inflate(R.layout.note_row_item, parent, false)

        return NoteViewHolder(customView)

    }
    //Load items from database into adapter
    fun setItems(list: ArrayList<String>, titlesList: ArrayList<String>) {
        notes.addAll(list)
        titles.addAll(titlesList)
    }


    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val itemNote = notes[position]
        val itemTitle = titles[position]

        val dateText = dates[position]
        holder.customView.dateText.text = dateText
        holder.customView.itemTitle.text = itemTitle
        holder.customView.itemNote.text = itemNote
        if (notes.size != 0) {

            //Delete all items
            deleteAll.setOnClickListener {
                if (!deleteAll.isSelected){
                    checkedItems.clear()
                    SelectAll()
                    for(i in 0 until notes.size){
                        checkedItems.add(i)
                    }
                    Log.d("itemAddedAll", checkedItems.size.toString())
                }
                else {
                    unSelectAll()
                    checkedItems.clear()
                    Log.d("itemsCleared", checkedItems.size.toString())
                }
            }
            // Add checked check boxes to array to delete checked items

            holder.customView.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->

                Log.d("itemChecked", isChecked.toString())
                Log.d("itemNotesSize", notes.size.toString())

                if (isChecked && !deleteAll.isChecked) {

                    checkedItems.add(position)
                    Log.d("itemAdded", position.toString())


                }
                else if(!isChecked){
                    if(position < checkedItems.size) {
                        deleteAll.isChecked = false
                        checkedItems.removeAt(position)
                        Log.d("itemRemoved", position.toString())
                    }
                }
                Log.d("itemsChecked", checkedItems.toString())
                Log.d("itemDeleteCheckState", deleteAll.isChecked.toString())

            }


            holder.customView.checkBox.setOnClickListener {
                deleteAll.isSelected = false

                deleteAll.isChecked = false

            }
            deleteAll.isSelected = isAllChecked
            deleteAll.isChecked = isAllChecked

            //DO NOT TOUCH
            holder.customView.checkBox.isChecked = isAllChecked

            // Delete items
            bDelete.setOnClickListener {

                if (deleteAll.isChecked){

                    DeleteAll(holder.customView, deleteAll, bDelete)

                }else {

                    DeleteItems(holder.customView, deleteAll, bDelete)

                }


            }
            if(notes.size == 0){
                checkedVisible = false
            }
            if(bDelete.visibility == View.GONE){
                holder.customView.checkBox.visibility = View.GONE
                holder.customView.button.visibility = View.GONE
                buttonLayout.visibility = View.GONE

            }
            //Show or hide check boxes
            if(checkedVisible) {
                holder.customView.checkBox.visibility = View.VISIBLE
                bDelete.visibility = View.VISIBLE
                holder.customView.button.visibility = View.VISIBLE
                deleteAll.visibility = View.VISIBLE
                buttonLayout.visibility = View.VISIBLE
                fab.isVisible = false


            }
            else if(!checkedVisible) {
                holder.customView.checkBox.visibility = View.GONE
                bDelete.visibility = View.GONE
                holder.customView.button.visibility = View.GONE
                deleteAll.visibility = View.GONE
                fab.isVisible = true

            }


            holder.customView.setOnClickListener {
                val intent = Intent(holder.customView.context, ViewNoteActivity::class.java)
                holder.customView.checkBox.visibility = View.GONE
                holder.customView.button.visibility = View.GONE
                bDelete.visibility = View.GONE
                checkedItems.clear()
                intent.putExtra("title", titles[position])
                intent.putExtra("note", notes[position])
                intent.putExtra("position", position)
                intent.putStringArrayListExtra("notes", notes)
                intent.putStringArrayListExtra("titles", titles)
                intent.putStringArrayListExtra("dates", dates)
                startActivity(holder.customView.context, intent, null)

                notifyDataSetChanged()
            }
        }


    }

    fun SelectAll(){
        isAllChecked = true
        notifyDataSetChanged()
    }
    fun unSelectAll(){
        isAllChecked = false
        notifyDataSetChanged()
    }
    fun DeleteAll(view:View,delete: CheckBox, btn: Button){
        val dialogBuilder =
            AlertDialog.Builder(view.context, R.style.MyDialogTheme)

        // set message of alert dialog
        dialogBuilder.setMessage("Do you want to delete the selected notes?")

            .setCancelable(false)
            .setPositiveButton("Yes") {
                    dialog, id-> dialog.dismiss()
                notes.clear()
                titles.clear()
                dates.clear()
                checkedItems.clear()
                notedbHandler.deleteAll()
                titleDbHandler.deleteAll()
                dateDbHandler.deleteAll()
                checkedVisible = false
                HideItems()
                delete.visibility = View.GONE
                btn.visibility = View.GONE
                buttonLayout.visibility = View.GONE
                fab.isVisible = true
                deleteAll.isSelected = false
                unSelectAll()
                deleteAll.isChecked = false
                notifyDataSetChanged()

            }
            .setNegativeButton("No") { dialog, id ->
                dialog.cancel()
            }
        val alert = dialogBuilder.create()
        alert.show()
    }

    fun DeleteItems(view: View, delete: CheckBox, btn: Button){
        val dialogBuilder =
            AlertDialog.Builder(view.context, R.style.MyDialogTheme)

        // set message of alert dialog
        dialogBuilder.setMessage("Do you want to delete the selected notes?")

            .setCancelable(false)
            .setPositiveButton("Yes") {
                    dialog, id-> dialog.dismiss()

                for (i in 0 until checkedItems.size){
                    if(i == 0 ||checkedItems[i] == 0 ) {
                        notedbHandler.DeleteItem(notes[checkedItems[i]])
                        dateDbHandler.DeleteItem(dates[checkedItems[i]])
                        titleDbHandler.DeleteItem(titles[checkedItems[i]])
                        notes.removeAt(checkedItems[i])
                        titles.removeAt(checkedItems[i])
                        dates.removeAt(checkedItems[i])


                        Log.d("itemDeleted", notes[checkedItems[i]].toString())
//                    notifyItemRemoved(position)
                    }
                    else{
                        notedbHandler.DeleteItem(notes[checkedItems[i]-1])
                        dateDbHandler.DeleteItem(dates[checkedItems[i]-1])
                        titleDbHandler.DeleteItem(titles[checkedItems[i]-1])
                        notes.removeAt(checkedItems[i]-1)
                        titles.removeAt(checkedItems[i]-1)
                        dates.removeAt(checkedItems[i]-1)

                        Log.d("itemDeleted", checkedItems[i].toString())
                    }
                }
                checkedVisible = false
                checkedItems.clear()
                HideItems()
                delete.visibility = View.GONE
                btn.visibility = View.GONE
                buttonLayout.visibility = View.GONE
                fab.isVisible = true
                deleteAll.isSelected = false
                unSelectAll()
                deleteAll.isChecked = false

                notifyDataSetChanged()
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.cancel()
            }
        val alert = dialogBuilder.create()
        alert.show()
    }

    inner class NoteViewHolder(val customView: View) : RecyclerView.ViewHolder(customView),
        View.OnLongClickListener, View.OnClickListener{


        init {

            customView.isLongClickable = true
            customView.setOnLongClickListener(this)
            customView.setOnClickListener(this)
//            customView.findViewById(R.id.checkBox)
//            customView.findViewById(R.id.button)

        }
        override fun onLongClick(v: View?): Boolean {
            //Changed the state of check box visibility
            checkedVisible = true
            notifyDataSetChanged()
            return true
        }

        override fun onClick(v: View?) {
            //Changed the state of check box visibility

            if(checkedVisible){
                checkedVisible = false
                notifyDataSetChanged()
            }

        }


    }




}


