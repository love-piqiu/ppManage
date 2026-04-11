<template>
  <div class="pp-page-container">
    <!-- 顶部导航 -->
    <div class="pp-top-nav">
      <div class="pp-breadcrumb">
        <a @click="goTo('/dashboard')">首页</a>
        <span>/</span>
        <span class="current">项目管理</span>
      </div>
      <div class="pp-page-actions">
        <button class="pp-btn pp-btn-secondary" @click="handleExport">导出Excel</button>
        <button class="pp-btn pp-btn-primary" @click="handleAdd">+ 新增项目</button>
      </div>
    </div>

    <!-- 筛选区域 -->
    <div class="pp-filter-bar">
      <div class="pp-filter-group">
        <span class="pp-filter-label">项目状态</span>
        <el-select v-model="queryParams.status" placeholder="全部状态" clearable size="small" class="pp-filter-select" @change="handleQuery">
          <el-option label="全部状态" value="" />
          <el-option label="进行中" value="进行中" />
          <el-option label="已完成" value="已完成" />
          <el-option label="暂停" value="暂停" />
        </el-select>
      </div>
      <div class="pp-filter-group">
        <span class="pp-filter-label">项目经理</span>
        <el-select v-model="queryParams.pmId" placeholder="全部人员" clearable filterable size="small" class="pp-filter-select" @change="handleQuery">
          <el-option v-for="item in personOptions" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </div>
      <div class="pp-filter-group">
        <el-input v-model="queryParams.keyword" placeholder="搜索项目名称/客户..." clearable size="small" class="pp-filter-input" @keyup.enter.native="handleQuery" />
      </div>
      <div class="pp-filter-group">
        <button class="pp-btn pp-btn-secondary" @click="handleQuery">搜索</button>
      </div>
    </div>

    <!-- 统计栏 -->
    <div class="pp-stats-bar">
      <div class="pp-stat-item">
        共 <span class="pp-stat-num" style="color: #111827;">{{ total }}</span> 个项目
      </div>
      <div class="pp-stat-item">
        进行中 <span class="pp-stat-num" style="color: #2563EB;">{{ statusCounts.ongoing }}</span>
      </div>
      <div class="pp-stat-item">
        已完成 <span class="pp-stat-num" style="color: #10B981;">{{ statusCounts.completed }}</span>
      </div>
      <div class="pp-stat-item">
        暂停 <span class="pp-stat-num" style="color: #F59E0B;">{{ statusCounts.paused }}</span>
      </div>
      <div class="pp-stat-item pp-stat-divider">
        里程碑预警 <span class="pp-stat-num" style="color: #EF4444;">{{ alertCount }}</span> 个项目
      </div>
      <div class="pp-stat-item pp-stat-right">
        按开始日期降序排列
      </div>
    </div>

    <!-- 数据表格 -->
    <table class="pp-data-table" v-loading="loading">
      <thead>
        <tr>
          <th style="min-width: 140px;">项目名称</th>
          <th>客户</th>
          <th>项目经理</th>
          <th style="min-width: 100px;">使用比例</th>
          <th style="min-width: 160px;">里程碑时间点</th>
          <th style="min-width: 180px;">开票情况</th>
          <th>状态</th>
          <th style="min-width: 120px;">操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(row, index) in projectList" :key="index" :style="{ opacity: row.status === '已完成' || row.status === '暂停' ? 0.7 : 1 }">
          <td>
            <div class="pp-project-name-cell">
              <a class="pp-project-link" :class="{ danger: row.hasAlert }" @click="handleDetail(row)">{{ row.name }}</a>
              <span class="pp-alert-badge issue" v-if="row.issueCount > 0" @click="goToIssues(row)">⚠ {{ row.issueCount }}问题</span>
              <span class="pp-alert-badge risk" v-if="row.riskCount > 0" @click="goToRisks(row)">⚡ {{ row.riskCount }}风险</span>
            </div>
          </td>
          <td>{{ row.customer || '-' }}</td>
          <td>{{ row.pmName || '-' }}</td>
          <td>
            <div class="pp-usage-cell">
              <div class="pp-usage-row">
                <span class="pp-usage-label">成本</span>
                <div class="pp-usage-bar">
                  <div class="pp-usage-fill" :class="getUsageClass(row.costUsage)" :style="{ width: Math.min(row.costUsage, 100) + '%' }"></div>
                </div>
                <span class="pp-usage-value" :class="{ danger: row.costUsage > 100 }">{{ row.costUsage || 0 }}%</span>
              </div>
              <div class="pp-usage-row">
                <span class="pp-usage-label">工时</span>
                <div class="pp-usage-bar">
                  <div class="pp-usage-fill" :class="getUsageClass(row.hourUsage)" :style="{ width: Math.min(row.hourUsage, 100) + '%' }"></div>
                </div>
                <span class="pp-usage-value" :class="{ danger: row.hourUsage > 100 }">{{ row.hourUsage || 0 }}%</span>
              </div>
            </div>
          </td>
          <td>
            <div class="pp-milestone-cell">
              <div class="pp-milestone-item" v-for="(m, mi) in row.milestones" :key="mi">
                <span class="pp-milestone-label">{{ m.name }}</span>
                <span class="pp-milestone-date" :class="getMilestoneClass(m)">{{ m.date || '待定' }}</span>
                <span class="pp-milestone-dot" :class="getMilestoneDotClass(m)"></span>
              </div>
            </div>
          </td>
          <td>
            <div class="pp-invoice-cell">
              <div class="pp-invoice-header">
                <span>合同: ¥{{ row.contractAmount || 0 }}万</span>
                <span class="pp-invoice-total">已开: ¥{{ row.invoicedAmount || 0 }}万</span>
              </div>
              <div class="pp-invoice-item" v-for="(inv, ii) in row.invoices" :key="ii">
                <span class="pp-invoice-amount">¥{{ inv.amount }}万</span>
                <span class="pp-invoice-percent">{{ inv.percent }}%</span>
                <span class="pp-invoice-type" :class="inv.type">{{ inv.type }}</span>
              </div>
              <span class="pp-invoice-add" @click="addInvoice(row)">+ 添加开票</span>
            </div>
          </td>
          <td>
            <span class="pp-status-tag" :class="statusClass(row.status)">{{ row.status }}</span>
          </td>
          <td>
            <div class="pp-action-btns">
              <button class="pp-action-btn" @click="handleDetail(row)">详情</button>
              <button class="pp-action-btn" @click="handleUpdate(row)">编辑</button>
              <button class="pp-action-btn danger" @click="handleDelete(row)">删除</button>
            </div>
          </td>
        </tr>
        <tr v-if="projectList.length === 0">
          <td colspan="8" class="pp-empty-tip">暂无项目数据</td>
        </tr>
      </tbody>
    </table>

    <!-- 分页 -->
    <div class="pp-pagination">
      <span>共 {{ total }} 条记录，第 {{ queryParams.pageNum }}/{{ totalPages }} 页</span>
      <div class="pp-pagination-btns">
        <button class="pp-page-btn" :disabled="queryParams.pageNum <= 1" @click="changePage(-1)">上一页</button>
        <button class="pp-page-btn" :class="{ active: queryParams.pageNum === p }" v-for="p in visiblePages" :key="p" @click="goToPage(p)">{{ p }}</button>
        <button class="pp-page-btn" :disabled="queryParams.pageNum >= totalPages" @click="changePage(1)">下一页</button>
      </div>
    </div>

    <!-- 添加/编辑项目对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body custom-class="pp-dialog">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="项目名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入项目名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户" prop="customer">
              <el-input v-model="form.customer" placeholder="请输入客户" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="项目经理" prop="pmId">
              <el-select v-model="form.pmId" placeholder="请选择项目经理" filterable @change="handlePmChange">
                <el-option v-for="item in personOptions" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="销售人员" prop="salesId">
              <el-select v-model="form.salesId" placeholder="请选择销售人员" filterable @change="handleSalesChange">
                <el-option v-for="item in personOptions" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="成本(万)" prop="cost">
              <el-input-number v-model="form.cost" :precision="2" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="已用成本(万)" prop="costUsed">
              <el-input-number v-model="form.costUsed" :precision="2" :min="0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="总工时(人天)" prop="workHours">
              <el-input-number v-model="form.workHours" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="已用工时(人天)" prop="workHoursUsed">
              <el-input-number v-model="form.workHoursUsed" :min="0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="进度(%)" prop="progress">
              <el-input-number v-model="form.progress" :min="0" :max="100" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="当前阶段" prop="stage">
              <el-input v-model="form.stage" placeholder="请输入当前阶段" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态">
                <el-option label="进行中" value="进行中" />
                <el-option label="已完成" value="已完成" />
                <el-option label="暂停" value="暂停" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker v-model="form.startDate" type="date" value-format="yyyy-MM-dd" placeholder="选择开始日期" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="计划结束" prop="endDate">
              <el-date-picker v-model="form.endDate" type="date" value-format="yyyy-MM-dd" placeholder="选择计划结束日期" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="实际结束" prop="actualEndDate">
              <el-date-picker v-model="form.actualEndDate" type="date" value-format="yyyy-MM-dd" placeholder="选择实际结束日期" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="合同金额(万)" prop="contractAmount">
              <el-input-number v-model="form.contractAmount" :precision="2" :min="0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="pp-dialog-footer">
        <button class="pp-btn pp-btn-secondary" @click="cancel">取消</button>
        <button class="pp-btn pp-btn-primary" @click="submitForm">确定</button>
      </div>
    </el-dialog>

    <!-- 项目详情对话框 -->
    <el-dialog title="项目详情" :visible.sync="detailOpen" width="900px" append-to-body custom-class="pp-dialog pp-detail-dialog">
      <div class="pp-detail-header">
        <div class="pp-detail-top">
          <div>
            <div class="pp-project-title">{{ detailData.name }}</div>
            <div class="pp-project-customer">客户：{{ detailData.customer }}</div>
          </div>
          <div class="pp-detail-status">
            <span class="pp-status-tag" :class="statusClass(detailData.status)">{{ detailData.status }}</span>
          </div>
        </div>

        <!-- 使用比例 -->
        <div class="pp-usage-section">
          <div class="pp-usage-card" :class="{ danger: detailData.costUsage > 100 }">
            <div class="pp-usage-card-header">
              <span class="pp-usage-card-label">成本使用</span>
              <span class="pp-usage-card-value" :class="{ danger: detailData.costUsage > 100 }">{{ detailData.costUsage || 0 }}%</span>
            </div>
            <div class="pp-usage-card-bar">
              <div class="pp-usage-card-fill" :style="{ width: Math.min(detailData.costUsage || 0, 100) + '%' }"></div>
            </div>
            <div class="pp-usage-card-footer">
              <span>已用: <strong :class="{ danger: detailData.costUsage > 100 }">¥{{ detailData.costUsed || 0 }}万</strong></span>
              <span>预算: ¥{{ detailData.cost || 0 }}万</span>
            </div>
          </div>
          <div class="pp-usage-card" :class="{ warning: detailData.hourUsage > 80 }">
            <div class="pp-usage-card-header">
              <span class="pp-usage-card-label">工时使用</span>
              <span class="pp-usage-card-value" :class="{ warning: detailData.hourUsage > 80 }">{{ detailData.hourUsage || 0 }}%</span>
            </div>
            <div class="pp-usage-card-bar">
              <div class="pp-usage-card-fill hour" :style="{ width: Math.min(detailData.hourUsage || 0, 100) + '%' }"></div>
            </div>
            <div class="pp-usage-card-footer">
              <span>已用: <strong>{{ detailData.workHoursUsed || 0 }}人天</strong></span>
              <span>预算: {{ detailData.workHours || 0 }}人天</span>
            </div>
          </div>
        </div>

        <!-- 信息网格 -->
        <div class="pp-info-grid">
          <div class="pp-info-item">
            <div class="pp-info-label">项目经理</div>
            <div class="pp-info-value link">{{ detailData.pmName || '-' }}</div>
          </div>
          <div class="pp-info-item">
            <div class="pp-info-label">销售人员</div>
            <div class="pp-info-value">{{ detailData.salesName || '-' }}</div>
          </div>
          <div class="pp-info-item">
            <div class="pp-info-label">项目成本</div>
            <div class="pp-info-value">¥ {{ detailData.cost || 0 }} 万</div>
          </div>
          <div class="pp-info-item">
            <div class="pp-info-label">合同金额</div>
            <div class="pp-info-value">¥ {{ detailData.contractAmount || 0 }} 万</div>
          </div>
          <div class="pp-info-item">
            <div class="pp-info-label">已开票</div>
            <div class="pp-info-value success">¥ {{ detailData.invoicedAmount || 0 }} 万</div>
          </div>
        </div>

        <!-- 里程碑时间点 -->
        <div class="pp-milestone-section">
          <div class="pp-milestone-header">里程碑时间点</div>
          <div class="pp-milestone-grid">
            <div class="pp-milestone-card" v-for="(m, mi) in detailMilestones" :key="mi" :class="getMilestoneCardClass(m)">
              <div class="pp-milestone-card-label">{{ m.name }}</div>
              <div class="pp-milestone-card-date" :class="getMilestoneClass(m)">{{ m.date || '待定' }}</div>
              <div class="pp-milestone-card-status">{{ getMilestoneStatus(m) }}</div>
            </div>
          </div>
        </div>

        <!-- 关联数据统计 -->
        <div class="pp-related-cards">
          <div class="pp-related-card danger" @click="goToIssues(detailData)">
            <div class="pp-related-num">{{ detailData.issueCount || 0 }}</div>
            <div class="pp-related-label">待处理问题</div>
          </div>
          <div class="pp-related-card warning" @click="goToRisks(detailData)">
            <div class="pp-related-num">{{ detailData.riskCount || 0 }}</div>
            <div class="pp-related-label">潜在风险</div>
          </div>
          <div class="pp-related-card primary">
            <div class="pp-related-num">{{ detailData.personCount || 0 }}</div>
            <div class="pp-related-label">参与人员</div>
          </div>
        </div>
      </div>

      <!-- Tab 导航 -->
      <div class="pp-tab-nav">
        <div class="pp-tab-item" :class="{ active: activeTab === 'info' }" @click="activeTab = 'info'">基本信息</div>
        <div class="pp-tab-item" :class="{ active: activeTab === 'milestone' }" @click="activeTab = 'milestone'">重要事项 <span class="pp-tab-badge">{{ milestones.length }}</span></div>
        <div class="pp-tab-item" :class="{ active: activeTab === 'issues' }" @click="activeTab = 'issues'">问题 <span class="pp-tab-badge">{{ detailData.issueCount || 0 }}</span></div>
        <div class="pp-tab-item" :class="{ active: activeTab === 'risks' }" @click="activeTab = 'risks'">风险 <span class="pp-tab-badge">{{ detailData.riskCount || 0 }}</span></div>
      </div>

      <!-- Tab 内容 -->
      <div class="pp-tab-content">
        <!-- 基本信息 -->
        <div v-show="activeTab === 'info'">
          <table class="pp-detail-table">
            <tr><td class="label">开始日期</td><td>{{ detailData.startDate || '-' }}</td><td class="label">计划结束</td><td>{{ detailData.endDate || '-' }}</td></tr>
            <tr><td class="label">实际结束</td><td>{{ detailData.actualEndDate || '-' }}</td><td class="label">当前阶段</td><td>{{ detailData.stage || '-' }}</td></tr>
            <tr><td class="label">进度</td><td>{{ detailData.progress || 0 }}%</td><td class="label">备注</td><td>{{ detailData.remark || '-' }}</td></tr>
          </table>
        </div>

        <!-- 重要事项时间线 -->
        <div v-show="activeTab === 'milestone'">
          <div class="pp-timeline">
            <div class="pp-timeline-item" v-for="(m, mi) in milestones" :key="mi">
              <div class="pp-timeline-dot"></div>
              <div class="pp-timeline-date">{{ m.recordDate }}</div>
              <div class="pp-timeline-content">{{ m.description }}</div>
              <div class="pp-timeline-actions">
                <button @click="editMilestone(m)">编辑</button>
                <button class="danger" @click="handleDelMilestone(m)">删除</button>
              </div>
            </div>
            <div v-if="milestones.length === 0" class="pp-empty-tip">暂无重要事项</div>
          </div>
          <div class="pp-add-milestone">
            <el-input v-model="milestoneForm.description" placeholder="输入重要事项描述..." style="flex: 1" />
            <el-date-picker v-model="milestoneForm.recordDate" type="date" value-format="yyyy-MM-dd" placeholder="选择日期" />
            <button class="pp-btn pp-btn-primary" @click="handleAddMilestone">添加</button>
          </div>
        </div>

        <!-- 问题列表 -->
        <div v-show="activeTab === 'issues'">
          <table class="pp-data-table">
            <thead><tr><th>描述</th><th>严重程度</th><th>状态</th><th>负责人</th></tr></thead>
            <tbody>
              <tr v-for="(issue, ii) in projectIssues" :key="ii">
                <td>{{ issue.description }}</td>
                <td><span class="pp-level-tag" :class="severityClass(issue.severity)">{{ issue.severity }}</span></td>
                <td><span class="pp-status-tag" :class="issueStatusClass(issue.status)">{{ issue.status }}</span></td>
                <td>{{ issue.ownerName }}</td>
              </tr>
              <tr v-if="projectIssues.length === 0"><td colspan="4" class="pp-empty-tip">暂无问题</td></tr>
            </tbody>
          </table>
        </div>

        <!-- 风险列表 -->
        <div v-show="activeTab === 'risks'">
          <table class="pp-data-table">
            <thead><tr><th>描述</th><th>等级</th><th>状态</th><th>应对措施</th></tr></thead>
            <tbody>
              <tr v-for="(risk, ri) in projectRisks" :key="ri">
                <td>{{ risk.description }}</td>
                <td><span class="pp-level-tag" :class="levelClass(risk.level)">{{ risk.level }}</span></td>
                <td><span class="pp-status-tag" :class="riskStatusClass(risk.status)">{{ risk.status }}</span></td>
                <td>{{ risk.measure || '-' }}</td>
              </tr>
              <tr v-if="projectRisks.length === 0"><td colspan="4" class="pp-empty-tip">暂无风险</td></tr>
            </tbody>
          </table>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listProject, getProject, delProject, addProject, updateProject, getMilestones, addMilestone, delMilestone } from "@/api/system/project";
import { listPersonAll } from "@/api/system/person";
import { listIssueByProject } from "@/api/system/issue";
import { listRiskByProject } from "@/api/system/risk";

export default {
  name: "Project",
  data() {
    return {
      loading: false,
      total: 0,
      projectList: [],
      personOptions: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        status: '',
        pmId: '',
        keyword: ''
      },
      title: "",
      open: false,
      detailOpen: false,
      detailData: {},
      milestones: [],
      projectIssues: [],
      projectRisks: [],
      milestoneForm: {},
      activeTab: 'milestone',
      detailMilestones: [],
      statusCounts: { ongoing: 0, completed: 0, paused: 0 },
      alertCount: 0,
      form: {},
      rules: {
        name: [{ required: true, message: "项目名称不能为空", trigger: "blur" }]
      }
    };
  },
  computed: {
    totalPages() {
      return Math.ceil(this.total / this.queryParams.pageSize) || 1;
    },
    visiblePages() {
      const pages = [];
      const start = Math.max(1, this.queryParams.pageNum - 2);
      const end = Math.min(this.totalPages, this.queryParams.pageNum + 2);
      for (let i = start; i <= end; i++) pages.push(i);
      return pages;
    }
  },
  created() {
    this.getList();
    this.getPersonList();
  },
  methods: {
    getList() {
      this.loading = true;
      listProject(this.queryParams).then(response => {
        this.projectList = (response.rows || []).map(row => {
          // 计算使用比例
          const costUsage = row.cost > 0 ? Math.round((row.costUsed || 0) * 100 / row.cost) : 0;
          const hourUsage = row.workHours > 0 ? Math.round((row.workHoursUsed || 0) * 100 / row.workHours) : 0;

          // 解析里程碑
          const milestones = this.parseMilestones(row);

          return {
            ...row,
            costUsage,
            hourUsage,
            milestones,
            invoices: row.invoices || [],
            hasAlert: (row.issueCount > 0 || row.riskCount > 0)
          };
        });
        this.total = response.total || 0;

        // 计算状态统计
        this.statusCounts = {
          ongoing: this.projectList.filter(p => p.status === '进行中').length,
          completed: this.projectList.filter(p => p.status === '已完成').length,
          paused: this.projectList.filter(p => p.status === '暂停').length
        };

        // 计算预警数量
        this.alertCount = this.projectList.filter(p => p.hasMilestoneAlert).length;

        this.loading = false;
      });
    },
    parseMilestones(row) {
      // 默认里程碑配置
      const defaultMilestones = [
        { name: '需求', date: row.reqDate },
        { name: 'UAT', date: row.uatDate },
        { name: '上线', date: row.launchDate },
        { name: '验收', date: row.acceptDate }
      ];
      return defaultMilestones;
    },
    getPersonList() {
      listPersonAll().then(response => {
        this.personOptions = response.data || [];
      });
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 10, status: '', pmId: '', keyword: '' };
      this.handleQuery();
    },
    changePage(delta) {
      this.queryParams.pageNum += delta;
      this.getList();
    },
    goToPage(page) {
      this.queryParams.pageNum = page;
      this.getList();
    },
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加项目";
    },
    handleUpdate(row) {
      this.reset();
      getProject(row.id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改项目";
      });
    },
    handleDetail(row) {
      getProject(row.id).then(response => {
        this.detailData = response.data;
        this.detailOpen = true;
        this.activeTab = 'milestone';
        this.loadMilestones(row.id);
        this.loadProjectIssues(row.id);
        this.loadProjectRisks(row.id);
        this.detailMilestones = this.parseMilestones(response.data);
      });
    },
    loadMilestones(projectId) {
      getMilestones(projectId).then(response => {
        this.milestones = response.data || [];
      });
    },
    loadProjectIssues(projectId) {
      listIssueByProject(projectId).then(response => {
        this.projectIssues = response.data || [];
      });
    },
    loadProjectRisks(projectId) {
      listRiskByProject(projectId).then(response => {
        this.projectRisks = response.data || [];
      });
    },
    reset() {
      this.form = {
        id: undefined,
        name: undefined,
        customer: undefined,
        pmId: undefined,
        pmName: undefined,
        salesId: undefined,
        salesName: undefined,
        cost: undefined,
        costUsed: undefined,
        workHours: undefined,
        workHoursUsed: undefined,
        progress: 0,
        stage: undefined,
        status: "进行中",
        startDate: undefined,
        endDate: undefined,
        actualEndDate: undefined,
        contractAmount: undefined,
        remark: undefined
      };
      this.resetForm("form");
    },
    cancel() {
      this.open = false;
      this.reset();
    },
    handlePmChange(val) {
      const person = this.personOptions.find(p => p.id === val);
      this.form.pmName = person ? person.name : '';
    },
    handleSalesChange(val) {
      const person = this.personOptions.find(p => p.id === val);
      this.form.salesName = person ? person.name : '';
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != undefined) {
            updateProject(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addProject(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    handleDelete(row) {
      this.$modal.confirm('是否确认删除项目"' + row.name + '"？').then(() => {
        return delProject(row.id);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    handleExport() {
      this.download('system/project/export', { ...this.queryParams }, `project_${new Date().getTime()}.xlsx`);
    },
    handleAddMilestone() {
      if (!this.milestoneForm.recordDate || !this.milestoneForm.description) {
        this.$modal.msgWarning("请填写完整信息");
        return;
      }
      this.milestoneForm.projectId = this.detailData.id;
      addMilestone(this.milestoneForm).then(response => {
        this.$modal.msgSuccess("添加成功");
        this.milestoneForm = {};
        this.loadMilestones(this.detailData.id);
      });
    },
    handleDelMilestone(row) {
      this.$modal.confirm('是否确认删除该重要事项？').then(() => {
        return delMilestone(row.id);
      }).then(() => {
        this.loadMilestones(this.detailData.id);
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    editMilestone(row) {
      this.$modal.msgWarning("编辑功能开发中");
    },
    addInvoice(row) {
      this.$modal.msgWarning("开票功能开发中");
    },
    goTo(path) {
      this.$router.push(path);
    },
    goToIssues(row) {
      this.$router.push({ path: '/system/issue', query: { projectId: row.id || row.projectId } });
    },
    goToRisks(row) {
      this.$router.push({ path: '/system/risk', query: { projectId: row.id || row.projectId } });
    },
    // 样式辅助方法
    getUsageClass(value) {
      if (value > 100) return 'danger';
      if (value > 80) return 'warning';
      return 'normal';
    },
    statusClass(status) {
      const map = { '进行中': 'ongoing', '已完成': 'completed', '暂停': 'paused' };
      return map[status] || '';
    },
    severityClass(severity) {
      const map = { '高': 'danger', '中': 'warning', '低': 'success' };
      return map[severity] || '';
    },
    levelClass(level) {
      const map = { '高': 'danger', '中': 'warning', '低': 'success' };
      return map[level] || '';
    },
    issueStatusClass(status) {
      const map = { '待处理': 'danger', '进行中': 'warning', '已解决': 'success', '已关闭': 'info' };
      return map[status] || '';
    },
    riskStatusClass(status) {
      const map = { '潜在': 'warning', '已发生': 'danger', '已消除': 'success' };
      return map[status] || '';
    },
    getMilestoneClass(m) {
      if (!m.date) return '';
      const today = new Date();
      const date = new Date(m.date);
      const diff = (date - today) / (1000 * 60 * 60 * 24);
      if (m.status === 'completed') return 'completed';
      if (diff < 0) return 'danger';
      if (diff <= 7) return 'warning';
      return '';
    },
    getMilestoneDotClass(m) {
      if (m.status === 'completed') return 'completed';
      const dateClass = this.getMilestoneClass(m);
      if (dateClass === 'danger') return 'danger';
      if (dateClass === 'warning') return 'warning';
      return 'pending';
    },
    getMilestoneCardClass(m) {
      const cls = this.getMilestoneClass(m);
      if (cls === 'danger') return 'danger';
      if (cls === 'warning') return 'warning';
      return '';
    },
    getMilestoneStatus(m) {
      if (m.status === 'completed') return '已完成';
      const dateClass = this.getMilestoneClass(m);
      if (dateClass === 'danger') return '已超期';
      if (dateClass === 'warning') return '一周内到期';
      return '待进行';
    }
  }
};
</script>

<style lang="scss" scoped>
@import "@/assets/styles/ppmanage.scss";

/* 页面容器 */
.pp-page-container {
  background: white;
  border-radius: 12px;
  border: 1px solid #E5E7EB;
  overflow: hidden;
}

/* 顶部导航 */
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

  a {
    color: #6B7280;
    cursor: pointer;

    &:hover { color: #2563EB; }
  }

  .current {
    color: #1F2937;
    font-weight: 500;
  }
}

.pp-page-actions {
  display: flex;
  gap: 8px;
}

/* 筛选区域 */
.pp-filter-bar {
  display: flex;
  gap: 16px;
  padding: 16px 24px;
  background: white;
  border-bottom: 1px solid #E5E7EB;
  flex-wrap: wrap;
}

.pp-filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pp-filter-label {
  font-size: 13px;
  color: #6B7280;
}

.pp-filter-select {
  min-width: 140px;
}

.pp-filter-input {
  width: 200px;
}

/* 统计栏 */
.pp-stats-bar {
  display: flex;
  gap: 32px;
  padding: 12px 24px;
  background: #F9FAFB;
  border-bottom: 1px solid #E5E7EB;
  font-size: 13px;
}

.pp-stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pp-stat-num {
  font-weight: 600;
  font-size: 16px;
}

.pp-stat-divider {
  margin-left: 16px;
  padding-left: 16px;
  border-left: 1px solid #E5E7EB;
}

.pp-stat-right {
  margin-left: auto;
  font-size: 12px;
  color: #9CA3AF;
}

/* 数据表格 */
.pp-data-table {
  width: 100%;
  border-collapse: collapse;

  th, td {
    padding: 12px 16px;
    text-align: left;
    font-size: 13px;
    border-bottom: 1px solid #F3F4F6;
  }

  th {
    color: #6B7280;
    font-weight: 500;
    background: #F9FAFB;
  }

  tr:hover td { background: #F9FAFB; }
  tr:last-child td { border-bottom: none; }
}

/* 项目名称带预警标识 */
.pp-project-name-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pp-project-link {
  color: #2563EB;
  cursor: pointer;
  font-weight: 500;

  &:hover { text-decoration: underline; }
  &.danger { color: #EF4444; }
}

.pp-alert-badge {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  padding: 2px 6px;
  border-radius: 10px;
  font-size: 10px;
  font-weight: 500;
  cursor: pointer;

  &.issue {
    background: #fef2f2;
    color: #EF4444;
    border: 1px solid #fecaca;
  }

  &.risk {
    background: #fffbeb;
    color: #F59E0B;
    border: 1px solid #fde68a;
  }

  &:hover { transform: scale(1.05); }
}

/* 使用比例 */
.pp-usage-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.pp-usage-row {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
}

.pp-usage-label {
  color: #6B7280;
  min-width: 36px;
}

.pp-usage-bar {
  flex: 1;
  height: 4px;
  background: #F3F4F6;
  border-radius: 2px;
  overflow: hidden;
}

.pp-usage-fill {
  height: 100%;
  border-radius: 2px;

  &.normal { background: #10B981; }
  &.warning { background: #F59E0B; }
  &.danger { background: #EF4444; }
}

.pp-usage-value {
  min-width: 42px;
  text-align: right;
  font-weight: 500;

  &.danger { color: #EF4444; }
}

/* 里程碑时间点 */
.pp-milestone-cell {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.pp-milestone-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
}

.pp-milestone-label {
  color: #9CA3AF;
  min-width: 32px;
}

.pp-milestone-date {
  color: #4B5563;
  font-size: 11px;

  &.completed { color: #10B981; }
  &.warning { color: #F59E0B; font-weight: 500; }
  &.danger { color: #EF4444; font-weight: 600; }
}

.pp-milestone-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  flex-shrink: 0;

  &.completed { background: #10B981; }
  &.warning { background: #F59E0B; }
  &.danger { background: #EF4444; animation: pulse 1.5s infinite; }
  &.pending { background: #D1D5DB; }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

/* 开票情况 */
.pp-invoice-cell {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 180px;
}

.pp-invoice-header {
  display: flex;
  justify-content: space-between;
  font-size: 11px;
  color: #6B7280;
  padding-bottom: 4px;
  border-bottom: 1px dashed #E5E7EB;
}

.pp-invoice-total { font-weight: 500; }

.pp-invoice-item {
  display: grid;
  grid-template-columns: 50px 40px 50px;
  gap: 4px;
  font-size: 11px;
}

.pp-invoice-amount { color: #374151; font-weight: 500; }
.pp-invoice-percent { color: #6B7280; }

.pp-invoice-type {
  padding: 1px 4px;
  border-radius: 2px;
  font-size: 10px;
  text-align: center;

  &.预付款 { background: #eff6ff; color: #2563EB; }
  &.进度款 { background: #f0fdf4; color: #10B981; }
  &.验收款 { background: #fffbeb; color: #F59E0B; }
  &.尾款 { background: #f5f3ff; color: #6366F1; }
}

.pp-invoice-add {
  font-size: 11px;
  color: #2563EB;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 2px;
  margin-top: 2px;

  &:hover { text-decoration: underline; }
}

/* 分页 */
.pp-pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: #F9FAFB;
  border-top: 1px solid #E5E7EB;
  font-size: 13px;
  color: #6B7280;
}

.pp-pagination-btns {
  display: flex;
  gap: 4px;
}

.pp-page-btn {
  padding: 6px 12px;
  border: 1px solid #E5E7EB;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;

  &.active {
    background: #2563EB;
    color: white;
    border-color: #2563EB;
  }

  &:hover:not(.active):not(:disabled) {
    border-color: #2563EB;
    color: #2563EB;
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}

/* 详情页 */
.pp-detail-header {
  padding: 24px;
  border-bottom: 1px solid #E5E7EB;
}

.pp-detail-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.pp-project-title {
  font-size: 24px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 4px;
}

.pp-project-customer {
  font-size: 14px;
  color: #6B7280;
}

/* 使用比例卡片 */
.pp-usage-section {
  display: flex;
  gap: 32px;
  margin-bottom: 20px;
}

.pp-usage-card {
  flex: 1;
  padding: 16px;
  background: #F9FAFB;
  border-radius: 8px;
  border: 1px solid #E5E7EB;

  &.danger {
    background: #fef2f2;
    border-color: #fecaca;
  }

  &.warning {
    background: #fffbeb;
    border-color: #fde68a;
  }
}

.pp-usage-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.pp-usage-card-label {
  font-size: 14px;
  color: #4B5563;
  font-weight: 500;
}

.pp-usage-card-value {
  font-size: 20px;
  font-weight: 700;
  color: #2563EB;

  &.danger { color: #EF4444; }
  &.warning { color: #F59E0B; }
}

.pp-usage-card-bar {
  height: 8px;
  background: #E5E7EB;
  border-radius: 4px;
  overflow: hidden;
}

.pp-usage-card-fill {
  height: 100%;
  border-radius: 4px;
  background: linear-gradient(90deg, #10B981 0%, #F59E0B 70%, #EF4444 95%);

  &.hour {
    background: linear-gradient(90deg, #10B981 0%, #F59E0B 80%);
  }
}

.pp-usage-card-footer {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #6B7280;
  margin-top: 6px;

  strong {
    color: #1F2937;
    &.danger { color: #EF4444; }
  }
}

/* 信息网格 */
.pp-info-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
  margin-top: 20px;
}

.pp-info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.pp-info-label {
  font-size: 12px;
  color: #6B7280;
}

.pp-info-value {
  font-size: 14px;
  color: #1F2937;
  font-weight: 500;

  &.link { color: #2563EB; cursor: pointer; }
  &.success { color: #10B981; }
}

/* 里程碑卡片 */
.pp-milestone-section {
  margin-top: 20px;
  padding: 16px;
  background: #F9FAFB;
  border-radius: 8px;
}

.pp-milestone-header {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 12px;
}

.pp-milestone-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.pp-milestone-card {
  text-align: center;
  padding: 12px;
  background: white;
  border-radius: 6px;
  border: 1px solid #E5E7EB;

  &.danger {
    border-color: #EF4444;
    background: #fef2f2;
  }

  &.warning {
    border-color: #F59E0B;
  }
}

.pp-milestone-card-label {
  font-size: 12px;
  color: #6B7280;
}

.pp-milestone-card-date {
  font-size: 16px;
  font-weight: 600;
  color: #374151;
  margin-top: 4px;

  &.danger { color: #EF4444; }
  &.warning { color: #F59E0B; }
  &.completed { color: #10B981; }
}

.pp-milestone-card-status {
  font-size: 11px;
  color: #9CA3AF;
  margin-top: 2px;
}

/* 关联数据卡片 */
.pp-related-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-top: 20px;
}

.pp-related-card {
  background: #F9FAFB;
  border: 1px solid #E5E7EB;
  border-radius: 8px;
  padding: 16px;
  text-align: center;
  cursor: pointer;

  &:hover { border-color: #2563EB; }

  &.danger .pp-related-num { color: #EF4444; }
  &.warning .pp-related-num { color: #F59E0B; }
  &.primary .pp-related-num { color: #2563EB; }
}

.pp-related-num {
  font-size: 32px;
  font-weight: 700;
}

.pp-related-label {
  font-size: 13px;
  color: #6B7280;
  margin-top: 4px;
}

/* Tab 导航 */
.pp-tab-nav {
  display: flex;
  border-bottom: 1px solid #E5E7EB;
  background: white;
}

.pp-tab-item {
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
  }
}

.pp-tab-badge {
  display: inline-block;
  margin-left: 6px;
  padding: 1px 6px;
  background: #F3F4F6;
  border-radius: 10px;
  font-size: 11px;
  font-weight: 500;
}

.pp-tab-item.active .pp-tab-badge {
  background: #93C5FD;
  color: #2563EB;
}

/* Tab 内容 */
.pp-tab-content {
  padding: 24px;
}

/* 时间线 */
.pp-timeline {
  position: relative;
  padding-left: 32px;

  &::before {
    content: '';
    position: absolute;
    left: 8px;
    top: 0;
    bottom: 0;
    width: 2px;
    background: #E5E7EB;
  }
}

.pp-timeline-item {
  position: relative;
  padding-bottom: 24px;

  &:last-child { padding-bottom: 0; }
}

.pp-timeline-dot {
  position: absolute;
  left: -28px;
  top: 4px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #2563EB;
  border: 2px solid white;
  box-shadow: 0 0 0 2px #E5E7EB;
}

.pp-timeline-date {
  font-size: 12px;
  color: #6B7280;
  margin-bottom: 4px;
}

.pp-timeline-content {
  font-size: 14px;
  color: #374151;
  line-height: 1.6;
}

.pp-timeline-actions {
  margin-top: 8px;
  display: flex;
  gap: 8px;

  button {
    font-size: 12px;
    color: #6B7280;
    background: none;
    border: none;
    cursor: pointer;

    &:hover { color: #2563EB; }
    &.danger { color: #EF4444; }
  }
}

/* 新增事项 */
.pp-add-milestone {
  display: flex;
  gap: 12px;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #E5E7EB;
}

/* 详情表格 */
.pp-detail-table {
  width: 100%;
  border-collapse: collapse;

  td {
    padding: 10px 16px;
    font-size: 14px;
    border-bottom: 1px solid #F3F4F6;

    &.label {
      color: #6B7280;
      font-weight: 500;
      width: 100px;
    }
  }
}

/* 空状态 */
.pp-empty-tip {
  text-align: center;
  padding: 30px;
  color: #909399;
  font-size: 14px;
}

/* 对话框 */
.pp-dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>