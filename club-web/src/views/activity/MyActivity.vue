<template>
  <el-card shadow="never">
    <template #header>我的活动报名</template>

    <el-table v-loading="loading" :data="records" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="title" label="活动名称" min-width="160" />
      <el-table-column prop="clubName" label="所属社团" min-width="120" />
      <el-table-column prop="createTime" label="报名时间" width="160" />
      <el-table-column label="签到状态" width="110" align="center">
        <template #default="{ row }">
          <el-tag :type="row.checkinStatus === 1 ? 'success' : 'info'" size="small">
            {{ row.checkinStatus === 1 ? '已签到' : '未签到' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right" align="center">
        <template #default="{ row }">
          <el-button size="small" type="primary" plain @click="$router.push('/activities/' + row.activityId)">
            活动详情
          </el-button>
          <el-popconfirm v-if="row.checkinStatus !== 1" title="确定取消报名吗？" @confirm="cancel(row)">
            <template #reference>
              <el-button size="small" type="warning" plain>取消报名</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <Pagination :total="total" :current="page" :size="size" @change="onPageChange" />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { mySignups, cancelSignup } from '../../api/activity'
import Pagination from '../../components/Pagination.vue'

const records = ref([])
const total = ref(0)
const loading = ref(false)
const page = ref(1)
const size = ref(10)

async function load(p) {
  if (p) page.value = p
  loading.value = true
  try {
    const res = await mySignups({ page: page.value, size: size.value })
    records.value = res.data.records
    total.value = Number(res.data.total)
  } finally {
    loading.value = false
  }
}

function onPageChange({ page: p, size: s }) {
  if (s) size.value = s
  load(p || page.value)
}

async function cancel(row) {
  await cancelSignup(row.activityId)
  ElMessage.success('已取消报名')
  load()
}

onMounted(() => load())
</script>