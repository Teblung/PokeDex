package com.teblung.pokedex.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teblung.pokedex.R
import com.teblung.pokedex.databinding.ListItemPokedexBinding
import com.teblung.pokedex.model.remote.response.PokemonResponse
import com.teblung.pokedex.ui.detail.DetailActivity

class MainAdapter : RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    private var listData = mutableListOf<PokemonResponse.Result>()
    private var isAscendingSort = true

    fun setData(data: List<PokemonResponse.Result>) {
        listData.addAll(data)
        sortData()
        notifyDataSetChanged()
    }

    fun filterData(data: List<PokemonResponse.Result>, query: String) {
        listData = data.filter {
            it.name.contains(
                query,
                ignoreCase = true
            )
        } as MutableList<PokemonResponse.Result>
        sortData()
        notifyDataSetChanged()
    }

    private fun sortData() {
        listData = if (isAscendingSort) {
            listData.sortedBy { it.name }.toMutableList()
        } else {
            listData.sortedByDescending { it.name }.toMutableList()
        }
    }

    fun toggleSortOrder(stateAsc: Boolean) {
        isAscendingSort = stateAsc
        sortData()
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ListItemPokedexBinding.bind(itemView)

        fun bind(item: PokemonResponse.Result) {
            binding.apply {
                titleName.text = item.name
                itemView.setOnClickListener {
                    val replaceUrl = item.url.replace("https://pokeapi.co/api/v2/pokemon/", "")
                    val replaceEndUrl = replaceUrl.replace("/", "")
                    itemView.context.startActivity(
                        DetailActivity.intentDetail(itemView.context, replaceEndUrl.toInt())
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_pokedex, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainAdapter.ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    override fun getItemCount(): Int = listData.size
}