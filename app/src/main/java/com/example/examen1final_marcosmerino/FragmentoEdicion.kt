package com.example.examen1final_marcosmerino

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.examen1final_marcosmerino.data.AppDatabase
import com.example.examen1final_marcosmerino.databinding.FragmentoEdicionBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentoEdicion : Fragment() {

    interface OnDatosGuardadosListener {
        fun onDatosGuardados()
    }

    private lateinit var binding: FragmentoEdicionBinding
    private lateinit var baseDatos: AppDatabase
    private var emailUsuario: String = ""
    private var fotoSeleccionadaUri: String = ""
    private var listener: OnDatosGuardadosListener? = null

    private val seleccionFotoLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            try {
                requireContext().contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) {}
            fotoSeleccionadaUri = it.toString()
            binding.imgEdiPreview.setImageURI(it)
        }
    }

    companion object {
        fun newInstance(email: String): FragmentoEdicion {
            val fragmento = FragmentoEdicion()
            val args = Bundle()
            args.putString("email_usuario", email)
            fragmento.arguments = args
            return fragmento
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnDatosGuardadosListener) {
            listener = context
        }
    }

    override fun onCreateView(
        inflador: LayoutInflater,
        contenedor: ViewGroup?,
        estadoGuardado: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflador, R.layout.fragmento_edicion, contenedor, false)
        return binding.root
    }

    override fun onViewCreated(vista: View, estadoGuardado: Bundle?) {
        super.onViewCreated(vista, estadoGuardado)
        baseDatos = AppDatabase.getDatabase(requireContext())
        emailUsuario = arguments?.getString("email_usuario") ?: ""

        cargarDatos()

        binding.btnEdiFoto.setOnClickListener {
            seleccionFotoLauncher.launch("image/*")
        }

        binding.btnEdiGuardar.setOnClickListener {
            val nombre = binding.edtEdiNombre.text.toString().trim()
            val apellidos = binding.edtEdiApellidos.text.toString().trim()
            val pass = binding.edtEdiPassword.text.toString().trim()
            val esFollower = binding.rbEdiFollower.isChecked
            val rol = if (esFollower) "Follower" else "Leader"

            if (nombre.isEmpty() || apellidos.isEmpty() || pass.isEmpty()) {
                Toast.makeText(requireContext(), "Vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val usuarioOriginal = baseDatos.userDao().getUserByEmail(emailUsuario)
                if (usuarioOriginal != null) {
                    val usuarioActualizado = usuarioOriginal.copy(
                        nombre = nombre,
                        apellidos = apellidos,
                        rol = rol,
                        password = pass,
                        fotoUri = if (fotoSeleccionadaUri.isNotEmpty()) fotoSeleccionadaUri else usuarioOriginal.fotoUri
                    )
                    baseDatos.userDao().updateUser(usuarioActualizado)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Guardado", Toast.LENGTH_SHORT).show()
                        listener?.onDatosGuardados()
                    }
                }
            }
        }
    }

    private fun cargarDatos() {
        lifecycleScope.launch(Dispatchers.IO) {
            val usuario = baseDatos.userDao().getUserByEmail(emailUsuario)
            withContext(Dispatchers.Main) {
                if (usuario != null) {
                    binding.usuario = usuario
                    fotoSeleccionadaUri = usuario.fotoUri

                    if (usuario.rol == "Follower") {
                        binding.rbEdiFollower.isChecked = true
                    } else {
                        binding.rbEdiLeader.isChecked = true
                    }

                    if (usuario.fotoUri.isNotEmpty()) {
                        try {
                            binding.imgEdiPreview.setImageURI(Uri.parse(usuario.fotoUri))
                        } catch (e: Exception) {
                            binding.imgEdiPreview.setImageResource(android.R.drawable.ic_menu_gallery)
                        }
                    }
                }
            }
        }
    }
}
