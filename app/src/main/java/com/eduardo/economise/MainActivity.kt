package com.eduardo.economise

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    //UI
    lateinit var btnReceita: Button
    lateinit var btnDespesa: Button
    lateinit var tvGrafico: TextView
    lateinit var tvLimites: TextView
    lateinit var tvCategorias: TextView
    lateinit var tvSair: TextView
    lateinit var tvSaldo: TextView
    lateinit var lvLancamentos: ListView
    lateinit var tvNull: TextView
    lateinit var imOlho: ImageView
    lateinit var ivUm: ImageView
    lateinit var ivDois: ImageView
    lateinit var ivTres: ImageView
    lateinit var ivQuatro: ImageView
    lateinit var ivCinco: ImageView

    //Variáveis globais
    lateinit var categoriaList: MutableList<Categoria>
    lateinit var lancamentoList: MutableList<Lancamento>
    var mostra = true
    lateinit var metaList: MutableList<Meta>
    private val PREF = "com.eduardo.economise.PREF"
    var progressBar: AlertDialog? = null

    //BD
    lateinit var refCategoria: DatabaseReference
    var firebaseAuth: FirebaseAuth? = null
    var firebaseUser: FirebaseUser? = null
    lateinit var refMeta: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialise()

        escondeSaldo()

        saldo()

        categorias()

        metaListener()
    }

    private fun initialise() {
        progressBar = progressBar()

        btnReceita = findViewById(R.id.btnReceita)
        btnDespesa = findViewById(R.id.btnDespesa)
        tvGrafico = findViewById(R.id.tvGrafico)
        tvLimites = findViewById(R.id.tvLimites)
        tvCategorias = findViewById(R.id.tvCategorias)
        tvSair = findViewById(R.id.tvSair)
        imOlho = findViewById(R.id.imOlho)
        tvSaldo = findViewById(R.id.tvSaldo)
        ivUm = findViewById(R.id.ivUm)
        ivDois = findViewById(R.id.ivDois)
        ivTres = findViewById(R.id.ivTres)
        ivQuatro = findViewById(R.id.ivQuatro)
        ivCinco = findViewById(R.id.ivCinco)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth!!.getCurrentUser()

        btnReceita.setOnClickListener {
            val intent = Intent(this@MainActivity, ReceitaActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

            startActivity(intent)
        }

        btnDespesa.setOnClickListener {
            val intent = Intent(this@MainActivity, DespesaActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

            startActivity(intent)
        }

        ivDois.setOnClickListener {
            val intent = Intent(this@MainActivity, GraficoActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

            startActivity(intent)
        }

        ivTres.setOnClickListener {
            val intent = Intent(this@MainActivity, LimitesActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

            startActivity(intent)
        }

        ivQuatro.setOnClickListener {
            val intent = Intent(this@MainActivity, CategoriasActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

            startActivity(intent)
        }

        ivCinco.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this@MainActivity, MenuActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            finish()
            startActivity(intent)
        }
    }

    private fun saldo() {
        lancamentoList = mutableListOf()

        val queryLancamento = FirebaseDatabase.getInstance().getReference("lancamento")
            .orderByChild("usuario")
            .equalTo(firebaseUser?.getEmail().toString())

        queryLancamento.addValueEventListener(lancamentoEventListener)
    }

    var lancamentoEventListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            lancamentoList.clear()

            for (snapshot in dataSnapshot.children) {
                val lanc = snapshot.getValue(Lancamento::class.java)
                lancamentoList.add(lanc!!)
            }

            var saldo = 0.00

            lvLancamentos = findViewById<View>(R.id.lvLancamentos) as ListView

            if (!lancamentoList.isEmpty()) {
                for (i in lancamentoList.indices) {
                    if (lancamentoList[i].valor.substring(0,1) == "-") {
                        val valor = lancamentoList[i].valor.substring(5).replace(",",".").toFloat() * -1
                        saldo += valor
                    } else {
                        val valor = lancamentoList[i].valor.substring(3).replace(",",".").toFloat()
                        saldo += valor
                    }
                }

                if (BigDecimal(saldo).setScale(2, RoundingMode.HALF_EVEN) < 0.toBigDecimal()) {
                    if (mostra) {
                        tvSaldo.text = "R$ ${BigDecimal(saldo).setScale(2, RoundingMode.HALF_EVEN).toString().replace(".",",")}"

                        tvSaldo.setTextColor(Color.parseColor("#B84A43"))
                    }
                } else if (BigDecimal(saldo).setScale(2, RoundingMode.HALF_EVEN) > 0.toBigDecimal()) {
                    if (mostra) {
                        tvSaldo.text = "R$ ${BigDecimal(saldo).setScale(2, RoundingMode.HALF_EVEN).toString().replace(".",",")}"

                        tvSaldo.setTextColor(Color.parseColor("#46A048"))
                    }
                }
                val sort = lancamentoList.sortedBy { (it.data.substring(6)+it.data.substring(3,5)+it.data.substring(0,2)).toBigInteger() }

                val adapter = LancamentoAdapter(this@MainActivity, R.layout.list_lancamento, sort.reversed())
                lvLancamentos.adapter = adapter
            } else {
                if (mostra) {
                    tvSaldo.text = ""
                }

                lvLancamentos.setVisibility(View.INVISIBLE)

                tvNull = findViewById(R.id.tvNull)

                tvNull.text = "Realize lançamentos, pelos botões de Receita e Despesa"
            }

            progressBar?.dismiss()
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }

    private fun categorias() {
        refCategoria = FirebaseDatabase.getInstance().getReference("categoria")

        categoriaList = mutableListOf()

        val queryCategoria = FirebaseDatabase.getInstance().getReference("categoria")
            .orderByChild("usuario")
            .equalTo(firebaseUser?.getEmail().toString())

        queryCategoria.addListenerForSingleValueEvent(categoriaEventListener)
    }

    var categoriaEventListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            categoriaList.clear()

            for (snapshot in dataSnapshot.children) {
                val cat = snapshot.getValue(Categoria::class.java)
                categoriaList.add(cat!!)
            }

            if (categoriaList.isEmpty()) {
                var categoriaId = refCategoria.push().key

                var value = Categoria(categoriaId!!, "Alimentação", firebaseUser?.getEmail().toString(), "Despesa")
                refCategoria.child(categoriaId).setValue(value)

                categoriaId = refCategoria.push().key

                value = Categoria(categoriaId!!, "Bares e restaurantes", firebaseUser?.getEmail().toString(), "Despesa")
                refCategoria.child(categoriaId).setValue(value)

                categoriaId = refCategoria.push().key

                value = Categoria(categoriaId!!, "Casa", firebaseUser?.getEmail().toString(), "Despesa")
                refCategoria.child(categoriaId).setValue(value)

                categoriaId = refCategoria.push().key

                value = Categoria(categoriaId!!, "Transporte", firebaseUser?.getEmail().toString(), "Despesa")
                refCategoria.child(categoriaId).setValue(value)

                categoriaId = refCategoria.push().key

                value = Categoria(categoriaId!!, "Educação", firebaseUser?.getEmail().toString(), "Despesa")
                refCategoria.child(categoriaId).setValue(value)

                categoriaId = refCategoria.push().key

                value = Categoria(categoriaId!!, "Mercado", firebaseUser?.getEmail().toString(), "Despesa")
                refCategoria.child(categoriaId).setValue(value)

                categoriaId = refCategoria.push().key

                value = Categoria(categoriaId!!, "Saúde", firebaseUser?.getEmail().toString(), "Despesa")
                refCategoria.child(categoriaId).setValue(value)

                categoriaId = refCategoria.push().key

                value = Categoria(categoriaId!!, "Outros", firebaseUser?.getEmail().toString(), "Ambos")
                refCategoria.child(categoriaId).setValue(value)

                categoriaId = refCategoria.push().key

                value = Categoria(categoriaId!!, "Salário", firebaseUser?.getEmail().toString(), "Receita")
                refCategoria.child(categoriaId).setValue(value)
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }

    private fun metaListener() {
        metaList = mutableListOf()
        refMeta = FirebaseDatabase.getInstance().getReference("meta")

        val query = FirebaseDatabase.getInstance().getReference("meta")
            .orderByChild("usuario")
            .equalTo(firebaseUser?.getEmail().toString())

        query.addListenerForSingleValueEvent(metaEventListener)
    }

    var metaEventListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            metaList.clear()

            for (snapshot in dataSnapshot.children) {
                val cat = snapshot.getValue(Meta::class.java)
                metaList.add(cat!!)
            }

            val currentDate: String =
                SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(Date())

            val sort = metaList.sortedBy { it.mes.toInt() }
            sort.reversed()

            var mesAtual = false

            for (i in metaList.indices) {
                if (metaList[i].mes.contains(currentDate)) {
                    mesAtual = true
                }
            }

            if (mesAtual == false && sort.isNotEmpty()) {
                val metaId = refMeta.push().key
                val meta = Meta(metaId!!, sort[0].econ, sort[0].max, sort[0].min, firebaseUser?.getEmail().toString(), currentDate)
                refMeta.child(metaId).setValue(meta)
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }

    fun escondeSaldo() {
        val sp: SharedPreferences? =
            this.getSharedPreferences(PREF, Context.MODE_PRIVATE)

        if (sp?.getString("mostra", "") == "N") {
            tvSaldo.setText("- - -")
        }

        mostra = sp?.getString("mostra", "") != "N"

        imOlho.setOnClickListener {
            if (mostra) {
                tvSaldo.setText("- - -")
                tvSaldo.setTextColor(Color.parseColor("#040404"))
                imOlho.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_visibility_off_gray_24dp))

                mostra = false
                sp?.edit()?.putString("mostra", "N")?.apply()
            } else {
                saldo()
                imOlho.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_visibility_gay_24dp))

                mostra = true
                sp?.edit()?.putString("mostra", "S")?.apply()
            }
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
