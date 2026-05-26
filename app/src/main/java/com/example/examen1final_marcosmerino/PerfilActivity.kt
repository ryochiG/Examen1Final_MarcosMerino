package com.example.examen1final_marcosmerino

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.examen1final_marcosmerino.databinding.ActivityPerfilBinding

class PerfilActivity : AppCompatActivity(), FragmentoEdicion.OnDatosGuardadosListener {

    private lateinit var binding: ActivityPerfilBinding
    private var emailUsuario: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_perfil)
        setSupportActionBar(binding.toolbarPerfil)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbarPerfil.setNavigationOnClickListener {
            finish()
        }

        val prefs = getSharedPreferences("sesion_pref", Context.MODE_PRIVATE)
        emailUsuario = prefs.getString("email_usuario", "") ?: ""

        if (emailUsuario.isEmpty()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        if (savedInstanceState == null) {
            val fragmentoEdicion = FragmentoEdicion.newInstance(emailUsuario)

            supportFragmentManager.beginTransaction()
                .replace(R.id.contenedor_edicion, fragmentoEdicion)
                .commit()
        }
    }

    override fun onDatosGuardados() {
        finish()
    }
}
