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


/**
 * This class represents an unrestricted vertical axis. It extends the VerticalAxis class and
 * provides additional functionality for handling vertical axis without any restrictions.
 *
 * @param position The position of the axis
 *
 * @see VerticalAxis
 * @see AxisPosition.Vertical
 */
public class UnrestrictedVerticalAxis<Position : AxisPosition.Vertical>(position: Position) :
  VerticalAxis<Position>(position = position) {

  /**
   * This function checks if the axis is not in restricted bounds.
   * As this is an unrestricted vertical axis, it always returns true.
   *
   * @param left The left bound.
   * @param top The top bound.
   * @param right The right bound.
   * @param bottom The bottom bound.
   * @return true as this is an unrestricted vertical axis.
   */
  override fun isNotInRestrictedBounds(
    left: Float,
    top: Float,
    right: Float,
    bottom: Float
  ): Boolean = true

  public class Builder<Position : AxisPosition.Vertical>(
    builder: BaseAxis.Builder<Position>? = null
  ) : BaseAxis.Builder<Position>(builder) {
    /**
     * Determines for what _y_ values this [VerticalAxis] is to display labels, ticks, and
     * guidelines.
     */
    public var itemPlacer: AxisItemPlacer.Vertical = AxisItemPlacer.Vertical.step()

    /** Defines the horizontal position of each axis label relative to the axis line. */
    public var horizontalLabelPosition: HorizontalLabelPosition = HorizontalLabelPosition.Outside

    /** Defines the vertical position of each axis label relative to its corresponding tick. */
    public var verticalLabelPosition: VerticalLabelPosition = VerticalLabelPosition.Center

    /** Creates a [VerticalAxis] instance with the properties from this [Builder]. */
    @Suppress("UNCHECKED_CAST")
    public inline fun <reified T : Position> build(): UnrestrictedVerticalAxis<T> {
      val position =
        when (T::class.java) {
          AxisPosition.Vertical.Start::class.java -> AxisPosition.Vertical.Start
          AxisPosition.Vertical.End::class.java -> AxisPosition.Vertical.End
          else ->
            throw IllegalStateException("Got unknown AxisPosition class ${T::class.java.name}")
        }
          as Position
      return setTo(UnrestrictedVerticalAxis(position)).also { axis ->
        axis.itemPlacer = itemPlacer
        axis.horizontalLabelPosition = horizontalLabelPosition
        axis.verticalLabelPosition = verticalLabelPosition
      } as UnrestrictedVerticalAxis<T>
    }
  }

  /** Houses a [VerticalAxis] factory function. */
  public companion object {
    /** Creates a [VerticalAxis] via [Builder]. */
    public inline fun <reified P : AxisPosition.Vertical> build(
      block: Builder<P>.() -> Unit = {}
    ): UnrestrictedVerticalAxis<P> = Builder<P>().apply(block).build()
  }
}
