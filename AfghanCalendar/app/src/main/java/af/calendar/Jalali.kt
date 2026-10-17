package af.calendar

import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.temporal.ChronoField

data class JDate(val y: Int, val m: Int, val d: Int)

object Jalali {
    private val gdm = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)

    val dari = listOf("حمل", "ثور", "جوزا", "سرطان", "اسد", "سنبله", "میزان", "عقرب", "قوس", "جدی", "دلو", "حوت")
    val pashto = listOf("وری", "غويی", "غبرګولی", "چنګاښ", "زمری", "وږی", "تله", "لړم", "ليندۍ", "مرغومی", "سلواغه", "کب")
    val latin = listOf("Hamal", "Sawr", "Jawza", "Saratan", "Asad", "Sunbula", "Mizan", "Aqrab", "Qaus", "Jadi", "Dalw", "Hut")

    // Index 0 = Saturday ... 6 = Friday
    val weekDari = listOf("شنبه", "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنجشنبه", "جمعه")
    val weekPashto = listOf("شنبه", "یکشنبه", "دوشنبه", "درېشنبه", "څلورشنبه", "پنجشنبه", "جمعه")
    val shortDari = listOf("ش", "ی", "د", "س", "چ", "پ", "ج")
    val shortPashto = listOf("ش", "ی", "د", "ډ", "څ", "پ", "ج")

    val hijriMonths = listOf("محرم", "صفر", "ربیع‌الاول", "ربیع‌الثانی", "جمادی‌الاول", "جمادی‌الثانی",
        "رجب", "شعبان", "رمضان", "شوال", "ذی‌القعده", "ذی‌الحجه")

    fun digits(n: Int): String = n.toString().map { if (it in '0'..'9') '۰' + (it - '0') else it }.joinToString("")

    /** Saturday = 0 ... Friday = 6 */
    fun weekIndex(g: LocalDate) = (g.dayOfWeek.value + 1) % 7

    fun fromGregorian(g: LocalDate): JDate {
        val gy = g.year; val gm = g.monthValue; val gd = g.dayOfMonth
        val gy2 = if (gm > 2) gy + 1 else gy
        var days = 355666 + 365 * gy + (gy2 + 3) / 4 - (gy2 + 99) / 100 + (gy2 + 399) / 400 + gd + gdm[gm - 1]
        var jy = -1595 + 33 * (days / 12053)
        days %= 12053
        jy += 4 * (days / 1461)
        days %= 1461
        if (days > 365) { jy += (days - 1) / 365; days = (days - 1) % 365 }
        return if (days < 186) JDate(jy, 1 + days / 31, 1 + days % 31)
        else JDate(jy, 7 + (days - 186) / 30, 1 + (days - 186) % 30)
    }

    fun toGregorian(jy0: Int, jm: Int, jd: Int): LocalDate {
        val jy = jy0 + 1595
        var days = -355668 + 365 * jy + (jy / 33) * 8 + ((jy % 33) + 3) / 4 + jd +
            (if (jm < 7) (jm - 1) * 31 else (jm - 7) * 30 + 186)
        var gy = 400 * (days / 146097)
        days %= 146097
        if (days > 36524) {
            days--
            gy += 100 * (days / 36524)
            days %= 36524
            if (days >= 365) days++
        }
        gy += 4 * (days / 1461)
        days %= 1461
        if (days > 365) { gy += (days - 1) / 365; days = (days - 1) % 365 }
        return LocalDate.ofYearDay(gy, days + 1)
    }

    fun monthLength(y: Int, m: Int): Int {
        val ny = if (m == 12) y + 1 else y
        val nm = if (m == 12) 1 else m + 1
        return (toGregorian(ny, nm, 1).toEpochDay() - toGregorian(y, m, 1).toEpochDay()).toInt()
    }

    /** Lunar Hijri (Umm al-Qura) text */
    fun hijriText(g: LocalDate): String {
        val h = HijrahDate.from(g)
        val y = h.get(ChronoField.YEAR)
        val m = h.get(ChronoField.MONTH_OF_YEAR)
        val d = h.get(ChronoField.DAY_OF_MONTH)
        return "${digits(d)} ${hijriMonths[m - 1]} ${digits(y)}"
    }
}

object Holidays {
    /**
     * Edit this list to match the official calendar you follow.
     * Lunar holidays use Umm al-Qura and may differ by +-1 day from Afghan moon sighting.
     */
    fun of(g: LocalDate, ps: Boolean): String? {
        val j = Jalali.fromGregorian(g)
        val h = HijrahDate.from(g)
        val hm = h.get(ChronoField.MONTH_OF_YEAR)
        val hd = h.get(ChronoField.DAY_OF_MONTH)
        val p: Pair<String, String>? = when {
            j.m == 1 && j.d == 1 -> "نوروز" to "نوروز"
            j.m == 2 && j.d == 8 -> "پیروزی جهاد" to "د جهاد بریا"
            j.m == 5 && j.d == 24 -> "پیروزی امارت اسلامی" to "د اسلامي امارت بریا"
            j.m == 5 && j.d == 28 -> "روز استقلال" to "د خپلواکۍ ورځ"
            hm == 10 && hd in 1..3 -> "عید فطر" to "د روژې اختر"
            hm == 12 && hd in 10..12 -> "عید قربان" to "د قربانۍ اختر"
            hm == 1 && hd == 10 -> "عاشورا" to "عاشورا"
            hm == 3 && hd == 12 -> "میلاد النبی ﷺ" to "د پیغمبر ﷺ زوکړه"
            else -> null
        }
        return p?.let { if (ps) it.second else it.first }
    }
}
