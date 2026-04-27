package com.example.moedacapitolina // Confirme se o pacote é esse mesmo!

import android.content.Context
import android.os.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class CapituModeActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capitu_mode)

        val eyesImage: ImageView = findViewById(R.id.eyes_image)

        // Vibração longa de entrada para o susto inicial
        triggerGlitchVibration(500)

        // Começa a loucura visual
        startGlitchLoop(eyesImage)

        // O CRONÔMETRO: Fecha a tela automaticamente após 3 segundos (3000 milissegundos)
        handler.postDelayed({
            finish()
        }, 3000)
    }

    private fun startGlitchLoop(view: ImageView) {
        handler.post(object : Runnable {
            override fun run() {
                val glitchType = Random.nextInt(4)
                when (glitchType) {
                    0 -> view.alpha = if (Random.nextBoolean()) 0.05f else 0.7f
                    1 -> view.translationX = (Random.nextInt(40) - 20).toFloat()
                    2 -> {
                        view.scaleX = 1f + (Random.nextFloat() * 0.2f)
                        // Vibração curta e seca durante o tremor visual
                        triggerGlitchVibration(30)
                    }
                    3 -> view.translationY = (Random.nextInt(20) - 10).toFloat()
                }
                // Continua o loop do glitch
                handler.postDelayed(this, Random.nextLong(30, 150))
            }
        })
    }

    private fun triggerGlitchVibration(duration: Long) {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(duration)
            }
        }
    }

    // Limpa a memória e desliga a tremedeira na hora que a tela fecha
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}