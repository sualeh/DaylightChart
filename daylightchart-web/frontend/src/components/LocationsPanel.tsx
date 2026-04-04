import { useState } from 'react'
import type { LocationDto } from '../types'

interface Props {
  locations: LocationDto[]
  selectedKey: string
  onChanged: () => void       // called after any add/edit/delete so parent refreshes list
  onSelect: (key: string) => void
}

// Form state used for both Add and Edit.
interface FormState {
  city: string
  adminCode: string
  countryCode: string
  timeZoneId: string
  latitude: string
  longitude: string
}

const EMPTY_FORM: FormState = {
  city: '', adminCode: '', countryCode: '', timeZoneId: '', latitude: '', longitude: '',
}

export function LocationsPanel({ locations, selectedKey, onChanged, onSelect }: Props) {
  // 'key' is the locationKey being edited; null means the form is in Add mode
  const [editingKey, setEditingKey] = useState<string | null | undefined>(undefined)
  const [form, setForm] = useState<FormState>(EMPTY_FORM)
  const [error, setError] = useState<string | null>(null)
  const [saving, setSaving] = useState(false)

  // filterText narrows the visible list (separate from the main combobox)
  const [filterText, setFilterText] = useState('')
  const visible = filterText
    ? locations.filter((l) => l.description.toLowerCase().includes(filterText.toLowerCase()))
    : locations

  // ── Open the inline form ───────────────────────────────────────────────

  function openAdd() {
    setEditingKey(null)   // null = add mode
    setForm(EMPTY_FORM)
    setError(null)
  }

  function openEdit(loc: LocationDto) {
    setEditingKey(loc.locationKey)
    setForm({
      city: loc.city,
      adminCode: loc.adminCode,
      countryCode: loc.countryCode,
      timeZoneId: loc.timeZoneId,
      latitude: String(loc.latitude),
      longitude: String(loc.longitude),
    })
    setError(null)
  }

  function closeForm() { setEditingKey(undefined); setError(null) }

  // ── Save (add or update) ──────────────────────────────────────────────

  async function save() {
    setSaving(true); setError(null)
    const body = {
      city: form.city.trim(),
      adminCode: form.adminCode.trim(),
      countryCode: form.countryCode.trim().toUpperCase(),
      timeZoneId: form.timeZoneId.trim(),
      latitude: parseFloat(form.latitude),
      longitude: parseFloat(form.longitude),
    }
    // Basic client-side checks so the server isn't called for obvious errors
    if (!body.city) { setError('City is required'); setSaving(false); return }
    if (!body.countryCode) { setError('Country code is required'); setSaving(false); return }
    if (!body.timeZoneId) { setError('Timezone is required'); setSaving(false); return }
    if (isNaN(body.latitude) || isNaN(body.longitude)) {
      setError('Latitude and longitude must be numbers'); setSaving(false); return
    }

    try {
      const url = editingKey == null
        ? '/api/locations'
        : `/api/locations?key=${encodeURIComponent(editingKey)}`
      const method = editingKey == null ? 'POST' : 'PUT'
      const res = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
      })
      const data = await res.json() as { error?: string; locationKey?: string }
      if (!res.ok || data.error) { setError(data.error ?? `HTTP ${res.status}`); return }
      // After add: select the new location; after edit: keep current selection
      if (editingKey == null && data.locationKey) onSelect(data.locationKey)
      onChanged()
      closeForm()
    } catch (e: unknown) {
      setError(String(e))
    } finally {
      setSaving(false)
    }
  }

  // ── Delete ─────────────────────────────────────────────────────────────

  async function deleteLocation(key: string) {
    if (!confirm('Delete this location?')) return
    const res = await fetch(`/api/locations?key=${encodeURIComponent(key)}`, { method: 'DELETE' })
    if (res.ok) {
      if (key === selectedKey) onSelect('')
      onChanged()
    }
  }

  // ── Render ─────────────────────────────────────────────────────────────

  return (
    <div>
      {/* Filter + Add button */}
      <div style={{ display: 'flex', gap: '0.4rem', marginBottom: '0.5rem' }}>
        <input
          type="text"
          placeholder="Filter…"
          value={filterText}
          onChange={(e) => setFilterText(e.target.value)}
          style={{ ...inputStyle, flex: 1 }}
        />
        <button onClick={openAdd} title="Add a new location" style={iconBtnStyle('#0070f3')}>＋</button>
      </div>

      {/* Inline Add / Edit form */}
      {editingKey !== undefined && (
        <div style={formBoxStyle}>
          <p style={{ margin: '0 0 0.5rem', fontSize: '0.78rem', fontWeight: 600, color: '#333' }}>
            {editingKey == null ? 'Add location' : 'Edit location'}
          </p>

          <FormRow label="City *">
            <input value={form.city} onChange={(e) => setForm({ ...form, city: e.target.value })} style={inputStyle} />
          </FormRow>
          <FormRow label="Country *" hint="2-letter ISO code">
            <input value={form.countryCode} onChange={(e) => setForm({ ...form, countryCode: e.target.value })} style={inputStyle} maxLength={2} />
          </FormRow>
          <FormRow label="State/Province" hint="e.g. US-MA">
            <input value={form.adminCode} onChange={(e) => setForm({ ...form, adminCode: e.target.value })} style={inputStyle} />
          </FormRow>
          <FormRow label="Timezone *" hint="e.g. America/New_York">
            <input value={form.timeZoneId} onChange={(e) => setForm({ ...form, timeZoneId: e.target.value })} style={inputStyle} />
          </FormRow>
          <FormRow label="Latitude *">
            <input value={form.latitude} onChange={(e) => setForm({ ...form, latitude: e.target.value })} style={inputStyle} type="number" step="any" />
          </FormRow>
          <FormRow label="Longitude *">
            <input value={form.longitude} onChange={(e) => setForm({ ...form, longitude: e.target.value })} style={inputStyle} type="number" step="any" />
          </FormRow>

          {error && <p style={{ margin: '0.3rem 0 0', fontSize: '0.72rem', color: '#c00' }}>{error}</p>}

          <div style={{ display: 'flex', gap: '0.4rem', marginTop: '0.5rem' }}>
            <button onClick={save} disabled={saving} style={saveBtnStyle}>{saving ? '…' : 'Save'}</button>
            <button onClick={closeForm} style={cancelBtnStyle}>Cancel</button>
          </div>
        </div>
      )}

      {/* Location list */}
      <div style={{ maxHeight: 200, overflowY: 'auto', marginTop: '0.3rem' }}>
        {visible.map((loc) => (
          <div
            key={loc.locationKey}
            style={{
              display: 'flex', alignItems: 'center', gap: '0.25rem',
              padding: '0.25rem 0.3rem', borderRadius: 4,
              background: loc.locationKey === selectedKey ? '#eef4ff' : 'transparent',
              borderLeft: loc.locationKey === selectedKey ? '3px solid #0070f3' : '3px solid transparent',
            }}
          >
            <span
              style={{ flex: 1, fontSize: '0.78rem', color: '#222', cursor: 'pointer', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}
              title={loc.description}
              onClick={() => onSelect(loc.locationKey)}
            >
              {loc.description}
            </span>
            <button onClick={() => openEdit(loc)} title="Edit" style={rowIconBtn}>✏️</button>
            <button onClick={() => void deleteLocation(loc.locationKey)} title="Delete" style={rowIconBtn}>🗑️</button>
          </div>
        ))}
        {visible.length === 0 && (
          <p style={{ fontSize: '0.75rem', color: '#888', margin: '0.5rem 0' }}>No matching locations</p>
        )}
      </div>
    </div>
  )
}

// ── Small helper component for labeled form rows ─────────────────────────

function FormRow({ label, hint, children }: { label: string; hint?: string; children: React.ReactNode }) {
  return (
    <div style={{ marginBottom: '0.35rem' }}>
      <label style={{ display: 'block', fontSize: '0.72rem', color: '#555', marginBottom: 2 }}>
        {label}{hint ? <span style={{ color: '#888', fontWeight: 400 }}> ({hint})</span> : null}
      </label>
      {children}
    </div>
  )
}

// ── Styles ────────────────────────────────────────────────────────────────

const inputStyle: React.CSSProperties = {
  width: '100%', padding: '0.25rem 0.4rem',
  fontSize: '0.78rem', border: '1px solid #ccc', borderRadius: 4,
  boxSizing: 'border-box', background: '#fff', color: '#111',
}

const formBoxStyle: React.CSSProperties = {
  background: '#fff', border: '1px solid #ddd', borderRadius: 6,
  padding: '0.6rem 0.7rem', marginBottom: '0.5rem',
}

const saveBtnStyle: React.CSSProperties = {
  flex: 1, padding: '0.3rem', fontSize: '0.78rem',
  background: '#0070f3', color: '#fff', border: 'none', borderRadius: 4, cursor: 'pointer',
}

const cancelBtnStyle: React.CSSProperties = {
  flex: 1, padding: '0.3rem', fontSize: '0.78rem',
  background: '#e4e4e7', color: '#333', border: 'none', borderRadius: 4, cursor: 'pointer',
}

function iconBtnStyle(color: string): React.CSSProperties {
  return {
    padding: '0.25rem 0.5rem', fontSize: '0.85rem', fontWeight: 700,
    background: color, color: '#fff', border: 'none', borderRadius: 4,
    cursor: 'pointer', flexShrink: 0,
  }
}

const rowIconBtn: React.CSSProperties = {
  background: 'none', border: 'none', cursor: 'pointer',
  fontSize: '0.75rem', padding: '0 0.1rem', flexShrink: 0,
}
