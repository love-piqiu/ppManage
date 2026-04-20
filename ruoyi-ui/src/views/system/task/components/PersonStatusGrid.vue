<template>
  <div class="pp-person-status-grid">
    <div
      class="pp-person-status-item"
      :class="getStatusClass(item)"
      v-for="item in personStatusList"
      :key="item.personId"
      @click="handleClick(item)"
    >
      <div class="pp-avatar-sm" :style="avatarStyle(item.personName)">{{ item.personName.charAt(0) }}</div>
      <div class="pp-person-info">
        <div class="pp-person-name">{{ item.personName }}</div>
        <div class="pp-person-status-text" :class="getStatusClass(item)">
          {{ getStatusIcon(item) }} {{ getStatusText(item) }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { isOverdue } from '@/utils/date'

export default {
  name: 'PersonStatusGrid',
  props: {
    persons: {
      type: Array,
      default: () => []
    },
    instances: {
      type: Array,
      default: () => []
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
    personStatusList() {
      // 合并人员信息和实例状态
      return this.persons.map(person => {
        const instance = this.instances.find(i => i.personId === person.id)
        return {
          personId: person.id,
          personName: person.name,
          status: this.getStatus(instance),
          instance: instance
        }
      })
    }
  },
  methods: {
    getStatus(instance) {
      if (!instance) return 'none'
      if (instance.completed === 1) return 'done'
      if (isOverdue(instance.deadline)) return 'overdue'
      return 'pending'
    },
    getStatusClass(item) {
      return {
        done: 'completed',
        pending: '',
        overdue: 'overdue',
        none: ''
      }[item.status]
    },
    getStatusIcon(item) {
      return {
        done: '✓',
        pending: '○',
        overdue: '!',
        none: '○'
      }[item.status]
    },
    getStatusText(item) {
      return {
        done: '已完成',
        pending: '待完成',
        overdue: '超期',
        none: '待完成'
      }[item.status]
    },
    handleClick(item) {
      // 如果已完成，发出取消完成事件
      if (item.status === 'done') {
        if (item.instance) {
          this.$emit('uncomplete', item.instance)
        }
        return
      }

      // 如果 instance 存在，发送 complete 事件
      if (item.instance) {
        this.$emit('complete', item.instance)
      } else {
        // instance 不存在，发送 create-and-complete 事件
        // 需要父组件来创建 instance 然后完成
        this.$emit('createAndComplete', {
          personId: item.personId,
          personName: item.personName
        })
      }
    },
    avatarStyle(name) {
      if (!name) return { background: '#D1D5DB' }
      const idx = name.charCodeAt(0) % this.avatarColors.length
      return { background: this.avatarColors[idx] }
    }
  }
}
</script>

<style lang="scss" scoped>
.pp-person-status-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 8px;
}

.pp-person-status-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  background: white;
  border-radius: 6px;
  border: 1px solid #E5E7EB;
  cursor: pointer;
  transition: all 0.15s;

  &:hover {
    border-color: #2563EB;
    box-shadow: 0 2px 4px rgba(37, 99, 235, 0.1);
  }

  &.completed {
    border-color: #10B981;
    background: #F0FDF4;
  }

  &.overdue {
    border-color: #EF4444;
    background: #FEF2F2;
  }
}

.pp-avatar-sm {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  color: white;
  background: linear-gradient(135deg, #2563EB, #3B82F6);
  flex-shrink: 0;
}

.pp-person-info {
  flex: 1;
  min-width: 0;
}

.pp-person-name {
  font-size: 13px;
  font-weight: 500;
  color: #1F2937;
}

.pp-person-status-text {
  font-size: 11px;
  margin-top: 2px;
  color: #9CA3AF;

  &.completed {
    color: #10B981;
  }

  &.overdue {
    color: #EF4444;
  }
}
</style>