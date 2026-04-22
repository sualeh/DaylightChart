/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.test.desktop;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.parser.SVGLoader;
import com.github.weisj.jsvg.view.ViewBox;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URL;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SvgIconTest {

  private static SVGDocument svgDocument;

  @BeforeAll
  static void loadSvgDocument() {
    final URL logoUrl = SvgIconTest.class.getResource("/META-INF/resources/logo.svg");
    assertThat("logo.svg must be accessible on classpath", logoUrl, is(notNullValue()));
    svgDocument = new SVGLoader().load(logoUrl);
    assertThat("SVG document must parse successfully", svgDocument, is(notNullValue()));
  }

  @ParameterizedTest
  @ValueSource(ints = {16, 32, 48, 256})
  void shouldRenderSvgAtExpectedSize(final int size) {
    final BufferedImage image = renderSvg(svgDocument, size);

    assertThat(image, is(notNullValue()));
    assertThat(image.getWidth(), is(size));
    assertThat(image.getHeight(), is(size));
  }

  @ParameterizedTest
  @ValueSource(ints = {16, 32, 48, 256})
  void shouldProduceNonBlankImageAtEachSize(final int size) {
    final BufferedImage image = renderSvg(svgDocument, size);
    final int totalPixels = size * size;
    int nonTransparentPixels = 0;
    for (int x = 0; x < size; x++) {
      for (int y = 0; y < size; y++) {
        if ((image.getRGB(x, y) >>> 24) != 0) {
          nonTransparentPixels++;
        }
      }
    }
    assertThat(
        "Rendered image should have visible pixels", nonTransparentPixels, is(greaterThan(0)));
    assertThat(
        "Rendered image should not be entirely opaque", nonTransparentPixels, is(greaterThan(0)));
    assertThat(
        "Most pixels should not be transparent for a logo",
        nonTransparentPixels * 10,
        is(greaterThan(totalPixels)));
  }

  private static BufferedImage renderSvg(final SVGDocument doc, final int size) {
    final BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D g2d = image.createGraphics();
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    doc.render(null, g2d, new ViewBox(0, 0, size, size));
    g2d.dispose();
    return image;
  }
}
