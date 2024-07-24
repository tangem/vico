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

package com.patrykandpatrick.vico.core.common

import android.graphics.Canvas
import android.graphics.RectF
import com.patrykandpatrick.vico.core.common.data.CacheStore
import com.patrykandpatrick.vico.core.common.data.MutableExtraStore

/** Holds data used for measuring and drawing. */
public interface MeasureContext {
  /** The bounds of the [Canvas]. */
  public val canvasBounds: RectF

  /** The number of pixels corresponding to one density-independent pixel. */
  public val density: Float

  /** The number of pixels corresponding to this number of density-independent pixels. */
  public val Float.pixels: Float
    get() = this * density

  /**
   * The number of pixels corresponding to this number of density-independent pixels, rounded down
   * to an integer.
   */
  public val Float.wholePixels: Int
    get() = pixels.toInt()

  /** Returns the number of pixels corresponding to [dp] density-independent pixels. */
  public fun dpToPx(dp: Float): Float = dp * density

  /** Returns the number of pixels corresponding to [sp] scalable pixels. */
  public fun spToPx(sp: Float): Float

  /** Whether the layout direction is left to right. */
  public val isLtr: Boolean

  /** Stores temporary auxiliary data. */
  @Deprecated(
    "To cache drawing data, use `cacheStore`. If using `extraStore` for communication between " +
      "functions or classes, switch to a suitable alternative."
  )
  public val extraStore: MutableExtraStore

  /** Caches drawing data. */
  public val cacheStore: CacheStore

  /** `1f` if [isLtr] is `true`, and `-1f` otherwise. */
  public val layoutDirectionMultiplier: Float
    get() = if (isLtr) 1f else -1f

  /** Removes all temporary data. */
  public fun reset() {
    @Suppress("DEPRECATION") extraStore.clear()
    cacheStore.purge()
  }
}
