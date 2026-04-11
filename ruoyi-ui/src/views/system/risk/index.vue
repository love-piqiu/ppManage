<template>
  <div class="pp-page-container">
    <!-- 顶部导航 -->
    <div class="pp-top-nav">
      <div class="pp-breadcrumb">
        <a @click="goTo('/dashboard')">首页</a>
        <span>/</span>
        <span class="current">风险管理</span>
      </div>
      <div class="pp-page-actions">
        <button class="pp-btn pp-btn-secondary" @click="handleExport">导出Excel</button>
        <button class="pp-btn pp-btn-primary" @click="handleAdd">+ 新增风险</button>
      </div>
    </div>

    <!-- 筛选区域 -->
    <div class="pp-filter-bar">
      <div class="pp-filter-group">
        <span class="pp-filter-label">所属项目</span>
        <el-select v-model="queryParams.projectId" placeholder="全部项目" clearable filterable size="small" class="pp-filter-select" @change="handleQuery">
          <el-option v-for="item in projectOptions" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </div>
      <div class="pp-filter-group">
        <span class="pp-filter-label">风险等级</span>
        <el-select v-model="queryParams.level" placeholder="全部等级" clearable size="small" class="pp-filter-select" @change="handleQuery">
          <el-option label="高" value="高" />
          <el-option label="中" value="中" />
          <el-option label="低" value="低" />
        </el-select>
      </div>
      <div class="pp-filter-group">
        <span class="pp-filter-label">风险状态</span>
        <el-select v-model="queryParams.status" placeholder="全部状态" clearable size="small" class="pp-filter-select" @change="handleQuery">
          <el-option label="潜在" value="潜在" />
          <el-option label="已发生" value="已发生" />
          <el-option label="已消除" value="已消除" />
        </el-select>
      </div>
      <div class="pp-filter-group">
        <el-input v-model="queryParams.keyword" placeholder="搜索风险描述..." clearable size="small" class="pp-filter-input" @keyup.enter.native="handleQuery" />
      </div>
      <div class="pp-filter-group">
        <button class="pp-btn pp-btn-secondary" @click="handleQuery">搜索</button>
        <button class="pp-btn pp-btn-secondary" @click="resetQuery">重置</button>
      </div>
    </div>

    <!-- 风险概览卡片 -->
    <div class="pp-risk-overview">
      <div class="pp-risk-card danger">
        <div class="pp-risk-num">{{ levelCounts.high }}</div>
        <div class="pp-risk-label">高风险</div>
      </div>
      <div class="pp-risk-card warning">
        <div class="pp-risk-num">{{ levelCounts.medium }}</div>
        <div class="pp-risk-label">中风险</div>
      </div>
      <div class="pp-risk-card success">
        <div class="pp-risk-num">{{ statusCounts.eliminated }}</div>
        <div class="pp-risk-label">已消除</div>
      </div>
      <div class="pp-risk-card info">
        <div class="pp-risk-num">{{ total }}</div>
        <div class="pp-risk-label">风险总数</div>
      </div>
    </div>

    <!-- 数据表格 -->
    <table class="pp-data-table" v-loading="loading">
      <thead>
        <tr>
          <th style="min-width: 100px;">所属项目</th>
          <th style="min-width: 200px;">风险描述</th>
          <th>风险等级</th>
          <th>状态</th>
          <th style="min-width: 150px;">应对措施</th>
          <th>负责人</th>
          <th>更新时间</th>
          <th style="min-width: 80px;">操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(row, index) in riskList" :key="index" :class="{ resolved: row.status === '已消除' }">
          <td>
            <a class="pp-project-link" @click="goToProject(row)">{{ row.projectName || '-' }}</a>
          </td>
          <td>
            <div class="pp-risk-desc" @click="handleDetail(row)">{{ row.description }}</div>
          </td>
          <td>
            <span class="pp-level-tag" :class="levelClass(row.level)">{{ levelEmoji(row.level) }} {{ row.level }}</span>
          </td>
          <td>
            <span class="pp-status-tag" :class="riskStatusClass(row.status)">{{ row.status }}</span>
          </td>
          <td>
            <div class="pp-measure-cell" :title="row.measure">{{ row.measure || '-' }}</div>
          </td>
          <td>{{ row.ownerName || '-' }}</td>
          <td>{{ row.updateTime || '-' }}</td>
          <td>
            <div class="pp-action-btns">
              <button class="pp-action-btn" @click="handleDetail(row)">详情</button>
            </div>
          </td>
        </tr>
        <tr v-if="riskList.length === 0">
          <td colspan="8" class="pp-empty-tip">暂无风险数据</td>
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

    <!-- 添加/编辑风险对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body custom-class="pp-dialog">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="项目" prop="projectId">
          <el-select v-model="form.projectId" placeholder="请选择项目" filterable @change="handleProjectChange">
            <el-option v-for="item in projectOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="风险描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入风险描述" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="风险等级" prop="level">
              <el-select v-model="form.level" placeholder="请选择风险等级">
                <el-option label="高" value="高" />
                <el-option label="中" value="中" />
                <el-option label="低" value="低" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态">
                <el-option label="潜在" value="潜在" />
                <el-option label="已发生" value="已发生" />
                <el-option label="已消除" value="已消除" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="负责人" prop="ownerId">
          <el-select v-model="form.ownerId" placeholder="请选择负责人" filterable @change="handleOwnerChange">
            <el-option v-for="item in personOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="应对措施" prop="measure">
          <el-input v-model="form.measure" type="textarea" :rows="3" placeholder="请输入应对措施" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="pp-dialog-footer">
        <button class="pp-btn pp-btn-secondary" @click="cancel">取消</button>
        <button class="pp-btn pp-btn-primary" @click="submitForm">确定</button>
      </div>
    </el-dialog>

    <!-- 风险详情对话框 -->
    <el-dialog title="风险详情" :visible.sync="detailOpen" width="800px" append-to-body custom-class="pp-dialog">
      <div class="pp-detail-header">
        <div class="pp-detail-top">
          <div>
            <div class="pp-detail-title">{{ detailData.description }}</div>
            <div class="pp-detail-project">所属项目：<a class="pp-project-link" @click="goToProject(detailData)">{{ detailData.projectName }}</a></div>
          </div>
          <div class="pp-detail-tags">
            <span class="pp-level-tag" :class="levelClass(detailData.level)">{{ levelEmoji(detailData.level) }} {{ detailData.level }}风险</span>
            <span class="pp-status-tag" :class="riskStatusClass(detailData.status)">{{ detailData.status }}</span>
          </div>
        </div>
        <!-- 信息网格 -->
        <div class="pp-info-grid">
          <div class="pp-info-item">
            <div class="pp-info-label">风险等级</div>
            <div class="pp-info-value"><span class="pp-level-tag" :class="levelClass(detailData.level)">{{ levelEmoji(detailData.level) }} {{ detailData.level }}</span></div>
          </div>
          <div class="pp-info-item">
            <div class="pp-info-label">当前状态</div>
            <div class="pp-info-value"><span class="pp-status-tag" :class="riskStatusClass(detailData.status)">{{ detailData.status }}</span></div>
          </div>
          <div class="pp-info-item">
            <div class="pp-info-label">负责人</div>
            <div class="pp-info-value">{{ detailData.ownerName || '-' }}</div>
          </div>
          <div class="pp-info-item">
            <div class="pp-info-label">创建时间</div>
            <div class="pp-info-value">{{ detailData.createTime || '-' }}</div>
          </div>
          <div class="pp-info-item">
            <div class="pp-info-label">更新时间</div>
            <div class="pp-info-value">{{ detailData.updateTime || '-' }}</div>
          </div>
          <div class="pp-info-item">
            <div class="pp-info-label">创建人</div>
            <div class="pp-info-value">{{ detailData.creatorName || '-' }}</div>
          </div>
        </div>
      </div>
      <!-- Tab 导航 -->
      <div class="pp-tab-nav">
        <div class="pp-tab-item" :class="{ active: detailTab === 'basic' }" @click="detailTab = 'basic'">基本信息</div>
        <div class="pp-tab-item" :class="{ active: detailTab === 'history' }" @click="detailTab = 'history'">状态变更记录</div>
      </div>
      <!-- Tab 内容 -->
      <div class="pp-tab-content">
        <!-- 基本信息 -->
        <div v-show="detailTab === 'basic'">
          <div class="pp-detail-section">
            <div class="pp-section-title">风险描述</div>
            <div class="pp-section-content">{{ detailData.descriptionDetail || detailData.description }}</div>
          </div>
          <div class="pp-detail-section">
            <div class="pp-section-title">应对措施</div>
            <div class="pp-section-content">{{ detailData.measure || '暂无应对措施' }}</div>
          </div>
        </div>
        <!-- 状态变更记录 -->
        <div v-show="detailTab === 'history'">
          <div class="pp-detail-section">
            <div class="pp-section-title">状态变更记录</div>
            <div class="pp-history-list" v-if="detailData.history && detailData.history.length > 0">
              <div class="pp-history-item" v-for="(item, idx) in detailData.history" :key="idx">
                <div class="pp-history-time">{{ item.time }}</div>
                <div class="pp-history-content">
                  <span class="pp-history-user">{{ item.user }}</span> {{ item.action }}
                  <div class="pp-history-note" v-if="item.note">{{ item.note }}</div>
                </div>
              </div>
            </div>
            <div class="pp-empty-section" v-else>暂无状态变更记录</div>
          </div>
          <div class="pp-detail-section">
            <div class="pp-section-title">更新应对措施</div>
            <div class="pp-add-history">
              <textarea class="pp-history-input" v-model="newHistoryContent" placeholder="输入应对措施更新或状态变更说明..."></textarea>
              <div class="pp-history-actions">
                <el-select v-model="newHistoryStatus" placeholder="变更状态" size="small">
                  <el-option label="潜在" value="潜在" />
                  <el-option label="已发生" value="已发生" />
                  <el-option label="已消除" value="已消除" />
                </el-select>
                <button class="pp-btn pp-btn-primary" @click="addHistoryRecord">提交更新</button>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div slot="footer" class="pp-dialog-footer">
        <button class="pp-btn pp-btn-secondary" @click="detailOpen = false">关闭</button>
        <button class="pp-btn pp-btn-primary" @click="handleUpdate(detailData)">编辑风险</button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listRisk, getRisk, delRisk, addRisk, updateRisk } from "@/api/system/risk";
import { listProjectAll } from "@/api/system/project";
import { listPersonAll } from "@/api/system/person";

export default {
  name: "Risk",
  data() {
    return {
      loading: false,
      total: 0,
      riskList: [],
      projectOptions: [],
      personOptions: [],
      queryParams: { pageNum: 1, pageSize: 10, projectId: undefined, level: undefined, status: undefined, keyword: undefined },
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
        description: [{ required: true, message: "风险描述不能为空", trigger: "blur" }]
      },
      levelCounts: { high: 0, medium: 0, low: 0 },
      statusCounts: { potential: 0, occurred: 0, eliminated: 0 }
    };
  },
  computed: {
    totalPages() { return Math.ceil(this.total / this.queryParams.pageSize) || 1; },
    visiblePages() {
      const pages = [];
      const start = Math.max(1, this.queryParams.pageNum - 2);
      const end = Math.min(this.totalPages, this.queryParams.pageNum + 2);
      for (let i = start; i <= end; i++) pages.push(i);
      return pages;
    }
  },
  created() { this.getList(); this.getProjectList(); this.getPersonList(); },
  methods: {
    getList() {
      this.loading = true;
      listRisk(this.queryParams).then(response => {
        this.riskList = response.rows || [];
        this.total = response.total || 0;
        this.levelCounts = {
          high: this.riskList.filter(r => r.level === '高').length,
          medium: this.riskList.filter(r => r.level === '中').length,
          low: this.riskList.filter(r => r.level === '低').length
        };
        this.statusCounts = {
          potential: this.riskList.filter(r => r.status === '潜在').length,
          occurred: this.riskList.filter(r => r.status === '已发生').length,
          eliminated: this.riskList.filter(r => r.status === '已消除').length
        };
        this.loading = false;
      });
    },
    getProjectList() { listProjectAll().then(response => { this.projectOptions = response.data || []; }); },
    getPersonList() { listPersonAll().then(response => { this.personOptions = response.data || []; }); },
    handleQuery() { this.queryParams.pageNum = 1; this.getList(); },
    resetQuery() { this.queryParams = { pageNum: 1, pageSize: 10, projectId: undefined, level: undefined, status: undefined, keyword: undefined }; this.getList(); },
    changePage(delta) { this.queryParams.pageNum += delta; this.getList(); },
    goToPage(page) { this.queryParams.pageNum = page; this.getList(); },
    reset() { this.form = { id: undefined, projectId: undefined, projectName: undefined, description: undefined, level: "中", status: "潜在", ownerId: undefined, ownerName: undefined, measure: undefined }; this.resetForm("form"); },
    cancel() { this.open = false; this.reset(); },
    handleAdd() { this.reset(); this.open = true; this.title = "添加风险"; },
    handleUpdate(row) { this.reset(); getRisk(row.id).then(response => { this.form = response.data; this.open = true; this.title = "修改风险"; }); },
    handleDetail(row) {
      getRisk(row.id).then(response => {
        this.detailData = response.data;
        this.detailTab = 'basic';
        this.newHistoryContent = '';
        this.newHistoryStatus = '';
        this.detailOpen = true;
      });
    },
    handleProjectChange(val) { const project = this.projectOptions.find(p => p.id === val); this.form.projectName = project ? project.name : ''; },
    handleOwnerChange(val) { const person = this.personOptions.find(p => p.id === val); this.form.ownerName = person ? person.name : ''; },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != undefined) {
            updateRisk(this.form).then(() => { this.$modal.msgSuccess("修改成功"); this.open = false; this.getList(); });
          } else {
            addRisk(this.form).then(() => { this.$modal.msgSuccess("新增成功"); this.open = false; this.getList(); });
          }
        }
      });
    },
    handleDelete(row) {
      this.$modal.confirm('是否确认删除该风险？').then(() => delRisk(row.id)).then(() => { this.getList(); this.$modal.msgSuccess("删除成功"); }).catch(() => {});
    },
    handleExport() { this.download('system/risk/export', { ...this.queryParams }, `risk_${new Date().getTime()}.xlsx`); },
    goTo(path) { this.$router.push(path); },
    goToProject(row) { this.$router.push({ path: '/system/project', query: { id: row.projectId } }); },
    // 等级emoji
    levelEmoji(level) {
      const map = { '高': '🔴', '中': '🟡', '低': '🔵' };
      return map[level] || '';
    },
    // 添加状态变更记录
    addHistoryRecord() {
      if (!this.newHistoryContent && !this.newHistoryStatus) {
        this.$modal.msgWarning("请输入更新说明或选择状态变更");
        return;
      }
      this.$modal.msgSuccess("记录添加成功");
      const now = new Date();
      const timeStr = `${now.getFullYear()}-${String(now.getMonth()+1).padStart(2,'0')}-${String(now.getDate()).padStart(2,'0')} ${String(now.getHours()).padStart(2,'0')}:${String(now.getMinutes()).padStart(2,'0')}`;
      if (!this.detailData.history) this.detailData.history = [];
      let action = '';
      if (this.newHistoryStatus && this.newHistoryStatus !== this.detailData.status) {
        action = `将状态从 ${this.detailData.status} 变更为 ${this.newHistoryStatus}`;
        this.detailData.status = this.newHistoryStatus;
      } else {
        action = '更新应对措施';
      }
      this.detailData.history.unshift({
        time: timeStr,
        user: '当前用户',
        action: action,
        note: this.newHistoryContent
      });
      this.newHistoryContent = '';
      this.newHistoryStatus = '';
    },
    levelClass(level) { const map = { '高': 'high', '中': 'medium', '低': 'low' }; return map[level] || ''; },
    riskStatusClass(status) { const map = { '潜在': 'potential', '已发生': 'occurred', '已消除': 'resolved' }; return map[status] || ''; }
  }
};
</script>

<style lang="scss" scoped>
@import "@/assets/styles/ppmanage.scss";

.pp-page-container { background: white; border-radius: 12px; border: 1px solid #E5E7EB; overflow: hidden; }
.pp-top-nav { display: flex; justify-content: space-between; align-items: center; padding: 16px 24px; border-bottom: 1px solid #E5E7EB; background: #F9FAFB; }
.pp-breadcrumb { display: flex; align-items: center; gap: 8px; font-size: 14px; color: #6B7280; a { color: #6B7280; cursor: pointer; &:hover { color: #2563EB; } } .current { color: #1F2937; font-weight: 500; } }
.pp-page-actions { display: flex; gap: 8px; }

.pp-filter-bar { display: flex; gap: 16px; padding: 16px 24px; background: white; border-bottom: 1px solid #E5E7EB; flex-wrap: wrap; }
.pp-filter-group { display: flex; align-items: center; gap: 8px; }
.pp-filter-label { font-size: 13px; color: #6B7280; }
.pp-filter-select { min-width: 140px; }

/* 风险概览卡片 */
.pp-risk-overview { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; padding: 20px 24px; background: white; border-bottom: 1px solid #E5E7EB; }
.pp-risk-card { padding: 16px; border-radius: 8px; text-align: center;
  &.danger { background: #fef2f2; border: 1px solid #fecaca; }
  &.warning { background: #fffbeb; border: 1px solid #fde68a; }
  &.success { background: #f0fdf4; border: 1px solid #bbf7d0; }
  &.info { background: #eff6ff; border: 1px solid #bfdbfe; }
}
.pp-risk-num { font-size: 32px; font-weight: 700; line-height: 1; }
.pp-risk-card.danger .pp-risk-num { color: #EF4444; }
.pp-risk-card.warning .pp-risk-num { color: #F59E0B; }
.pp-risk-card.success .pp-risk-num { color: #10B981; }
.pp-risk-card.info .pp-risk-num { color: #2563EB; }
.pp-risk-label { font-size: 13px; color: #4B5563; margin-top: 6px; }

.pp-data-table { width: 100%; border-collapse: collapse; th, td { padding: 12px 16px; text-align: left; font-size: 13px; border-bottom: 1px solid #F3F4F6; } th { color: #6B7280; font-weight: 500; background: #F9FAFB; } tr:hover td { background: #F9FAFB; } tr.resolved { opacity: 0.7; } }
.pp-project-link { color: #2563EB; cursor: pointer; font-weight: 500; &:hover { text-decoration: underline; } }
.pp-risk-desc { max-width: 280px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; cursor: pointer; &:hover { color: #2563EB; } }
.pp-measure-cell { max-width: 200px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; cursor: pointer; color: #6B7280; font-size: 12px; &:hover { color: #2563EB; } }

/* 等级标签 */
.pp-level-tag { display: inline-flex; align-items: center; gap: 4px; padding: 3px 10px; border-radius: 4px; font-size: 12px; font-weight: 500; &.high { background: #fef2f2; color: #EF4444; } &.medium { background: #fffbeb; color: #F59E0B; } &.low { background: #eff6ff; color: #2563EB; } }
/* 风险状态标签 */
.pp-status-tag.potential { background: #fffbeb; color: #F59E0B; }
.pp-status-tag.occurred { background: #fef2f2; color: #EF4444; }
.pp-status-tag.resolved { background: #f0fdf4; color: #10B981; }

.pp-action-btns { display: flex; gap: 8px; }
.pp-action-btn { padding: 4px 8px; font-size: 12px; border: 1px solid #E5E7EB; background: white; border-radius: 4px; cursor: pointer; color: #4B5563; &:hover { border-color: #2563EB; color: #2563EB; } }

.pp-pagination { display: flex; justify-content: space-between; align-items: center; padding: 16px 24px; background: #F9FAFB; border-top: 1px solid #E5E7EB; font-size: 13px; color: #6B7280; }
.pp-pagination-btns { display: flex; gap: 4px; }
.pp-page-btn { padding: 6px 12px; border: 1px solid #E5E7EB; background: white; border-radius: 4px; cursor: pointer; font-size: 13px; &.active { background: #2563EB; color: white; border-color: #2563EB; } &:hover:not(.active):not(:disabled) { border-color: #2563EB; color: #2563EB; } &:disabled { opacity: 0.5; cursor: not-allowed; } }

/* 详情页 */
.pp-detail-header { padding: 24px; border-bottom: 1px solid #E5E7EB; }
.pp-detail-top { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16px; }
.pp-detail-title { font-size: 20px; font-weight: 600; color: #111827; margin-bottom: 4px; }
.pp-detail-project { font-size: 14px; color: #6B7280; }
.pp-detail-tags { display: flex; gap: 8px; }

/* 信息网格 */
.pp-info-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-top: 20px; }
.pp-info-item { display: flex; flex-direction: column; gap: 4px; }
.pp-info-label { font-size: 12px; color: #6B7280; }
.pp-info-value { font-size: 14px; color: #1F2937; font-weight: 500; }

/* Tab 导航 */
.pp-tab-nav { display: flex; border-bottom: 1px solid #E5E7EB; background: white; }
.pp-tab-item { padding: 14px 24px; font-size: 14px; font-weight: 500; color: #6B7280; cursor: pointer; border-bottom: 2px solid transparent; &:hover { color: #374151; } &.active { color: #2563EB; border-bottom-color: #2563EB; } }
.pp-tab-content { padding: 24px; }

/* 详情区块 */
.pp-detail-section { margin-bottom: 24px; }
.pp-section-title { font-size: 14px; font-weight: 600; color: #374151; margin-bottom: 12px; }
.pp-section-content { padding: 16px; background: #F9FAFB; border-radius: 8px; font-size: 14px; color: #374151; line-height: 1.6; }

/* 历史记录 */
.pp-history-list { border: 1px solid #E5E7EB; border-radius: 8px; overflow: hidden; }
.pp-history-item { display: flex; gap: 16px; padding: 12px 16px; border-bottom: 1px solid #F3F4F6; font-size: 13px; &:last-child { border-bottom: none; } }
.pp-history-time { color: #9CA3AF; min-width: 140px; }
.pp-history-content { flex: 1; }
.pp-history-user { color: #2563EB; font-weight: 500; }
.pp-history-note { color: #6B7280; font-size: 12px; margin-top: 4px; }

/* 添加历史记录 */
.pp-add-history { display: flex; gap: 12px; }
.pp-history-input { flex: 1; padding: 12px; border: 1px solid #E5E7EB; border-radius: 6px; font-size: 14px; min-height: 80px; font-family: inherit; resize: vertical; }
.pp-history-actions { display: flex; flex-direction: column; gap: 8px; }

/* 空状态 */
.pp-empty-section { text-align: center; padding: 20px; color: #9CA3AF; font-size: 14px; }
.pp-empty-tip { text-align: center; padding: 30px; color: #909399; font-size: 14px; }
.pp-dialog-footer { display: flex; justify-content: flex-end; gap: 8px; }
</style>