# 0.8.5 (build 15)

## [Changelog]

### 🔧 Рефакторинг: Улучшение расписания и уроков
- Улучшены отступы и макеты в `fragment_schedule.xml` и `item_lesson.xml`.
- Добавлено сообщение "Нет уроков", скрывающееся при наличии занятий.

### 🔧 Рефакторинг: Обновление работы с семестрами
- Добавлено сообщение о необходимости добавления семестра.
- Улучшено управление списком семестров, добавлена радиокнопка для выбора.

### 🔧 Рефакторинг: Пустые состояния и видимость
- Добавлены сообщения о пустых списках (семестры, уроки, студенты и др.).
- Реализована динамическая видимость сообщений при изменении данных.

### 🔧 Рефакторинг: Улучшение редактирования уроков
- Улучшена загрузка кабинетов, добавлена фильтрация по кампусу.
- Обновлён UI: перераспределены поля, улучшена логика отображения.

---

## [Список изменений]

### 🔧 Refactor: Schedule and Lessons Improvements
- Adjusted spacing and layouts in `fragment_schedule.xml` and `item_lesson.xml`.
- Added "No lessons" message that toggles based on lesson availability.

### 🔧 Refactor: Semester Management Updates
- Added a message prompting semester creation.
- Improved semester list handling, added a selection radio button.

### 🔧 Refactor: Empty States and Visibility
- Implemented empty state messages across multiple activities.
- Visibility dynamically updates based on data changes.

### 🔧 Refactor: Lesson Editing Enhancements
- Improved cabinet loading with campus-based filtering.
- Updated UI: reorganized fields and enhanced display logic.  
