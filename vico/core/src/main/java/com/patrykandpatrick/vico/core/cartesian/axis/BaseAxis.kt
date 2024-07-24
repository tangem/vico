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

package com.patrykandpatrick.vico.core.cartesian.axis

import android.graphics.RectF
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.common.MeasureContext
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.orZero
import com.patrykandpatrick.vico.core.common.setAll

/**
 * A base [Axis] implementation. This is extended by [HorizontalAxis] and [VerticalAxis].
 *
 * @property line used for the axis line.
 * @property label used for the labels.
 * @property labelRotationDegrees the label rotation (in degrees).
 * @property valueFormatter formats the values.
 * @property tick used for the ticks.
 * @property tickLengthDp the tick length (in dp).
 * @property guideline used for the guidelines.
 * @property sizeConstraint determines how the [BaseAxis] sizes itself.
 * @property titleComponent the title [TextComponent].
 * @property title the title text.
 */
public abstract class BaseAxis<Position : AxisPosition>(
  public var line: LineComponent?,
  public var label: TextComponent?,
  public var labelRotationDegrees: Float,
  public var valueFormatter: CartesianValueFormatter,
  public var tick: LineComponent?,
  public var tickLengthDp: Float,
  public var guideline: LineComponent?,
  public var sizeConstraint: SizeConstraint,
  public var titleComponent: TextComponent?,
  public var title: CharSequence?,
) : Axis<Position> {
  private val restrictedBounds: MutableList<RectF> = mutableListOf()

  override val bounds: RectF = RectF()

  protected val MeasureContext.lineThickness: Float
    get() = line?.thicknessDp.orZero.pixels

  protected val MeasureContext.tickThickness: Float
    get() = tick?.thicknessDp.orZero.pixels

  protected val MeasureContext.guidelineThickness: Float
    get() = guideline?.thicknessDp.orZero.pixels

  protected val MeasureContext.tickLength: Float
    get() = if (tick != null) tickLengthDp.pixels else 0f

  override fun setRestrictedBounds(vararg bounds: RectF?) {
    restrictedBounds.setAll(bounds.filterNotNull())
  }

  protected open fun isNotInRestrictedBounds(
    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
  ): Boolean =
    restrictedBounds.none {
      it.contains(left, top, right, bottom) || it.intersects(left, top, right, bottom)
    }

  /**
   * Determines how a [BaseAxis] sizes itself.
   * - For [VerticalAxis], this defines the width.
   * - For [HorizontalAxis], this defines the height.
   */
  public sealed class SizeConstraint {
    /**
     * The axis will measure itself and use as much space as it needs, but no less than [minSizeDp],
     * and no more than [maxSizeDp].
     */
    public class Auto(
      public val minSizeDp: Float = 0f,
      public val maxSizeDp: Float = Float.MAX_VALUE,
    ) : SizeConstraint()

    /** The axis size will be exactly [sizeDp]. */
    public class Exact(public val sizeDp: Float) : SizeConstraint()

    /**
     * The axis will use a fraction of the available space.
     *
     * @property fraction the fraction of the available space that the axis should use.
     */
    public class Fraction(public val fraction: Float) : SizeConstraint() {
      init {
        require(fraction in MIN..MAX) {
          "Expected a value in the interval [$MIN, $MAX]. Got $fraction."
        }
      }

      private companion object {
        const val MIN = 0f
        const val MAX = 0.5f
      }
    }

    /**
     * The axis will measure the width of its label component ([label]) for the given [String]
     * ([text]), and it will use this width as its size. In the case of [VerticalAxis], the width of
     * the axis line and the tick length will also be considered.
     */
    public class TextWidth(public val text: String) : SizeConstraint()
  }
}
