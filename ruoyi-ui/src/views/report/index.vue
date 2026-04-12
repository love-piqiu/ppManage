<template>
  <div class="pp-page-container">
    <!-- 顶部导航 -->
    <div class="pp-top-nav">
      <div class="pp-breadcrumb">
        <a @click="goTo('/dashboard')">首页</a>
        <span>/</span>
        <span class="current">周报管理</span>
      </div>
      <div class="pp-page-actions">
        <button class="pp-btn pp-btn-secondary" @click="showHistory = true">历史周报</button>
      </div>
    </div>

    <!-- Tab切换 -->
    <div class="pp-tabs">
      <div class="pp-tab" :class="{ active: activeTab === 'report' }" @click="activeTab = 'report'">本周周报</div>
      <div class="pp-tab" :class="{ active: activeTab === 'email' }" @click="activeTab = 'email'">邮件配置</div>
    </div>

    <!-- 周报内容 -->
    <div v-show="activeTab === 'report'" class="pp-page-content">
      <!-- 周报标题 -->
      <div class="pp-report-header">
        <div>
          <div class="pp-report-title">项目周报</div>
          <div class="pp-report-date">{{ weekRange }}</div>
        </div>
        <div class="pp-report-actions">
          <button class="pp-btn pp-btn-primary" @click="handleSendEmail">📧 发送邮件</button>
        </div>
      </div>

      <!-- 周报内容区域 -->
      <div class="pp-report-body" v-loading="loading">
        <!-- 概览统计 -->
        <div class="pp-section">
          <div class="pp-section-title">📊 本周概览</div>
          <div class="pp-stats-grid">
            <div class="pp-stat-card">
              <div class="pp-stat-value">{{ overview.ongoingProjects }}</div>
              <div class="pp-stat-label">进行中项目</div>
            </div>
            <div class="pp-stat-card">
              <div class="pp-stat-value" style="color: #EF4444;">{{ overview.newIssues }}</div>
              <div class="pp-stat-label">新增问题</div>
            </div>
            <div class="pp-stat-card">
              <div class="pp-stat-value" style="color: #10B981;">{{ overview.resolvedIssues }}</div>
              <div class="pp-stat-label">已解决问题</div>
            </div>
            <div class="pp-stat-card">
              <div class="pp-stat-value" style="color: #F59E0B;">{{ overview.taskRate }}%</div>
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
              <tr v-for="p in projectProgress" :key="p.id">
                <td>{{ p.name }}</td>
                <td>
                  <div class="pp-stage-cell">
                    <span class="pp-stage-dot" :class="getStageClass(p.stageStatus)"></span>
                    <span class="pp-stage-name">{{ p.stage }}</span>
                    <span class="pp-stage-date" :class="getStageDateClass(p)">{{ p.stageDateInfo }}</span>
                  </div>
                </td>
                <td>
                  <div class="pp-usage-mini">
                    <div class="pp-usage-bar-mini">
                      <div class="pp-usage-fill-mini" :class="getUsageClass(p.costUsage)" :style="{ width: Math.min(p.costUsage, 100) + '%' }"></div>
                    </div>
                    <span class="pp-usage-val" :class="{ danger: p.costUsage > 100 }">{{ p.costUsage }}%</span>
                  </div>
                </td>
                <td>
                  <div class="pp-usage-mini">
                    <div class="pp-usage-bar-mini">
                      <div class="pp-usage-fill-mini" :class="getUsageClass(p.hourUsage)" :style="{ width: Math.min(p.hourUsage, 100) + '%' }"></div>
                    </div>
                    <span class="pp-usage-val" :class="{ danger: p.hourUsage > 100 }">{{ p.hourUsage }}%</span>
                  </div>
                </td>
                <td><span class="pp-status-tag" :class="statusClass(p.status)">{{ p.status }}</span></td>
                <td :class="getChangeClass(p.weekChange)">{{ p.weekChange || '-' }}</td>
              </tr>
              <tr v-if="projectProgress.length === 0"><td colspan="6" class="pp-empty-tip">暂无项目数据</td></tr>
            </tbody>
          </table>
          <div class="pp-summary-bar">
            <strong>项目汇总：</strong>进行中 {{ overview.ongoingProjects }} 个 | 已完成 {{ overview.completedProjects }} 个 | 暂停 {{ overview.pausedProjects }} 个 | 成本超支 {{ overview.costOverrun }} 个 | 工时超支 {{ overview.hourOverrun }} 个
          </div>
        </div>

        <!-- 问题跟踪 -->
        <div class="pp-section">
          <div class="pp-section-title">🐛 问题跟踪</div>
          <div class="pp-issue-summary">
            本周新增 <strong style="color: #EF4444;">{{ overview.newIssues }}</strong> 个问题，解决 <strong style="color: #10B981;">{{ overview.resolvedIssues }}</strong> 个问题，当前待处理 <strong>{{ overview.pendingIssues }}</strong> 个
          </div>
          <table class="pp-data-table">
            <thead><tr><th>问题描述</th><th>所属项目</th><th>严重程度</th><th>状态</th></tr></thead>
            <tbody>
              <tr v-for="i in issueList" :key="i.id">
                <td>{{ i.description }}</td>
                <td>{{ i.projectName }}</td>
                <td><span class="pp-level-tag" :class="severityClass(i.severity)">{{ i.severity }}</span></td>
                <td><span class="pp-status-tag" :class="issueStatusClass(i.status)">{{ i.status }}</span></td>
              </tr>
              <tr v-if="issueList.length === 0"><td colspan="4" class="pp-empty-tip">暂无问题数据</td></tr>
            </tbody>
          </table>
        </div>

        <!-- 风险管理 -->
        <div class="pp-section">
          <div class="pp-section-title">⚠️ 风险管理</div>
          <div class="pp-risk-summary">
            当前跟踪风险 <strong>{{ overview.totalRisks }}</strong> 个，其中高风险 <strong style="color: #EF4444;">{{ overview.highRisks }}</strong> 个
          </div>
          <table class="pp-data-table">
            <thead><tr><th>风险描述</th><th>所属项目</th><th>等级</th><th>状态</th><th>应对措施</th></tr></thead>
            <tbody>
              <tr v-for="r in riskList" :key="r.id" :class="{ 'pp-row-highlight': r.level === '高' }">
                <td>{{ r.description }}</td>
                <td>{{ r.projectName }}</td>
                <td><span class="pp-level-tag" :class="levelClass(r.level)">{{ r.level }}</span></td>
                <td><span class="pp-status-tag" :class="riskStatusClass(r.status)">{{ r.status }}</span></td>
                <td>{{ r.measure || '-' }}</td>
              </tr>
              <tr v-if="riskList.length === 0"><td colspan="5" class="pp-empty-tip">暂无风险数据</td></tr>
            </tbody>
          </table>
        </div>

        <!-- 任务完成情况 -->
        <div class="pp-section">
          <div class="pp-section-title">✅ 任务完成情况</div>
          <div class="pp-stats-grid pp-stats-5">
            <div class="pp-stat-card pp-stat-success">
              <div class="pp-stat-value">{{ taskStats.completed }}</div>
              <div class="pp-stat-label">已完成</div>
            </div>
            <div class="pp-stat-card pp-stat-warning">
              <div class="pp-stat-value">{{ taskStats.pending }}</div>
              <div class="pp-stat-label">待完成</div>
            </div>
            <div class="pp-stat-card pp-stat-danger">
              <div class="pp-stat-value">{{ taskStats.overdue }}</div>
              <div class="pp-stat-label">已超期</div>
            </div>
            <div class="pp-stat-card">
              <div class="pp-stat-value">{{ taskStats.total }}</div>
              <div class="pp-stat-label">总任务数</div>
            </div>
            <div class="pp-stat-card pp-stat-primary">
              <div class="pp-stat-value">{{ taskStats.rate }}%</div>
              <div class="pp-stat-label">完成率</div>
            </div>
          </div>

          <div class="pp-task-type-row">
            <span>📋 周期性任务：{{ taskStats.periodicCount }}项（完成率 {{ taskStats.periodicRate }}%）</span>
            <span>📌 一次性任务：{{ taskStats.oneoffCount }}项（完成率 {{ taskStats.oneoffRate }}%）</span>
          </div>

          <div class="pp-task-detail">
            <div class="pp-task-detail-title">本周任务详情</div>
            <table class="pp-data-table">
              <thead><tr><th>任务名称</th><th>类型</th><th>截止时间</th><th>完成人数</th><th>完成率</th></tr></thead>
              <tbody>
                <tr v-for="t in taskList" :key="t.id">
                  <td>{{ t.name }}</td>
                  <td><span class="pp-type-tag" :class="taskTypeClass(t.taskType)">{{ t.taskType }}</span></td>
                  <td>{{ t.deadline }}</td>
                  <td>{{ t.completedCount }}/{{ t.totalCount }}</td>
                  <td :class="getRateClass(t.rate)">{{ t.rate }}%</td>
                </tr>
                <tr v-if="taskList.length === 0"><td colspan="5" class="pp-empty-tip">暂无任务数据</td></tr>
              </tbody>
            </table>
          </div>

          <div class="pp-person-rank">
            <div class="pp-person-rank-title">人员完成率排名</div>
            <div class="pp-person-grid">
              <div v-for="p in personRank" :key="p.name" class="pp-person-card" :class="getPersonClass(p.rate)">
                <div class="pp-person-name">{{ p.name }}</div>
                <div class="pp-person-rate">{{ p.rate }}%</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 本周重要事项 -->
        <div class="pp-section">
          <div class="pp-section-title">📌 本周重要事项</div>
          <table class="pp-data-table">
            <thead><tr><th>日期</th><th>项目</th><th>事项描述</th></tr></thead>
            <tbody>
              <tr v-for="e in eventList" :key="e.id">
                <td>{{ e.date }}</td>
                <td>{{ e.projectName }}</td>
                <td>{{ e.description }}</td>
              </tr>
              <tr v-if="eventList.length === 0"><td colspan="3" class="pp-empty-tip">暂无重要事项</td></tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- 邮件配置内容 -->
    <div v-show="activeTab === 'email'" class="pp-page-content">
      <email-config />
    </div>

    <!-- 历史周报对话框 -->
    <el-dialog title="历史周报" :visible.sync="showHistory" width="600px" append-to-body>
      <el-table :data="historyList" size="small">
        <el-table-column prop="weekRange" label="周范围" />
        <el-table-column prop="sendTime" label="发送时间" />
        <el-table-column prop="status" label="状态" />
        <el-table-column label="操作" width="80">
          <template slot-scope="scope">
            <el-button type="text" size="mini" @click="viewHistory(scope.row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { getWeeklyReportData, sendWeeklyReport } from "@/api/report";
import EmailConfig from "./components/EmailConfig";

export default {
  name: "Report",
  components: { EmailConfig },
  data() {
    return {
      activeTab: 'report',
      showHistory: false,
      loading: false,
      weekRange: '',
      startDate: '',
      endDate: '',
      overview: {
        ongoingProjects: 0,
        completedProjects: 0,
        pausedProjects: 0,
        costOverrun: 0,
        hourOverrun: 0,
        newIssues: 0,
        resolvedIssues: 0,
        pendingIssues: 0,
        totalRisks: 0,
        highRisks: 0,
        taskRate: 0
      },
      projectProgress: [],
      issueList: [],
      riskList: [],
      taskStats: {
        completed: 0,
        pending: 0,
        overdue: 0,
        total: 0,
        rate: 0,
        periodicCount: 0,
        periodicRate: 0,
        oneoffCount: 0,
        oneoffRate: 0
      },
      taskList: [],
      personRank: [],
      eventList: [],
      historyList: []
    };
  },
  created() {
    this.initWeekRange();
    this.loadReportData();
  },
  methods: {
    initWeekRange() {
      const today = new Date();
      const dayOfWeek = today.getDay();
      const monday = new Date(today);
      monday.setDate(today.getDate() - (dayOfWeek === 0 ? 6 : dayOfWeek - 1));
      const sunday = new Date(monday);
      sunday.setDate(monday.getDate() + 6);

      const fmt = d => `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`;
      this.startDate = fmt(monday);
      this.endDate = fmt(sunday);

      const weekNum = Math.ceil((monday.getDate() + 6) / 7);
      this.weekRange = `${monday.getFullYear()}年${monday.getMonth()+1}月第${weekNum}周 (${this.startDate.slice(5)} ~ ${this.endDate.slice(5)})`;
    },
    loadReportData() {
      this.loading = true;
      getWeeklyReportData({ startDate: this.startDate, endDate: this.endDate }).then(res => {
        const data = res.data || {};
        this.overview = data.overview || this.overview;
        this.projectProgress = data.projectProgress || [];
        this.issueList = data.issueList || [];
        this.riskList = data.riskList || [];
        this.taskStats = data.taskStats || this.taskStats;
        this.taskList = data.taskList || [];
        this.personRank = data.personRank || [];
        this.eventList = data.eventList || [];
        this.loading = false;
      }).catch(() => { this.loading = false; });
    },
    handleSendEmail() {
      this.$modal.confirm('确认发送周报到指定邮箱？').then(() => {
        return sendWeeklyReport({ startDate: this.startDate, endDate: this.endDate });
      }).then(() => {
        this.$modal.msgSuccess("邮件发送成功");
      }).catch(() => {});
    },
    goTo(path) { this.$router.push(path); },
    viewHistory(row) {
      this.showHistory = false;
      this.startDate = row.startDate;
      this.endDate = row.endDate;
      this.weekRange = row.weekRange;
      this.loadReportData();
    },
    // 样式辅助
    getStageClass(status) {
      if (status === 'completed') return 'completed';
      if (status === 'warning') return 'warning';
      if (status === 'danger') return 'danger';
      return 'pending';
    },
    getStageDateClass(p) {
      if (p.stageStatus === 'completed') return 'completed';
      if (p.stageStatus === 'warning') return 'warning';
      if (p.stageStatus === 'danger') return 'danger';
      return '';
    },
    getUsageClass(val) {
      if (val > 100) return 'danger';
      if (val > 80) return 'warning';
      return 'normal';
    },
    statusClass(status) {
      const map = { '进行中': 'progress', '已完成': 'completed', '暂停': 'paused' };
      return map[status] || '';
    },
    getChangeClass(change) {
      if (!change) return '';
      if (change.includes('+') || change.includes('通过') || change.includes('成功')) return 'pp-change-success';
      if (change.includes('超') || change.includes('预警')) return 'pp-change-warning';
      return '';
    },
    severityClass(sev) {
      const map = { '高': 'danger', '中': 'warning', '低': 'success' };
      return map[sev] || '';
    },
    issueStatusClass(status) {
      const map = { '待处理': 'pending', '进行中': 'progress', '已解决': 'resolved', '已关闭': 'closed' };
      return map[status] || '';
    },
    levelClass(level) {
      const map = { '高': 'danger', '中': 'warning', '低': 'success' };
      return map[level] || '';
    },
    riskStatusClass(status) {
      const map = { '潜在': 'potential', '已发生': 'happened', '已消除': 'eliminated' };
      return map[status] || '';
    },
    taskTypeClass(type) {
      const map = { '每日': 'daily', '每周': 'weekly', '每月': 'monthly', '一次性': 'oneoff' };
      return map[type] || '';
    },
    getRateClass(rate) {
      if (rate >= 80) return 'pp-rate-success';
      if (rate >= 50) return 'pp-rate-warning';
      return 'pp-rate-danger';
    },
    getPersonClass(rate) {
      if (rate === 100) return 'pp-person-full';
      if (rate >= 75) return 'pp-person-good';
      if (rate < 50) return 'pp-person-low';
      return '';
    }
  }
};
</script>

<style lang="scss" scoped>
@import "@/assets/styles/ppmanage.scss";

.pp-page-container { background: white; border-radius: 12px; border: 1px solid #E5E7EB; }

.pp-top-nav { display: flex; justify-content: space-between; align-items: center; padding: 16px 24px; border-bottom: 1px solid #E5E7EB; background: #F9FAFB; }
.pp-breadcrumb { display: flex; align-items: center; gap: 8px; font-size: 14px; color: #6B7280; a { color: #6B7280; cursor: pointer; &:hover { color: #2563EB; } } .current { color: #1F2937; font-weight: 500; } }
.pp-page-actions { display: flex; gap: 8px; }

.pp-tabs { display: flex; background: white; border-bottom: 1px solid #E5E7EB; }
.pp-tab { padding: 14px 24px; font-size: 14px; font-weight: 500; color: #6B7280; cursor: pointer; border-bottom: 2px solid transparent; &:hover { color: #374151; } &.active { color: #2563EB; border-bottom-color: #2563EB; background: #EFF6FF; } }

.pp-page-content { padding: 24px; }

.pp-report-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 24px; padding-bottom: 16px; border-bottom: 1px solid #E5E7EB; }
.pp-report-title { font-size: 20px; font-weight: 600; color: #1F2937; }
.pp-report-date { font-size: 13px; color: #6B7280; margin-top: 4px; }

.pp-report-body { background: #F9FAFB; border: 1px solid #E5E7EB; border-radius: 8px; padding: 24px; }

.pp-section { margin-bottom: 24px; &:last-child { margin-bottom: 0; } }
.pp-section-title { font-size: 16px; font-weight: 600; color: #374151; margin-bottom: 12px; padding-bottom: 8px; border-bottom: 2px solid #2563EB; display: inline-block; }

.pp-stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 20px; }
.pp-stats-5 { grid-template-columns: repeat(5, 1fr); }
.pp-stat-card { background: white; border: 1px solid #E5E7EB; border-radius: 8px; padding: 16px; text-align: center; }
.pp-stat-value { font-size: 28px; font-weight: 700; color: #1F2937; }
.pp-stat-label { font-size: 12px; color: #6B7280; margin-top: 4px; }
.pp-stat-success { .pp-stat-value { color: #10B981; } }
.pp-stat-warning { .pp-stat-value { color: #F59E0B; } }
.pp-stat-danger { .pp-stat-value { color: #EF4444; } }
.pp-stat-primary { .pp-stat-value { color: #2563EB; } }

.pp-summary-bar { margin-top: 12px; padding: 12px; background: #EFF6FF; border-radius: 6px; font-size: 13px; color: #374151; }

.pp-stage-cell { display: flex; align-items: center; gap: 6px; flex-wrap: wrap; }
.pp-stage-dot { width: 20px; height: 20px; border-radius: 50%; display: flex; align-items: center; justify-content: center; color: white; font-size: 11px; &.completed { background: #10B981; content: "✓"; } &.warning { background: #F59E0B; } &.danger { background: #EF4444; } &.pending { background: #9CA3AF; } }
.pp-stage-name { font-weight: 600; color: #374151; }
.pp-stage-date { font-size: 11px; color: #6B7280; &.completed { color: #10B981; } &.warning { color: #F59E0B; } &.danger { color: #EF4444; } }

.pp-usage-mini { display: flex; align-items: center; gap: 6px; }
.pp-usage-bar-mini { width: 60px; height: 4px; background: #E5E7EB; border-radius: 2px; overflow: hidden; }
.pp-usage-fill-mini { height: 100%; border-radius: 2px; &.normal { background: #10B981; } &.warning { background: #F59E0B; } &.danger { background: #EF4444; } }
.pp-usage-val { font-size: 12px; font-weight: 600; color: #374151; &.danger { color: #EF4444; } }

.pp-change-success { color: #10B981; }
.pp-change-warning { color: #F59E0B; }

.pp-issue-summary, .pp-risk-summary { margin-bottom: 12px; font-size: 13px; color: #6B7280; }

.pp-row-highlight { background: #FEF2F2; }

.pp-task-type-row { margin-top: 16px; display: flex; gap: 24px; font-size: 13px; color: #6B7280; }
.pp-task-detail { margin-top: 16px; }
.pp-task-detail-title { font-size: 14px; font-weight: 600; color: #374151; margin-bottom: 8px; }

.pp-person-rank { margin-top: 16px; }
.pp-person-rank-title { font-size: 14px; font-weight: 600; color: #374151; margin-bottom: 8px; }
.pp-person-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 8px; }
.pp-person-card { border-radius: 6px; padding: 8px; text-align: center; border: 1px solid #E5E7EB; background: #F9FAFB; }
.pp-person-name { font-size: 12px; font-weight: 600; }
.pp-person-rate { font-size: 16px; font-weight: 700; }
.pp-person-full { background: #F0FDF4; border-color: #10B981; .pp-person-rate { color: #10B981; } }
.pp-person-good { background: #F9FAFB; border-color: #E5E7EB; .pp-person-rate { color: #F59E0B; } }
.pp-person-low { background: #FEF2F2; border-color: #EF4444; .pp-person-rate { color: #EF4444; } }

.pp-rate-success { color: #10B981; font-weight: 600; }
.pp-rate-warning { color: #F59E0B; font-weight: 600; }
.pp-rate-danger { color: #EF4444; font-weight: 600; }

.pp-type-tag { padding: 2px 8px; border-radius: 4px; font-size: 11px; &.daily { background: #DBEAFE; color: #1D4ED8; } &.weekly { background: #FEF3C7; color: #B45309; } &.monthly { background: #D1FAE5; color: #059669; } &.oneoff { background: #F3E8FF; color: #7C3AED; } }

.pp-status-tag { padding: 2px 8px; border-radius: 4px; font-size: 11px; font-weight: 500; &.progress { background: #DBEAFE; color: #1D4ED8; } &.completed { background: #D1FAE5; color: #059669; } &.paused { background: #FEF3C7; color: #B45309; } &.pending { background: #FEF3C7; color: #B45309; } &.resolved { background: #D1FAE5; color: #059669; } &.closed { background: #F3F4F6; color: #6B7280; } &.potential { background: #FEF3C7; color: #B45309; } &.happened { background: #FEE2E2; color: #DC2626; } &.eliminated { background: #D1FAE5; color: #059669; } }

.pp-level-tag { padding: 2px 8px; border-radius: 4px; font-size: 11px; font-weight: 600; &.danger { color: #EF4444; } &.warning { color: #F59E0B; } &.success { color: #10B981; } }

.pp-empty-tip { text-align: center; padding: 24px; color: #9CA3AF; font-size: 13px; }
</style>