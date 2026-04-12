<template>
  <div class="pp-week-selector">
    <span class="pp-week-label">选择周期：</span>
    <button class="pp-week-nav-btn" @click="goPrev">◀ 上周</button>
    <span class="pp-week-current">{{ displayText }}</span>
    <button class="pp-week-nav-btn" @click="goNext">下周 ▶</button>
    <button class="pp-btn pp-btn-secondary pp-btn-sm" @click="goCurrent">回到本周</button>
  </div>
</template>

<script>
import { getCurrentWeek, formatWeekDisplay, prevWeek, nextWeek } from '@/utils/date'

export default {
  name: 'WeekSelector',
  props: {
    value: {
      type: String,
      default: ''
    }
  },
  computed: {
    displayText() {
      return formatWeekDisplay(this.value)
    }
  },
  methods: {
    goPrev() {
      const newPeriod = prevWeek(this.value)
      this.$emit('input', newPeriod)
      this.$emit('change', newPeriod)
    },
    goNext() {
      const newPeriod = nextWeek(this.value)
      this.$emit('input', newPeriod)
      this.$emit('change', newPeriod)
    },
    goCurrent() {
      const current = getCurrentWeek()
      this.$emit('input', current)
      this.$emit('change', current)
    }
  }
}
</script>

<style lang="scss" scoped>
.pp-week-selector {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 24px;
  background: white;
  border-bottom: 1px solid #E5E7EB;
}

.pp-week-label {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
}

.pp-week-nav-btn {
  padding: 6px 12px;
  border: 1px solid #E5E7EB;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  color: #4B5563;
  &:hover {
    background: #F9FAFB;
    border-color: #2563EB;
    color: #2563EB;
  }
}

.pp-week-current {
  font-size: 16px;
  font-weight: 600;
  color: #2563EB;
  padding: 8px 16px;
  background: #EFF6FF;
  border-radius: 6px;
}

.pp-btn-sm {
  padding: 4px 10px;
  font-size: 12px;
}
</style>