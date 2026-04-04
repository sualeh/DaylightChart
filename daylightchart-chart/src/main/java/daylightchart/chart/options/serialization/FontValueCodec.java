/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.chart.options.serialization;

import java.awt.Font;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;

/** Shared Font serializer and deserializer logic. */
public final class FontValueCodec {

  public static final class Deserializer extends ValueDeserializer<Font> {
    @Override
    public Font deserialize(final JsonParser parser, final DeserializationContext ctxt) {
      return parse(parser.getValueAsString());
    }
  }

  public static final class Serializer extends ValueSerializer<Font> {
    @Override
    public void serialize(
        final Font value, final JsonGenerator gen, final SerializationContext ctxt) {
      gen.writeString(format(value));
    }
  }

  static String format(final Font value) {
    return value.getName() + "|" + value.getStyle() + "|" + value.getSize();
  }

  static Font parse(final String value) {
    final String[] parts = value.split("\\|", -1);
    return new Font(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
  }

  private FontValueCodec() {
    // Prevent instantiation
  }
}
