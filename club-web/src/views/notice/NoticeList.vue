<template>
  <el-card shadow="never">
    <div class="toolbar">
      <el-select v-model="clubId" placeholder="按社团筛选" clearable style="width: 200px" @change="load(1)">
        <el-option v-for="c in clubOptions" :key="c.id" :label="c.name" :value="c.id" />
      </el-select>
      <el-button
        v-if="role === 'admin' || role === 'leader'"
        type="primary"
        :icon="Plus"
        @click="openDialog()"
      >
        发布公告
      </el-button>
    </div>

    <div v-loading="loading">
      <el-empty v-if="!loading && records.length === 0" description="暂无公告" />
      <el-timeline v-else style="padding-left: 6px">
        <el-timeline-item
          v-for="n in records"
          :key="n.id"
          :timestamp="n.createTime + ' · ' + (n.clubName ? n.clubName : '校级公告')"
          placement="top"
          :type="n.clubId ? 'primary' : 'warning'"
        >
          <el-card shadow="hover">
            <div class="notice-head">
              <h3 class="notice-title">{{ n.title }}</h3>
              <span v-if="canManageClub(n)" class="notice-actions">
                <el-button size="small" @click="openDialog(n)">编辑</el-button>
                <el-popconfirm title="确定删除该公告吗？" @confirm="del(n)">
                  <template #reference>
                    <el-button size="small" type="danger" plain>删除</el-button>
                  </template>
                </el-popconfirm>
              </span>
            </div>
            <p class="notice-content">{{ n.content }}</p>
            <div class="notice-footer">发布人：{{ n.publisherName || '-' }}</div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </div>

    <el-card shadow="never" v-if="total > 0">
      <Pagination :total="total" :current="page" :size="size" @change="onPageChange" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editing ? '编辑公告' : '发布公告'" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" maxlength="60" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="6" maxlength="1000" show-word-limit />
        </el-form-item>
        <el-form-item v-if="role === 'leader' || role === 'admin'" label="发布范围">
          <el-select v-model="form.clubId" style="width: 100%" placeholder="选择社团（社长仅限本社）">
            <el-option label="校级公告（仅管理员）" :value="null" :disabled="role !== 'admin'" />
            <el-option v-for="c in clubOptions" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { pageNotices, publishNotice, updateNotice, deleteNotice } from '../../api/notice'
import { myCreated } from '../../api/club'
import { myJoined } from '../../api/member'
import { useUserStore } from '../../stores/user'
import Pagination from '../../components/Pagination.vue'

const store = useUserStore()
const role = computed(() => store.role)

const records = ref([])
const total = ref(0)
const loading = ref(false)
const saving = ref(false)
const page = ref(1)
const size = ref(10)
const clubId = ref(null)
const clubOptions = ref([])

const dialogVisible = ref(false)
const editing = ref(false)
const editId = ref(null)
const formRef = ref()
const form = reactive({ title: '', content: '', clubId: null })

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

function canManageClub(n) {
  if (role.value === 'admin') return true
  return role.value === 'leader' && store.userInfo && n.publisherId === store.userInfo.id
}

async function load(p) {
  if (p) page.value = p
  loading.value = true
  try {
    const res = await pageNotices({
      page: page.value,
      size: size.value,
      clubId: clubId.value || undefined
    })
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

function openDialog(row) {
  editing.value = !!row
  editId.value = row ? row.id : null
  form.title = row ? row.title : ''
  form.content = row ? row.content : ''
  form.clubId = row
    ? row.clubId
    : role.value === 'admin'
      ? null
      : (clubOptions.value[0] && clubOptions.value[0].id) || null
  dialogVisible.value = true
}

async function save() {
  await formRef.value.validate()
  saving.value = true
  try {
    if (editing.value) {
      await updateNotice(editId.value, { ...form })
      ElMessage.success('修改成功')
    } else {
      await publishNotice({ ...form })
      ElMessage.success('发布成功')
    }
    dialogVisible.value = false
    load()
  } finally {
    saving.value = false
  }
}

async function del(row) {
  await deleteNotice(row.id)
  ElMessage.success('删除成功')
  load()
}

onMounted(async () => {
  load()
  try {
    if (role.value === 'leader' || role.value === 'admin') {
      const res = await myCreated()
      clubOptions.value = (res.data || []).filter((c) => c.status === 1)
    } else if (role.value === 'student') {
      const res = await myJoined()
      clubOptions.value = res.data || []
    }
  } catch (e) {
    /* 忽略选项加载失败 */
  }
})
</script>

<style scoped>
.notice-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.notice-title {
  margin: 0;
  font-size: 16px;
}
.notice-actions {
  flex-shrink: 0;
}
.notice-content {
  color: #606266;
  white-space: pre-wrap;
  margin: 8px 0;
}
.notice-footer {
  color: #909399;
  font-size: 12px;
}
</style>