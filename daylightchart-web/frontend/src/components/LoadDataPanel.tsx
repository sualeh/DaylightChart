import { useRef, useState } from 'react'

interface UploadResult {
  filename: string
  added: number
  error?: string
}

interface Props {
  totalLocations: number
  onLoaded: () => void
}

export function LoadDataPanel({ totalLocations, onLoaded }: Props) {
  const inputRef = useRef<HTMLInputElement>(null)
  const [status, setStatus] = useState<UploadResult | null>(null)
  const [loading, setLoading] = useState(false)
  const [dragging, setDragging] = useState(false)

  async function upload(file: File) {
    setLoading(true)
    setStatus(null)
    const form = new FormData()
    form.append('file', file)
    try {
      const res = await fetch('/api/locations/upload', { method: 'POST', body: form })
      const data = (await res.json()) as UploadResult
      setStatus(res.ok ? data : { filename: file.name, added: 0, error: data.error ?? `HTTP ${res.status}` })
      onLoaded()
    } catch (e: unknown) {
      setStatus({ filename: file.name, added: 0, error: String(e) })
    } finally {
      setLoading(false)
      if (inputRef.current) inputRef.current.value = ''
    }
  }

  function handleFiles(files: FileList | null) {
    const file = files?.[0]
    if (file) void upload(file)
  }

  const dropZoneStyle: React.CSSProperties = {
    border: `2px dashed ${dragging ? '#0070f3' : '#ccc'}`,
    borderRadius: 6,
    padding: '0.75rem 0.5rem',
    textAlign: 'center',
    cursor: 'pointer',
    background: dragging ? '#eef4ff' : '#fafafa',
    fontSize: '0.8rem',
    color: '#555',
    transition: 'all 0.15s',
  }

  return (
    <div>
      <div
        style={{
          fontSize: '0.7rem',
          fontWeight: 600,
          letterSpacing: '0.07em',
          textTransform: 'uppercase',
          marginBottom: '0.5rem',
          color: '#666',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
        }}
      >
        <span>Load Data</span>
        <span
          style={{
            background: '#e4e4e7',
            borderRadius: 10,
            padding: '0.1rem 0.45rem',
            fontSize: '0.7rem',
            fontWeight: 700,
            color: '#333',
          }}
          title="Total locations loaded"
        >
          {totalLocations.toLocaleString()}
        </span>
      </div>

      {/* Drop zone */}
      <div
        style={dropZoneStyle}
        onClick={() => inputRef.current?.click()}
        onDragOver={(e) => { e.preventDefault(); setDragging(true) }}
        onDragLeave={() => setDragging(false)}
        onDrop={(e) => {
          e.preventDefault()
          setDragging(false)
          handleFiles(e.dataTransfer.files)
        }}
      >
        {loading ? '⏳ Loading…' : '📂 Drop GNS / GNIS zip here or click'}
      </div>
      <input
        ref={inputRef}
        type="file"
        accept=".zip"
        style={{ display: 'none' }}
        onChange={(e) => handleFiles(e.target.files)}
      />

      {/* Status message */}
      {status && (
        <p
          style={{
            margin: '0.5rem 0 0',
            fontSize: '0.75rem',
            color: status.error ? '#c00' : '#197a1d',
          }}
        >
          {status.error
            ? `Error: ${status.error}`
            : `✓ ${status.added.toLocaleString()} new locations from ${status.filename}`}
        </p>
      )}
    </div>
  )
}
