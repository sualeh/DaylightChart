[![Quick Build](https://github.com/sualeh/daylight-chart/actions/workflows/quick-build.yml/badge.svg)](https://github.com/sualeh/daylight-chart/actions/workflows/quick-build.yml)
[![Release Desktop](https://github.com/sualeh/daylight-chart/actions/workflows/release-desktop.yml/badge.svg)](https://github.com/sualeh/daylight-chart/actions/workflows/release-desktop.yml)


# ![DaylightChart](https://github.com/sualeh/DaylightChart/blob/main/daylightchart/src/site/resources/images/daylightchart_logo.png?raw=true) Daylight Chart

Daylight Chart shows sunrise and sunset times in an attractive chart, for any location in the world. The effect of daylight savings time is also displayed. Charts can be exported to image files.


## Running with Docker

Pull and start the web application using Docker:

```bash
docker run --rm -p 8080:8080 sualeh/daylight-chart:latest
```

Then open your browser and navigate to:

```
http://localhost:8080
```

### Changing the port

To run on a different host port (e.g., 9090), change the host-side of the port mapping:

```bash
docker run --rm -p 9090:8080 sualeh/daylight-chart:latest
```

To change the server port for the container, set the `SERVER_PORT` environment variable. In that case, update both the environment variable and the port mapping to match:

```bash
docker run --rm -e SERVER_PORT=9090 -p 9090:9090 sualeh/daylight-chart:latest
```

Then access the app at `http://localhost:9090`.


## Desktop Application

![DaylightChart Screenshot](https://github.com/sualeh/DaylightChart/blob/main/daylightchart/src/site/resources/screenshot.jpg?raw=true)

### Prerequisites

**Java 21 or later** must be installed on your system before running the desktop application. You can download it from [Adoptium](https://adoptium.net/) or another OpenJDK distribution.

### Downloading

The packaged desktop application is attached as a downloadable artifact to each [Release Desktop](https://github.com/sualeh/daylight-chart/actions/workflows/release-desktop.yml) workflow run:

1. Go to the [Release Desktop](https://github.com/sualeh/daylight-chart/actions/workflows/release-desktop.yml) workflow page.
2. Click on the latest successful run.
3. Download the `daylight-chart-desktop` artifact from the **Artifacts** section at the bottom of the run page.
4. Unzip the downloaded file.

### Running

On **Linux / macOS**, run the shell launcher:

```bash
sh daylightchart.sh
```

On **Windows**, run the command launcher:

```cmd
daylightchart.cmd
```
