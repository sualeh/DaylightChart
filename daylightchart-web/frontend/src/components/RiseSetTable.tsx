// RiseSetTable — shows one row per day of the year with sunrise, sunset, and daylight duration.
// Each column is sortable by clicking the header.

import { useState } from 'react'

export interface RiseSetDay {
  date: string           // "2025-06-21"
  sunrise: string | null // "06:05" or null for polar days
  sunset: string | null  // "20:42" or null for polar days
  daylightMinutes: number
}

interface Props {
  data: RiseSetDay[]
  locationKey: string
  year: number
}

type SortCol = 'date' | 'sunrise' | 'sunset' | 'daylightMinutes'
type SortDir = 'asc' | 'desc'

function formatDaylight(minutes: number): string {
  if (minutes === 0) return '—'
  const h = Math.floor(minutes / 60)
  const m = minutes % 60
  return `${h}h ${String(m).padStart(2, '0')}m`
}

export function RiseSetTable({ data, locationKey, year }: Props) {
  const [sortCol, setSortCol] = useState<SortCol>('date')
  const [sortDir, setSortDir] = useState<SortDir>('asc')

  function handleSort(col: SortCol) {
    if (col === sortCol) {
      setSortDir(d => d === 'asc' ? 'desc' : 'asc')
    } else {
      setSortCol(col)
      setSortDir('asc')
    }
  }

  const sorted = [...data].sort((a, b) => {
    let cmp = 0
    if (sortCol === 'date') {
      cmp = a.date.localeCompare(b.date)
    } else if (sortCol === 'sunrise') {
      cmp = (a.sunrise ?? '99:99').localeCompare(b.sunrise ?? '99:99')
    } else if (sortCol === 'sunset') {
      cmp = (a.sunset ?? '99:99').localeCompare(b.sunset ?? '99:99')
    } else {
      cmp = a.daylightMinutes - b.daylightMinutes
    }
    return sortDir === 'asc' ? cmp : -cmp
  })

  const arrow = (col: SortCol) => sortCol === col ? (sortDir === 'asc' ? ' ▲' : ' ▼') : ''

  const csvHref =
    '/api/riseset/csv?' +
    new URLSearchParams({
      locationKey,
      year: String(year),
    }).toString()

  const thStyle: React.CSSProperties = {
    padding: '0.5rem 0.75rem',
    textAlign: 'left',
    borderBottom: '2px solid #e4e4e7',
    background: '#f4f4f5',
    fontWeight: 600,
    fontSize: '0.8rem',
    cursor: 'pointer',
    userSelect: 'none',
    whiteSpace: 'nowrap',
  }

  const tdStyle: React.CSSProperties = {
    padding: '0.35rem 0.75rem',
    borderBottom: '1px solid #f4f4f5',
    fontSize: '0.85rem',
    whiteSpace: 'nowrap',
  }

  return (
    <div style={{ marginTop: '1.5rem', width: '100%' }}>

      {/* Heading + download link */}
      <div style={{ display: 'flex', alignItems: 'baseline', gap: '1rem', marginBottom: '0.75rem' }}>
        <h2 style={{ margin: 0, fontSize: '1rem', fontWeight: 600, color: '#18181b' }}>
          Sunrise &amp; Sunset — {year}
        </h2>
        <a
          href={csvHref}
          download
          style={{ fontSize: '0.8rem', color: '#2563eb', textDecoration: 'none' }}
        >
          ⬇ Download CSV
        </a>
      </div>

      {/* Scrollable table container */}
      <div style={{ overflowX: 'auto', border: '1px solid #e4e4e7', borderRadius: 4 }}>
        <table style={{ borderCollapse: 'collapse', width: '100%', minWidth: 360 }}>
          <thead>
            <tr>
              <th style={thStyle} onClick={() => handleSort('date')}>Date{arrow('date')}</th>
              <th style={thStyle} onClick={() => handleSort('sunrise')}>Sunrise{arrow('sunrise')}</th>
              <th style={thStyle} onClick={() => handleSort('sunset')}>Sunset{arrow('sunset')}</th>
              <th style={{ ...thStyle, textAlign: 'right' }} onClick={() => handleSort('daylightMinutes')}>
                Daylight{arrow('daylightMinutes')}
              </th>
            </tr>
          </thead>
          <tbody>
            {sorted.map(row => (
              <tr key={row.date} style={{ background: '#fff' }}
                onMouseEnter={e => (e.currentTarget.style.background = '#f9f9fb')}
                onMouseLeave={e => (e.currentTarget.style.background = '#fff')}
              >
                <td style={tdStyle}>{row.date}</td>
                <td style={tdStyle}>{row.sunrise ?? '—'}</td>
                <td style={tdStyle}>{row.sunset ?? '—'}</td>
                <td style={{ ...tdStyle, textAlign: 'right' }}>{formatDaylight(row.daylightMinutes)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

    </div>
  )
}
