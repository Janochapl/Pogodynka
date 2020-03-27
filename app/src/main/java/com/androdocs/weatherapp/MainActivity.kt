package com.androdocs.weatherapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    var CITY: String = "katowice,pl"
    val API: String = "8118ed6ee68db2debfaaa5a44c832918"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inlocalization.text.clear()
        weatherTask().execute()

        btn.setOnClickListener{
            val intent = Intent(this, DetailsActivity::class.java)
            startActivity(intent)
        }
        inlocalization.setOnClickListener{
            myEnter()
            //hideKeyboard()
        }


    }

    fun myEnter(){

        inlocalization.setOnKeyListener(View.OnKeyListener{v, keyCode, event->
            if(keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP){

                CITY = inlocalization.text.toString()
                inlocalization.text.clear()
                hideKeyboard()
                weatherTask().execute()
                return@OnKeyListener true

            }
            false
        })
    }


    fun hideKeyboard(){

        val view = this.currentFocus
        if(view!=null){

            val hideMe = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            hideMe.hideSoftInputFromWindow(view.windowToken, 0)

        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    @SuppressLint("StaticFieldLeak")
    inner class weatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            /* Showing the ProgressBar, Making the main design GONE */
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String? {
            var response:String?
            try{
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API").readText(
                    Charsets.UTF_8
                )
            }catch (e: Exception){
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                /* Extracting JSON returns from the API */
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val updatedAt:Long = jsonObj.getLong("dt")
                val temp = main.getString("temp").substringBefore(".") +"°C"
                val icon = weather.getString("icon")
                val tempMin = "Min Temp: " + main.getString("temp_min").substringBefore(".")+"°C"
                val tempMax = "Max Temp: " + main.getString("temp_max").substringBefore(".")+"°C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")

                val sunrise:Long = sys.getLong("sunrise")
                val sunset:Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")
                val weatherDescription = weather.getString("description")

                val address = jsonObj.getString("name")+", "+sys.getString("country")

                /* Populating extracted data into our views */
                when(icon){
                    "01d" -> findViewById<ImageView>(R.id.imageView).setImageResource(R.drawable.i01d)
                    "01n" -> findViewById<ImageView>(R.id.imageView).setImageResource(R.drawable.i01n)
                    "02d" -> findViewById<ImageView>(R.id.imageView).setImageResource(R.drawable.i02d)
                    "02n" -> findViewById<ImageView>(R.id.imageView).setImageResource(R.drawable.i02n)
                    "03d" -> findViewById<ImageView>(R.id.imageView).setImageResource(R.drawable.i03d)
                    "03n" -> findViewById<ImageView>(R.id.imageView).setImageResource(R.drawable.i03n)
                    "04d" -> findViewById<ImageView>(R.id.imageView).setImageResource(R.drawable.i04d)
                    "04n" -> findViewById<ImageView>(R.id.imageView).setImageResource(R.drawable.i04n)
                    "09d" -> findViewById<ImageView>(R.id.imageView).setImageResource(R.drawable.i09d)
                    "09n" -> findViewById<ImageView>(R.id.imageView).setImageResource(R.drawable.i09n)
                    "10d" -> findViewById<ImageView>(R.id.imageView).setImageResource(R.drawable.i10d)
                    "10n" -> findViewById<ImageView>(R.id.imageView).setImageResource(R.drawable.i10n)
                    "11d" -> findViewById<ImageView>(R.id.imageView).setImageResource(R.drawable.i11d)
                    "11n" -> findViewById<ImageView>(R.id.imageView).setImageResource(R.drawable.i11n)
                    "13d" -> findViewById<ImageView>(R.id.imageView).setImageResource(R.drawable.i13d)
                    "13n" -> findViewById<ImageView>(R.id.imageView).setImageResource(R.drawable.i13n)
                }
                findViewById<TextView>(R.id.address).text = address
                findViewById<TextView>(R.id.temp).text = temp
                findViewById<TextView>(R.id.temp_min).text = tempMin
                findViewById<TextView>(R.id.temp_max).text = tempMax

                /* Views populated, Hiding the loader, Showing the main design */
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE

            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
            }

        }
    }
}
