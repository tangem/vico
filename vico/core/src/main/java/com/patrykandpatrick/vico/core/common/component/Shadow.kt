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

import android.graphics.Paint
import androidx.compose.runtime.Immutable
import com.patrykandpatrick.vico.core.common.Defaults
import com.patrykandpatrick.vico.core.common.MeasureContext
import com.patrykandpatrick.vico.core.common.alphaFloat
import com.patrykandpatrick.vico.core.common.copyColor

/**
 * Stores shadow properties.
 *
 * @param radiusDp the blur radius (in dp).
 * @param dxDp the horizontal offset (in dp).
 * @param dyDp the vertical offset (in dp).
 * @param color the color.
 */
@Immutable
public data class Shadow(
  private val radiusDp: Float,
  private val dxDp: Float = 0f,
  private val dyDp: Float = 0f,
  private val color: Int = Defaults.SHADOW_COLOR,
) {
  /** Updates [paint]’s shadow layer. */
  public fun updateShadowLayer(context: MeasureContext, paint: Paint, opacity: Float) {
    with(context) {
      paint.setShadowLayer(
        radiusDp.pixels,
        dxDp.pixels,
        dyDp.pixels,
        color.copyColor(opacity * color.alphaFloat),
      )
    }
  }
}
