export interface LocationDto {
  locationKey: string
  city: string
  description: string
  adminCode: string
  adminName: string
  countryCode: string
  countryName: string
  timeZoneId: string
  latitude: number
  longitude: number
}

// Chart display options sent as URL params to GET /api/chart
export interface ChartOptions {
  twilightType: 'CIVIL' | 'NAUTICAL' | 'ASTRONOMICAL'
  orientation: 'STANDARD' | 'CONVENTIONAL' | 'VERTICAL'
  showLegend: boolean
  showTable: boolean
}

export const DEFAULT_CHART_OPTIONS: ChartOptions = {
  twilightType: 'CIVIL',
  orientation: 'STANDARD',
  showLegend: true,
  showTable: false,
}
