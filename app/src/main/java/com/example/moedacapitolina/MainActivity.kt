package com.example.moedacapitolina // Confirme se o pacote é esse mesmo!

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val coinImage: ImageView = findViewById(R.id.coin_image)
        val resultText: TextView = findViewById(R.id.result_text)

        coinImage.setOnClickListener {
            flipCoin(coinImage, resultText)
        }
    }

    private fun flipCoin(coinImage: ImageView, resultText: TextView) {
        // Trava a moeda e limpa o texto
        coinImage.isEnabled = false
        resultText.text = ""

        val isCara = Random.nextBoolean()

        // PARTE 1: Jogar para o alto (Sobe, dá zoom e gira 3 vezes muito rápido)
        coinImage.animate()
            .translationY(-400f) // Sobe 400 pixels na tela
            .scaleX(1.3f).scaleY(1.3f) // Zoom simulando a altura
            .rotationYBy(1080f) // 1080 graus = 3 voltas completas
            .setDuration(400) // Tempo de subida (0.4 segundos)
            .withEndAction {

                // No ponto mais alto (no ar), a gente troca a imagem secretamente
                if (isCara) {
                    coinImage.setImageResource(R.drawable.moeda_cara)
                } else {
                    coinImage.setImageResource(R.drawable.moeda_coroa)
                }

                // PARTE 2: A Queda (Desce, tira o zoom e gira mais 3 vezes)
                coinImage.animate()
                    .translationY(0f) // Volta pra mesa (posição original)
                    .scaleX(1f).scaleY(1f) // Tamanho normal
                    .rotationYBy(1080f)
                    .setDuration(400) // Tempo de descida
                    .withEndAction {

                        // O IMPACTO: Tremer o celular quando a moeda "bate na mesa"
                        triggerVibration()

                        // Revela o resultado
                        if (isCara) {
                            resultText.text = "CARA!\nConsulte a ação no jogo."
                        } else {
                            resultText.text = "COROA!\nConsulte a ação no jogo."
                        }

                        // Libera pro próximo lançamento
                        coinImage.isEnabled = true
                    }
                    .start()
            }
            .start()
    }

    // Função separada e limpa só para lidar com a vibração
    private fun triggerVibration() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Vibração moderna (80 milissegundos, pancada seca)
                vibrator.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                // Vibração para celulares antigos
                @Suppress("DEPRECATION")
                vibrator.vibrate(80)
            }
        }
    }
}