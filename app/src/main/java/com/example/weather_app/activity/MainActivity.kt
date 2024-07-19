package com.example.weather_app.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import com.example.weather_app.R
import com.example.weather_app.model.WeatherApp
import com.example.weather_app.databinding.ActivityMainBinding
import com.example.weather_app.retrofit.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// API KEY: 6175c9b562da1c0621cd5952009f4625

val BaseURL = "https://api.openweathermap.org/data/2.5/"
val API_KEY = "6175c9b562da1c0621cd5952009f4625"

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Ha Noi")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BaseURL)
            .build().create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(cityName, API_KEY, "metric")
        response.enqueue(object :Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null){
                    val temperature = responseBody.main.temp.toInt().toString()
                    val humidity = responseBody.main.humidity.toString()
                    val windSpeed = responseBody.wind.speed.toString()
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure.toString()
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxTemp = responseBody.main.temp_max.toInt().toString()
                    val minTemp = responseBody.main.temp_min.toInt().toString()


                    binding.temp.text = "$temperature℃"
                    binding.maxTemp.text = "Max Temp: $maxTemp℃"
                    binding.minTemp.text = "Min Temp: $minTemp℃"
                    binding.weather.text = condition
                    binding.textHumidity.text = "${humidity}%"
                    binding.textWinSpeed.text = "${windSpeed}m/s"
                    binding.textSunrise.text = "${time(sunRise)}"
                    binding.textSunset.text = "${time(sunSet)}"
                    binding.textSea.text = "${seaLevel}hPa"
                    binding.textConditions.text = condition
                    binding.day.text =_day(System.currentTimeMillis())
                    binding.date.text =_date()
                    binding.cityName.text = "$cityName"


                    //Log.d("TAG", "onResponse: $temperature")
                    changeImageAccordingToWeatherCondition(condition)
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun changeImageAccordingToWeatherCondition(conditions:String) {
        when(conditions){
            "Clear Sky", "Sunny", "Clear" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }

            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.snow)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun _date(): CharSequence? {
        val sdf = SimpleDateFormat("dd MMMM yyyy ", Locale.getDefault())
        return sdf.format((Date()))
    }
    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm ", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }

    fun _day(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }

}