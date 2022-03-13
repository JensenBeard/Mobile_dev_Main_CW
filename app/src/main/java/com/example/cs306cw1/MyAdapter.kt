package com.example.cs306cw1

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class MyAdapter(private val imageModelArrayList: MutableList<MyModel>): RecyclerView.Adapter<MyAdapter.ViewHolder>(){

    /**
     * initialises adapter
     * @param parent
     * @param viewType
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.row_layout, parent, false)
        return ViewHolder(v)
    }

    /**
     * Updates displays after initialisation
     * @param holder the items viewholder
     * @param position position of the item in the array
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = imageModelArrayList[position]
        //passes values from the MyModel object in the imageModelArrayList to the viewHolder
        holder.url = info.getURL()
        //Sets image URL using picasso
        Picasso.get().load(info.getImages()).into(holder.imgView)
        holder.titleText.text = info.getNames()
    }

    /**
     * Get item count
     * @return item count in array
     */
    override fun getItemCount(): Int {
        return imageModelArrayList.size
    }

    /**
     * Viewholder
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
        var imgView : ImageView = itemView.findViewById(R.id.imageView)
        var titleText : TextView = itemView.findViewById(R.id.titleText)
        var url : String = ""
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
            //Passes url and title through to load the article.
            intent.putExtra("url",url)
            intent.putExtra("title", titleText.text)
            context.startActivity(intent)
        }
    }

}