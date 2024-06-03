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

package com.tangem.vico.demo.utils

import com.patrykandpatrick.vico.core.cartesian.CartesianDrawContext
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasureContext
import com.patrykandpatrick.vico.core.cartesian.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.cartesian.axis.AxisPosition
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis

object MyVerticalAxisItemPlacer : AxisItemPlacer.Vertical {

  private val default = AxisItemPlacer.Vertical.count({ 3 }, false)

  private fun List<Float>.trans(): List<Float> {
    return mapIndexed { index, fl ->
      fl
//      when (index) {
//        0 -> {
//          fl + 4
//        }
//
//        1 -> {
//          fl + 2
//        }
//
//        else -> fl
//      }
    }
  }

  override fun getLabelValues(
    context: CartesianDrawContext,
    axisHeight: Float,
    maxLabelHeight: Float,
    position: AxisPosition.Vertical,
  ): List<Float> {
    return default.getLabelValues(context, axisHeight, maxLabelHeight, position)
      .trans()
  }

  override fun getWidthMeasurementLabelValues(
    context: CartesianMeasureContext,
    axisHeight: Float,
    maxLabelHeight: Float,
    position: AxisPosition.Vertical,
  ): List<Float> {
    return default.getWidthMeasurementLabelValues(context, axisHeight, maxLabelHeight, position)
      .trans()
  }

  override fun getHeightMeasurementLabelValues(
    context: CartesianMeasureContext,
    position: AxisPosition.Vertical,
  ): List<Float> {
    return default.getHeightMeasurementLabelValues(context, position).trans()
  }

  override fun getTopVerticalAxisInset(
    context: CartesianMeasureContext,
    verticalLabelPosition: VerticalAxis.VerticalLabelPosition,
    maxLabelHeight: Float,
    maxLineThickness: Float,
  ): Float {
    return default.getTopVerticalAxisInset(
      context,
      verticalLabelPosition,
      maxLabelHeight,
      maxLineThickness,
    )
  }

  override fun getBottomVerticalAxisInset(
    context: CartesianMeasureContext,
    verticalLabelPosition: VerticalAxis.VerticalLabelPosition,
    maxLabelHeight: Float,
    maxLineThickness: Float,
  ): Float {
    return default.getBottomVerticalAxisInset(
      context,
      verticalLabelPosition,
      maxLabelHeight,
      maxLineThickness,
    )
  }
}
