# StudentSchedule

Offline native Android app for the third-year, second-shift class schedule. Built with Kotlin, Jetpack Compose, Material 3, Navigation Compose and Preferences DataStore.

## Screens

- Басты бет — choose the group (default: 310) and open its schedule.
- Сабақ кестесі — browse the selected group's lessons by weekday.
- Апталық кесте — view Monday through Friday in separate compact sections.
- Бүгінгі сабақтар — show lessons for the device's current weekday.
- Баптаулар — change the saved group and view app information.

The schedule is bundled locally in `app/src/main/java/kz/studentschedule/data/ScheduleData.kt`. Group 303–305 use the merged daily production-training cells as shown in the source image. The photo itself is not displayed in the app.

## Build

Open this folder in Android Studio or run:

```sh
./gradlew :app:assembleDebug
```
