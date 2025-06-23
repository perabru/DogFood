package com.maximus.dogfood



import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import android.location.Geocoder
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var edtRua: EditText
    private lateinit var btnSalvar: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LocalizacaoAdapter
    private val listaLocalizacoes = mutableListOf<Map<String, Any>>()
    private val databaseRef = FirebaseDatabase.getInstance().getReference("dogfood")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edtRua = findViewById(R.id.edtRua)
        btnSalvar = findViewById(R.id.btnSalvar)
        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = LocalizacaoAdapter(listaLocalizacoes)
        recyclerView.adapter = adapter

        btnSalvar.setOnClickListener {
            val rua = edtRua.text.toString().trim()
            if (rua.isNotEmpty()) {
                salvarLocalizacao(rua)
            } else {
                Toast.makeText(this, "Digite o nome da rua", Toast.LENGTH_SHORT).show()
            }
        }

        carregarLista()
    }

    private fun salvarLocalizacao(rua: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        var latitude: Double? = null
        var longitude: Double? = null

        try {
            val resultados = geocoder.getFromLocationName(rua, 1)
            if (!resultados.isNullOrEmpty()) {
                latitude = resultados[0].latitude
                longitude = resultados[0].longitude
            }
        } catch (e: Exception) {
            // Se não encontrar, segue mesmo assim
        }

        val dados = mapOf(
            "rua" to rua,
            "latitude" to (latitude ?: "Sem localização"),
            "longitude" to (longitude ?: "Sem localização")
        )

        val chaveRua = rua.replace("\\s+".toRegex(), "_")

        databaseRef.child(chaveRua).setValue(dados)
            .addOnSuccessListener {
                Toast.makeText(this, "Endereço salvo!", Toast.LENGTH_SHORT).show()
                edtRua.text.clear()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun carregarLista() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaLocalizacoes.clear()
                for (localSnapshot in snapshot.children) {
                    val item = localSnapshot.value as? Map<String, Any>
                    if (item != null) {
                        listaLocalizacoes.add(item)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Erro ao carregar lista", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
