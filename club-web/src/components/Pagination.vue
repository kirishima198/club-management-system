<template>
  <div class="pagination-wrap">
    <el-pagination
      :current-page="current"
      :page-size="size"
      :page-sizes="[5, 10, 20, 50]"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      background
      @current-change="onCurrentChange"
      @size-change="onSizeChange"
    />
  </div>
</template>

<script setup>
defineProps({
  total: { type: Number, default: 0 },
  current: { type: Number, default: 1 },
  size: { type: Number, default: 10 }
})
const emit = defineEmits(['change'])

function onCurrentChange(page) {
  emit('change', { page, size: null })
}
function onSizeChange(size) {
  emit('change', { page: 1, size })
}
</script>

<style scoped>
.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>