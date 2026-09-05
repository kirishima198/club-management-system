<template>
  <el-card shadow="hover" class="stat-card">
    <div class="stat-body">
      <div class="icon-wrap" :style="{ backgroundColor: color + '20', color }">
        <el-icon :size="26">
          <component :is="icon" />
        </el-icon>
      </div>
      <div class="text">
        <div class="value">{{ value }}</div>
        <div class="label">{{ label }}</div>
      </div>
    </div>
  </el-card>
</template>

<script setup>
defineProps({
  label: String,
  value: { type: [Number, String], default: 0 },
  icon: String,
  color: { type: String, default: '#409eff' }
})
</script>

<style scoped>
.stat-card {
  cursor: default;
}
.stat-body {
  display: flex;
  align-items: center;
  gap: 16px;
}
.icon-wrap {
  width: 52px;
  height: 52px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.value {
  font-size: 24px;
  font-weight: 700;
  line-height: 1.2;
}
.label {
  color: #909399;
  font-size: 13px;
  margin-top: 2px;
}
</style>