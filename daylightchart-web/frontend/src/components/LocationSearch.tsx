import { useEffect, useRef, useState } from 'react'
import type { LocationDto } from '../types'

interface Props {
  locations: LocationDto[]       // full sorted list from the server
  selectedKey: string            // locationKey of the currently selected location
  onSelect: (key: string) => void
}

// ── Filter logic ──────────────────────────────────────────────────────────
// Decide which items to show in the dropdown:
//   • empty query      → show everything
//   • partial match    → show only matching items
//   • exact match      → show everything (user already found what they want)
//   • no match at all  → show everything (so user can browse the full list)
function getFilteredLocations(query: string, locations: LocationDto[]): LocationDto[] {
  const lower = query.trim().toLowerCase()
  if (lower === '') return locations

  const matches = locations.filter((l) => l.description.toLowerCase().includes(lower))

  const isExactMatch = matches.length === 1 && matches[0].description.toLowerCase() === lower
  const isNoMatch = matches.length === 0

  return isExactMatch || isNoMatch ? locations : matches
}

// ── Component ─────────────────────────────────────────────────────────────
export function LocationSearch({ locations, selectedKey, onSelect }: Props) {
  // Text currently shown in the input box
  const [query, setQuery] = useState('')
  // Whether the dropdown is visible
  const [isOpen, setIsOpen] = useState(false)
  // Which dropdown item is highlighted (-1 = none)
  const [activeIndex, setActiveIndex] = useState(-1)

  const inputRef = useRef<HTMLInputElement>(null)
  const listRef = useRef<HTMLUListElement>(null)

  const selected = locations.find((l) => l.locationKey === selectedKey)
  const filtered = getFilteredLocations(query, locations)

  // Keep the input text in sync with the selected location.
  // This fires on initial load and after an upload resets the selection.
  useEffect(() => {
    if (selected) setQuery(selected.description)
  }, [selected])

  // ── Event handlers ───────────────────────────────────────────────────────

  function selectLocation(loc: LocationDto) {
    onSelect(loc.locationKey)
    setQuery(loc.description)
    setIsOpen(false)
    setActiveIndex(-1)
    inputRef.current?.blur()
  }

  function handleKeyDown(e: React.KeyboardEvent<HTMLInputElement>) {
    if (e.key === 'ArrowDown') {
      e.preventDefault()
      if (!isOpen) { setIsOpen(true); return }
      const next = Math.min(activeIndex + 1, filtered.length - 1)
      setActiveIndex(next)
      scrollItemIntoView(next)
    } else if (e.key === 'ArrowUp') {
      e.preventDefault()
      const prev = Math.max(activeIndex - 1, 0)
      setActiveIndex(prev)
      scrollItemIntoView(prev)
    } else if (e.key === 'Enter' && activeIndex >= 0 && filtered[activeIndex]) {
      e.preventDefault()
      selectLocation(filtered[activeIndex])
    } else if (e.key === 'Escape') {
      setIsOpen(false)
      setQuery(selected?.description ?? '')
    }
  }

  function scrollItemIntoView(index: number) {
    const item = listRef.current?.children[index] as HTMLElement | undefined
    item?.scrollIntoView({ block: 'nearest' })
  }

  // ── Render ────────────────────────────────────────────────────────────────
  return (
    <div style={{ position: 'relative' }}>
      <label htmlFor="location-input" style={labelStyle}>Location</label>

      {/* Input with a search icon overlaid on the left */}
      <div style={{ position: 'relative' }}>
        <span style={iconStyle}>🔍</span>
        <input
          ref={inputRef}
          id="location-input"
          type="text"
          value={query}
          placeholder="Search locations…"
          autoComplete="off"
          spellCheck={false}
          style={inputStyle}
          // ARIA attributes tell screen-readers this is a combobox with a popup list
          role="combobox"
          aria-autocomplete="list"
          aria-expanded={isOpen}
          aria-controls="location-list"
          aria-activedescendant={activeIndex >= 0 ? `location-item-${activeIndex}` : undefined}
          onChange={(e) => { setQuery(e.target.value); setIsOpen(true); setActiveIndex(-1) }}
          onFocus={() => setIsOpen(true)}
          // Small delay lets a mousedown on a list item register before the input loses focus
          onBlur={() => setTimeout(() => setIsOpen(false), 150)}
          onKeyDown={handleKeyDown}
        />
      </div>

      {/* Dropdown list */}
      {isOpen && filtered.length > 0 && (
        <ul ref={listRef} id="location-list" role="listbox" style={listStyle}>
          {filtered.map((loc, i) => (
            <li
              key={loc.locationKey}
              id={`location-item-${i}`}
              role="option"
              aria-selected={loc.locationKey === selectedKey}
              style={itemStyle(i === activeIndex, loc.locationKey === selectedKey)}
              // mouseDown fires before the input's onBlur, so the click registers correctly
              onMouseDown={() => selectLocation(loc)}
              onMouseEnter={() => setActiveIndex(i)}
            >
              {loc.description}
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}

// ── Styles ────────────────────────────────────────────────────────────────

const labelStyle: React.CSSProperties = {
  display: 'block',
  fontSize: '0.7rem',
  fontWeight: 600,
  letterSpacing: '0.07em',
  textTransform: 'uppercase',
  marginBottom: '0.35rem',
  color: '#666',
}

const iconStyle: React.CSSProperties = {
  position: 'absolute',
  left: '0.5rem',
  top: '50%',
  transform: 'translateY(-50%)',
  color: '#999',
  fontSize: '0.8rem',
  pointerEvents: 'none',
  userSelect: 'none',
}

const inputStyle: React.CSSProperties = {
  width: '100%',
  padding: '0.45rem 0.5rem 0.45rem 1.75rem',
  fontSize: '0.875rem',
  border: '1.5px solid #bbb',
  borderRadius: 6,
  boxSizing: 'border-box',
  outline: 'none',
  background: '#fff',
  color: '#111',
}

const listStyle: React.CSSProperties = {
  position: 'absolute',
  top: '100%',
  left: 0,
  right: 0,
  maxHeight: 280,
  overflowY: 'auto',
  margin: '2px 0 0',
  padding: 0,
  listStyle: 'none',
  background: '#fff',
  border: '1.5px solid #bbb',
  borderRadius: 6,
  boxShadow: '0 4px 12px rgba(0,0,0,0.12)',
  zIndex: 100,
}

function itemStyle(isActive: boolean, isSelected: boolean): React.CSSProperties {
  return {
    padding: '0.4rem 0.65rem',
    fontSize: '0.875rem',
    cursor: 'pointer',
    background: isActive ? '#0070f3' : isSelected ? '#eef4ff' : '#fff',
    color: isActive ? '#fff' : '#111',
    borderLeft: isSelected && !isActive ? '3px solid #0070f3' : '3px solid transparent',
  }
}

