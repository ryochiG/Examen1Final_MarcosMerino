package com.example.examen1final_marcosmerino

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.examen1final_marcosmerino.data.AppDatabase
import com.example.examen1final_marcosmerino.databinding.ActivityLoginBinding
import com.example.examen1final_marcosmerino.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var fotoSeleccionadaUri: String = ""
    private lateinit var baseDatos: AppDatabase

    private val seleccionFotoLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            try {
                contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) {}
            fotoSeleccionadaUri = it.toString()
            binding.txtFotoInfo.text = "OK"
            binding.imgFotoPreview.visibility = View.VISIBLE
            binding.imgFotoPreview.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        baseDatos = AppDatabase.getDatabase(this)

        val prefs = getSharedPreferences("sesion_pref", Context.MODE_PRIVATE)
        val emailActivo = prefs.getString("email_usuario", null)
        if (emailActivo != null) {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        binding.btnIrRegistro.setOnClickListener {
            binding.layoutLogin.visibility = View.GONE
            binding.layoutRegistro.visibility = View.VISIBLE
        }

        binding.btnIrLogin.setOnClickListener {
            binding.layoutRegistro.visibility = View.GONE
            binding.layoutLogin.visibility = View.VISIBLE
        }

        binding.btnSeleccionarFoto.setOnClickListener {
            seleccionFotoLauncher.launch("image/*")
        }

        binding.btnAcceder.setOnClickListener {
            val email = binding.edtLoginEmail.text.toString().trim()
            val pass = binding.edtLoginPassword.text.toString().trim()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val usuario = baseDatos.userDao().getUserByEmail(email)
                withContext(Dispatchers.Main) {
                    if (usuario != null && usuario.password == pass) {
                        val edit = prefs.edit()
                        edit.putString("email_usuario", email)
                        edit.apply()

                        val intent = Intent(this@LoginActivity, WelcomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.btnRegistrar.setOnClickListener {
            val nombre = binding.edtRegNombre.text.toString().trim()
            val apellidos = binding.edtRegApellidos.text.toString().trim()
            val email = binding.edtRegEmail.text.toString().trim()
            val pass = binding.edtRegPassword.text.toString().trim()
            val esFollower = binding.rbFollower.isChecked
            val rol = if (esFollower) "Follower" else "Leader"

            if (nombre.isEmpty() || apellidos.isEmpty() || email.isEmpty() || pass.isEmpty() || fotoSeleccionadaUri.isEmpty()) {
                Toast.makeText(this, "Incompleto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val existente = baseDatos.userDao().getUserByEmail(email)
                withContext(Dispatchers.Main) {
                    if (existente != null) {
                        Toast.makeText(this@LoginActivity, "Duplicado", Toast.LENGTH_SHORT).show()
                    } else {
                        val nuevoUsuario = User(
                            nombre = nombre,
                            apellidos = apellidos,
                            email = email,
                            rol = rol,
                            password = pass,
                            fotoUri = fotoSeleccionadaUri
                        )
                        lifecycleScope.launch(Dispatchers.IO) {
                            baseDatos.userDao().insertUser(nuevoUsuario)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@LoginActivity, "OK", Toast.LENGTH_SHORT).show()
                                binding.layoutRegistro.visibility = View.GONE
                                binding.layoutLogin.visibility = View.VISIBLE
                                binding.edtLoginEmail.setText(email)
                                binding.edtLoginPassword.setText(pass)
                            }
                        }
                    }
                }
            }
        }
    }
}
