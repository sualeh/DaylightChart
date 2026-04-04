/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.chart.options;

import daylightchart.chart.options.serialization.PaintValueCodec;
import daylightchart.chart.options.serialization.RectangleInsetsValueCodec;
import daylightchart.chart.options.serialization.StrokeValueCodec;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.Serial;
import java.io.Serializable;
import org.jfree.chart.ui.RectangleInsets;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

/** Serializable plot options. */
public record PlotOptions(
    @JsonSerialize(using = PaintValueCodec.Serializer.class)
        @JsonDeserialize(using = PaintValueCodec.Deserializer.class)
        Paint backgroundPaint,
    @JsonSerialize(using = PaintValueCodec.Serializer.class)
        @JsonDeserialize(using = PaintValueCodec.Deserializer.class)
        Paint outlinePaint,
    @JsonSerialize(using = StrokeValueCodec.Serializer.class)
        @JsonDeserialize(using = StrokeValueCodec.Deserializer.class)
        Stroke outlineStroke,
    @JsonSerialize(using = RectangleInsetsValueCodec.Serializer.class)
        @JsonDeserialize(using = RectangleInsetsValueCodec.Deserializer.class)
        RectangleInsets insets,
    AxisOptions domainAxisOptions,
    AxisOptions rangeAxisOptions)
    implements Serializable {

  @Serial private static final long serialVersionUID = 6801804849033048049L;

  public PlotOptions() {
    this(null, null, null, null, new AxisOptions(), new AxisOptions());
  }

  public Paint getBackgroundPaint() {
    return backgroundPaint;
  }

  public AxisOptions getDomainAxisOptions() {
    return domainAxisOptions;
  }

  public RectangleInsets getInsets() {
    return insets;
  }

  public Paint getOutlinePaint() {
    return outlinePaint;
  }

  public Stroke getOutlineStroke() {
    return outlineStroke;
  }

  public AxisOptions getRangeAxisOptions() {
    return rangeAxisOptions;
  }
}
