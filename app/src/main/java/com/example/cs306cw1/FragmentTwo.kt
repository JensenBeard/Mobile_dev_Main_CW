package com.example.cs306cw1

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.koushikdutta.ion.Ion
import kotlinx.android.synthetic.main.fragment_one.*
import org.json.JSONObject

class FragmentTwo : Fragment() {

    /**
     * Initialise view
     * @return inflater inflating the fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_two, container, false)

    }

    /**
     * Fills out the view
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //custom callback to get articles from newsAPI
        //Passes returned list to the adapter to set up recyclerView
        populateList() { returnedList ->
            val recyclerView = view.findViewById<View>(R.id.my_recycler_view) as RecyclerView
            val layoutManager = LinearLayoutManager(requireContext())
            recyclerView.layoutManager = layoutManager
            val mAdapter = MyAdapter(returnedList)
            recyclerView.adapter = mAdapter

            //refreshes recyclerView
            swipeRefresh.setOnRefreshListener {
                refreshAction(mAdapter)
                swipeRefresh.isRefreshing = false   // reset the SwipeRefreshLayout (stop the loading spinner)
            }
        }
    }

    /**
     * Uses Ion to query newsAPI to populate a MutableList<MyModel>
     * @return callback the result of the Ion query callback
     */
    private fun populateList(callback: (MutableList<MyModel>) -> Unit) {
        val finalList = mutableListOf<MyModel>()
        var searchVal: String = ""

        //Queries database to get filters
        val mDatabase = SqliteDatabase(requireContext())
        val mutableFilters: MutableList<Filter> = mDatabase.listFilters()

        //uses filters to create query string for newsAPI using url formatting
        if(mutableFilters.isEmpty()){
            searchVal = "News"
        } else {
            for(i in 1 .. mutableFilters.size){
                val name = mutableFilters[i-1].name
                if(searchVal.isEmpty()){
                    searchVal = mutableFilters[0].name
                } else {
                    searchVal += "%20OR%20$name"
                }
            }
            Log.i("Search", searchVal)
        }

        Ion.with(this)
            .load(
                "GET",
                "http://newsapi.org/v2/everything?q=$searchVal&sortBy=popularity&apiKey=3d40983666424488b7056970fe17144c"
            )
            .setHeader("user-agent", "insomnia/2020.4.1")
            .asString()
            .setCallback { ex, result ->
                Log.i("output", "got result")
                val articleList = JSONObject(result)
                Log.i("articlelist", articleList.toString())
                val myArticle = articleList.getJSONArray("articles")

                for (i in 0..18) {
                    //adds article contents to a NyModel Object
                    val imageModel = MyModel()
                    val myString = myArticle.getJSONObject(i).getString("title")
                    val myImageURL = myArticle.getJSONObject(i).getString("urlToImage")
                    val URL = myArticle.getJSONObject(i).getString("url")
                    imageModel.setNames(myString)
                    imageModel.setImages(myImageURL)
                    imageModel.setURL(URL)
                    //add object to List
                    finalList.add(imageModel)
                }
                callback(finalList)
            }
    }

    /**
     * Refreshes the fragment and recyclerview
     * @param mAdapter adpater for refreshing the recycler view
     */
    private fun refreshAction(mAdapter: MyAdapter) {
        //refreshes the fragment
        val ft: FragmentTransaction = fragmentManager!!.beginTransaction()
        ft.detach(this).attach(this).commit()
        //updates recyclerView
        mAdapter.notifyDataSetChanged()
    }
}