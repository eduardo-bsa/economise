package com.eduardo.economise

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.*


class ReceitaActivity : AppCompatActivity() {

    //Variáveis Globais
    lateinit var date: String
    lateinit var oldReais: String
    lateinit var text: String
    var categoria = ""
    lateinit var lancamentoList: MutableList<Lancamento>
    lateinit var categoriaList: MutableList<Categoria>

    //UI
    lateinit var tvInicio: TextView
    lateinit var tvGrafico: TextView
    lateinit var tvLimites: TextView
    lateinit var tvCategorias: TextView
    lateinit var tvSair: TextView

    lateinit var etTitle: EditText
    lateinit var btnSave: Button
    lateinit var etReais: EditText
    lateinit var tvData: TextView
    lateinit var etCategoria: EditText
    lateinit var tiCategoria: TextInputLayout
    lateinit var tiData: TextInputLayout

    //Date Listener
    lateinit var setData: OnDateSetListener

    //BD
    lateinit var ref: DatabaseReference
    var firebaseAuth: FirebaseAuth? = null
    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receita)

        initialise()

        dataListener()

        currencyListener()
    }

    private fun initialise() {
        tvInicio = findViewById(R.id.tvInicio)
        tvGrafico = findViewById(R.id.tvGrafico)
        tvLimites = findViewById(R.id.tvLimites)
        tvCategorias = findViewById(R.id.tvCategorias)
        tvSair = findViewById(R.id.tvSair)

        etReais = findViewById(R.id.etReais)
        etTitle = findViewById(R.id.etTitle)
        tvData = findViewById(R.id.tvData)
        btnSave = findViewById(R.id.btnSave)
        etCategoria = findViewById(R.id.etCategoria)
        tiData = findViewById(R.id.tiData)
        tiCategoria = findViewById(R.id.tiCategoria)


        lancamentoList = mutableListOf()
        ref = FirebaseDatabase.getInstance().getReference("lancamento")

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth!!.getCurrentUser()

        tvInicio.setOnClickListener {
            val intent = Intent(this@ReceitaActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

            startActivity(intent)
        }

        tvGrafico.setOnClickListener {
            val intent = Intent(this@ReceitaActivity, GraficoActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

            startActivity(intent)
        }

        tvLimites.setOnClickListener {
            val intent = Intent(this@ReceitaActivity, LimitesActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

            startActivity(intent)
        }

        tvCategorias.setOnClickListener {
            val intent = Intent(this@ReceitaActivity, CategoriasActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

            startActivity(intent)
        }

        tvSair.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this@ReceitaActivity, MenuActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            finish()
            startActivity(intent)
        }

        btnSave.setOnClickListener { save() }

        etCategoria.setOnClickListener { categoriaListener() }

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
    }

    private fun dataListener() {
        tvData.setOnClickListener {
            val cal: Calendar = Calendar.getInstance()
            val year: Int = cal.get(Calendar.YEAR)
            val month: Int = cal.get(Calendar.MONTH)
            val day: Int = cal.get(Calendar.DAY_OF_MONTH)
            val dialog = DatePickerDialog(
                this@ReceitaActivity,
                android.R.style.Theme_DeviceDefault_Dialog,
                setData,
                year, month, day
            )
            dialog.show()
        }

        setData = OnDateSetListener { datePicker, year, month, day ->
            var month = month
            month = month + 1

            if (day < 10 && month < 10) {
                date = "0$day/0$month/$year"
            } else if (day < 10) {
                date = "0$day/$month/$year"
            } else if (month < 10) {
                date = "$day/0$month/$year"
            } else {
                date = "$day/$month/$year"
            }

            tvData.text = date

            val face = Typeface.SANS_SERIF
            tvData.typeface = face
        }
    }

    private fun currencyListener() {
        oldReais = ""

        etReais.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if(s.toString().length > oldReais.length && s.toString().length < 13) {
                    etReais.removeTextChangedListener(this)

                    if(oldReais == "") {
                        val num = s.toString().toFloat() / 100
                        text = "R$ " + num.toString().replace(".",",")
                    } else {
                        val strConvert = (s.toString().trim().replace(",",".").replace("R$", "")).toDouble() * 10
                        val str = (java.lang.String.format("%.2f", strConvert))
                        text = "R$ " + str.replace(".",",")
                    }

                    etReais.setText(text)
                    etReais.setSelection(text.length)

                    oldReais = text

                    etReais.addTextChangedListener(this)
                } else if (s.toString().length < 13){
                    etReais.removeTextChangedListener(this)

                    etReais.setText("")
                    oldReais = ""

                    etReais.addTextChangedListener(this)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
            }
        })
    }

    private fun categoriaListener() {
        categoriaList = mutableListOf()

        val query = FirebaseDatabase.getInstance().getReference("categoria")
            .orderByChild("usuario")
            .equalTo(firebaseUser?.getEmail().toString())

        query.addListenerForSingleValueEvent(valueEventListener)
    }

    private fun save() {
        val desc = etTitle.text.toString()
        val valor = etReais.text.toString()
        val data = tvData.text.toString()

        tvData?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                tiData.error = null
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        etCategoria?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                tiCategoria.error = null
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        if (valor.isEmpty()) {
            etReais.error = "Digite o valor da receita"
        }

        if (data.isEmpty()) {
            tiData.error = "Escolha a data da sua receita"
        }

        if (categoria == "") {
            tiCategoria.error = "Escolha uma categoria"
        }

        if (!valor.isEmpty() && !data.isEmpty()) {
            val lancamentoId = ref.push().key

            val lanc = Lancamento(lancamentoId!!, desc, valor, data, categoria, firebaseUser?.getEmail().toString())

            ref.child(lancamentoId).setValue(lanc).addOnCompleteListener {
                Toast.makeText(applicationContext, "Lançamento realizado", Toast.LENGTH_SHORT).show()
            }

            val intent = Intent(this@ReceitaActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

            startActivity(intent)
        }

    }

    var valueEventListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            categoriaList.clear()

            for (snapshot in dataSnapshot.children) {
                val cat = snapshot.getValue(Categoria::class.java)
                categoriaList.add(cat!!)
            }

            val option: MutableList<String> = mutableListOf()

            categoriaList.forEach { t: Categoria ->
                option.add(t.categoria.trim())
            }

            val builder = AlertDialog.Builder(this@ReceitaActivity)

            val inflater = LayoutInflater.from(this@ReceitaActivity)

            val view = inflater.inflate(R.layout.dropdown, null)

            val lvDrop = view.findViewById<ListView>(R.id.lvDrop)

            val adapter = DropAdapter(
                this@ReceitaActivity,
                R.layout.list_dropdown,
                option
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
                -> etCategoria.setText(option[position])
                categoria = option[position]
                alert.dismiss()
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }

}
