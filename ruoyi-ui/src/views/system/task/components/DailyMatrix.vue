<template>
  <div class="pp-daily-matrix">
    <div class="pp-matrix-header">
      <div class="pp-matrix-corner">人员</div>
      <div class="pp-matrix-days">
        <div class="pp-matrix-day" v-for="day in weekDays" :key="day.date">
          <div class="pp-day-name">{{ day.name }}</div>
          <div class="pp-day-date">{{ day.shortDate }}</div>
          <div class="pp-day-actions">
            <button class="pp-day-btn complete" v-if="hasDayUncompleted(day.date)" @click="handleCompleteDay(day.date)">✓</button>
            <button class="pp-day-btn uncomplete" v-if="hasDayCompleted(day.date)" @click="handleUncompleteDay(day.date)">○</button>
          </div>
        </div>
      </div>
    </div>
    <div class="pp-matrix-body">
      <div class="pp-matrix-row" v-for="person in persons" :key="person.id">
        <div class="pp-matrix-person">
          <div class="pp-avatar-xs" :style="avatarStyle(person.name)">{{ person.name.charAt(0) }}</div>
          <span class="pp-person-name">{{ person.name }}</span>
        </div>
        <div class="pp-matrix-cells">
          <div
            class="pp-matrix-cell"
            :class="getCellClass(person.id, day.date)"
            v-for="day in weekDays"
            :key="day.date"
            @click="handleCellClick(person, day.date)"
          >
            <span class="pp-cell-icon">{{ getCellIcon(person.id, day.date) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getWeekDays } from '@/utils/date'

export default {
  name: 'DailyMatrix',
  props: {
    task: {
      type: Object,
      required: true
    },
    persons: {
      type: Array,
      default: () => []
    },
    instances: {
      type: Array,
      default: () => []
    },
    period: {
      type: String,
      default: ''
    },
    workdaysOnly: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      avatarColors: [
        'linear-gradient(135deg, #2563EB, #3B82F6)',
        'linear-gradient(135deg, #10B981, #34D399)',
        'linear-gradient(135deg, #F59E0B, #FBBF24)',
        'linear-gradient(135deg, #6366F1, #818CF8)',
        'linear-gradient(135deg, #EF4444, #F87171)',
        'linear-gradient(135deg, #8B5CF6, #A78BFA)'
      ]
    }
  },
  computed: {
    weekDays() {
      // 根据 period 计算一周的日期
      const days = getWeekDays(this.period)
      // 如果是每工作日，只返回前5天（周一到周五）
      if (this.workdaysOnly) {
        return days.slice(0, 5)
      }
      return days
    }
  },
  methods: {
    getPersonInstance(personId, date) {
      // 查找特定人员和日期的 instance
      return this.instances.find(i => i.personId === personId && i.period === date)
    },
    getCellClass(personId, date) {
      const instance = this.getPersonInstance(personId, date)
      if (!instance) return 'empty'
      if (instance.completed === 1) return 'done'
      // 检查是否超期（今天的日期 > date）
      const today = new Date().toISOString().split('T')[0]
      if (date < today) return 'overdue'
      return 'pending'
    },
    getCellIcon(personId, date) {
      const cls = this.getCellClass(personId, date)
      return { done: '✓', overdue: '!', pending: '○', empty: '○' }[cls]
    },
    handleCellClick(person, date) {
      const instance = this.getPersonInstance(person.id, date)
      if (instance) {
        if (instance.completed === 1) {
          // 已完成，发出取消完成事件
          this.$emit('uncomplete', instance)
          return
        }
        this.$emit('complete', instance)
      } else {
        this.$emit('createAndComplete', {
          personId: person.id,
          personName: person.name,
          date: date
        })
      }
    },
    avatarStyle(name) {
      if (!name) return { background: '#D1D5DB' }
      const idx = name.charCodeAt(0) % this.avatarColors.length
      return { background: this.avatarColors[idx] }
    },
    hasDayUncompleted(date) {
      // 检查该日期是否有未完成的任务（包括未创建instance的）
      const dayInstances = this.instances.filter(i => i.period === date)
      const completedPersonIds = dayInstances.filter(i => i.completed === 1).map(i => i.personId)
      // 有instance但未完成的
      const hasUncompletedInstance = dayInstances.some(i => i.completed === 0)
      // 有人员但没有instance的
      const hasNoInstance = this.persons.some(p => !dayInstances.find(i => i.personId === p.id))
      return hasUncompletedInstance || hasNoInstance
    },
    hasDayCompleted(date) {
      // 检查该日期是否有已完成的任务
      return this.instances.filter(i => i.period === date && i.completed === 1).length > 0
    },
    handleCompleteDay(date) {
      // 获取该日期所有未完成的instance和没有instance的人员
      const dayInstances = this.instances.filter(i => i.period === date)
      const uncompletedInstances = dayInstances.filter(i => i.completed === 0)
      const noInstancePersons = this.persons.filter(p => !dayInstances.find(i => i.personId === p.id))
      this.$emit('completeDay', {
        date,
        instances: uncompletedInstances,
        noInstancePersons,
        taskId: this.task.id,
        taskName: this.task.name
      })
    },
    handleUncompleteDay(date) {
      // 获取该日期所有已完成的instance
      const completedInstances = this.instances.filter(i => i.period === date && i.completed === 1)
      this.$emit('uncompleteDay', { date, instances: completedInstances })
    }
  }
}
</script>

<style lang="scss" scoped>
.pp-daily-matrix {
  border: 1px solid #E5E7EB;
  border-radius: 8px;
  overflow: hidden;
}

.pp-matrix-header {
  display: flex;
  background: #F9FAFB;
  border-bottom: 1px solid #E5E7EB;
}

.pp-matrix-corner {
  width: 100px;
  padding: 8px 12px;
  font-size: 12px;
  font-weight: 600;
  color: #6B7280;
  border-right: 1px solid #E5E7EB;
}

.pp-matrix-days {
  flex: 1;
  display: flex;
}

.pp-matrix-day {
  flex: 1;
  padding: 8px 4px;
  text-align: center;
  border-right: 1px solid #E5E7EB;

  &:last-child {
    border-right: none;
  }
}

.pp-day-actions {
  display: flex;
  justify-content: center;
  gap: 4px;
  margin-top: 4px;
}

.pp-day-btn {
  padding: 2px 6px;
  font-size: 11px;
  border: none;
  border-radius: 3px;
  cursor: pointer;

  &.complete {
    background: #10B981;
    color: white;
    &:hover { background: #059669; }
  }

  &.uncomplete {
    background: #F3F4F6;
    color: #6B7280;
    &:hover { background: #E5E7EB; }
  }
}

.pp-day-name {
  font-size: 12px;
  font-weight: 600;
  color: #374151;
}

.pp-day-date {
  font-size: 10px;
  color: #9CA3AF;
}

.pp-matrix-body {
  display: flex;
  flex-direction: column;
}

.pp-matrix-row {
  display: flex;
  border-bottom: 1px solid #E5E7EB;

  &:last-child {
    border-bottom: none;
  }
}

.pp-matrix-person {
  width: 100px;
  padding: 8px 12px;
  display: flex;
  align-items: center;
  gap: 6px;
  background: white;
  border-right: 1px solid #E5E7EB;
}

.pp-avatar-xs {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  font-weight: 600;
  color: white;
  flex-shrink: 0;
}

.pp-person-name {
  font-size: 12px;
  color: #1F2937;
  font-weight: 500;
}

.pp-matrix-cells {
  flex: 1;
  display: flex;
}

.pp-matrix-cell {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px;
  cursor: pointer;
  border-right: 1px solid #E5E7EB;
  transition: background 0.15s;

  &:last-child {
    border-right: none;
  }

  &:hover {
    background: #F3F4F6;
  }

  &.done {
    background: #F0FDF4;

    .pp-cell-icon {
      color: #10B981;
    }

    &:hover {
      background: #DCFCE7;
    }
  }

  &.overdue {
    background: #FEF2F2;

    .pp-cell-icon {
      color: #EF4444;
    }
  }

  &.pending {
    .pp-cell-icon {
      color: #9CA3AF;
    }
  }

  &.empty {
    .pp-cell-icon {
      color: #D1D5DB;
    }
  }
}

.pp-cell-icon {
  font-size: 14px;
  font-weight: 600;
}
</style>