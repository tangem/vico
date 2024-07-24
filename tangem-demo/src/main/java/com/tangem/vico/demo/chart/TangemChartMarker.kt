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

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.common.component.rememberLayeredComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberUnboundedLineComponent
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.compose.common.shape.dashed
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Defaults
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.shape.Shape

@Composable
fun rememberTangemChartMarker(
  color: Color,
): CartesianMarker {
  val indicatorFrontComponent =
    rememberShapeComponent(shape = Shape.Pill, color = MaterialTheme.colorScheme.surface)
  val indicatorCenterComponent = rememberShapeComponent(
    shape = Shape.Pill,
    color = color,
  )
  val indicatorRearComponent = rememberShapeComponent(
    shape = Shape.Pill,
    color = color.copy(alpha = .24f),
  )
  val indicator =
    rememberLayeredComponent(
      rear = indicatorRearComponent,
      front = rememberLayeredComponent(
        rear = indicatorCenterComponent,
        front = indicatorFrontComponent,
        padding = Dimensions.of(3.dp),
      ),
      padding = Dimensions.of(3.dp),
    )
  val guideline = rememberUnboundedLineComponent(
    color = color,
    verticalAddDrawSpace = 24.dp,
    shape = Shape.dashed(Shape.Rectangle, Defaults.DASH_LENGTH.dp, Defaults.DASH_GAP.dp),
  )
  return remember(indicator, guideline) {
    DefaultCartesianMarker(
      label = TextComponent(textSizeSp = 0f),
      indicator = { indicator },
      indicatorSizeDp = 16f,
      guideline = guideline,
    )
  }
}
