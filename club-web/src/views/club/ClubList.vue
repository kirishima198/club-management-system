<template>
  <div>
    <el-card shadow="never" class="page-card">
      <div class="toolbar">
        <el-input
          v-model="query.keyword"
          placeholder="社团名称"
          clearable
          style="width: 200px"
          @keyup.enter="load(1)"
          @clear="load(1)"
        />
        <el-select v-model="query.category" placeholder="分类" clearable style="width: 140px" @change="load(1)">
          <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="load(1)">搜索</el-button>
      </div>
    </el-card>

    <div v-loading="loading">
      <el-empty v-if="!loading && records.length === 0" description="暂无社团" />
      <el-row :gutter="16" v-else>
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="club in records" :key="club.id">
          <el-card shadow="hover" class="club-card" @click="$router.push('/clubs/' + club.id)">
            <div class="club-head">
              <el-avatar :size="46" shape="square" class="club-logo">
                {{ club.name.charAt(0) }}
              </el-avatar>
              <div class="club-name">
                <div class="name">{{ club.name }}</div>
                <el-tag size="small" type="info">{{ club.category || '其他' }}</el-tag>
              </div>
            </div>
            <p class="desc">{{ club.description || '暂无简介' }}</p>
            <div class="meta">
              <span>社长：{{ club.presidentName || '-' }}</span>
              <span><el-icon><User /></el-icon> {{ club.memberCount }} 人</span>
            </div>
            <el-button
              type="primary"
              size="small"
              class="join-btn"
              v-if="role === 'student'"
              @click.stop="join(club)"
            >
              申请加入
            </el-button>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <el-card shadow="never" v-if="total > 0">
      <Pagination :total="total" :current="query.page" :size="query.size" @change="onPageChange" />
    </el-card>

    <el-dialog v-model="joinVisible" title="申请加入" width="420px">
      <el-form>
        <el-form-item label="申请留言">
          <el-input v-model="joinReason" type="textarea" :rows="3" placeholder="想对社长说的话（选填）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="joinVisible = false">取消</el-button>
        <el-button type="primary" :loading="joining" @click="submitJoin">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { pageClubs } from '../../api/club'
import { applyJoin } from '../../api/member'
import { useUserStore } from '../../stores/user'
import Pagination from '../../components/Pagination.vue'

const categories = ['文艺', '体育', '学术', '科技', '公益', '其他']
const store = useUserStore()
const role = computed(() => store.role)

const records = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, size: 8, keyword: '', category: null })

const joinVisible = ref(false)
const joining = ref(false)
const joinReason = ref('')
const joinClubId = ref(null)

async function load(page) {
  if (page) query.page = page
  loading.value = true
  try {
    const res = await pageClubs({
      page: query.page,
      size: query.size,
      keyword: query.keyword || undefined,
      category: query.category || undefined,
      status: 1
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

function join(club) {
  joinClubId.value = club.id
  joinReason.value = ''
  joinVisible.value = true
}

async function submitJoin() {
  joining.value = true
  try {
    await applyJoin(joinClubId.value, { applyReason: joinReason.value || null })
    ElMessage.success('申请已提交，等待社长审核')
    joinVisible.value = false
  } finally {
    joining.value = false
  }
}

onMounted(() => load())
</script>

<style scoped>
.club-card {
  margin-bottom: 16px;
  cursor: pointer;
}
.club-head {
  display: flex;
  align-items: center;
  gap: 12px;
}
.club-logo {
  background: #409eff;
  color: #fff;
  font-size: 20px;
  flex-shrink: 0;
}
.club-name .name {
  font-weight: 600;
  margin-bottom: 4px;
}
.desc {
  color: #909399;
  font-size: 13px;
  height: 40px;
  margin: 10px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.meta {
  display: flex;
  justify-content: space-between;
  color: #606266;
  font-size: 13px;
  align-items: center;
}
.meta span {
  display: inline-flex;
  align-items: center;
  gap: 3px;
}
.join-btn {
  margin-top: 12px;
  width: 100%;
}
</style>