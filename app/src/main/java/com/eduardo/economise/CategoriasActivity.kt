package com.eduardo.economise

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.google.android.material.textfield.TextInputLayout
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
    lateinit var ivUm: ImageView
    lateinit var ivDois: ImageView
    lateinit var ivTres: ImageView
    lateinit var ivQuatro: ImageView
    lateinit var ivCinco: ImageView

    lateinit var ivSave: ImageView
    lateinit var etCategoria: EditText
    lateinit var lvCategorias : ListView
    lateinit var etTipo: EditText
    lateinit var tiTipo: TextInputLayout
    lateinit var tiCategoria: TextInputLayout

    //Variáveis globais
    lateinit var categoriaList: MutableList<Categoria>
    private var novaCategoria: String? = null
    lateinit var tipoList: MutableList<String>
    var progressBar: AlertDialog? = null

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
        progressBar = progressBar()

        tvGrafico = findViewById(R.id.tvGrafico)
        tvLimites = findViewById(R.id.tvLimites)
        tvCategorias = findViewById(R.id.tvCategorias)
        tvSair = findViewById(R.id.tvSair)
        tvInicio = findViewById(R.id.tvInicio)
        lvCategorias = findViewById<View>(R.id.lvCategorias) as ListView
        etTipo = findViewById(R.id.etTipo)
        tiTipo = findViewById(R.id.tiTipo)
        tiCategoria = findViewById(R.id.tiCategoria)
        ivUm = findViewById(R.id.ivUm)
        ivDois = findViewById(R.id.ivDois)
        ivTres = findViewById(R.id.ivTres)
        ivQuatro = findViewById(R.id.ivQuatro)
        ivCinco = findViewById(R.id.ivCinco)

        ivSave = findViewById(R.id.ivSave)
        categoriaList = mutableListOf()

        firebaseAuth = FirebaseAuth.getInstance()
        refCategoria = FirebaseDatabase.getInstance().getReference("categoria")
        firebaseUser = firebaseAuth!!.getCurrentUser()

        ivDois.setOnClickListener {
            val intent = Intent(this@CategoriasActivity, GraficoActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

            startActivity(intent)
        }

        ivTres.setOnClickListener {
            val intent = Intent(this@CategoriasActivity, LimitesActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

            startActivity(intent)
        }

        ivUm.setOnClickListener {
            val intent = Intent(this@CategoriasActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

            startActivity(intent)
        }

        ivCinco.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this@CategoriasActivity, MenuActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            finish()
            startActivity(intent)
        }

        ivSave.setOnClickListener { novaCategoria() }
        etTipo.setOnClickListener { tipoListener() }
    }

    private fun novaCategoria() {
        progressBar = progressBar()

        etCategoria = findViewById(R.id.etCategoria)
        novaCategoria = etCategoria.text.toString()
        val tipo = etTipo.text.toString()

        etCategoria.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                tiCategoria.error = null
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        etTipo.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                tiTipo.error = null
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        if (TextUtils.isEmpty(novaCategoria)) {
            tiCategoria.error = "Digite o nome da nova categoria"
        }

        if (tipo.isEmpty()) {
            tiTipo.error = "Escolha o tipo da categoria"
        }

        if (!TextUtils.isEmpty(novaCategoria) && tipo.isNotEmpty()) {
            val categoriaId = refCategoria.push().key
            val value = Categoria(categoriaId!!, novaCategoria!!, firebaseUser?.getEmail().toString(), tipo)
            refCategoria.child(categoriaId).setValue(value)

            lvCategorias.setVisibility(View.VISIBLE)

            Toast.makeText(this@CategoriasActivity, "Categoria inserida", Toast.LENGTH_SHORT).show()
        }

        progressBar?.dismiss()
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

            progressBar?.dismiss()
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }

    fun tipoListener() {
        tipoList = mutableListOf()

        tipoList.add("Receita")
        tipoList.add("Despesa")
        tipoList.add("Ambos")

        val builder = AlertDialog.Builder(this@CategoriasActivity)

        val inflater = LayoutInflater.from(this@CategoriasActivity)

        val view = inflater.inflate(R.layout.dropdown, null)

        val lvDrop = view.findViewById<ListView>(R.id.lvDrop)

        val adapter = DropAdapter(
            this@CategoriasActivity,
            R.layout.list_dropdown,
            tipoList
        )
        lvDrop.adapter = adapter

        builder.setView(view)

        val alert = builder.create()
        alert.show()

        lvDrop.setOnItemClickListener {
                adapterView,
                view,
                position,
                l
            -> etTipo.setText(tipoList[position])
            alert.dismiss()
        }
    }

    fun progressBar(): AlertDialog {
        val builder = AlertDialog.Builder(this)

        val inflater = LayoutInflater.from(this)

        val view = inflater.inflate(R.layout.progress_bar, null)

        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        progressBar.getIndeterminateDrawable().setColorFilter(
            Color.rgb(0,163,81), android.graphics.PorterDuff.Mode.SRC_IN)

        builder.setView(view)

        val alert = builder.create()

        alert.show()
        alert.setCancelable(false)
        alert.setCanceledOnTouchOutside(false)

        return alert
    }
}
