package com.example.examen1final_marcosmerino.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import com.example.examen1final_marcosmerino.R
import com.example.examen1final_marcosmerino.databinding.ItemAficionBinding
import com.example.examen1final_marcosmerino.model.Aficion

class AdaptadorAficiones(
    private val contexto: Context,
    private val lista: List<Aficion>,
    private val onCambioSeleccion: (Aficion, Boolean) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = lista.size

    override fun getItem(posicion: Int): Any = lista[posicion]

    override fun getItemId(posicion: Int): Long = posicion.toLong()

    override fun getView(posicion: Int, vistaConvertida: View?, padre: ViewGroup?): View {
        val binding: ItemAficionBinding
        val vista: View

        if (vistaConvertida == null) {
            val inflador = LayoutInflater.from(contexto)
            binding = DataBindingUtil.inflate(inflador, R.layout.item_aficion, padre, false)
            vista = binding.root
            vista.tag = binding
        } else {
            vista = vistaConvertida
            binding = vista.tag as ItemAficionBinding
        }

        val elemento = lista[posicion]
        binding.aficion = elemento
        binding.imgIcono.setImageResource(elemento.iconoRes)

        binding.chkSeleccion.setOnCheckedChangeListener(null)
        binding.chkSeleccion.isChecked = elemento.seleccionada

        binding.chkSeleccion.setOnCheckedChangeListener { _, estaChequeado ->
            elemento.seleccionada = estaChequeado
            onCambioSeleccion(elemento, estaChequeado)
        }

        return vista
    }
}
