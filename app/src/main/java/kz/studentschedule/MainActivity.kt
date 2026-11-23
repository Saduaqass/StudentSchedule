package kz.studentschedule

import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.ViewWeek
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import kz.studentschedule.data.GROUPS
import kz.studentschedule.data.Lesson
import kz.studentschedule.data.Weekday
import kz.studentschedule.data.allLessons
import kz.studentschedule.data.lessonsFor
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

private val Context.scheduleStore by preferencesDataStore(name = "student_schedule")
private val GroupKey = stringPreferencesKey("selected_group")

private val Navy = Color(0xFF193653)
private val Blue = Color(0xFF2563EB)
private val Canvas = Color(0xFFF8FAFC)
private val Ink = Color(0xFF1E293B)
private val LightGray = Color(0xFFE9EEF3)
private val Muted = Color(0xFF64748B)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val group by scheduleStore.data.map { it[GroupKey] ?: "310" }.collectAsState(initial = "310")
            val scope = rememberCoroutineScope()
            StudentScheduleTheme {
                ScheduleApp(group = group, onGroupChange = { next ->
                    scope.launch { scheduleStore.edit { it[GroupKey] = next } }
                })
            }
        }
    }
}

@Composable
private fun StudentScheduleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(primary = Navy, secondary = Blue, background = Canvas, surface = Color.White,
            onBackground = Ink, onSurface = Ink, surfaceVariant = LightGray),
        content = content
    )
}

private data class Destination(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
private val destinations = listOf(
    Destination("home", "Басты бет", Icons.Default.Home),
    Destination("schedule", "Кесте", Icons.Default.CalendarMonth),
    Destination("week", "Апта", Icons.Default.ViewWeek),
    Destination("today", "Бүгін", Icons.Default.Today),
    Destination("settings", "Баптау", Icons.Default.Settings)
)

@Composable
private fun ScheduleApp(group: String, onGroupChange: (String) -> Unit) {
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val route = backStack?.destination?.route ?: "home"
    Scaffold(
        containerColor = Canvas,
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 4.dp) {
                destinations.forEach { destination ->
                    NavigationBarItem(
                        selected = route == destination.route,
                        onClick = { nav.navigate(destination.route) {
                            popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        } },
                        icon = { Icon(destination.icon, contentDescription = destination.label) },
                        label = { Text(destination.label, fontSize = 10.sp, maxLines = 1) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(navController = nav, startDestination = "home", modifier = Modifier.padding(padding)) {
            composable("home") { HomeScreen(group, onGroupChange) { nav.navigate("schedule") } }
            composable("schedule") { ScheduleScreen(group) }
            composable("week") { WeekScreen(group) }
            composable("today") { TodayScreen(group) }
            composable("settings") { SettingsScreen(group, onGroupChange) { nav.navigate("home") { popUpTo("home") { inclusive = false }; launchSingleTop = true } } }
        }
    }
}

@Composable
private fun Page(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier.fillMaxSize().background(Canvas).padding(horizontal = 20.dp, vertical = 18.dp), content = content)
}

@Composable
private fun PageTitle(title: String, subtitle: String? = null) {
    Text(title, color = Navy, fontSize = 25.sp, fontWeight = FontWeight.Bold)
    if (subtitle != null) Text(subtitle, color = Muted, fontSize = 14.sp, modifier = Modifier.padding(top = 5.dp))
    Spacer(Modifier.height(20.dp))
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun GroupPicker(group: String, onGroupChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = "${group}-топ", onValueChange = {}, readOnly = true, label = { Text("Оқу тобы") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
            shape = RoundedCornerShape(16.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Blue, unfocusedBorderColor = LightGray)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            GROUPS.forEach { value -> DropdownMenuItem(text = { Text("${value}-топ") }, onClick = { onGroupChange(value); expanded = false }) }
        }
    }
}

@Composable
private fun HomeScreen(group: String, onGroupChange: (String) -> Unit, onSeeSchedule: () -> Unit) {
    Page {
        Text("StudentSchedule", color = Blue, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(Modifier.height(22.dp))
        Text("Студенттің сабақ кестесі", color = Navy, fontWeight = FontWeight.Bold, fontSize = 30.sp, lineHeight = 36.sp)
        Text("3 курс · II ауысым", color = Muted, fontSize = 16.sp, modifier = Modifier.padding(top = 8.dp, bottom = 28.dp))
        GroupPicker(group, onGroupChange)
        Spacer(Modifier.height(20.dp))
        Button(onClick = onSeeSchedule, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(16.dp)) {
            Text("Сабақ кестесін көру", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(20.dp))
        Surface(color = Color.White, shape = RoundedCornerShape(20.dp), shadowElevation = 1.dp) {
            Column(Modifier.fillMaxWidth().padding(20.dp)) {
                Text("Таңдалған топ", color = Muted, fontSize = 13.sp)
                Text("${group}-топ", color = Navy, fontWeight = FontWeight.Bold, fontSize = 23.sp, modifier = Modifier.padding(top = 5.dp))
                Text("Аптасына 5 оқу күні", color = Muted, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

@Composable
private fun DaySelector(selected: Weekday, onSelected: (Weekday) -> Unit) {
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Weekday.entries.forEach { day ->
            FilterChip(selected = selected == day, onClick = { onSelected(day) }, label = { Text(day.shortTitle) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Navy, selectedLabelColor = Color.White))
        }
    }
}

@Composable
private fun LessonList(group: String, weekday: Weekday, compact: Boolean = false) {
    val lessons = remember(group, weekday) { lessonsFor(group, weekday) }
    if (lessons.isEmpty()) {
        EmptyMessage("Бұл күні сабақ жоқ")
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 12.dp), contentPadding = PaddingValues(bottom = 18.dp)) {
            items(lessons) { lesson -> LessonCard(lesson, compact) }
        }
    }
}

@Composable
private fun ScheduleScreen(group: String) {
    var selected by remember { mutableStateOf(Weekday.MONDAY) }
    Page {
        PageTitle("Сабақ кестесі", "$group-топ · 3 курс")
        DaySelector(selected, onSelected = { selected = it })
        Spacer(Modifier.height(14.dp))
        Text(selected.title, color = Ink, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, modifier = Modifier.padding(bottom = 12.dp))
        LessonList(group, selected)
    }
}

@Composable
private fun WeekScreen(group: String) {
    Page {
        PageTitle("Апталық кесте", "$group-топ · дүйсенбіден жұмаға дейін")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(18.dp), contentPadding = PaddingValues(bottom = 20.dp)) {
            items(Weekday.entries.toList()) { day ->
                Column {
                    Text(day.title, color = Navy, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                    val lessons = remember(group, day) { lessonsFor(group, day) }
                    if (lessons.isEmpty()) Text("Бұл күні сабақ жоқ", color = Muted, modifier = Modifier.padding(8.dp))
                    lessons.forEach { LessonCard(it, compact = true) }
                }
            }
        }
    }
}

@Composable
private fun TodayScreen(group: String) {
    val today = when (LocalDate.now().dayOfWeek) {
        DayOfWeek.MONDAY -> Weekday.MONDAY
        DayOfWeek.TUESDAY -> Weekday.TUESDAY
        DayOfWeek.WEDNESDAY -> Weekday.WEDNESDAY
        DayOfWeek.THURSDAY -> Weekday.THURSDAY
        DayOfWeek.FRIDAY -> Weekday.FRIDAY
        else -> null
    }
    Page {
        PageTitle("Бүгінгі сабақтар", "$group-топ · ${LocalDate.now().dayOfMonth}.${LocalDate.now().monthValue}")
        if (today == null) EmptyMessage("Бүгін сабақ жоқ") else {
            Text(today.title, color = Ink, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, modifier = Modifier.padding(bottom = 12.dp))
            LessonList(group, today)
        }
    }
}

@Composable
private fun SettingsScreen(group: String, onGroupChange: (String) -> Unit, onHome: () -> Unit) {
    Page {
        PageTitle("Баптаулар")
        Text("Оқу тобын өзгерту", fontWeight = FontWeight.SemiBold, color = Ink, modifier = Modifier.padding(bottom = 10.dp))
        GroupPicker(group, onGroupChange)
        Spacer(Modifier.height(28.dp))
        Surface(color = Color.White, shape = RoundedCornerShape(18.dp)) {
            Column(Modifier.fillMaxWidth().padding(18.dp)) {
                Text("Қолданба туралы", color = Navy, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text("StudentSchedule · 1.0", color = Muted, modifier = Modifier.padding(top = 8.dp))
                Text("3 курс, II ауысымға арналған сабақ кестесі", color = Muted, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
            }
        }
        Spacer(Modifier.height(20.dp))
        OutlinedButton(onClick = onHome, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)) {
            Text("Басты бетке қайту")
        }
    }
}

@Composable
private fun LessonCard(lesson: Lesson, compact: Boolean) {
    Surface(color = Color.White, shape = RoundedCornerShape(if (compact) 14.dp else 18.dp), shadowElevation = 1.dp) {
        Row(Modifier.fillMaxWidth().padding(if (compact) 12.dp else 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.width(if (compact) 74.dp else 82.dp)) {
                Text(lesson.start, color = Blue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(lesson.end, color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
            }
            Spacer(Modifier.width(12.dp))
            Box(Modifier.width(3.dp).height(if (compact) 34.dp else 42.dp).background(Blue, RoundedCornerShape(2.dp)))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(lesson.subject, color = Ink, fontWeight = FontWeight.SemiBold, fontSize = if (compact) 13.sp else 15.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                    Text("${lesson.weekday.title} · ${lessonNumber(lesson)}-сабақ", color = Muted, fontSize = 11.sp)
                    lesson.room?.let { Text("$it-кабинет", color = Muted, fontSize = 11.sp) }
                }
                if (lesson.needsClarification) Text("Нақтылау қажет", color = Color(0xFFB45309), fontSize = 11.sp)
            }
        }
    }
}

private fun lessonNumber(lesson: Lesson): Int {
    if (lesson.subject == "Өндірістік оқыту") return 1
    val times = if (lesson.weekday == Weekday.FRIDAY) listOf("11:30", "12:40", "13:50", "15:00", "16:10")
        else listOf("12:40", "13:50", "15:00", "16:10")
    return (times.indexOf(lesson.start) + 1).coerceAtLeast(1)
}

@Composable
private fun EmptyMessage(message: String) {
    Surface(color = Color.White, shape = RoundedCornerShape(18.dp)) {
        Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
            Text(message, color = Muted, fontWeight = FontWeight.Medium)
        }
    }
}
