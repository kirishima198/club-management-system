<template>
  <el-card shadow="never">
    <div class="toolbar">
      <el-radio-group v-model="query.status" @change="load(1)">
        <el-radio-button :value="0">待审核</el-radio-button>
        <el-radio-button :value="1">已通过</el-radio-button>
        <el-radio-button :value="2">已驳回</el-radio-button>
      </el-radio-group>
      <el-input
        v-model="query.keyword"
        placeholder="社团名称"
        clearable
        style="width: 200px"
        @keyup.enter="load(1)"
        @clear="load(1)"
      />
      <el-button type="primary" :icon="Search" @click="load(1)">搜索</el-button>
    </div>

    <el-table v-loading="loading" :data="records" border stripe>
      <el-table-column prop="id" label="ID" width="64" />
      <el-table-column prop="name" label="社团名称" min-width="140" />
      <el-table-column prop="category" label="分类" width="90" align="center" />
      <el-table-column prop="presidentName" label="申请人(社长)" width="120" />
      <el-table-column prop="description" label="社团简介" min-width="200" show-overflow-tooltip />
      <el-table-column label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="statusTag(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="rejectReason" label="驳回原因" min-width="120" show-overflow-tooltip />
      <el-table-column prop="createTime" label="申请时间" width="160" />
      <el-table-column label="操作" width="220" fixed="right" align="center">
        <template #default="{ row }">
          <template v-if="row.status === 0">
            <el-button size="small" type="success" @click="audit(row, true)">通过</el-button>
            <el-button size="small" type="danger" plain @click="audit(row, false)">驳回</el-button>
          </template>
          <el-button v-else size="small" @click="$router.push('/clubs/' + row.id)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <Pagination :total="total" :current="query.page" :size="query.size" @change="onPageChange" />

    <el-dialog v-model="rejectVisible" title="驳回原因" width="420px">
      <el-input v-model="rejectReason" type="textarea" :rows="3" placeholder="请填写驳回原因" />
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="danger" :loading="auditing" @click="submitReject">确认驳回</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { pageClubs, auditClub } from '../../api/club'
import Pagination from '../../components/Pagination.vue'

const records = ref([])
const total = ref(0)
const loading = ref(false)
const auditing = ref(false)
const query = reactive({ page: 1, size: 10, keyword: '', status: 0 })

const rejectVisible = ref(false)
const rejectReason = ref('')
const auditClubId = ref(null)

function statusText(s) {
  return { 0: '待审核', 1: '已通过', 2: '已驳回' }[s] || '-'
}
function statusTag(s) {
  return { 0: 'warning', 1: 'success', 2: 'danger' }[s] || 'info'
}

async function load(page) {
  if (page) query.page = page
  loading.value = true
  try {
    const res = await pageClubs({
      page: query.page,
      size: query.size,
      keyword: query.keyword || undefined,
      status: query.status
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

async function audit(row, approve) {
  if (approve) {
    await ElMessageBox.confirm(`确定通过社团「${row.name}」的创建申请吗？`, '审核确认', { type: 'success' })
    auditing.value = true
    try {
      await auditClub(row.id, { approve: true })
      ElMessage.success('已通过')
      load()
    } finally {
      auditing.value = false
    }
  } else {
    auditClubId.value = row.id
    rejectReason.value = ''
    rejectVisible.value = true
  }
}

async function submitReject() {
  if (!rejectReason.value.trim()) {
    ElMessage.warning('请填写驳回原因')
    return
  }
  auditing.value = true
  try {
    await auditClub(auditClubId.value, { approve: false, reason: rejectReason.value })
    ElMessage.success('已驳回')
    rejectVisible.value = false
    load()
  } finally {
    auditing.value = false
  }
}

onMounted(() => load())
</script>