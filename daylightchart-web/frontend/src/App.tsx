import { useCallback, useEffect, useRef, useState } from 'react'
import { DataPanel } from './components/DataPanel'
import { LocationSearch } from './components/LocationSearch'
import { LocationsPanel } from './components/LocationsPanel'
import { NavSection } from './components/NavSection'
import { OptionsPanel } from './components/OptionsPanel'
import { type RiseSetDay, RiseSetTable } from './components/RiseSetTable'
import { useWindowWidth } from './hooks/useWindowWidth'
import { type ChartOptions, DEFAULT_CHART_OPTIONS, type LocationDto } from './types'

const SIDEBAR_WIDTH = 280
const MIN_CHART_WIDTH = 640
const MAX_CHART_WIDTH = 1920
const CURRENT_YEAR = new Date().getFullYear()
// Screens narrower than this get the hamburger menu instead of a fixed sidebar.
const MOBILE_BREAKPOINT = 768

function toChartSize(containerWidth: number): { w: number; h: number } {
  const w = Math.max(MIN_CHART_WIDTH, Math.min(MAX_CHART_WIDTH, Math.floor(containerWidth)))
  return { w, h: Math.round(w * 9 / 16) }
}

export default function App() {
  const [locations, setLocations] = useState<LocationDto[]>([])
  const [selectedKey, setSelectedKey] = useState<string>('')
  const [chartOptions, setChartOptions] = useState<ChartOptions>(DEFAULT_CHART_OPTIONS)
  const [chartSize, setChartSize] = useState<{ w: number; h: number }>({ w: 1024, h: 576 })
  const [riseSetData, setRiseSetData] = useState<RiseSetDay[]>([])
  const [error, setError] = useState<string | null>(null)
  const [navOpen, setNavOpen] = useState(false)
  const mainRef = useRef<HTMLElement>(null)
  const windowWidth = useWindowWidth()
  const isSmall = windowWidth < MOBILE_BREAKPOINT

  // Close hamburger menu whenever the screen grows past the breakpoint.
  useEffect(() => {
    if (!isSmall) setNavOpen(false)
  }, [isSmall])

  // Load (or reload) the location list from the server.
  // resetSelection=true picks the first location in the new list.
  const fetchLocations = useCallback((resetSelection = false) => {
    fetch('/api/locations', { cache: 'no-store' })
      .then((res) => {
        if (!res.ok) throw new Error(`HTTP ${res.status}`)
        return res.json() as Promise<LocationDto[]>
      })
      .then((locs) => {
        setLocations(locs)
        setSelectedKey((prev) => {
          if (!resetSelection && prev) return prev
          const boston = locs.find((l) => l.city === 'Boston' && l.countryCode === 'US')
          return boston?.locationKey ?? locs[0]?.locationKey ?? ''
        })
      })
      .catch((err: unknown) => setError(String(err)))
  }, [])

  useEffect(() => { fetchLocations() }, [fetchLocations])

  // Fetch rise/set data when the table is shown or the selected location changes.
  // Only fetches when the table is actually visible to avoid unnecessary requests.
  useEffect(() => {
    if (!selectedKey || !chartOptions.showTable) return
    const params = new URLSearchParams({ locationKey: selectedKey, year: String(CURRENT_YEAR) })
    fetch(`/api/riseset?${params}`, { cache: 'no-store' })
      .then(res => {
        if (!res.ok) throw new Error(`HTTP ${res.status}`)
        return res.json() as Promise<RiseSetDay[]>
      })
      .then(setRiseSetData)
      .catch((err: unknown) => setError(String(err)))
  }, [selectedKey, chartOptions.showTable])

  // Keep chart dimensions at 16:9 as the window resizes.
  useEffect(() => {
    const el = mainRef.current
    if (!el) return
    const padding = 32
    const measure = (target: Element) =>
      setChartSize(toChartSize(target.clientWidth - padding))
    measure(el)
    const ro = new ResizeObserver(([entry]) => measure(entry.target))
    ro.observe(el)
    return () => ro.disconnect()
  }, [])

  // Build the chart URL from all current state.
  // Every state change above immediately invalidates this URL, causing the <img> to reload.
  function buildChartSrc() {
    const params = new URLSearchParams()
    if (selectedKey) params.set('locationKey', selectedKey)
    params.set('width', String(chartSize.w))
    params.set('height', String(chartSize.h))
    params.set('twilightType', chartOptions.twilightType)
    params.set('orientation', chartOptions.orientation)
    params.set('showLegend', String(chartOptions.showLegend))
    return `/api/chart?${params}`
  }
  const chartSrc = buildChartSrc()

  // ── Sidebar content (shared between fixed and mobile overlay) ────────────
  const sidebarContent = (
    <>
      {/* Location search — always visible at the top */}
      <LocationSearch
        locations={locations}
        selectedKey={selectedKey}
        onSelect={(key) => { setSelectedKey(key); if (isSmall) setNavOpen(false) }}
      />

      {/* Locations section — add / edit / delete */}
      <NavSection title="Locations">
        <LocationsPanel
          locations={locations}
          selectedKey={selectedKey}
          onSelect={(key) => { setSelectedKey(key); if (isSmall) setNavOpen(false) }}
          onChanged={() => fetchLocations(false)}
        />
      </NavSection>

      {/* Chart options section */}
      <NavSection title="Chart Options">
        <OptionsPanel options={chartOptions} onChange={setChartOptions} />
      </NavSection>

      {/* Data section — upload and download */}
      <NavSection title="Load / Save Data">
        <DataPanel
          totalLocations={locations.length}
          onLoaded={() => fetchLocations(true)}
        />
      </NavSection>
    </>
  )

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100vh', fontFamily: 'system-ui, sans-serif' }}>

      {/* ── Full-width header ── */}
      <header style={{
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        padding: '0 1.25rem',
        height: 52, minHeight: 52,
        background: '#f4f4f5', color: '#18181b',
        borderBottom: '1px solid #e4e4e7',
        flexShrink: 0,
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
          <img src="/logo.png" alt="" width={28} height={28} style={{ flexShrink: 0 }} />
          <span style={{ fontSize: '1.05rem', fontWeight: 700, letterSpacing: '-0.01em' }}>
            Daylight Chart
          </span>
        </div>

        {/* Hamburger button — visible only on small screens */}
        {isSmall && (
          <button
            aria-label="Open navigation menu"
            onClick={() => setNavOpen(true)}
            style={{
              background: 'none', border: '1px solid #a1a1aa',
              color: '#18181b', borderRadius: 5,
              padding: '0.3rem 0.55rem', fontSize: '1.1rem',
              cursor: 'pointer', lineHeight: 1,
            }}
          >
            ☰
          </button>
        )}
      </header>

      {/* ── Body (sidebar + main) ── */}
      <div style={{ display: 'flex', flex: 1, overflow: 'hidden' }}>

        {/* ── Sidebar — fixed panel on large screens ── */}
        {!isSmall && (
          <nav style={{
            width: SIDEBAR_WIDTH, minWidth: SIDEBAR_WIDTH,
            background: '#f4f4f5', borderRight: '1px solid #e4e4e7',
            display: 'flex', flexDirection: 'column',
            padding: '1rem', gap: '1rem', overflowY: 'auto',
          }}>
            {sidebarContent}
          </nav>
        )}

        {/* ── Mobile nav overlay — full-screen drawer ── */}
        {isSmall && navOpen && (
          <>
            {/* Backdrop */}
            <div
              onClick={() => setNavOpen(false)}
              style={{
                position: 'fixed', inset: 0, zIndex: 90,
                background: 'rgba(0,0,0,0.45)',
              }}
            />
            {/* Drawer */}
            <nav style={{
              position: 'fixed', inset: 0, zIndex: 100,
              background: '#f4f4f5',
              display: 'flex', flexDirection: 'column',
              padding: '0.75rem 1rem 1rem', gap: '1rem', overflowY: 'auto',
            }}>
              {/* Close button row */}
              <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
                <button
                  aria-label="Close navigation menu"
                  onClick={() => setNavOpen(false)}
                  style={{
                    background: 'none', border: '1px solid #a1a1aa',
                    borderRadius: 5, padding: '0.3rem 0.6rem',
                    fontSize: '1rem', cursor: 'pointer', color: '#333', lineHeight: 1,
                  }}
                >
                  ✕ Close
                </button>
              </div>
              {sidebarContent}
            </nav>
          </>
        )}

        {/* ── Main content ── */}
        <main ref={mainRef} style={{
          flex: 1, padding: '1.5rem', overflowY: 'auto',
          background: '#ffffff', display: 'flex', flexDirection: 'column', alignItems: 'flex-start',
        }}>
          {error && <p style={{ color: '#c00', fontSize: '0.875rem' }}>Error: {error}</p>}

          <img
            key={chartSrc}
            src={chartSrc}
            alt="Daylight chart"
            width={chartSize.w}
            height={chartSize.h}
            style={{ maxWidth: '100%', border: '1px solid #e4e4e7', borderRadius: 4 }}
          />

          {chartOptions.showTable && riseSetData.length > 0 && (
            <RiseSetTable data={riseSetData} locationKey={selectedKey} year={CURRENT_YEAR} />
          )}
        </main>

      </div>
    </div>
  )
}

