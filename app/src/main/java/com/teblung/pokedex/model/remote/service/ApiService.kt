package com.teblung.pokedex.model.remote.service

import com.teblung.pokedex.model.remote.response.PokemonDetailResponse
import com.teblung.pokedex.model.remote.response.PokemonResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("pokemon/")
    fun getListPokemon(): Call<PokemonResponse>

    @GET("pokemon/{id}")
    fun getDetailPokemon(
        @Path("id") id: Int
    ): Call<PokemonDetailResponse>
}