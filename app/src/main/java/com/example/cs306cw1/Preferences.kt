package com.example.cs306cw1


import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * initialises Preferences activity
 */
class Preferences: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.preferences)

        //Access database
        val mDatabase = SqliteDatabase(this)
        val mutableFilters: MutableList<Filter> = mDatabase.listFilters()
        val mAdapter = filterAdapter(mutableFilters, mDatabase)

        //initialise layout
        val filterView = findViewById<RecyclerView>(R.id.newsFilters)
        val linearLayoutManager = LinearLayoutManager(this)
        filterView.layoutManager = linearLayoutManager
        filterView.adapter = mAdapter


        val addBtn = findViewById<Button>(R.id.addBtn)
        addBtn.setOnClickListener{
            addFilterDialog( mAdapter)
        }

        //swipe to delete
        val simpleItemTouchHelper=
            object :
                ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val position = viewHolder.adapterPosition
                        mAdapter.removeItem(position)
                    }
            }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchHelper)
        itemTouchHelper.attachToRecyclerView(filterView)
    }

    /**
     * Open dialogue that allows user to enter a filter
     * @param mAdapter adpater for the filter recyclerView
     */
    private fun addFilterDialog(mAdapter: filterAdapter){
        val inflater = LayoutInflater.from(this)
        val addFilterView = inflater.inflate(R.layout.add_filter_layout, null)
        val filterNameField = addFilterView.findViewById(R.id.enterFilter) as EditText
        //creates dialogue
        val builder = AlertDialog.Builder(this)
            .setTitle("")
            .setView(addFilterView)
        builder.create()
        builder.setPositiveButton("Add") {_, _ ->
            val name = filterNameField.text.toString()
            if (!TextUtils.isEmpty(name)){
                //adds item to database
                mAdapter.addItem(name)
            }
        }
        builder.setNegativeButton("Cancel") {_, _ ->
        }
        builder.show()
    }


}
