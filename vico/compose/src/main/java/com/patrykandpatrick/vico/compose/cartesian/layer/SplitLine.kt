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

package com.patrykandpatrick.vico.compose.cartesian.layer

import androidx.annotation.FloatRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.common.shader.color
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.spec.SplitLine
import com.patrykandpatrick.vico.core.common.Defaults
import com.patrykandpatrick.vico.core.common.VerticalPosition
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.shader.DynamicShader

@Composable
public fun rememberSplitLine(
  shader: DynamicShader = DynamicShader.color(Color.Black),
  thickness: Dp = Defaults.LINE_SPEC_THICKNESS_DP.dp,
  backgroundShaderFirst: DynamicShader? = shader.getDefaultBackgroundShader(),
  backgroundShaderSecond: DynamicShader? = shader.getDefaultBackgroundShader(),
  cap: StrokeCap = StrokeCap.Round,
  pointProvider: LineCartesianLayer.PointProvider? = null,
  pointConnector: LineCartesianLayer.PointConnector = remember {
    LineCartesianLayer.PointConnector.cubic()
  },
  dataLabel: TextComponent? = null,
  dataLabelVerticalPosition: VerticalPosition = VerticalPosition.Top,
  dataLabelValueFormatter: CartesianValueFormatter = remember { CartesianValueFormatter.decimal() },
  dataLabelRotationDegrees: Float = 0f,
  @FloatRange(from = 0.0, to = 1.0) xSplitFraction: Float = 0f,
): LineCartesianLayer.Line =
  remember(
    shader,
    thickness,
    backgroundShaderFirst,
    backgroundShaderSecond,
    cap,
    dataLabel,
    dataLabelVerticalPosition,
    dataLabelRotationDegrees,
    dataLabelRotationDegrees,
    pointConnector,
    xSplitFraction
  ) {
    SplitLine(
      shader = shader,
      thicknessDp = thickness.value,
      backgroundShaderFirst = backgroundShaderFirst,
      backgroundShaderSecond = backgroundShaderSecond,
      cap = cap.paintCap,
      pointProvider = pointProvider,
      dataLabel = dataLabel,
      dataLabelVerticalPosition = dataLabelVerticalPosition,
      dataLabelValueFormatter = dataLabelValueFormatter,
      dataLabelRotationDegrees = dataLabelRotationDegrees,
      pointConnector = pointConnector,
      xSplitFraction = xSplitFraction
    )
  }
