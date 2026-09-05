<template>
  <el-card shadow="never">
    <div class="toolbar">
      <el-input
        v-model="query.keyword"
        placeholder="活动名称"
        clearable
        style="width: 200px"
        @keyup.enter="load(1)"
        @clear="load(1)"
      />
      <el-button type="primary" :icon="Search" @click="load(1)">搜索</el-button>
    </div>

    <el-table v-loading="loading" :data="records" border stripe>
      <el-table-column prop="id" label="ID" width="64" />
      <el-table-column prop="title" label="活动名称" min-width="160" />
      <el-table-column prop="clubName" label="所属社团" min-width="120" />
      <el-table-column label="时间" min-width="220">
        <template #default="{ row }">
          {{ row.startTime }} ~ {{ row.endTime }}
        </template>
      </el-table-column>
      <el-table-column prop="location" label="地点" min-width="110" show-overflow-tooltip />
      <el-table-column label="报名" width="110" align="center">
        <template #default="{ row }">
          <span :class="{ full: isFull(row) }">
            {{ row.signupCount || 0 }}{{ row.maxParticipants ? ' / ' + row.maxParticipants : '' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="110" fixed="right" align="center">
        <template #default="{ row }">
          <el-button size="small" type="primary" plain @click="$router.push('/activities/' + row.id)">
            详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <Pagination :total="total" :current="query.page" :size="query.size" @change="onPageChange" />
  </el-card>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { pageActivities } from '../../api/activity'
import Pagination from '../../components/Pagination.vue'

const records = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, size: 10, keyword: '' })

function isFull(row) {
  return row.maxParticipants && row.signupCount >= row.maxParticipants
}

async function load(page) {
  if (page) query.page = page
  loading.value = true
  try {
    const res = await pageActivities({
      page: query.page,
      size: query.size,
      keyword: query.keyword || undefined
    })
    records.value = res.data.records
    total.value = Number(res.data.total)
  } finally {
    loading.value = false
  }
}

function onPageChange({ page, size }) {
  if (size) query.size = size
  load(page || query.page)
}

onMounted(() => load())
</script>

<style scoped>
.full {
  color: #f56c6c;
  font-weight: 600;
}
</style>