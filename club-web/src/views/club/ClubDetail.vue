<template>
  <el-card v-loading="loading" shadow="never">
    <template #header>
      <div class="header-bar">
        <el-button :icon="ArrowLeft" size="small" @click="$router.back()">返回</el-button>
        <span class="title">{{ club.name || '社团详情' }}</span>
      </div>
    </template>

    <el-descriptions :column="2" border>
      <el-descriptions-item label="社团名称">{{ club.name }}</el-descriptions-item>
      <el-descriptions-item label="分类">
        <el-tag size="small" type="info">{{ club.category || '其他' }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="社长">{{ club.presidentName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="成员数">{{ club.memberCount }} 人</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="statusTag(club.status)" size="small">{{ statusText(club.status) }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ club.createTime }}</el-descriptions-item>
      <el-descriptions-item label="社团简介" :span="2">
        {{ club.description || '暂无简介' }}
      </el-descriptions-item>
    </el-descriptions>

    <div class="actions" v-if="role === 'student' && club.status === 1">
      <el-button type="primary" @click="joinVisible = true">申请加入</el-button>
    </div>

    <el-dialog v-model="joinVisible" title="申请加入" width="420px">
      <el-input v-model="joinReason" type="textarea" :rows="3" placeholder="申请留言（选填）" />
      <template #footer>
        <el-button @click="joinVisible = false">取消</el-button>
        <el-button type="primary" :loading="joining" @click="submitJoin">提交申请</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { clubDetail } from '../../api/club'
import { applyJoin } from '../../api/member'
import { useUserStore } from '../../stores/user'

const route = useRoute()
const store = useUserStore()
const role = computed(() => store.role)

const club = ref({})
const loading = ref(false)
const joinVisible = ref(false)
const joining = ref(false)
const joinReason = ref('')

function statusText(s) {
  return { 0: '待审核', 1: '已通过', 2: '已驳回' }[s] || '-'
}
function statusTag(s) {
  return { 0: 'warning', 1: 'success', 2: 'danger' }[s] || 'info'
}

async function load() {
  loading.value = true
  try {
    const res = await clubDetail(route.params.id)
    club.value = res.data
  } finally {
    loading.value = false
  }
}

async function submitJoin() {
  joining.value = true
  try {
    await applyJoin(club.value.id, { applyReason: joinReason.value || null })
    ElMessage.success('申请已提交，等待社长审核')
    joinVisible.value = false
  } finally {
    joining.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.header-bar {
  display: flex;
  align-items: center;
  gap: 12px;
}
.title {
  font-weight: 600;
}
.actions {
  margin-top: 20px;
}
</style>