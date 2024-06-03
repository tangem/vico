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

package com.patrykandpatrick.vico.core.common.component

import android.graphics.Color
import com.patrykandpatrick.vico.core.common.Defaults
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.DrawContext
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.patrykandpatrick.vico.core.common.shape.Shape

/**
 * This class represents an unbounded line component. It extends the LineComponent class and
 * provides additional functionality for drawing vertical and horizontal lines with additional
 * drawing space.
 *
 * @property color the background color.
 * @property thicknessDp the thickness of the line.
 * @property shape the [Shape] to use for the line.
 * @property dynamicShader an optional [DynamicShader] to apply to the line.
 * @property margins the margins of the line.
 * @property strokeWidthDp the stroke width.
 * @property strokeColor the stroke color.
 * @param verticalAddDrawSpaceDp Additional vertical drawing space for the line component in density-independent pixels (dp).
 * @param horizontalAddDrawSpaceDp Additional horizontal drawing space for the line component in density-independent pixels (dp).
 *
 * @see LineComponent
 * @see Shape
 * @see DynamicShader
 * @see Dimensions
 */
public class UnboundedLineComponent(
  color: Int,
  thicknessDp: Float = Defaults.LINE_COMPONENT_THICKNESS_DP,
  shape: Shape = Shape.Rectangle,
  dynamicShader: DynamicShader? = null,
  margins: Dimensions = Dimensions.Empty,
  strokeWidthDp: Float = 0f,
  strokeColor: Int = Color.TRANSPARENT,
  protected val verticalAddDrawSpaceDp: Float = 0f,
  protected val horizontalAddDrawSpaceDp: Float = 0f,
) : LineComponent(
  color = color,
  thicknessDp = thicknessDp,
  shape = shape,
  dynamicShader = dynamicShader,
  margins = margins,
  strokeWidthDp = strokeWidthDp,
  strokeColor = strokeColor,
) {

  override fun drawVertical(
    context: DrawContext,
    top: Float,
    bottom: Float,
    centerX: Float,
    thicknessScale: Float,
    opacity: Float,
  ) {
    with(context) {
      draw(
        context,
        left = centerX - thicknessDp.pixels * thicknessScale / 2,
        top = top - verticalAddDrawSpaceDp,
        right = centerX + thicknessDp.pixels * thicknessScale / 2,
        bottom = bottom + verticalAddDrawSpaceDp,
        opacity = opacity,
      )
    }
  }

  override fun drawHorizontal(
    context: DrawContext,
    left: Float,
    right: Float,
    centerY: Float,
    thicknessScale: Float,
    opacity: Float,
  ) {
    with(context) {
      draw(
        context,
        left = left - horizontalAddDrawSpaceDp,
        top = centerY - thicknessDp.pixels * thicknessScale / 2,
        right = right + horizontalAddDrawSpaceDp,
        bottom = centerY + thicknessDp.pixels * thicknessScale / 2,
        opacity = opacity,
      )
    }
  }

}
