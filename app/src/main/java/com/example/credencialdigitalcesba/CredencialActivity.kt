package com.example.credencialdigitalcesba

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException

class CredencialActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credencial)

        val nombre = intent.getStringExtra("nombre") ?: ""
        val matricula = intent.getStringExtra("matricula") ?: ""
        val carrera = intent.getStringExtra("carrera") ?: ""
        val semestre = intent.getStringExtra("semestre") ?: ""
        val grupo = intent.getStringExtra("grupo") ?: ""
        val correo = intent.getStringExtra("correo") ?: ""
        val tipo = intent.getStringExtra("tipo") ?: ""
        val rutaFoto = intent.getStringExtra("foto")

        findViewById<TextView>(R.id.tvNombre).text = nombre
        findViewById<TextView>(R.id.tvTipo).text = tipo
        findViewById<TextView>(R.id.tvMatricula).text = "Matrícula: $matricula"
        findViewById<TextView>(R.id.tvCarrera).text = "Carrera: $carrera"
        findViewById<TextView>(R.id.tvSemestre).text = "Semestre/Cuatri: $semestre"
        findViewById<TextView>(R.id.tvGrupo).text = "Grupo: $grupo"
        findViewById<TextView>(R.id.tvCorreo).text = correo

        if (rutaFoto != null) {
            val foto = BitmapFactory.decodeFile(rutaFoto)
            findViewById<ImageView>(R.id.ivFotoCred).setImageBitmap(foto)
        }

        val contenidoQR = "Nombre: $nombre\nMatricula: $matricula\nCarrera: $carrera\nGrupo: $grupo"
        try {
            findViewById<ImageView>(R.id.ivQR).setImageBitmap(generarQR(contenidoQR))
        } catch (e: WriterException) {
            Toast.makeText(this, "No se pudo generar el QR", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnNueva).setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
            finish()
        }
    }

    private fun generarQR(texto: String): Bitmap {
        val tam = 500
        val matriz = MultiFormatWriter().encode(texto, BarcodeFormat.QR_CODE, tam, tam)
        val ancho = matriz.width
        val alto = matriz.height
        val pixeles = IntArray(ancho * alto)
        for (y in 0 until alto) {
            for (x in 0 until ancho) {
                pixeles[y * ancho + x] = if (matriz[x, y]) Color.BLACK else Color.WHITE
            }
        }
        val bmp = Bitmap.createBitmap(ancho, alto, Bitmap.Config.ARGB_8888)
        bmp.setPixels(pixeles, 0, ancho, 0, 0, ancho, alto)
        return bmp
    }
}