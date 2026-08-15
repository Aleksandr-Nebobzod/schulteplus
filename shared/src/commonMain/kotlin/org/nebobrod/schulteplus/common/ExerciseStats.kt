package org.nebobrod.schulteplus.common

/**
 * Статистика упражнения, вычисляемая движком (STable) и передаваемая в Result
 * полиморфно (этап 1.3; порт в shared — этап 2) — вместо кастов к подклассам.
 */
class ExerciseStats(
    val timeStamp: Long,
    val numValue: Long, // общее время упражнения, ms
    val turns: Int,
    val turnsMissed: Int,
    val average: Float,
    val rmsd: Float,
    val levelOfEmotion: Int,
    val levelOfEnergy: Int,
    val note: String
)
