<template>
  <div class="pp-page-container">
    <!-- 顶部导航 -->
    <div class="pp-top-nav">
      <div class="pp-breadcrumb">
        <a @click="goTo('/dashboard')">首页</a>
        <span>/</span>
        <span class="current">问题跟踪</span>
      </div>
      <div class="pp-page-actions">
        <button class="pp-btn pp-btn-secondary" @click="handleExport">导出Excel</button>
        <button class="pp-btn pp-btn-primary" @click="handleAdd">+ 新增问题</button>
      </div>
    </div>

    <!-- 筛选区域 -->
    <div class="pp-filter-bar">
      <div class="pp-filter-group">
        <span class="pp-filter-label">所属项目</span>
        <el-select v-model="queryParams.projectId" placeholder="全部项目" clearable filterable size="small" class="pp-filter-select" @change="handleQuery">
          <el-option v-for="item in projectOptions" :key="item.id" :label="item.customer + '-' + item.name" :value="item.id" />
        </el-select>
      </div>
      <div class="pp-filter-group">
        <span class="pp-filter-label">问题状态</span>
        <el-select v-model="queryParams.status" placeholder="全部状态" clearable size="small" class="pp-filter-select" @change="handleQuery">
          <el-option label="待处理" value="待处理" />
          <el-option label="进行中" value="进行中" />
          <el-option label="已解决" value="已解决" />
          <el-option label="已关闭" value="已关闭" />
        </el-select>
      </div>
      <div class="pp-filter-group">
        <span class="pp-filter-label">严重程度</span>
        <el-select v-model="queryParams.severity" placeholder="全部" clearable size="small" class="pp-filter-select" @change="handleQuery">
          <el-option label="高" value="高" />
          <el-option label="中" value="中" />
          <el-option label="低" value="低" />
        </el-select>
      </div>
      <div class="pp-filter-group">
        <span class="pp-filter-label">负责人</span>
        <el-select v-model="queryParams.ownerId" placeholder="全部人员" clearable filterable size="small" class="pp-filter-select" @change="handleQuery">
          <el-option v-for="item in personOptions" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </div>
      <div class="pp-filter-group">
        <el-input v-model="queryParams.keyword" placeholder="搜索问题描述..." clearable size="small" class="pp-filter-input" @keyup.enter.native="handleQuery" />
      </div>
      <div class="pp-filter-group">
        <button class="pp-btn pp-btn-secondary" @click="handleQuery">搜索</button>
        <button class="pp-btn pp-btn-secondary" @click="resetQuery">重置</button>
      </div>
    </div>

    <!-- 统计栏 -->
    <div class="pp-stats-bar">
      <div class="pp-stat-item">
        共 <span class="pp-stat-num" style="color: #111827;">{{ total }}</span> 个问题
      </div>
      <div class="pp-stat-item">
        待处理 <span class="pp-stat-num" style="color: #EF4444;">{{ statusCounts.pending }}</span>
      </div>
      <div class="pp-stat-item">
        进行中 <span class="pp-stat-num" style="color: #F59E0B;">{{ statusCounts.progress }}</span>
      </div>
      <div class="pp-stat-item">
        已解决 <span class="pp-stat-num" style="color: #10B981;">{{ statusCounts.resolved }}</span>
      </div>
      <div class="pp-stat-item">
        已关闭 <span class="pp-stat-num" style="color: #6B7280;">{{ statusCounts.closed }}</span>
      </div>
      <div class="pp-stat-item pp-stat-divider">
        高优先级 <span class="pp-stat-num" style="color: #EF4444;">{{ highPriorityCount }}</span>
      </div>
      <div class="pp-stat-item pp-stat-divider">
        超期 <span class="pp-stat-num" style="color: #EF4444;">{{ overdueCount }}</span>
      </div>
    </div>

    <!-- 数据表格 -->
    <table class="pp-data-table" v-loading="loading">
      <thead>
        <tr>
          <th style="min-width: 100px;">所属项目</th>
          <th style="min-width: 200px;">问题描述</th>
          <th>类型</th>
          <th>严重程度</th>
          <th>状态</th>
          <th>负责人</th>
          <th style="min-width: 100px;">计划日期</th>
          <th style="min-width: 80px;">操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(row, index) in issueList" :key="index">
          <td>
            <a class="pp-project-link" @click="goToProject(row)">{{ row.customer }}-{{ row.projectName || '-' }}</a>
          </td>
          <td>
            <div class="pp-issue-desc" @click="handleDetail(row)">{{ row.description }}</div>
          </td>
          <td>{{ row.type || '-' }}</td>
          <td>
            <span class="pp-severity-tag" :class="severityClass(row.severity)">{{ row.severity }}</span>
          </td>
          <td>
            <span class="pp-status-tag" :class="issueStatusClass(row.status)">{{ row.status }}</span>
          </td>
          <td>{{ row.ownerName || '-' }}</td>
          <td :class="{ overdue: isOverdue(row) }">
            {{ row.planDate || '-' }}
            <span v-if="isOverdue(row)" class="pp-overdue-tip"> (超期{{ overdueDays(row) }}天)</span>
          </td>
          <td>
            <div class="pp-action-btns">
              <button class="pp-action-btn" v-if="row.status === '待处理'" @click="handleUpdate(row)">处理</button>
              <button class="pp-action-btn" v-else @click="handleDetail(row)">详情</button>
            </div>
          </td>
        </tr>
        <tr v-if="issueList.length === 0">
          <td colspan="8" class="pp-empty-tip">暂无问题数据</td>
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

    <!-- 添加/编辑问题对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body custom-class="pp-dialog">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="项目" prop="projectId">
          <el-select v-model="form.projectId" placeholder="请选择项目" filterable @change="handleProjectChange">
            <el-option v-for="item in projectOptions" :key="item.id" :label="item.customer + '-' + item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="问题描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入问题描述" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="问题类型" prop="type">
              <el-select v-model="form.type" placeholder="请选择类型">
                <el-option label="技术问题" value="技术问题" />
                <el-option label="业务问题" value="业务问题" />
                <el-option label="管理问题" value="管理问题" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="严重程度" prop="severity">
              <el-select v-model="form.severity" placeholder="请选择严重程度">
                <el-option label="高" value="高" />
                <el-option label="中" value="中" />
                <el-option label="低" value="低" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态">
                <el-option label="待处理" value="待处理" />
                <el-option label="进行中" value="进行中" />
                <el-option label="已解决" value="已解决" />
                <el-option label="已关闭" value="已关闭" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人" prop="ownerId">
              <el-select v-model="form.ownerId" placeholder="请选择负责人" filterable @change="handleOwnerChange">
                <el-option v-for="item in personOptions" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="发现日期" prop="discoverDate">
              <el-date-picker v-model="form.discoverDate" type="date" value-format="yyyy-MM-dd" placeholder="选择发现日期" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计划解决" prop="planDate">
              <el-date-picker v-model="form.planDate" type="date" value-format="yyyy-MM-dd" placeholder="选择计划解决日期" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="解决方案" prop="solution">
          <el-input v-model="form.solution" type="textarea" :rows="2" placeholder="请输入解决方案" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="pp-dialog-footer">
        <button class="pp-btn pp-btn-secondary" @click="cancel">取消</button>
        <button class="pp-btn pp-btn-primary" @click="submitForm">确定</button>
      </div>
    </el-dialog>

    <!-- 问题详情对话框 -->
    <el-dialog title="问题详情" :visible.sync="detailOpen" width="800px" append-to-body custom-class="pp-dialog">
      <div class="pp-detail-header">
        <div class="pp-detail-top">
          <div>
            <div class="pp-detail-title">{{ detailData.description }}</div>
            <div class="pp-detail-project">所属项目：<a class="pp-project-link" @click="goToProject(detailData)">{{ detailData.projectName }}</a></div>
          </div>
          <span class="pp-status-tag" :class="issueStatusClass(detailData.status)">{{ detailData.status }}</span>
        </div>
        <!-- 状态流转 -->
        <div class="pp-status-flow">
          <div class="pp-flow-step" :class="flowStepClass('待处理', detailData.status)">
            <span>待处理</span>
          </div>
          <span class="pp-flow-arrow">→</span>
          <div class="pp-flow-step" :class="flowStepClass('进行中', detailData.status)">
            <span>进行中</span>
          </div>
          <span class="pp-flow-arrow">→</span>
          <div class="pp-flow-step" :class="flowStepClass('已解决', detailData.status)">
            <span>已解决</span>
          </div>
          <span class="pp-flow-arrow">→</span>
          <div class="pp-flow-step" :class="flowStepClass('已关闭', detailData.status)">
            <span>已关闭</span>
          </div>
        </div>
        <!-- 信息网格 -->
        <div class="pp-info-grid">
          <div class="pp-info-item">
            <div class="pp-info-label">问题类型</div>
            <div class="pp-info-value">{{ detailData.type || '-' }}</div>
          </div>
          <div class="pp-info-item">
            <div class="pp-info-label">严重程度</div>
            <div class="pp-info-value"><span class="pp-severity-tag" :class="severityClass(detailData.severity)">{{ detailData.severity }}</span></div>
          </div>
          <div class="pp-info-item">
            <div class="pp-info-label">负责人</div>
            <div class="pp-info-value">{{ detailData.ownerName || '-' }}</div>
          </div>
          <div class="pp-info-item">
            <div class="pp-info-label">发现日期</div>
            <div class="pp-info-value">{{ detailData.discoverDate || '-' }}</div>
          </div>
          <div class="pp-info-item">
            <div class="pp-info-label">计划解决日期</div>
            <div class="pp-info-value" :class="{ overdue: isOverdue(detailData) }">{{ detailData.planDate || '-' }}<span v-if="isOverdue(detailData)"> (已超期{{ overdueDays(detailData) }}天)</span></div>
          </div>
          <div class="pp-info-item">
            <div class="pp-info-label">实际解决日期</div>
            <div class="pp-info-value" :style="{ color: detailData.actualDate ? '#10B981' : '#9CA3AF' }">{{ detailData.actualDate || '-' }}</div>
          </div>
          <div class="pp-info-item">
            <div class="pp-info-label">创建人</div>
            <div class="pp-info-value">{{ detailData.creatorName || '-' }}</div>
          </div>
          <div class="pp-info-item">
            <div class="pp-info-label">创建时间</div>
            <div class="pp-info-value">{{ detailData.createTime || '-' }}</div>
          </div>
        </div>
      </div>
      <!-- Tab 导航 -->
      <div class="pp-tab-nav">
        <div class="pp-tab-item" :class="{ active: detailTab === 'basic' }" @click="detailTab = 'basic'">基本信息</div>
        <div class="pp-tab-item" :class="{ active: detailTab === 'history' }" @click="detailTab = 'history'">处理记录</div>
      </div>
      <!-- Tab 内容 -->
      <div class="pp-tab-content">
        <!-- 基本信息 -->
        <div v-show="detailTab === 'basic'">
          <div class="pp-detail-section">
            <div class="pp-section-title">问题描述</div>
            <div class="pp-section-content">{{ detailData.descriptionDetail || detailData.description }}</div>
          </div>
          <div class="pp-detail-section" v-if="detailData.solution">
            <div class="pp-section-title">解决方案</div>
            <div class="pp-section-content">{{ detailData.solution }}</div>
          </div>
        </div>
        <!-- 处理记录 -->
        <div v-show="detailTab === 'history'">
          <div class="pp-detail-section">
            <div class="pp-section-title">处理记录</div>
            <div class="pp-history-list" v-if="detailData.history && detailData.history.length > 0">
              <div class="pp-history-item" v-for="(item, idx) in detailData.history" :key="idx">
                <div class="pp-history-time">{{ item.time }}</div>
                <div class="pp-history-content">
                  <span class="pp-history-user">{{ item.user }}</span> {{ item.action }}
                </div>
              </div>
            </div>
            <div class="pp-empty-section" v-else>暂无处理记录</div>
          </div>
          <div class="pp-detail-section">
            <div class="pp-section-title">添加处理记录</div>
            <div class="pp-add-history">
              <textarea class="pp-history-input" v-model="newHistoryContent" placeholder="输入处理说明..."></textarea>
              <div class="pp-history-actions">
                <el-select v-model="newHistoryStatus" placeholder="变更状态" size="small">
                  <el-option label="进行中" value="进行中" />
                  <el-option label="已解决" value="已解决" />
                  <el-option label="已关闭" value="已关闭" />
                </el-select>
                <button class="pp-btn pp-btn-primary" @click="addHistoryRecord">提交记录</button>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div slot="footer" class="pp-dialog-footer">
        <button class="pp-btn pp-btn-secondary" @click="detailOpen = false">关闭</button>
        <button class="pp-btn pp-btn-primary" @click="handleUpdate(detailData)">编辑问题</button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listIssue, getIssue, delIssue, addIssue, updateIssue } from "@/api/system/issue";
import { listProjectAll } from "@/api/system/project";
import { listPersonAll } from "@/api/system/person";

export default {
  name: "Issue",
  data() {
    return {
      loading: false,
      total: 0,
      issueList: [],
      projectOptions: [],
      personOptions: [],
      queryParams: { pageNum: 1, pageSize: 10, projectId: undefined, severity: undefined, status: undefined, ownerId: undefined, keyword: undefined },
      title: "",
      open: false,
      detailOpen: false,
      detailTab: 'basic',
      detailData: {},
      newHistoryContent: '',
      newHistoryStatus: '',
      form: {},
      rules: {
        projectId: [{ required: true, message: "项目不能为空", trigger: "change" }],
        description: [{ required: true, message: "问题描述不能为空", trigger: "blur" }]
      },
      statusCounts: { pending: 0, progress: 0, resolved: 0, closed: 0 },
      highPriorityCount: 0,
      overdueCount: 0
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
    this.getProjectList();
    this.getPersonList();
  },
  methods: {
    getList() {
      this.loading = true;
      listIssue(this.queryParams).then(response => {
        this.issueList = response.rows || [];
        this.total = response.total || 0;

        // 计算统计
        this.statusCounts = {
          pending: this.issueList.filter(i => i.status === '待处理').length,
          progress: this.issueList.filter(i => i.status === '进行中').length,
          resolved: this.issueList.filter(i => i.status === '已解决').length,
          closed: this.issueList.filter(i => i.status === '已关闭').length
        };
        this.highPriorityCount = this.issueList.filter(i => i.severity === '高' && i.status !== '已关闭').length;
        this.overdueCount = this.issueList.filter(i => this.isOverdue(i)).length;

        this.loading = false;
      });
    },
    getProjectList() {
      listProjectAll().then(response => { this.projectOptions = response.data || []; });
    },
    getPersonList() {
      listPersonAll().then(response => { this.personOptions = response.data || []; });
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 10, projectId: undefined, severity: undefined, status: undefined, ownerId: undefined, keyword: undefined };
      this.getList();
    },
    changePage(delta) {
      this.queryParams.pageNum += delta;
      this.getList();
    },
    goToPage(page) {
      this.queryParams.pageNum = page;
      this.getList();
    },
    handleSelectAll() {
      if (this.selectAll) {
        this.selectedIds = this.issueList.map(i => i.id);
      } else {
        this.selectedIds = [];
      }
    },
    handleSelect(id) {
      const idx = this.selectedIds.indexOf(id);
      if (idx >= 0) {
        this.selectedIds.splice(idx, 1);
      } else {
        this.selectedIds.push(id);
      }
      this.selectAll = this.selectedIds.length === this.issueList.length;
    },
    reset() {
      this.form = {
        id: undefined,
        projectId: undefined,
        projectName: undefined,
        description: undefined,
        type: undefined,
        severity: "中",
        status: "待处理",
        ownerId: undefined,
        ownerName: undefined,
        discoverDate: undefined,
        planDate: undefined,
        solution: undefined
      };
      this.resetForm("form");
    },
    cancel() {
      this.open = false;
      this.reset();
    },
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加问题";
    },
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.selectedIds[0];
      getIssue(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改问题";
      });
    },
    handleDetail(row) {
      getIssue(row.id).then(response => {
        this.detailData = response.data;
        this.detailTab = 'basic';
        this.newHistoryContent = '';
        this.newHistoryStatus = '';
        this.detailOpen = true;
      });
    },
    handleProjectChange(val) {
      const project = this.projectOptions.find(p => p.id === val);
      this.form.projectName = project ? project.name : '';
    },
    handleOwnerChange(val) {
      const person = this.personOptions.find(p => p.id === val);
      this.form.ownerName = person ? person.name : '';
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != undefined) {
            updateIssue(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addIssue(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    handleDelete(row) {
      this.$modal.confirm('是否确认删除该问题？').then(() => {
        return delIssue(row.id);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    handleExport() {
      this.download('system/issue/export', { ...this.queryParams }, `issue_${new Date().getTime()}.xlsx`);
    },
    goTo(path) {
      this.$router.push(path);
    },
    goToProject(row) {
      this.$router.push({ path: '/system/project', query: { id: row.projectId } });
    },
    // 状态流转样式
    flowStepClass(stepStatus, currentStatus) {
      const statusOrder = ['待处理', '进行中', '已解决', '已关闭'];
      const stepIndex = statusOrder.indexOf(stepStatus);
      const currentIndex = statusOrder.indexOf(currentStatus);
      if (stepIndex < currentIndex) return 'done';
      if (stepIndex === currentIndex) return 'active';
      return '';
    },
    // 超期天数计算
    overdueDays(row) {
      if (!row.planDate) return 0;
      const today = new Date();
      const planDate = new Date(row.planDate);
      const diff = Math.floor((today - planDate) / (1000 * 60 * 60 * 24));
      return diff > 0 ? diff : 0;
    },
    // 添加处理记录
    addHistoryRecord() {
      if (!this.newHistoryContent && !this.newHistoryStatus) {
        this.$modal.msgWarning("请输入处理说明或选择状态变更");
        return;
      }
      // 实际实现需要调用API保存记录
      this.$modal.msgSuccess("记录添加成功");
      // 模拟添加历史记录
      const now = new Date();
      const timeStr = `${now.getFullYear()}-${String(now.getMonth()+1).padStart(2,'0')}-${String(now.getDate()).padStart(2,'0')} ${String(now.getHours()).padStart(2,'0')}:${String(now.getMinutes()).padStart(2,'0')}`;
      if (!this.detailData.history) this.detailData.history = [];
      this.detailData.history.push({
        time: timeStr,
        user: '当前用户',
        action: this.newHistoryContent + (this.newHistoryStatus ? `，状态变更为 ${this.newHistoryStatus}` : '')
      });
      if (this.newHistoryStatus) {
        this.detailData.status = this.newHistoryStatus;
      }
      this.newHistoryContent = '';
      this.newHistoryStatus = '';
    },
    // 样式辅助方法
    severityClass(severity) {
      const map = { '高': 'high', '中': 'medium', '低': 'low' };
      return map[severity] || '';
    },
    issueStatusClass(status) {
      const map = { '待处理': 'pending', '进行中': 'progress', '已解决': 'resolved', '已关闭': 'closed' };
      return map[status] || '';
    },
    isOverdue(row) {
      if (!row.planDate || row.status === '已解决' || row.status === '已关闭') return false;
      const today = new Date();
      const planDate = new Date(row.planDate);
      return planDate < today;
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

  a { color: #6B7280; cursor: pointer; &:hover { color: #2563EB; } }
  .current { color: #1F2937; font-weight: 500; }
}

.pp-page-actions { display: flex; gap: 8px; }

/* 筛选区域 */
.pp-filter-bar {
  display: flex;
  gap: 16px;
  padding: 16px 24px;
  background: white;
  border-bottom: 1px solid #E5E7EB;
  flex-wrap: wrap;
}

.pp-filter-group { display: flex; align-items: center; gap: 8px; }
.pp-filter-label { font-size: 13px; color: #6B7280; }
.pp-filter-select { min-width: 140px; }

/* 统计栏 */
.pp-stats-bar {
  display: flex;
  gap: 32px;
  padding: 12px 24px;
  background: #F9FAFB;
  border-bottom: 1px solid #E5E7EB;
  font-size: 13px;
}

.pp-stat-item { display: flex; align-items: center; gap: 8px; }
.pp-stat-num { font-weight: 600; font-size: 16px; }
.pp-stat-divider { margin-left: 16px; padding-left: 16px; border-left: 1px solid #E5E7EB; }

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
}

.pp-project-link {
  color: #2563EB;
  cursor: pointer;
  font-weight: 500;
  &:hover { text-decoration: underline; }
}

.pp-issue-desc {
  max-width: 280px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;
  &:hover { color: #2563EB; }
}

/* 严重程度标签 */
.pp-severity-tag {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 500;

  &.high { background: #fef2f2; color: #EF4444; border: 1px solid #fecaca; }
  &.medium { background: #fffbeb; color: #F59E0B; border: 1px solid #fde68a; }
  &.low { background: #eff6ff; color: #2563EB; border: 1px solid #bfdbfe; }
}

/* 问题状态标签 */
.pp-status-tag.pending { background: #fef2f2; color: #EF4444; }
.pp-status-tag.progress { background: #fffbeb; color: #F59E0B; }
.pp-status-tag.resolved { background: #f0fdf4; color: #10B981; }
.pp-status-tag.closed { background: #F3F4F6; color: #6B7280; }

/* 超期标记 */
.overdue { color: #EF4444; font-weight: 500; }

/* 操作按钮 */
.pp-action-btns { display: flex; gap: 8px; }
.pp-action-btn {
  padding: 4px 8px;
  font-size: 12px;
  border: 1px solid #E5E7EB;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  color: #4B5563;
  &:hover { border-color: #2563EB; color: #2563EB; }
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

.pp-pagination-btns { display: flex; gap: 4px; }
.pp-page-btn {
  padding: 6px 12px;
  border: 1px solid #E5E7EB;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  &.active { background: #2563EB; color: white; border-color: #2563EB; }
  &:hover:not(.active):not(:disabled) { border-color: #2563EB; color: #2563EB; }
  &:disabled { opacity: 0.5; cursor: not-allowed; }
}

/* 详情页 */
.pp-detail-header { padding: 24px; border-bottom: 1px solid #E5E7EB; }
.pp-detail-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}
.pp-detail-title { font-size: 20px; font-weight: 600; color: #111827; margin-bottom: 4px; }
.pp-detail-project { font-size: 14px; color: #6B7280; }

/* 状态流转 */
.pp-status-flow {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px;
  background: #F9FAFB;
  border-radius: 8px;
  margin-top: 20px;
}
.pp-flow-step {
  display: flex;
  align-items: center;
  padding: 8px 16px;
  background: white;
  border-radius: 6px;
  border: 1px solid #E5E7EB;
  font-size: 13px;
  &.active {
    border-color: #2563EB;
    background: #eff6ff;
    color: #2563EB;
    font-weight: 500;
  }
  &.done {
    border-color: #10B981;
    background: #f0fdf4;
    color: #10B981;
  }
}
.pp-flow-arrow { color: #D1D5DB; font-size: 16px; }

/* 信息网格 */
.pp-info-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-top: 20px; }
.pp-info-item { display: flex; flex-direction: column; gap: 4px; }
.pp-info-label { font-size: 12px; color: #6B7280; }
.pp-info-value { font-size: 14px; color: #1F2937; font-weight: 500; }

/* Tab 导航 */
.pp-tab-nav { display: flex; border-bottom: 1px solid #E5E7EB; background: white; }
.pp-tab-item {
  padding: 14px 24px;
  font-size: 14px;
  font-weight: 500;
  color: #6B7280;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  &:hover { color: #374151; }
  &.active { color: #2563EB; border-bottom-color: #2563EB; }
}
.pp-tab-content { padding: 24px; }

/* 详情区块 */
.pp-detail-section { margin-bottom: 24px; }
.pp-section-title { font-size: 14px; font-weight: 600; color: #374151; margin-bottom: 12px; }
.pp-section-content {
  padding: 16px;
  background: #F9FAFB;
  border-radius: 8px;
  font-size: 14px;
  color: #374151;
  line-height: 1.6;
}

/* 历史记录 */
.pp-history-list { border: 1px solid #E5E7EB; border-radius: 8px; overflow: hidden; }
.pp-history-item {
  display: flex;
  gap: 16px;
  padding: 12px 16px;
  border-bottom: 1px solid #F3F4F6;
  font-size: 13px;
  &:last-child { border-bottom: none; }
}
.pp-history-time { color: #9CA3AF; min-width: 140px; }
.pp-history-content { flex: 1; }
.pp-history-user { color: #2563EB; font-weight: 500; }

/* 添加历史记录 */
.pp-add-history { display: flex; gap: 12px; }
.pp-history-input {
  flex: 1;
  padding: 12px;
  border: 1px solid #E5E7EB;
  border-radius: 6px;
  font-size: 14px;
  min-height: 80px;
  font-family: inherit;
  resize: vertical;
}
.pp-history-actions { display: flex; flex-direction: column; gap: 8px; }

/* 空状态 */
.pp-empty-section { text-align: center; padding: 20px; color: #9CA3AF; font-size: 14px; }
.pp-empty-tip { text-align: center; padding: 30px; color: #909399; font-size: 14px; }

/* 对话框 */
.pp-dialog-footer { display: flex; justify-content: flex-end; gap: 8px; }
</style>