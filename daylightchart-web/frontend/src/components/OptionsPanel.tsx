import type { ChartOptions } from '../types'

interface Props {
  options: ChartOptions
  onChange: (options: ChartOptions) => void
}

// Each <select> or <input> calls onChange with the full updated options object.
// The parent re-builds the chart URL immediately, so the chart re-loads on every change.
export function OptionsPanel({ options, onChange }: Props) {
  function set<K extends keyof ChartOptions>(key: K, value: ChartOptions[K]) {
    onChange({ ...options, [key]: value })
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '0.6rem' }}>

      <div style={rowStyle}>
        <label htmlFor="opt-twilight" style={labelStyle}>Twilight</label>
        <select
          id="opt-twilight"
          value={options.twilightType}
          onChange={(e) => set('twilightType', e.target.value as ChartOptions['twilightType'])}
          style={selectStyle}
        >
          <option value="CIVIL">Civil</option>
          <option value="NAUTICAL">Nautical</option>
          <option value="ASTRONOMICAL">Astronomical</option>
        </select>
      </div>

      <div style={rowStyle}>
        <label htmlFor="opt-orientation" style={labelStyle}>Orientation</label>
        <select
          id="opt-orientation"
          value={options.orientation}
          onChange={(e) => set('orientation', e.target.value as ChartOptions['orientation'])}
          style={selectStyle}
        >
          <option value="STANDARD">Standard</option>
          <option value="CONVENTIONAL">Conventional</option>
          <option value="VERTICAL">Vertical</option>
        </select>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
        <input
          id="opt-legend"
          type="checkbox"
          checked={options.showLegend}
          onChange={(e) => set('showLegend', e.target.checked)}
          style={{ width: 14, height: 14, cursor: 'pointer' }}
        />
        <label htmlFor="opt-legend" style={{ ...labelStyle, marginBottom: 0, cursor: 'pointer' }}>
          Show legend
        </label>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
        <input
          id="opt-table"
          type="checkbox"
          checked={options.showTable}
          onChange={(e) => set('showTable', e.target.checked)}
          style={{ width: 14, height: 14, cursor: 'pointer' }}
        />
        <label htmlFor="opt-table" style={{ ...labelStyle, marginBottom: 0, cursor: 'pointer' }}>
          Show sunrise/sunset table
        </label>
      </div>

    </div>
  )
}

// ── Styles ────────────────────────────────────────────────────────────────

const rowStyle: React.CSSProperties = {
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'space-between',
  gap: '0.4rem',
}

const labelStyle: React.CSSProperties = {
  fontSize: '0.8rem',
  color: '#444',
  marginBottom: 2,
  flexShrink: 0,
}

const selectStyle: React.CSSProperties = {
  fontSize: '0.8rem',
  border: '1px solid #ccc',
  borderRadius: 4,
  padding: '0.2rem 0.4rem',
  background: '#fff',
  color: '#111',
  flex: 1,
  minWidth: 0,
}
