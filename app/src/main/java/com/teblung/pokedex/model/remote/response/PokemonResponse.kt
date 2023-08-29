package com.teblung.pokedex.model.remote.response


import com.google.gson.annotations.SerializedName

data class PokemonResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("next")
    val next: String,
    @SerializedName("previous")
    val previous: Any,
    @SerializedName("results")
    var results: List<Result>
) {
    data class Result(
        @SerializedName("name")
        val name: String,
        @SerializedName("url")
        val url: String
    )
}
