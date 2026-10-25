package fr.citations.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

/** Remet la notification de l'objectif du jour après un redémarrage du téléphone. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        try {
            ContextCompat.startForegroundService(context, Intent(context, GaugeService::class.java))
        } catch (_: Exception) {
            // Démarrage refusé par le système : la notification reviendra à la prochaine ouverture.
        }
    }
}
