package daylightchart.web.dto;

/**
 * A single day's sunrise/sunset summary, returned by {@code GET /api/riseset}.
 *
 * <p>{@code sunrise} and {@code sunset} are {@code null} for polar days where the sun never rises
 * or never sets (both use the same sentinel times internally, so {@code daylightMinutes} is set to
 * zero for those days as a conservative default).
 */
public record RiseSetDayDto(
    /** ISO-8601 date, e.g. {@code "2025-06-21"}. */
    String date,
    /** Local sunrise time formatted as {@code "HH:mm"}, or {@code null} for polar days. */
    String sunrise,
    /** Local sunset time formatted as {@code "HH:mm"}, or {@code null} for polar days. */
    String sunset,
    /** Minutes of daylight (sunrise to sunset), or {@code 0} for polar days. */
    int daylightMinutes) {}
