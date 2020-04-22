package com.eduardo.economise

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase

class CategoriaAdapter (val mCtx: Context, val layoutResId: Int, val categoriaList: List<Categoria>)
    : ArrayAdapter<Categoria>(mCtx, layoutResId, categoriaList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutResId, null)

        val dbCategoria = FirebaseDatabase.getInstance().getReference("categoria")
        val categoria = categoriaList[position]

        val tvLabel = view.findViewById<TextView>(R.id.tvLabel)
        val ivDelete = view.findViewById<ImageView>(R.id.ivDelete)
        val tvTipo = view.findViewById<TextView>(R.id.tvTipo)

        ivDelete.setOnClickListener {
            dbCategoria.child(categoria.id).removeValue()

            Toast.makeText(mCtx, "Categoria removida", Toast.LENGTH_SHORT).show()
        }

        tvLabel.text = categoria.categoria
        tvTipo.text = categoria.tipo

        return view
    }

}