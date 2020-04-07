package com.eduardo.economise

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

class LimitesActivity : AppCompatActivity() {

    //UI
    lateinit var tvGrafico: TextView
    lateinit var tvCategorias: TextView
    lateinit var tvSair: TextView
    lateinit var tvInicio: TextView
    lateinit var btSave: Button

    lateinit var etEconomiaValor: EditText
    lateinit var etMaxValor: EditText
    lateinit var etMinValor: EditText

    //Variáveis Globais
    lateinit var oldReaisEcon: String
    lateinit var textEcon: String
    lateinit var oldReaisMax: String
    lateinit var textMax: String
    lateinit var oldReaisMin: String
    lateinit var textMin: String
    lateinit var metaList: MutableList<Meta>
    lateinit var valList: MutableList<Meta>

    //BD
    lateinit var ref: DatabaseReference
    var firebaseUser: FirebaseUser? = null
    var firebaseAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_limites)

        initialise()

        getValores()
    }

    private fun initialise() {
        tvGrafico = findViewById(R.id.tvGrafico)
        tvInicio = findViewById(R.id.tvInicio)
        tvCategorias = findViewById(R.id.tvCategorias)
        tvSair = findViewById(R.id.tvSair)
        btSave = findViewById(R.id.btSave)

        etEconomiaValor = findViewById(R.id.etEconomiaValor)
        etMaxValor = findViewById(R.id.etMaxValor)
        etMinValor = findViewById(R.id.etMinValor)

        ref = FirebaseDatabase.getInstance().getReference("meta")
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth!!.getCurrentUser()

        tvGrafico.setOnClickListener {
            val intent = Intent(this@LimitesActivity, GraficoActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

            startActivity(intent)
        }

        tvInicio.setOnClickListener {
            val intent = Intent(this@LimitesActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

            startActivity(intent)
        }

        tvCategorias.setOnClickListener {
            val intent = Intent(this@LimitesActivity, CategoriasActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

            startActivity(intent)
        }

        tvSair.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this@LimitesActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            finish()
            startActivity(intent)
        }

        btSave.setOnClickListener { save() }
    }

    private fun save() {
        metaList = mutableListOf()

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

            var contador = 0

            val economia = etEconomiaValor.text.toString()
            val max = etMaxValor.text.toString()
            val min = etMinValor.text.toString()

            for (i in metaList.indices) {
                if (metaList[i].mes.contains(currentDate)) {
                    val meta = Meta(metaList[i].id, economia, max, min, metaList[i].usuario, currentDate)
                    ref.child(metaList[i].id).setValue(meta).addOnCompleteListener {
                        Toast.makeText(applicationContext, "Metas salvas com sucesso", Toast.LENGTH_SHORT).show()
                    }

                    contador ++
                }
            }

            if (contador == 0) {
                val metaId = ref.push().key

                val meta = Meta(metaId!!, economia, max, min, firebaseUser?.getEmail().toString(), currentDate)

                ref.child(metaId).setValue(meta).addOnCompleteListener {
                    Toast.makeText(applicationContext, "Metas salvas com sucesso", Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }

    private fun getValores() {
        valList = mutableListOf()

        val queryValores = FirebaseDatabase.getInstance().getReference("meta")
            .orderByChild("usuario")
            .equalTo(firebaseUser?.getEmail().toString())

        queryValores.addListenerForSingleValueEvent(valoresEventListener)
    }

    var valoresEventListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            valList.clear()

            for (snapshot in dataSnapshot.children) {
                val cat = snapshot.getValue(Meta::class.java)
                valList.add(cat!!)
            }

            val currentDateVal: String =
                SimpleDateFormat("MM", Locale.getDefault()).format(Date())

            var existe = false

            for (i in valList.indices) {
                if (valList[i].mes.contains(currentDateVal)) {
                    etEconomiaValor.setText(valList[i].econ)
                    etMaxValor.setText(valList[i].max)
                    etMinValor.setText(valList[i].min)

                    currencyListenerEcon(valList[i].econ)
                    currencyListenerMax(valList[i].max)
                    currencyListenerMin(valList[i].min)

                    existe = true
                }
            }

            if (existe == false) {
                currencyListenerEcon("")
                currencyListenerMax("")
                currencyListenerMin("")
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }

    private fun currencyListenerEcon(old: String) {
        oldReaisEcon = old

        etEconomiaValor.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if(s.toString().length > oldReaisEcon.length && s.toString().length < 13) {
                    etEconomiaValor.removeTextChangedListener(this)

                    if(oldReaisEcon == "") {
                        val num = s.toString().toFloat() / 100
                        textEcon = "R$ " + num.toString().replace(".",",")
                    } else {
                        val strConvert = (s.toString().trim().replace(",",".").replace("R$", "")).toDouble() * 10
                        val str = (java.lang.String.format("%.2f", strConvert))
                        textEcon = "R$ " + str.replace(".",",")
                    }

                    etEconomiaValor.setText(textEcon)
                    etEconomiaValor.setSelection(textEcon.length)

                    oldReaisEcon = textEcon

                    etEconomiaValor.addTextChangedListener(this)
                } else if (s.toString().length < 13){
                    etEconomiaValor.removeTextChangedListener(this)

                    etEconomiaValor.setText("")
                    oldReaisEcon = ""

                    etEconomiaValor.addTextChangedListener(this)
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

    private fun currencyListenerMax(old: String) {
        oldReaisMax = old

        etMaxValor.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if(s.toString().length > oldReaisMax.length && s.toString().length < 13) {
                    etMaxValor.removeTextChangedListener(this)

                    if(oldReaisMax == "") {
                        val num = s.toString().toFloat() / 100
                        textMax = "R$ " + num.toString().replace(".",",")
                    } else {
                        val strConvert = (s.toString().trim().replace(",",".").replace("R$", "")).toDouble() * 10
                        val str = (java.lang.String.format("%.2f", strConvert))
                        textMax = "R$ " + str.replace(".",",")
                    }

                    etMaxValor.setText(textMax)
                    etMaxValor.setSelection(textMax.length)

                    oldReaisMax = textMax

                    etMaxValor.addTextChangedListener(this)
                } else if (s.toString().length < 13){
                    etMaxValor.removeTextChangedListener(this)

                    etMaxValor.setText("")
                    oldReaisMax = ""

                    etMaxValor.addTextChangedListener(this)
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

    private fun currencyListenerMin(old: String) {
        oldReaisMin = old

        etMinValor.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if(s.toString().length > oldReaisMin.length && s.toString().length < 13) {
                    etMinValor.removeTextChangedListener(this)

                    if(oldReaisMin == "") {
                        val num = s.toString().toFloat() / 100
                        textMin = "R$ " + num.toString().replace(".",",")
                    } else {
                        val strConvert = (s.toString().trim().replace(",",".").replace("R$", "")).toDouble() * 10
                        val str = (java.lang.String.format("%.2f", strConvert))
                        textMin = "R$ " + str.replace(".",",")
                    }

                    etMinValor.setText(textMin)
                    etMinValor.setSelection(textMin.length)

                    oldReaisMin = textMin

                    etMinValor.addTextChangedListener(this)
                } else if (s.toString().length < 13){
                    etMinValor.removeTextChangedListener(this)

                    etMinValor.setText("")
                    oldReaisMin = ""

                    etMinValor.addTextChangedListener(this)
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
}
