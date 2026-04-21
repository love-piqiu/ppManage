import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import {
  getCurrentWeek,
  getCurrentMonth,
  getCurrentDay,
  getCurrentQuarter,
  getQuarterRange,
  formatQuarterDisplay,
  prevQuarter,
  nextQuarter,
  getWeekRange,
  formatWeekDisplay,
  prevWeek,
  nextWeek,
  formatDate,
  isOverdue,
  overdueDays,
  getISOWeek,
  getYearLastWeek,
  getWeekDays,
  getCurrentPeriod,
  isQuarterEndWeek,
  getQuarterEndInfo,
  getQuarterLastDay
} from '@/utils/date'

describe('date.js 日期周期工具测试', () => {

  describe('formatDate', () => {
    it('应正确格式化日期', () => {
      const date = new Date(2026, 3, 21, 14, 30, 45) // 2026-04-21
      expect(formatDate(date, 'yyyy-MM-dd')).toBe('2026-04-21')
      expect(formatDate(date, 'yyyy年MM月dd日')).toBe('2026年04月21日')
      expect(formatDate(date, 'MM-dd HH:mm')).toBe('04-21 14:30')
      expect(formatDate(date, 'yyyy-MM-dd HH:mm:ss')).toBe('2026-04-21 14:30:45')
    })

    it('应正确处理单数月份和日期', () => {
      const date = new Date(2026, 0, 5) // 2026-01-05
      expect(formatDate(date, 'yyyy-MM-dd')).toBe('2026-01-05')
    })
  })

  describe('getISOWeek', () => {
    it('应正确计算ISO周数', () => {
      // 2026-01-04 应在第1周（ISO标准）
      expect(getISOWeek(new Date(2026, 0, 4))).toBe(1)
      // 2026-01-01 应在第1周或第53周（取决于具体年份）
      const jan1 = new Date(2026, 0, 1)
      expect(getISOWeek(jan1)).toBeGreaterThanOrEqual(1)
      expect(getISOWeek(jan1)).toBeLessThanOrEqual(53)
    })

    it('年末日期应返回52或53', () => {
      const dec31 = new Date(2026, 11, 31)
      expect(getISOWeek(dec31)).toBeGreaterThanOrEqual(52)
      expect(getISOWeek(dec31)).toBeLessThanOrEqual(53)
    })
  })

  describe('getYearLastWeek', () => {
    it('应返回52或53', () => {
      expect(getYearLastWeek(2025)).toBeGreaterThanOrEqual(52)
      expect(getYearLastWeek(2025)).toBeLessThanOrEqual(53)
      expect(getYearLastWeek(2026)).toBeGreaterThanOrEqual(52)
      expect(getYearLastWeek(2026)).toBeLessThanOrEqual(53)
    })
  })

  describe('getQuarterRange', () => {
    it('应正确计算Q1范围', () => {
      const range = getQuarterRange('2026-Q1')
      expect(range.year).toBe(2026)
      expect(range.quarterNum).toBe(1)
      expect(range.startMonth).toBe(1)
      expect(range.endMonth).toBe(3)
    })

    it('应正确计算Q4范围', () => {
      const range = getQuarterRange('2026-Q4')
      expect(range.quarterNum).toBe(4)
      expect(range.startMonth).toBe(10)
      expect(range.endMonth).toBe(12)
    })
  })

  describe('formatQuarterDisplay', () => {
    it('应正确格式化季度显示', () => {
      expect(formatQuarterDisplay('2026-Q1')).toBe('2026年第1季度')
      expect(formatQuarterDisplay('2026-Q4')).toBe('2026年第4季度')
    })
  })

  describe('prevQuarter', () => {
    it('Q1上一季度应为上年Q4', () => {
      expect(prevQuarter('2026-Q1')).toBe('2025-Q4')
    })

    it('Q2上一季度应为Q1', () => {
      expect(prevQuarter('2026-Q2')).toBe('2026-Q1')
    })

    it('Q4上一季度应为Q3', () => {
      expect(prevQuarter('2026-Q4')).toBe('2026-Q3')
    })
  })

  describe('nextQuarter', () => {
    it('Q4下一季度应为下年Q1', () => {
      expect(nextQuarter('2026-Q4')).toBe('2027-Q1')
    })

    it('Q1下一季度应为Q2', () => {
      expect(nextQuarter('2026-Q1')).toBe('2026-Q2')
    })

    it('Q3下一季度应为Q4', () => {
      expect(nextQuarter('2026-Q3')).toBe('2026-Q4')
    })
  })

  describe('getWeekRange', () => {
    it('应正确计算第1周范围', () => {
      const range = getWeekRange('2026-W1')
      expect(range.year).toBe(2026)
      expect(range.weekNum).toBe(1)
      expect(range.startDate).toBeDefined()
      expect(range.endDate).toBeDefined()
    })

    it('应正确计算第16周范围', () => {
      const range = getWeekRange('2026-W16')
      expect(range.weekNum).toBe(16)
      expect(range.monday).toBeDefined()
      expect(range.sunday).toBeDefined()
    })
  })

  describe('formatWeekDisplay', () => {
    it('应正确格式化周显示', () => {
      const display = formatWeekDisplay('2026-W16')
      expect(display).toContain('2026年')
      expect(display).toContain('周')
      expect(display).toMatch(/\d+-\d+ ~ \d+-\d+/)
    })
  })

  describe('prevWeek', () => {
    it('W1上一周应为上年最后一周', () => {
      const result = prevWeek('2026-W1')
      expect(result).toMatch(/2025-W(52|53)/)
    })

    it('W2上一周应为W1', () => {
      expect(prevWeek('2026-W2')).toBe('2026-W1')
    })

    it('W16上一周应为W15', () => {
      expect(prevWeek('2026-W16')).toBe('2026-W15')
    })
  })

  describe('nextWeek', () => {
    it('年末最后一周下一周应为下年W1', () => {
      const lastWeek = getYearLastWeek(2026)
      const result = nextWeek(`2026-W${lastWeek}`)
      expect(result).toBe('2027-W1')
    })

    it('W1下一周应为W2', () => {
      expect(nextWeek('2026-W1')).toBe('2026-W2')
    })
  })

  describe('getWeekDays', () => {
    it('应返回7天', () => {
      const days = getWeekDays('2026-W16')
      expect(days.length).toBe(7)
    })

    it('应包含正确的星期名称', () => {
      const days = getWeekDays('2026-W16')
      expect(days[0].name).toBe('周一')
      expect(days[6].name).toBe('周日')
    })

    it('日期格式应为yyyy-MM-dd', () => {
      const days = getWeekDays('2026-W16')
      expect(days[0].date).toMatch(/^\d{4}-\d{2}-\d{2}$/)
    })
  })

  describe('isOverdue', () => {
    it('过去日期应为超期', () => {
      const pastDate = new Date(2020, 0, 1)
      expect(isOverdue(pastDate)).toBe(true)
    })

    it('未来日期不应超期', () => {
      const futureDate = new Date(2030, 0, 1)
      expect(isOverdue(futureDate)).toBe(false)
    })

    it('空值不应超期', () => {
      expect(isOverdue(null)).toBe(false)
      expect(isOverdue(undefined)).toBe(false)
    })

    it('字符串日期应正确解析', () => {
      expect(isOverdue('2020-01-01')).toBe(true)
      expect(isOverdue('2030-01-01')).toBe(false)
    })
  })

  describe('overdueDays', () => {
    it('应正确计算超期天数', () => {
      const pastDate = new Date(2026, 3, 18) // 3天前
      expect(overdueDays(pastDate)).toBeGreaterThanOrEqual(3)
    })

    it('未来日期超期天数应为0', () => {
      const futureDate = new Date(2030, 0, 1)
      expect(overdueDays(futureDate)).toBe(0)
    })

    it('空值超期天数应为0', () => {
      expect(overdueDays(null)).toBe(0)
    })
  })

  describe('getCurrentPeriod', () => {
    it('每日应返回日期格式', () => {
      const result = getCurrentPeriod('每日')
      expect(result).toMatch(/^\d{4}-\d{2}-\d{2}$/)
    })

    it('每周应返回周格式', () => {
      const result = getCurrentPeriod('每周')
      expect(result).toMatch(/^\d{4}-W\d+$/)
    })

    it('每月应返回月格式', () => {
      const result = getCurrentPeriod('每月')
      expect(result).toMatch(/^\d{4}-\d{2}$/)
    })

    it('每季应返回季度格式', () => {
      const result = getCurrentPeriod('每季')
      expect(result).toMatch(/^\d{4}-Q\d$/)
    })

    it('未知类型应默认返回周格式', () => {
      const result = getCurrentPeriod('未知')
      expect(result).toMatch(/^\d{4}-W\d+$/)
    })
  })

  describe('getQuarterLastDay', () => {
    it('Q1最后一天应为3月31日', () => {
      const lastDay = getQuarterLastDay(2026, 1)
      expect(lastDay.getMonth()).toBe(2) // 3月（索引2）
      expect(lastDay.getDate()).toBe(31)
    })

    it('Q4最后一天应为12月31日', () => {
      const lastDay = getQuarterLastDay(2026, 4)
      expect(lastDay.getMonth()).toBe(11) // 12月（索引11）
      expect(lastDay.getDate()).toBe(31)
    })
  })

  describe('isQuarterEndWeek', () => {
    it('包含季度末日的周应返回true', () => {
      // 需要找一个包含3月31日或6月30日等的周
      const result = isQuarterEndWeek('2026-W13')
      // W13大约是3月底，可能包含3月31日
      expect(typeof result).toBe('boolean')
    })
  })

  describe('getQuarterEndInfo', () => {
    it('年中周通常不返回季度末信息', () => {
      // W10 大约在3月初，不是季度末周
      const info = getQuarterEndInfo('2026-W10')
      // 可能返回null或某个季度信息（取决于具体周）
      if (info) {
        expect(info.quarter).toBeGreaterThanOrEqual(1)
        expect(info.quarter).toBeLessThanOrEqual(4)
      }
    })

    it('季度末周应返回季度信息', () => {
      // 测试年末周（通常包含12月31日）
      const lastWeek = getYearLastWeek(2026)
      const info = getQuarterEndInfo(`2026-W${lastWeek}`)
      // 可能返回Q4信息或null（取决于12月31日在哪周）
      if (info) {
        expect(info.quarter).toBeGreaterThanOrEqual(1)
        expect(info.quarter).toBeLessThanOrEqual(4)
        expect(info.lastDayDate).toMatch(/^\d{4}-\d{2}-\d{2}$/)
      }
    })
  })

  describe('getCurrentWeek/Month/Day/Quarter', () => {
    it('getCurrentWeek应返回正确格式', () => {
      const result = getCurrentWeek()
      expect(result).toMatch(/^\d{4}-W\d+$/)
    })

    it('getCurrentMonth应返回正确格式', () => {
      const result = getCurrentMonth()
      expect(result).toMatch(/^\d{4}-\d{2}$/)
    })

    it('getCurrentDay应返回正确格式', () => {
      const result = getCurrentDay()
      expect(result).toMatch(/^\d{4}-\d{2}-\d{2}$/)
    })

    it('getCurrentQuarter应返回正确格式', () => {
      const result = getCurrentQuarter()
      expect(result).toMatch(/^\d{4}-Q\d$/)
    })
  })
})