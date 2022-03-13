package com.example.cs306cw1

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.koushikdutta.ion.Ion
import kotlinx.android.synthetic.main.fragment_one.*
import org.json.JSONObject
import java.util.*


class FragmentThree(location: String) : Fragment() {
    private var countryCode :String = location
    /**
     * Initialise view
     * @return inflater inflating the fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_three, container, false)

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
         //takes the country code as a search param to get the top headlines from that country
        var searchVal = countryCode
         Log.i("countryCode", searchVal)
        Ion.with(this)
            .load(
                "GET",
                "http://newsapi.org/v2/top-headlines?country=$countryCode&apiKey=3d40983666424488b7056970fe17144c"
            )
            .setHeader("user-agent", "insomnia/2020.4.1")
            .asString()
            .setCallback { ex, result ->
                val articleList = JSONObject(result)
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