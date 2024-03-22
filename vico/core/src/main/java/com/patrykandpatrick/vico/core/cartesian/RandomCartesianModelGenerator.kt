/*
 * Copyright 2024 by Patryk Goworowski and Patrick Michalik.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.patrykandpatrick.vico.core.cartesian

import androidx.annotation.RestrictTo
import com.patrykandpatrick.vico.core.cartesian.model.CandlestickCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.model.CartesianChartModel
import com.patrykandpatrick.vico.core.cartesian.model.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.model.LineCartesianLayerModel
import com.patrykandpatrick.vico.core.common.extension.random
import kotlin.random.Random

/** @suppress */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public object RandomCartesianModelGenerator {
    public val defaultX: IntProgression = 0..96
    public val defaultY: ClosedFloatingPointRange<Float> = 2f..20f
    public val defaultOpenCloseRange: ClosedFloatingPointRange<Float> = 5f..15f
    public val defaultLowHighRange: ClosedFloatingPointRange<Float> = .5f..5f

    public fun getRandomColumnLayerModelPartial(
        seriesCount: Int = 1,
        x: IntProgression = defaultX,
        y: ClosedFloatingPointRange<Float> = defaultY,
    ): ColumnCartesianLayerModel.Partial =
        ColumnCartesianLayerModel.partial { repeat(seriesCount) { series(x.toList(), x.map { y.random() }) } }

    public fun getRandomLineLayerModelPartial(
        seriesCount: Int = 1,
        x: IntProgression = defaultX,
        y: ClosedFloatingPointRange<Float> = defaultY,
    ): LineCartesianLayerModel.Partial =
        LineCartesianLayerModel.partial { repeat(seriesCount) { series(x.toList(), x.map { y.random() }) } }

    public fun getRandomCandlestickLayerModelPartial(
        x: IntProgression = defaultX,
        y: ClosedFloatingPointRange<Float> = defaultY,
        openCloseRange: ClosedFloatingPointRange<Float> = defaultOpenCloseRange,
        lowHighRange: ClosedFloatingPointRange<Float> = defaultLowHighRange,
    ): CandlestickCartesianLayerModel.Partial {
        var previousClose: Float? = null
        var previousOpen: Float? = null
        val entries = mutableListOf<CandlestickCartesianLayerModel.Entry>()
        for (i in x) {
            val isIncreasing = Random.nextBoolean()
            val open: Float
            val close: Float
            if (isIncreasing) {
                open =
                    if (previousOpen != null && previousClose != null) {
                        floatArrayOf(previousOpen, previousClose).random().coerceIn(openCloseRange)
                    } else {
                        y.random().coerceIn(openCloseRange)
                    }
                close = (open + lowHighRange.random())
            } else {
                close =
                    if (previousOpen != null && previousClose != null) {
                        floatArrayOf(previousOpen, previousClose).random().coerceIn(openCloseRange)
                    } else {
                        y.random().coerceIn(openCloseRange)
                    }
                open = (close + lowHighRange.random())
            }
            val low = minOf(open, close) - lowHighRange.random()
            val high = maxOf(open, close) + lowHighRange.random()

            entries.add(CandlestickCartesianLayerModel.Entry(i.toFloat(), low, high, open, close))

            previousOpen = open
            previousClose = close
        }
        return CandlestickCartesianLayerModel.partial(entries)
    }

    public fun getRandomModel(
        columnSeriesCount: Int = 1,
        lineSeriesCount: Int = 1,
        x: IntProgression = defaultX,
        y: ClosedFloatingPointRange<Float> = defaultY,
    ): CartesianChartModel =
        CartesianChartModel(
            buildList {
                add(getRandomColumnLayerModelPartial(columnSeriesCount, x, y).complete())
                add(getRandomLineLayerModelPartial(lineSeriesCount, x, y).complete())
            },
        )
}