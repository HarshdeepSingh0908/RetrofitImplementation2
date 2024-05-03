package com.harsh.retrofitimplementation2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.harsh.retrofitimplementation2.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var retrofit: Retrofit?= null
    private val TAG = "RetrofitActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
       binding.rvRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.btnGetApi.setOnClickListener(){
            binding.pbProgressBar.visibility = View.VISIBLE
            getApiData()
        }
        binding.fabAdd.setOnClickListener{
        showCustomDialog()
        }

    }

    private fun getApiData() {
        retrofit = Retrofit.Builder()
            .baseUrl("https://gorest.co.in/public/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiInterface = retrofit?.create(RetrofitInterface::class.java)
        apiInterface?.getApiResponse()?.enqueue(object : Callback<List<GetApiResponseItem>> {
            override fun onResponse(
                call: Call<List<GetApiResponseItem>>,
                response: Response<List<GetApiResponseItem>>
            ) {
                val responseBody = response.body()
                responseBody?.let {
                    val userAdapter = UserAdapter(it)

                    binding.rvRecyclerView.adapter = userAdapter
                    binding.pbProgressBar.visibility = View.GONE
                }
                Log.e(TAG, "Api response $responseBody")
            }
            override fun onFailure(call: Call<List<GetApiResponseItem>>, t: Throwable) {

            }
        })
    }

    private fun showCustomDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_tosend_api_data, null)
        android.app.AlertDialog.Builder(this)
            .setTitle("Add User")
            .setView(dialogView)
            .show()

    }
}

interface RetrofitInterface {
    @GET("users")
    fun getApiResponse(): Call<List<GetApiResponseItem>>
}
