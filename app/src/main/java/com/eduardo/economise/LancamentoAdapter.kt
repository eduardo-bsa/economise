package com.eduardo.economise

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.text.Editable
import android.text.Layout
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.database.FirebaseDatabase


class LancamentoAdapter (val mCtx: Context, val layoutResId: Int, val lancamentoList: List<Lancamento>)
    : ArrayAdapter<Lancamento>(mCtx, layoutResId, lancamentoList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutResId, null)

        val tvValor = view.findViewById<TextView>(R.id.tvValor)
        val tvDesc = view.findViewById<TextView>(R.id.tvDesc)
        val tvData = view.findViewById<TextView>(R.id.tvData)
        val tvCategoria = view.findViewById<TextView>(R.id.tvCategoria)
        val layout = view.findViewById<View>(R.id.layout)

        val lancamento = lancamentoList[position]

        var mes = ""

        when (lancamento.data.substring(3,5)) {
            "01" -> {
                mes = "Jan"
            }
            "02" -> {
                mes = "Fev"
            }
            "03" -> {
                mes = "Mar"
            }
            "04" -> {
                mes = "Abr"
            }
            "05" -> {
                mes = "Mai"
            }
            "06" -> {
                mes = "Jun"
            }
            "07" -> {
                mes = "Jul"
            }
            "08" -> {
                mes = "Ago"
            }
            "09" -> {
                mes = "Set"
            }
            "10" -> {
                mes = "Out"
            }
            "11" -> {
                mes = "Nov"
            }
            "12" -> {
                mes = "Dez"
            }
        }

        tvValor.text = lancamento.valor
        tvDesc.text = lancamento.desc
        tvData.text = lancamento.data.substring(0,2) + " $mes"
        tvCategoria.text = lancamento.categoria

        layout.setOnClickListener {
            showUpdateDialog(lancamento)
        }

        return view
    }

    fun showUpdateDialog(lancamento: Lancamento) {
        val builder = AlertDialog.Builder(mCtx)

        val inflater = LayoutInflater.from(mCtx)

        val view = inflater.inflate(R.layout.update_lancamento, null)

        val etValor = view.findViewById<EditText>(R.id.etValor)
        val tvSave = view.findViewById<TextView>(R.id.tvSave)
        val tvDelete = view.findViewById<TextView>(R.id.tvDelete)

        var oldReais: String
        var text: String

        etValor.setText(lancamento.valor)

        builder.setView(view)

        oldReais = lancamento.valor

        if (lancamento.valor.substring(0,1) == "-") {
            etValor.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {
                    if(s.toString().length > oldReais.length && s.toString().length < 15) {
                        etValor.removeTextChangedListener(this)

                        if(oldReais == "") {
                            val num = s.toString().toFloat() / 100
                            text = "- R$ " + num.toString().replace(".",",")
                        } else {
                            val strConvert = (s.toString().trim().replace(",",".").replace("- R$", "")).toDouble() * 10
                            val str = (java.lang.String.format("%.2f", strConvert))
                            text = "- R$ " + str.replace(".",",")
                        }

                        etValor.setText(text)
                        etValor.setSelection(text.length)

                        oldReais = text

                        etValor.addTextChangedListener(this)
                    } else if (s.toString().length < 15){
                        etValor.removeTextChangedListener(this)

                        etValor.setText("")
                        oldReais = ""

                        etValor.addTextChangedListener(this)
                    }
                }

                override fun beforeTextChanged(s: CharSequence, start: Int,
                                               count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence, start: Int,
                                           before: Int, count: Int) {
                }
            })
        } else {
            etValor.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {
                    if(s.toString().length > oldReais.length && s.toString().length < 13) {
                        etValor.removeTextChangedListener(this)

                        if(oldReais == "") {
                            val num = s.toString().toFloat() / 100
                            text = "R$ " + num.toString().replace(".",",")
                        } else {
                            val strConvert = (s.toString().trim().replace(",",".").replace("R$", "")).toDouble() * 10
                            val str = (java.lang.String.format("%.2f", strConvert))
                            text = "R$ " + str.replace(".",",")
                        }

                        etValor.setText(text)
                        etValor.setSelection(text.length)

                        oldReais = text

                        etValor.addTextChangedListener(this)
                    } else if (s.toString().length < 13){
                        etValor.removeTextChangedListener(this)

                        etValor.setText("")
                        oldReais = ""

                        etValor.addTextChangedListener(this)
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

        val dbLancamento = FirebaseDatabase.getInstance().getReference("lancamento")

        val alert = builder.create()

        tvSave.setOnClickListener {
            val newValor = etValor.text.toString()

            val lancamento = Lancamento(lancamento.id, lancamento.desc, newValor, lancamento.data, lancamento.categoria, lancamento.usuario)

            dbLancamento.child(lancamento.id).setValue(lancamento)

            Toast.makeText(mCtx, "Valor alterado", Toast.LENGTH_SHORT).show()

            alert.hide()
        }

        tvDelete.setOnClickListener {
            dbLancamento.child(lancamento.id).removeValue()

            Toast.makeText(mCtx, "Lançamento excluído", Toast.LENGTH_SHORT).show()

            alert.hide()
        }


        alert.show()
    }

}