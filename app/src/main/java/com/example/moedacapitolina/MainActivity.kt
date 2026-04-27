package com.example.moedacapitolina // Confirme o seu pacote!

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button

class MainActivity : AppCompatActivity(), SensorEventListener {

    private var secretCounter = 0

    // Variáveis do Sensor
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    // Variáveis do Chacoalhão (Shake)
    private val SHAKE_THRESHOLD = 12.0f
    private var lastShakeTime: Long = 0

    // Variáveis Globais da tela
    private lateinit var coinImage: ImageView
    private lateinit var resultText: TextView
    private lateinit var quoteText: TextView // Nossa nova variável para a citação
    private var isFlipping = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        coinImage = findViewById(R.id.coin_image)
        resultText = findViewById(R.id.result_text)
        quoteText = findViewById(R.id.quote_text) // Conectando o texto do XML
        val titleText: TextView = findViewById(R.id.text_title_game)
        val btnSobre: TextView = findViewById(R.id.btn_sobre)

        // Quando clicar em SOBRE, chama a função da janelinha
        btnSobre.setOnClickListener {
            mostrarDialogoSobre()
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        coinImage.setOnClickListener {
            if (!isFlipping) {
                flipCoin(coinImage, resultText)
            }
        }

        titleText.setOnClickListener {
            secretCounter++
            if (secretCounter >= 5) {
                secretCounter = 0
                val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (vibrator.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(200)
                    }
                }
                val intent = Intent(this, CapituModeActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || isFlipping) return

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            coinImage.rotationY = x * -2f
            coinImage.rotationX = (y - 5f) * 2f

            val acceleration = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat() - SensorManager.GRAVITY_EARTH

            if (acceleration > SHAKE_THRESHOLD) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastShakeTime > 1000) {
                    lastShakeTime = currentTime
                    flipCoin(coinImage, resultText)
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun flipCoin(coinImage: ImageView, resultText: TextView) {
        isFlipping = true
        coinImage.isEnabled = false
        resultText.text = ""

        // FADE OUT: Esconde a citação antiga rapidamente antes da moeda subir
        quoteText.animate().alpha(0f).setDuration(200).start()

        val isCara = Random.nextBoolean()

        coinImage.animate()
            .translationY(-400f)
            .scaleX(1.3f).scaleY(1.3f)
            .rotationYBy(1080f)
            .setDuration(400)
            .withEndAction {
                if (isCara) {
                    coinImage.setImageResource(R.drawable.moeda_cara)
                } else {
                    coinImage.setImageResource(R.drawable.moeda_coroa)
                }

                coinImage.animate()
                    .translationY(0f)
                    .scaleX(1f).scaleY(1f)
                    .rotationYBy(1080f)
                    .setDuration(400)
                    .withEndAction {

                        // Áudio Dinâmico
                        val sons = listOf(
                            R.raw.som_moeda_1,
                            R.raw.som_moeda_2,
                            R.raw.som_moeda_3,
                            R.raw.som_moeda_4
                        )
                        val somSorteado = sons.random()
                        val mediaPlayer = MediaPlayer.create(this@MainActivity, somSorteado)
                        mediaPlayer.start()
                        mediaPlayer.setOnCompletionListener { it.release() }

                        triggerVibration()

                        if (isCara) {
                            resultText.text = "CARA!\nConsulte a ação no jogo."
                        } else {
                            resultText.text = "COROA!\nConsulte a ação no jogo."
                        }

                        // === O NOVO EFEITO LITERÁRIO ===
                        val citacoes = listOf(
                            "\"O destino não é só dramaturgo, é também o seu próprio contra-regra.\"",
                            "\"A imaginação foi a companheira de toda a minha existência.\"",
                            "\"Olhos de ressaca... traziam não sei que fluido misterioso e enérgico.\"",
                            "\"A dúvida é a autora das insônias.\"",
                            "\"A mentira é muita vez tão involuntária como a respiração.\"",
                            "\"Tudo é aliado do homem que sabe querer.\"",
                            "\"Ora, há só um modo de escrever a própria história: é contá-la.\"",
                            "\"Nem tudo é claro na vida, nem na alma humana.\""
                        )

                        // Define o texto com uma citação aleatória do livro
                        quoteText.text = citacoes.random()
                        // FADE IN: Faz o texto aparecer suavemente durante 1.5 segundos (1500ms)
                        quoteText.animate().alpha(1f).setDuration(1500).start()

                        coinImage.isEnabled = true
                        isFlipping = false
                    }
                    .start()
            }
            .start()
    }

    private fun triggerVibration() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(80)
            }
        }
    }

    private fun mostrarDialogoSobre() {
        // Cria a caixa de diálogo
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true) // Permite fechar clicando fora dela
        dialog.setContentView(R.layout.dialog_sobre)

        // Deixa o fundo ao redor da janela transparente/escurecido
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Puxa o botão de fechar que está DENTRO da janela
        val btnFechar = dialog.findViewById<Button>(R.id.btn_fechar_sobre)
        btnFechar.setOnClickListener {
            dialog.dismiss() // Comando que fecha a janela
        }

        // Mostra a janela na tela
        dialog.show()
    }
}