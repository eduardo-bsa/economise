package com.eduardo.economise

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class DropAdapter (val mCtx: Context, val layoutResId: Int, val loteList: List<String>)
    : ArrayAdapter<String>(mCtx, layoutResId, loteList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutResId, null)

        val tvItens = view.findViewById<TextView>(R.id.tvItens)

        val resultado = loteList[position]

        tvItens.text = resultado

        return view
    }

}