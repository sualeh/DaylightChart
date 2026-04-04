/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.chart.options.serialization;

import java.awt.Color;
import java.awt.Paint;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;

/** Shared Paint serializer and deserializer logic. */
public final class PaintValueCodec {

  public static final class Deserializer extends ValueDeserializer<Paint> {
    @Override
    public Paint deserialize(final JsonParser parser, final DeserializationContext ctxt) {
      return parse(parser.getValueAsString());
    }
  }

  public static final class Serializer extends ValueSerializer<Paint> {
    @Override
    public void serialize(
        final Paint value, final JsonGenerator gen, final SerializationContext ctxt) {
      gen.writeString(format(value));
    }
  }

  static String format(final Paint value) {
    if (!(value instanceof final Color color)) {
      throw new IllegalArgumentException("Only Color paints are supported");
    }
    return "#%08X".formatted(color.getRGB());
  }

  static Paint parse(final String value) {
    final int rgba = (int) Long.parseLong(value.substring(1), 16);
    return new Color(rgba, true);
  }

  private PaintValueCodec() {
    // Prevent instantiation
  }
}
