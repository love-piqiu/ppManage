/**
 * 日期周期计算工具函数
 */

/**
 * 获取当前周标识
 * @returns {string} 格式：2026-W15
 */
export function getCurrentWeek() {
  const now = new Date()
  const year = now.getFullYear()
  const weekNum = getISOWeek(now)
  return `${year}-W${weekNum}`
}

/**
 * 获取当前月标识
 * @returns {string} 格式：2026-04
 */
export function getCurrentMonth() {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  return `${year}-${month}`
}

/**
 * 获取当前日标识
 * @returns {string} 格式：2026-04-11
 */
export function getCurrentDay() {
  const now = new Date()
  return formatDate(now, 'yyyy-MM-dd')
}

/**
 * 获取当前季度标识
 * @returns {string} 格式：2026-Q1
 */
export function getCurrentQuarter() {
  const now = new Date()
  const year = now.getFullYear()
  const month = now.getMonth() + 1
  const quarterNum = Math.ceil(month / 3)
  return `${year}-Q${quarterNum}`
}

/**
 * 获取季度范围
 * @param {string} period 格式：2026-Q1
 * @returns {object} { year, quarterNum, startDate, endDate }
 */
export function getQuarterRange(period) {
  const [year, quarterStr] = period.split('-Q')
  const quarterNum = parseInt(quarterStr)
  const startMonth = (quarterNum - 1) * 3 + 1
  const endMonth = startMonth + 2

  const startDate = new Date(year, startMonth - 1, 1)
  const endDate = new Date(year, endMonth - 1, new Date(year, endMonth, 0).getDate())

  return {
    year: parseInt(year),
    quarterNum,
    startMonth,
    endMonth,
    startDate,
    endDate
  }
}

/**
 * 格式化季度显示文本
 * @param {string} period 格式：2026-Q1
 * @returns {string} 格式：2026年第1季度
 */
export function formatQuarterDisplay(period) {
  const range = getQuarterRange(period)
  return `${range.year}年第${range.quarterNum}季度`
}

/**
 * 获取上一季度
 * @param {string} period 格式：2026-Q1
 * @returns {string}
 */
export function prevQuarter(period) {
  const [year, quarterStr] = period.split('-Q')
  const quarterNum = parseInt(quarterStr)
  const yearNum = parseInt(year)

  if (quarterNum === 1) {
    return `${yearNum - 1}-Q4`
  }
  return `${year}-Q${quarterNum - 1}`
}

/**
 * 获取下一季度
 * @param {string} period 格式：2026-Q1
 * @returns {string}
 */
export function nextQuarter(period) {
  const [year, quarterStr] = period.split('-Q')
  const quarterNum = parseInt(quarterStr)
  const yearNum = parseInt(year)

  if (quarterNum === 4) {
    return `${yearNum + 1}-Q1`
  }
  return `${year}-Q${quarterNum + 1}`
}

/**
 * 根据周期类型获取当前周期标识
 * @param {string} cycle 每日/每周/每月/每季
 * @returns {string}
 */
export function getCurrentPeriod(cycle) {
  switch (cycle) {
    case '每日': return getCurrentDay()
    case '每周': return getCurrentWeek()
    case '每月': return getCurrentMonth()
    case '每季': return getCurrentQuarter()
    default: return getCurrentWeek()
  }
}

/**
 * 获取一周7天的日期列表
 * @param {string} period 格式：2026-W15
 * @returns {object} { year, month, weekNum, startDate, endDate }
 */
export function getWeekRange(period) {
  const [year, weekStr] = period.split('-W')
  const weekNum = parseInt(weekStr)

  // ISO周计算：找到该年第1周的周一
  const jan4 = new Date(year, 0, 4) // 1月4日一定在第1周
  const jan4Day = jan4.getDay() || 7 // 周日=7
  const week1Monday = new Date(jan4.getTime() - (jan4Day - 1) * 24 * 60 * 60 * 1000)

  // 计算目标周的周一
  const targetMonday = new Date(week1Monday.getTime() + (weekNum - 1) * 7 * 24 * 60 * 60 * 1000)
  const targetSunday = new Date(targetMonday.getTime() + 6 * 24 * 60 * 60 * 1000)

  return {
    year: parseInt(year),
    month: targetMonday.getMonth() + 1,
    weekNum: weekNum,
    startDate: formatDate(targetMonday, 'MM-dd'),
    endDate: formatDate(targetSunday, 'MM-dd'),
    monday: targetMonday,
    sunday: targetSunday
  }
}

/**
 * 格式化周的显示文本
 * @param {string} period 格式：2026-W15
 * @returns {string} 格式：2026年4月第2周 (04-06 ~ 04-12)
 */
export function formatWeekDisplay(period) {
  const range = getWeekRange(period)
  return `${range.year}年${range.month}月第${range.weekNum}周 (${range.startDate} ~ ${range.endDate})`
}

/**
 * 获取上一周
 * @param {string} period 格式：2026-W15
 * @returns {string}
 */
export function prevWeek(period) {
  const [year, weekStr] = period.split('-W')
  const weekNum = parseInt(weekStr)

  if (weekNum === 1) {
    // 跨年：上年最后一周（通常是52或53周）
    const prevYear = parseInt(year) - 1
    const lastWeek = getYearLastWeek(prevYear)
    return `${prevYear}-W${lastWeek}`
  }
  return `${year}-W${weekNum - 1}`
}

/**
 * 获取下一周
 * @param {string} period 格式：2026-W15
 * @returns {string}
 */
export function nextWeek(period) {
  const [year, weekStr] = period.split('-W')
  const weekNum = parseInt(weekStr)
  const yearNum = parseInt(year)

  const lastWeek = getYearLastWeek(yearNum)
  if (weekNum === lastWeek) {
    // 跨年：下年第1周
    return `${yearNum + 1}-W1`
  }
  return `${year}-W${weekNum + 1}`
}

/**
 * 获取某年最后一周数
 * @param {number} year
 * @returns {number} 52 或 53
 */
export function getYearLastWeek(year) {
  const dec31 = new Date(year, 11, 31)
  const dec31Week = getISOWeek(dec31)
  return dec31Week
}

/**
 * 获取日期的ISO周数
 * @param {Date} date
 * @returns {number}
 */
export function getISOWeek(date) {
  const jan4 = new Date(date.getFullYear(), 0, 4)
  const jan4Day = jan4.getDay() || 7
  const week1Monday = new Date(jan4.getTime() - (jan4Day - 1) * 24 * 60 * 60 * 1000)
  const days = Math.floor((date - week1Monday) / (24 * 60 * 60 * 1000))
  return Math.floor(days / 7) + 1
}

/**
 * 格式化日期
 * @param {Date} date
 * @param {string} format
 * @returns {string}
 */
export function formatDate(date, format) {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')

  return format
    .replace('yyyy', year)
    .replace('MM', month)
    .replace('dd', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

/**
 * 判断日期是否超期
 * @param {string|Date} deadline
 * @returns {boolean}
 */
export function isOverdue(deadline) {
  if (!deadline) return false
  const deadlineDate = new Date(deadline)
  const now = new Date()
  return deadlineDate < now
}

/**
 * 计算超期天数
 * @param {string|Date} deadline
 * @returns {number}
 */
export function overdueDays(deadline) {
  if (!deadline) return 0
  const deadlineDate = new Date(deadline)
  const now = new Date()
  const diff = Math.floor((now - deadlineDate) / (24 * 60 * 60 * 1000))
  return diff > 0 ? diff : 0
}

/**
 * 获取一周7天的日期列表
 * @param {string} period 格式：2026-W15
 * @returns {Array} [{ date, name, shortDate }]
 */
export function getWeekDays(period) {
  const range = getWeekRange(period)
  const dayNames = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  const days = []

  for (let i = 0; i < 7; i++) {
    const date = new Date(range.monday.getTime() + i * 24 * 60 * 60 * 1000)
    const dateStr = formatDate(date, 'yyyy-MM-dd')
    days.push({
      date: dateStr,
      name: dayNames[i],
      shortDate: formatDate(date, 'MM-dd')
    })
  }

  return days
}

/**
 * 获取当前季度的最后一天
 * @param {number} year 年份
 * @param {number} quarter 季度（1-4）
 * @returns {Date}
 */
export function getQuarterLastDay(year, quarter) {
  const endMonth = quarter * 3 // 3, 6, 9, 12
  const lastDay = new Date(year, endMonth, 0).getDate() // 该月最后一天
  return new Date(year, endMonth - 1, lastDay)
}

/**
 * 判断某周是否是季度末周（包含季度最后一天）
 * @param {string} period 格式：2026-W15
 * @returns {boolean}
 */
export function isQuarterEndWeek(period) {
  const range = getWeekRange(period)
  const year = range.year

  // 检查该周的每一天是否是某季度的最后一天
  for (let quarter = 1; quarter <= 4; quarter++) {
    const lastDay = getQuarterLastDay(year, quarter)
    // 检查季度最后一天是否在该周范围内
    if (lastDay >= range.monday && lastDay <= range.sunday) {
      return true
    }
  }

  // 检查跨年情况：12月31日可能在上年最后一周
  const dec31PrevYear = new Date(year - 1, 11, 31)
  if (dec31PrevYear >= range.monday && dec31PrevYear <= range.sunday) {
    return true
  }

  return false
}

/**
 * 获取某周包含的季度最后一天信息
 * @param {string} period 格式：2026-W15
 * @returns {object|null} { quarter, lastDayDate } 或 null
 */
export function getQuarterEndInfo(period) {
  const range = getWeekRange(period)
  const year = range.year

  for (let quarter = 1; quarter <= 4; quarter++) {
    const lastDay = getQuarterLastDay(year, quarter)
    if (lastDay >= range.monday && lastDay <= range.sunday) {
      return {
        quarter,
        year,
        lastDayDate: formatDate(lastDay, 'yyyy-MM-dd')
      }
    }
  }

  // 检查跨年情况
  const dec31PrevYear = new Date(year - 1, 11, 31)
  if (dec31PrevYear >= range.monday && dec31PrevYear <= range.sunday) {
    return {
      quarter: 4,
      year: year - 1,
      lastDayDate: formatDate(dec31PrevYear, 'yyyy-MM-dd')
    }
  }

  return null
}