<template>
  <div class="dashboard-container">
    <!-- 顶部操作栏 -->
    <div class="db-header">
      <div class="db-title">首页看板</div>
      <div class="db-actions">
        <el-button type="primary" size="small" @click="goTo('/system/project')">+ 新增项目</el-button>
        <el-button type="primary" size="small" @click="goTo('/system/issue')">+ 新增问题</el-button>
        <el-button size="small" @click="goTo('/report')">生成周报</el-button>
        <el-button size="small" @click="handleExport">导出数据</el-button>
      </div>
    </div>

    <!-- 预警条 -->
    <div class="alert-bar" v-if="hasAlerts">
      <div class="alert-item" @click="goTo('/system/task')">
        <span class="alert-dot"></span> 超期任务 <strong>{{ alerts.overdueTasks }}</strong>
      </div>
      <div class="alert-item warning" @click="goTo('/system/issue')">
        <span class="alert-dot"></span> 高优先级问题 <strong>{{ alerts.highPriorityIssues }}</strong>
      </div>
      <div class="alert-item" @click="goTo('/system/risk')">
        <span class="alert-dot"></span> 高风险项目 <strong>{{ alerts.highRisks }}</strong>
      </div>
      <div class="alert-item warning" @click="goTo('/system/task')">
        <span class="alert-dot"></span> 本周到期 <strong>{{ alerts.weekDue }}</strong>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="db-content">
      <!-- 第一行：统计卡片 + 大图表 -->
      <div class="charts-section">
        <!-- 统计卡片 -->
        <div class="stats-panel">
          <div class="stat-card" @click="goTo('/system/project')">
            <div class="stat-info">
              <div class="label">进行中项目</div>
              <div class="value">{{ summary.activeProjects }}</div>
              <div class="stat-trend up">↑ 较上周持平</div>
            </div>
            <div class="stat-icon">📁</div>
          </div>
          <div class="stat-card" @click="goTo('/system/issue')">
            <div class="stat-info">
              <div class="label">待处理问题</div>
              <div class="value">{{ summary.pendingIssues }}</div>
              <div class="stat-trend down">↓ 较上周减少 {{ summary.issueTrend || 0 }}</div>
            </div>
            <div class="stat-icon">⚠️</div>
          </div>
          <div class="stat-card" @click="goTo('/system/risk')">
            <div class="stat-info">
              <div class="label">潜在风险</div>
              <div class="value">{{ summary.potentialRisks }}</div>
              <div class="stat-trend down">↓ 较上周减少 {{ summary.riskTrend || 0 }}</div>
            </div>
            <div class="stat-icon">🔴</div>
          </div>
          <div class="stat-card" @click="goTo('/system/task')">
            <div class="stat-info">
              <div class="label">任务完成率</div>
              <div class="value">{{ summary.taskCompletionRate }}%</div>
              <div class="stat-trend up">↑ 较上周提升 {{ summary.rateChange || 0 }}%</div>
            </div>
            <div class="stat-icon">✅</div>
          </div>
        </div>

        <!-- 大图表：任务完成情况（纯CSS柱状图） -->
        <div class="big-chart-card">
          <div class="chart-header">
            <div class="chart-title">本周任务完成情况</div>
            <div class="chart-legend">
              <div class="legend-item"><span class="legend-dot" style="background: #10B981"></span> 按时完成</div>
              <div class="legend-item"><span class="legend-dot" style="background: #EF4444"></span> 超期完成</div>
              <div class="legend-item"><span class="legend-dot dashed"></span> 未完成</div>
            </div>
          </div>

          <div class="task-chart-container">
            <!-- 纯CSS柱状图 -->
            <div class="task-bars-wrapper">
              <div class="task-bars">
                <div class="bar-group" v-for="(item, index) in taskChartData" :key="index">
                  <div class="bar-values">
                    <span style="color: #10B981">{{ item.onTime }}</span>/<span style="color: #EF4444">{{ item.late }}</span>/<span style="color: #9CA3AF">{{ item.incomplete }}</span>
                  </div>
                  <div class="bar-container">
                    <div class="bar ontime" :style="{ height: getBarHeight(item.onTime) + 'px' }"></div>
                    <div class="bar late" :style="{ height: getBarHeight(item.late) + 'px' }"></div>
                    <div class="bar incomplete" :style="{ height: getBarHeight(item.incomplete) + 'px' }"></div>
                  </div>
                  <div class="bar-label">{{ item.day }}</div>
                </div>
              </div>
            </div>

            <!-- 完成统计 - 5项 -->
            <div class="completion-stats">
              <div class="completion-stat">
                <div class="num" style="color: #111827;">{{ taskStats.total }}</div>
                <div class="label">本周总任务</div>
              </div>
              <div class="completion-stat">
                <div class="num" style="color: #10B981;">{{ taskStats.onTime }}</div>
                <div class="label">按时完成</div>
              </div>
              <div class="completion-stat">
                <div class="num" style="color: #EF4444;">{{ taskStats.late }}</div>
                <div class="label">超期完成</div>
              </div>
              <div class="completion-stat">
                <div class="num" style="color: #9CA3AF;">{{ taskStats.incomplete }}</div>
                <div class="label">未完成</div>
              </div>
              <div class="completion-stat">
                <div class="num" style="color: #2563EB;">{{ taskStats.rate }}%</div>
                <div class="label">完成率</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 第二行：饼图 + 风险分布 -->
      <div class="second-row">
        <!-- 问题状态分布（纯CSS饼图） -->
        <div class="chart-card">
          <div class="chart-title">问题状态分布</div>
          <div class="pie-container">
            <div class="pie-chart-big" :style="pieChartStyle">
              <div class="pie-center">{{ issueStats.total }}</div>
            </div>
            <div class="pie-details">
              <div class="pie-detail-item" v-for="(item, index) in issuePieData" :key="index" @click="goTo('/system/issue')">
                <div class="pie-detail-left">
                  <span class="pie-detail-dot" :style="{ background: item.color }"></span>
                  <span class="pie-detail-label">{{ item.label }}</span>
                </div>
                <div>
                  <span class="pie-detail-value">{{ item.value }}</span>
                  <span class="pie-detail-percent">{{ item.percent }}%</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 风险等级分布 -->
        <div class="chart-card">
          <div class="chart-title">风险等级分布</div>
          <div class="risk-distribution">
            <div class="risk-bar-container">
              <div class="risk-bar-header">
                <span class="risk-bar-label">高风险</span>
                <span class="risk-bar-value" style="color: #EF4444;">{{ riskStats.high }}</span>
              </div>
              <div class="risk-bar">
                <div class="risk-bar-fill" :style="{ width: riskPercent.high + '%', background: '#EF4444' }"></div>
              </div>
            </div>
            <div class="risk-bar-container">
              <div class="risk-bar-header">
                <span class="risk-bar-label">中风险</span>
                <span class="risk-bar-value" style="color: #F59E0B;">{{ riskStats.medium }}</span>
              </div>
              <div class="risk-bar">
                <div class="risk-bar-fill" :style="{ width: riskPercent.medium + '%', background: '#F59E0B' }"></div>
              </div>
            </div>
            <div class="risk-bar-container">
              <div class="risk-bar-header">
                <span class="risk-bar-label">低风险</span>
                <span class="risk-bar-value" style="color: #10B981;">{{ riskStats.low }}</span>
              </div>
              <div class="risk-bar">
                <div class="risk-bar-fill" :style="{ width: riskPercent.low + '%', background: '#10B981' }"></div>
              </div>
            </div>
            <div class="risk-summary">
              <div class="risk-summary-item">总计 <strong>{{ riskStats.total }}</strong> 个风险</div>
              <div class="risk-summary-item">潜在 <strong style="color: #F59E0B;">{{ riskStats.potential }}</strong> / 已发生 <strong style="color: #EF4444;">{{ riskStats.occurred }}</strong></div>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部表格 -->
      <div class="bottom-row">
        <!-- 最近问题 -->
        <div class="section-card">
          <div class="section-header">
            <span>最近问题 <span class="badge">{{ issueList.length }}</span></span>
            <a @click="goTo('/system/issue')">查看全部 →</a>
          </div>
          <table class="data-table" v-if="issueList.length > 0">
            <thead>
              <tr><th>项目</th><th>描述</th><th>严重程度</th><th>状态</th></tr>
            </thead>
            <tbody>
              <tr v-for="(item, index) in issueList" :key="index">
                <td>{{ item.projectName || '-' }}</td>
                <td>{{ item.description || '-' }}</td>
                <td><span class="status-tag" :class="severityClass(item.severity)">{{ item.severity }}</span></td>
                <td><span class="status-tag" :class="statusClass(item.status)">{{ item.status }}</span></td>
              </tr>
            </tbody>
          </table>
          <div v-else class="empty-tip">暂无问题</div>
        </div>

        <!-- 风险监控 -->
        <div class="section-card">
          <div class="section-header">
            <span>风险监控 <span class="badge">{{ riskList.length }}</span></span>
            <a @click="goTo('/system/risk')">查看全部 →</a>
          </div>
          <table class="data-table" v-if="riskList.length > 0">
            <thead>
              <tr><th>项目</th><th>描述</th><th>等级</th><th>状态</th></tr>
            </thead>
            <tbody>
              <tr v-for="(item, index) in riskList" :key="index">
                <td>{{ item.projectName || '-' }}</td>
                <td>{{ item.description || '-' }}</td>
                <td><span class="status-tag" :class="levelClass(item.level)">{{ item.level }}</span></td>
                <td><span class="status-tag" :class="riskStatusClass(item.status)">{{ item.status }}</span></td>
              </tr>
            </tbody>
          </table>
          <div v-else class="empty-tip">暂无风险</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getDashboardData } from "@/api/dashboard";

export default {
  name: "Dashboard",
  data() {
    return {
      loading: false,
      // 汇总数据
      summary: {
        activeProjects: 0,
        pendingIssues: 0,
        potentialRisks: 0,
        taskCompletionRate: 0,
        issueTrend: 0,
        riskTrend: 0,
        rateChange: 0
      },
      // 预警数据
      alerts: {
        overdueTasks: 0,
        highPriorityIssues: 0,
        highRisks: 0,
        weekDue: 0
      },
      // 任务统计
      taskStats: {
        total: 0,
        onTime: 0,
        late: 0,
        incomplete: 0,
        rate: 0
      },
      // 问题统计
      issueStats: {
        pending: 0,
        inProgress: 0,
        resolved: 0,
        closed: 0,
        total: 0
      },
      // 风险统计
      riskStats: {
        high: 0,
        medium: 0,
        low: 0,
        total: 0,
        potential: 0,
        occurred: 0
      },
      // 图表数据
      taskChartData: [],
      issueList: [],
      riskList: []
    };
  },
  computed: {
    hasAlerts() {
      return this.alerts.overdueTasks > 0 || this.alerts.highPriorityIssues > 0 ||
             this.alerts.highRisks > 0 || this.alerts.weekDue > 0;
    },
    riskPercent() {
      const max = Math.max(this.riskStats.high, this.riskStats.medium, this.riskStats.low, 1);
      return {
        high: (this.riskStats.high / max) * 100,
        medium: (this.riskStats.medium / max) * 100,
        low: (this.riskStats.low / max) * 100
      };
    },
    issuePieData() {
      const total = this.issueStats.total || 1;
      return [
        { label: '待处理', value: this.issueStats.pending, color: '#2563EB', percent: Math.round(this.issueStats.pending * 100 / total) },
        { label: '进行中', value: this.issueStats.inProgress, color: '#F59E0B', percent: Math.round(this.issueStats.inProgress * 100 / total) },
        { label: '已解决', value: this.issueStats.resolved, color: '#10B981', percent: Math.round(this.issueStats.resolved * 100 / total) },
        { label: '已关闭', value: this.issueStats.closed, color: '#D1D5DB', percent: Math.round(this.issueStats.closed * 100 / total) }
      ];
    },
    pieChartStyle() {
      const total = this.issueStats.total || 1;
      const pending = (this.issueStats.pending / total) * 360;
      const inProgress = (this.issueStats.inProgress / total) * 360;
      const resolved = (this.issueStats.resolved / total) * 360;
      // conic-gradient 从上开始，顺时针
      return {
        background: `conic-gradient(#2563EB 0deg ${pending}deg, #F59E0B ${pending}deg ${pending + inProgress}deg, #10B981 ${pending + inProgress}deg ${pending + inProgress + resolved}deg, #D1D5DB ${pending + inProgress + resolved}deg 360deg)`
      };
    }
  },
  mounted() {
    this.loadData();
  },
  methods: {
    async loadData() {
      this.loading = true;
      try {
        const response = await getDashboardData();
        const data = response.data || {};

        // 汇总数据
        this.summary = data.summary || this.summary;
        this.alerts = data.alerts || this.alerts;
        this.taskStats = data.taskStats || this.taskStats;
        this.issueStats = data.issueStats || this.issueStats;
        this.riskStats = data.riskStats || this.riskStats;
        this.taskChartData = data.taskChartData || this.getDefaultTaskData();
        this.issueList = data.recentIssues || [];
        this.riskList = data.recentRisks || [];
      } catch (error) {
        console.error('加载数据失败:', error);
        this.taskChartData = this.getDefaultTaskData();
      } finally {
        this.loading = false;
      }
    },
    getDefaultTaskData() {
      return [
        { day: '周一', onTime: 8, late: 0, incomplete: 2 },
        { day: '周二', onTime: 10, late: 2, incomplete: 0 },
        { day: '周三', onTime: 5, late: 1, incomplete: 3 },
        { day: '周四', onTime: 9, late: 1, incomplete: 1 },
        { day: '周五', onTime: 11, late: 1, incomplete: 4 }
      ];
    },
    getBarHeight(value) {
      const max = Math.max(...this.taskChartData.map(d => d.onTime + d.late + d.incomplete), 10);
      return (value / max) * 110;
    },
    goTo(path) {
      this.$router.push(path);
    },
    handleExport() {
      this.$message.info('导出功能开发中');
    },
    severityClass(severity) {
      const map = { '高': 'danger', '中': 'warning', '低': 'info' };
      return map[severity] || 'info';
    },
    statusClass(status) {
      const map = { '待处理': 'danger', '进行中': 'warning', '已解决': 'success', '已关闭': 'info' };
      return map[status] || 'info';
    },
    levelClass(level) {
      const map = { '高': 'danger', '中': 'warning', '低': 'success' };
      return map[level] || 'info';
    },
    riskStatusClass(status) {
      const map = { '潜在': 'warning', '已发生': 'danger', '已消除': 'success' };
      return map[status] || 'info';
    }
  }
};
</script>

<style lang="scss" scoped>
.dashboard-container {
  background: #e5e7eb;
  min-height: 100vh;
  padding: 24px;
}

.db-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: white;
  border-radius: 12px;
  margin-bottom: 16px;
  border: 1px solid #E5E7EB;

  .db-title {
    font-size: 22px;
    font-weight: 600;
    color: #111827;
  }

  .db-actions {
    display: flex;
    gap: 8px;
  }
}

/* 预警条 */
.alert-bar {
  display: flex;
  gap: 16px;
  padding: 16px 24px;
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 8px;
  margin-bottom: 16px;

  .alert-item {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 14px;
    color: #374151;
    cursor: pointer;

    .alert-dot {
      width: 8px;
      height: 8px;
      border-radius: 50%;
      background: #EF4444;
      animation: pulse 2s infinite;
    }

    &.warning .alert-dot {
      background: #F59E0B;
    }

    &.warning strong {
      color: #F59E0B;
    }

    strong {
      color: #EF4444;
      font-weight: 600;
    }
  }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.db-content {
  .charts-section {
    display: grid;
    grid-template-columns: 280px 1fr;
    gap: 24px;
    margin-bottom: 24px;
  }

  /* 统计卡片 */
  .stats-panel {
    display: grid;
    grid-template-rows: repeat(4, 1fr);
    gap: 12px;
  }

  .stat-card {
    background: white;
    border: 1px solid #E5E7EB;
    border-radius: 10px;
    padding: 18px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      border-color: #3B82F6;
      box-shadow: 0 2px 8px rgba(37, 99, 235, 0.1);
    }

    .stat-info {
      .label {
        font-size: 13px;
        color: #6B7280;
      }
      .value {
        font-size: 28px;
        font-weight: 700;
        color: #111827;
      }
      .stat-trend {
        font-size: 12px;
        margin-top: 2px;
        &.up { color: #10B981; }
        &.down { color: #EF4444; }
      }
    }

    .stat-icon {
      width: 44px;
      height: 44px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 22px;
    }
  }

  /* 大图表卡片 */
  .big-chart-card {
    background: white;
    border: 1px solid #E5E7EB;
    border-radius: 10px;
    padding: 20px;
    display: flex;
    flex-direction: column;
  }

  .chart-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
  }

  .chart-title {
    font-size: 16px;
    font-weight: 600;
    color: #1F2937;
  }

  .chart-legend {
    display: flex;
    gap: 16px;
    font-size: 12px;

    .legend-item {
      display: flex;
      align-items: center;
      gap: 6px;
      color: #6B7280;
    }

    .legend-dot {
      width: 10px;
      height: 10px;
      border-radius: 2px;

      &.dashed {
        background: #D1D5DB;
        border: 1px dashed #9CA3AF;
      }
    }
  }

  .task-chart-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  /* 纯CSS柱状图 */
  .task-bars-wrapper {
    flex: 1;
    background: #F9FAFB;
    border-radius: 8px;
    padding: 16px;
  }

  .task-bars {
    display: flex;
    align-items: flex-end;
    justify-content: space-around;
    height: 200px;
    padding-bottom: 32px;
    position: relative;

    &::before {
      content: '';
      position: absolute;
      bottom: 32px;
      left: 0;
      right: 0;
      height: 1px;
      background: #E5E7EB;
    }
  }

  .bar-group {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    flex: 1;
    max-width: 100px;
  }

  .bar-container {
    display: flex;
    gap: 6px;
    align-items: flex-end;
    height: 160px;
  }

  .bar {
    width: 28px;
    border-radius: 4px 4px 0 0;

    &.ontime { background: #10B981; }
    &.late { background: #EF4444; }
    &.incomplete {
      background: #D1D5DB;
      border: 1px dashed #9CA3AF;
    }
  }

  .bar-values {
    font-size: 11px;
    color: #6B7280;
    margin-bottom: 4px;

    span {
      font-weight: 500;
    }
  }

  .bar-label {
    font-size: 13px;
    color: #4B5563;
    font-weight: 500;
  }

  /* 完成统计 */
  .completion-stats {
    display: grid;
    grid-template-columns: repeat(5, 1fr);
    gap: 12px;
    background: #F9FAFB;
    border-radius: 8px;
    padding: 16px;
  }

  .completion-stat {
    text-align: center;
    padding: 12px;
    background: white;
    border-radius: 6px;
    border: 1px solid #F3F4F6;

    .num {
      font-size: 28px;
      font-weight: 700;
      line-height: 1.2;
    }

    .label {
      font-size: 12px;
      color: #6B7280;
      margin-top: 4px;
    }
  }

  /* 第二行 */
  .second-row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 24px;
    margin-bottom: 24px;
  }

  .chart-card {
    background: white;
    border: 1px solid #E5E7EB;
    border-radius: 10px;
    padding: 20px;
  }

  /* 纯CSS饼图 */
  .pie-container {
    display: flex;
    align-items: center;
    gap: 32px;
    padding: 16px 0;
  }

  .pie-chart-big {
    width: 180px;
    height: 180px;
    border-radius: 50%;
    position: relative;
    flex-shrink: 0;
    /* 使用伪元素显示中心数字 */
  }

  .pie-center {
    position: absolute;
    width: 100px;
    height: 100px;
    background: white;
    border-radius: 50%;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 32px;
    font-weight: 700;
    color: #1F2937;
    z-index: 10;
    box-shadow: 0 0 0 4px white;
  }

  .pie-details {
    flex: 1;
  }

  .pie-detail-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12px 0;
    border-bottom: 1px solid #F3F4F6;
    cursor: pointer;

    &:last-child { border-bottom: none; }

    .pie-detail-left {
      display: flex;
      align-items: center;
      gap: 12px;
    }

    .pie-detail-dot {
      width: 12px;
      height: 12px;
      border-radius: 3px;
    }

    .pie-detail-label {
      font-size: 14px;
      color: #374151;
    }

    .pie-detail-value {
      font-size: 18px;
      font-weight: 600;
      color: #1F2937;
    }

    .pie-detail-percent {
      font-size: 12px;
      color: #6B7280;
      margin-left: 8px;
    }
  }

  /* 风险分布 */
  .risk-distribution {
    padding: 16px 0;
  }

  .risk-bar-container {
    margin-bottom: 16px;
  }

  .risk-bar-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 8px;
  }

  .risk-bar-label {
    font-size: 14px;
    color: #374151;
  }

  .risk-bar-value {
    font-size: 14px;
    font-weight: 600;
  }

  .risk-bar {
    height: 12px;
    background: #F3F4F6;
    border-radius: 6px;
    overflow: hidden;
  }

  .risk-bar-fill {
    height: 100%;
    border-radius: 6px;
    transition: width 0.3s;
  }

  .risk-summary {
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px solid #E5E7EB;
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    color: #6B7280;

    strong {
      color: #1F2937;
    }
  }

  /* 底部表格 */
  .bottom-row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
  }

  .section-card {
    background: white;
    border: 1px solid #E5E7EB;
    border-radius: 8px;
    overflow: hidden;
  }

  .section-header {
    background: #F9FAFB;
    padding: 12px 16px;
    font-weight: 600;
    font-size: 14px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid #E5E7EB;

    .badge {
      background: #2563EB;
      color: white;
      padding: 2px 8px;
      border-radius: 12px;
      font-size: 12px;
      font-weight: 500;
      margin-left: 8px;
    }

    a {
      font-size: 12px;
      color: #2563EB;
      cursor: pointer;
      text-decoration: none;

      &:hover { text-decoration: underline; }
    }
  }

  .data-table {
    width: 100%;
    border-collapse: collapse;

    th, td {
      padding: 10px 16px;
      text-align: left;
      font-size: 13px;
      border-bottom: 1px solid #F3F4F6;
    }

    th {
      color: #6B7280;
      font-weight: 500;
      background: #F9FAFB;
    }

    tr:hover td {
      background: #F9FAFB;
    }

    tr:last-child td {
      border-bottom: none;
    }
  }

  .empty-tip {
    text-align: center;
    padding: 30px;
    color: #909399;
    font-size: 14px;
  }
}

/* 状态标签 */
.status-tag {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 500;

  &.danger { background: #fef2f2; color: #EF4444; }
  &.warning { background: #fffbeb; color: #F59E0B; }
  &.success { background: #f0fdf4; color: #10B981; }
  &.info { background: #eef2ff; color: #6366F1; }
}

/* 响应式 */
@media (max-width: 1200px) {
  .db-content {
    .charts-section {
      grid-template-columns: 1fr;
    }
    .second-row {
      grid-template-columns: 1fr;
    }
    .bottom-row {
      grid-template-columns: 1fr;
    }
  }
}
</style>