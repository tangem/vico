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

package com.patrykandpatrick.vico.views.common.theme

import android.content.Context
import android.content.res.TypedArray
import androidx.annotation.StyleableRes
import com.patrykandpatrick.vico.core.common.shape.Corner
import com.patrykandpatrick.vico.core.common.shape.CornerTreatment
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import com.patrykandpatrick.vico.core.common.shape.CutCornerTreatment
import com.patrykandpatrick.vico.core.common.shape.DashedShape
import com.patrykandpatrick.vico.core.common.shape.RoundedCornerTreatment
import com.patrykandpatrick.vico.core.common.shape.Shape
import com.patrykandpatrick.vico.core.common.shape.SharpCornerTreatment
import com.patrykandpatrick.vico.views.R

private const val ONE_HUNDRED_PERCENT = 100

internal fun TypedArray.getShape(context: Context): Shape {
  val shape =
    CorneredShape(
      topLeft =
        getCorner(
          context = context,
          sizeIndex = R.styleable.ShapeStyle_topStartCornerSize,
          treatmentIndex = R.styleable.ShapeStyle_topStartCornerTreatment,
        ),
      topRight =
        getCorner(
          context = context,
          sizeIndex = R.styleable.ShapeStyle_topEndCornerSize,
          treatmentIndex = R.styleable.ShapeStyle_topEndCornerTreatment,
        ),
      bottomLeft =
        getCorner(
          context = context,
          sizeIndex = R.styleable.ShapeStyle_bottomStartCornerSize,
          treatmentIndex = R.styleable.ShapeStyle_bottomStartCornerTreatment,
        ),
      bottomRight =
        getCorner(
          context = context,
          sizeIndex = R.styleable.ShapeStyle_bottomEndCornerSize,
          treatmentIndex = R.styleable.ShapeStyle_bottomEndCornerTreatment,
        ),
    )

  val dashLengthDp =
    getRawDimension(context = context, index = R.styleable.ShapeStyle_dashLength, defaultValue = 0f)

  return if (dashLengthDp == 0f) {
    shape
  } else {
    DashedShape(
      shape = shape,
      dashLengthDp = dashLengthDp,
      gapLengthDp =
        getRawDimension(
          context,
          R.styleable.ShapeStyle_gapLength,
          getRawDimension(context, R.styleable.ShapeStyle_dashGapLength, 0f),
        ),
    )
  }
}

private fun TypedArray.getCorner(
  context: Context,
  @StyleableRes sizeIndex: Int,
  @StyleableRes treatmentIndex: Int,
  handleNullSizeIndex: Boolean = true,
): Corner =
  when {
    !hasValue(sizeIndex) && handleNullSizeIndex -> {
      getCorner(
        context = context,
        sizeIndex = R.styleable.ShapeStyle_cornerSize,
        treatmentIndex = treatmentIndex,
        handleNullSizeIndex = false,
      )
    }
    isFraction(sizeIndex) -> {
      val percentage = (getFraction(sizeIndex, defaultValue = 0f) * ONE_HUNDRED_PERCENT).toInt()
      Corner.Relative(
        percentage = percentage,
        cornerTreatment =
          if (percentage == 0) {
            SharpCornerTreatment
          } else {
            getCornerTreatment(treatmentIndex)
          },
      )
    }
    else -> {
      val sizeDp = getRawDimension(context, sizeIndex, defaultValue = 0f)
      Corner.Absolute(
        sizeDp = sizeDp,
        cornerTreatment =
          if (sizeDp == 0f) {
            SharpCornerTreatment
          } else {
            getCornerTreatment(treatmentIndex)
          },
      )
    }
  }

private fun TypedArray.getCornerTreatment(
  @StyleableRes index: Int,
  defaultValue: Int = -1,
): CornerTreatment =
  when (getInt(index, defaultValue)) {
    -1 -> getCornerTreatment(R.styleable.ShapeStyle_cornerTreatment, defaultValue = 0)
    0 -> RoundedCornerTreatment
    else -> CutCornerTreatment
  }
