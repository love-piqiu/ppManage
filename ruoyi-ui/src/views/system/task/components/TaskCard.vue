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
    </div>
    <div class="pp-task-expand-content" v-if="isExpanded" @click.stop>
      <div class="pp-expand-title">人员完成情况（点击可更新状态）</div>
      <person-status-grid
        :persons="persons"
        :instances="instances"
        @complete="handleComplete"
      />
    </div>
  </div>
</template>

<script>
import PersonStatusGrid from './PersonStatusGrid.vue'

export default {
  name: 'TaskCard',
  components: { PersonStatusGrid },
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
        '每周': 'weekly',
        '每月': 'monthly'
      }
      return map[this.task.cycle] || 'daily'
    },
    deadlineText() {
      if (this.task.cycle === '每日') {
        return this.task.deadlineTime || '18:00'
      } else if (this.task.cycle === '每周') {
        return `周${this.task.deadlineWeekday || '五'} ${this.task.deadlineTime || '17:00'}`
      } else if (this.task.cycle === '每月') {
        return `${this.task.deadlineDay || 5}日 ${this.task.deadlineTime || '12:00'}`
      }
      return this.task.deadlineTime || '-'
    },
    completionStats() {
      const total = this.instances.length
      const completed = this.instances.filter(i => i.completed === 1).length
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
    }
  },
  methods: {
    toggleExpand() {
      this.isExpanded = !this.isExpanded
    },
    handleComplete(instance) {
      this.$emit('complete', instance)
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

.pp-expand-title {
  font-size: 13px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 12px;
}
</style>