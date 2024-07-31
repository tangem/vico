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

//val xx = listOf(
//  5656320.0,5669280.0,5696640.0,5721120.0,5771520.0,5791680.0,5810400.0,5888160.0,5912640.0,5414400.0,5456160.0,5503680.0,5505120.0,5542560.0,5578560.0,5603040.0,5627520.0,0.0,60480.0,82080.0,92160.0,151200.0,152640.0,182880.0,241920.0,259200.0,275040.0,311040.0,341280.0,364320.0,423360.0,436320.0,465120.0,499680.0,544320.0,555840.0,577440.0,609120.0,642240.0,686880.0,725760.0,756000.0,758880.0,810720.0,839520.0,869760.0,901440.0,908640.0,961920.0,983520.0,1013760.0,1058400.0,1090080.0,1108800.0,1137600.0,1159200.0,1182240.0,1221120.0,1251360.0,1283040.0,1324800.0,1347840.0,1383840.0,1398240.0,1428480.0,1455840.0,1487520.0,1520640.0,1572480.0,1604160.0,1615680.0,1648800.0,1671840.0,1716480.0,1735200.0,1758240.0,1808640.0,1831680.0,1848960.0,1887840.0,1928160.0,1939680.0,1961280.0,2011680.0,2023200.0,2053440.0,2099520.0,2128320.0,2141280.0,2167200.0,2217600.0,2221920.0,2262240.0,2285280.0,2315520.0,2347200.0,2388960.0,2404800.0,2424960.0,2468160.0,2481120.0,2512800.0,2553120.0,2592000.0,2599200.0,2640960.0,2662560.0,2697120.0,2718720.0,2756160.0,2770560.0,2816640.0,2835360.0,2865600.0,2908800.0,2933280.0,2952000.0,2999520.0,3028320.0,3041280.0,3064320.0,3108960.0,3116160.0,3157920.0,3180960.0,3216960.0,3241440.0,3260160.0,3288960.0,3319200.0,3363840.0,3378240.0,3418560.0,3432960.0,3461760.0,3492000.0,3519360.0,3574080.0,3576960.0,3615840.0,3640320.0,3673440.0,3696480.0,3732480.0,3762720.0,3803040.0,3820320.0,3843360.0,3875040.0,3896640.0,3931200.0,3954240.0,4006080.0,4010400.0,4050720.0,4078080.0,4114080.0,4142880.0,4152960.0,4190400.0,4223520.0,4245120.0,4276800.0,4322880.0,4328640.0,4363200.0,4397760.0,4429440.0,4461120.0,4488480.0,4498560.0,4534560.0,4559040.0,4596480.0,4631040.0,4651200.0,4691520.0,4700160.0,4753440.0,4763520.0,4792320.0,4826880.0,4867200.0,4887360.0,4923360.0,4932000.0,4982400.0,5009760.0,5032800.0,5073120.0,5094720.0,5103360.0,5132160.0,5188320.0,5189760.0,5220000.0,5270400.0,5297760.0,5328000.0,5333760.0,5369760.0,5918072.0
//)
//val x = run {
//  val min = xx.min()
//  val max = xx.max()
//
//  xx.map { ((it - min) * 1000 / (max - min)).roundToInt().toDouble() }
//}
//
//val y = listOf(
//  0.57459880402,0.58913138779,0.707780710043,1.0,0.83815178279,0.79753441894,0.84181878841,0.764602731617,0.89437312525,0.40011053179,0.343256084151,0.36694570268,0.37087753949,0.510477262939,0.60307440876,0.6016741768,0.641832765814,0.001,0.001,0.001,0.0,0.0,0.001,0.001,0.001,0.001,0.002,0.015,0.007,0.012,0.007,0.006,0.008,0.004,0.005,0.005,0.0082,0.0067603983617,0.007658024428,0.0053692505765,0.006,0.003523574414,0.0036,0.0048,0.0042941126429,0.004,0.0014,0.002,0.002,0.0031,0.003,0.002,0.002,0.0021,0.003,0.003319275866,0.0031,0.0019485083319,0.002,0.002,0.0046,0.003,0.0054392657685,0.0053124970099,0.004,0.004,0.005,0.0046414319266,0.0053697531598,0.005,0.005,0.0095539301605,0.0085622388543,0.0061532343386,0.0068461604829,0.0069012666037,0.0074,0.0076949992198,0.0091,0.009,0.0117950407664,0.0145576673404,0.011,0.0153081908183,0.0167308414949,0.0119443864227,0.016,0.022,0.0326,0.0403,0.02546180117,0.03022089921,0.0574444446,0.06566975076,0.0486749038,0.07684467185,0.07940958451,0.11023767626,0.2522964316,0.2502762514,0.1964345895,0.09290592544,0.1569184411,0.09269393152,0.08984799725,0.13281393828,0.1146965061,0.08581287591,0.07959412768,0.11441048113,0.10056948078,0.099854588382,0.084662861572,0.090181249609,0.089109141273,0.0524224191038,0.0457489731835,0.0536054631717,0.0458154164217,0.0455502820994,0.0554283479907,0.053008852041,0.055266532768,0.070278753871,0.111258711737,0.104080213233,0.177045099545,0.170855833249,0.129312960482,0.14828483974,0.139482929327,0.109507994296,0.129195552676,0.126977455963,0.09709136274,0.089826196619,0.099724618635,0.140519116251,0.140399421655,0.069501399277,0.080078198652,0.092768788636,0.135455882067,0.13831477677,0.13161484199,0.124555073058,0.160830557283,0.167135053734,0.137727072896,0.139079653923,0.154719032607,0.184744585762,0.250178365273,0.246364658682,0.5580201741,0.41600361406,0.7888160261,0.838744243387,0.79849595734,0.864278411728,0.80391513646,0.478066513424,0.55539840388,0.429069783309,0.409517659292,0.65252923028,0.721309596186,0.56068254185,0.90615229645,0.9250436488,0.874604827408,0.6468637832,0.694390135,0.480845205471,0.609488519,0.60791737871,0.648995956279,0.63753889901,0.39502332972,0.393088481828,0.427232220361,0.264844113116,0.32564896507,0.333369802171,0.257340320917,0.30499905303,0.26690440492,0.290528415077,0.215638715406,0.223967999273,0.227461212078,0.23454480358,0.32496257018,0.278112210105,0.2756322826,0.389590724426,0.403333536141,0.359766641186,0.34290621483,0.359686441256,0.4297046794,0.93340870948
//)

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
//        .width(1000.dp)
        .onGloballyPositioned {
//          if (maxCanvasX == 0) {
            maxCanvasX = it.size.width
//          }
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
