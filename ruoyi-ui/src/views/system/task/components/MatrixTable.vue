<template>
  <div class="pp-matrix-container">
    <div class="pp-matrix-tip">💡 表格可横向滚动查看所有人员</div>
    <table class="pp-matrix-table">
      <thead>
        <tr>
          <th class="pp-task-header">任务名称</th>
          <th class="pp-person-header" v-for="person in persons" :key="person.id">{{ person.name }}</th>
          <th class="pp-rate-header">完成率</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="task in tasks" :key="task.id">
          <td class="pp-task-name-cell">
            <span class="pp-task-cell-name">{{ task.name }}</span>
            <span class="pp-type-tag-sm" :class="getCycleClass(task)">{{ task.cycle }}</span>
          </td>
          <td
            class="pp-matrix-cell"
            :class="getCellClass(task, person)"
            v-for="person in persons"
            :key="person.id"
            @click="handleCellClick(task, person)"
          >
            {{ getCellIcon(task, person) }}
          </td>
          <td class="pp-rate-cell" :style="{ color: getTaskRateColor(task) }">{{ getTaskRate(task) }}%</td>
        </tr>
        <tr class="pp-stats-row">
          <td class="pp-task-name-cell pp-stats-label">人员完成率</td>
          <td
            class="pp-matrix-cell pp-rate-cell"
            :style="{ color: getPersonRateColor(person) }"
            v-for="person in persons"
            :key="person.id"
          >
            {{ getPersonRate(person) }}%
          </td>
          <td class="pp-rate-cell pp-total-rate">{{ totalRate }}%</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import { isOverdue } from '@/utils/date'

export default {
  name: 'MatrixTable',
  props: {
    tasks: {
      type: Array,
      default: () => []
    },
    persons: {
      type: Array,
      default: () => []
    },
    instances: {
      type: Array,
      default: () => []
    }
  },
  computed: {
    instanceMap() {
      const map = {}
      this.instances.forEach(i => {
        map[`${i.taskId}-${i.personId}`] = i
      })
      return map
    },
    totalRate() {
      const total = this.instances.length
      const completed = this.instances.filter(i => i.completed === 1).length
      return total > 0 ? Math.round(completed * 100 / total) : 0
    }
  },
  methods: {
    getInstance(task, person) {
      return this.instanceMap[`${task.id}-${person.id}`]
    },
    getCellStatus(task, person) {
      const instance = this.getInstance(task, person)
      if (!instance) return 'none'
      if (instance.completed === 1) return 'done'
      if (isOverdue(instance.deadline)) return 'overdue'
      return 'pending'
    },
    getCellClass(task, person) {
      return this.getCellStatus(task, person)
    },
    getCellIcon(task, person) {
      const status = this.getCellStatus(task, person)
      return { done: '✓', pending: '○', overdue: '!', none: '-' }[status]
    },
    getCycleClass(task) {
      const map = { '每日': 'daily', '每周': 'weekly', '每月': 'monthly' }
      return map[task.cycle] || 'daily'
    },
    getTaskRate(task) {
      const taskInstances = this.instances.filter(i => i.taskId === task.id)
      const total = taskInstances.length
      const completed = taskInstances.filter(i => i.completed === 1).length
      return total > 0 ? Math.round(completed * 100 / total) : 0
    },
    getTaskRateColor(task) {
      const rate = this.getTaskRate(task)
      return rate >= 80 ? '#10B981' : rate >= 50 ? '#F59E0B' : '#EF4444'
    },
    getPersonRate(person) {
      const personInstances = this.instances.filter(i => i.personId === person.id)
      const total = personInstances.length
      const completed = personInstances.filter(i => i.completed === 1).length
      return total > 0 ? Math.round(completed * 100 / total) : 0
    },
    getPersonRateColor(person) {
      const rate = this.getPersonRate(person)
      return rate >= 80 ? '#10B981' : rate >= 50 ? '#F59E0B' : '#EF4444'
    },
    handleCellClick(task, person) {
      const instance = this.getInstance(task, person)
      if (instance && instance.completed === 0) {
        this.$emit('complete', instance)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.pp-matrix-container {
  border: 1px solid #E5E7EB;
  border-radius: 8px;
  overflow: hidden;
}

.pp-matrix-tip {
  font-size: 12px;
  color: #6B7280;
  padding: 8px 12px;
  background: #F9FAFB;
  border-bottom: 1px solid #E5E7EB;
}

.pp-matrix-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;

  th, td {
    padding: 10px 12px;
    text-align: center;
    border: 1px solid #E5E7EB;
  }

  th {
    background: #F9FAFB;
    font-weight: 600;
    color: #374151;
  }
}

.pp-task-header {
  text-align: left;
  min-width: 160px;
  position: sticky;
  left: 0;
  z-index: 10;
  background: #F9FAFB;
}

.pp-person-header {
  min-width: 50px;
  font-size: 11px;
}

.pp-rate-header {
  min-width: 60px;
}

.pp-task-name-cell {
  text-align: left;
  position: sticky;
  left: 0;
  background: white;
  z-index: 5;
}

.pp-task-cell-name {
  font-weight: 500;
  color: #1F2937;
}

.pp-type-tag-sm {
  padding: 1px 6px;
  border-radius: 3px;
  font-size: 10px;
  font-weight: 500;
  margin-left: 6px;

  &.daily { background: #DBEAFE; color: #1D4ED8; }
  &.weekly { background: #FEF3C7; color: #B45309; }
  &.monthly { background: #E0E7FF; color: #4338CA; }
}

.pp-matrix-cell {
  min-width: 50px;
  cursor: pointer;

  &.done {
    background: #D1FAE5;
    color: #10B981;
  }
  &.pending {
    background: #F9FAFB;
    color: #9CA3AF;
  }
  &.overdue {
    background: #FEE2E2;
    color: #EF4444;
  }
  &.none {
    color: #D1D5DB;
  }

  &:hover {
    opacity: 0.8;
  }
}

.pp-rate-cell {
  font-weight: 600;
}

.pp-stats-row {
  td {
    background: #EFF6FF;
  }
}

.pp-stats-label {
  color: #374151;
  font-weight: 600;
}

.pp-total-rate {
  color: #2563EB;
  font-weight: 700;
}
</style>