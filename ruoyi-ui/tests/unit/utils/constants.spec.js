import { describe, it, expect } from 'vitest'
import {
  PROJECT_STATUS,
  PROJECT_STATUS_OPTIONS,
  PROJECT_TYPE,
  PROJECT_TYPE_OPTIONS,
  SUBORDINATE,
  ISSUE_SEVERITY,
  ISSUE_SEVERITY_OPTIONS,
  ISSUE_STATUS,
  ISSUE_STATUS_OPTIONS,
  RISK_LEVEL,
  RISK_LEVEL_OPTIONS,
  RISK_STATUS,
  RISK_STATUS_OPTIONS,
  RESOURCE_STATUS,
  RESOURCE_STATUS_OPTIONS,
  MILESTONE_WARNING_DAYS,
  MILESTONE_DISPLAY,
  DEFAULT_MILESTONES,
  INVOICE_TYPES_FOR_PROJECT,
  STATUS_CLASS_MAP
} from '@/utils/constants'

describe('constants.js 常量配置测试', () => {

  describe('PROJECT_STATUS', () => {
    it('应包含正确的状态值', () => {
      expect(PROJECT_STATUS.ONGOING).toBe('进行中')
      expect(PROJECT_STATUS.COMPLETED).toBe('已完成')
      expect(PROJECT_STATUS.PAUSED).toBe('暂停')
    })

    it('应有三个状态', () => {
      expect(Object.keys(PROJECT_STATUS).length).toBe(3)
    })
  })

  describe('PROJECT_STATUS_OPTIONS', () => {
    it('应包含全部选项', () => {
      expect(PROJECT_STATUS_OPTIONS.length).toBe(4)
      expect(PROJECT_STATUS_OPTIONS[0].value).toBe('')
      expect(PROJECT_STATUS_OPTIONS[0].label).toBe('全部状态')
    })

    it('各状态选项应匹配常量值', () => {
      expect(PROJECT_STATUS_OPTIONS[1].value).toBe(PROJECT_STATUS.ONGOING)
      expect(PROJECT_STATUS_OPTIONS[2].value).toBe(PROJECT_STATUS.COMPLETED)
      expect(PROJECT_STATUS_OPTIONS[3].value).toBe(PROJECT_STATUS.PAUSED)
    })
  })

  describe('PROJECT_TYPE', () => {
    it('应包含正确的类型值', () => {
      expect(PROJECT_TYPE.PROJECT).toBe('项目')
      expect(PROJECT_TYPE.OUTSOURCE).toBe('外包')
    })
  })

  describe('SUBORDINATE', () => {
    it('应包含正确的下辖标识', () => {
      expect(SUBORDINATE.YES).toBe('是')
      expect(SUBORDINATE.NO).toBe('否')
    })
  })

  describe('ISSUE_SEVERITY', () => {
    it('应包含正确的严重程度值', () => {
      expect(ISSUE_SEVERITY.HIGH).toBe('高')
      expect(ISSUE_SEVERITY.MEDIUM).toBe('中')
      expect(ISSUE_SEVERITY.LOW).toBe('低')
    })
  })

  describe('ISSUE_STATUS', () => {
    it('应包含正确的问题状态值', () => {
      expect(ISSUE_STATUS.PENDING).toBe('待处理')
      expect(ISSUE_STATUS.PROGRESS).toBe('进行中')
      expect(ISSUE_STATUS.RESOLVED).toBe('已解决')
      expect(ISSUE_STATUS.CLOSED).toBe('已关闭')
    })
  })

  describe('RISK_LEVEL', () => {
    it('应包含正确的风险等级值', () => {
      expect(RISK_LEVEL.HIGH).toBe('高')
      expect(RISK_LEVEL.MEDIUM).toBe('中')
      expect(RISK_LEVEL.LOW).toBe('低')
    })
  })

  describe('RISK_STATUS', () => {
    it('应包含正确的风险状态值', () => {
      expect(RISK_STATUS.POTENTIAL).toBe('潜在')
      expect(RISK_STATUS.OCCURRED).toBe('已发生')
      expect(RISK_STATUS.ELIMINATED).toBe('已消除')
    })
  })

  describe('RESOURCE_STATUS', () => {
    it('应包含正确的资源状态值', () => {
      expect(RESOURCE_STATUS.FREE).toBe('空闲')
      expect(RESOURCE_STATUS.SOON_FREE).toBe('即将空闲')
      expect(RESOURCE_STATUS.BUSY).toBe('忙碌')
      expect(RESOURCE_STATUS.PENDING).toBe('待定')
    })
  })

  describe('MILESTONE_WARNING_DAYS', () => {
    it('应设置为 10 天', () => {
      expect(MILESTONE_WARNING_DAYS).toBe(10)
    })
  })

  describe('MILESTONE_DISPLAY', () => {
    it('应包含正确的里程碑显示文本', () => {
      expect(MILESTONE_DISPLAY.PENDING).toBe('待定')
      expect(MILESTONE_DISPLAY.WAITING).toBe('待进行')
      expect(MILESTONE_DISPLAY.COMPLETED).toBe('已完成')
      expect(MILESTONE_DISPLAY.OVERDUE).toBe('已超期')
      expect(MILESTONE_DISPLAY.WARNING).toBe('10天内到期')
    })
  })

  describe('DEFAULT_MILESTONES', () => {
    it('应包含4个默认里程碑', () => {
      expect(DEFAULT_MILESTONES.length).toBe(4)
    })

    it('里程碑应包含正确的字段映射', () => {
      const milestone = DEFAULT_MILESTONES[0]
      expect(milestone.name).toBe('需求')
      expect(milestone.planField).toBe('reqPlanDate')
      expect(milestone.actualField).toBe('reqActualDate')
    })
  })

  describe('INVOICE_TYPES_FOR_PROJECT', () => {
    it('应包含6种开票类型', () => {
      expect(INVOICE_TYPES_FOR_PROJECT.length).toBe(6)
    })

    it('首尾应为首款和尾款', () => {
      expect(INVOICE_TYPES_FOR_PROJECT[0].value).toBe('首款')
      expect(INVOICE_TYPES_FOR_PROJECT[5].value).toBe('尾款')
    })
  })

  describe('STATUS_CLASS_MAP', () => {
    it('应正确映射项目状态到 CSS 类名', () => {
      expect(STATUS_CLASS_MAP.projectStatus[PROJECT_STATUS.ONGOING]).toBe('ongoing')
      expect(STATUS_CLASS_MAP.projectStatus[PROJECT_STATUS.COMPLETED]).toBe('completed')
      expect(STATUS_CLASS_MAP.projectStatus[PROJECT_STATUS.PAUSED]).toBe('paused')
    })

    it('应正确映射问题严重程度到 CSS 类名', () => {
      expect(STATUS_CLASS_MAP.severity[ISSUE_SEVERITY.HIGH]).toBe('danger')
      expect(STATUS_CLASS_MAP.severity[ISSUE_SEVERITY.MEDIUM]).toBe('warning')
      expect(STATUS_CLASS_MAP.severity[ISSUE_SEVERITY.LOW]).toBe('success')
    })

    it('应正确映射风险等级到 CSS 类名', () => {
      expect(STATUS_CLASS_MAP.riskLevel[RISK_LEVEL.HIGH]).toBe('danger')
      expect(STATUS_CLASS_MAP.riskLevel[RISK_LEVEL.MEDIUM]).toBe('warning')
      expect(STATUS_CLASS_MAP.riskLevel[RISK_LEVEL.LOW]).toBe('success')
    })

    it('应正确映射风险状态到 CSS 类名', () => {
      expect(STATUS_CLASS_MAP.riskStatus[RISK_STATUS.POTENTIAL]).toBe('warning')
      expect(STATUS_CLASS_MAP.riskStatus[RISK_STATUS.OCCURRED]).toBe('danger')
      expect(STATUS_CLASS_MAP.riskStatus[RISK_STATUS.ELIMINATED]).toBe('success')
    })

    it('应正确映射资源状态到 CSS 类名', () => {
      expect(STATUS_CLASS_MAP.resourceStatus[RESOURCE_STATUS.FREE]).toBe('free')
      expect(STATUS_CLASS_MAP.resourceStatus[RESOURCE_STATUS.BUSY]).toBe('busy')
    })
  })
})