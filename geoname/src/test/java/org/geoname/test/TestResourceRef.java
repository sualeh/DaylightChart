/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package org.geoname.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.geoname.parser.resources.ResourceRef;
import org.geoname.parser.resources.ResourceRefs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class TestResourceRef {

  @TempDir Path tempDir;

  // -------------------------------------------------------------------------
  // ClasspathResourceRef
  // -------------------------------------------------------------------------

  @Test
  public void classpathExistingResource() {
    final ResourceRef ref = ResourceRefs.ofClasspath("locations.data");
    assertThat(ref.exists(), is(true));
  }

  @Test
  public void classpathMissingResource() {
    final ResourceRef ref = ResourceRefs.ofClasspath("does-not-exist.txt");
    assertThat(ref.exists(), is(false));
  }

  @Test
  public void classpathLocation() {
    final ResourceRef ref = ResourceRefs.ofClasspath("locations.data");
    assertThat(ref.location(), is("classpath:locations.data"));
  }

  @Test
  public void classpathConstructorDoesNotOpenStream() {
    // Must not throw even for a non-existent resource
    final ResourceRef ref = ResourceRefs.ofClasspath("nonexistent.txt");
    assertThat(ref, is(notNullValue()));
  }

  @Test
  public void classpathOpenStreamReadsData() throws IOException {
    final ResourceRef ref = ResourceRefs.ofClasspath("locations.data");
    try (InputStream is = ref.openStream()) {
      final byte[] first = is.readNBytes(1);
      assertThat("Stream should contain data", first.length, is(1));
    }
  }

  @Test
  public void classpathWithExplicitClassLoader() throws IOException {
    final ClassLoader cl = Thread.currentThread().getContextClassLoader();
    final ResourceRef ref = ResourceRefs.ofClasspath(cl, "locations.data");
    assertThat(ref.exists(), is(true));
    try (InputStream is = ref.openStream()) {
      assertThat(is.readNBytes(1).length, is(1));
    }
  }

  // -------------------------------------------------------------------------
  // FileResourceRef
  // -------------------------------------------------------------------------

  @Test
  public void fileExistingResource() throws IOException {
    final Path file = tempDir.resolve("test.txt");
    Files.writeString(file, "hello");

    final ResourceRef ref = ResourceRefs.ofFile(file);
    assertThat(ref.exists(), is(true));
  }

  @Test
  public void fileMissingResource() {
    final Path file = tempDir.resolve("missing.txt");
    final ResourceRef ref = ResourceRefs.ofFile(file);
    assertThat(ref.exists(), is(false));
  }

  @Test
  public void fileLocation() throws IOException {
    final Path file = tempDir.resolve("test.txt");
    Files.writeString(file, "hello");

    final ResourceRef ref = ResourceRefs.ofFile(file);
    assertThat(ref.location(), startsWith("file:"));
  }

  @Test
  public void fileConstructorDoesNotOpenStream() {
    // Must not throw even if file does not yet exist
    final Path missing = tempDir.resolve("not-yet.txt");
    final ResourceRef ref = ResourceRefs.ofFile(missing);
    assertThat(ref, is(notNullValue()));
  }

  @Test
  public void fileOpenStreamReadsData() throws IOException {
    final Path file = tempDir.resolve("data.txt");
    Files.writeString(file, "File content", StandardCharsets.UTF_8);

    final ResourceRef ref = ResourceRefs.ofFile(file);
    try (InputStream is = ref.openStream()) {
      final String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
      assertThat(content, is("File content"));
    }
  }

  // -------------------------------------------------------------------------
  // UrlResourceRef
  // -------------------------------------------------------------------------

  @Test
  public void urlExistingResource() throws IOException {
    final Path file = tempDir.resolve("test.txt");
    Files.writeString(file, "hello");

    final ResourceRef ref = ResourceRefs.ofUrl(file.toUri().toURL());
    assertThat(ref.exists(), is(true));
  }

  @Test
  public void urlMissingResource() throws IOException {
    final Path missing = tempDir.resolve("missing.txt");
    final ResourceRef ref = ResourceRefs.ofUrl(missing.toUri().toURL());
    assertThat(ref.exists(), is(false));
  }

  @Test
  public void urlLocation() throws IOException {
    final URL url = tempDir.resolve("test.txt").toUri().toURL();
    final ResourceRef ref = ResourceRefs.ofUrl(url);
    assertThat(ref.location(), startsWith("url:file:"));
  }

  @Test
  public void urlConstructorDoesNotOpenStream() throws IOException {
    final URL url = tempDir.resolve("not-yet.txt").toUri().toURL();
    final ResourceRef ref = ResourceRefs.ofUrl(url);
    assertThat(ref, is(notNullValue()));
  }

  @Test
  public void urlOpenStreamReadsData() throws IOException {
    final Path file = tempDir.resolve("data.txt");
    Files.writeString(file, "URL content", StandardCharsets.UTF_8);

    final ResourceRef ref = ResourceRefs.ofUrl(file.toUri().toURL());
    try (InputStream is = ref.openStream()) {
      final String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
      assertThat(content, is("URL content"));
    }
  }

  // -------------------------------------------------------------------------
  // ZipEntryResourceRef
  // -------------------------------------------------------------------------

  @Test
  public void zipEntryExists() throws IOException {
    final Path zipFile = createTempZip("hello.txt", "Hello World");

    final ResourceRef ref = ResourceRefs.ofZipEntry(zipFile, "hello.txt");
    assertThat(ref.exists(), is(true));
  }

  @Test
  public void zipEntryMissing() throws IOException {
    final Path zipFile = createTempZip("hello.txt", "Hello World");

    final ResourceRef ref = ResourceRefs.ofZipEntry(zipFile, "not-there.txt");
    assertThat(ref.exists(), is(false));
  }

  @Test
  public void zipEntryLocation() throws IOException {
    final Path zipFile = createTempZip("entry.txt", "data");

    final ResourceRef ref = ResourceRefs.ofZipEntry(zipFile, "entry.txt");
    assertThat(ref.location(), startsWith("jar:file:"));
    assertThat(ref.location(), containsString("!/entry.txt"));
  }

  @Test
  public void zipEntryConstructorDoesNotOpenStream() throws IOException {
    final Path zipFile = createTempZip("entry.txt", "data");
    // Must not throw, no stream opened
    final ResourceRef ref = ResourceRefs.ofZipEntry(zipFile, "missing.txt");
    assertThat(ref, is(notNullValue()));
  }

  @Test
  public void zipEntryOpenStreamReadsData() throws IOException {
    final Path zipFile = createTempZip("entry.txt", "Zip content");

    final ResourceRef ref = ResourceRefs.ofZipEntry(zipFile, "entry.txt");
    try (InputStream is = ref.openStream()) {
      final String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
      assertThat(content, is("Zip content"));
    }
  }

  @Test
  public void zipEntryLeadingSlashNormalized() throws IOException {
    final Path zipFile = createTempZip("entry.txt", "Normalized");

    // Both with and without leading slash should resolve the same entry
    final ResourceRef refWithSlash = ResourceRefs.ofZipEntry(zipFile, "/entry.txt");
    final ResourceRef refWithoutSlash = ResourceRefs.ofZipEntry(zipFile, "entry.txt");

    assertThat(refWithSlash.exists(), is(true));
    assertThat(refWithoutSlash.exists(), is(true));
  }

  // -------------------------------------------------------------------------
  // Helper
  // -------------------------------------------------------------------------

  private Path createTempZip(final String entryName, final String content) throws IOException {
    final Path zipFile = tempDir.resolve("test.zip");
    try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile))) {
      zos.putNextEntry(new ZipEntry(entryName));
      zos.write(content.getBytes(StandardCharsets.UTF_8));
      zos.closeEntry();
    }
    return zipFile;
  }
}
