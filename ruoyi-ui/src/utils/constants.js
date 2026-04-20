/**
 * 系统常量配置
 * 所有状态、类型等枚举值统一定义在此文件
 */

// 项目状态
export const PROJECT_STATUS = {
  ONGOING: '进行中',
  COMPLETED: '已完成',
  PAUSED: '暂停'
};

export const PROJECT_STATUS_OPTIONS = [
  { label: '全部状态', value: '' },
  { label: '进行中', value: PROJECT_STATUS.ONGOING },
  { label: '已完成', value: PROJECT_STATUS.COMPLETED },
  { label: '暂停', value: PROJECT_STATUS.PAUSED }
];

// 项目类型
export const PROJECT_TYPE = {
  PROJECT: '项目',
  OUTSOURCE: '外包'
};

export const PROJECT_TYPE_OPTIONS = [
  { label: '项目', value: PROJECT_TYPE.PROJECT },
  { label: '外包', value: PROJECT_TYPE.OUTSOURCE }
];

// 是否下辖
export const SUBORDINATE = {
  YES: '是',
  NO: '否'
};

// 问题严重程度
export const ISSUE_SEVERITY = {
  HIGH: '高',
  MEDIUM: '中',
  LOW: '低'
};

export const ISSUE_SEVERITY_OPTIONS = [
  { label: '高', value: ISSUE_SEVERITY.HIGH },
  { label: '中', value: ISSUE_SEVERITY.MEDIUM },
  { label: '低', value: ISSUE_SEVERITY.LOW }
];

// 问题状态
export const ISSUE_STATUS = {
  PENDING: '待处理',
  PROGRESS: '进行中',
  RESOLVED: '已解决',
  CLOSED: '已关闭'
};

export const ISSUE_STATUS_OPTIONS = [
  { label: '待处理', value: ISSUE_STATUS.PENDING },
  { label: '进行中', value: ISSUE_STATUS.PROGRESS },
  { label: '已解决', value: ISSUE_STATUS.RESOLVED },
  { label: '已关闭', value: ISSUE_STATUS.CLOSED }
];

// 风险等级
export const RISK_LEVEL = {
  HIGH: '高',
  MEDIUM: '中',
  LOW: '低'
};

export const RISK_LEVEL_OPTIONS = [
  { label: '高', value: RISK_LEVEL.HIGH },
  { label: '中', value: RISK_LEVEL.MEDIUM },
  { label: '低', value: RISK_LEVEL.LOW }
];

// 风险状态
export const RISK_STATUS = {
  POTENTIAL: '潜在',
  OCCURRED: '已发生',
  ELIMINATED: '已消除'
};

export const RISK_STATUS_OPTIONS = [
  { label: '潜在', value: RISK_STATUS.POTENTIAL },
  { label: '已发生', value: RISK_STATUS.OCCURRED },
  { label: '已消除', value: RISK_STATUS.ELIMINATED }
];

// 资源状态
export const RESOURCE_STATUS = {
  FREE: '空闲',
  SOON_FREE: '即将空闲',
  BUSY: '忙碌',
  PENDING: '待定'
};

export const RESOURCE_STATUS_OPTIONS = [
  { label: '空闲', value: RESOURCE_STATUS.FREE },
  { label: '即将空闲', value: RESOURCE_STATUS.SOON_FREE },
  { label: '忙碌', value: RESOURCE_STATUS.BUSY },
  { label: '待定', value: RESOURCE_STATUS.PENDING }
];

// 里程碑预警阈值（天数）
export const MILESTONE_WARNING_DAYS = 10;

// 里程碑状态显示文本
export const MILESTONE_DISPLAY = {
  PENDING: '待定',
  WAITING: '待进行',
  COMPLETED: '已完成',
  OVERDUE: '已超期',
  WARNING: '10天内到期'
};

// 开票类型默认选项（项目类型）
export const INVOICE_TYPES_FOR_PROJECT = [
  { label: '首款', value: '首款' },
  { label: '需求确认款', value: '需求确认款' },
  { label: 'UAT测试款', value: 'UAT测试款' },
  { label: '上线款', value: '上线款' },
  { label: '验收款', value: '验收款' },
  { label: '尾款', value: '尾款' }
];

// 默认里程碑名称（项目类型）
export const DEFAULT_MILESTONES = [
  { name: '需求', planField: 'reqPlanDate', actualField: 'reqActualDate' },
  { name: 'UAT', planField: 'uatPlanDate', actualField: 'uatActualDate' },
  { name: '上线', planField: 'launchPlanDate', actualField: 'launchActualDate' },
  { name: '验收', planField: 'acceptPlanDate', actualField: 'acceptActualDate' }
];

// 样式映射：状态 -> CSS类名
export const STATUS_CLASS_MAP = {
  // 项目状态
  projectStatus: {
    [PROJECT_STATUS.ONGOING]: 'ongoing',
    [PROJECT_STATUS.COMPLETED]: 'completed',
    [PROJECT_STATUS.PAUSED]: 'paused'
  },
  // 问题严重程度
  severity: {
    [ISSUE_SEVERITY.HIGH]: 'danger',
    [ISSUE_SEVERITY.MEDIUM]: 'warning',
    [ISSUE_SEVERITY.LOW]: 'success'
  },
  // 问题状态
  issueStatus: {
    [ISSUE_STATUS.PENDING]: 'danger',
    [ISSUE_STATUS.PROGRESS]: 'warning',
    [ISSUE_STATUS.RESOLVED]: 'success',
    [ISSUE_STATUS.CLOSED]: 'info'
  },
  // 风险等级
  riskLevel: {
    [RISK_LEVEL.HIGH]: 'danger',
    [RISK_LEVEL.MEDIUM]: 'warning',
    [RISK_LEVEL.LOW]: 'success'
  },
  // 风险状态
  riskStatus: {
    [RISK_STATUS.POTENTIAL]: 'warning',
    [RISK_STATUS.OCCURRED]: 'danger',
    [RISK_STATUS.ELIMINATED]: 'success'
  },
  // 资源状态
  resourceStatus: {
    [RESOURCE_STATUS.FREE]: 'free',
    [RESOURCE_STATUS.SOON_FREE]: 'soon-free',
    [RESOURCE_STATUS.BUSY]: 'busy',
    [RESOURCE_STATUS.PENDING]: 'pending'
  }
};