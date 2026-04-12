<template>
  <div class="pp-page-wrapper">
    <div class="pp-page-container">
    <!-- 顶部导航 -->
    <div class="pp-top-nav">
      <div class="pp-breadcrumb">
        <a @click="goTo('/dashboard')">首页</a>
        <span>/</span>
        <a v-if="activeTab === 'config'" @click="switchTab('report')">周报管理</a>
        <span v-else class="current">周报管理</span>
        <span v-if="activeTab === 'config'">/</span>
        <span v-if="activeTab === 'config'" class="current">邮件配置</span>
      </div>
      <div class="pp-page-actions">
        <button class="pp-btn pp-btn-secondary" @click="showHistory">历史周报</button>
      </div>
    </div>

    <!-- Tab切换 -->
    <div class="pp-tabs">
      <div class="pp-tab" :class="{ active: activeTab === 'report' }" @click="switchTab('report')">本周周报</div>
      <div class="pp-tab" :class="{ active: activeTab === 'config' }" @click="switchTab('config')">邮件配置</div>
    </div>

    <!-- 周报内容 -->
    <div class="pp-page-content" v-if="activeTab === 'report'" v-loading="loading">
      <!-- 报告头部 -->
      <div class="pp-report-header">
        <div class="pp-report-info">
          <div class="pp-report-title">项目周报</div>
          <div class="pp-report-date">{{ periodText }}</div>
        </div>
        <div class="pp-report-actions">
          <button class="pp-btn pp-btn-primary" @click="sendEmail">📧 发送邮件</button>
        </div>
      </div>

      <!-- 报告内容 -->
      <div class="pp-report-content">
        <!-- 本周概览 -->
        <div class="pp-section">
          <div class="pp-section-title">📊 本周概览</div>
          <div class="pp-stats-grid pp-stats-4">
            <div class="pp-stat-item">
              <div class="pp-stat-value">{{ overview.activeProjects }}</div>
              <div class="pp-stat-label">进行中项目</div>
            </div>
            <div class="pp-stat-item">
              <div class="pp-stat-value pp-text-success">{{ overview.newIssues }}</div>
              <div class="pp-stat-label">新增问题</div>
            </div>
            <div class="pp-stat-item">
              <div class="pp-stat-value pp-text-primary">{{ overview.resolvedIssues }}</div>
              <div class="pp-stat-label">已解决问题</div>
            </div>
            <div class="pp-stat-item">
              <div class="pp-stat-value pp-text-warning">{{ overview.taskRate }}%</div>
              <div class="pp-stat-label">任务完成率</div>
            </div>
          </div>
        </div>

        <!-- 项目进度 -->
        <div class="pp-section">
          <div class="pp-section-title">📁 项目进度</div>
          <table class="pp-data-table">
            <thead>
              <tr>
                <th>项目名称</th>
                <th>当前阶段</th>
                <th>成本使用</th>
                <th>工时使用</th>
                <th>状态</th>
                <th>本周变化</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="project in projects" :key="project.id">
                <td>{{ project.name }}</td>
                <td>
                  <div class="pp-stage-row">
                    <span class="pp-stage-icon" :class="getStageClass(project.stage)">{{ getStageIcon(project.stage) }}</span>
                    <span class="pp-stage-name" :class="{ paused: project.status === '暂停' }">{{ project.stage }}</span>
                  </div>
                  <div class="pp-stage-date" :class="{ warning: project.isExpiring, paused: project.status === '暂停' }">{{ project.stageDate }}</div>
                </td>
                <td>
                  <div class="pp-progress-cell">
                    <div class="pp-mini-progress">
                      <div class="pp-mini-bar" :style="{ width: project.costRate + '%', background: getProgressColor(project.costRate) }"></div>
                    </div>
                    <span class="pp-mini-value" :class="getProgressClass(project.costRate)">{{ project.costRate }}%</span>
                  </div>
                </td>
                <td>
                  <div class="pp-progress-cell">
                    <div class="pp-mini-progress">
                      <div class="pp-mini-bar" :style="{ width: Math.min(project.hoursRate, 100) + '%', background: getProgressColor(project.hoursRate) }"></div>
                    </div>
                    <span class="pp-mini-value" :class="getProgressClass(project.hoursRate)">{{ project.hoursRate }}%</span>
                  </div>
                </td>
                <td><span class="pp-status-tag" :class="getProjectStatusClass(project.status)">{{ project.status }}</span></td>
                <td class="pp-change-cell" :class="{ success: project.changeType === 'success', muted: project.changeType === 'none' }">{{ project.change }}</td>
              </tr>
            </tbody>
          </table>
          <div class="pp-summary-bar">
            <strong>项目汇总：</strong>进行中 {{ projectSummary.active }} 个 | 已完成 {{ projectSummary.completed }} 个 | 暂停 {{ projectSummary.paused }} 个 | 成本超支 {{ projectSummary.costOver }} 个 | 工时超支 {{ projectSummary.hoursOver }} 个
          </div>
        </div>

        <!-- 问题跟踪 -->
        <div class="pp-section">
          <div class="pp-section-title">🐛 问题跟踪</div>
          <div class="pp-section-desc">
            本周新增 <strong class="pp-text-danger">{{ issueSummary.new }}</strong> 个问题，解决 <strong class="pp-text-success">{{ issueSummary.resolved }}</strong> 个问题，当前待处理 <strong>{{ issueSummary.pending }}</strong> 个
          </div>
          <table class="pp-data-table">
            <thead>
              <tr>
                <th>问题描述</th>
                <th>所属项目</th>
                <th>严重程度</th>
                <th>状态</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="issue in issues" :key="issue.id">
                <td>{{ issue.description }}</td>
                <td>{{ issue.projectName }}</td>
                <td><span class="pp-severity" :class="getSeverityClass(issue.severity)">{{ issue.severity }}</span></td>
                <td><span class="pp-status-tag" :class="getIssueStatusClass(issue.status)">{{ issue.status }}</span></td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- 风险管理 -->
        <div class="pp-section">
          <div class="pp-section-title">⚠️ 风险管理</div>
          <div class="pp-section-desc">
            当前跟踪风险 <strong>{{ riskSummary.total }}</strong> 个，其中高风险 <strong class="pp-text-danger">{{ riskSummary.high }}</strong> 个
          </div>
          <table class="pp-data-table">
            <thead>
              <tr>
                <th>风险描述</th>
                <th>所属项目</th>
                <th>等级</th>
                <th>状态</th>
                <th>应对措施</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="risk in risks" :key="risk.id" :class="{ danger: risk.level === '高' }">
                <td>{{ risk.description }}</td>
                <td>{{ risk.projectName }}</td>
                <td>
                  <span class="pp-risk-level" :class="getRiskClass(risk.level)">
                    <span class="pp-risk-dot" :class="getRiskClass(risk.level)"></span>
                    {{ risk.level }}
                  </span>
                </td>
                <td><span class="pp-status-tag" :class="getRiskStatusClass(risk.status)">{{ risk.status }}</span></td>
                <td>{{ risk.measure }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- 任务完成情况 -->
        <div class="pp-section">
          <div class="pp-section-title">✅ 任务完成情况</div>
          <div class="pp-stats-grid pp-stats-5">
            <div class="pp-stat-item">
              <div class="pp-stat-value">{{ taskStats.completed }}</div>
              <div class="pp-stat-label">已完成</div>
            </div>
            <div class="pp-stat-item">
              <div class="pp-stat-value pp-text-warning">{{ taskStats.pending }}</div>
              <div class="pp-stat-label">待完成</div>
            </div>
            <div class="pp-stat-item">
              <div class="pp-stat-value pp-text-danger">{{ taskStats.overdue }}</div>
              <div class="pp-stat-label">已超期</div>
            </div>
            <div class="pp-stat-item">
              <div class="pp-stat-value">{{ taskStats.total }}</div>
              <div class="pp-stat-label">总任务数</div>
            </div>
            <div class="pp-stat-item">
              <div class="pp-stat-value pp-text-success">{{ taskStats.rate }}%</div>
              <div class="pp-stat-label">完成率</div>
            </div>
          </div>

          <div class="pp-task-type-row">
            <div class="pp-task-type-label">任务类型分布</div>
            <div class="pp-task-type-info">
              <span>📋 周期性任务：{{ taskStats.cycleTotal }}项（完成率 {{ taskStats.cycleRate }}%）</span>
              <span>📌 一次性任务：{{ taskStats.onceTotal }}项（完成率 {{ taskStats.onceRate }}%）</span>
            </div>
          </div>

          <div class="pp-task-detail">
            <div class="pp-task-detail-label">本周任务详情</div>
            <table class="pp-data-table">
              <thead>
                <tr>
                  <th>任务名称</th>
                  <th>类型</th>
                  <th>截止时间</th>
                  <th>完成人数</th>
                  <th>完成率</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="task in taskDetails" :key="task.id">
                  <td>{{ task.name }}</td>
                  <td><span class="pp-status-tag" :class="getTaskTypeClass(task.cycle)">{{ task.cycle }}</span></td>
                  <td>{{ task.deadline }}</td>
                  <td>{{ task.completedCount }}/{{ task.totalCount }}</td>
                  <td class="pp-rate-cell" :class="getRateClass(task.rate)">{{ task.rate }}%</td>
                </tr>
              </tbody>
            </table>
          </div>

          <div class="pp-person-ranking">
            <div class="pp-ranking-label">人员完成率排名</div>
            <div class="pp-ranking-grid">
              <div v-for="person in personRanking" :key="person.id" class="pp-ranking-item" :class="getRankingClass(person.rate)">
                <div class="pp-ranking-name">{{ person.name }}</div>
                <div class="pp-ranking-rate">{{ person.rate }}%</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 本周重要事项 -->
        <div class="pp-section">
          <div class="pp-section-title">📌 本周重要事项</div>
          <table class="pp-data-table">
            <thead>
              <tr>
                <th>日期</th>
                <th>项目</th>
                <th>事项描述</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in milestones" :key="item.id">
                <td>{{ item.date }}</td>
                <td>{{ item.projectName }}</td>
                <td>{{ item.description }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- 邮件配置 -->
    <email-config v-if="activeTab === 'config'" ref="emailConfig" @test="testEmail" @save="saveEmailConfig" />
    </div>
  </div>
</template>

<script>
import { getReportData, sendReportEmail, testReportEmail, getEmailConfig, saveEmailConfig } from '@/api/system/report'
import { getCurrentWeek } from '@/utils/date'
import EmailConfig from './components/EmailConfig.vue'

export default {
  name: 'Report',
  components: { EmailConfig },
  data() {
    return {
      activeTab: 'report',
      loading: false,
      period: getCurrentWeek(),

      // 概览统计
      overview: {
        activeProjects: 0,
        newIssues: 0,
        resolvedIssues: 0,
        taskRate: 0
      },

      // 项目数据
      projects: [],
      projectSummary: { active: 0, completed: 0, paused: 0, costOver: 0, hoursOver: 0 },

      // 问题数据
      issues: [],
      issueSummary: { new: 0, resolved: 0, pending: 0 },

      // 风险数据
      risks: [],
      riskSummary: { total: 0, high: 0 },

      // 任务数据
      taskStats: { completed: 0, pending: 0, overdue: 0, total: 0, rate: 0, cycleTotal: 0, cycleRate: 0, onceTotal: 0, onceRate: 0 },
      taskDetails: [],
      personRanking: [],

      // 重要事项
      milestones: []
    }
  },
  computed: {
    periodText() {
      if (!this.period) return ''
      const [year, week] = this.period.split('-W')
      const weekDates = this.getWeekDates(parseInt(year), parseInt(week))
      return `${year}年${Math.ceil(parseInt(week) / 4)}月第${((parseInt(week) - 1) % 4) + 1}周 (${weekDates.start} ~ ${weekDates.end})`
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getReportData(this.period)
        const data = res.data || {}

        // 概览
        const overview = data.overview || {}
        this.overview = {
          activeProjects: overview.activeProjects || 0,
          newIssues: overview.newIssues || 0,
          resolvedIssues: overview.resolvedIssues || 0,
          taskRate: overview.taskRate || 0
        }

        // 项目
        this.projects = (data.projects || []).map(p => ({
          ...p,
          isExpiring: p.isExpiring || false
        }))
        this.projectSummary = data.projectSummary || { active: 0, completed: 0, paused: 0, costOver: 0, hoursOver: 0 }

        // 问题
        this.issues = data.issues || []
        this.issueSummary = data.issueSummary || { new: 0, resolved: 0, pending: 0 }

        // 风险
        this.risks = data.risks || []
        this.riskSummary = data.riskSummary || { total: 0, high: 0 }

        // 任务
        this.taskStats = data.taskStats || {}
        this.taskDetails = data.taskDetails || []
        this.personRanking = data.personRanking || []

        // 重要事项
        this.milestones = data.milestones || []
      } finally {
        this.loading = false
      }
    },

    getWeekDates(year, weekNum) {
      const firstDay = new Date(year, 0, 1)
      const daysOffset = (weekNum - 1) * 7
      const weekStart = new Date(firstDay.getTime() + daysOffset * 24 * 60 * 60 * 1000)
      const weekEnd = new Date(weekStart.getTime() + 6 * 24 * 60 * 60 * 1000)
      const formatDate = d => `${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
      return { start: formatDate(weekStart), end: formatDate(weekEnd) }
    },

    switchTab(tab) {
      this.activeTab = tab
    },

    async sendEmail() {
      try {
        await this.$modal.confirm('确认发送周报邮件？')
        await sendReportEmail(this.period)
        this.$modal.msgSuccess('邮件发送成功！')
      } catch (e) {}
    },

    async testEmail() {
      try {
        await testReportEmail()
        this.$modal.msgSuccess('测试邮件已发送，请检查收件箱')
      } catch (e) {}
    },

    async saveEmailConfig(config) {
      try {
        await saveEmailConfig(config)
        this.$modal.msgSuccess('配置保存成功')
      } catch (e) {}
    },

    showHistory() {
      this.$message.info('历史周报功能开发中')
    },

    goTo(path) {
      this.$router.push(path)
    },

    // 样式辅助方法
    getStageClass(stage) {
      if (stage === '验收交付' || stage === '上线部署') return 'success'
      if (stage === 'UAT测试') return 'warning'
      if (stage === '暂停') return 'paused'
      return 'primary'
    },
    getStageIcon(stage) {
      if (stage === '验收交付') return '✓'
      if (stage === '上线部署') return '✓'
      if (stage === 'UAT测试') return '!'
      if (stage === '暂停' || stage === '需求确认') return '○'
      return '●'
    },
    getProgressColor(rate) {
      if (rate > 100) return '#EF4444'
      if (rate > 80) return '#F59E0B'
      return '#10B981'
    },
    getProgressClass(rate) {
      if (rate > 100) return 'danger'
      if (rate > 80) return 'warning'
      return 'success'
    },
    getProjectStatusClass(status) {
      const map = { '进行中': 'progress', '已完成': 'completed', '暂停': 'paused' }
      return map[status] || 'default'
    },
    getSeverityClass(severity) {
      const map = { '高': 'danger', '中': 'warning', '低': 'default' }
      return map[severity] || 'default'
    },
    getIssueStatusClass(status) {
      const map = { '待处理': 'pending', '处理中': 'progress', '已解决': 'resolved', '已关闭': 'closed' }
      return map[status] || 'default'
    },
    getRiskClass(level) {
      const map = { '高': 'high', '中': 'medium', '低': 'low' }
      return map[level] || 'low'
    },
    getRiskStatusClass(status) {
      const map = { '潜在': 'potential', '已发生': 'happened', '已消除': 'eliminated' }
      return map[status] || 'default'
    },
    getTaskTypeClass(cycle) {
      const map = { '每日': 'progress', '每周': 'pending', '每月': 'resolved' }
      return map[cycle] || 'default'
    },
    getRateClass(rate) {
      if (rate >= 80) return 'success'
      if (rate >= 50) return 'warning'
      return 'danger'
    },
    getRankingClass(rate) {
      if (rate >= 100) return 'success'
      if (rate >= 50) return 'warning'  // 灰色背景 + 橙色数字
      return 'danger'  // 红色背景 + 煤色数字
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/assets/styles/ppmanage.scss";

/* Outer wrapper with gray background like design */
.pp-page-wrapper {
  background: #F3F4F6;
  padding: 24px;
  min-height: 100vh;
  font-family: 'Plus Jakarta Sans', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}

.pp-page-container {
  background: white;
  border-radius: 12px;
  border: 1px solid #E5E7EB;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.pp-top-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid #E5E7EB;
  background: #F9FAFB;
}

.pp-breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #6B7280;
  a { color: #6B7280; cursor: pointer; &:hover { color: #2563EB; } }
  .current { color: #1F2937; font-weight: 500; }
}

/* Tab */
.pp-tabs {
  display: flex;
  background: white;
  border-bottom: 1px solid #E5E7EB;
}

.pp-tab {
  padding: 14px 24px;
  font-size: 14px;
  font-weight: 500;
  color: #6B7280;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  &:hover { color: #374151; }
  &.active {
    color: #2563EB;
    border-bottom-color: #2563EB;
    background: #EFF6FF;
  }
}

/* Content */
.pp-page-content { padding: 24px; }

/* Report Header */
.pp-report-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #E5E7EB;
}

.pp-report-title {
  font-size: 20px;
  font-weight: 600;
  color: #1F2937;
}

.pp-report-date {
  font-size: 13px;
  color: #6B7280;
  margin-top: 4px;
}

/* Report Content */
.pp-report-content {
  background: #F9FAFB;
  border: 1px solid #E5E7EB;
  border-radius: 8px;
  padding: 24px;
}

.pp-section { margin-bottom: 24px; &:last-child { margin-bottom: 0; } }

.pp-section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1F2937;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 2px solid #2563EB;
  display: inline-block;
}

.pp-section-desc {
  font-size: 13px;
  color: #4B5563;
  margin-bottom: 12px;
}

/* Stats Grid */
.pp-stats-grid { display: grid; gap: 16px; margin-bottom: 20px; }
.pp-stats-4 { grid-template-columns: repeat(4, 1fr); }
.pp-stats-5 { grid-template-columns: repeat(5, 1fr); }

.pp-stat-item {
  background: white;
  border: 1px solid #E5E7EB;
  border-radius: 8px;
  padding: 16px;
  text-align: center;
}

.pp-stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1F2937;
}

.pp-stat-label {
  font-size: 12px;
  color: #6B7280;
  margin-top: 4px;
}

/* Background variants */
.pp-bg-success-light { background: #D1FAE5; border-color: #10B981; }
.pp-bg-warning-light { background: #FEF3C7; border-color: #F59E0B; }
.pp-bg-danger-light { background: #FEE2E2; border-color: #EF4444; }
.pp-bg-gray { background: #F3F4F6; }
.pp-bg-primary-light { background: #DBEAFE; border-color: #2563EB; }

/* Text colors */
.pp-text-success { color: #10B981; }
.pp-text-warning { color: #F59E0B; }
.pp-text-danger { color: #EF4444; }
.pp-text-primary { color: #2563EB; }

/* Data Table */
.pp-data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.pp-data-table th, .pp-data-table td {
  padding: 10px 12px;
  text-align: left;
  border-bottom: 1px solid #E5E7EB;
}

.pp-data-table th {
  background: #F3F4F6;
  font-weight: 600;
  color: #374151;
}

.pp-data-table tr:hover { background: #F9FAFB; }
.pp-data-table tr.danger { background: #FEF2F2; }

/* Stage Cell */
.pp-stage-row {
  display: flex;
  align-items: center;
  gap: 6px;
}

.pp-stage-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  font-size: 11px;
  color: white;
  &.success { background: #10B981; }
  &.warning { background: #F59E0B; }
  &.primary { background: #2563EB; }
  &.paused { background: #9CA3AF; }
}

.pp-stage-name { font-weight: 600; color: #1F2937; }
.pp-stage-name.paused { color: #6B7280; }
.pp-stage-date {
  font-size: 11px;
  color: #6B7280;
  margin-left: 26px;
  margin-top: 2px;
  &.warning { color: #F59E0B; }
  &.paused { color: #9CA3AF; }
}

/* Progress Cell */
.pp-progress-cell { display: flex; align-items: center; gap: 6px; }
.pp-mini-progress {
  width: 60px;
  height: 4px;
  background: #E5E7EB;
  border-radius: 2px;
  overflow: hidden;
}
.pp-mini-bar { height: 100%; border-radius: 2px; }
.pp-mini-value { font-size: 12px; font-weight: 600; &.success { color: #10B981; } &.warning { color: #F59E0B; } &.danger { color: #EF4444; } }

/* Status Tags */
.pp-status-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 500;
  &.progress { background: #DBEAFE; color: #1D4ED8; }
  &.completed { background: #D1FAE5; color: #059669; }
  &.paused { background: #FEF3C7; color: #B45309; }
  &.pending { background: #FEF3C7; color: #B45309; }
  &.resolved { background: #D1FAE5; color: #059669; }
  &.closed { background: #F3F4F6; color: #4B5563; }
  &.potential { background: #FEF3C7; color: #B45309; }
  &.happened { background: #FEE2E2; color: #DC2626; }
  &.eliminated { background: #D1FAE5; color: #059669; }
}

/* Severity */
.pp-severity {
  font-weight: 600;
  &.danger { color: #EF4444; }
  &.warning { color: #F59E0B; }
  &.default { color: #6B7280; }
}

/* Risk Level */
.pp-risk-level {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-weight: 500;
  &.high { color: #EF4444; }
  &.medium { color: #F59E0B; }
  &.low { color: #10B981; }
}

.pp-risk-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  &.high { background: #EF4444; }
  &.medium { background: #F59E0B; }
  &.low { background: #10B981; }
}

/* Change Cell */
.pp-change-cell {
  &.success { color: #10B981; }
  &.muted { color: #9CA3AF; }
}

/* Summary Bar */
.pp-summary-bar {
  margin-top: 12px;
  padding: 12px;
  background: #EFF6FF;
  border-radius: 6px;
  font-size: 13px;
  color: #374151;
}

/* Task Type Row */
.pp-task-type-row { margin-top: 16px; }
.pp-task-type-label { font-size: 14px; font-weight: 600; color: #374151; margin-bottom: 8px; }
.pp-task-type-info { display: flex; gap: 24px; font-size: 13px; color: #4B5563; }

/* Task Detail */
.pp-task-detail { margin-top: 16px; }
.pp-task-detail-label { font-size: 14px; font-weight: 600; color: #374151; margin-bottom: 8px; }
.pp-rate-cell { font-weight: 600; &.success { color: #10B981; } &.warning { color: #F59E0B; } &.danger { color: #EF4444; } }

/* Person Ranking */
.pp-person-ranking { margin-top: 16px; }
.pp-ranking-label { font-size: 14px; font-weight: 600; color: #374151; margin-bottom: 8px; }
.pp-ranking-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 8px; }
.pp-ranking-item {
  border-radius: 6px;
  padding: 8px;
  text-align: center;
  &.success { background: #F0FDF4; border: 1px solid #10B981; .pp-ranking-rate { color: #10B981; } }
  &.warning { background: #F9FAFB; border: 1px solid #E5E7EB; .pp-ranking-rate { color: #F59E0B; } }
  &.danger { background: #FEF2F2; border: 1px solid #EF4444; .pp-ranking-rate { color: #EF4444; } }
}
.pp-ranking-name { font-size: 12px; font-weight: 600; color: #1F2937; }
.pp-ranking-rate {
  font-size: 16px;
  font-weight: 700;
  margin-top: 2px;
}
</style>