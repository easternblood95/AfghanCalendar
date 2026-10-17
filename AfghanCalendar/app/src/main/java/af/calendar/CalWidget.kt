package af.calendar

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class CalWidget : AppWidgetProvider() {

    override fun onUpdate(c: Context, m: AppWidgetManager, ids: IntArray) {
        for (id in ids) m.updateAppWidget(id, build(c))
    }

    override fun onReceive(c: Context, i: Intent) {
        super.onReceive(c, i)
        when (i.action) {
            Intent.ACTION_DATE_CHANGED, Intent.ACTION_TIME_SET, Intent.ACTION_TIMEZONE_CHANGED -> updateAll(c)
        }
    }

    companion object {
        fun updateAll(c: Context) {
            val m = AppWidgetManager.getInstance(c)
            val ids = m.getAppWidgetIds(ComponentName(c, CalWidget::class.java))
            for (id in ids) m.updateAppWidget(id, build(c))
        }

        private fun build(c: Context): RemoteViews {
            val ps = c.getSharedPreferences("p", Context.MODE_PRIVATE).getBoolean("ps", false)
            val today = LocalDate.now()
            val j = Jalali.fromGregorian(today)
            val months = if (ps) Jalali.pashto else Jalali.dari
            val week = if (ps) Jalali.weekPashto else Jalali.weekDari

            val rv = RemoteViews(c.packageName, R.layout.widget)
            rv.setTextViewText(R.id.weekday, week[Jalali.weekIndex(today)])
            rv.setTextViewText(R.id.day, Jalali.digits(j.d))
            rv.setTextViewText(R.id.month, "${months[j.m - 1]} ${Jalali.digits(j.y)}")
            rv.setTextViewText(R.id.greg, today.format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH)))

            val hol = Holidays.of(today, ps)
            rv.setViewVisibility(R.id.holiday, if (hol != null) View.VISIBLE else View.GONE)
            if (hol != null) rv.setTextViewText(R.id.holiday, hol)

            val pi = PendingIntent.getActivity(c, 0, Intent(c, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
            rv.setOnClickPendingIntent(R.id.root, pi)
            return rv
        }
    }
}
