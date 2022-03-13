package com.example.cs306cw1

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

//Displays the articles saved by the user
class Favourites: AppCompatActivity() {

    /**
     * Initialises activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favourites)
        //gets the shared preference file of the current user
        val user = FirebaseAuth.getInstance().currentUser?.uid
        val pref = getSharedPreferences("$user", Context.MODE_PRIVATE)
        var favArticlesList = getFavouriteArticles(pref)

        //Set up layout
        val recyclerView = findViewById<View>(R.id.favArticles) as RecyclerView
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        val mAdapter = FavAdapter(favArticlesList)
        recyclerView.adapter = mAdapter

        val btnClear = findViewById<Button>(R.id.btnClear)
        btnClear.setOnClickListener {
            mAdapter.removeAllItems(pref)
        }


    }

    /**
     * Gets values from the shared preferences file and add to MutableList
     * @param pref shared preferences file
     * @return MutableList<Set<String>> array of set<String>
     */
    private fun getFavouriteArticles(pref: SharedPreferences): MutableList<Set<String>>{
        var counter = pref.getInt("counter", 1)
        var favArray: MutableList<Set<String>> = mutableListOf()

        //Iterates through the shared preferences file
        for (i in 1 .. counter){
            var article = pref.getStringSet("Article$i", null)
            if (article != null) {
                favArray.add(article)
            }

        }

        return favArray
    }


}