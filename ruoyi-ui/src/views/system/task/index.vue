<template>
  <div class="pp-page-container">
    <!-- 顶部导航 -->
    <div class="pp-top-nav">
      <div class="pp-breadcrumb">
        <a @click="goTo('/dashboard')">首页</a>
        <span>/</span>
        <span class="current">任务管理</span>
      </div>
      <div class="pp-page-actions">
        <button class="pp-btn pp-btn-primary" @click="handleAdd">+ 新增任务</button>
      </div>
    </div>

    <!-- Tab 导航 -->
    <div class="pp-tab-nav">
      <div class="pp-tab-item" :class="{ active: activeName === 'task' }" @click="switchTab('task')">任务管理</div>
      <div class="pp-tab-item" :class="{ active: activeName === 'instance' }" @click="switchTab('instance')">任务实例</div>
      <div class="pp-tab-item" :class="{ active: activeName === 'stats' }" @click="switchTab('stats')">完成统计</div>
    </div>

    <!-- 任务管理 Tab -->
    <div class="pp-tab-content" v-show="activeName === 'task'">
      <!-- 筛选区域 -->
      <div class="pp-filter-bar">
        <div class="pp-filter-group">
          <el-input v-model="queryParams.name" placeholder="搜索任务名称..." clearable size="small" class="pp-filter-input" @keyup.enter.native="handleQuery" />
        </div>
        <div class="pp-filter-group">
          <span class="pp-filter-label">类型</span>
          <el-select v-model="queryParams.type" placeholder="全部" clearable size="small" class="pp-filter-select" @change="handleQuery">
            <el-option label="周期性" value="周期性" />
            <el-option label="一次性" value="一次性" />
          </el-select>
        </div>
        <div class="pp-filter-group">
          <span class="pp-filter-label">状态</span>
          <el-select v-model="queryParams.status" placeholder="全部" clearable size="small" class="pp-filter-select" @change="handleQuery">
            <el-option label="启用" value="启用" />
            <el-option label="禁用" value="禁用" />
          </el-select>
        </div>
        <div class="pp-filter-group">
          <button class="pp-btn pp-btn-secondary" @click="handleQuery">搜索</button>
        </div>
      </div>

      <!-- 统计栏 -->
      <div class="pp-stats-bar">
        <div class="pp-stat-item">
          共 <span class="pp-stat-num" style="color: #111827;">{{ total }}</span> 个任务
        </div>
        <div class="pp-stat-item">
          启用 <span class="pp-stat-num" style="color: #10B981;">{{ statusCounts.enabled }}</span>
        </div>
        <div class="pp-stat-item">
          禁用 <span class="pp-stat-num" style="color: #6B7280;">{{ statusCounts.disabled }}</span>
        </div>
      </div>

      <!-- 数据表格 -->
      <table class="pp-data-table" v-loading="loading">
        <thead>
          <tr>
            <th style="width: 40px;"><input type="checkbox" v-model="selectAll" @change="handleSelectAll" /></th>
            <th>任务名称</th>
            <th>类型</th>
            <th>周期</th>
            <th>截止时间</th>
            <th>状态</th>
            <th>描述</th>
            <th style="min-width: 120px;">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(row, index) in taskList" :key="index" :class="{ selected: selectedIds.includes(row.id) }">
            <td><input type="checkbox" :checked="selectedIds.includes(row.id)" @change="handleSelect(row.id)" /></td>
            <td><span class="pp-task-name">{{ row.name }}</span></td>
            <td><span class="pp-type-tag" :class="row.type === '周期性' ? 'cycle' : 'once'">{{ row.type }}</span></td>
            <td>{{ row.cycle || '-' }}</td>
            <td>{{ row.deadlineTime || '-' }}</td>
            <td><span class="pp-status-tag" :class="row.status === '启用' ? 'success' : 'closed'">{{ row.status }}</span></td>
            <td>{{ row.description || '-' }}</td>
            <td>
              <div class="pp-action-btns">
                <button class="pp-action-btn" @click="handleUpdate(row)">编辑</button>
                <button class="pp-action-btn danger" @click="handleDelete(row)">删除</button>
              </div>
            </td>
          </tr>
          <tr v-if="taskList.length === 0"><td colspan="8" class="pp-empty-tip">暂无任务数据</td></tr>
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
    </div>

    <!-- 任务实例 Tab -->
    <div class="pp-tab-content" v-show="activeName === 'instance'">
      <!-- 筛选区域 -->
      <div class="pp-filter-bar">
        <div class="pp-filter-group">
          <span class="pp-filter-label">人员</span>
          <el-select v-model="instanceQuery.personId" placeholder="全部人员" clearable filterable size="small" class="pp-filter-select" @change="getInstanceList">
            <el-option v-for="item in personOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </div>
        <div class="pp-filter-group">
          <span class="pp-filter-label">完成状态</span>
          <el-select v-model="instanceQuery.completed" placeholder="全部" clearable size="small" class="pp-filter-select" @change="getInstanceList">
            <el-option label="未完成" :value="0" />
            <el-option label="已完成" :value="1" />
          </el-select>
        </div>
        <div class="pp-filter-group">
          <button class="pp-btn pp-btn-secondary" @click="getInstanceList">搜索</button>
        </div>
      </div>

      <!-- 统计栏 -->
      <div class="pp-stats-bar">
        <div class="pp-stat-item">
          共 <span class="pp-stat-num" style="color: #111827;">{{ instanceTotal }}</span> 个实例
        </div>
        <div class="pp-stat-item">
          未完成 <span class="pp-stat-num" style="color: #EF4444;">{{ instanceCounts.incomplete }}</span>
        </div>
        <div class="pp-stat-item">
          已完成 <span class="pp-stat-num" style="color: #10B981;">{{ instanceCounts.completed }}</span>
        </div>
        <div class="pp-stat-item">
          按时 <span class="pp-stat-num" style="color: #10B981;">{{ instanceCounts.onTime }}</span>
        </div>
        <div class="pp-stat-item">
          超期 <span class="pp-stat-num" style="color: #F59E0B;">{{ instanceCounts.late }}</span>
        </div>
      </div>

      <!-- 数据表格 -->
      <table class="pp-data-table" v-loading="instanceLoading">
        <thead>
          <tr>
            <th>任务名称</th>
            <th>负责人</th>
            <th>周期</th>
            <th>截止时间</th>
            <th>完成状态</th>
            <th>是否按时</th>
            <th style="min-width: 100px;">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(row, index) in instanceList" :key="index" :class="{ overdue: isOverdue(row) }">
            <td><span class="pp-task-name">{{ row.taskName }}</span></td>
            <td>{{ row.personName || '-' }}</td>
            <td>{{ row.period || '-' }}</td>
            <td :class="{ overdue: isOverdue(row) }">{{ row.deadline || '-' }}</td>
            <td>
              <span class="pp-status-tag" :class="row.completed === 1 ? 'success' : 'danger'">{{ row.completed === 1 ? '已完成' : '未完成' }}</span>
            </td>
            <td>
              <span v-if="row.completed === 1" class="pp-status-tag" :class="row.onTime === 1 ? 'success' : 'warning'">{{ row.onTime === 1 ? '按时' : '超期' }}</span>
              <span v-else>-</span>
            </td>
            <td>
              <button v-if="row.completed === 0" class="pp-btn pp-btn-primary pp-btn-sm" @click="handleComplete(row)">完成</button>
              <span v-else class="pp-completed-text">已完成</span>
            </td>
          </tr>
          <tr v-if="instanceList.length === 0"><td colspan="7" class="pp-empty-tip">暂无实例数据</td></tr>
        </tbody>
      </table>

      <!-- 分页 -->
      <div class="pp-pagination">
        <span>共 {{ instanceTotal }} 条记录，第 {{ instanceQuery.pageNum }}/{{ instanceTotalPages }} 页</span>
        <div class="pp-pagination-btns">
          <button class="pp-page-btn" :disabled="instanceQuery.pageNum <= 1" @click="changeInstancePage(-1)">上一页</button>
          <button class="pp-page-btn" :class="{ active: instanceQuery.pageNum === p }" v-for="p in visibleInstancePages" :key="p" @click="goToInstancePage(p)">{{ p }}</button>
          <button class="pp-page-btn" :disabled="instanceQuery.pageNum >= instanceTotalPages" @click="changeInstancePage(1)">下一页</button>
        </div>
      </div>
    </div>

    <!-- 完成统计 Tab -->
    <div class="pp-tab-content" v-show="activeName === 'stats'">
      <div class="pp-stats-section">
        <div class="pp-stats-card">
          <div class="pp-stats-title">本周任务完成情况</div>
          <div class="pp-stats-grid">
            <div class="pp-stat-box">
              <div class="pp-stat-value">{{ instanceCounts.completed }}</div>
              <div class="pp-stat-label">已完成</div>
            </div>
            <div class="pp-stat-box">
              <div class="pp-stat-value" style="color: #10B981;">{{ instanceCounts.onTime }}</div>
              <div class="pp-stat-label">按时完成</div>
            </div>
            <div class="pp-stat-box">
              <div class="pp-stat-value" style="color: #F59E0B;">{{ instanceCounts.late }}</div>
              <div class="pp-stat-label">超期完成</div>
            </div>
            <div class="pp-stat-box">
              <div class="pp-stat-value" style="color: #EF4444;">{{ instanceCounts.incomplete }}</div>
              <div class="pp-stat-label">未完成</div>
            </div>
            <div class="pp-stat-box">
              <div class="pp-stat-value" style="color: #2563EB;">{{ completionRate }}%</div>
              <div class="pp-stat-label">完成率</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加/编辑任务对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body custom-class="pp-dialog">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="任务名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入任务名称" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型" @change="handleTypeChange">
            <el-option label="周期性" value="周期性" />
            <el-option label="一次性" value="一次性" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.type === '周期性'" label="周期" prop="cycle">
          <el-select v-model="form.cycle" placeholder="请选择周期">
            <el-option label="每日" value="每日" />
            <el-option label="每周" value="每周" />
            <el-option label="每月" value="每月" />
          </el-select>
        </el-form-item>
        <el-form-item label="截止时间" prop="deadlineTime">
          <el-time-picker v-model="form.deadlineTime" placeholder="选择截止时间" value-format="HH:mm:ss" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="启用">启用</el-radio>
            <el-radio label="禁用">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="pp-dialog-footer">
        <button class="pp-btn pp-btn-secondary" @click="cancel">取消</button>
        <button class="pp-btn pp-btn-primary" @click="submitForm">确定</button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listTask, getTask, delTask, addTask, updateTask, listInstance, completeTask } from "@/api/system/task";
import { listPersonAll } from "@/api/system/person";

export default {
  name: "Task",
  data() {
    return {
      activeName: 'task',
      loading: false,
      total: 0,
      taskList: [],
      selectedIds: [],
      selectAll: false,
      title: "",
      open: false,
      instanceLoading: false,
      instanceList: [],
      instanceTotal: 0,
      personOptions: [],
      queryParams: { pageNum: 1, pageSize: 10, name: undefined, type: undefined, status: undefined },
      instanceQuery: { pageNum: 1, pageSize: 10, personId: undefined, completed: undefined },
      form: {},
      rules: {
        name: [{ required: true, message: "任务名称不能为空", trigger: "blur" }],
        type: [{ required: true, message: "类型不能为空", trigger: "change" }]
      },
      statusCounts: { enabled: 0, disabled: 0 },
      instanceCounts: { completed: 0, incomplete: 0, onTime: 0, late: 0 }
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
    },
    instanceTotalPages() { return Math.ceil(this.instanceTotal / this.instanceQuery.pageSize) || 1; },
    visibleInstancePages() {
      const pages = [];
      const start = Math.max(1, this.instanceQuery.pageNum - 2);
      const end = Math.min(this.instanceTotalPages, this.instanceQuery.pageNum + 2);
      for (let i = start; i <= end; i++) pages.push(i);
      return pages;
    },
    completionRate() {
      const total = this.instanceCounts.completed + this.instanceCounts.incomplete;
      return total > 0 ? Math.round(this.instanceCounts.completed * 100 / total) : 0;
    }
  },
  created() { this.getList(); this.getPersonList(); },
  methods: {
    getList() {
      this.loading = true;
      listTask(this.queryParams).then(response => {
        this.taskList = response.rows || [];
        this.total = response.total || 0;
        this.statusCounts = {
          enabled: this.taskList.filter(t => t.status === '启用').length,
          disabled: this.taskList.filter(t => t.status === '禁用').length
        };
        this.loading = false;
      });
    },
    getInstanceList() {
      this.instanceLoading = true;
      listInstance(this.instanceQuery).then(response => {
        this.instanceList = response.rows || [];
        this.instanceTotal = response.total || 0;
        this.instanceCounts = {
          completed: this.instanceList.filter(i => i.completed === 1).length,
          incomplete: this.instanceList.filter(i => i.completed === 0).length,
          onTime: this.instanceList.filter(i => i.completed === 1 && i.onTime === 1).length,
          late: this.instanceList.filter(i => i.completed === 1 && i.onTime === 0).length
        };
        this.instanceLoading = false;
      });
    },
    getPersonList() { listPersonAll().then(response => { this.personOptions = response.data || []; }); },
    switchTab(name) {
      this.activeName = name;
      if (name === 'instance' || name === 'stats') this.getInstanceList();
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList(); },
    changePage(delta) { this.queryParams.pageNum += delta; this.getList(); },
    goToPage(page) { this.queryParams.pageNum = page; this.getList(); },
    changeInstancePage(delta) { this.instanceQuery.pageNum += delta; this.getInstanceList(); },
    goToInstancePage(page) { this.instanceQuery.pageNum = page; this.getInstanceList(); },
    handleSelectAll() { this.selectedIds = this.selectAll ? this.taskList.map(t => t.id) : []; },
    handleSelect(id) {
      const idx = this.selectedIds.indexOf(id);
      if (idx >= 0) this.selectedIds.splice(idx, 1);
      else this.selectedIds.push(id);
      this.selectAll = this.selectedIds.length === this.taskList.length;
    },
    reset() { this.form = { id: undefined, name: undefined, type: undefined, cycle: undefined, deadlineTime: undefined, status: "启用", description: undefined }; this.resetForm("form"); },
    cancel() { this.open = false; this.reset(); },
    handleAdd() { this.reset(); this.open = true; this.title = "添加任务"; },
    handleUpdate(row) { this.reset(); getTask(row.id || this.selectedIds[0]).then(response => { this.form = response.data; this.open = true; this.title = "修改任务"; }); },
    handleTypeChange() { this.form.cycle = undefined; },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != undefined) {
            updateTask(this.form).then(() => { this.$modal.msgSuccess("修改成功"); this.open = false; this.getList(); });
          } else {
            addTask(this.form).then(() => { this.$modal.msgSuccess("新增成功"); this.open = false; this.getList(); });
          }
        }
      });
    },
    handleDelete(row) {
      const ids = row.id || this.selectedIds.join(',');
      this.$modal.confirm('是否确认删除该任务？').then(() => delTask(ids)).then(() => { this.getList(); this.$modal.msgSuccess("删除成功"); }).catch(() => {});
    },
    handleComplete(row) {
      this.$modal.confirm('确认完成该任务？').then(() => completeTask(row.id)).then(() => { this.getInstanceList(); this.$modal.msgSuccess("已完成"); }).catch(() => {});
    },
    isOverdue(row) {
      if (row.completed === 1) return false;
      if (!row.deadline) return false;
      return new Date(row.deadline) < new Date();
    },
    goTo(path) { this.$router.push(path); }
  }
};
</script>

<style lang="scss" scoped>
@import "@/assets/styles/ppmanage.scss";

.pp-page-container { background: white; border-radius: 12px; border: 1px solid #E5E7EB; overflow: hidden; }
.pp-top-nav { display: flex; justify-content: space-between; align-items: center; padding: 16px 24px; border-bottom: 1px solid #E5E7EB; background: #F9FAFB; }
.pp-breadcrumb { display: flex; align-items: center; gap: 8px; font-size: 14px; color: #6B7280; a { color: #6B7280; cursor: pointer; &:hover { color: #2563EB; } } .current { color: #1F2937; font-weight: 500; } }
.pp-page-actions { display: flex; gap: 8px; }

.pp-tab-nav { display: flex; border-bottom: 1px solid #E5E7EB; background: white; }
.pp-tab-item { padding: 14px 24px; font-size: 14px; font-weight: 500; color: #6B7280; cursor: pointer; border-bottom: 2px solid transparent; &:hover { color: #374151; } &.active { color: #2563EB; border-bottom-color: #2563EB; } }

.pp-tab-content { padding: 0; }

.pp-filter-bar { display: flex; gap: 16px; padding: 16px 24px; background: white; border-bottom: 1px solid #E5E7EB; flex-wrap: wrap; }
.pp-filter-group { display: flex; align-items: center; gap: 8px; }
.pp-filter-label { font-size: 13px; color: #6B7280; }
.pp-filter-select { min-width: 120px; }
.pp-filter-input { width: 180px; }

.pp-stats-bar { display: flex; gap: 32px; padding: 12px 24px; background: #F9FAFB; border-bottom: 1px solid #E5E7EB; font-size: 13px; }
.pp-stat-item { display: flex; align-items: center; gap: 8px; }
.pp-stat-num { font-weight: 600; font-size: 16px; }

.pp-data-table { width: 100%; border-collapse: collapse; th, td { padding: 12px 16px; text-align: left; font-size: 13px; border-bottom: 1px solid #F3F4F6; } th { color: #6B7280; font-weight: 500; background: #F9FAFB; } tr:hover td { background: #F9FAFB; } tr.selected td { background: #eff6ff; } tr.overdue td { background: #fef2f2; } }
.pp-task-name { color: #1F2937; font-weight: 500; }

/* 类型标签 */
.pp-type-tag { padding: 2px 8px; border-radius: 4px; font-size: 11px; font-weight: 500; &.cycle { background: #eff6ff; color: #2563EB; } &.once { background: #f0fdf4; color: #10B981; } }

/* 状态标签 */
.pp-status-tag.success { background: #f0fdf4; color: #10B981; }
.pp-status-tag.danger { background: #fef2f2; color: #EF4444; }
.pp-status-tag.warning { background: #fffbeb; color: #F59E0B; }
.pp-status-tag.closed { background: #F3F4F6; color: #6B7280; }

.overdue { color: #EF4444; font-weight: 500; }
.pp-completed-text { color: #10B981; font-size: 13px; }
.pp-btn-sm { padding: 4px 12px; font-size: 12px; }

.pp-action-btns { display: flex; gap: 8px; }
.pp-action-btn { padding: 4px 8px; font-size: 12px; border: 1px solid #E5E7EB; background: white; border-radius: 4px; cursor: pointer; color: #4B5563; &:hover { border-color: #2563EB; color: #2563EB; } &.danger:hover { border-color: #EF4444; color: #EF4444; } }

.pp-pagination { display: flex; justify-content: space-between; align-items: center; padding: 16px 24px; background: #F9FAFB; border-top: 1px solid #E5E7EB; font-size: 13px; color: #6B7280; }
.pp-pagination-btns { display: flex; gap: 4px; }
.pp-page-btn { padding: 6px 12px; border: 1px solid #E5E7EB; background: white; border-radius: 4px; cursor: pointer; font-size: 13px; &.active { background: #2563EB; color: white; border-color: #2563EB; } &:hover:not(.active):not(:disabled) { border-color: #2563EB; color: #2563EB; } &:disabled { opacity: 0.5; cursor: not-allowed; } }

/* 统计区域 */
.pp-stats-section { padding: 24px; }
.pp-stats-card { background: #F9FAFB; border-radius: 8px; padding: 20px; }
.pp-stats-title { font-size: 16px; font-weight: 600; color: #374151; margin-bottom: 16px; }
.pp-stats-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 16px; }
.pp-stat-box { text-align: center; padding: 16px; background: white; border-radius: 6px; border: 1px solid #E5E7EB; }
.pp-stat-value { font-size: 32px; font-weight: 700; color: #374151; }
.pp-stat-label { font-size: 13px; color: #6B7280; margin-top: 4px; }

.pp-empty-tip { text-align: center; padding: 30px; color: #909399; font-size: 14px; }
.pp-dialog-footer { display: flex; justify-content: flex-end; gap: 8px; }
</style>