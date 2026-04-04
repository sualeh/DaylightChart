// Returns the current browser window width, updating whenever the window is resized.
// Used to switch between the full sidebar and the hamburger menu on small screens.

import { useEffect, useState } from 'react'

export function useWindowWidth(): number {
  const [width, setWidth] = useState(window.innerWidth)

  useEffect(() => {
    const handler = () => setWidth(window.innerWidth)
    window.addEventListener('resize', handler)
    return () => window.removeEventListener('resize', handler)
  }, [])

  return width
}
