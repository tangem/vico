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

package com.patrykandpatrick.vico.core.cartesian.layer.spec

import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import androidx.annotation.FloatRange
import com.patrykandpatrick.vico.core.cartesian.DefaultPointConnector
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.Defaults
import com.patrykandpatrick.vico.core.common.DrawContext
import com.patrykandpatrick.vico.core.common.VerticalPosition
import com.patrykandpatrick.vico.core.common.component.Component
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.copy
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.patrykandpatrick.vico.core.common.updateBounds

/**
 * TODO
 */
public class SplitLineSpec(
  shader: DynamicShader,
  thicknessDp: Float = Defaults.LINE_SPEC_THICKNESS_DP,
  public val backgroundShaderFirst: DynamicShader? = null,
  public val backgroundShaderSecond: DynamicShader? = null,
  cap: Paint.Cap = Paint.Cap.ROUND,
  point: Component? = null,
  pointSizeDp: Float = Defaults.POINT_SIZE,
  dataLabel: TextComponent? = null,
  dataLabelVerticalPosition: VerticalPosition = VerticalPosition.Top,
  dataLabelValueFormatter: CartesianValueFormatter = CartesianValueFormatter.decimal(),
  dataLabelRotationDegrees: Float = 0f,
  pointConnector: PointConnector = DefaultPointConnector(),
  @FloatRange(from = 0.0, to = 1.0) public val xSplitFraction: Float = 1f,
) : LineCartesianLayer.LineSpec(
  shader = shader,
  thicknessDp = thicknessDp,
  backgroundShader = backgroundShaderFirst,
  cap = cap,
  point = point,
  pointSizeDp = pointSizeDp,
  dataLabel = dataLabel,
  dataLabelVerticalPosition = dataLabelVerticalPosition,
  dataLabelValueFormatter = dataLabelValueFormatter,
  dataLabelRotationDegrees = dataLabelRotationDegrees,
  pointConnector = pointConnector,
) {

  override fun drawBackgroundLine(
    context: DrawContext,
    bounds: RectF,
    zeroLineYFraction: Float,
    path: Path,
    opacity: Float,
  ) {
    backgroundShader = backgroundShaderFirst
    when {
        xSplitFraction <= 0f -> {
          backgroundShader = backgroundShaderSecond
          super.drawBackgroundLine(context, bounds, zeroLineYFraction, path, opacity)
          backgroundShader = backgroundShaderFirst
          return
        }
        xSplitFraction >= 1 -> {
          super.drawBackgroundLine(context, bounds, zeroLineYFraction, path, opacity)
          return
        }
    }

    val firstBounds = RectF(
      /* left = */ bounds.left,
      /* top = */ bounds.top,
      /* right = */  bounds.right * xSplitFraction,
      /* bottom = */ bounds.bottom,
    )
    val secondBounds = RectF(
      /* left = */ bounds.right * xSplitFraction,
      /* top = */ bounds.top,
      /* right = */ bounds.right,
      /* bottom = */ bounds.bottom,
    )
    super.drawBackgroundLine(context, firstBounds, zeroLineYFraction, path, opacity)
    backgroundShader = backgroundShaderSecond
    super.drawBackgroundLine(context, secondBounds, zeroLineYFraction, path, opacity)
    backgroundShader = backgroundShaderFirst
  }
}
