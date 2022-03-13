package com.example.cs306cw1


import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.koushikdutta.ion.Ion
import kotlinx.android.synthetic.main.fragment_one.*
import kotlinx.android.synthetic.main.search_story.*
import org.json.JSONObject
import java.util.*


class FragmentOne : Fragment() {
    //initialises search val
    private var searchVal = "News"
    //Speech recognition code
    private val RQ_SPEECH_REC = 102

    /**
     * Initialise view
     * @return inflater inflating the fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_one, container, false)

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

            //opens search dialogue
            fab.setOnClickListener{
                searchDialog(mAdapter)
            }

            //opens google voice recognition
            voiceBtn.setOnClickListener{
                askSpeechInput()
            }
        }


    }

    /**
     * Uses Ion to query newsAPI to populate a MutableList<MyModel>
     * @return callback the result of the Ion query callback
     */
    private fun populateList(callback: (MutableList<MyModel>) -> Unit) {
        val finalList = mutableListOf<MyModel>()

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

    /**
     * Create dialogue for the user to enter search terms
     * @param mAdapter adapter to refresh recyclerView
     */
    private fun searchDialog(mAdapter: MyAdapter){
        val inflater = LayoutInflater.from(requireContext())
        val addFilterView = inflater.inflate(R.layout.search_story, null)
        val searchItemField = addFilterView.findViewById(R.id.enterSearch) as EditText

        //creates dialogue
        val builder = AlertDialog.Builder(requireContext())
            .setTitle("")
            .setView(addFilterView)
        builder.create()
        builder.setPositiveButton("Search") { _, _ ->
            val name = searchItemField.text.toString()
            if (!TextUtils.isEmpty(name)){
                searchVal = name
                refreshAction(mAdapter)
            }
        }
        builder.setNegativeButton("Cancel") { _, _ ->
        }
        builder.show()
    }

    /**
     * Calls googles speech intent for user input
     */
    private fun askSpeechInput(){
        if(!SpeechRecognizer.isRecognitionAvailable(requireContext())){
            Toast.makeText(requireContext(), "Speech recognition unavailable", Toast.LENGTH_SHORT).show()
        } else {
            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something!")
            startActivityForResult(i, RQ_SPEECH_REC)
        }
    }

    /**
     * Receives result from user search intent
     * @param requestCode
     * @param resultCode
     * @param data data returned from the speech input
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RQ_SPEECH_REC && resultCode == Activity.RESULT_OK){
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            searchVal = result?.get(0).toString()
        }
    }
}