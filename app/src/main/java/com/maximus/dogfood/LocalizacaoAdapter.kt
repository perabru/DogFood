package com.maximus.dogfood



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LocalizacaoAdapter(private val lista: List<Map<String, Any>>) :
    RecyclerView.Adapter<LocalizacaoAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtRua: TextView = itemView.findViewById(R.id.txtRua)
        val txtCoordenadas: TextView = itemView.findViewById(R.id.txtCoordenadas)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_local, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.txtRua.text = item["rua"].toString()
        val lat = item["latitude"].toString()
        val lon = item["longitude"].toString()
        holder.txtCoordenadas.text = "Lat: $lat | Lon: $lon"
    }
}
