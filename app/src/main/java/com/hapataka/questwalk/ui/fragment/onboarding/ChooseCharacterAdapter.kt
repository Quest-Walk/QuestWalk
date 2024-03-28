package com.hapataka.questwalk.ui.fragment.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.ItemChooseCharacterBinding

class ChooseCharacterAdapter(val character : List<CharacterData>,
    private val onItemClick :(CharacterData) -> Unit
) : RecyclerView.Adapter<ChooseCharacterAdapter.Holder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemChooseCharacterBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        return character.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val character = character[position]
        holder.bind(character)
    }

    inner class Holder(private val binding : ItemChooseCharacterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(items : CharacterData) {
            with(binding) {
                ivCharacterImg.setImageResource(items.img)
                tvCharacterName.text = items.name
            }

            itemView.setOnClickListener {
                onItemClick(items)
            }

        }
    }
}