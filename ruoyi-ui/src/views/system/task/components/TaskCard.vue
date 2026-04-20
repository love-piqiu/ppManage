<template>
  <div class="pp-task-card" :class="{ expanded: isExpanded }">
    <div class="pp-task-card-header" @click="toggleExpand">
      <div class="pp-expand-icon">▶</div>
      <div class="pp-task-info">
        <span class="pp-task-name">{{ task.name }}</span>
        <span class="pp-type-tag" :class="cycleClass">{{ task.cycle }}</span>
        <span class="pp-task-deadline">截止：{{ deadlineText }}</span>
      </div>
      <div class="pp-completion-summary">
        <div class="pp-completion-numbers">
          <div class="pp-completion-count">
            <span class="completed">{{ completionStats.completed }}</span>
            <span class="total"> / {{ completionStats.total }}</span>
          </div>
          <div class="pp-completion-label">已完成</div>
        </div>
        <div class="pp-completion-progress">
          <div class="pp-progress-fill" :class="progressClass" :style="{ width: completionStats.rate + '%' }"></div>
        </div>
        <div class="pp-completion-rate" :style="{ color: rateColor }">{{ completionStats.rate }}%</div>
      </div>
      <div class="pp-task-actions" @click.stop>
        <button class="pp-action-btn edit" @click="handleEdit">编辑</button>
        <button class="pp-action-btn delete" @click="handleDelete">删除</button>
      </div>
    </div>
    <div class="pp-task-expand-content" v-if="isExpanded" @click.stop>
      <div class="pp-expand-header">
        <div class="pp-expand-title">{{ expandTitle }}</div>
        <div class="pp-expand-actions">
          <button class="pp-btn-complete-all" @click="handleCompleteAll" v-if="hasUncompleted">
            全部完成
          </button>
          <button class="pp-btn-uncomplete-all" @click="handleUncompleteAll" v-if="hasCompleted">
            取消全部
          </button>
        </div>
      </div>
      <!-- 每日/每工作日任务：显示日期矩阵 -->
      <daily-matrix
        v-if="task.cycle === '每日' || task.cycle === '每工作日'"
        :task="task"
        :persons="persons"
        :instances="instances"
        :period="period"
        :workdays-only="task.cycle === '每工作日'"
        @complete="handleComplete"
        @createAndComplete="handleCreateAndComplete"
        @uncomplete="handleUncomplete"
        @completeDay="handleCompleteDay"
        @uncompleteDay="handleUncompleteDay"
      />
      <!-- 每周/每月任务：显示人员网格 -->
      <person-status-grid
        v-else
        :persons="persons"
        :instances="instances"
        @complete="handleComplete"
        @createAndComplete="handleCreateAndComplete"
        @uncomplete="handleUncomplete"
      />
    </div>
  </div>
</template>

<script>
import PersonStatusGrid from './PersonStatusGrid.vue'
import DailyMatrix from './DailyMatrix.vue'

export default {
  name: 'TaskCard',
  components: { PersonStatusGrid, DailyMatrix },
  props: {
    task: {
      type: Object,
      required: true
    },
    instances: {
      type: Array,
      default: () => []
    },
    persons: {
      type: Array,
      default: () => []
    },
    period: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      isExpanded: false
    }
  },
  computed: {
    cycleClass() {
      const map = {
        '每日': 'daily',
        '每工作日': 'workday',
        '每周': 'weekly',
        '每月': 'monthly'
      }
      return map[this.task.cycle] || 'daily'
    },
    deadlineText() {
      // 一次性任务显示截止日期
      if (this.task.type === '一次性') {
        return this.task.deadlineDate || '-'
      }
      // 周期性任务
      if (this.task.cycle === '每日' || this.task.cycle === '每工作日') {
        return this.task.deadlineTime || '18:00'
      } else if (this.task.cycle === '每周') {
        return `周${this.task.deadlineWeekday || '五'} ${this.task.deadlineTime || '17:00'}`
      } else if (this.task.cycle === '每月') {
        return `${this.task.deadlineDay || 5}日 ${this.task.deadlineTime || '12:00'}`
      }
      return this.task.deadlineTime || '-'
    },
    expandTitle() {
      if (this.task.cycle === '每日') {
        return '本周每日完成情况（点击单元格可更新状态）'
      }
      if (this.task.cycle === '每工作日') {
        return '本周工作日完成情况（点击单元格可更新状态）'
      }
      return '人员完成情况（点击可更新状态）'
    },
    completionStats() {
      // 计算已完成和总数
      // 对于一次性任务，如果没有 instance，总数应该是人员数量
      let total = this.instances.length
      const completed = this.instances.filter(i => i.completed === 1).length

      // 如果 instance 数量少于人员数量，说明有些人员还没有创建 instance
      // 此时总数应该是人员数量（每个人员都应该完成这个任务）
      if (this.persons.length > total) {
        total = this.persons.length
      }

      // 对于每日任务，total 是人员数 * 7天；每工作日是人员数 * 5天
      if (this.task.cycle === '每日') {
        total = this.persons.length * 7
        // 计算每日任务的已完成数
        const dailyCompleted = this.instances.filter(i => i.completed === 1).length
        const rate = total > 0 ? Math.round(dailyCompleted * 100 / total) : 0
        return { total, completed: dailyCompleted, rate }
      }
      if (this.task.cycle === '每工作日') {
        total = this.persons.length * 5
        // 计算每工作日任务的已完成数
        const workdayCompleted = this.instances.filter(i => i.completed === 1).length
        const rate = total > 0 ? Math.round(workdayCompleted * 100 / total) : 0
        return { total, completed: workdayCompleted, rate }
      }

      const rate = total > 0 ? Math.round(completed * 100 / total) : 0
      return { total, completed, rate }
    },
    progressClass() {
      const rate = this.completionStats.rate
      return rate >= 80 ? 'high' : rate >= 50 ? 'medium' : 'low'
    },
    rateColor() {
      const rate = this.completionStats.rate
      return rate >= 80 ? '#10B981' : rate >= 50 ? '#F59E0B' : '#EF4444'
    },
    hasUncompleted() {
      // 检查是否有未完成的人员
      // 1. 有 instance 但未完成
      // 2. 有人员但没有对应的 instance
      const completedPersonIds = this.instances.filter(i => i.completed === 1).map(i => i.personId)
      const pendingInstancePersonIds = this.instances.filter(i => i.completed === 0).map(i => i.personId)
      const noInstancePersons = this.persons.filter(p => !this.instances.find(i => i.personId === p.id))
      return pendingInstancePersonIds.length > 0 || noInstancePersons.length > 0
    },
    hasCompleted() {
      // 检查是否有已完成的人员
      return this.instances.filter(i => i.completed === 1).length > 0
    }
  },
  methods: {
    toggleExpand() {
      this.isExpanded = !this.isExpanded
    },
    handleComplete(instance) {
      this.$emit('complete', instance)
    },
    handleUncomplete(instance) {
      this.$emit('uncomplete', instance)
    },
    handleCreateAndComplete(data) {
      // 转发事件，包含任务信息和日期
      this.$emit('createAndComplete', {
        taskId: this.task.id,
        taskName: this.task.name,
        personId: data.personId,
        personName: data.personName,
        date: data.date // 每日任务需要日期
      })
    },
    handleCompleteAll() {
      // 发出全部完成事件，传递任务信息和人员列表
      this.$emit('completeAll', {
        taskId: this.task.id,
        taskName: this.task.name,
        cycle: this.task.cycle,
        persons: this.persons,
        instances: this.instances
      })
    },
    handleUncompleteAll() {
      // 发出取消全部完成事件
      this.$emit('uncompleteAll', {
        taskId: this.task.id,
        instances: this.instances
      })
    },
    handleEdit() {
      this.$emit('edit', this.task)
    },
    handleDelete() {
      this.$emit('delete', this.task)
    },
    handleCompleteDay(data) {
      // 转发事件，包含任务信息和日期
      this.$emit('completeDay', {
        taskId: this.task.id,
        taskName: this.task.name,
        ...data
      })
    },
    handleUncompleteDay(data) {
      this.$emit('uncompleteDay', {
        taskId: this.task.id,
        ...data
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.pp-task-card {
  border: 1px solid #E5E7EB;
  border-radius: 8px;
  margin-bottom: 12px;
  overflow: hidden;

  &.expanded {
    .pp-task-card-header {
      background: #EFF6FF;
    }
    .pp-expand-icon {
      transform: rotate(90deg);
    }
  }
}

.pp-task-card-header {
  display: flex;
  align-items: center;
  padding: 16px;
  cursor: pointer;
  background: white;
  transition: background 0.15s;

  &:hover {
    background: #F9FAFB;
  }
}

.pp-expand-icon {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  color: #9CA3AF;
  transition: transform 0.2s;
}

.pp-task-info {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 16px;
}

.pp-task-name {
  font-weight: 600;
  color: #1F2937;
  min-width: 180px;
}

.pp-type-tag {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 500;

  &.daily {
    background: #DBEAFE;
    color: #1D4ED8;
  }
  &.workday {
    background: #E0E7FF;
    color: #4338CA;
  }
  &.weekly {
    background: #FEF3C7;
    color: #B45309;
  }
  &.monthly {
    background: #E0E7FF;
    color: #4338CA;
  }
}

.pp-task-deadline {
  font-size: 13px;
  color: #6B7280;
}

.pp-completion-summary {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-left: auto;
}

.pp-completion-numbers {
  text-align: right;
}

.pp-completion-count {
  font-size: 16px;
  font-weight: 600;

  .completed {
    color: #10B981;
  }
  .total {
    color: #6B7280;
  }
}

.pp-completion-label {
  font-size: 11px;
  color: #9CA3AF;
}

.pp-completion-progress {
  width: 120px;
  height: 8px;
  background: #F3F4F6;
  border-radius: 4px;
  overflow: hidden;
}

.pp-progress-fill {
  height: 100%;
  border-radius: 4px;

  &.high {
    background: #10B981;
  }
  &.medium {
    background: #F59E0B;
  }
  &.low {
    background: #EF4444;
  }
}

.pp-completion-rate {
  font-size: 14px;
  font-weight: 600;
  min-width: 50px;
  text-align: right;
}

.pp-task-expand-content {
  border-top: 1px solid #F3F4F6;
  background: #F9FAFB;
  padding: 16px;
}

.pp-expand-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.pp-expand-title {
  font-size: 13px;
  font-weight: 600;
  color: #374151;
}

.pp-btn-complete-all {
  padding: 6px 12px;
  font-size: 12px;
  font-weight: 500;
  color: white;
  background: #10B981;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.15s;

  &:hover {
    background: #059669;
  }
}

.pp-expand-actions {
  display: flex;
  gap: 8px;
}

.pp-btn-uncomplete-all {
  padding: 6px 12px;
  font-size: 12px;
  font-weight: 500;
  color: white;
  background: #F59E0B;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.15s;

  &:hover {
    background: #D97706;
  }
}

.pp-task-actions {
  display: flex;
  gap: 4px;
  margin-left: 16px;
}

.pp-action-btn {
  padding: 4px 8px;
  font-size: 12px;
  border: 1px solid #E5E7EB;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  color: #4B5563;

  &:hover {
    border-color: #2563EB;
    color: #2563EB;
  }

  &.delete:hover {
    border-color: #EF4444;
    color: #EF4444;
  }
}
</style>