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

package com.patrykandpatrick.vico.core.cartesian.layer

import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import com.patrykandpatrick.vico.core.cartesian.CartesianDrawContext
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasureContext
import com.patrykandpatrick.vico.core.cartesian.CubicPointConnector
import com.patrykandpatrick.vico.core.cartesian.HorizontalDimensions
import com.patrykandpatrick.vico.core.cartesian.HorizontalLayout
import com.patrykandpatrick.vico.core.cartesian.Insets
import com.patrykandpatrick.vico.core.cartesian.MutableHorizontalDimensions
import com.patrykandpatrick.vico.core.cartesian.axis.AxisPosition
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.ChartValues
import com.patrykandpatrick.vico.core.cartesian.data.LineCartesianLayerDrawingModel
import com.patrykandpatrick.vico.core.cartesian.data.LineCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.data.MutableChartValues
import com.patrykandpatrick.vico.core.cartesian.data.forEachIn
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer.Line
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.LineCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.core.cartesian.marker.MutableLineCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.core.common.Defaults
import com.patrykandpatrick.vico.core.common.DrawContext
import com.patrykandpatrick.vico.core.common.Point
import com.patrykandpatrick.vico.core.common.VerticalPosition
import com.patrykandpatrick.vico.core.common.component.Component
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.data.DefaultDrawingModelInterpolator
import com.patrykandpatrick.vico.core.common.data.DrawingModelInterpolator
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.data.MutableExtraStore
import com.patrykandpatrick.vico.core.common.doubled
import com.patrykandpatrick.vico.core.common.getEnd
import com.patrykandpatrick.vico.core.common.getRepeating
import com.patrykandpatrick.vico.core.common.getStart
import com.patrykandpatrick.vico.core.common.half
import com.patrykandpatrick.vico.core.common.inBounds
import com.patrykandpatrick.vico.core.common.orZero
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.patrykandpatrick.vico.core.common.shader.TopBottomShader
import com.patrykandpatrick.vico.core.common.withOpacity
import kotlin.math.max
import kotlin.math.min

/**
 * [LineCartesianLayer] displays data as a continuous line.
 *
 * @param lineProvider provides the [Line]s.
 * @param pointSpacingDp the point spacing (in dp).
 * @param verticalAxisPosition the position of the [VerticalAxis] with which the
 *   [LineCartesianLayer] should be associated. Use this for independent [CartesianLayer] scaling.
 * @param drawingModelInterpolator interpolates the [LineCartesianLayer]’s
 *   [LineCartesianLayerDrawingModel]s.
 */
public open class LineCartesianLayer(
  public var lineProvider: LineProvider,
  public var pointSpacingDp: Float = Defaults.POINT_SPACING,
  public var verticalAxisPosition: AxisPosition.Vertical? = null,
  public var drawingModelInterpolator:
    DrawingModelInterpolator<
      LineCartesianLayerDrawingModel.PointInfo,
      LineCartesianLayerDrawingModel,
    > =
    DefaultDrawingModelInterpolator(),
) : BaseCartesianLayer<LineCartesianLayerModel>() {
  /**
   * Defines the appearance of a line in a line chart.
   *
   * @param shader the [DynamicShader] for the line.
   * @param thicknessDp the thickness of the line (in dp).
   * @param backgroundShader an optional [DynamicShader] to use for the areas bounded by the
   *   [LineCartesianLayer] line and the zero line (_y_ = 0).
   * @param cap the stroke cap for the line.
   * @param pointProvider provides the [Point]s.
   * @param dataLabel an optional [TextComponent] to use for data labels.
   * @param dataLabelVerticalPosition the vertical position of data labels relative to the line.
   * @param dataLabelValueFormatter the [CartesianValueFormatter] to use for data labels.
   * @param dataLabelRotationDegrees the rotation of data labels (in degrees).
   * @param pointConnector the [PointConnector] for the line.
   */
  public open class Line(
    public var shader: DynamicShader,
    public var thicknessDp: Float = Defaults.LINE_SPEC_THICKNESS_DP,
    public var backgroundShader: DynamicShader? = null,
    cap: Paint.Cap = Paint.Cap.ROUND,
    public var pointProvider: PointProvider? = null,
    public var pointConnector: PointConnector = PointConnector.cubic(),
    public var dataLabel: TextComponent? = null,
    public var dataLabelVerticalPosition: VerticalPosition = VerticalPosition.Top,
    public var dataLabelValueFormatter: CartesianValueFormatter = CartesianValueFormatter.decimal(),
    public var dataLabelRotationDegrees: Float = 0f,
  ) {
    /** Returns `true` if the [backgroundShader] is not null, and `false` otherwise. */
    public val hasBackgroundShader: Boolean
      get() = backgroundShader != null

    protected val linePaint: Paint =
      Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = cap
      }

    protected val lineBackgroundPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    protected val lineBackgroundPath: Path = Path()

    protected val clipPath: Path = Path()

    protected val pathBounds: RectF = RectF()

    /** The stroke cap for the line. */
    public var cap: Paint.Cap by linePaint::strokeCap

    /** Draws the line. */
    public fun drawLine(
      context: DrawContext,
      bounds: RectF,
      zeroLineYFraction: Float,
      path: Path,
      opacity: Float = 1f,
    ) {
      with(context) {
        linePaint.strokeWidth = thicknessDp.pixels
        setSplitY(zeroLineYFraction)
        linePaint.shader = shader.provideShader(context, bounds)
        linePaint.withOpacity(opacity) { canvas.drawPath(path, it) }
      }
    }

    /** Draws the line background. */
    public open fun drawBackground(
      context: DrawContext,
      bounds: RectF,
      zeroLineYFraction: Float,
      path: Path,
      opacity: Float = 1f,
    ) {
      val fill = backgroundShader ?: return
      with(lineBackgroundPaint) {
        if (zeroLineYFraction > 0) {
          val zeroLineY = bounds.top + (zeroLineYFraction * bounds.height())
          setSplitY(1f)
          shader = fill.provideShader(context, bounds.left, bounds.top, bounds.right, zeroLineY)
          lineBackgroundPath.set(path)
          lineBackgroundPath.computeBounds(pathBounds, false)
          lineBackgroundPath.lineTo(pathBounds.getEnd(context.isLtr), bounds.bottom)
          lineBackgroundPath.lineTo(pathBounds.getStart(context.isLtr), bounds.bottom)
          lineBackgroundPath.close()
          clipPath.rewind()
          clipPath.addRect(bounds.left, bounds.top, bounds.right, zeroLineY, Path.Direction.CW)
          lineBackgroundPath.op(clipPath, Path.Op.INTERSECT)
          withOpacity(opacity) { context.canvas.drawPath(lineBackgroundPath, it) }
        }

        if (zeroLineYFraction < 1f) {
          val zeroLineY = bounds.top + (zeroLineYFraction * bounds.height())
          setSplitY(0f)
          shader = fill.provideShader(context, bounds.left, zeroLineY, bounds.right, bounds.bottom)
          lineBackgroundPath.set(path)
          lineBackgroundPath.computeBounds(pathBounds, false)
          lineBackgroundPath.lineTo(pathBounds.getEnd(context.isLtr), bounds.top)
          lineBackgroundPath.lineTo(pathBounds.getStart(context.isLtr), bounds.top)
          lineBackgroundPath.close()
          clipPath.rewind()
          clipPath.addRect(bounds.left, zeroLineY, bounds.right, bounds.bottom, Path.Direction.CW)
          lineBackgroundPath.op(clipPath, Path.Op.INTERSECT)
          withOpacity(opacity) { context.canvas.drawPath(lineBackgroundPath, it) }
        }
      }
    }

    /**
     * For [shader] and [backgroundShader], if the [DynamicShader] is a [TopBottomShader], updates
     * [TopBottomShader.splitY] to match the position of the zero line (_y_ = 0).
     */
    public fun setSplitY(splitY: Float) {
      (shader as? TopBottomShader)?.splitY = splitY
      (backgroundShader as? TopBottomShader)?.splitY = splitY
    }
  }

  /** Connects a [LineCartesianLayer] line’s points, thus defining its shape. */
  public fun interface PointConnector {
    /** Connects ([x1], [y2]) and ([x2], [y2]). */
    public fun connect(
      context: CartesianDrawContext,
      path: Path,
      x1: Float,
      y1: Float,
      x2: Float,
      y2: Float,
    )

    /** Houses a [PointConnector] factory function. */
    public companion object {
      /**
       * Uses cubic Bézier curves. [curvature], which must be in the interval [[0, 1]], defines
       * their strength.
       */
      public fun cubic(curvature: Float = Defaults.LINE_CURVATURE): PointConnector =
        CubicPointConnector(curvature)
    }
  }

  /** Provides [Line]s to [LineCartesianLayer]s. */
  public fun interface LineProvider {
    /** Returns the [Line] for the specified series. */
    public fun getLine(seriesIndex: Int, extraStore: ExtraStore): Line

    /** Houses [LineProvider] factory functions. */
    public companion object {
      private data class Series(private val lines: List<Line>) : LineProvider {
        override fun getLine(seriesIndex: Int, extraStore: ExtraStore) =
          lines.getRepeating(seriesIndex)
      }

      /**
       * Uses the provided [Line]s. The [Line]s and series are associated by index. If there are
       * more series than [Line]s, [lines] is iterated multiple times.
       */
      public fun series(lines: List<Line>): LineProvider = Series(lines)

      /**
       * Uses the provided [Line]s. The [Line]s and series are associated by index. If there are
       * more series than [Line]s, the [Line] list is iterated multiple times.
       */
      public fun series(vararg lines: Line): LineProvider = series(lines.toList())
    }
  }

  /**
   * Defines a point style.
   *
   * @param component the point [Component].
   * @property sizeDp the point size (in dp).
   */
  public data class Point(
    private val component: Component,
    public val sizeDp: Float = Defaults.POINT_SIZE,
  ) {
    /** Draws a point at ([x], [y]). */
    public fun draw(context: CartesianDrawContext, x: Float, y: Float) {
      val halfSize = context.run { sizeDp.half.pixels }
      component.draw(
        context = context,
        left = x - halfSize,
        top = y - halfSize,
        right = x + halfSize,
        bottom = y + halfSize,
      )
    }
  }

  /** Provides [Point]s to [LineCartesianLayer]s. */
  public interface PointProvider {
    /** Returns the [Point] for the point with the given properties. */
    public fun getPoint(
      entry: LineCartesianLayerModel.Entry,
      seriesIndex: Int,
      extraStore: ExtraStore,
    ): Point?

    /** Returns the largest [Point]. */
    public fun getLargestPoint(extraStore: ExtraStore): Point?

    /** Houses a [PointProvider] factory function. */
    public companion object {
      private data class Single(private val point: Point) : PointProvider {
        override fun getPoint(
          entry: LineCartesianLayerModel.Entry,
          seriesIndex: Int,
          extraStore: ExtraStore,
        ) = point

        override fun getLargestPoint(extraStore: ExtraStore) = point
      }

      /** Uses [point] for each point. */
      public fun single(point: Point): PointProvider = Single(point)
    }
  }

  private val _markerTargets = mutableMapOf<Double, List<MutableLineCartesianLayerMarkerTarget>>()

  protected val linePath: Path = Path()

  protected val lineBackgroundPath: Path = Path()

  protected val drawingModelKey: ExtraStore.Key<LineCartesianLayerDrawingModel> = ExtraStore.Key()

  override val markerTargets: Map<Double, List<CartesianMarker.Target>> = _markerTargets

  override fun drawInternal(context: CartesianDrawContext, model: LineCartesianLayerModel): Unit =
    with(context) {
      resetTempData()

      val drawingModel = model.extraStore.getOrNull(drawingModelKey)
      val yRange = chartValues.getYRange(verticalAxisPosition)
      val zeroLineYFraction =
        drawingModel?.zeroY ?: (yRange.maxY / yRange.length).coerceIn(0.0..1.0).toFloat()

      model.series.forEachIndexed { seriesIndex, series ->
        val pointInfoMap = drawingModel?.getOrNull(seriesIndex)

        linePath.rewind()
        lineBackgroundPath.rewind()
        val line =
          lineProvider.getLine(seriesIndex, chartValues.model.extraStore).apply {
            setSplitY(zeroLineYFraction)
          }

        var prevX = layerBounds.getStart(isLtr = isLtr)
        var prevY = layerBounds.bottom

        val drawingStartAlignmentCorrection =
          layoutDirectionMultiplier * horizontalDimensions.startPadding

        val drawingStart =
          layerBounds.getStart(isLtr = isLtr) + drawingStartAlignmentCorrection - scroll

        forEachPointInBounds(series, drawingStart, pointInfoMap) { entry, x, y, _, _ ->
          if (linePath.isEmpty) {
            linePath.moveTo(x, y)
          } else {
            line.pointConnector.connect(this, linePath, prevX, prevY, x, y)
          }
          prevX = x
          prevY = y

          updateMarkerTargets(entry, x, y, line)
        }

        if (line.hasBackgroundShader) {
          lineBackgroundPath.addPath(linePath)
          lineBackgroundPath.lineTo(prevX, layerBounds.bottom)
          line.drawBackground(
            context,
            layerBounds,
            zeroLineYFraction,
            lineBackgroundPath,
            drawingModel?.opacity ?: 1f,
          )
        }

        line.drawLine(
          context,
          layerBounds,
          zeroLineYFraction,
          linePath,
          drawingModel?.opacity ?: 1f,
        )

        drawPointsAndDataLabels(line, series, seriesIndex, drawingStart, pointInfoMap)
      }
    }

  protected open fun CartesianDrawContext.updateMarkerTargets(
    entry: LineCartesianLayerModel.Entry,
    canvasX: Float,
    canvasY: Float,
    line: Line,
  ) {
    if (canvasX <= layerBounds.left - 1 || canvasX >= layerBounds.right + 1) return
    val limitedCanvasY = canvasY.coerceIn(layerBounds.top, layerBounds.bottom)
    _markerTargets
      .getOrPut(entry.x) { listOf(MutableLineCartesianLayerMarkerTarget(entry.x, canvasX)) }
      .first()
      .points +=
      LineCartesianLayerMarkerTarget.Point(
        entry,
        limitedCanvasY,
        line.shader.getColorAt(Point(canvasX, limitedCanvasY), this, layerBounds),
      )
  }

  protected open fun CartesianDrawContext.drawPointsAndDataLabels(
    line: Line,
    series: List<LineCartesianLayerModel.Entry>,
    seriesIndex: Int,
    drawingStart: Float,
    pointInfoMap: Map<Double, LineCartesianLayerDrawingModel.PointInfo>?,
  ) {
    forEachPointInBounds(
      series = series,
      drawingStart = drawingStart,
      pointInfoMap = pointInfoMap,
    ) { chartEntry, x, y, previousX, nextX ->
      val point =
        line.pointProvider?.getPoint(chartEntry, seriesIndex, chartValues.model.extraStore)
      point?.draw(this, x, y)

      line.dataLabel
        .takeIf {
          horizontalLayout is HorizontalLayout.Segmented ||
            chartEntry.x != chartValues.minX && chartEntry.x != chartValues.maxX ||
            chartEntry.x == chartValues.minX && horizontalDimensions.startPadding > 0 ||
            chartEntry.x == chartValues.maxX && horizontalDimensions.endPadding > 0
        }
        ?.let { textComponent ->
          val distanceFromLine = max(line.thicknessDp, point?.sizeDp.orZero).half.pixels

          val text =
            line.dataLabelValueFormatter.format(
              value = chartEntry.y,
              chartValues = chartValues,
              verticalAxisPosition = verticalAxisPosition,
            )
          val maxWidth = getMaxDataLabelWidth(chartEntry, x, previousX, nextX)
          val verticalPosition =
            line.dataLabelVerticalPosition.inBounds(
              bounds = layerBounds,
              distanceFromPoint = distanceFromLine,
              componentHeight =
                textComponent.getHeight(
                  context = this,
                  text = text,
                  maxWidth = maxWidth,
                  rotationDegrees = line.dataLabelRotationDegrees,
                ),
              y = y,
            )
          val dataLabelY =
            y +
              when (verticalPosition) {
                VerticalPosition.Top -> -distanceFromLine
                VerticalPosition.Center -> 0f
                VerticalPosition.Bottom -> distanceFromLine
              }
          textComponent.draw(
            context = this,
            x = x,
            y = dataLabelY,
            text = text,
            verticalPosition = verticalPosition,
            maxWidth = maxWidth,
            rotationDegrees = line.dataLabelRotationDegrees,
          )
        }
    }
  }

  protected fun CartesianDrawContext.getMaxDataLabelWidth(
    entry: LineCartesianLayerModel.Entry,
    x: Float,
    previousX: Float?,
    nextX: Float?,
  ): Int =
    when {
      previousX != null && nextX != null -> min(x - previousX, nextX - x)
      previousX == null && nextX == null ->
        min(horizontalDimensions.startPadding, horizontalDimensions.endPadding).doubled
      nextX != null -> {
        val extraSpace =
          when (horizontalLayout) {
            is HorizontalLayout.Segmented -> horizontalDimensions.xSpacing.half
            is HorizontalLayout.FullWidth -> horizontalDimensions.startPadding
          }
        ((entry.x - chartValues.minX) / chartValues.xStep * horizontalDimensions.xSpacing +
            extraSpace)
          .doubled
          .toFloat()
          .coerceAtMost(nextX - x)
      }
      else -> {
        val extraSpace =
          when (horizontalLayout) {
            is HorizontalLayout.Segmented -> horizontalDimensions.xSpacing.half
            is HorizontalLayout.FullWidth -> horizontalDimensions.endPadding
          }
        ((chartValues.maxX - entry.x) / chartValues.xStep * horizontalDimensions.xSpacing +
            extraSpace)
          .doubled
          .toFloat()
          .coerceAtMost(x - previousX!!)
      }
    }.toInt()

  protected fun resetTempData() {
    _markerTargets.clear()
    linePath.rewind()
    lineBackgroundPath.rewind()
  }

  protected open fun CartesianDrawContext.forEachPointInBounds(
    series: List<LineCartesianLayerModel.Entry>,
    drawingStart: Float,
    pointInfoMap: Map<Double, LineCartesianLayerDrawingModel.PointInfo>?,
    action:
      (
        entry: LineCartesianLayerModel.Entry, x: Float, y: Float, previousX: Float?, nextX: Float?,
      ) -> Unit,
  ) {
    val minX = chartValues.minX
    val maxX = chartValues.maxX
    val xStep = chartValues.xStep

    var x: Float? = null
    var nextX: Float? = null

    val boundsStart = layerBounds.getStart(isLtr = isLtr)
    val boundsEnd = boundsStart + layoutDirectionMultiplier * layerBounds.width()

    fun getDrawX(entry: LineCartesianLayerModel.Entry): Float =
      drawingStart +
        layoutDirectionMultiplier *
          horizontalDimensions.xSpacing *
          ((entry.x - minX) / xStep).toFloat()

    fun getDrawY(entry: LineCartesianLayerModel.Entry): Float {
      val yRange = chartValues.getYRange(verticalAxisPosition)
      return layerBounds.bottom -
        (pointInfoMap?.get(entry.x)?.y ?: ((entry.y - yRange.minY) / yRange.length).toFloat()) *
          layerBounds.height()
    }

    series.forEachIn(range = minX..maxX, padding = 1) { entry, next ->
      val previousX = x
      val immutableX = nextX ?: getDrawX(entry)
      val immutableNextX = next?.let(::getDrawX)
      x = immutableX
      nextX = immutableNextX
      if (
        immutableNextX != null &&
          (isLtr && immutableX < boundsStart || !isLtr && immutableX > boundsStart) &&
          (isLtr && immutableNextX < boundsStart || !isLtr && immutableNextX > boundsStart)
      ) {
        return@forEachIn
      }
      action(entry, immutableX, getDrawY(entry), previousX, nextX)
      if (isLtr && immutableX > boundsEnd || isLtr.not() && immutableX < boundsEnd) return
    }
  }

  override fun updateHorizontalDimensions(
    context: CartesianMeasureContext,
    horizontalDimensions: MutableHorizontalDimensions,
    model: LineCartesianLayerModel,
  ) {
    with(context) {
      val maxPointSize =
        (0..<model.series.size)
          .maxOf {
            lineProvider
              .getLine(it, model.extraStore)
              .pointProvider
              ?.getLargestPoint(model.extraStore)
              ?.sizeDp
              .orZero
          }
          .pixels
      val xSpacing = maxPointSize + pointSpacingDp.pixels
      when (val horizontalLayout = horizontalLayout) {
        is HorizontalLayout.Segmented ->
          horizontalDimensions.ensureSegmentedValues(xSpacing, chartValues)
        is HorizontalLayout.FullWidth -> {
          horizontalDimensions.ensureValuesAtLeast(
            xSpacing = xSpacing,
            scalableStartPadding = horizontalLayout.scalableStartPaddingDp.pixels,
            scalableEndPadding = horizontalLayout.scalableEndPaddingDp.pixels,
            unscalableStartPadding =
              maxPointSize.half + horizontalLayout.unscalableStartPaddingDp.pixels,
            unscalableEndPadding =
              maxPointSize.half + horizontalLayout.unscalableEndPaddingDp.pixels,
          )
        }
      }
    }
  }

  override fun updateChartValues(chartValues: MutableChartValues, model: LineCartesianLayerModel) {
    chartValues.tryUpdate(
      axisValueOverrider.getMinX(model.minX, model.maxX, model.extraStore),
      axisValueOverrider.getMaxX(model.minX, model.maxX, model.extraStore),
      axisValueOverrider.getMinY(model.minY, model.maxY, model.extraStore),
      axisValueOverrider.getMaxY(model.minY, model.maxY, model.extraStore),
      verticalAxisPosition,
    )
  }

  override fun updateInsets(
    context: CartesianMeasureContext,
    horizontalDimensions: HorizontalDimensions,
    model: LineCartesianLayerModel,
    insets: Insets,
  ) {
    with(context) {
      val verticalInset =
        (0..<model.series.size)
          .mapNotNull { lineProvider.getLine(it, model.extraStore) }
          .maxOf {
            max(it.thicknessDp, it.pointProvider?.getLargestPoint(model.extraStore)?.sizeDp.orZero)
          }
          .half
          .pixels
      insets.ensureValuesAtLeast(top = verticalInset, bottom = verticalInset)
    }
  }

  override fun prepareForTransformation(
    model: LineCartesianLayerModel?,
    extraStore: MutableExtraStore,
    chartValues: ChartValues,
  ) {
    drawingModelInterpolator.setModels(
      old = extraStore.getOrNull(drawingModelKey),
      new = model?.toDrawingModel(chartValues),
    )
  }

  override suspend fun transform(extraStore: MutableExtraStore, fraction: Float) {
    drawingModelInterpolator.transform(fraction)?.let { extraStore[drawingModelKey] = it }
      ?: extraStore.remove(drawingModelKey)
  }

  private fun LineCartesianLayerModel.toDrawingModel(
    chartValues: ChartValues
  ): LineCartesianLayerDrawingModel {
    val yRange = chartValues.getYRange(verticalAxisPosition)
    return series
      .map { series ->
        series.associate { entry ->
          entry.x to
            LineCartesianLayerDrawingModel.PointInfo(
              ((entry.y - yRange.minY) / yRange.length).toFloat()
            )
        }
      }
      .let { pointInfo ->
        LineCartesianLayerDrawingModel(
          pointInfo,
          (yRange.maxY / yRange.length).coerceIn(0.0..1.0).toFloat(),
        )
      }
  }
}
