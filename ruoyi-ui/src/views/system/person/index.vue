<template>
  <div class="pp-page-container">
    <!-- 顶部导航 -->
    <div class="pp-top-nav">
      <div class="pp-breadcrumb">
        <a @click="goTo('/dashboard')">首页</a>
        <span>/</span>
        <span class="current">人员管理</span>
      </div>
      <div class="pp-page-actions">
        <button class="pp-btn pp-btn-secondary" @click="handleExport">导出Excel</button>
        <button class="pp-btn pp-btn-primary" @click="handleAdd">+ 新增人员</button>
      </div>
    </div>

    <!-- 筛选区域 -->
    <div class="pp-filter-bar">
      <div class="pp-filter-group">
        <span class="pp-filter-label">人员状态</span>
        <el-select v-model="queryParams.status" placeholder="全部状态" clearable size="small" class="pp-filter-select" @change="handleQuery">
          <el-option label="在职" value="在职" />
          <el-option label="离职" value="离职" />
        </el-select>
      </div>
      <div class="pp-filter-group">
        <span class="pp-filter-label">学历</span>
        <el-select v-model="queryParams.education" placeholder="全部学历" clearable size="small" class="pp-filter-select" @change="handleQuery">
          <el-option label="本科" value="本科" />
          <el-option label="硕士" value="硕士" />
          <el-option label="博士" value="博士" />
          <el-option label="大专" value="大专" />
        </el-select>
      </div>
      <div class="pp-filter-group">
        <el-input v-model="queryParams.keyword" placeholder="搜索姓名/手机号..." clearable size="small" class="pp-filter-input" @keyup.enter.native="handleQuery" />
      </div>
      <div class="pp-filter-group">
        <button class="pp-btn pp-btn-secondary" @click="handleQuery">搜索</button>
        <button class="pp-btn pp-btn-secondary" @click="resetQuery">重置</button>
      </div>
    </div>

    <!-- 统计栏 -->
    <div class="pp-stats-bar">
      <div class="pp-stat-item">
        共 <span class="pp-stat-num" style="color: #111827;">{{ total }}</span> 人
      </div>
      <div class="pp-stat-item">
        在职 <span class="pp-stat-num" style="color: #10B981;">{{ statusCounts.active }}</span>
      </div>
      <div class="pp-stat-item">
        离职 <span class="pp-stat-num" style="color: #6B7280;">{{ statusCounts.left }}</span>
      </div>
    </div>

    <!-- 数据表格 -->
    <table class="pp-data-table" v-loading="loading">
      <thead>
        <tr>
          <th style="min-width: 240px;">人员信息</th>
          <th>等级</th>
          <th>年龄</th>
          <th>工作年限</th>
          <th>入司年限</th>
          <th>毕业院校</th>
          <th style="min-width: 160px;">在建项目</th>
          <th>直属</th>
          <th style="min-width: 100px;">操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(row, index) in personList" :key="index" :class="{ inactive: row.status === '离职' }">
          <td>
            <div class="pp-person-cell">
              <div class="pp-avatar" :class="{ 'has-image': row.avatar }" :style="avatarStyle(row)">
                <img v-if="row.avatar" :src="row.avatar" class="pp-avatar-img" />
                <span v-else>{{ (row.name || '').charAt(0) }}</span>
              </div>
              <div class="pp-person-info">
                <div class="pp-person-name" @click="handleDetail(row)">{{ row.name }}</div>
                <div class="pp-person-contact">{{ row.contact || '-' }}</div>
              </div>
            </div>
          </td>
          <td><span class="pp-level-tag" :class="{ inactive: row.status === '离职' }">{{ row.level || '-' }}</span></td>
          <td :class="{ gray: row.status === '离职' }">{{ row.age || '-' }}</td>
          <td :class="{ gray: row.status === '离职' }">{{ row.workYears || 0 }}年</td>
          <td :class="{ gray: row.status === '离职' }">{{ row.companyYears || 0 }}年</td>
          <td :class="{ gray: row.status === '离职' }">{{ row.school || '-' }}</td>
          <td>
            <div class="pp-project-tags" v-if="row.projects && row.projects.length > 0">
              <span class="pp-project-tag" v-for="(proj, pi) in visibleProjects(row.projects)" :key="pi">{{ proj }}</span>
              <span class="pp-project-tag more" v-if="row.projects.length > 2">+{{ row.projects.length - 2 }}</span>
            </div>
            <span class="pp-no-projects" v-else>-</span>
          </td>
          <td>
            <span class="pp-direct-tag" :class="{ direct: row.isDirect === '1', indirect: row.isDirect === '0' }">{{ row.isDirect === '1' ? '是' : '否' }}</span>
          </td>
          <td>
            <div class="pp-action-btns">
              <button class="pp-action-btn" @click="handleDetail(row)">详情</button>
              <button class="pp-action-btn" @click="handleUpdate(row)" v-if="row.status !== '离职'">编辑</button>
            </div>
          </td>
        </tr>
        <tr v-if="personList.length === 0">
          <td colspan="9" class="pp-empty-tip">暂无人员数据</td>
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

    <!-- 添加/编辑人员对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body custom-class="pp-dialog">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职位" prop="position">
              <el-select v-model="form.position" placeholder="请选择职位">
                <el-option label="开发工程师" value="开发工程师" />
                <el-option label="ETL工程师" value="ETL工程师" />
                <el-option label="业务工程师" value="业务工程师" />
                <el-option label="工程主管" value="工程主管" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="等级" prop="level">
              <el-select v-model="form.level" placeholder="请选择等级">
                <el-option label="B20" value="B20" />
                <el-option label="B30" value="B30" />
                <el-option label="B40" value="B40" />
                <el-option label="B50" value="B50" />
                <el-option label="B60" value="B60" />
                <el-option label="B70" value="B70" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系方式" prop="contact">
              <el-input v-model="form.contact" placeholder="请输入联系方式" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学历" prop="education">
              <el-select v-model="form.education" placeholder="请选择学历">
                <el-option label="本科" value="本科" />
                <el-option label="硕士" value="硕士" />
                <el-option label="博士" value="博士" />
                <el-option label="大专" value="大专" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="毕业院校" prop="school">
              <el-input v-model="form.school" placeholder="请输入毕业院校" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="籍贯" prop="hometown">
              <el-input v-model="form.hometown" placeholder="请输入籍贯" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工作年限" prop="workYears">
              <el-input-number v-model="form.workYears" :min="0" :max="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入司年限" prop="companyYears">
              <el-input-number v-model="form.companyYears" :min="0" :max="50" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker v-model="form.birthDate" type="date" value-format="yyyy-MM-dd" placeholder="选择出生日期" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入职日期" prop="entryDate">
              <el-date-picker v-model="form.entryDate" type="date" value-format="yyyy-MM-dd" placeholder="选择入职日期" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio label="在职">在职</el-radio>
                <el-radio label="离职">离职</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="直属下级" prop="isDirect">
              <el-switch v-model="form.isDirect" active-value="1" inactive-value="0" />
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

    <!-- 人员详情对话框 -->
    <el-dialog title="人员详情" :visible.sync="detailOpen" width="800px" append-to-body custom-class="pp-dialog">
      <div class="pp-detail-header">
        <div class="pp-detail-top">
          <div class="pp-avatar-large" :class="{ 'has-image': detailData.avatar }" :style="avatarStyle(detailData)">
            <img v-if="detailData.avatar" :src="detailData.avatar" class="pp-avatar-img" />
            <span v-else>{{ (detailData.name || '').charAt(0) }}</span>
          </div>
          <div class="pp-detail-info-main">
            <div class="pp-detail-name">
              {{ detailData.name }}
              <span class="pp-level-tag">{{ detailData.level }}</span>
            </div>
            <div class="pp-detail-meta">
              <span>状态：<span :style="{ color: detailData.status === '在职' ? '#10B981' : '#6B7280' }">{{ detailData.status }}</span></span>
              <span>📞 {{ detailData.contact || '-' }}</span>
              <span>✉️ {{ detailData.email || '-' }}</span>
            </div>
          </div>
        </div>
        <!-- 基本信息网格 -->
        <div class="pp-info-grid">
          <div class="pp-info-item">
            <div class="pp-info-label">年龄</div>
            <div class="pp-info-value">{{ detailData.age || '-' }}岁</div>
          </div>
          <div class="pp-info-item">
            <div class="pp-info-label">工作年限</div>
            <div class="pp-info-value">{{ detailData.workYears || 0 }}年</div>
          </div>
          <div class="pp-info-item">
            <div class="pp-info-label">入司年限</div>
            <div class="pp-info-value">{{ detailData.companyYears || 0 }}年</div>
          </div>
          <div class="pp-info-item">
            <div class="pp-info-label">学历</div>
            <div class="pp-info-value">{{ detailData.education || '-' }}</div>
          </div>
          <div class="pp-info-item">
            <div class="pp-info-label">毕业院校</div>
            <div class="pp-info-value">{{ detailData.school || '-' }}</div>
          </div>
          <div class="pp-info-item">
            <div class="pp-info-label">籍贯</div>
            <div class="pp-info-value">{{ detailData.hometown || '-' }}</div>
          </div>
          <div class="pp-info-item">
            <div class="pp-info-label">入职日期</div>
            <div class="pp-info-value">{{ detailData.entryDate || '-' }}</div>
          </div>
          <div class="pp-info-item">
            <div class="pp-info-label">在建项目数</div>
            <div class="pp-info-value">{{ (detailData.projects && detailData.projects.length) || 0 }}个</div>
          </div>
        </div>
      </div>
      <!-- Tab 导航 -->
      <div class="pp-tab-nav">
        <div class="pp-tab-item" :class="{ active: detailTab === 'projects' }" @click="detailTab = 'projects'">项目参与</div>
        <div class="pp-tab-item" :class="{ active: detailTab === 'tasks' }" @click="detailTab = 'tasks'">本周任务</div>
        <div class="pp-tab-item" :class="{ active: detailTab === 'records' }" @click="detailTab = 'records'">动态记录</div>
      </div>
      <!-- Tab 内容 -->
      <div class="pp-tab-content">
        <!-- 项目参与 -->
        <div v-show="detailTab === 'projects'">
          <h3 class="pp-section-title">在建项目 ({{ (detailData.projects && detailData.projects.length) || 0 }}个)</h3>
          <div class="pp-project-cards" v-if="detailData.projects && detailData.projects.length > 0">
            <div class="pp-project-card" v-for="(proj, pi) in detailData.projects" :key="pi">
              <div class="pp-project-card-header">
                <span class="pp-project-card-name">{{ proj.name }}</span>
                <span class="pp-project-card-role">{{ proj.role }}</span>
              </div>
              <div class="pp-project-card-info">
                <span>参与时间：{{ proj.joinDate || '-' }}</span>
              </div>
              <div class="pp-project-card-progress">
                <div class="pp-progress-label">项目进度 {{ proj.progress || 0 }}%</div>
                <div class="pp-progress-bar">
                  <div class="pp-progress-fill" :style="{ width: (proj.progress || 0) + '%', background: proj.status === '暂停' ? '#F59E0B' : '#2563EB' }"></div>
                </div>
              </div>
            </div>
          </div>
          <div class="pp-empty-section" v-else>暂无参与项目</div>
        </div>
        <!-- 本周任务 -->
        <div v-show="detailTab === 'tasks'">
          <h3 class="pp-section-title">本周任务 ({{ (detailData.tasks && detailData.tasks.length) || 0 }}项)</h3>
          <div class="pp-task-list" v-if="detailData.tasks && detailData.tasks.length > 0">
            <div class="pp-task-item" v-for="(task, ti) in detailData.tasks" :key="ti">
              <div class="pp-task-info">
                <div class="pp-task-check" :class="{ done: task.status === '已完成' }">
                  <span v-if="task.status === '已完成'">✓</span>
                </div>
                <div>
                  <div class="pp-task-name" :class="{ done: task.status === '已完成' }">{{ task.name }}</div>
                  <div class="pp-task-project">{{ task.project }}</div>
                </div>
              </div>
              <div class="pp-task-date" :class="{ overdue: task.overdue }">{{ task.deadline }}<span v-if="task.overdue"> (超期)</span></div>
            </div>
          </div>
          <div class="pp-empty-section" v-else>暂无本周任务</div>
        </div>
        <!-- 动态记录 -->
        <div v-show="detailTab === 'records'">
          <h3 class="pp-section-title">动态记录 ({{ personRecords.length }}条)</h3>
          <div class="pp-record-list" v-if="personRecords.length > 0">
            <div class="pp-record-item" v-for="(record, ri) in personRecords" :key="ri">
              <div class="pp-record-header">
                <span class="pp-record-type" :class="recordTypeClass(record.recordType)">{{ record.recordType }}</span>
                <span class="pp-record-time">{{ formatRecordTime(record.recordDate) }}</span>
                <span class="pp-record-user">记录人：{{ record.createBy }}</span>
              </div>
              <div class="pp-record-content">{{ record.content }}</div>
              <div class="pp-record-actions">
                <button class="pp-record-btn" @click="handleEditRecord(record)">编辑</button>
                <button class="pp-record-btn delete" @click="handleDeleteRecord(record)">删除</button>
              </div>
            </div>
          </div>
          <div class="pp-empty-section" v-else>暂无动态记录</div>
          <!-- 添加新记录 -->
          <div class="pp-add-record">
            <div class="pp-add-record-header">添加新记录</div>
            <el-row :gutter="20">
              <el-col :span="8">
                <el-select v-model="newRecord.recordType" placeholder="记录类型" size="small">
                  <el-option label="谈话记录" value="谈话记录" />
                  <el-option label="行为观察" value="行为观察" />
                  <el-option label="想法记录" value="想法记录" />
                  <el-option label="其他" value="其他" />
                </el-select>
              </el-col>
              <el-col :span="8">
                <el-date-picker v-model="newRecord.recordDate" type="datetime" placeholder="记录日期" size="small" value-format="yyyy-MM-dd HH:mm:ss" />
              </el-col>
              <el-col :span="8">
                <button class="pp-btn pp-btn-primary" size="small" @click="handleAddRecord">添加记录</button>
              </el-col>
            </el-row>
            <el-input v-model="newRecord.content" type="textarea" :rows="3" placeholder="输入记录内容..." style="margin-top: 12px;" />
          </div>
        </div>
      </div>
      <div slot="footer" class="pp-dialog-footer">
        <button class="pp-btn pp-btn-secondary" @click="detailOpen = false">关闭</button>
        <button class="pp-btn pp-btn-primary" @click="handleUpdate(detailData)">编辑信息</button>
      </div>
    </el-dialog>

    <!-- 编辑动态记录对话框 -->
    <el-dialog title="编辑动态记录" :visible.sync="editRecordOpen" width="500px" append-to-body custom-class="pp-dialog">
      <el-form ref="editRecordForm" :model="editRecordForm" label-width="80px">
        <el-form-item label="记录类型">
          <el-select v-model="editRecordForm.recordType">
            <el-option label="谈话记录" value="谈话记录" />
            <el-option label="行为观察" value="行为观察" />
            <el-option label="想法记录" value="想法记录" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="记录日期">
          <el-date-picker v-model="editRecordForm.recordDate" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" />
        </el-form-item>
        <el-form-item label="记录内容">
          <el-input v-model="editRecordForm.content" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="pp-dialog-footer">
        <button class="pp-btn pp-btn-secondary" @click="editRecordOpen = false">取消</button>
        <button class="pp-btn pp-btn-primary" @click="submitEditRecord">保存</button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listPerson, getPerson, delPerson, addPerson, updatePerson, getPersonStatusCount, getPersonRecords, addPersonRecord, updatePersonRecord, delPersonRecord } from "@/api/system/person";

export default {
  name: "Person",
  data() {
    return {
      loading: false,
      total: 0,
      personList: [],
      queryParams: { pageNum: 1, pageSize: 10, keyword: undefined, education: undefined, status: undefined },
      title: "",
      open: false,
      detailOpen: false,
      detailTab: 'projects',
      detailData: {},
      form: {},
      rules: { name: [{ required: true, message: "姓名不能为空", trigger: "blur" }] },
      statusCounts: { active: 0, left: 0 },
      avatarColors: [
        'linear-gradient(135deg, #2563EB, #3B82F6)',
        'linear-gradient(135deg, #10B981, #34D399)',
        'linear-gradient(135deg, #F59E0B, #FBBF24)',
        'linear-gradient(135deg, #6366F1, #818CF8)',
        'linear-gradient(135deg, #EF4444, #F87171)',
        'linear-gradient(135deg, #8B5CF6, #A78BFA)'
      ],
      personRecords: [],
      newRecord: { recordType: '谈话记录', recordDate: null, content: '' },
      editRecordOpen: false,
      editRecordForm: {}
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
  created() { this.getList(); },
  methods: {
    getList() {
      this.loading = true;
      // 获取人员列表
      listPerson(this.queryParams).then(response => {
        this.personList = response.rows || [];
        this.total = response.total || 0;
        this.loading = false;
      });
      // 获取状态统计（所有人员，不限于当前页）
      getPersonStatusCount().then(response => {
        this.statusCounts = {
          active: response.active || 0,
          left: response.inactive || 0
        };
      });
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList(); },
    resetQuery() { this.queryParams = { pageNum: 1, pageSize: 10, keyword: undefined, education: undefined, status: undefined }; this.getList(); },
    changePage(delta) { this.queryParams.pageNum += delta; this.getList(); },
    goToPage(page) { this.queryParams.pageNum = page; this.getList(); },
    reset() { this.form = { id: undefined, name: undefined, position: undefined, level: undefined, contact: undefined, email: undefined, birthDate: undefined, workYears: undefined, companyYears: undefined, education: undefined, school: undefined, hometown: undefined, entryDate: undefined, status: "在职", isDirect: "1", remark: undefined }; this.resetForm("form"); },
    cancel() { this.open = false; this.reset(); },
    handleAdd() { this.reset(); this.open = true; this.title = "添加人员"; },
    handleUpdate(row) { this.reset(); getPerson(row.id).then(response => { this.form = response.data; this.open = true; this.title = "修改人员"; }); },
    handleDetail(row) {
      getPerson(row.id).then(response => {
        this.detailData = response.data;
        this.detailTab = 'projects';
        this.detailOpen = true;
        // 加载人员动态记录
        this.loadPersonRecords(row.id);
      });
    },
    loadPersonRecords(personId) {
      getPersonRecords(personId).then(response => {
        this.personRecords = response.data || [];
      });
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != undefined) {
            updatePerson(this.form).then(() => { this.$modal.msgSuccess("修改成功"); this.open = false; this.getList(); });
          } else {
            addPerson(this.form).then(() => { this.$modal.msgSuccess("新增成功"); this.open = false; this.getList(); });
          }
        }
      });
    },
    handleDelete(row) {
      this.$modal.confirm('是否确认删除该人员？').then(() => delPerson(row.id)).then(() => { this.getList(); this.$modal.msgSuccess("删除成功"); }).catch(() => {});
    },
    handleExport() { this.download('system/person/export', { ...this.queryParams }, `person_${new Date().getTime()}.xlsx`); },
    // 动态记录相关方法
    handleAddRecord() {
      if (!this.newRecord.content) {
        this.$modal.msgWarning('请输入记录内容');
        return;
      }
      if (!this.newRecord.recordDate) {
        this.newRecord.recordDate = new Date().toISOString().slice(0, 16).replace('T', ' ');
      }
      const data = {
        personId: this.detailData.id,
        personName: this.detailData.name,
        recordType: this.newRecord.recordType,
        recordDate: this.newRecord.recordDate,
        content: this.newRecord.content
      };
      addPersonRecord(data).then(() => {
        this.$modal.msgSuccess('添加成功');
        this.newRecord = { recordType: '谈话记录', recordDate: null, content: '' };
        this.loadPersonRecords(this.detailData.id);
      });
    },
    handleEditRecord(record) {
      this.editRecordForm = { ...record };
      this.editRecordOpen = true;
    },
    handleDeleteRecord(record) {
      this.$modal.confirm('确认删除该记录？').then(() => {
        return delPersonRecord(record.id);
      }).then(() => {
        this.$modal.msgSuccess('删除成功');
        this.loadPersonRecords(this.detailData.id);
      }).catch(() => {});
    },
    submitEditRecord() {
      updatePersonRecord(this.editRecordForm).then(() => {
        this.$modal.msgSuccess('修改成功');
        this.editRecordOpen = false;
        this.loadPersonRecords(this.detailData.id);
      });
    },
    recordTypeClass(type) {
      const map = { '谈话记录': 'talk', '行为观察': 'behavior', '想法记录': 'idea', '其他': 'other' };
      return map[type] || 'other';
    },
    formatRecordTime(dateStr) {
      if (!dateStr) return '-';
      return dateStr.slice(0, 16).replace('T', ' ');
    },
    goTo(path) { this.$router.push(path); },
    personStatusClass(status) { return status === '在职' ? 'active' : 'inactive'; },
    avatarStyle(person) {
      if (!person) return { background: '#D1D5DB' };
      // 如果有头像图片，不需要背景色
      if (person.avatar) return {};
      // 根据姓名生成渐变色背景
      const name = person.name || '';
      const idx = name.charCodeAt(0) % this.avatarColors.length;
      return { background: this.avatarColors[idx] };
    },
    visibleProjects(projects) {
      return projects ? projects.slice(0, 2) : [];
    }
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
.pp-filter-select { min-width: 100px; }
.pp-filter-input { width: 180px; }
.pp-filter-input-sm { width: 120px; }

.pp-stats-bar { display: flex; gap: 32px; padding: 12px 24px; background: #F9FAFB; border-bottom: 1px solid #E5E7EB; font-size: 13px; }
.pp-stat-item { display: flex; align-items: center; gap: 8px; }
.pp-stat-num { font-weight: 600; font-size: 16px; }

.pp-data-table { width: 100%; border-collapse: collapse; th, td { padding: 12px 16px; text-align: left; font-size: 13px; border-bottom: 1px solid #F3F4F6; } th { color: #6B7280; font-weight: 500; background: #F9FAFB; } tr:hover td { background: #F9FAFB; } tr.inactive { opacity: 0.6; } td.gray { color: #9CA3AF; } }

.pp-person-cell { display: flex; align-items: center; gap: 12px; }
.pp-avatar { width: 40px; height: 40px; border-radius: 50%; background: linear-gradient(135deg, #2563EB, #3B82F6); color: white; display: flex; align-items: center; justify-content: center; font-size: 14px; font-weight: 600; overflow: hidden;
  &.has-image { background: transparent; }
  .pp-avatar-img { width: 100%; height: 100%; object-fit: cover; }
}
.pp-person-info { display: flex; flex-direction: column; }
.pp-person-name { color: #1F2937; font-weight: 500; cursor: pointer; &:hover { color: #2563EB; } }
.pp-person-contact { font-size: 12px; color: #9CA3AF; }

.pp-level-tag { padding: 2px 8px; border-radius: 4px; font-size: 12px; font-weight: 600; background: #eff6ff; color: #2563EB; &.inactive { background: #F3F4F6; color: #6B7280; } }

.pp-project-tags { display: flex; flex-wrap: wrap; gap: 4px; }
.pp-project-tag { padding: 2px 8px; background: #eff6ff; color: #2563EB; border-radius: 4px; font-size: 11px; &.more { background: #F3F4F6; color: #6B7280; } }
.pp-no-projects { font-size: 12px; color: #9CA3AF; }

.pp-status-tag.active { background: #f0fdf4; color: #10B981; }
.pp-status-tag.inactive { background: #F3F4F6; color: #6B7280; }

.pp-direct-tag { padding: 2px 8px; border-radius: 4px; font-size: 12px; font-weight: 500;
  &.direct { background: #eff6ff; color: #2563EB; }
  &.indirect { background: #fef3c7; color: #B45309; }
}

.pp-action-btns { display: flex; gap: 8px; }
.pp-action-btn { padding: 4px 8px; font-size: 12px; border: 1px solid #E5E7EB; background: white; border-radius: 4px; cursor: pointer; color: #4B5563; &:hover { border-color: #2563EB; color: #2563EB; } }

.pp-pagination { display: flex; justify-content: space-between; align-items: center; padding: 16px 24px; background: #F9FAFB; border-top: 1px solid #E5E7EB; font-size: 13px; color: #6B7280; }
.pp-pagination-btns { display: flex; gap: 4px; }
.pp-page-btn { padding: 6px 12px; border: 1px solid #E5E7EB; background: white; border-radius: 4px; cursor: pointer; font-size: 13px; &.active { background: #2563EB; color: white; border-color: #2563EB; } &:hover:not(.active):not(:disabled) { border-color: #2563EB; color: #2563EB; } &:disabled { opacity: 0.5; cursor: not-allowed; } }

// 详情页样式
.pp-detail-header { padding: 24px; border-bottom: 1px solid #E5E7EB; }
.pp-detail-top { display: flex; align-items: flex-start; gap: 24px; }
.pp-avatar-large { width: 80px; height: 80px; border-radius: 50%; background: linear-gradient(135deg, #2563EB, #3B82F6); color: white; display: flex; align-items: center; justify-content: center; font-size: 28px; font-weight: 600; overflow: hidden;
  &.has-image { background: transparent; }
  .pp-avatar-img { width: 100%; height: 100%; object-fit: cover; }
}
.pp-detail-info-main { flex: 1; }
.pp-detail-name { font-size: 24px; font-weight: 600; color: #111827; display: flex; align-items: center; gap: 8px; }
.pp-detail-meta { display: flex; gap: 24px; margin-top: 8px; font-size: 14px; color: #6B7280; }

.pp-info-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-top: 20px; }
.pp-info-item { display: flex; flex-direction: column; gap: 4px; }
.pp-info-label { font-size: 12px; color: #6B7280; }
.pp-info-value { font-size: 14px; color: #1F2937; font-weight: 500; }

.pp-tab-nav { display: flex; border-bottom: 1px solid #E5E7EB; background: white; }
.pp-tab-item { padding: 14px 24px; font-size: 14px; font-weight: 500; color: #6B7280; cursor: pointer; border-bottom: 2px solid transparent; &:hover { color: #374151; } &.active { color: #2563EB; border-bottom-color: #2563EB; } }
.pp-tab-content { padding: 24px; }

.pp-section-title { font-size: 14px; font-weight: 600; margin-bottom: 12px; color: #374151; }

.pp-project-cards { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; }
.pp-project-card { border: 1px solid #E5E7EB; border-radius: 8px; padding: 16px; }
.pp-project-card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.pp-project-card-name { font-weight: 600; color: #1F2937; }
.pp-project-card-role { font-size: 12px; padding: 2px 8px; background: #eff6ff; color: #2563EB; border-radius: 4px; }
.pp-project-card-info { font-size: 13px; color: #6B7280; }
.pp-project-card-progress { margin-top: 12px; }
.pp-progress-label { font-size: 12px; color: #6B7280; margin-bottom: 6px; }
.pp-progress-bar { height: 8px; background: #F3F4F6; border-radius: 4px; overflow: hidden; }
.pp-progress-fill { height: 100%; border-radius: 4px; background: #2563EB; }

.pp-task-list { border: 1px solid #E5E7EB; border-radius: 8px; overflow: hidden; }
.pp-task-item { display: flex; justify-content: space-between; align-items: center; padding: 12px 16px; border-bottom: 1px solid #F3F4F6; &:last-child { border-bottom: none; } }
.pp-task-info { display: flex; align-items: center; gap: 12px; }
.pp-task-check { width: 18px; height: 18px; border: 2px solid #D1D5DB; border-radius: 4px; display: flex; align-items: center; justify-content: center; font-size: 12px; color: white; &.done { background: #10B981; border-color: #10B981; } }
.pp-task-name { font-size: 14px; color: #374151; &.done { text-decoration: line-through; color: #9CA3AF; } }
.pp-task-project { font-size: 12px; color: #9CA3AF; }
.pp-task-date { font-size: 12px; color: #9CA3AF; &.overdue { color: #EF4444; } }

// 动态记录样式
.pp-record-list { border: 1px solid #E5E7EB; border-radius: 8px; overflow: hidden; margin-bottom: 24px; }
.pp-record-item { padding: 16px; border-bottom: 1px solid #F3F4F6; &:last-child { border-bottom: none; } }
.pp-record-header { display: flex; align-items: center; gap: 12px; margin-bottom: 8px; }
.pp-record-type { padding: 2px 8px; border-radius: 4px; font-size: 12px; font-weight: 500;
  &.talk { background: #eff6ff; color: #2563EB; }
  &.behavior { background: #fef3c7; color: #B45309; }
  &.idea { background: #f0fdf4; color: #10B981; }
  &.other { background: #F3F4F6; color: #6B7280; }
}
.pp-record-time { font-size: 12px; color: #9CA3AF; }
.pp-record-user { font-size: 12px; color: #6B7280; }
.pp-record-content { font-size: 14px; color: #374151; line-height: 1.6; }
.pp-record-actions { display: flex; gap: 8px; margin-top: 8px; }
.pp-record-btn { padding: 4px 8px; font-size: 12px; border: 1px solid #E5E7EB; background: white; border-radius: 4px; cursor: pointer; color: #4B5563;
  &:hover { border-color: #2563EB; color: #2563EB; }
  &.delete:hover { border-color: #EF4444; color: #EF4444; }
}

.pp-add-record { background: #F9FAFB; border-radius: 8px; padding: 16px; }
.pp-add-record-header { font-size: 14px; font-weight: 600; color: #374151; margin-bottom: 12px; }

.pp-empty-section { text-align: center; padding: 20px; color: #9CA3AF; font-size: 14px; }
.pp-empty-tip { text-align: center; padding: 30px; color: #909399; font-size: 14px; }
.pp-dialog-footer { display: flex; justify-content: flex-end; gap: 8px; }
</style>