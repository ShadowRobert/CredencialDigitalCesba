package com.example.credencialdigitalcesba

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class FormularioActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etMatricula: EditText
    private lateinit var etGrupo: EditText
    private lateinit var etCorreo: EditText
    private lateinit var spCarrera: Spinner
    private lateinit var spSemestre: Spinner
    private lateinit var spTipo: Spinner
    private lateinit var ivFoto: ImageView
    private lateinit var btnFoto: Button
    private lateinit var btnGenerar: Button

    private var foto: Bitmap? = null

    private val camaraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                foto = bitmap
                ivFoto.setImageBitmap(bitmap)
                btnFoto.text = "Volver a tomar foto"
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario)

        etNombre = findViewById(R.id.etNombre)
        etMatricula = findViewById(R.id.etMatricula)
        etGrupo = findViewById(R.id.etGrupo)
        etCorreo = findViewById(R.id.etCorreo)
        spCarrera = findViewById(R.id.spCarrera)
        spSemestre = findViewById(R.id.spSemestre)
        spTipo = findViewById(R.id.spTipo)
        ivFoto = findViewById(R.id.ivFoto)
        btnFoto = findViewById(R.id.btnFoto)
        btnGenerar = findViewById(R.id.btnGenerar)

        btnFoto.setOnClickListener { camaraLauncher.launch(null) }
        btnGenerar.setOnClickListener { validarYGenerar() }
    }

    private fun validarYGenerar() {
        val nombre = etNombre.text.toString().trim()
        val matricula = etMatricula.text.toString().trim()
        val grupo = etGrupo.text.toString().trim()
        val correo = etCorreo.text.toString().trim()

        if (nombre.length < 5 || !Regex("[\\p{L} .]+").matches(nombre)) {
            etNombre.error = "Escribe tu nombre completo (solo letras)"
            etNombre.requestFocus()
            return
        }
        if (!Regex("[A-Za-z0-9]{5,15}").matches(matricula)) {
            etMatricula.error = "Matrícula inválida (5 a 15 letras o números)"
            etMatricula.requestFocus()
            return
        }
        if (spCarrera.selectedItemPosition == 0) {
            toast("Selecciona tu carrera")
            return
        }
        if (spSemestre.selectedItemPosition == 0) {
            toast("Selecciona tu semestre o cuatrimestre")
            return
        }
        if (grupo.isEmpty()) {
            etGrupo.error = "Escribe tu grupo"
            etGrupo.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreo.error = "Correo electrónico inválido"
            etCorreo.requestFocus()
            return
        }
        if (spTipo.selectedItemPosition == 0) {
            toast("Selecciona el tipo de usuario")
            return
        }
        val fotoTomada = foto
        if (fotoTomada == null) {
            toast("Debes tomar tu foto")
            return
        }

        val archivo = File(filesDir, "foto_credencial.jpg")
        try {
            FileOutputStream(archivo).use { out ->
                fotoTomada.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
        } catch (e: IOException) {
            toast("No se pudo guardar la foto")
            return
        }

        val intent = Intent(this, CredencialActivity::class.java).apply {
            putExtra("nombre", nombre)
            putExtra("matricula", matricula.uppercase())
            putExtra("carrera", spCarrera.selectedItem.toString())
            putExtra("semestre", spSemestre.selectedItem.toString())
            putExtra("grupo", grupo.uppercase())
            putExtra("correo", correo)
            putExtra("tipo", spTipo.selectedItem.toString())
            putExtra("foto", archivo.absolutePath)
        }
        startActivity(intent)
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}