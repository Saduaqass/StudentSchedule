package kz.studentschedule.data

/** Local transcription of the supplied 3rd year, second shift timetable. */
enum class Weekday(val title: String, val shortTitle: String) {
    MONDAY("Дүйсенбі", "Дс"), TUESDAY("Сейсенбі", "Сс"), WEDNESDAY("Сәрсенбі", "Ср"),
    THURSDAY("Бейсенбі", "Бс"), FRIDAY("Жұма", "Жм")
}

data class Lesson(
    val group: String,
    val weekday: Weekday,
    val start: String,
    val end: String,
    val subject: String,
    val room: String? = null,
    val needsClarification: Boolean = false
)

val GROUPS = listOf("301", "302", "303", "304", "305", "306", "308", "309", "310", "311", "312", "313")
private val normalTimes = listOf("12:40" to "13:40", "13:50" to "14:50", "15:00" to "16:00", "16:10" to "17:10")
private val fridayTimes = listOf("11:30" to "12:30") + normalTimes
private val sourceMissing = "Мәліметті нақтылау қажет"

/* A dash is an empty source cell. Each ordinary day has four slots; Friday has five. */
private val cells: Map<String, Map<Weekday, List<String>>> = mapOf(
    "301" to mapOf(
        Weekday.MONDAY to listOf("Куратор сағаты", "КМ 5 ОН 5.5", "КМ 5 ОН 5.4", "ЖММ 1 ОН 1.1"),
        Weekday.TUESDAY to listOf("ЖММ 4 ОН 4.2", "КМ 5 ОН 5.2", "КМ 5 ОН 5.2", "КМ 5 ОН 5.4"),
        Weekday.WEDNESDAY to listOf("ЖММ 3 ОН 3.3", "ЖММ 4 ОН 4.1", "КМ 5 ОН 5.1", "КМ 5 ОН 5.1"),
        Weekday.THURSDAY to listOf("КМ 5 ОН 5.1", "КМ 5 ОН 5.1", "КМ 5 ОН 5.2", "КМ 5 ОН 5.2"),
        Weekday.FRIDAY to listOf("-", "КМ 5 ОН 5.3", "КМ 5 ОН 5.4", "КМ 5 ОН 5.4", "КМ 5 ОН 5.4")
    ),
    "302" to mapOf(
        Weekday.MONDAY to listOf("Кураторлық час", "ПМ 5 РО 5.2", "ПМ 5 РО 5.2", "ПМ 5 РО 5.1"),
        Weekday.TUESDAY to listOf("ПМ 5 РО 5.3", "ПМ 5 РО 5.2", "ООМ 1 РО 1.1", "ООМ 3 РО 3.3"),
        Weekday.WEDNESDAY to listOf("ПМ 5 РО 5.1", "ПМ 5 РО 5.2", "ПМ 5 РО 5.5", "-"),
        Weekday.THURSDAY to listOf("ООМ 4 РО 4.1", "ООМ 4 РО 4.2", "ПМ 5 РО 5.4", "ПМ 5 РО 5.4"),
        Weekday.FRIDAY to listOf("-", "ПМ 5 РО 5.4", "ПМ 5 РО 5.4", "ПМ 5 РО 5.1", "ПМ 5 РО 5.1")
    ),
    // The source merges the cells for these groups into one daily production-training block.
    "303" to emptyMap(), "304" to emptyMap(), "305" to emptyMap(),
    "306" to mapOf(
        Weekday.MONDAY to listOf("Куратор сағаты", "КМ 5 ОН 5.1 (К.С)", "КМ 5 ОН 5.1 (К.С)", "КМ 5 ОН 5.1 (К.С)"),
        Weekday.TUESDAY to listOf("КМ 5 ОН 5.4 (Г.Т)", "КМ 5 ОН 5.1 (К.С)", "КМ 5 ОН 5.1 (К.С)", "КМ 5 ОН 5.1 (К.С)"),
        Weekday.WEDNESDAY to listOf("КМ 5 ОН 5.2 (К.С)", "КМ 5 ОН 5.2 (К.С)", "КМ 5 ОН 5.2 (К.С)", "КМ 5 ОН 5.2 (К.С)"),
        Weekday.THURSDAY to listOf("ЖММ 3 ОН 3.3", "ЖММ 4 ОН 4.3", "КМ 5 ОН 5.3 (Г.Т)", "КМ 5 ОН 5.3 (Г.Т)"),
        Weekday.FRIDAY to listOf("ЖММ 2 ОН 2.1", "-", "КМ 5 ОН 5.4 (Г.Т)", "КМ 5 ОН 5.4 (Г.Т)", "ЖММ 1 ОН 1.1")
    ),
    "308" to mapOf(
        Weekday.MONDAY to listOf("КМ 4 ОН 4.1.2", "КМ 4 ОН 4.2 (ӨО)", "Кураторлық час", "ЖММ 1 ОН 1.2"),
        Weekday.TUESDAY to listOf("КМ 4 ОН 4.1.1", "КМ 4 ОН 4.1.1", "КМ 4 ОН 4.1.1", "КМ 4 ОН 4.2 (ӨО)"),
        Weekday.WEDNESDAY to listOf("КМ 4 ОН 4.2 (ӨО)", "КМ 4 ОН 4.1.2", "КМ 4 ОН 4.1.2", "КМ 4 ОН 4.2 (ӨО)"),
        Weekday.THURSDAY to listOf("КМ 4 ОН 4.1.1", "КМ 4 ОН 4.1.1", "КМ 4 ОН 4.2 (ӨО)", "КМ 4 ОН 4.2 (ӨО)"),
        Weekday.FRIDAY to listOf("КМ 4 ОН 4.2", "КМ 4 ОН 4.2", "КМ 4 ОН 4.2", "КМ 4 ОН 4.2", "КМ 4 ОН 4.2")
    ),
    "309" to mapOf(
        Weekday.MONDAY to listOf("ПМ 4 РО 4.2 (ПО)", "Кураторский час", "ПМ 4 РО 4.1.1 (А.С)", "ПМ 4 РО 4.2 (ПО)"),
        Weekday.TUESDAY to listOf("ПМ 4 РО 4.2 (ПО)", "ПМ 4 РО 4.2 (ПО)", "ПМ 4 РО 4.1.1 (А.С)", "ПМ 4 РО 4.2 (ПО)"),
        Weekday.WEDNESDAY to listOf("ПМ 4 РО 4.1.1 (А.С)", "ПМ 4 РО 4.2 (ПО)", "ПМ 4 РО 4.1.2 (А.С)", "ПМ 4 РО 4.1.2 (А.С)"),
        Weekday.THURSDAY to listOf("ПМ 4 РО 4.1.2 (А.С)", "ПМ 4 РО 4.1.1 (А.С)", "ООМ 1 РО 1.1", "ПМ 4 РО 4.2 (ПО)"),
        Weekday.FRIDAY to listOf("-", "ПМ 4 РО 4.2 (А.С)", "ПМ 4 РО 4.1.2 (А.С)", "ООМ 1 РО 1.1", "ПМ 4 РО 4.2 (ПО)")
    ),
    "310" to mapOf(
        Weekday.MONDAY to listOf("ЖММ 4 ОН 4.3", "КМ 5 ОН 5.2", "КМ 6 ОН 6.1", "Куратор сағаты"),
        Weekday.TUESDAY to listOf("КМ 6 ОН 6.1", "КМ 6 ОН 6.1 (ӨО)", "КМ 6 ОН 6.3", "КМ 6 ОН 6.1"),
        Weekday.WEDNESDAY to listOf("КМ 6 ОН 6.1 (ӨО)", "КМ 6 ОН 6.1 (ӨО)", "КМ 6 ОН 6.3", "КМ 6 ОН 6.1"),
        Weekday.THURSDAY to listOf("КМ 1 ОН 1.1", "КМ 6 ОН 6.1 (ӨО)", "КМ 6 ОН 6.3", "КМ 6 ОН 6.3"),
        Weekday.FRIDAY to listOf("-", "КМ 5 ОН 5.5", "КМ 5 ОН 5.5", "КМ 6 ОН 6.1 (ӨО)", "КМ 6 ОН 6.1 (ӨО)")
    ),
    "311" to mapOf(
        Weekday.MONDAY to listOf("Кураторский час", "ПМ 6 РО 6.1 (ПО)", "ООМ 1 РО 1.1 (Р.Ф)", "ПМ 6 РО 6.1 (ПО)"),
        Weekday.TUESDAY to listOf("ПМ 6 РО 6.1 (ПО)", "ПМ 6 РО 6.1 (ПО)", "ПМ 6 РО 6.1 (ПО)", "ПМ 6 РО 6.1 (ПО)"),
        Weekday.WEDNESDAY to listOf("ПМ 6 РО 6.3", "ООМ 4 РО 4.3", "ПМ 6 РО 6.1 (ПО)", "ПМ 6 РО 6.1 (ПО)"),
        Weekday.THURSDAY to listOf("ПМ 6 РО 6.1 (ПО)", "ПМ 6 РО 6.1 (ПО)", "ПМ 6 РО 6.1 (ПО)", "ПМ 6 РО 6.1 (ПО)"),
        Weekday.FRIDAY to listOf("-", "ПМ 5 РО 5.3 (ДИ)", "ПМ 5 РО 5.3 (ДИ)", "ПМ 6 РО 6.4 (ДИ)", "ПМ 6 РО 6.4 (ДИ)")
    ),
    "312" to mapOf(
        Weekday.MONDAY to listOf("Куратор сағаты", "КМ 5 ОН 5.1", "ЖММ 3 ОН 3.3", "КМ 5 ОН 5.2"),
        Weekday.TUESDAY to listOf("КМ 5 ОН 5.3", "КМ 5 ОН 5.3", "КМ 5 ОН 5.3", "КМ 5 ОН 5.3"),
        Weekday.WEDNESDAY to listOf("КМ 5 ОН 5.3 (ӨО)", "КМ 5 ОН 5.3 (ӨО)", "КМ 5 ОН 5.3 (ӨО)", "КМ 5 ОН 5.3 (ӨО)"),
        Weekday.THURSDAY to listOf("КМ 5 ОН 5.2", "КМ 5 ОН 5.2", "КМ 5 ОН 5.2", "КМ 5 ОН 5.2"),
        Weekday.FRIDAY to listOf("ЖММ 1 ОН 1.1", "КМ 5 ОН 5.1", "КМ 5 ОН 5.1", "КМ 5 ОН 5.3", "КМ 5 ОН 5.3")
    ),
    "313" to mapOf(
        Weekday.MONDAY to listOf("Куратор сағаты", "КМ 5 ОН 5.1", "КМ 5 ОН 5.2", "КМ 5 ОН 5.2"),
        Weekday.TUESDAY to listOf("КМ 5 ОН 5.3", "КМ 5 ОН 5.3", "КМ 5 ОН 5.3", "КМ 5 ОН 5.3"),
        Weekday.WEDNESDAY to listOf("КМ 5 ОН 5.3 (ӨО)", "КМ 5 ОН 5.3 (ӨО)", "КМ 5 ОН 5.3 (ӨО)", "КМ 5 ОН 5.3 (ӨО)"),
        Weekday.THURSDAY to listOf("КМ 5 ОН 5.2", "КМ 5 ОН 5.2", "ЖММ 3 ОН 3.3", "КМ 5 ОН 5.3 (ӨО)"),
        Weekday.FRIDAY to listOf("ЖММ 1 ОН 1.1", "КМ 5 ОН 5.1", "КМ 5 ОН 5.1", "КМ 5 ОН 5.3 (ӨО)", "КМ 5 ОН 5.3 (ӨО)")
    )
)

fun lessonsFor(group: String, weekday: Weekday): List<Lesson> {
    if (group in listOf("303", "304", "305")) {
        val (start, end) = if (weekday == Weekday.FRIDAY) "11:30" to "17:10" else "12:40" to "17:10"
        val subject = if (group == "305") "Производственное обучение" else "Өндірістік оқыту"
        return listOf(Lesson(group, weekday, start, end, subject))
    }
    val times = if (weekday == Weekday.FRIDAY) fridayTimes else normalTimes
    return cells[group]?.get(weekday).orEmpty().mapIndexedNotNull { index, subject ->
        if (subject == "-" || index >= times.size) null else {
            val (start, end) = times[index]
            Lesson(group, weekday, start, end, if (subject == "?") sourceMissing else subject, needsClarification = subject == "?")
        }
    }
}

fun allLessons(group: String): List<Lesson> = Weekday.entries.flatMap { lessonsFor(group, it) }
