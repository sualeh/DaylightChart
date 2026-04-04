package daylightchart.web;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.geoname.data.LocationRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LocationRegistryConfigTest {

  @Autowired private LocationRegistry locationRegistry;

  @Test
  void registryBeanIsLoaded() {
    assertThat("LocationRegistry bean must not be null", locationRegistry, is(notNullValue()));
  }

  @Test
  void registryContainsLocations() {
    assertThat(
        "LocationRegistry must contain bundled locations",
        locationRegistry.size(),
        is(greaterThan(0)));
  }
}
