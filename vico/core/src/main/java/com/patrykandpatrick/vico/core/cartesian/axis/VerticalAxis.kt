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

import androidx.annotation.RestrictTo
import com.patrykandpatrick.vico.core.cartesian.CartesianChart
import com.patrykandpatrick.vico.core.cartesian.CartesianDrawContext
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasureContext
import com.patrykandpatrick.vico.core.cartesian.HorizontalDimensions
import com.patrykandpatrick.vico.core.cartesian.HorizontalInsets
import com.patrykandpatrick.vico.core.cartesian.Insets
import com.patrykandpatrick.vico.core.cartesian.MutableHorizontalDimensions
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis.HorizontalLabelPosition.Inside
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis.HorizontalLabelPosition.Outside
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis.VerticalLabelPosition.Center
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.ChartValues
import com.patrykandpatrick.vico.core.cartesian.layer.CartesianLayer
import com.patrykandpatrick.vico.core.common.HorizontalPosition
import com.patrykandpatrick.vico.core.common.VerticalPosition
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.half
import com.patrykandpatrick.vico.core.common.orZero
import com.patrykandpatrick.vico.core.common.translate
import kotlin.math.ceil
import kotlin.math.max

private const val TITLE_ABS_ROTATION_DEGREES = 90f

/**
 * Draws vertical axes. See the [BaseAxis] documentation for descriptions of the inherited
 * properties.
 *
 * @property itemPlacer determines for what _y_ values the [VerticalAxis] displays labels, ticks,
 *   and guidelines.
 * @property horizontalLabelPosition defines the horizontal position of the labels relative to the
 *   axis line.
 * @property verticalLabelPosition defines the vertical positions of the labels relative to their
 *   ticks.
 */
public open class VerticalAxis<Position : AxisPosition.Vertical>
protected constructor(
  override val position: Position,
  line: LineComponent?,
  label: TextComponent?,
  labelRotationDegrees: Float,
  public var horizontalLabelPosition: HorizontalLabelPosition = Outside,
  public var verticalLabelPosition: VerticalLabelPosition = Center,
  valueFormatter: CartesianValueFormatter,
  tick: LineComponent?,
  tickLengthDp: Float,
  guideline: LineComponent?,
  public var itemPlacer: ItemPlacer = ItemPlacer.step(),
  sizeConstraint: SizeConstraint,
  titleComponent: TextComponent?,
  title: CharSequence?,
) :
  BaseAxis<Position>(
    line,
    label,
    labelRotationDegrees,
    valueFormatter,
    tick,
    tickLengthDp,
    guideline,
    sizeConstraint,
    titleComponent,
    title,
  ) {
  protected val areLabelsOutsideAtStartOrInsideAtEnd: Boolean
    get() =
      horizontalLabelPosition == Outside && position is AxisPosition.Vertical.Start ||
        horizontalLabelPosition == Inside && position is AxisPosition.Vertical.End

  protected val textHorizontalPosition: HorizontalPosition
    get() =
      if (areLabelsOutsideAtStartOrInsideAtEnd) HorizontalPosition.Start else HorizontalPosition.End

  protected var maxLabelWidth: Float? = null

  /** @suppress */
  @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
  public constructor(
    position: Position,
    horizontalLabelPosition: HorizontalLabelPosition,
    verticalLabelPosition: VerticalLabelPosition,
    itemPlacer: ItemPlacer,
  ) : this(
    position = position,
    line = null,
    label = null,
    labelRotationDegrees = 0f,
    horizontalLabelPosition = horizontalLabelPosition,
    verticalLabelPosition = verticalLabelPosition,
    valueFormatter = CartesianValueFormatter.decimal(),
    tick = null,
    tickLengthDp = 0f,
    guideline = null,
    itemPlacer = itemPlacer,
    sizeConstraint = SizeConstraint.Auto(),
    titleComponent = null,
    title = null,
  )

  override fun drawUnderLayers(context: CartesianDrawContext) {
    with(context) {
      var centerY: Float
      val yRange = chartValues.getYRange(position)
      val maxLabelHeight = getMaxLabelHeight()
      val lineValues =
        itemPlacer.getLineValues(this, bounds.height(), maxLabelHeight, position)
          ?: itemPlacer.getLabelValues(this, bounds.height(), maxLabelHeight, position)

      lineValues.forEach { lineValue ->
        centerY =
          bounds.bottom - bounds.height() * ((lineValue - yRange.minY) / yRange.length).toFloat() +
            getLineCanvasYCorrection(guidelineThickness, lineValue)

        guideline
          ?.takeIf {
            isNotInRestrictedBounds(
              left = layerBounds.left,
              top = centerY - guidelineThickness.half,
              right = layerBounds.right,
              bottom = centerY + guidelineThickness.half,
            )
          }
          ?.drawHorizontal(
            context = context,
            left = layerBounds.left,
            right = layerBounds.right,
            centerY = centerY,
          )
      }
      val lineExtensionLength = if (itemPlacer.getShiftTopLines(this)) tickThickness else 0f
      line?.drawVertical(
        context = context,
        top = bounds.top - lineExtensionLength,
        bottom = bounds.bottom + lineExtensionLength,
        centerX =
          if (position.isLeft(isLtr = isLtr)) {
            bounds.right - lineThickness.half
          } else {
            bounds.left + lineThickness.half
          },
      )
    }
  }

  override fun drawOverLayers(context: CartesianDrawContext) {
    with(context) {
      val label = label
      val labelValues =
        itemPlacer.getLabelValues(this, bounds.height(), getMaxLabelHeight(), position)
      val tickLeftX = getTickLeftX()
      val tickRightX = tickLeftX + lineThickness + tickLength
      val labelX = if (areLabelsOutsideAtStartOrInsideAtEnd == isLtr) tickLeftX else tickRightX
      var tickCenterY: Float
      val yRange = chartValues.getYRange(position)

      labelValues.forEach { labelValue ->
        tickCenterY =
          bounds.bottom - bounds.height() * ((labelValue - yRange.minY) / yRange.length).toFloat() +
            getLineCanvasYCorrection(tickThickness, labelValue)

        tick?.drawHorizontal(
          context = context,
          left = tickLeftX,
          right = tickRightX,
          centerY = tickCenterY,
        )

        label ?: return@forEach
        drawLabel(
          context = this,
          labelComponent = label,
          label = valueFormatter.format(labelValue, chartValues, position),
          labelX = labelX,
          tickCenterY = tickCenterY,
        )
      }

      title?.let { title ->
        titleComponent?.draw(
          context = this,
          text = title,
          x = if (position.isLeft(isLtr)) bounds.left else bounds.right,
          y = bounds.centerY(),
          horizontalPosition =
            if (position.isStart) HorizontalPosition.End else HorizontalPosition.Start,
          verticalPosition = VerticalPosition.Center,
          rotationDegrees = TITLE_ABS_ROTATION_DEGREES * if (position.isStart) -1f else 1f,
          maxHeight = bounds.height().toInt(),
        )
      }
    }
  }

  override fun updateHorizontalDimensions(
    context: CartesianMeasureContext,
    horizontalDimensions: MutableHorizontalDimensions,
  ): Unit = Unit

  protected open fun drawLabel(
    context: CartesianDrawContext,
    labelComponent: TextComponent,
    label: CharSequence,
    labelX: Float,
    tickCenterY: Float,
  ): Unit =
    with(context) {
      val textBounds =
        labelComponent
          .getBounds(context = this, text = label, rotationDegrees = labelRotationDegrees)
          .apply { translate(labelX, tickCenterY - centerY()) }

      if (
        horizontalLabelPosition == Outside ||
          isNotInRestrictedBounds(
            left = textBounds.left,
            top = textBounds.top,
            right = textBounds.right,
            bottom = textBounds.bottom,
          )
      ) {
        labelComponent.draw(
          context = this,
          text = label,
          x = labelX,
          y = tickCenterY,
          horizontalPosition = textHorizontalPosition,
          verticalPosition = verticalLabelPosition.textPosition,
          rotationDegrees = labelRotationDegrees,
          maxWidth = (maxLabelWidth ?: (layerBounds.width().half - tickLength)).toInt(),
        )
      }
    }

  protected fun CartesianMeasureContext.getTickLeftX(): Float {
    val onLeft = position.isLeft(isLtr = isLtr)
    val base = if (onLeft) bounds.right else bounds.left
    return when {
      onLeft && horizontalLabelPosition == Outside -> base - lineThickness - tickLength
      onLeft && horizontalLabelPosition == Inside -> base - lineThickness
      horizontalLabelPosition == Outside -> base
      horizontalLabelPosition == Inside -> base - tickLength
      else -> error("Unexpected combination of axis position and label position")
    }
  }

  override fun updateHorizontalInsets(
    context: CartesianMeasureContext,
    freeHeight: Float,
    model: CartesianChartModel,
    insets: HorizontalInsets,
  ) {
    val width = getWidth(context, freeHeight)
    when {
      position.isStart -> insets.ensureValuesAtLeast(start = width)
      position.isEnd -> insets.ensureValuesAtLeast(end = width)
    }
  }

  override fun updateInsets(
    context: CartesianMeasureContext,
    horizontalDimensions: HorizontalDimensions,
    model: CartesianChartModel,
    insets: Insets,
  ): Unit =
    with(context) {
      val maxLabelHeight = getMaxLabelHeight()
      val maxLineThickness = max(lineThickness, tickThickness)
      insets.ensureValuesAtLeast(
        top =
          itemPlacer.getTopVerticalAxisInset(
            context,
            verticalLabelPosition,
            maxLabelHeight,
            maxLineThickness,
          ),
        bottom =
          itemPlacer.getBottomVerticalAxisInset(
            context,
            verticalLabelPosition,
            maxLabelHeight,
            maxLineThickness,
          ),
      )
    }

  protected open fun getWidth(context: CartesianMeasureContext, freeHeight: Float): Float =
    with(context) {
      when (val constraint = sizeConstraint) {
        is SizeConstraint.Auto -> {
          val titleComponentWidth =
            title
              ?.let { title ->
                titleComponent?.getWidth(
                  context = this,
                  text = title,
                  rotationDegrees = TITLE_ABS_ROTATION_DEGREES,
                  maxHeight = bounds.height().toInt(),
                )
              }
              .orZero
          val labelSpace =
            when (horizontalLabelPosition) {
              Outside -> ceil(getMaxLabelWidth(freeHeight)).also { maxLabelWidth = it } + tickLength
              Inside -> 0f
            }
          (labelSpace + titleComponentWidth + lineThickness).coerceIn(
            minimumValue = constraint.minSizeDp.pixels,
            maximumValue = constraint.maxSizeDp.pixels,
          )
        }
        is SizeConstraint.Exact -> constraint.sizeDp.pixels
        is SizeConstraint.Fraction -> canvasBounds.width() * constraint.fraction
        is SizeConstraint.TextWidth ->
          label
            ?.getWidth(
              context = this,
              text = constraint.text,
              rotationDegrees = labelRotationDegrees,
            )
            .orZero + tickLength + lineThickness.half
      }
    }

  protected fun CartesianMeasureContext.getMaxLabelHeight(): Float =
    label
      ?.let { label ->
        itemPlacer.getHeightMeasurementLabelValues(this, position).maxOfOrNull { value ->
          label.getHeight(
            context = this,
            text = valueFormatter.format(value, chartValues, position),
            rotationDegrees = labelRotationDegrees,
          )
        }
      }
      .orZero

  protected fun CartesianMeasureContext.getMaxLabelWidth(axisHeight: Float): Float =
    label
      ?.let { label ->
        itemPlacer
          .getWidthMeasurementLabelValues(this, axisHeight, getMaxLabelHeight(), position)
          .maxOfOrNull { value ->
            label.getWidth(
              context = this,
              text = valueFormatter.format(value, chartValues, position),
              rotationDegrees = labelRotationDegrees,
            )
          }
      }
      .orZero

  protected fun CartesianDrawContext.getLineCanvasYCorrection(thickness: Float, y: Double): Float =
    if (y == chartValues.getYRange(position).maxY && itemPlacer.getShiftTopLines(this)) {
      -thickness.half
    } else {
      thickness.half
    }

  /**
   * Defines the horizontal position of each of a vertical axis’s labels relative to the axis line.
   */
  public enum class HorizontalLabelPosition {
    Outside,
    Inside,
  }

  /**
   * Defines the vertical position of each of a horizontal axis’s labels relative to the label’s
   * corresponding tick.
   *
   * @param textPosition the label position.
   * @see VerticalPosition
   */
  public enum class VerticalLabelPosition(public val textPosition: VerticalPosition) {
    Center(VerticalPosition.Center),
    Top(VerticalPosition.Top),
    Bottom(VerticalPosition.Bottom),
  }

  /** Determines for what _y_ values a [VerticalAxis] displays labels, ticks, and guidelines. */
  public interface ItemPlacer {
    /**
     * Returns a boolean indicating whether to shift the lines whose _y_ values are equal to
     * [ChartValues.YRange.maxY], if such lines are present, such that they’re immediately above the
     * [CartesianLayer] bounds. If the [CartesianChart] has a top axis, the shifted tick is then
     * aligned with it, and the shifted guideline is hidden.
     */
    public fun getShiftTopLines(context: CartesianDrawContext): Boolean = true

    /** Returns, as a list, the _y_ values for which labels are to be displayed. */
    public fun getLabelValues(
      context: CartesianDrawContext,
      axisHeight: Float,
      maxLabelHeight: Float,
      position: AxisPosition.Vertical,
    ): List<Double>

    /**
     * Returns, as a list, the _y_ values for which the [VerticalAxis] is to create labels and
     * measure their widths during the measuring phase. This affects how much horizontal space the
     * [VerticalAxis] requests.
     */
    public fun getWidthMeasurementLabelValues(
      context: CartesianMeasureContext,
      axisHeight: Float,
      maxLabelHeight: Float,
      position: AxisPosition.Vertical,
    ): List<Double>

    /**
     * Returns, as a list, the _y_ values for which the [VerticalAxis] is to create labels and
     * measure their heights during the measuring phase. The height of the tallest label is passed
     * to other functions.
     */
    public fun getHeightMeasurementLabelValues(
      context: CartesianMeasureContext,
      position: AxisPosition.Vertical,
    ): List<Double>

    /** Returns, as a list, the _y_ values for which ticks and guidelines are to be displayed. */
    public fun getLineValues(
      context: CartesianDrawContext,
      axisHeight: Float,
      maxLabelHeight: Float,
      position: AxisPosition.Vertical,
    ): List<Double>? = null

    /** Returns the top inset required by the [VerticalAxis]. */
    public fun getTopVerticalAxisInset(
      context: CartesianMeasureContext,
      verticalLabelPosition: VerticalLabelPosition,
      maxLabelHeight: Float,
      maxLineThickness: Float,
    ): Float

    /** Returns the bottom inset required by the [VerticalAxis]. */
    public fun getBottomVerticalAxisInset(
      context: CartesianMeasureContext,
      verticalLabelPosition: VerticalLabelPosition,
      maxLabelHeight: Float,
      maxLineThickness: Float,
    ): Float

    /** Houses [ItemPlacer] factory functions. */
    public companion object {
      /**
       * Creates a step-based [ItemPlacer] implementation. [step] returns the difference between the
       * _y_ values of neighboring labels (and their corresponding line pairs). A multiple of this
       * may be used for overlap prevention. If `null` is returned, the step will be determined
       * automatically. [shiftTopLines] defines whether to shift the lines whose _y_ values are
       * equal to [ChartValues.YRange.maxY], if such lines are present, such that they’re
       * immediately above the [CartesianChart]’s bounds. If the chart has a top axis, the shifted
       * tick will then be aligned with this axis, and the shifted guideline will be hidden.
       */
      public fun step(
        step: (ExtraStore) -> Double? = { null },
        shiftTopLines: Boolean = true,
      ): ItemPlacer =
        DefaultVerticalAxisItemPlacer(DefaultVerticalAxisItemPlacer.Mode.Step(step), shiftTopLines)

      /**
       * Creates a count-based [ItemPlacer] implementation. [count] returns the number of labels
       * (and their corresponding line pairs) to be displayed. This may be reduced for overlap
       * prevention. If `null` is returned, the [VerticalAxis] will display as many items as
       * possible. [shiftTopLines] defines whether to shift the lines whose _y_ values are equal to
       * [ChartValues.YRange.maxY], if such lines are present, such that they’re immediately above
       * the [CartesianChart]’s bounds. If the chart has a top axis, the shifted tick will then be
       * aligned with this axis, and the shifted guideline will be hidden.
       */
      public fun count(
        count: (ExtraStore) -> Int? = { null },
        shiftTopLines: Boolean = true,
      ): ItemPlacer =
        DefaultVerticalAxisItemPlacer(
          DefaultVerticalAxisItemPlacer.Mode.Count(count),
          shiftTopLines,
        )
    }
  }

  /** Houses [VerticalAxis] factory functions. */
  public companion object {
    /** Creates a start [VerticalAxis]. */
    public fun start(
      line: LineComponent? = null,
      label: TextComponent? = null,
      labelRotationDegrees: Float = 0f,
      horizontalLabelPosition: HorizontalLabelPosition = Outside,
      verticalLabelPosition: VerticalLabelPosition = Center,
      valueFormatter: CartesianValueFormatter = CartesianValueFormatter.decimal(),
      tick: LineComponent? = null,
      tickLengthDp: Float = 0f,
      guideline: LineComponent? = null,
      itemPlacer: ItemPlacer = ItemPlacer.step(),
      sizeConstraint: SizeConstraint = SizeConstraint.Auto(),
      titleComponent: TextComponent? = null,
      title: CharSequence? = null,
    ): VerticalAxis<AxisPosition.Vertical.Start> =
      VerticalAxis(
        AxisPosition.Vertical.Start,
        line,
        label,
        labelRotationDegrees,
        horizontalLabelPosition,
        verticalLabelPosition,
        valueFormatter,
        tick,
        tickLengthDp,
        guideline,
        itemPlacer,
        sizeConstraint,
        titleComponent,
        title,
      )

    /** Creates an end [VerticalAxis]. */
    public fun end(
      line: LineComponent? = null,
      label: TextComponent? = null,
      labelRotationDegrees: Float = 0f,
      horizontalLabelPosition: HorizontalLabelPosition = Outside,
      verticalLabelPosition: VerticalLabelPosition = Center,
      valueFormatter: CartesianValueFormatter = CartesianValueFormatter.decimal(),
      tick: LineComponent? = null,
      tickLengthDp: Float = 0f,
      guideline: LineComponent? = null,
      itemPlacer: ItemPlacer = ItemPlacer.step(),
      sizeConstraint: SizeConstraint = SizeConstraint.Auto(),
      titleComponent: TextComponent? = null,
      title: CharSequence? = null,
    ): VerticalAxis<AxisPosition.Vertical.End> =
      VerticalAxis(
        AxisPosition.Vertical.End,
        line,
        label,
        labelRotationDegrees,
        horizontalLabelPosition,
        verticalLabelPosition,
        valueFormatter,
        tick,
        tickLengthDp,
        guideline,
        itemPlacer,
        sizeConstraint,
        titleComponent,
        title,
      )
  }
}
