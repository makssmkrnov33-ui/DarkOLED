data class UserPreferences(
    val interests: List<String>,  // ["tech", "sports", "politics"]
    val readHistory: List<String> // ID прочитанных статей
)

// На бэкенде (Cloud Functions):
// 1. Собираем интересы пользователя
// 2. Считаем cosine similarity между профилем и статьями
// 3. Возвращаем топ-20 рекомендаций