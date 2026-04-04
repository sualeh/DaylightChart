import { useRef, useState } from 'react'

// The three data sources the app understands, each with its own upload endpoint.
// downloadUrl links to the official source where raw data files can be obtained.
const DATA_SOURCES = [
  {
    id: 'gnis',
    label: 'GNIS (US States)',
    title: 'Geographic Names Information System — US state zip or text file',
    accept: '.zip,.txt',
    downloadUrl: 'https://edits.nationalmap.gov/apps/gaz-domestic/public/search/names',
    downloadLabel: 'Get GNIS files (USGS)',
  },
  {
    id: 'gns',
    label: 'GNS (Countries)',
    title: 'Geographic Names Server — country zip or text file',
    accept: '.zip,.txt',
    downloadUrl: 'http://geonames.nga.mil/gns/html/namefiles.html',
    downloadLabel: 'Get GNS files (NGA)',
  },
  {
    id: 'data',
    label: 'Daylight Chart Locations',
    title: 'locations.data — Daylight Chart semicolon-delimited format',
    accept: '.data,.txt',
    downloadUrl: null,
    downloadLabel: null,
  },
]

interface Props {
  totalLocations: number  // shown in the badge next to the heading
  onLoaded: () => void    // called after a successful upload so the parent can refresh
}

export function DataPanel({ totalLocations, onLoaded }: Props) {
  const inputRefs = useRef<Record<string, HTMLInputElement | null>>({})
  const [statusMsg, setStatusMsg] = useState<string | null>(null)
  const [isError, setIsError] = useState(false)
  const [isLoading, setIsLoading] = useState(false)

  async function upload(file: File, sourceId: string) {
    setIsLoading(true)
    setStatusMsg(null)

    const form = new FormData()
    form.append('file', file)

    try {
      const res = await fetch(`/api/locations/upload/${sourceId}`, { method: 'POST', body: form })
      const data = await res.json() as { added?: number; error?: string; filename?: string }

      if (data.error) {
        setIsError(true)
        setStatusMsg(`Error: ${data.error}`)
      } else {
        setIsError(false)
        setStatusMsg(`✓ Loaded ${data.added?.toLocaleString()} locations from ${data.filename}`)
        onLoaded()
      }
    } catch (e: unknown) {
      setIsError(true)
      setStatusMsg(`Error: ${String(e)}`)
    } finally {
      setIsLoading(false)
      // Reset the input so the same file can be selected again if needed
      const input = inputRefs.current[sourceId]
      if (input) input.value = ''
    }
  }

  return (
    <div>
      {/* Location count badge */}
      <div style={{ display: 'flex', justifyContent: 'flex-end', marginBottom: '0.4rem' }}>
        <span style={badgeStyle} title="Total locations in memory">
          {totalLocations.toLocaleString()} locations
        </span>
      </div>

      {/* One upload button + optional download link per data source */}
      {DATA_SOURCES.map((src) => (
        <div key={src.id} style={{ marginBottom: '0.6rem' }}>
          {/* Hidden file input — triggered by the button below */}
          <input
            ref={(el) => { inputRefs.current[src.id] = el }}
            type="file"
            accept={src.accept}
            style={{ display: 'none' }}
            onChange={(e) => {
              const file = e.target.files?.[0]
              if (file) void upload(file, src.id)
            }}
          />
          <button
            title={src.title}
            disabled={isLoading}
            onClick={() => inputRefs.current[src.id]?.click()}
            style={buttonStyle(isLoading)}
          >
            📂 {src.label}
          </button>

          {/* External link to download raw data files for this source */}
          {src.downloadUrl && (
            <a
              href={src.downloadUrl}
              target="_blank"
              rel="noopener noreferrer"
              title={`Download ${src.label} data files from the official source`}
              style={externalLinkStyle}
            >
              ↗ {src.downloadLabel}
            </a>
          )}
        </div>
      ))}

      {/* Download current registry as locations.data */}
      <div style={{ marginTop: '0.6rem' }}>
        <a
          href="/api/locations/download"
          download="locations.data"
          title="Download current locations as a locations.data file"
          style={downloadLinkStyle}
        >
          💾 Download locations.data
        </a>
      </div>

      {/* Upload status feedback */}
      {isLoading && <p style={{ ...statusStyle, color: '#555' }}>⏳ Loading…</p>}
      {!isLoading && statusMsg && (
        <p style={{ ...statusStyle, color: isError ? '#c00' : '#197a1d' }}>{statusMsg}</p>
      )}
    </div>
  )
}

// ── Styles ──────────────────────────────────────────────────────────────────

const badgeStyle: React.CSSProperties = {
  background: '#e4e4e7',
  borderRadius: 10,
  padding: '0.1rem 0.45rem',
  fontSize: '0.7rem',
  fontWeight: 700,
  color: '#333',
}

function buttonStyle(disabled: boolean): React.CSSProperties {
  return {
    width: '100%',
    padding: '0.4rem 0.6rem',
    fontSize: '0.8rem',
    textAlign: 'left',
    background: '#fff',
    border: '1px solid #ccc',
    borderRadius: 5,
    cursor: disabled ? 'wait' : 'pointer',
    color: '#333',
  }
}

const downloadLinkStyle: React.CSSProperties = {
  display: 'block',
  width: '100%',
  padding: '0.4rem 0.6rem',
  fontSize: '0.8rem',
  textAlign: 'left',
  background: '#fff',
  border: '1px solid #ccc',
  borderRadius: 5,
  textDecoration: 'none',
  color: '#333',
  boxSizing: 'border-box',
}

const externalLinkStyle: React.CSSProperties = {
  display: 'block',
  marginTop: '0.2rem',
  paddingLeft: '0.4rem',
  fontSize: '0.75rem',
  color: '#2563eb',
  textDecoration: 'none',
}

const statusStyle: React.CSSProperties = {
  margin: '0.5rem 0 0',
  fontSize: '0.75rem',
}
