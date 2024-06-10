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
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberSplitLineSpec
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.compose.common.shader.BrushShader
import com.patrykandpatrick.vico.core.cartesian.HorizontalLayout
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.AxisItemPlacer
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
import com.patrykandpatrick.vico.core.common.shader.ColorShader
import com.patrykandpatrick.vico.core.common.shader.LinearGradientShader
import com.patrykandpatrick.vico.core.common.shape.Shape
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


private val y = listOf(
  59270, 61748, 61941, 62889, 62740, 62989, 63858, 63608, 63951, 63917,
  63253, 63765, 63862, 64502, 63876, 64028, 63875, 64531, 64249, 63680,
  63228, 63159, 63249, 63664, 63469, 63711, 63003, 62333, 62882, 62183,
  62231, 62244, 62200, 61188, 61683, 61199, 61036, 62116, 62436, 63065,
  62908, 63067, 63294, 60904, 60677, 60790, 60705, 61041, 60667, 61158,
  61112, 60795, 60900, 60771, 61124, 61343, 61371, 61484, 61133, 62372,
  62704, 63007, 63092, 62905, 62470, 62019, 61756, 61776, 61557, 61540,
  61904, 62151, 62419, 64663, 65955, 66229, 65991, 66176, 66517, 65838,
  65210, 65216, 65611, 66467, 66209, 67247, 66913, 67059, 66978, 66845,
  67240, 66874, 66993, 66940, 67185, 67330, 67340, 66845, 66062, 66273,
  66645, 66865, 67005, 67382, 70049, 71464, 71293, 70875, 71137, 69720,
  69323, 70139, 69964, 69716, 69855, 70442, 69774, 69125, 69404, 69714,
  69967, 68042, 67077, 67938, 67833, 67182, 67306, 68327, 69093, 68517,
  68726, 68759, 69061, 68880, 69148, 69305, 69044, 69313, 69122, 68821,
  68854, 68506, 68823, 68613, 68420, 70356, 69241, 69401, 68004, 67630,
  68311, 68311, 68291, 68302, 68717, 67926, 67690, 67314, 67285, 67567,
  68026, 67552, 67740, 68519, 68611, 68363, 68503, 68171, 68326, 67121,
  67626, 67481, 67657, 67567, 67608, 67607, 67683, 67729, 67733, 67709,
)
val timestamps: List<Long> = listOf(
  1714752000000,
  1714766400000,
  1714780800000,
  1714795200000,
  1714809600000,
  1714824000000,
  1714838400000,
  1714852800000,
  1714867200000,
  1714881600000,
  1714896000000,
  1714910400000,
  1714924800000,
  1714939200000,
  1714953600000,
  1714968000000,
  1714982400000,
  1714996800000,
  1715011200000,
  1715025600000,
  1715040000000,
  1715054400000,
  1715068800000,
  1715083200000,
  1715097600000,
  1715112000000,
  1715126400000,
  1715140800000,
  1715155200000,
  1715169600000,
  1715184000000,
  1715198400000,
  1715212800000,
  1715227200000,
  1715241600000,
  1715256000000,
  1715270400000,
  1715284800000,
  1715299200000,
  1715313600000,
  1715328000000,
  1715342400000,
  1715356800000,
  1715371200000,
  1715385600000,
  1715400000000,
  1715414400000,
  1715428800000,
  1715443200000,
  1715457600000,
  1715472000000,
  1715486400000,
  1715500800000,
  1715515200000,
  1715529600000,
  1715544000000,
  1715558400000,
  1715572800000,
  1715587200000,
  1715601600000,
  1715616000000,
  1715630400000,
  1715644800000,
  1715659200000,
  1715673600000,
  1715688000000,
  1715702400000,
  1715716800000,
  1715731200000,
  1715745600000,
  1715760000000,
  1715774400000,
  1715788800000,
  1715803200000,
  1715817600000,
  1715832000000,
  1715846400000,
  1715860800000,
  1715875200000,
  1715889600000,
  1715904000000,
  1715918400000,
  1715932800000,
  1715947200000,
  1715961600000,
  1715976000000,
  1715990400000,
  1716004800000,
  1716019200000,
  1716033600000,
  1716048000000,
  1716062400000,
  1716076800000,
  1716091200000,
  1716105600000,
  1716120000000,
  1716134400000,
  1716148800000,
  1716163200000,
  1716177600000,
  1716192000000,
  1716206400000,
  1716220800000,
  1716235200000,
  1716249600000,
  1716264000000,
  1716278400000,
  1716292800000,
  1716307200000,
  1716321600000,
  1716336000000,
  1716350400000,
  1716364800000,
  1716379200000,
  1716393600000,
  1716408000000,
  1716422400000,
  1716436800000,
  1716451200000,
  1716465600000,
  1716480000000,
  1716494400000,
  1716508800000,
  1716523200000,
  1716537600000,
  1716552000000,
  1716566400000,
  1716580800000,
  1716595200000,
  1716609600000,
  1716624000000,
  1716638400000,
  1716652800000,
  1716667200000,
  1716681600000,
  1716696000000,
  1716710400000,
  1716724800000,
  1716739200000,
  1716753600000,
  1716768000000,
  1716782400000,
  1716796800000,
  1716811200000,
  1716825600000,
  1716840000000,
  1716854400000,
  1716868800000,
  1716883200000,
  1716897600000,
  1716912000000,
  1716926400000,
  1716940800000,
  1716955200000,
  1716969600000,
  1716984000000,
  1716998400000,
  1717012800000,
  1717027200000,
  1717041600000,
  1717056000000,
  1717070400000,
  1717084800000,
  1717099200000,
  1717113600000,
  1717128000000,
  1717142400000,
  1717156800000,
  1717171200000,
  1717185600000,
  1717200000000,
  1717214400000,
  1717228800000,
  1717243200000,
  1717257600000,
  1717272000000,
  1717286400000,
  1717300800000,
  1717315200000,
  1717329600000,
)

private val x = List(y.size) { index -> index }

private val redColor = Color(0xFFFF3333)
private val blueColor = Color(0xFF0099FF)

@Composable
fun MarketDetailsChart(modifier: Modifier = Modifier) {
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

  val chart = rememberCartesianChart(
    layer,
    startAxis = rememberMyStartAxis(),
    bottomAxis = rememberMyBottomAxis(),
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

  var valueText by remember {
    mutableStateOf("")
  }
  var dateText by remember {
    mutableStateOf("")
  }
  var maxCanvasX by remember {
    mutableIntStateOf(0)
  }

  Column {
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
      horizontalLayout = HorizontalLayout.fullWidth(),
      diffAnimationSpec = null,
      markerVisibilityListener = rememberMarkerVisibilityListener { y, x, xCanvas ->
        valueText = y?.toString() ?: ""
        dateText = x?.let {
          timestamps[it.toInt()].toLocalDateTime()
            .format(DateTimeFormatter.ofPattern("dd.MM", Locale.getDefault()))
        } ?: ""

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
fun rememberMyStartAxis(modifier: Modifier = Modifier): VerticalAxis<AxisPosition.Vertical.Start> {
  return rememberCustomStartAxis(
    axis = null,
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
    itemPlacer = remember { AxisItemPlacer.Vertical.count({ 3 }, false) },
    valueFormatter = remember { CartesianValueFormatter.decimal() },
  )
}

@Composable
fun rememberMyBottomAxis(modifier: Modifier = Modifier): HorizontalAxis<AxisPosition.Horizontal.Bottom> {
  return rememberBottomAxis(
    label = rememberAxisLabelComponent(
      color = Color(0xFF909090),
      padding = Dimensions.of(
        top = 24.dp,
      ),
    ),
    tick = null,
    axis = null,
    guideline = null,
    sizeConstraint = BaseAxis.SizeConstraint.Auto(24.dp.value, 48.dp.value),
    itemPlacer = remember {
      AxisItemPlacer.Horizontal.default(
        spacing = 20,
        offset = 60,
        shiftExtremeTicks = false,
        addExtremeLabelPadding = false,
      )
    },
    valueFormatter = CartesianValueFormatter { value, chartValues, verticalAxisPosition ->
      timestamps[value.toInt()].toLocalDateTime()
        .format(DateTimeFormatter.ofPattern("dd.MM", Locale.getDefault()))
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
      listOf(
        rememberLineSpec(
          shader = ColorShader(
            color = alineColor,
          ),
          thickness = 0.5.dp,
          backgroundShader = BrushShader(
            brush = Brush.verticalGradient(backColor),
          ),
        ),
      ),

      )
  }

  return rememberLineCartesianLayer(
    listOf(
      rememberSplitLineSpec(
        shader = LinearGradientShader(
          colors = intArrayOf(alineColor, alineColorRight),
          positions = floatArrayOf(fractionValue, fractionValue),
          isHorizontal = true,
        ),
        backgroundShaderFirst = BrushShader(
          brush = Brush.verticalGradient(backColor),
        ),
        backgroundShaderSecond = BrushShader(
          brush = Brush.verticalGradient(backColorRight),
        ),
        xSplitFraction = fractionValue,
      ),
    ),
    axisValueOverrider = remember { AxisValueOverrider.fixed(minY = y.min().toFloat()) },
  )

}


fun Long.toLocalDateTime(isMillis: Boolean = true): LocalDateTime {
  val zoneOffset = OffsetDateTime.now(ZoneOffset.systemDefault()).offset
  val epochSeconds = if (isMillis) TimeUnit.MILLISECONDS.toSeconds(this) else this
  return LocalDateTime.ofEpochSecond(epochSeconds, 0, zoneOffset)
}

