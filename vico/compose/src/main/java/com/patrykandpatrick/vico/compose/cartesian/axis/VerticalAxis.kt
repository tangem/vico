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
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.common.Defaults
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent

/** Creates and remembers a start [VerticalAxis]. */
@Composable
public fun rememberStartAxis(
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
): VerticalAxis<AxisPosition.Vertical.Start> =
  remember { VerticalAxis.start() }
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
    }

/** Creates and remembers an end [VerticalAxis]. */
@Composable
public fun rememberEndAxis(
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
): VerticalAxis<AxisPosition.Vertical.End> =
  remember { VerticalAxis.end() }
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
    }
