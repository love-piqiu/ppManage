import { describe, it, expect } from 'vitest'
import {
  isPathMatch,
  isEmpty,
  isHttp,
  isExternal,
  validUsername,
  validURL,
  validLowerCase,
  validUpperCase,
  validAlphabets,
  validEmail,
  isString,
  isArray
} from '@/utils/validate'

describe('validate.js 工具函数测试', () => {

  describe('isEmpty', () => {
    it('应正确识别空字符串', () => {
      expect(isEmpty('')).toBe(true)
      expect(isEmpty(null)).toBe(true)
      expect(isEmpty(undefined)).toBe(true)
      expect(isEmpty('undefined')).toBe(true)
    })

    it('应正确识别非空字符串', () => {
      expect(isEmpty('hello')).toBe(false)
      expect(isEmpty('test value')).toBe(false)
      expect(isEmpty('0')).toBe(false)
    })
  })

  describe('isHttp', () => {
    it('应正确识别 HTTP URL', () => {
      expect(isHttp('http://example.com')).toBe(true)
      expect(isHttp('https://example.com')).toBe(true)
      expect(isHttp('http://localhost:8080/api')).toBe(true)
    })

    it('应正确识别非 HTTP URL', () => {
      expect(isHttp('/api/users')).toBe(false)
      expect(isHttp('ftp://files.com')).toBe(false)
      expect(isHttp('')).toBe(false)
    })
  })

  describe('isExternal', () => {
    it('应正确识别外链', () => {
      expect(isExternal('https://google.com')).toBe(true)
      expect(isExternal('http://example.com')).toBe(true)
      expect(isExternal('mailto:test@example.com')).toBe(true)
      expect(isExternal('tel:+1234567890')).toBe(true)
    })

    it('应正确识别内链', () => {
      expect(isExternal('/dashboard')).toBe(false)
      expect(isExternal('/system/user')).toBe(false)
      expect(isExternal('relative/path')).toBe(false)
    })
  })

  describe('validUsername', () => {
    it('应正确验证有效用户名', () => {
      expect(validUsername('admin')).toBe(true)
      expect(validUsername('editor')).toBe(true)
      expect(validUsername('  admin  ')).toBe(true) // trim 处理
    })

    it('应正确拒绝无效用户名', () => {
      expect(validUsername('user')).toBe(false)
      expect(validUsername('test')).toBe(false)
      expect(validUsername('')).toBe(false)
    })
  })

  describe('validEmail', () => {
    it('应正确验证有效邮箱', () => {
      expect(validEmail('test@example.com')).toBe(true)
      expect(validEmail('user.name@domain.co')).toBe(true)
      expect(validEmail('admin@test.org')).toBe(true)
    })

    it('应正确拒绝无效邮箱', () => {
      expect(validEmail('invalid')).toBe(false)
      expect(validEmail('test@')).toBe(false)
      expect(validEmail('@example.com')).toBe(false)
    })
  })

  describe('validURL', () => {
    it('应正确验证有效 URL', () => {
      expect(validURL('https://example.com')).toBe(true)
      expect(validURL('http://test.org/api')).toBe(true)
      expect(validURL('ftp://files.server.com')).toBe(true)
    })

    it('应正确拒绝无效 URL', () => {
      expect(validURL('invalid')).toBe(false)
      expect(validURL('example.com')).toBe(false)
      expect(validURL('http://localhost:3000')).toBe(false) // localhost 无 TLD
    })
  })

  describe('validLowerCase', () => {
    it('应正确验证小写字母', () => {
      expect(validLowerCase('abc')).toBe(true)
      expect(validLowerCase('test')).toBe(true)
    })

    it('应正确拒绝非小写字母', () => {
      expect(validLowerCase('ABC')).toBe(false)
      expect(validLowerCase('Test')).toBe(false)
      expect(validLowerCase('123')).toBe(false)
    })
  })

  describe('validUpperCase', () => {
    it('应正确验证大写字母', () => {
      expect(validUpperCase('ABC')).toBe(true)
      expect(validUpperCase('TEST')).toBe(true)
    })

    it('应正确拒绝非大写字母', () => {
      expect(validUpperCase('abc')).toBe(false)
      expect(validUpperCase('Test')).toBe(false)
      expect(validUpperCase('123')).toBe(false)
    })
  })

  describe('validAlphabets', () => {
    it('应正确验证字母', () => {
      expect(validAlphabets('abc')).toBe(true)
      expect(validAlphabets('ABC')).toBe(true)
      expect(validAlphabets('Test')).toBe(true)
    })

    it('应正确拒绝非字母', () => {
      expect(validAlphabets('123')).toBe(false)
      expect(validAlphabets('test123')).toBe(false)
    })
  })

  describe('isString', () => {
    it('应正确识别字符串', () => {
      expect(isString('hello')).toBe(true)
      expect(isString('')).toBe(true)
      expect(isString(String('test'))).toBe(true)
    })

    it('应正确识别非字符串', () => {
      expect(isString(123)).toBe(false)
      expect(isString(null)).toBe(false)
      expect(isString(undefined)).toBe(false)
      expect(isString([])).toBe(false)
    })
  })

  describe('isArray', () => {
    it('应正确识别数组', () => {
      expect(isArray([1, 2, 3])).toBe(true)
      expect(isArray([])).toBe(true)
      expect(isArray(['a', 'b'])).toBe(true)
    })

    it('应正确识别非数组', () => {
      expect(isArray('string')).toBe(false)
      expect(isArray(123)).toBe(false)
      expect(isArray({})).toBe(false)
      expect(isArray(null)).toBe(false)
    })
  })

  describe('isPathMatch', () => {
    it('应正确匹配路径', () => {
      expect(isPathMatch('/system/user', '/system/user')).toBe(true)
      expect(isPathMatch('/system/*', '/system/user')).toBe(true)
      expect(isPathMatch('/system/*', '/system/role')).toBe(true)
    })

    it('应正确拒绝不匹配的路径', () => {
      expect(isPathMatch('/system/user', '/system/role')).toBe(false)
      expect(isPathMatch('/system/*', '/other/path')).toBe(false)
    })
  })
})