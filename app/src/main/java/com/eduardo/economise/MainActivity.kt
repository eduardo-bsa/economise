package com.eduardo.economise

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*


class MainActivity : AppCompatActivity() {

    //UI

    lateinit var btnReceita: Button
    lateinit var btnDespesa: Button
    lateinit var tvExtrato: TextView
    lateinit var tvLimites: TextView
    lateinit var tvCategorias: TextView
    lateinit var tvSair: TextView

    //Variáveis globais

    lateinit var categoriaList: MutableList<Categoria>

    //BD

    lateinit var ref: DatabaseReference
    var firebaseAuth: FirebaseAuth? = null
    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialise()

        categorias()
    }

    private fun initialise() {
        btnReceita = findViewById(R.id.btnReceita)
        btnDespesa = findViewById(R.id.btnDespesa)
        tvExtrato = findViewById(R.id.tvExtrato)
        tvLimites = findViewById(R.id.tvLimites)
        tvCategorias = findViewById(R.id.tvCategorias)
        tvSair = findViewById(R.id.tvSair)

        btnReceita.setOnClickListener {
            startActivity(Intent(this@MainActivity, ReceitaActivity::class.java))
        }

        btnDespesa.setOnClickListener {
            startActivity(Intent(this@MainActivity, DespesaActivity::class.java))
        }

        tvExtrato.setOnClickListener {
            startActivity(Intent(this@MainActivity, ExtratoActivity::class.java))
        }

        tvLimites.setOnClickListener {
            startActivity(Intent(this@MainActivity, LimitesActivity::class.java))
        }

        tvCategorias.setOnClickListener {
            startActivity(Intent(this@MainActivity, CategoriasActivity::class.java))
        }

        tvSair.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            finish()
            startActivity(intent)
        }
    }

    private fun categorias() {
        ref = FirebaseDatabase.getInstance().getReference("categoria")

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth!!.getCurrentUser()

        categoriaList = mutableListOf()

        val query = FirebaseDatabase.getInstance().getReference("categoria")
            .orderByChild("usuario")
            .equalTo(firebaseUser?.getEmail().toString())

        query.addListenerForSingleValueEvent(valueEventListener)
    }

    var valueEventListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            categoriaList.clear()

            for (snapshot in dataSnapshot.children) {
                val cat = snapshot.getValue(Categoria::class.java)
                categoriaList.add(cat!!)
            }

            if (categoriaList.isEmpty()) {
                var categoriaId = ref.push().key

                var value = Categoria(categoriaId!!, "Alimentação", firebaseUser?.getEmail().toString())
                ref.child(categoriaId).setValue(value)

                categoriaId = ref.push().key

                value = Categoria(categoriaId!!, "Bares e restaurantes", firebaseUser?.getEmail().toString())
                ref.child(categoriaId).setValue(value)

                categoriaId = ref.push().key

                value = Categoria(categoriaId!!, "Casa", firebaseUser?.getEmail().toString())
                ref.child(categoriaId).setValue(value)

                categoriaId = ref.push().key

                value = Categoria(categoriaId!!, "Transporte", firebaseUser?.getEmail().toString())
                ref.child(categoriaId).setValue(value)

                categoriaId = ref.push().key

                value = Categoria(categoriaId!!, "Educação", firebaseUser?.getEmail().toString())
                ref.child(categoriaId).setValue(value)

                categoriaId = ref.push().key

                value = Categoria(categoriaId!!, "Mercado", firebaseUser?.getEmail().toString())
                ref.child(categoriaId).setValue(value)

                categoriaId = ref.push().key

                value = Categoria(categoriaId!!, "Saúde", firebaseUser?.getEmail().toString())
                ref.child(categoriaId).setValue(value)

                categoriaId = ref.push().key

                value = Categoria(categoriaId!!, "Outros", firebaseUser?.getEmail().toString())
                ref.child(categoriaId).setValue(value)
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }
}
