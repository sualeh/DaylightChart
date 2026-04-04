/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.chart.options;

import daylightchart.chart.options.serialization.FontValueCodec;
import daylightchart.chart.options.serialization.PaintValueCodec;
import daylightchart.chart.options.serialization.RectangleInsetsValueCodec;
import java.awt.Font;
import java.awt.Paint;
import java.io.Serial;
import java.io.Serializable;
import org.jfree.chart.ui.RectangleInsets;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

/** Serializable axis chart options. */
public record AxisOptions(
    String label,
    @JsonSerialize(using = FontValueCodec.Serializer.class)
        @JsonDeserialize(using = FontValueCodec.Deserializer.class)
        Font labelFont,
    @JsonSerialize(using = RectangleInsetsValueCodec.Serializer.class)
        @JsonDeserialize(using = RectangleInsetsValueCodec.Deserializer.class)
        RectangleInsets labelInsets,
    @JsonSerialize(using = PaintValueCodec.Serializer.class)
        @JsonDeserialize(using = PaintValueCodec.Deserializer.class)
        Paint labelPaint,
    @JsonSerialize(using = FontValueCodec.Serializer.class)
        @JsonDeserialize(using = FontValueCodec.Deserializer.class)
        Font tickLabelFont,
    @JsonSerialize(using = RectangleInsetsValueCodec.Serializer.class)
        @JsonDeserialize(using = RectangleInsetsValueCodec.Deserializer.class)
        RectangleInsets tickLabelInsets,
    @JsonSerialize(using = PaintValueCodec.Serializer.class)
        @JsonDeserialize(using = PaintValueCodec.Deserializer.class)
        Paint tickLabelPaint,
    boolean tickLabelsVisible,
    boolean tickMarksVisible)
    implements Serializable {

  @Serial private static final long serialVersionUID = 7658976939630828679L;

  public AxisOptions() {
    this(null, null, null, null, null, null, null, false, false);
  }

  public String getLabel() {
    return label;
  }

  public Font getLabelFont() {
    return labelFont;
  }

  public RectangleInsets getLabelInsets() {
    return labelInsets;
  }

  public Paint getLabelPaint() {
    return labelPaint;
  }

  public Font getTickLabelFont() {
    return tickLabelFont;
  }

  public RectangleInsets getTickLabelInsets() {
    return tickLabelInsets;
  }

  public Paint getTickLabelPaint() {
    return tickLabelPaint;
  }

  public boolean isTickLabelsVisible() {
    return tickLabelsVisible;
  }

  public boolean isTickMarksVisible() {
    return tickMarksVisible;
  }
}
