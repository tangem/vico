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

import android.text.Layout
import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberCustomStartAxis
import com.patrykandpatrick.vico.compose.cartesian.fullWidth
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberSplitLine
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.compose.common.shader.color
import com.patrykandpatrick.vico.compose.common.shader.toDynamicShader
import com.patrykandpatrick.vico.core.cartesian.HorizontalLayout
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.AxisPosition
import com.patrykandpatrick.vico.core.cartesian.axis.BaseAxis
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.AxisValueOverrider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.patrykandpatrick.vico.core.common.shader.LinearGradientShader
import com.patrykandpatrick.vico.core.common.shape.Shape
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// max = 721
const val testNumberOfPoints = 721

val sortedData = marketChartData.sortedBy { it.first.toLong() }

val timestamps = sortedData.takeLast(testNumberOfPoints).map { it.first.toLong() }

val sorted = timestamps.sorted().also {
  Log.i( "ASDASD","""
    sorted: $it
    unsirt: $timestamps
  """.trimIndent())
}
val minTimestamp = timestamps.min()
val timestampsReady = timestamps.map { (it - minTimestamp) / 60000 }.takeLast(testNumberOfPoints)

val y = sortedData.take(testNumberOfPoints).map { BigDecimal(it.second).toDouble() }
private val x = timestampsReady.map { it.toDouble() }

private val redColor = Color(0xFFFF3333)
private val blueColor = Color(0xFF0099FF)

@Composable
fun MarketDetailsChart() {
  sorted
  val initDrawAnimationStart = remember { mutableStateOf(true) }
  var markerFraction: Float? by remember {
    mutableStateOf(null)
  }
  var lineColor by remember {
    mutableStateOf(redColor)
  }

  val layer = rememberLayer(
    lineColor = lineColor,
    startDrawingAnimation = initDrawAnimationStart,
    markerFraction = markerFraction,
  )

  var valueText by remember {
    mutableStateOf("")
  }
  var dateText by remember {
    mutableStateOf("")
  }
  var maxCanvasX by remember {
    mutableIntStateOf(0)
  }

  val chart = rememberCartesianChart(
    layer,
    startAxis = rememberMyStartAxis(),
    bottomAxis = rememberMyBottomAxis(),
    horizontalLayout = HorizontalLayout.fullWidth(),
    markerVisibilityListener = rememberMarkerVisibilityListener { y, x, xCanvas ->
      valueText = y?.toString() ?: ""
      dateText = x?.toString() ?: ""

      markerFraction = if (xCanvas == null || xCanvas == 0f) {
        null
      } else {
        val max = maxCanvasX.toFloat()
        if (max == 0f) {
          null
        } else {
          xCanvas / maxCanvasX.toFloat()
        }
      }
    },
    marker = rememberTangemChartMarker(color = lineColor),
  )

  val modelProducer = remember {
    CartesianChartModelProducer()
  }

  LaunchedEffect(Unit) {
    withContext(Dispatchers.Default) {
      modelProducer.runTransaction {
        lineSeries {
          series(x, y)
        }
      }
    }
  }

  val width = LocalConfiguration.current.screenWidthDp
  val height = LocalConfiguration.current.screenHeightDp
  val density = LocalDensity.current

  val widthPixels = with(density) { width.dp.toPx() }
  val heightPixels = with(density) { height.dp.toPx() }

  Column {
    Text("width: $width, height: $height")
    Text("widthPixels: $widthPixels, heightPixels: $heightPixels")
    Text("density: ${density.density}")
    Text(text = "Value: $valueText")
    Text(text = "Date: $dateText")

    CartesianChartHost(
      modifier = Modifier
        .fillMaxWidth()
        .onGloballyPositioned {
          maxCanvasX = it.size.width
        },
      chart = chart,
      modelProducer = modelProducer,
      zoomState = rememberVicoZoomState(initialZoom = Zoom.Content, zoomEnabled = false),
      animationSpec = null,

      )

    Spacer(modifier = Modifier.height(16.dp))
    Button(onClick = { initDrawAnimationStart.value = true }) {
      Text("Start Drawing Animation")
    }
    Button(
      onClick = {
        lineColor = if (lineColor == redColor) {
          blueColor
        } else {
          redColor
        }
      },
    ) {
      Text(text = "Change Line Color")
    }
  }
}

@Composable
fun rememberMyStartAxis(): VerticalAxis<AxisPosition.Vertical.Start> {
  return rememberCustomStartAxis(
    line = null,
    tick = null,
    guideline = rememberMyAxisGuidelineComponent(),
    label = rememberAxisLabelComponent(
      color = Color(0xFF909090),
      padding = Dimensions.of(
        start = 4.dp,
      ),
      textSize = 14.sp,
      textAlignment = Layout.Alignment.ALIGN_CENTER,
    ),
    horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Inside,
    verticalLabelPosition = VerticalAxis.VerticalLabelPosition.Center,
    itemPlacer = remember { VerticalAxis.ItemPlacer.count({ 3 }, false) },
    valueFormatter = remember { CartesianValueFormatter.decimal() },
  )
}

@Composable
fun rememberMyBottomAxis(): HorizontalAxis<AxisPosition.Horizontal.Bottom> {
  return rememberBottomAxis(
    label = rememberAxisLabelComponent(
      color = Color(0xFF909090),
      padding = Dimensions.of(
        top = 24.dp,
      ),
    ),
    line = null,
    tick = null,
    guideline = null,
    sizeConstraint = BaseAxis.SizeConstraint.Auto(24.dp.value, 48.dp.value),
    itemPlacer = remember {
      HorizontalAxis.ItemPlacer.default(
        spacing = 20,
        offset = 60,
        shiftExtremeTicks = false,
        addExtremeLabelPadding = false,
      )
    },
    valueFormatter = { value, chartValues, verticalAxisPosition ->
      value.toString()
    },
  )
}

@Composable
fun rememberMyAxisGuidelineComponent(): LineComponent {
  return rememberAxisGuidelineComponent(
    color = Color(0x1fc9c9c9),
    shape = Shape.Rectangle,
    margins = Dimensions(
      startDp = (measureTextWidth(
        text = "55555",
        style = TextStyle.Default,
      ) + 16.dp).value, //get longest label's text
      endDp = 0f,
      topDp = 0f,
      bottomDp = 0f,
    ),
    thickness = 2.dp,
  )
}

@Composable
fun measureTextWidth(text: String, style: TextStyle): Dp {
  val textMeasurer = rememberTextMeasurer()
  val density = LocalDensity.current
  return remember(textMeasurer, density, text, style) {
    val widthInPixels = textMeasurer.measure(text, style).size.width
    with(density) { widthInPixels.toDp() }
  }
}

@Composable
fun rememberLayer(
  lineColor: Color,
  markerFraction: Float? = null,
  startDrawingAnimation: MutableState<Boolean>,
): LineCartesianLayer {
  val backColor = listOf(lineColor.copy(alpha = 0.24f), Color.Transparent)

  val lineColor2 = Color(0x33909090)
  val backColor2 = listOf(Color(0x3d909090), Color.Transparent)

  var value by remember {
    mutableFloatStateOf(0f)
  }

  LaunchedEffect(startDrawingAnimation.value) {
    if (startDrawingAnimation.value) {
      animate(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = tween(easing = LinearEasing, durationMillis = 2000),
      ) { start, end ->
        value = start
        if (start == 1f) {
          startDrawingAnimation.value = false
        }
      }
    }
  }

  return buildLayer(
    fractionValue = markerFraction ?: value,
    lineColor = if (markerFraction != null) {
      lineColor2
    } else {
      lineColor
    },
    backColor = if (markerFraction != null) {
      backColor2
    } else {
      backColor
    },
    lineColorRight = if (markerFraction != null) {
      lineColor
    } else {
      Color.Transparent
    },
    backColorRight = if (markerFraction != null) {
      backColor
    } else {
      listOf(Color.Transparent, Color.Transparent)
    },
  )
}

@Composable
fun buildLayer(
  fractionValue: Float,
  lineColor: Color,
  backColor: List<Color>,
  lineColorRight: Color? = null,
  backColorRight: List<Color>? = null,
): LineCartesianLayer {
  val alineColor = remember(lineColor) { lineColor.toArgb() }
  val alineColorRight = remember(lineColorRight) { lineColorRight?.toArgb() }

  if (alineColorRight == null || backColorRight == null) {
    return rememberLineCartesianLayer(
      LineCartesianLayer.LineProvider.series(
        listOf(
          rememberLine(
            shader = DynamicShader.color(lineColor),
            thickness = 0.5.dp,
            backgroundShader = Brush.verticalGradient(backColor).toDynamicShader(),
          ),
        ),
      ),
    )
  }

  return rememberLineCartesianLayer(
    lineProvider = LineCartesianLayer.LineProvider.series(
      rememberSplitLine(
        shader = remember(alineColor, alineColorRight, fractionValue) {
          LinearGradientShader(
            colors = intArrayOf(alineColor, alineColorRight),
            positions = floatArrayOf(fractionValue, fractionValue),
            isHorizontal = true,
          )
        },
        backgroundShaderFirst = Brush.verticalGradient(backColor).toDynamicShader(),
        backgroundShaderSecond = Brush.verticalGradient(backColorRight).toDynamicShader(),
        xSplitFraction = fractionValue,
      ),
    ),
    axisValueOverrider = remember { AxisValueOverrider.fixed(minY = y.min()) },
  )

}
