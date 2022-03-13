package com.example.cs306cw1

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.article_layout.*

/**
 * Implements the Article page
 * Displays the article url of the clicked on article
 */

class ArticlePage: AppCompatActivity() {
    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.article_layout)
        //Receiving intent extras
        var articleURL = intent.getStringExtra("url")
        var articleTitle = intent.getStringExtra("title")
        var articleNum = 0
        webview.webViewClient = MyWebViewClient()
        webview.loadUrl("$articleURL")

        //Assigns user a personal Shared Preference
        val user = FirebaseAuth.getInstance().currentUser?.uid
        val pref = getSharedPreferences("$user",Context.MODE_PRIVATE)
        val editor = pref.edit()
        //Initialises Article counter
        editor.putInt("counter", articleNum)

        val btnFav = findViewById<ImageButton>(R.id.btnFav)
        //Adds current Article to favourites
        btnFav.setOnClickListener {
            if (articleURL != null) {
                if (articleTitle != null) {
                    addFavourite(pref, articleURL, articleTitle)
                }
            }
        }


    }

    /**
     * Adds selected article to favourites
     * @param pref holds the users shared preference file
     * @param articleURL url of the article
     * @param articleTitle title of the article
     */

    private fun addFavourite(pref: SharedPreferences, articleURL: String, articleTitle: String){
        val fullArticle = mutableSetOf(articleURL,articleTitle )
        val editor = pref.edit()
        //Get counter
        var counter = pref.getInt("counter", 0)
        counter++
        editor.putInt("counter", counter)
        //save URL under Article + counter value
        editor.putStringSet("Article$counter", fullArticle)
        editor.apply()
        val v =findViewById<LinearLayout>(R.id.articleLayout)
        showMessage(v, getString(R.string.addToFav))
    }

    /**
     * Creates snackbar showing displayed message
     * @param view view the message will be displayed on
     * @param message message to be displayed
     */
    private fun showMessage(view: View, message: String){
        Snackbar.make(view, message, Snackbar .LENGTH_SHORT).show()
    }

    /**
     * Creates custom webView client for in app usage
     */
    class MyWebViewClient : WebViewClient(){

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            view?.loadUrl(request?.url.toString())
            return true
        }
    }


}