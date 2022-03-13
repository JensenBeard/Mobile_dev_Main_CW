package com.example.cs306cw1

import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//Adapter for the recyclerView in the favourites class
//Takes a MutableList of Set<String>
class FavAdapter(var listFav: MutableList<Set<String>>): RecyclerView.Adapter<FavAdapter.ViewHolder>(){

    /**
     * initialises adapter
     * @param parent
     * @param viewType
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.fav_layout, parent, false)
        return ViewHolder(v)
    }

    /**
     * Updates displays after initialisation
     * @param holder the items viewholder
     * @param position position of the item in the array
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Breaks down the set into the title and URL
        val info = listFav[position]
        var title = info.elementAt(1)
        var url = info.elementAt(0)
        //sets to viewHolders
        holder.titleText.text= title
        holder.urlText.text= url
    }


    /**
     * Get item count
     * @return item count in array
     */
    override fun getItemCount(): Int {
        return listFav.size
    }

    /**
     * Clears the recyclerview and the shared pref file
     * @param pref shared preference file
     */
    fun removeAllItems(pref: SharedPreferences){
        //removes all values in the recyclerview
        var editor = pref.edit()
        listFav.clear()
        notifyDataSetChanged()
        //removes all items from the shared preference file
        editor.clear()
        editor.apply()
    }

    /**
     * Viewholder
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
        var urlText : TextView = itemView.findViewById(R.id.urlText)
        var titleText : TextView = itemView.findViewById(R.id.titleText)

        init{
            itemView.setOnClickListener(this)
        }

        /**
         * On cardView click open article
         * @param v CardView
         */
        override fun onClick(v: View?) {
            //opens article on click
            val context=v!!.context
            val intent = Intent( context, ArticlePage::class.java)
            intent.putExtra("url", urlText.text)
            intent.putExtra("title", titleText.text)
            context.startActivity(intent)
        }
    }

}