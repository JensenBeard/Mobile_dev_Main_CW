package com.example.cs306cw1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class filterAdapter(var listFilters: MutableList<Filter>, private val mDatabase: SqliteDatabase): RecyclerView.Adapter<filterAdapter.ViewHolder>(){

    /**
     * Adds filter to database
     * @param nameFilter add object to database
     */
    fun addItem(nameFilter: String){
        mDatabase.addFilter(nameFilter)
        listFilters.clear()
        listFilters.addAll(mDatabase.listFilters())
        notifyDataSetChanged()
    }

    /**
     * Removes filter from database
     * @param position id of value to be removed
     */
    fun removeItem(position: Int){
        if (position < listFilters.size){
            val filter = listFilters[position]
            mDatabase.deleteFilter(filter.id)
            listFilters.removeAt(position)
            notifyItemRemoved(position)
        }
    }
    /**
     * initialises adapter
     * @param parent
     * @param viewType
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.filter_layout, parent, false)
        return ViewHolder(v)
    }
    /**
     * Updates displays after initialisation
     * @param holder the items viewholder
     * @param position position of the item in the array
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //sets the viewholder items.
        val singleItem = listFilters[position]
        holder.filterTitle.text = singleItem.name
    }
    /**
     * Get item count
     * @return item count in array
     */
    override fun getItemCount(): Int {
        return listFilters.size
    }
    /**
     * Viewholder
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var filterTitle : TextView = itemView.findViewById(R.id.filterTitle)

    }
}
