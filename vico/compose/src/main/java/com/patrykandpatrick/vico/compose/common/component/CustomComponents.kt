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

package com.patrykandpatrick.vico.compose.common.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.core.common.Defaults
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.component.UnboundedLineComponent
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.patrykandpatrick.vico.core.common.shape.Shape

/** Creates and remembers a [UnboundedLineComponent] with the specified properties. */
@Composable
public fun rememberUnboundedLineComponent(
  color: Color = Color.Black,
  thickness: Dp = Defaults.LINE_COMPONENT_THICKNESS_DP.dp,
  shape: Shape = Shape.Rectangle,
  dynamicShader: DynamicShader? = null,
  margins: Dimensions = Dimensions.Empty,
  strokeWidth: Dp = 0.dp,
  strokeColor: Color = Color.Transparent,
  verticalAddDrawSpace: Dp = 0.dp,
  horizontalAddDrawSpace: Dp = 0.dp,
): LineComponent =
  remember(
      color,
      thickness,
      shape,
      dynamicShader,
      margins,
      strokeWidth,
      strokeColor,
      verticalAddDrawSpace,
      horizontalAddDrawSpace,
  ) {
    UnboundedLineComponent(
      color = color.toArgb(),
      thicknessDp = thickness.value,
      shape = shape,
      shader = dynamicShader,
      margins = margins,
      strokeThicknessDp = strokeWidth.value,
      strokeColor = strokeColor.toArgb(),
      verticalAddDrawSpaceDp = verticalAddDrawSpace.value,
      horizontalAddDrawSpaceDp = horizontalAddDrawSpace.value,
    )
  }
