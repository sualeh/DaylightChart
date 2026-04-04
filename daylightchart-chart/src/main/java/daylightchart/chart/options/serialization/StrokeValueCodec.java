/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.chart.options.serialization;

import java.awt.BasicStroke;
import java.awt.Stroke;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;

/** Shared Stroke serializer and deserializer logic. */
public final class StrokeValueCodec {

  public static final class Deserializer extends ValueDeserializer<Stroke> {
    @Override
    public Stroke deserialize(final JsonParser parser, final DeserializationContext ctxt) {
      return parse(parser.getValueAsString());
    }
  }

  public static final class Serializer extends ValueSerializer<Stroke> {
    @Override
    public void serialize(
        final Stroke value, final JsonGenerator gen, final SerializationContext ctxt) {
      gen.writeString(format(value));
    }
  }

  static String format(final Stroke value) {
    if (!(value instanceof final BasicStroke stroke)) {
      throw new IllegalArgumentException("Only BasicStroke values are supported");
    }

    final float[] dashArray = stroke.getDashArray();
    final StringBuilder dashBuilder = new StringBuilder();
    if (dashArray != null) {
      for (int i = 0; i < dashArray.length; i++) {
        if (i > 0) {
          dashBuilder.append(',');
        }
        dashBuilder.append(dashArray[i]);
      }
    }

    return stroke.getLineWidth()
        + "|"
        + stroke.getEndCap()
        + "|"
        + stroke.getLineJoin()
        + "|"
        + stroke.getMiterLimit()
        + "|"
        + dashBuilder
        + "|"
        + stroke.getDashPhase();
  }

  static Stroke parse(final String value) {
    final String[] parts = value.split("\\|", -1);
    final float lineWidth = Float.parseFloat(parts[0]);
    final int endCap = Integer.parseInt(parts[1]);
    final int lineJoin = Integer.parseInt(parts[2]);
    final float miterLimit = Float.parseFloat(parts[3]);
    final float[] dashArray;
    if (parts[4].isEmpty()) {
      dashArray = null;
    } else {
      final String[] dashParts = parts[4].split(",", -1);
      dashArray = new float[dashParts.length];
      for (int i = 0; i < dashParts.length; i++) {
        dashArray[i] = Float.parseFloat(dashParts[i]);
      }
    }
    final float dashPhase = Float.parseFloat(parts[5]);
    return new BasicStroke(lineWidth, endCap, lineJoin, miterLimit, dashArray, dashPhase);
  }

  private StrokeValueCodec() {
    // Prevent instantiation
  }
}
