import { afterAll, afterEach, beforeAll, vi } from 'vitest'
import { server } from './msw'

beforeAll(() => {
  vi.stubGlobal(
    'matchMedia',
    vi.fn().mockImplementation((query: string) => ({
      matches: false,
      media: query,
      onchange: null,
      addEventListener: vi.fn(),
      removeEventListener: vi.fn(),
      dispatchEvent: vi.fn(),
    })),
  )

  server.listen({ onUnhandledRequest: 'error' })
})

afterEach(() => {
  server.resetHandlers()
  localStorage.clear()
})

afterAll(() => {
  server.close()
  vi.unstubAllGlobals()
})
