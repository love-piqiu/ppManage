<template>
  <div class="pp-page-container">
    <!-- 顶部导航 -->
    <div class="pp-top-nav">
      <div class="pp-breadcrumb">
        <a @click="goTo('/dashboard')">首页</a>
        <span>/</span>
        <span class="current">资源管理</span>
      </div>
      <div class="pp-page-actions">
        <button class="pp-btn pp-btn-secondary" @click="handleExportSkill">导出技能</button>
        <button class="pp-btn pp-btn-secondary" @click="handleExportProject">导出项目</button>
      </div>
    </div>

    <!-- 统计概览 -->
    <div class="pp-overview-bar">
      <div class="pp-overview-item">
        <div class="pp-overview-label">总人数</div>
        <div class="pp-overview-value">{{ overview.total || 0 }}</div>
      </div>
      <div class="pp-overview-item free">
        <div class="pp-overview-label">空闲人员</div>
        <div class="pp-overview-value">{{ overview.freeCount || 0 }}</div>
      </div>
      <div class="pp-overview-item soon">
        <div class="pp-overview-label">即将空闲</div>
        <div class="pp-overview-value">{{ overview.soonFreeCount || 0 }}</div>
      </div>
      <div class="pp-overview-chart">
        <div class="pp-overview-label">技能分布</div>
        <div class="pp-category-tags">
          <span class="pp-category-tag" v-for="(count, cat) in overview.categoryDistribution" :key="cat">
            {{ cat }}: {{ count }}
          </span>
        </div>
      </div>
    </div>

    <!-- 篮选区域 -->
    <div class="pp-filter-bar">
      <div class="pp-filter-group">
        <span class="pp-filter-label">技能大类</span>
        <el-select v-model="queryParams.category" placeholder="全部" clearable size="small" class="pp-filter-select" @change="handleCategoryChange">
          <el-option v-for="cat in skillCategories" :key="cat.id" :label="cat.category" :value="cat.category" />
        </el-select>
      </div>
      <div class="pp-filter-group">
        <span class="pp-filter-label">具体技能</span>
        <el-select v-model="queryParams.skill" placeholder="全部" clearable size="small" class="pp-filter-select" @change="handleQuery">
          <el-option v-for="skill in currentSkillList" :key="skill" :label="skill" :value="skill" />
        </el-select>
      </div>
      <div class="pp-filter-group">
        <span class="pp-filter-label">资源状态</span>
        <el-select v-model="queryParams.resourceStatus" placeholder="全部" clearable size="small" class="pp-filter-select" @change="handleQuery">
          <el-option label="空闲" value="空闲" />
          <el-option label="临近空闲" value="临近空闲" />
          <el-option label="即将空闲" value="即将空闲" />
          <el-option label="忙碌" value="忙碌" />
          <el-option label="未设置" value="未设置" />
        </el-select>
      </div>
      <div class="pp-filter-group">
        <el-input v-model="queryParams.projectName" placeholder="搜索项目名称..." clearable size="small" class="pp-filter-input" @keyup.enter.native="handleQuery" />
      </div>
      <div class="pp-filter-group">
        <button class="pp-btn pp-btn-secondary" @click="handleQuery">筛选</button>
        <button class="pp-btn pp-btn-secondary" @click="resetQuery">重置</button>
      </div>
    </div>

    <!-- 人员卡片网格 -->
    <div class="pp-card-grid" v-loading="loading">
      <div class="pp-person-card" v-for="person in resourceList" :key="person.id" @click="handleDetail(person)">
        <div class="pp-card-header">
          <div class="pp-avatar" :style="avatarStyle(person.name)">{{ (person.name || '').charAt(0) }}</div>
          <div class="pp-card-info">
            <div class="pp-card-name">
              {{ person.name }}
              <span class="pp-indirect-tag" v-if="person.isDirect === '0'">非直属</span>
            </div>
            <div class="pp-card-meta">{{ person.position }} | {{ person.level }}</div>
          </div>
        </div>
        <!-- 项目数 -->
        <div class="pp-card-project-count">
          <span class="pp-project-label">当前项目数</span>
          <span class="pp-project-num" :class="projectCountClass(person.projectCount)">{{ person.projectCount }}</span>
          <span class="pp-status-tag" :class="statusClass(person.resourceStatus)">{{ person.resourceStatus || '未设置' }}</span>
        </div>
        <!-- 技能标签 -->
        <div class="pp-card-skills">
          <span class="pp-skill-label">技能</span>
          <div class="pp-skill-tags">
            <span class="pp-skill-tag" v-for="skill in person.skills" :key="skill.category + skill.skill">
              {{ skill.category }}/{{ skill.skill }}
            </span>
            <span class="pp-skill-tag empty" v-if="!person.skills || person.skills.length === 0">暂无技能记录</span>
          </div>
        </div>
        <!-- 操作 -->
        <div class="pp-card-actions">
          <button class="pp-card-btn" @click.stop="handleAddSkill(person)">+ 添加技能</button>
        </div>
      </div>

      <!-- 空状态 -->
      <div class="pp-empty-state" v-if="resourceList.length === 0 && !loading">
        <div class="pp-empty-icon">?</div>
        <div class="pp-empty-text">暂无符合条件的资源</div>
      </div>
    </div>

    <!-- 人员详情对话框 -->
    <el-dialog title="人员详情" :visible.sync="detailOpen" width="600px" append-to-body custom-class="pp-dialog">
      <div class="pp-detail-card">
        <div class="pp-detail-header">
          <div class="pp-avatar-large" :style="avatarStyle(detailData.name)">{{ (detailData.name || '').charAt(0) }}</div>
          <div class="pp-detail-info">
            <div class="pp-detail-name">
              {{ detailData.name }}
              <span class="pp-indirect-tag" v-if="detailData.isDirect === '0'">非直属</span>
            </div>
            <div class="pp-detail-meta">{{ detailData.position }} | {{ detailData.level }}</div>
          </div>
        </div>
        <div class="pp-detail-stats">
          <div class="pp-stat-row">
            <span class="pp-stat-label">当前项目数</span>
            <span class="pp-stat-value" :class="projectCountClass(detailData.projectCount)">{{ detailData.projectCount }}</span>
          </div>
          <div class="pp-stat-row">
            <span class="pp-stat-label">预计释放日期</span>
            <span class="pp-stat-value">{{ detailData.expectedReleaseDate || '未设置' }}</span>
          </div>
          <div class="pp-stat-row">
            <span class="pp-stat-label">资源状态</span>
            <span class="pp-status-tag" :class="statusClass(detailData.resourceStatus)">{{ detailData.resourceStatus || '未设置' }}</span>
          </div>
          <div class="pp-stat-row" v-if="detailData.resourceStatusRemark">
            <span class="pp-stat-label">状态说明</span>
            <span class="pp-stat-value remark">{{ detailData.resourceStatusRemark }}</span>
          </div>
          <div class="pp-stat-row" v-if="detailData.nextProject">
            <span class="pp-stat-label">下一个项目</span>
            <span class="pp-stat-value remark">{{ detailData.nextProject }}</span>
          </div>
        </div>
        <div class="pp-detail-actions">
          <button class="pp-btn pp-btn-primary" @click="handleSetStatus(detailData)">设置日期</button>
        </div>
        <div class="pp-detail-section">
          <h4>技能列表</h4>
          <div class="pp-skill-list" v-if="detailSkills.length > 0">
            <div class="pp-skill-item" v-for="skill in detailSkills" :key="skill.id">
              <span class="pp-skill-category">{{ skill.category }}</span>
              <span class="pp-skill-name">{{ skill.skill }}</span>
              <button class="pp-skill-delete" @click="handleDeleteSkill(skill)">删除</button>
            </div>
          </div>
          <div class="pp-empty-tip" v-else>暂无技能记录</div>
        </div>
        <div class="pp-detail-section">
          <h4>当前参与项目</h4>
          <div class="pp-project-list" v-if="detailData.projects && detailData.projects.length > 0">
            <div class="pp-project-item" v-for="(proj, idx) in detailData.projects" :key="idx">{{ proj }}</div>
          </div>
          <div class="pp-empty-tip" v-else>暂无参与项目</div>
        </div>
      </div>
      <div slot="footer" class="pp-dialog-footer">
        <button class="pp-btn pp-btn-secondary" @click="detailOpen = false">关闭</button>
      </div>
    </el-dialog>

    <!-- 添加技能对话框 -->
    <el-dialog title="添加技能" :visible.sync="skillOpen" width="400px" append-to-body custom-class="pp-dialog">
      <el-form ref="skillForm" :model="skillForm" label-width="80px">
        <el-form-item label="技能大类">
          <el-select v-model="skillForm.category" placeholder="请选择" @change="handleSkillCategoryChange">
            <el-option v-for="cat in skillCategories" :key="cat.id" :label="cat.category" :value="cat.category" />
          </el-select>
        </el-form-item>
        <el-form-item label="具体技能">
          <el-select v-model="skillForm.skills" placeholder="请选择" multiple collapse-tags>
            <el-option v-for="skill in addSkillList" :key="skill" :label="skill" :value="skill" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="pp-dialog-footer">
        <button class="pp-btn pp-btn-secondary" @click="skillOpen = false">取消</button>
        <button class="pp-btn pp-btn-primary" @click="submitSkill">确定</button>
      </div>
    </el-dialog>

    <!-- 设置状态对话框 -->
    <el-dialog title="设置预计释放日期" :visible.sync="statusOpen" width="450px" append-to-body custom-class="pp-dialog">
      <el-form ref="statusForm" :model="statusForm" label-width="120px">
        <el-form-item label="预计释放日期">
          <el-date-picker v-model="statusForm.expectedReleaseDate" type="date" value-format="yyyy-MM-dd" placeholder="选择预计释放日期" style="width: 100%" />
          <div class="pp-form-tip">状态将根据日期自动计算：超期=空闲，&lt;15天=即将空闲，15-30天=临近空闲，&gt;30天=忙碌</div>
        </el-form-item>
        <el-form-item label="状态说明">
          <el-input v-model="statusForm.resourceStatusRemark" type="textarea" :rows="3" placeholder="请输入状态说明（如：预计X月可安排新项目）" />
        </el-form-item>
        <el-form-item label="下一个项目">
          <el-input v-model="statusForm.nextProject" placeholder="请输入下一个预计参与的项目名称" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="pp-dialog-footer">
        <button class="pp-btn pp-btn-secondary" @click="statusOpen = false">取消</button>
        <button class="pp-btn pp-btn-primary" @click="submitStatus">确定</button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listResource, getSkillCategories, getPersonSkills, addPersonSkill, delPersonSkill, getResourceOverview, updateResourceStatus } from "@/api/system/resource";

export default {
  name: "Resource",
  data() {
    return {
      loading: false,
      resourceList: [],
      skillCategories: [],
      currentSkillList: [],
      addSkillList: [],
      queryParams: { category: undefined, skill: undefined, resourceStatus: undefined, projectName: undefined },
      overview: { total: 0, freeCount: 0, soonFreeCount: 0, categoryDistribution: {} },
      detailOpen: false,
      detailData: {},
      detailSkills: [],
      skillOpen: false,
      skillForm: { personId: undefined, category: undefined, skills: [] },
      statusOpen: false,
      statusForm: { personId: undefined, expectedReleaseDate: undefined, resourceStatusRemark: undefined, nextProject: undefined },
      avatarColors: [
        'linear-gradient(135deg, #2563EB, #3B82F6)',
        'linear-gradient(135deg, #10B981, #34D399)',
        'linear-gradient(135deg, #F59E0B, #FBBF24)',
        'linear-gradient(135deg, #6366F1, #818CF8)',
        'linear-gradient(135deg, #EF4444, #F87171)',
        'linear-gradient(135deg, #8B5CF6, #A78BFA)'
      ]
    };
  },
  created() {
    this.getList();
    this.loadSkillCategories();
    this.loadOverview();
  },
  methods: {
    getList() {
      this.loading = true;
      listResource(this.queryParams).then(response => {
        this.resourceList = response.data || [];
        this.loading = false;
      });
    },
    loadSkillCategories() {
      getSkillCategories().then(response => {
        this.skillCategories = response.data || [];
      });
    },
    loadOverview() {
      getResourceOverview().then(response => {
        this.overview = response.data || { total: 0, freeCount: 0, categoryDistribution: {} };
      });
    },
    handleCategoryChange(val) {
      this.queryParams.skill = undefined;
      if (val) {
        const cat = this.skillCategories.find(c => c.category === val);
        this.currentSkillList = cat ? (cat.skillList || []) : [];
      } else {
        this.currentSkillList = [];
      }
      this.handleQuery();
    },
    handleSkillCategoryChange(val) {
      this.skillForm.skills = [];
      if (val) {
        const cat = this.skillCategories.find(c => c.category === val);
        this.addSkillList = cat ? (cat.skillList || []) : [];
      } else {
        this.addSkillList = [];
      }
    },
    handleQuery() {
      this.getList();
    },
    resetQuery() {
      this.queryParams = { category: undefined, skill: undefined, resourceStatus: undefined, projectName: undefined };
      this.currentSkillList = [];
      this.getList();
    },
    handleDetail(person) {
      this.detailData = person;
      this.detailOpen = true;
      getPersonSkills(person.id).then(response => {
        this.detailSkills = response.data || [];
      });
    },
    handleAddSkill(person) {
      this.skillForm = { personId: person.id, category: undefined, skills: [] };
      this.addSkillList = [];
      this.skillOpen = true;
    },
    handleSetStatus(person) {
      this.statusForm = {
        personId: person.id,
        expectedReleaseDate: person.expectedReleaseDate || undefined,
        resourceStatusRemark: person.resourceStatusRemark || undefined,
        nextProject: person.nextProject || undefined
      };
      this.statusOpen = true;
    },
    submitStatus() {
      updateResourceStatus(this.statusForm.personId, this.statusForm).then(() => {
        this.$modal.msgSuccess('设置成功');
        this.statusOpen = false;
        this.getList();
        this.loadOverview();
        if (this.detailOpen) {
          // 更新详情数据
          this.detailData.expectedReleaseDate = this.statusForm.expectedReleaseDate;
          this.detailData.resourceStatusRemark = this.statusForm.resourceStatusRemark;
          this.detailData.nextProject = this.statusForm.nextProject;
          // 重新获取列表以更新状态
          this.getList();
        }
      });
    },
    submitSkill() {
      if (!this.skillForm.category || !this.skillForm.skills || this.skillForm.skills.length === 0) {
        this.$modal.msgWarning('请选择技能大类和具体技能');
        return;
      }
      // 批量添加技能
      const promises = this.skillForm.skills.map(skill => {
        return addPersonSkill({
          personId: this.skillForm.personId,
          category: this.skillForm.category,
          skill: skill
        });
      });
      Promise.all(promises).then(() => {
        this.$modal.msgSuccess('添加成功');
        this.skillOpen = false;
        this.getList();
        this.loadOverview();
        if (this.detailOpen) {
          getPersonSkills(this.detailData.id).then(response => {
            this.detailSkills = response.data || [];
          });
        }
      });
    },
    handleDeleteSkill(skill) {
      this.$modal.confirm('确认删除该技能？').then(() => {
        return delPersonSkill(skill.id);
      }).then(() => {
        this.$modal.msgSuccess('删除成功');
        getPersonSkills(this.detailData.id).then(response => {
          this.detailSkills = response.data || [];
        });
        this.getList();
        this.loadOverview();
      }).catch(() => {});
    },
    handleExportSkill() {
      this.download('system/resource/exportSkill', {}, `skill_${new Date().getTime()}.xlsx`);
    },
    handleExportProject() {
      this.download('system/resource/exportProject', {}, `project_participation_${new Date().getTime()}.xlsx`);
    },
    goTo(path) {
      this.$router.push(path);
    },
    avatarStyle(name) {
      if (!name) return { background: '#D1D5DB' };
      const idx = name.charCodeAt(0) % this.avatarColors.length;
      return { background: this.avatarColors[idx] };
    },
    projectCountClass(count) {
      if (count >= 3) return 'busy';
      if (count >= 2) return 'medium';
      return 'free';
    },
    statusClass(status) {
      if (status === '空闲') return 'free';
      if (status === '临近空闲') return 'near';
      if (status === '即将空闲') return 'soon';
      if (status === '忙碌') return 'busy';
      return 'pending';
    }
  }
};
</script>

<style lang="scss" scoped>
@import "@/assets/styles/ppmanage.scss";

.pp-page-container {
  background: white;
  border-radius: 12px;
  border: 1px solid #E5E7EB;
  overflow: hidden;
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

  a {
    color: #6B7280;
    cursor: pointer;

    &:hover {
      color: #2563EB;
    }
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

.pp-overview-bar {
  display: flex;
  gap: 24px;
  padding: 16px 24px;
  background: linear-gradient(135deg, #F0F9FF, #E0F2FE);
  border-bottom: 1px solid #E5E7EB;
}

.pp-overview-item {
  display: flex;
  flex-direction: column;
  gap: 4px;

  &.free {
    .pp-overview-value {
      color: #10B981;
    }
  }

  &.soon {
    .pp-overview-value {
      color: #F59E0B;
    }
  }
}

.pp-overview-label {
  font-size: 12px;
  color: #6B7280;
}

.pp-overview-value {
  font-size: 24px;
  font-weight: 600;
  color: #2563EB;
}

.pp-overview-chart {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.pp-category-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.pp-category-tag {
  padding: 4px 12px;
  background: white;
  border: 1px solid #E5E7EB;
  border-radius: 4px;
  font-size: 12px;
  color: #374151;
}

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
  min-width: 120px;
}

.pp-filter-input {
  width: 180px;
}

.pp-card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
  padding: 24px;
}

.pp-person-card {
  border: 1px solid #E5E7EB;
  border-radius: 12px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: #2563EB;
    box-shadow: 0 4px 12px rgba(37, 99, 235, 0.1);
  }
}

.pp-card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.pp-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #2563EB, #3B82F6);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 600;
}

.pp-card-info {
  flex: 1;
}

.pp-card-name {
  font-size: 16px;
  font-weight: 600;
  color: #1F2937;
}

.pp-indirect-tag {
  font-size: 10px;
  font-weight: 500;
  color: #9CA3AF;
  background: #F3F4F6;
  padding: 2px 6px;
  border-radius: 4px;
  margin-left: 6px;
  vertical-align: middle;
}

.pp-card-meta {
  font-size: 12px;
  color: #6B7280;
}

.pp-card-project-count {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.pp-project-label {
  font-size: 12px;
  color: #6B7280;
}

.pp-project-num {
  font-size: 18px;
  font-weight: 600;

  &.busy {
    color: #EF4444;
  }

  &.medium {
    color: #F59E0B;
  }

  &.free {
    color: #10B981;
  }
}

.pp-free-tag {
  padding: 2px 8px;
  background: #f0fdf4;
  color: #10B981;
  border-radius: 4px;
  font-size: 11px;
}

.pp-status-tag {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;

  &.free {
    background: #f0fdf4;
    color: #10B981;
  }

  &.near {
    background: #eff6ff;
    color: #2563EB;
  }

  &.soon {
    background: #fffbeb;
    color: #F59E0B;
  }

  &.busy {
    background: #fef2f2;
    color: #EF4444;
  }

  &.pending {
    background: #f3f4f6;
    color: #6B7280;
  }
}

.pp-form-tip {
  font-size: 11px;
  color: #6B7280;
  margin-top: 4px;
  line-height: 1.4;
}

.pp-card-skills {
  margin-bottom: 12px;
}

.pp-skill-label {
  font-size: 12px;
  color: #6B7280;
  display: block;
  margin-bottom: 6px;
}

.pp-skill-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.pp-skill-tag {
  padding: 2px 8px;
  background: #eff6ff;
  color: #2563EB;
  border-radius: 4px;
  font-size: 11px;

  &.empty {
    background: #F3F4F6;
    color: #9CA3AF;
  }
}

.pp-card-actions {
  display: flex;
  gap: 8px;
}

.pp-card-btn {
  padding: 6px 12px;
  font-size: 12px;
  border: 1px solid #E5E7EB;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  color: #4B5563;

  &:hover {
    border-color: #2563EB;
    color: #2563EB;
  }
}

.pp-empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 48px;
}

.pp-empty-icon {
  font-size: 48px;
  color: #D1D5DB;
}

.pp-empty-text {
  font-size: 14px;
  color: #6B7280;
  margin-top: 12px;
}

// 详情对话框样式
.pp-detail-card {
  padding: 24px;
}

.pp-detail-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.pp-avatar-large {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: linear-gradient(135deg, #2563EB, #3B82F6);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 600;
}

.pp-detail-info {
  flex: 1;
}

.pp-detail-name {
  font-size: 20px;
  font-weight: 600;
  color: #1F2937;
}

.pp-detail-meta {
  font-size: 14px;
  color: #6B7280;
}

.pp-detail-stats {
  padding: 12px;
  background: #F9FAFB;
  border-radius: 8px;
  margin-bottom: 16px;
}

.pp-stat-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.pp-stat-label {
  font-size: 14px;
  color: #6B7280;
}

.pp-stat-value {
  font-size: 18px;
  font-weight: 600;

  &.busy {
    color: #EF4444;
  }

  &.medium {
    color: #F59E0B;
  }

  &.free {
    color: #10B981;
  }

  &.remark {
    font-size: 14px;
    font-weight: normal;
    color: #374151;
  }
}

.pp-detail-actions {
  margin-bottom: 16px;
}

.pp-btn-primary {
  padding: 8px 16px;
  font-size: 14px;
  background: #2563EB;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;

  &:hover {
    background: #1D4ED8;
  }
}

.pp-btn-secondary {
  padding: 8px 16px;
  font-size: 14px;
  background: white;
  color: #374151;
  border: 1px solid #D1D5DB;
  border-radius: 6px;
  cursor: pointer;

  &:hover {
    background: #F3F4F6;
  }
}

.pp-detail-section {
  margin-bottom: 16px;

  h4 {
    font-size: 14px;
    font-weight: 600;
    color: #374151;
    margin-bottom: 12px;
  }
}

.pp-skill-list {
  border: 1px solid #E5E7EB;
  border-radius: 8px;
}

.pp-skill-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-bottom: 1px solid #F3F4F6;

  &:last-child {
    border-bottom: none;
  }
}

.pp-skill-category {
  padding: 2px 8px;
  background: #eff6ff;
  color: #2563EB;
  border-radius: 4px;
  font-size: 12px;
}

.pp-skill-name {
  flex: 1;
  font-size: 14px;
  color: #374151;
}

.pp-skill-delete {
  padding: 4px 8px;
  font-size: 12px;
  border: 1px solid #EF4444;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  color: #EF4444;

  &:hover {
    background: #FEE2E2;
  }
}

.pp-project-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.pp-project-item {
  padding: 6px 12px;
  background: #F3F4F6;
  border-radius: 6px;
  font-size: 13px;
  color: #374151;
}

.pp-empty-tip {
  text-align: center;
  padding: 12px;
  color: #9CA3AF;
  font-size: 14px;
}

.pp-dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>