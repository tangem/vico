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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarkerVisibilityListener
import com.patrykandpatrick.vico.core.cartesian.marker.LineCartesianLayerMarkerTarget

@Composable
fun rememberMarkerVisibilityListener(
  yxFractionCallback : (y : Double?, x : Double?, xCanvas : Float?) -> Unit
): CartesianMarkerVisibilityListener {
  val hapticFeedback = LocalHapticFeedback.current

  return remember {
    object : CartesianMarkerVisibilityListener {
      override fun onShown(marker: CartesianMarker, targets: List<CartesianMarker.Target>) {
        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)

        val tg = targets[0] as LineCartesianLayerMarkerTarget
        val point = tg.points[0]
        yxFractionCallback(point.entry.y, point.entry.x, tg.canvasX)
      }

      override fun onHidden(marker: CartesianMarker) {
        yxFractionCallback(null, null, null)
      }

      override fun onUpdated(marker: CartesianMarker, targets: List<CartesianMarker.Target>) {
        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        val tg = targets[0] as LineCartesianLayerMarkerTarget
        val point = tg.points[0]
        yxFractionCallback(point.entry.y, point.entry.x, tg.canvasX)
      }
    }
  }

}
