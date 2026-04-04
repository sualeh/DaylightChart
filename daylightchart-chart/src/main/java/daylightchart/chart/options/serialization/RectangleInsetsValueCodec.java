/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.chart.options.serialization;

import org.jfree.chart.ui.RectangleInsets;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;

/** Shared RectangleInsets serializer and deserializer logic. */
public final class RectangleInsetsValueCodec {

  public static final class Deserializer extends ValueDeserializer<RectangleInsets> {
    @Override
    public RectangleInsets deserialize(final JsonParser parser, final DeserializationContext ctxt) {
      return parse(parser.getValueAsString());
    }
  }

  public static final class Serializer extends ValueSerializer<RectangleInsets> {
    @Override
    public void serialize(
        final RectangleInsets value, final JsonGenerator gen, final SerializationContext ctxt) {
      gen.writeString(format(value));
    }
  }

  static String format(final RectangleInsets value) {
    return value.getTop()
        + "|"
        + value.getLeft()
        + "|"
        + value.getBottom()
        + "|"
        + value.getRight();
  }

  static RectangleInsets parse(final String value) {
    final String[] parts = value.split("\\|", -1);
    return new RectangleInsets(
        Double.parseDouble(parts[0]),
        Double.parseDouble(parts[1]),
        Double.parseDouble(parts[2]),
        Double.parseDouble(parts[3]));
  }

  private RectangleInsetsValueCodec() {
    // Prevent instantiation
  }
}
