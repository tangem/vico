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

package com.patrykandpatrick.vico.core.cartesian

import com.patrykandpatrick.vico.core.cartesian.axis.Axis
import com.patrykandpatrick.vico.core.cartesian.layer.CartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker

/**
 * Enables a component to add insets to [CartesianChart]s to make room for itself. This is used by
 * [Axis], [CartesianMarker], and the like.
 */
public interface ChartInsetter<M> {
  /** Ensures that there are sufficient insets. */
  public fun updateInsets(
    context: CartesianMeasureContext,
    horizontalDimensions: HorizontalDimensions,
    model: M,
    insets: Insets,
  ) {}

  /**
   * Ensures that there are sufficient horizontal insets. [freeHeight] is the height of the
   * [CartesianLayer] area.
   */
  public fun updateHorizontalInsets(
    context: CartesianMeasureContext,
    freeHeight: Float,
    model: M,
    insets: HorizontalInsets,
  ) {}
}
