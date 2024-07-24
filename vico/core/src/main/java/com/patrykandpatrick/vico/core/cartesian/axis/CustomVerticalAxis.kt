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

import com.patrykandpatrick.vico.core.cartesian.CartesianDrawContext
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis.HorizontalLabelPosition.Outside
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis.VerticalLabelPosition.Center
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.translate


/**
 * Custom implementation of vertical axis. It extends the VerticalAxis class and
 * provides additional functionality for handling vertical axis labels and guidelines.
 *
 * @see VerticalAxis
 * @see AxisPosition.Vertical
 */
public class CustomVerticalAxis<Position : AxisPosition.Vertical>(
  override val position: Position,
  line: LineComponent?,
  label: TextComponent?,
  labelRotationDegrees: Float,
  _horizontalLabelPosition: HorizontalLabelPosition = Outside,
  _verticalLabelPosition: VerticalLabelPosition = Center,
  valueFormatter: CartesianValueFormatter,
  tick: LineComponent?,
  tickLengthDp: Float,
  guideline: LineComponent?,
  _itemPlacer: ItemPlacer = ItemPlacer.step(),
  sizeConstraint: SizeConstraint,
  titleComponent: TextComponent?,
  title: CharSequence?,
  /** The [LineComponent] to use for guidelines that aware of label width. */
  public var labelGuideline: LineComponent? = null,
) :
  VerticalAxis<Position>(
    position = position,
    line = line,
    label = label,
    labelRotationDegrees = labelRotationDegrees,
    horizontalLabelPosition = _horizontalLabelPosition,
    verticalLabelPosition = _verticalLabelPosition,
    valueFormatter = valueFormatter,
    tick = tick,
    tickLengthDp = tickLengthDp,
    guideline = guideline,
    itemPlacer = _itemPlacer,
    sizeConstraint = sizeConstraint,
    titleComponent = titleComponent,
    title = title,
  ) {

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
    labelComponent: TextComponent,
    label: CharSequence,
    labelX: Float,
    tickCenterY: Float,
  ) {
    with(context) {
        val textBounds =
          labelComponent
            .getBounds(context = this, text = label, rotationDegrees = labelRotationDegrees)
            .apply { translate(labelX, tickCenterY - centerY()) }

      if (position.isStart) {
        labelGuideline?.drawHorizontal(
          context = context,
          left = layerBounds.left + textBounds.width(),
          right = layerBounds.right,
          centerY = tickCenterY,
        )
      } else {
        labelGuideline?.drawHorizontal(
          context = context,
          left = layerBounds.left,
          right = layerBounds.right - textBounds.width(),
          centerY = tickCenterY,
        )
      }
    }

    super.drawLabel(context, labelComponent, label, labelX, tickCenterY)
  }

  /** Houses [VerticalAxis] factory functions. */
  public companion object {
    /** Creates a start [VerticalAxis]. */
    public fun start(
      line: LineComponent? = null,
      label: TextComponent? = null,
      labelRotationDegrees: Float = 0f,
      horizontalLabelPosition: HorizontalLabelPosition = Outside,
      verticalLabelPosition: VerticalLabelPosition = Center,
      valueFormatter: CartesianValueFormatter = CartesianValueFormatter.decimal(),
      tick: LineComponent? = null,
      tickLengthDp: Float = 0f,
      guideline: LineComponent? = null,
      itemPlacer: ItemPlacer = ItemPlacer.step(),
      sizeConstraint: SizeConstraint = SizeConstraint.Auto(),
      titleComponent: TextComponent? = null,
      title: CharSequence? = null,
      labelGuideline: LineComponent? = null,
    ): CustomVerticalAxis<AxisPosition.Vertical.Start> =
      CustomVerticalAxis(
        AxisPosition.Vertical.Start,
        line,
        label,
        labelRotationDegrees,
        horizontalLabelPosition,
        verticalLabelPosition,
        valueFormatter,
        tick,
        tickLengthDp,
        guideline,
        itemPlacer,
        sizeConstraint,
        titleComponent,
        title,
        labelGuideline,
      )

    /** Creates an end [VerticalAxis]. */
    public fun end(
      line: LineComponent? = null,
      label: TextComponent? = null,
      labelRotationDegrees: Float = 0f,
      horizontalLabelPosition: HorizontalLabelPosition = Outside,
      verticalLabelPosition: VerticalLabelPosition = Center,
      valueFormatter: CartesianValueFormatter = CartesianValueFormatter.decimal(),
      tick: LineComponent? = null,
      tickLengthDp: Float = 0f,
      guideline: LineComponent? = null,
      itemPlacer: ItemPlacer = ItemPlacer.step(),
      sizeConstraint: SizeConstraint = SizeConstraint.Auto(),
      titleComponent: TextComponent? = null,
      title: CharSequence? = null,
      labelGuideline: LineComponent? = null,
    ): CustomVerticalAxis<AxisPosition.Vertical.End> =
      CustomVerticalAxis(
        AxisPosition.Vertical.End,
        line,
        label,
        labelRotationDegrees,
        horizontalLabelPosition,
        verticalLabelPosition,
        valueFormatter,
        tick,
        tickLengthDp,
        guideline,
        itemPlacer,
        sizeConstraint,
        titleComponent,
        title,
        labelGuideline,
      )
  }
}
