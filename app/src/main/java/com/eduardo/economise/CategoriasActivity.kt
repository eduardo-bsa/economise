package com.eduardo.economise

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class CategoriasActivity : AppCompatActivity() {

    //UI
    lateinit var tvGrafico: TextView
    lateinit var tvLimites: TextView
    lateinit var tvCategorias: TextView
    lateinit var tvSair: TextView
    lateinit var tvInicio: TextView

    lateinit var ivSave: ImageView
    lateinit var etCategoria: EditText
    lateinit var lvCategorias : ListView

    //Variáveis globais
    lateinit var categoriaList: MutableList<Categoria>
    private var novaCategoria: String? = null

    //BD
    var firebaseUser: FirebaseUser? = null
    lateinit var refCategoria: DatabaseReference
    var firebaseAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categorias)

        initialise()

        categorias()
    }

    private fun initialise() {
        tvGrafico = findViewById(R.id.tvGrafico)
        tvLimites = findViewById(R.id.tvLimites)
        tvCategorias = findViewById(R.id.tvCategorias)
        tvSair = findViewById(R.id.tvSair)
        tvInicio = findViewById(R.id.tvInicio)
        lvCategorias = findViewById<View>(R.id.lvCategorias) as ListView

        ivSave = findViewById(R.id.ivSave)
        categoriaList = mutableListOf()

        firebaseAuth = FirebaseAuth.getInstance()
        refCategoria = FirebaseDatabase.getInstance().getReference("categoria")
        firebaseUser = firebaseAuth!!.getCurrentUser()

        tvGrafico.setOnClickListener {
            val intent = Intent(this@CategoriasActivity, GraficoActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

            startActivity(intent)
        }

        tvLimites.setOnClickListener {
            val intent = Intent(this@CategoriasActivity, LimitesActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

            startActivity(intent)
        }

        tvInicio.setOnClickListener {
            val intent = Intent(this@CategoriasActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

            startActivity(intent)
        }

        tvSair.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this@CategoriasActivity, MenuActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            finish()
            startActivity(intent)
        }

        ivSave.setOnClickListener {
            novaCategoria()
        }
    }

    private fun novaCategoria() {
        etCategoria = findViewById(R.id.etCategoria)
        novaCategoria = etCategoria.text.toString()

        if (!TextUtils.isEmpty(novaCategoria)) {
            val categoriaId = refCategoria.push().key
            val value = Categoria(categoriaId!!, novaCategoria!!, firebaseUser?.getEmail().toString())
            refCategoria.child(categoriaId).setValue(value)

            lvCategorias.setVisibility(View.VISIBLE)

            Toast.makeText(this@CategoriasActivity, "Categoria inserida", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this@CategoriasActivity, "Preencha a categoria", Toast.LENGTH_SHORT).show()
        }
    }

    private fun categorias() {
        val queryCategoria = FirebaseDatabase.getInstance().getReference("categoria")
            .orderByChild("usuario")
            .equalTo(firebaseUser?.getEmail().toString())

        queryCategoria.addValueEventListener(categoriasEventListener)
    }

    var categoriasEventListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            categoriaList.clear()

            for (snapshot in dataSnapshot.children) {
                val cat = snapshot.getValue(Categoria::class.java)
                categoriaList.add(cat!!)
            }

            lvCategorias = findViewById<View>(R.id.lvCategorias) as ListView

            if (!categoriaList.isEmpty()) {
            val adapter = CategoriaAdapter(this@CategoriasActivity, R.layout.list_categoria, categoriaList)
            lvCategorias.adapter = adapter
            } else {
                lvCategorias.setVisibility(View.INVISIBLE)
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }


}
