/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.chart.options;

import daylightchart.chart.options.serialization.PaintValueCodec;
import java.awt.Paint;
import java.io.Serial;
import java.io.Serializable;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

/** Serializable chart options. */
public record ChartOptions(
    boolean antiAlias,
    @JsonSerialize(using = PaintValueCodec.Serializer.class)
        @JsonDeserialize(using = PaintValueCodec.Deserializer.class)
        Paint backgroundPaint,
    PlotOptions plotOptions,
    TitleOptions titleOptions)
    implements Serializable {

  @Serial private static final long serialVersionUID = -7527051325325384357L;

  public ChartOptions() {
    this(false, null, new PlotOptions(), new TitleOptions());
  }

  public Paint getBackgroundPaint() {
    return backgroundPaint;
  }

  public PlotOptions getPlotOptions() {
    return plotOptions;
  }

  public TitleOptions getTitleOptions() {
    return titleOptions;
  }

  public boolean isAntiAlias() {
    return antiAlias;
  }
}
