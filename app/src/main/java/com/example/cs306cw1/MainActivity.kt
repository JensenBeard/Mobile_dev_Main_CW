package com.example.cs306cw1

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

/**
 * Implements the Main Activity
 */

class MainActivity : AppCompatActivity() {

    //notification params
    private val CHANNEL_ID = "channel_id_example"
    private val notificationId = 101

    //Location params
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQ_CODE = 1000;
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var mCountryCode: String = "us"

    //Firebase User params
    var name: String? = null
    var userUri: Uri? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val v =findViewById<ConstraintLayout>(R.id.mainLayout)
        //Determines if there is a user logged in and notifies the user
        //only on initial startup
        val curUser = FirebaseAuth.getInstance().currentUser
        if(curUser == null){
            showMessage(v, getString(R.string.noLogIn))
        } else {
            showMessage(v, getString(R.string.userLoggedIn))
        }

        //initialise toolbar
        val mToolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(mToolbar)

        //set up location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocation()

        createNotificationChannel()

        setUpFragments()

    }

    /**
     * Creates the toolbar
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * Determines the actions taken when options in the toolbar are selected
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        val v =findViewById<ConstraintLayout>(R.id.mainLayout)
        if(id==R.id.action_logout){
            //signs out current user and opens login screen
            FirebaseAuth.getInstance().signOut()
            showMessage(v, getString(R.string.LogOut))
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        if(id==R.id.login){
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        if(id==R.id.favourites){
            val intent = Intent(this, Favourites::class.java)
            startActivity(intent)
        }

        if(id==R.id.action_pref){
            val intent = Intent(this, Preferences::class.java)
            startActivity(intent)
        }

        if(id==R.id.action_Profile){
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }

        if(id==R.id.notifications){
            randomNotification()
        }
        return super.onOptionsItemSelected(item)
    }


    /**
     * performs actions every time the activity is selected
     */
    override fun onStart() {
        super.onStart()
        /*
         * Gets the details of the current user logged in through firebase
         * Displays them appropriately
         * Reissues every time the activity is selected
         */
        val curUser = FirebaseAuth.getInstance().currentUser
        if (curUser != null){
            name = curUser.displayName
            userUri = curUser.photoUrl
            username.text = name
            Glide.with(this)
                .load(userUri)
                .into(profilePic)
        } else {
            Glide.with(this)
                .load(R.drawable.ic_baseline_face_24)
                .into(profilePic)
            username.text = getString(R.string.noUser)
        }

    }

    /**
     * Initialises the fragments assigning them names
     */
    private fun setUpFragments(){
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager>(R.id.pager)

        tabLayout!!.tabGravity = TabLayout.GRAVITY_CENTER
        val tabTitles = resources.getStringArray(R.array.tabTitles)

        val mAdapter = TabsPagerAdapter(supportFragmentManager, tabTitles, mCountryCode)
        viewPager.adapter = mAdapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab!!.position
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
        })


    }

    /**
     * Gets the users current location
     */
    private fun getCurrentLocation() {
        // checking location permission
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            // request permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQ_CODE
            );
            return
        }
        //Checks the last known location of the user as lat and long
        fusedLocationClient.lastLocation
            .addOnCompleteListener(this) { task ->
                val location: Location? = task.result
                if(location==null){
                    requestNewLocationData()
                } else {
                    latitude = location.latitude
                    longitude = location.longitude
                    Log.i("LatLongLocation", "$latitude and $longitude")
                    getCountry()
                }
                // getting the last known or current location
            }
    }

    /**
     * Uses Geocoder to access the country using the Latitude and longitude
     */
    private fun getCountry(){

        val gcd = Geocoder(this, Locale.getDefault())
        val addresses: List<Address> = gcd.getFromLocation(latitude, longitude, 1)

        //Gets the country code as lowercase to use in the newsAPI URL
        if (addresses.size > 0) {
            val countryName: String = addresses[0].countryCode
            mCountryCode = countryName.toLowerCase(Locale.ROOT)
            Log.i("Location", countryName)
        } else {
            Log.i("Location", "Not Registered")
        }


    }

    /**
     * If there is no location data new data needs to be requested
     */

    private fun requestNewLocationData(){
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            // request permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQ_CODE
            );
            return
        }
        //requesting location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback, Looper.myLooper()
        )
    }

    //Get Lat and Long of last known location
    private val mLocationCallback = object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            val mLastLocation: Location = p0.lastLocation
            latitude = mLastLocation.latitude
            longitude = mLastLocation.longitude
        }
    }

    /**
     * Initialise notification channel
     */
    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Notification Title"
            val desText = "Notification Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = desText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Generate random notification using the values in the preferences database
     */
    private fun randomNotification(){
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent,0)

        //query database
        val mDatabase = SqliteDatabase(this)
        val mutableFilters: MutableList<Filter> = mDatabase.listFilters()

        //generate random topic
        var notificationArr: MutableList<String> = arrayListOf()
        for(i in 0 until mutableFilters.size){
            notificationArr.add(mutableFilters[i].name)
        }
        var randInt = (0 until mutableFilters.size).shuffled().last()
        var randTopic = mutableFilters[randInt].name

        //build notification
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.notificationTitle) + randTopic)
            .setContentText(getString(R.string.notifdescription))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(this)){
            notify(notificationId, builder.build())
        }
        
    }

    /**
     * Creates snackbar showing displayed message
     * @param view view the message will be displayed on
     * @param message message to be displayed
     */
    private fun showMessage(view: View, message: String){
        Snackbar.make(view, message, Snackbar .LENGTH_SHORT).show()
    }
}