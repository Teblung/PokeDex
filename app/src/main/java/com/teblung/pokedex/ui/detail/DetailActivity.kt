package com.teblung.pokedex.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.teblung.pokedex.R
import com.teblung.pokedex.databinding.ActivityDetailBinding
import com.teblung.pokedex.model.remote.response.PokemonDetailResponse
import com.teblung.pokedex.model.remote.service.ApiClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class DetailActivity : AppCompatActivity() {

    private val binding: ActivityDetailBinding by lazy {
        ActivityDetailBinding.inflate(layoutInflater)
    }

    private var id = 0

    private lateinit var detailPokemon: PokemonDetailResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupBundle()
    }

    private fun setupBundle() {
        id = intent.getIntExtra(ID, 0)
        ApiClient().getApiService(this).getDetailPokemon(id).enqueue(
            object : Callback<PokemonDetailResponse?> {
                override fun onResponse(
                    call: Call<PokemonDetailResponse?>,
                    response: Response<PokemonDetailResponse?>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { detailPokemon = it }
                        setupUI()
                    } else {
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            Toast.makeText(
                                this@DetailActivity,
                                jObjError.getString("status_message"),
                                Toast.LENGTH_LONG
                            ).show()
                        } catch (e: Exception) {
                            Toast.makeText(this@DetailActivity, e.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onFailure(call: Call<PokemonDetailResponse?>, t: Throwable) {
                    if (t is HttpException) {
                        val errorResponse = t.response()
                        val errorCode = errorResponse?.code()
                        Log.d("Genre", "onFailure $errorResponse || $errorCode")
                    }
                }
            }
        )
    }

    private fun setupUI() {
        binding.apply {
            titleDetail.text = detailPokemon.name
            Glide.with(this@DetailActivity)
                .load(detailPokemon.sprites.frontDefault)
                .apply(
                    RequestOptions.placeholderOf(R.drawable.ic_loading)
                        .error(R.drawable.ic_error)
                ).into(imgDetail)
            DescDetail.text = "Abilities"
            AbilitiesDetail.text = detailPokemon.abilities.map {
                it.ability.name
            }.joinToString(", ")
        }
    }

    companion object {

        const val ID = "ID"

        fun intentDetail(context: Context, id: Int): Intent {
            return Intent(context, DetailActivity::class.java).apply {
                putExtra(ID, id)
            }
        }
    }
}