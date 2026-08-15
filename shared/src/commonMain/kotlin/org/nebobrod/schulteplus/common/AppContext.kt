package org.nebobrod.schulteplus.common

/**
 * Явный контекст выполнения упражнения (этап 1 развязки ядра; порт в shared — этап 2).
 * Создаётся на границе приложения и передаётся домену вместо статических чтений.
 */
class AppContext(
    val exTypeId: String,
    val symbolType: String,
    val isRatings: Boolean,
    val uak: String,
    val uid: String,
    val name: String
)
