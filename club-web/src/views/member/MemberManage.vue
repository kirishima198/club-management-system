<template>
  <el-card shadow="never">
    <template #header>
      <div class="header-bar">
        <span>成员管理</span>
        <el-button size="small" :icon="ArrowLeft" @click="$router.push('/clubs/my')">返回我的社团</el-button>
      </div>
    </template>

    <el-tabs v-model="tab" @tab-change="load(1)">
      <el-tab-pane label="正式成员" name="members">
        <div class="toolbar">
          <el-input
            v-model="keyword"
            placeholder="用户名/姓名"
            clearable
            style="width: 200px"
            @keyup.enter="load(1)"
            @clear="load(1)"
          />
          <el-button type="primary" :icon="Search" @click="load(1)">搜索</el-button>
        </div>
        <el-table v-loading="loading" :data="records" border stripe>
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="username" label="用户名" min-width="110" />
          <el-table-column prop="nickname" label="姓名" min-width="90" />
          <el-table-column prop="phone" label="手机号" width="130" />
          <el-table-column prop="applyTime" label="申请时间" width="160" />
          <el-table-column prop="handleTime" label="加入时间" width="160" />
          <el-table-column label="操作" width="120" fixed="right" align="center">
            <template #default="{ row }">
              <el-popconfirm title="确定移除该成员吗？" @confirm="remove(row)">
                <template #reference>
                  <el-button size="small" type="danger" plain>移除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="入会申请" name="pending">
        <el-table v-loading="loading" :data="records" border stripe>
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="username" label="用户名" min-width="110" />
          <el-table-column prop="nickname" label="姓名" min-width="90" />
          <el-table-column prop="applyReason" label="申请留言" min-width="180" show-overflow-tooltip />
          <el-table-column prop="applyTime" label="申请时间" width="160" />
          <el-table-column label="操作" width="180" fixed="right" align="center">
            <template #default="{ row }">
              <el-button size="small" type="success" @click="audit(row, true)">通过</el-button>
              <el-button size="small" type="danger" plain @click="audit(row, false)">拒绝</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <Pagination :total="total" :current="page" :size="size" @change="onPageChange" />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Search } from '@element-plus/icons-vue'
import { pageMembers, pagePending, auditMember, removeMember } from '../../api/member'
import Pagination from '../../components/Pagination.vue'

const route = useRoute()
const clubId = route.params.clubId

const tab = ref('members')
const records = ref([])
const total = ref(0)
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const keyword = ref('')

async function load(p) {
  if (p) page.value = p
  loading.value = true
  try {
    const params = { page: page.value, size: size.value }
    let res
    if (tab.value === 'members') {
      if (keyword.value) params.keyword = keyword.value
      res = await pageMembers(clubId, params)
    } else {
      res = await pagePending(clubId, params)
    }
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

async function audit(row, approve) {
  await auditMember(clubId, row.id, { approve })
  ElMessage.success(approve ? '已通过' : '已拒绝')
  load()
}

async function remove(row) {
  await removeMember(clubId, row.id)
  ElMessage.success('已移除')
  load()
}

onMounted(() => load())
</script>

<style scoped>
.header-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>