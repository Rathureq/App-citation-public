package fr.citations.app

import android.content.Context
import java.time.LocalDate

/** Compteur du jour et paliers, partagés entre l'application et la notification. */
object Gauge {
    val TIERS = listOf(5 to "Lancé", 10 to "Régulier", 20 to "Sérieux", 35 to "Assidu", 50 to "Maître")
    val MAX = TIERS.last().first

    private const val PREFS = "gauge"

    fun save(context: Context, count: Int) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString("date", LocalDate.now().toString())
            .putInt("count", count)
            .apply()
    }

    /** Nombre de citations révisées aujourd'hui. Revient à 0 dès que la date change. */
    fun today(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return if (prefs.getString("date", null) == LocalDate.now().toString()) prefs.getInt("count", 0) else 0
    }

    fun title(n: Int): String {
        val s = if (n > 1) "s" else ""
        return "$n citation$s révisée$s aujourd’hui"
    }

    fun line(n: Int): String {
        val reached = TIERS.lastOrNull { n >= it.first }
        val next = TIERS.firstOrNull { n < it.first }
        return when {
            next == null -> "Palier maximum atteint : ${reached?.second}"
            reached == null -> "Encore ${next.first - n} pour le palier ${next.second}"
            else -> "Palier ${reached.second} atteint · encore ${next.first - n} pour ${next.second}"
        }
    }

    fun tiersText(): String = "Paliers : " + TIERS.joinToString(" · ") { it.first.toString() }
}
