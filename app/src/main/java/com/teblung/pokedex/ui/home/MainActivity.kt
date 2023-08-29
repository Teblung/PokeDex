package com.teblung.pokedex.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.teblung.pokedex.R
import com.teblung.pokedex.databinding.ActivityMainBinding
import com.teblung.pokedex.model.local.sql.PokemonDatabaseHelper
import com.teblung.pokedex.model.remote.response.PokemonResponse
import com.teblung.pokedex.model.remote.service.ApiClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val dbHelper = PokemonDatabaseHelper(this)

    private var stateAsc = true
    private var data: List<PokemonResponse.Result> = emptyList()

    private lateinit var mainAdapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupUI()
        setupData()
    }

    private fun insertData(results: List<PokemonResponse.Result>) {
        val pokemonList: List<PokemonResponse.Result> = results
        for (pokemon in pokemonList) {
            dbHelper.insertPokemon(pokemon.name, pokemon.url)
        }
    }

    private fun showLoading(state: Boolean) {
        binding.apply {
            if (state) {
                loadingBar.visibility = View.VISIBLE
            } else {
                loadingBar.visibility = View.GONE
            }
        }
    }

    private fun setupData() {
        if (dbHelper.getAllPokemon().isNotEmpty()) {
            data = dbHelper.getAllPokemon()
            mainAdapter.setData(dbHelper.getAllPokemon())
        } else {
            showLoading(true)
            ApiClient().getApiService(this).getListPokemon()
                .enqueue(object : Callback<PokemonResponse?> {
                    override fun onResponse(
                        call: Call<PokemonResponse?>,
                        response: Response<PokemonResponse?>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                data = it.results
                                insertData(data)
                                mainAdapter.setData(data)
                            }
                        } else {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                Toast.makeText(
                                    this@MainActivity,
                                    jObjError.getString("status_message"),
                                    Toast.LENGTH_LONG
                                ).show()
                            } catch (e: Exception) {
                                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                        showLoading(false)
                    }

                    override fun onFailure(call: Call<PokemonResponse?>, t: Throwable) {
                        if (t is HttpException) {
                            val errorResponse = t.response()
                            val errorCode = errorResponse?.code()
                            Log.d("Genre", "onFailure $errorResponse || $errorCode")
                        }
                        showLoading(false)
                    }
                })
        }
    }

    private fun setupUI() {
        binding.apply {
            mainAdapter = MainAdapter()
            rvPokemon.apply {
                adapter = mainAdapter
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
            edSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    mainAdapter.filterData(data, p0.toString())
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })
            btnAscSort.apply {
                setOnClickListener {
                    stateAsc = if (stateAsc) {
                        btnAscSort.setImageResource(R.drawable.ic_descending)
                        false
                    } else {
                        btnAscSort.setImageResource(R.drawable.ic_ascending)
                        true
                    }
                    mainAdapter.toggleSortOrder(stateAsc)
                }
            }
        }
    }

    companion object {
        fun intentMain(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}