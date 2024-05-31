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

package com.tangem.vico.demo.chart

import android.text.TextUtils
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.fullWidth
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberSplitLineSpec
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.shader.BrushShader
import com.patrykandpatrick.vico.core.cartesian.HorizontalLayout
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.cartesian.axis.AxisPosition
import com.patrykandpatrick.vico.core.cartesian.axis.BaseAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.DefaultAlpha
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.shader.LinearGradientShader
import com.patrykandpatrick.vico.core.common.shape.Shape
import com.tangem.vico.demo.utils.MyVerticalAxisItemPlacer
import java.text.DecimalFormat
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


private val x = (1..50).toList()
private val y = x.map { Random.nextFloat() * 15 + 150 }

@Composable
fun MarketDetailsChart(modifier: Modifier = Modifier) {
  val layer = rememberLayer()

  val chart = rememberCartesianChart(
    layer,
    startAxis = rememberStartAxis(
      axis = null,
      tick = null,
      guideline = rememberAxisGuidelineComponent(
        shape = Shape.Rectangle,
        margins = Dimensions(
          startDp = (measureTextWidth(
            text = "123",
            style = TextStyle.Default,
          ) + 16.dp).value, //TODO get longest label
          endDp = 0f,
          topDp = 0f,
          bottomDp = 0f,
        ),
        thickness = 1.dp,
      ),
      label = rememberAxisLabelComponent(
        padding = Dimensions.Empty,
        textSize = 14.sp,
        textAlignment = android.text.Layout.Alignment.ALIGN_CENTER,
        ellipsize = TextUtils.TruncateAt.START
      ),
      sizeConstraint = BaseAxis.SizeConstraint.Exact(48f),
      horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Inside,
      verticalLabelPosition = VerticalAxis.VerticalLabelPosition.Center,
      itemPlacer = remember { MyVerticalAxisItemPlacer },
      valueFormatter = remember { CartesianValueFormatter.decimal(DecimalFormat("#")) },
    ),
    bottomAxis = rememberBottomAxis(
      label = rememberAxisLabelComponent(),
      tick = null,
      axis = null,
      guideline = null,
      sizeConstraint = BaseAxis.SizeConstraint.Auto(24.dp.value, 48.dp.value),
      itemPlacer = remember {
        AxisItemPlacer.Horizontal.default(
          spacing = 5,
          offset = 1,
          shiftExtremeTicks = true,
          addExtremeLabelPadding = false,
        )
      },
    ),
  )

  val modelProducer = remember {
    CartesianChartModelProducer.build()
  }

  LaunchedEffect(Unit) {
    withContext(Dispatchers.Default) {
      modelProducer.tryRunTransaction {
        lineSeries {
          series(x, y)
        }
      }
    }
  }

  CartesianChartHost(
    modifier = Modifier
      .fillMaxWidth()
      .height(196.dp),
    chart = chart,
    modelProducer = modelProducer,
    zoomState = rememberVicoZoomState(initialZoom = Zoom.Content, zoomEnabled = false),
    horizontalLayout = HorizontalLayout.fullWidth(),
    diffAnimationSpec = null,
  )
}

@Composable
fun measureTextWidth(text: String, style: TextStyle): Dp {
  val textMeasurer = rememberTextMeasurer()
  val widthInPixels = textMeasurer.measure(text, style).size.width
  return with(LocalDensity.current) { widthInPixels.toDp() }
}

@Composable
fun rememberLayer(): LineCartesianLayer {
  val color = Color.Blue
  val color2 = Color.Red

  var value by remember {
    mutableFloatStateOf(0.4f)
  }

  LaunchedEffect(Unit) {
    animate(
      initialValue = 0f,
      targetValue = 1f,
      animationSpec = repeatable(
        Int.MAX_VALUE,
        animation = tween(easing = LinearEasing, durationMillis = 2000),
      ),
    ) { start, end ->
//      value = start
    }
  }

  return rememberLineCartesianLayer(
    listOf(
      rememberSplitLineSpec(
        shader = LinearGradientShader(
          colors = intArrayOf(android.graphics.Color.BLUE, android.graphics.Color.RED),
          positions = floatArrayOf(value, value),
          isHorizontal = true,
        ),
        backgroundShaderFirst = BrushShader(
          brush = Brush.verticalGradient(
            listOf(
              Color(color.toArgb()).copy(alpha = DefaultAlpha.LINE_BACKGROUND_SHADER_START),
              Color(color.toArgb()).copy(alpha = DefaultAlpha.LINE_BACKGROUND_SHADER_END),
            ),
          ),
        ),
        backgroundShaderSecond = BrushShader(
          brush = Brush.verticalGradient(
            listOf(
              Color(color2.toArgb()).copy(alpha = DefaultAlpha.LINE_BACKGROUND_SHADER_START),
              Color(color2.toArgb()).copy(alpha = DefaultAlpha.LINE_BACKGROUND_SHADER_END),
            ),
          ),
        ),
        xSplitFraction = value,
      ),
    ),
//    axisValueOverrider = remember { AxisValueOverrider.my() },
//    verticalAxisPosition = AxisPosition.Vertical.Start,
  )
}

