package org.nebobrod.schulteplus.common

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Этап 2, ТЗ «Мешанина» (docs/TZ_Mishmash.md, п. 8):
 * полнота замощения, размеры плиток ⊆ TILE_SIZES, детерминизм.
 * Тесты инвариантов PavingMap — этап 3 (вместе с классом PavingMap).
 */
class TilePavingTest {

    private fun build(seed: Long): Array<IntArray> = TilePaving(seed).build()

    @Test
    fun fieldIs10x10() {
        for (seed in 0L..9L) {
            val field = build(seed)
            assertEquals(10, field.size, "seed=$seed: rows")
            for (row in field) assertEquals(10, row.size, "seed=$seed: cols")
        }
    }

    @Test
    fun pavingIsComplete() {
        for (seed in 0L..9L) {
            val field = build(seed)
            val cellsByTile = IntArray(26)
            for (row in field) for (value in row) {
                assertTrue(value in 1..25, "seed=$seed: value $value out of 1..25")
                cellsByTile[value]++
            }
            assertEquals(100, cellsByTile.sum(), "seed=$seed: total covered cells")
            assertEquals(25, cellsByTile.count { it > 0 }, "seed=$seed: tiles used")
        }
    }

    @Test
    fun tileSizesMatchAllowedList() {
        for (seed in 0L..9L) {
            val field = build(seed)
            for (tile in 1..25) {
                var minRow = Int.MAX_VALUE
                var maxRow = Int.MIN_VALUE
                var minCol = Int.MAX_VALUE
                var maxCol = Int.MIN_VALUE
                var count = 0
                for (r in field.indices) for (c in field[r].indices) {
                    if (field[r][c] == tile) {
                        minRow = minOf(minRow, r)
                        maxRow = maxOf(maxRow, r)
                        minCol = minOf(minCol, c)
                        maxCol = maxOf(maxCol, c)
                        count++
                    }
                }
                assertTrue(count > 0, "seed=$seed: tile $tile not found")
                val h = maxRow - minRow + 1
                val w = maxCol - minCol + 1
                val allowed = TilePaving.TILE_SIZES.any { it[0] == h && it[1] == w }
                assertTrue(allowed, "seed=$seed: tile $tile size ${h}x$w not in TILE_SIZES")
                assertEquals(count, h * w, "seed=$seed: tile $tile not rectangular")
            }
        }
    }

    @Test
    fun deterministicForSameSeed() {
        for (seed in longArrayOf(0, 1, 42, 2026)) {
            val first = build(seed)
            val second = build(seed)
            for (r in first.indices) {
                assertContentEquals(second[r], first[r], "seed=$seed: row $r differs")
            }
        }
    }
}
