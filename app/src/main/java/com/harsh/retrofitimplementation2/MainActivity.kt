package com.harsh.retrofitimplementation2

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.harsh.retrofitimplementation2.adapter.UserAdapter
import com.harsh.retrofitimplementation2.databinding.ActivityMainBinding
import com.harsh.retrofitimplementation2.databinding.CustomDialogTosendApiDataBinding
import com.harsh.retrofitimplementation2.dataclasses.GetApiResponse
import com.harsh.retrofitimplementation2.dataclasses.GetApiResponseItem
import com.harsh.retrofitimplementation2.interfaces.RetrofitInterface
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var retrofit: Retrofit? = null
    private val TAG = "MainActivity"
    var apiInterface: RetrofitInterface? = null
    var userAdapter: UserAdapter? = null
    var isLoading = false
    var page = 0
    var perPage = 10
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userAdapter = UserAdapter()
        binding.rvRecyclerView.layoutManager = LinearLayoutManager(this)

        retrofit = Retrofit.Builder()
            .baseUrl("https://gorest.co.in/public/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiInterface = retrofit?.create(RetrofitInterface::class.java)
        binding.btnGetApi.setOnClickListener() {
            binding.pbProgressBar.visibility = View.VISIBLE
            GlobalScope.launch {
                getApiData()
//                hitPaginationApi()
            }

        }
        binding.btnSingleUser.setOnClickListener{
            getSingleUser()
        }

        binding.fabAdd.setOnClickListener {
            showCustomDialog()
        }
        binding.rvRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val visibleItemCount : Int = (binding.rvRecyclerView.layoutManager as LinearLayoutManager).childCount
                val pastVisibleItem : Int = (binding.rvRecyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                val total = userAdapter?.itemCount
                if (!isLoading){
                    if (visibleItemCount + pastVisibleItem >= total ?:0){
                        hitPaginationApi()
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })

    }

    private fun getSingleUser() {
        binding.pbProgressBar.visibility = View.VISIBLE
        apiInterface?.getSingleUser()?.enqueue(object : Callback<GetApiResponseItem>{
            override fun onResponse(
                call: Call<GetApiResponseItem>,
                response: Response<GetApiResponseItem>
            ) {
                Log.e("responze",response.body().toString())
                response.body().let { item ->
                    val itemList = mutableListOf<GetApiResponseItem>()
                    item?.let {
                        itemList.add(it)
                    }
                    itemList.addAll(
                        userAdapter?.dataList ?: emptyList()
                    )
                    userAdapter = UserAdapter(itemList)
                }
                binding.rvRecyclerView.adapter = userAdapter
                binding.pbProgressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<GetApiResponseItem>, t: Throwable) {
               Log.e("error",t.toString())
            }
        })
    }

    private fun getApiData() {
        apiInterface?.getApiResponse()?.enqueue(object : Callback<List<GetApiResponseItem>> {
            override fun onResponse(
                call: Call<List<GetApiResponseItem>>,
                response: Response<List<GetApiResponseItem>>
            ) {

                val responseBody = response.body()
                responseBody?.let {
                    userAdapter = UserAdapter(it)

                    binding.rvRecyclerView.adapter = userAdapter
                    binding.pbProgressBar.visibility = View.GONE
                }
                Log.e(TAG, "Api response $responseBody")
            }

            override fun onFailure(call: Call<List<GetApiResponseItem>>, t: Throwable) {

            }
        })

    }

    private fun hitPaginationApi() {
        isLoading = true
        apiInterface?.getUsersPerPage(page, perPage)
            ?.enqueue(object : Callback<GetApiResponse> {
                override fun onResponse(
                    call: Call<GetApiResponse>,
                    response: Response<GetApiResponse>
                ) {
                    page++
                    Log.e(TAG, " pagination response ${response.body()}")
                    var responseBody = response.body()
                    responseBody.let {
                        userAdapter = UserAdapter(it as List<GetApiResponseItem>)

                    }
                    binding.rvRecyclerView.adapter = userAdapter
                    binding.pbProgressBar.visibility = View.GONE

                }

                override fun onFailure(call: Call<GetApiResponse>, t: Throwable) {
                }
            })
        isLoading = false
    }

    private fun showCustomDialog() {
        val dialogView = CustomDialogTosendApiDataBinding.inflate(layoutInflater)
        var dialog = Dialog(this)
        dialog.setContentView(dialogView.root)
        dialog.show()

        dialogView.buttonSubmit.setOnClickListener {
            if (dialogView.editTextName.text.isNullOrEmpty()) {
                dialogView.editTextName.setError("Enter Name")
            } else if (dialogView.editTextEmail.text.isNullOrEmpty()) {
                dialogView.editTextEmail.setError("Enter Email")
            } else {
                var selectedGender = if (dialogView.radioButtonMale.isSelected)
                    "Male"
                else
                    "Female"
                var isActive = if (dialogView.checkBox.isChecked)
                    "active"
                else
                    "inactive"
                apiInterface?.postUser(
                    "Bearer 4acb0abb665fcc30ba77fff2f96f2a876c330a1e2c135a9d53e4879d06572f44",
                    dialogView.editTextEmail.text.toString(),
                    dialogView.editTextName.text.toString(),
                    selectedGender,
                    isActive
                )?.enqueue(object : Callback<GetApiResponseItem> {
                    override fun onResponse(
                        call: Call<GetApiResponseItem>,
                        response: Response<GetApiResponseItem>
                    ) {
                        Log.e(TAG, "response body of put${response.body()}")
                        if (response.isSuccessful) {
                            response.body().let { item ->
                                val itemList = mutableListOf<GetApiResponseItem>()
                                item?.let {
                                    itemList.add(it)
                                }
                                itemList.addAll(
                                    userAdapter?.dataList ?: emptyList()
                                )
                                userAdapter = UserAdapter(itemList)
                            }


                            binding.rvRecyclerView.adapter = userAdapter
                            AlertDialog.Builder(this@MainActivity).apply {
                                setTitle("Added")
                                show()

                            }
                            dialog.dismiss()
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Response Unsuccesful",
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                    }

                    override fun onFailure(call: Call<GetApiResponseItem>, t: Throwable) {
                        Toast.makeText(this@MainActivity, "Response Failed $t", Toast.LENGTH_SHORT)
                            .show()
                    }

                })

            }
        }

    }
}


