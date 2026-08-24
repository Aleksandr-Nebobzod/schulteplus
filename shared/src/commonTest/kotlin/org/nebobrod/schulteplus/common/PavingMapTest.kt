package org.nebobrod.schulteplus.common

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Инварианты PavingMap (этап 3, ТЗ «Мешанина» п. 8):
 * биекция ячеек ↔ плиток, anchorCell внутри плитки, периметр плиток,
 * согласованность позиций row-major.
 */
class PavingMapTest {

    @Test
    fun cellsOfTileIsBijection() {
        for (seed in 0L..9L) {
            val map = PavingMap(TilePaving(seed).build())
            val seen = BooleanArray(100)
            var total = 0
            for (tile in 1..25) {
                val cells = map.cellsOfTile(tile)
                assertTrue(cells.isNotEmpty(), "seed=$seed: tile $tile has no cells")
                total += cells.size
                for (position in cells) {
                    assertTrue(position in 0..99, "seed=$seed: position $position out of range")
                    assertTrue(!seen[position], "seed=$seed: position $position in multiple tiles")
                    seen[position] = true
                    assertEquals(tile, map.tileAt(position), "seed=$seed: tileAt($position)")
                }
            }
            assertEquals(100, total, "seed=$seed: total cells")
        }
    }

    @Test
    fun anchorCellIsInsideTile() {
        for (seed in 0L..9L) {
            val map = PavingMap(TilePaving(seed).build())
            for (tile in 1..25) {
                val anchor = map.anchorCell(tile)
                assertEquals(tile, map.tileAt(anchor), "seed=$seed: anchor of tile $tile")
                assertTrue(anchor in map.cellsOfTile(tile), "seed=$seed: anchor not in cellsOfTile")
            }
        }
    }

    @Test
    fun outerSideMatchesNeighbourDefinition() {
        for (seed in 0L..9L) {
            val map = PavingMap(TilePaving(seed).build())
            for (row in 0 until 10) for (col in 0 until 10) {
                val tile = map.tileAt(row, col)
                val expected = booleanArrayOf(
                    col + 1 >= 10 || map.tileAt(row, col + 1) != tile, // East
                    row + 1 >= 10 || map.tileAt(row + 1, col) != tile, // South
                    col - 1 < 0 || map.tileAt(row, col - 1) != tile,   // West
                    row - 1 < 0 || map.tileAt(row - 1, col) != tile    // North
                )
                for (side in 0..3) {
                    assertEquals(expected[side], map.isOuterSide(row, col, side),
                        "seed=$seed: ($row,$col) side=$side")
                }
            }
        }
    }

    @Test
    fun tileBoundsMatchCells() {
        for (seed in 0L..9L) {
            val map = PavingMap(TilePaving(seed).build())
            for (tile in 1..25) {
                val b = map.tileBounds(tile)
                val cells = map.cellsOfTile(tile)
                for (position in cells) {
                    val r = position / 10
                    val c = position % 10
                    assertTrue(r in b[0]..b[2] && c in b[1]..b[3],
                        "seed=$seed: cell $position outside bounds of tile $tile")
                }
                // bounding-box заполнен только плиткой (прямоугольность)
                for (r in b[0]..b[2]) for (c in b[1]..b[3]) {
                    assertEquals(tile, map.tileAt(r, c), "seed=$seed: bbox cell ($r,$c) of tile $tile")
                }
            }
        }
    }

    @Test
    fun tileAtPositionMatchesRowMajor() {
        for (seed in 0L..9L) {
            val map = PavingMap(TilePaving(seed).build())
            for (row in 0 until 10) for (col in 0 until 10) {
                assertEquals(map.tileAt(row, col), map.tileAt(row * 10 + col),
                    "seed=$seed: row-major position of ($row,$col)")
            }
        }
    }
}
