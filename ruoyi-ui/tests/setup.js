import Vue from 'vue'
import ElementUI from 'element-ui'

// 注册 Element UI（用于组件测试）
Vue.use(ElementUI)

// 全局 Mock localStorage
const localStorageMock = {
  getItem: vi.fn((key) => {
    if (key === 'Admin-Token') return 'test-token'
    return null
  }),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn()
}
global.localStorage = localStorageMock

// 全局 Mock sessionStorage
global.sessionStorage = localStorageMock

// Mock window.location
global.window = {
  ...global.window,
  location: {
    href: 'http://localhost/'
  }
}

// 导出用于组件测试的工具
export function createTestVue() {
  const localVue = Vue.extend()
  return localVue
}

// 导出常用的 mount options
export const mountOptions = {
  localVue: Vue,
  mocks: {
    $route: {
      path: '/',
      params: {},
      query: {}
    },
    $router: {
      push: vi.fn(),
      replace: vi.fn(),
      go: vi.fn()
    }
  }
}