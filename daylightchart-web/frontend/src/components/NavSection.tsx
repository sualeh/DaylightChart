import { useState } from 'react'

interface Props {
  title: string
  defaultOpen?: boolean
  children: React.ReactNode
}

// A collapsible section for the left sidebar navigation.
// Clicking the header toggles the content open or closed.
export function NavSection({ title, defaultOpen = false, children }: Props) {
  const [open, setOpen] = useState(defaultOpen)

  return (
    <div style={{ borderTop: '1px solid #e4e4e7', paddingTop: '0.75rem' }}>
      <button
        onClick={() => setOpen((o) => !o)}
        style={{
          display: 'flex', alignItems: 'center', justifyContent: 'space-between',
          width: '100%', background: 'none', border: 'none', cursor: 'pointer',
          padding: '0 0 0.4rem', marginBottom: open ? '0.5rem' : 0,
        }}
      >
        <span style={{
          fontSize: '0.7rem', fontWeight: 600, letterSpacing: '0.07em',
          textTransform: 'uppercase', color: '#666',
        }}>
          {title}
        </span>
        <span style={{ fontSize: '0.65rem', color: '#999' }}>{open ? '▲' : '▼'}</span>
      </button>
      {open && children}
    </div>
  )
}
