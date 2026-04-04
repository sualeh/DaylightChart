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
import java.awt.Font;
import java.awt.Paint;
import java.io.Serial;
import java.io.Serializable;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

/** Serializable title options. */
public record TitleOptions(
    boolean showTitle,
    @JsonSerialize(using = FontValueCodec.Serializer.class)
        @JsonDeserialize(using = FontValueCodec.Deserializer.class)
        Font titleFont,
    @JsonSerialize(using = PaintValueCodec.Serializer.class)
        @JsonDeserialize(using = PaintValueCodec.Deserializer.class)
        Paint titlePaint,
    String titleText)
    implements Serializable {

  @Serial private static final long serialVersionUID = -6096894681186027546L;

  public TitleOptions() {
    this(true, null, null, null);
  }

  public boolean getShowTitle() {
    return showTitle;
  }

  public Font getTitleFont() {
    return titleFont;
  }

  public Paint getTitlePaint() {
    return titlePaint;
  }

  public String getTitleText() {
    return titleText;
  }
}
