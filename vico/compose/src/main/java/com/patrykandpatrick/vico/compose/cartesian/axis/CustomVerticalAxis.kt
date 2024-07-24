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

package com.patrykandpatrick.vico.compose.cartesian.axis

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.core.cartesian.axis.AxisPosition
import com.patrykandpatrick.vico.core.cartesian.axis.BaseAxis
import com.patrykandpatrick.vico.core.cartesian.axis.CustomVerticalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.common.Defaults
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent

/** Creates and remembers a start [CustomVerticalAxis]. */
@Composable
public fun rememberCustomStartAxis(
  line: LineComponent? = rememberAxisLineComponent(),
  label: TextComponent? = rememberAxisLabelComponent(),
  labelRotationDegrees: Float = Defaults.AXIS_LABEL_ROTATION_DEGREES,
  horizontalLabelPosition: VerticalAxis.HorizontalLabelPosition =
    VerticalAxis.HorizontalLabelPosition.Outside,
  verticalLabelPosition: VerticalAxis.VerticalLabelPosition =
    VerticalAxis.VerticalLabelPosition.Center,
  valueFormatter: CartesianValueFormatter = remember { CartesianValueFormatter.decimal() },
  tick: LineComponent? = rememberAxisTickComponent(),
  tickLength: Dp = Defaults.AXIS_TICK_LENGTH.dp,
  guideline: LineComponent? = rememberAxisGuidelineComponent(),
  itemPlacer: VerticalAxis.ItemPlacer = remember { VerticalAxis.ItemPlacer.step() },
  sizeConstraint: BaseAxis.SizeConstraint = remember { BaseAxis.SizeConstraint.Auto() },
  titleComponent: TextComponent? = null,
  title: CharSequence? = null,
  labelGuideline: LineComponent? = null,
): CustomVerticalAxis<AxisPosition.Vertical.Start> =
  remember { CustomVerticalAxis.start() }
    .apply {
      this.line = line
      this.label = label
      this.labelRotationDegrees = labelRotationDegrees
      this.horizontalLabelPosition = horizontalLabelPosition
      this.verticalLabelPosition = verticalLabelPosition
      this.valueFormatter = valueFormatter
      this.tick = tick
      tickLengthDp = tickLength.value
      this.guideline = guideline
      this.itemPlacer = itemPlacer
      this.sizeConstraint = sizeConstraint
      this.titleComponent = titleComponent
      this.title = title
      this.labelGuideline = labelGuideline
    }

/**
 * Creates and remembers an end axis (i.e., a [VerticalAxis] with [AxisPosition.Vertical.End]).
 *
 * @param label the [TextComponent] to use for the labels.
 * @param line the [LineComponent] to use for the axis line.
 * @param tick the [LineComponent] to use for the ticks.
 * @param tickLength the length of the ticks.
 * @param guideline the [LineComponent] to use for the guidelines.
 * @param labelGuideline the [LineComponent] to use for the guidelines which starts from label text.
 * @param valueFormatter formats the labels.
 * @param sizeConstraint defines how the [VerticalAxis] is to size itself.
 * @param horizontalLabelPosition the horizontal position of the labels.
 * @param verticalLabelPosition the vertical position of the labels.
 * @param itemPlacer determines for what _y_ values the [VerticalAxis] is to display labels, ticks,
 *   and guidelines.
 * @param labelRotationDegrees the rotation of the axis labels (in degrees).
 * @param titleComponent an optional [TextComponent] to use as the axis title.
 * @param title the axis title.
 */
@Composable
public fun rememberCustomEndAxis(
  line: LineComponent? = rememberAxisLineComponent(),
  label: TextComponent? = rememberAxisLabelComponent(),
  labelRotationDegrees: Float = Defaults.AXIS_LABEL_ROTATION_DEGREES,
  horizontalLabelPosition: VerticalAxis.HorizontalLabelPosition =
    VerticalAxis.HorizontalLabelPosition.Outside,
  verticalLabelPosition: VerticalAxis.VerticalLabelPosition =
    VerticalAxis.VerticalLabelPosition.Center,
  valueFormatter: CartesianValueFormatter = remember { CartesianValueFormatter.decimal() },
  tick: LineComponent? = rememberAxisTickComponent(),
  tickLength: Dp = Defaults.AXIS_TICK_LENGTH.dp,
  guideline: LineComponent? = rememberAxisGuidelineComponent(),
  itemPlacer: VerticalAxis.ItemPlacer = remember { VerticalAxis.ItemPlacer.step() },
  sizeConstraint: BaseAxis.SizeConstraint = remember { BaseAxis.SizeConstraint.Auto() },
  titleComponent: TextComponent? = null,
  title: CharSequence? = null,
  labelGuideline: LineComponent? = null,
): CustomVerticalAxis<AxisPosition.Vertical.End> =
  remember { CustomVerticalAxis.end() }
    .apply {
      this.line = line
      this.label = label
      this.labelRotationDegrees = labelRotationDegrees
      this.horizontalLabelPosition = horizontalLabelPosition
      this.verticalLabelPosition = verticalLabelPosition
      this.valueFormatter = valueFormatter
      this.tick = tick
      tickLengthDp = tickLength.value
      this.guideline = guideline
      this.itemPlacer = itemPlacer
      this.sizeConstraint = sizeConstraint
      this.titleComponent = titleComponent
      this.title = title
      this.labelGuideline = labelGuideline
    }
