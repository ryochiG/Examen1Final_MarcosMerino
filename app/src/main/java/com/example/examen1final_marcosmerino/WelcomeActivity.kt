package com.example.examen1final_marcosmerino

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.examen1final_marcosmerino.adapter.AdaptadorAficiones
import com.example.examen1final_marcosmerino.data.AppDatabase
import com.example.examen1final_marcosmerino.databinding.ActivityWelcomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.examen1final_marcosmerino.model.Aficion
import com.example.examen1final_marcosmerino.model.User

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var baseDatos: AppDatabase
    private var emailUsuario: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome)
        setSupportActionBar(binding.toolbar)
        baseDatos = AppDatabase.getDatabase(this)

        val prefs = getSharedPreferences("sesion_pref", Context.MODE_PRIVATE)
        emailUsuario = prefs.getString("email_usuario", "") ?: ""

        if (emailUsuario.isEmpty()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        binding.txtEmailClickable.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(emailUsuario))
                putExtra(Intent.EXTRA_SUBJECT, "Asociación Rock")
            }
            try {
                startActivity(Intent.createChooser(intent, "Enviar"))
            } catch (e: Exception) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        cargarDatosUsuario()
    }

    private fun cargarDatosUsuario() {
        lifecycleScope.launch(Dispatchers.IO) {
            val usuario = baseDatos.userDao().getUserByEmail(emailUsuario)
            withContext(Dispatchers.Main) {
                if (usuario != null) {
                    binding.usuario = usuario

                    if (usuario.rol == "Follower") {
                        binding.layoutRaiz.setBackgroundColor(Color.parseColor("#D4EDDA"))
                    } else {
                        binding.layoutRaiz.setBackgroundColor(Color.parseColor("#FFF3CD"))
                    }

                    if (usuario.fotoUri.isNotEmpty()) {
                        try {
                            binding.imgFotoPerfil.setImageURI(Uri.parse(usuario.fotoUri))
                        } catch (e: Exception) {
                            binding.imgFotoPerfil.setImageResource(android.R.drawable.ic_menu_gallery)
                        }
                    }

                    cargarAficiones()
                }
            }
        }
    }

    private fun cargarAficiones() {
        val prefs = getSharedPreferences("sesion_pref", Context.MODE_PRIVATE)
        val aficiones = listOf(
            Aficion(
                "Rock clásico",
                android.R.drawable.ic_media_play,
                prefs.getBoolean("${emailUsuario}_rock", false)
            ),
            Aficion(
                "Jive",
                android.R.drawable.ic_menu_compass,
                prefs.getBoolean("${emailUsuario}_jive", false)
            ),
            Aficion(
                "Lindy hop",
                android.R.drawable.ic_menu_directions,
                prefs.getBoolean("${emailUsuario}_lindy", false)
            )
        )

        val adaptador = AdaptadorAficiones(this, aficiones) { aficion, seleccionada ->
            val edit = prefs.edit()
            when (aficion.nombre) {
                "Rock clásico" -> edit.putBoolean("${emailUsuario}_rock", seleccionada)
                "Jive" -> edit.putBoolean("${emailUsuario}_jive", seleccionada)
                "Lindy hop" -> edit.putBoolean("${emailUsuario}_lindy", seleccionada)
            }
            edit.apply()
        }

        binding.lstAficiones.adapter = adaptador
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_editar -> {
                val intent = Intent(this, PerfilActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_logout -> {
                val prefs = getSharedPreferences("sesion_pref", Context.MODE_PRIVATE)
                prefs.edit().remove("email_usuario").apply()

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
