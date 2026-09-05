<template>
  <el-card v-loading="loading" shadow="never">
    <template #header>
      <div class="header-bar">
        <el-button :icon="ArrowLeft" size="small" @click="$router.back()">返回</el-button>
        <span class="title">{{ act.title || '活动详情' }}</span>
      </div>
    </template>

    <el-descriptions :column="2" border>
      <el-descriptions-item label="所属社团">{{ act.clubName }}</el-descriptions-item>
      <el-descriptions-item label="地点">{{ act.location }}</el-descriptions-item>
      <el-descriptions-item label="开始时间">{{ act.startTime }}</el-descriptions-item>
      <el-descriptions-item label="结束时间">{{ act.endTime }}</el-descriptions-item>
      <el-descriptions-item label="报名人数">
        {{ act.signupCount || 0 }}{{ act.maxParticipants ? ' / ' + act.maxParticipants : '（不限）' }}
      </el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="act.status === 2 ? 'info' : 'success'" size="small">
          {{ act.status === 2 ? '已结束' : '进行中/未开始' }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="活动介绍" :span="2">
        <div style="white-space: pre-wrap">{{ act.description || '暂无介绍' }}</div>
      </el-descriptions-item>
    </el-descriptions>

    <div class="actions">
      <template v-if="role === 'student' || role === 'leader'">
        <el-button
          v-if="!signed"
          type="primary"
          :disabled="isFull || act.status === 2"
          :loading="acting"
          @click="doSignup"
        >
          {{ isFull ? '名额已满' : '立即报名' }}
        </el-button>
        <el-button v-else type="warning" plain :loading="acting" @click="doCancel">取消报名</el-button>
      </template>
      <el-button type="info" plain @click="openSignups">查看报名名单</el-button>
    </div>

    <el-drawer v-model="drawer" title="报名名单" size="55%">
      <el-table v-loading="drawerLoading" :data="signupRecords" border>
        <el-table-column prop="username" label="用户名" min-width="110" />
        <el-table-column prop="nickname" label="姓名" min-width="90" />
        <el-table-column label="签到状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="row.checkinStatus === 1 ? 'success' : 'info'" size="small">
              {{ row.checkinStatus === 1 ? '已签到' : '未签到' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="报名时间" width="160" />
        <el-table-column v-if="canManage" label="操作" width="110" align="center">
          <template #default="{ row }">
            <el-button
              v-if="row.checkinStatus !== 1"
              size="small"
              type="success"
              @click="doCheckin(row)"
            >
              签到
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <Pagination :total="signupTotal" :current="signupPage" :size="signupSize" @change="onSignupPage" />
    </el-drawer>
  </el-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { activityDetail, signup, cancelSignup, pageSignups, checkin, mySignups } from '../../api/activity'
import { useUserStore } from '../../stores/user'
import Pagination from '../../components/Pagination.vue'

const route = useRoute()
const store = useUserStore()
const role = computed(() => store.role)
const canManage = computed(() => role.value === 'admin' || role.value === 'leader')

const act = ref({})
const loading = ref(false)
const acting = ref(false)
const signed = ref(false)

const drawer = ref(false)
const drawerLoading = ref(false)
const signupRecords = ref([])
const signupTotal = ref(0)
const signupPage = ref(1)
const signupSize = ref(10)

const isFull = computed(
  () => act.value.maxParticipants && (act.value.signupCount || 0) >= act.value.maxParticipants
)

async function load() {
  loading.value = true
  try {
    const res = await activityDetail(route.params.id)
    act.value = res.data
    if (role.value === 'student') {
      const mine = await mySignups({ page: 1, size: 100 })
      signed.value = (mine.data.records || []).some((s) => s.activityId === Number(route.params.id))
    }
  } finally {
    loading.value = false
  }
}

async function doSignup() {
  acting.value = true
  try {
    await signup(act.value.id)
    ElMessage.success('报名成功')
    load()
  } finally {
    acting.value = false
  }
}

async function doCancel() {
  acting.value = true
  try {
    await cancelSignup(act.value.id)
    ElMessage.success('已取消报名')
    load()
  } finally {
    acting.value = false
  }
}

async function openSignups() {
  drawer.value = true
  loadSignups(1)
}

async function loadSignups(p) {
  if (p) signupPage.value = p
  drawerLoading.value = true
  try {
    const res = await pageSignups(route.params.id, {
      page: signupPage.value,
      size: signupSize.value
    })
    signupRecords.value = res.data.records
    signupTotal.value = Number(res.data.total)
  } finally {
    drawerLoading.value = false
  }
}

function onSignupPage({ page, size }) {
  if (size) signupSize.value = size
  loadSignups(page || signupPage.value)
}

async function doCheckin(row) {
  await checkin(route.params.id, row.userId)
  ElMessage.success('已签到：' + (row.nickname || row.username))
  loadSignups()
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
  display: flex;
  gap: 10px;
}
</style>