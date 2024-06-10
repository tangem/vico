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

package com.patrykandpatrick.vico.core.cartesian.axis

import android.graphics.Color
import com.patrykandpatrick.vico.core.cartesian.CartesianDrawContext
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent


/**
 * Custom implementation of vertical axis. It extends the VerticalAxis class and
 * provides additional functionality for handling vertical axis labels and guidelines.
 *
 * @param position The position of the axis
 *
 * @see VerticalAxis
 * @see AxisPosition.Vertical
 */
public class CustomVerticalAxis<Position : AxisPosition.Vertical>(position: Position) :
  VerticalAxis<Position>(position = position) {

  /** The [LineComponent] to use for guidelines that aware of label width. */
  public var labelGuideline: LineComponent? = null

  /**
   * This function checks if the axis is not in restricted bounds.
   * As this is an unrestricted vertical axis, it always returns true.
   *
   * @param left The left bound.
   * @param top The top bound.
   * @param right The right bound.
   * @param bottom The bottom bound.
   * @return true as this is an unrestricted vertical axis.
   */
  override fun isNotInRestrictedBounds(
    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
  ): Boolean = true

  override fun drawLabel(
    context: CartesianDrawContext,
    label: TextComponent,
    labelText: CharSequence,
    labelX: Float,
    tickCenterY: Float,
  ) {
    with(context) {
      val textBounds =
        label.getTextBounds(this, labelText, rotationDegrees = labelRotationDegrees)

      if (position.isStart) {
        labelGuideline?.drawHorizontal(
          context = context,
          left = chartBounds.left + textBounds.width(),
          right = chartBounds.right,
          centerY = tickCenterY,
        )
      } else {
        labelGuideline?.drawHorizontal(
          context = context,
          left = chartBounds.left,
          right = chartBounds.right - textBounds.width(),
          centerY = tickCenterY,
        )
      }
    }

    super.drawLabel(context, label, labelText, labelX, tickCenterY)
  }

  public class Builder<Position : AxisPosition.Vertical>(
    builder: BaseAxis.Builder<Position>? = null,
  ) : BaseAxis.Builder<Position>(builder) {
    /**
     * Determines for what _y_ values this [VerticalAxis] is to display labels, ticks, and
     * guidelines.
     */
    public var itemPlacer: AxisItemPlacer.Vertical = AxisItemPlacer.Vertical.step()

    /** Defines the horizontal position of each axis label relative to the axis line. */
    public var horizontalLabelPosition: HorizontalLabelPosition = HorizontalLabelPosition.Outside

    /** Defines the vertical position of each axis label relative to its corresponding tick. */
    public var verticalLabelPosition: VerticalLabelPosition = VerticalLabelPosition.Center

    /** Creates a [VerticalAxis] instance with the properties from this [Builder]. */
    @Suppress("UNCHECKED_CAST")
    public inline fun <reified T : Position> build(): CustomVerticalAxis<T> {
      val position =
        when (T::class.java) {
          AxisPosition.Vertical.Start::class.java -> AxisPosition.Vertical.Start
          AxisPosition.Vertical.End::class.java -> AxisPosition.Vertical.End
          else ->
            throw IllegalStateException("Got unknown AxisPosition class ${T::class.java.name}")
        }
          as Position
      return setTo(CustomVerticalAxis(position)).also { axis ->
        axis.itemPlacer = itemPlacer
        axis.horizontalLabelPosition = horizontalLabelPosition
        axis.verticalLabelPosition = verticalLabelPosition
      } as CustomVerticalAxis<T>
    }
  }

  /** Houses a [VerticalAxis] factory function. */
  public companion object {
    /** Creates a [VerticalAxis] via [Builder]. */
    public inline fun <reified P : AxisPosition.Vertical> build(
      block: Builder<P>.() -> Unit = {},
    ): CustomVerticalAxis<P> = Builder<P>().apply(block).build()
  }
}
