<template>
  <el-card shadow="never">
    <template #header>
      <div class="header-bar">
        <span>活动管理</span>
        <el-button type="primary" size="small" :icon="Plus" @click="openDialog()">发布活动</el-button>
      </div>
    </template>

    <el-table v-loading="loading" :data="records" border stripe>
      <el-table-column prop="id" label="ID" width="64" />
      <el-table-column prop="title" label="活动名称" min-width="150" />
      <el-table-column prop="clubName" label="所属社团" min-width="120" />
      <el-table-column label="时间" min-width="220">
        <template #default="{ row }">
          {{ row.startTime }} ~ {{ row.endTime }}
        </template>
      </el-table-column>
      <el-table-column prop="location" label="地点" min-width="110" show-overflow-tooltip />
      <el-table-column label="报名/上限" width="100" align="center">
        <template #default="{ row }">{{ row.signupCount || 0 }}/{{ row.maxParticipants || '不限' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right" align="center">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" type="primary" plain @click="$router.push('/activities/' + row.id)">
            报名名单
          </el-button>
          <el-popconfirm title="确定删除该活动吗？" @confirm="del(row)">
            <template #reference>
              <el-button size="small" type="danger" plain>删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editing ? '编辑活动' : '发布活动'" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="所属社团" prop="clubId">
          <el-select v-model="form.clubId" style="width: 100%" placeholder="选择我管理的社团">
            <el-option v-for="c in myClubs" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="活动名称" prop="title">
          <el-input v-model="form.title" maxlength="60" />
        </el-form-item>
        <el-form-item label="活动介绍" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="活动地点" prop="location">
          <el-input v-model="form.location" maxlength="100" />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="form.startTime"
            type="datetime"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="选择开始时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="form.endTime"
            type="datetime"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="选择结束时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="人数上限" prop="maxParticipants">
          <el-input-number v-model="form.maxParticipants" :min="0" :max="9999" />
          <span class="tip">0 表示不限制</span>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { myPublished, publishActivity, updateActivity, deleteActivity } from '../../api/activity'
import { myCreated } from '../../api/club'

const records = ref([])
const loading = ref(false)
const saving = ref(false)
const myClubs = ref([])

const dialogVisible = ref(false)
const editing = ref(false)
const editId = ref(null)
const formRef = ref()
const emptyForm = () => ({
  clubId: null,
  title: '',
  description: '',
  location: '',
  startTime: '',
  endTime: '',
  maxParticipants: 0
})
const form = reactive(emptyForm())

const rules = {
  clubId: [{ required: true, message: '请选择社团', trigger: 'change' }],
  title: [{ required: true, message: '请输入活动名称', trigger: 'blur' }],
  location: [{ required: true, message: '请输入活动地点', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
}

async function load() {
  loading.value = true
  try {
    const res = await myPublished()
    records.value = res.data || []
  } finally {
    loading.value = false
  }
}

function openDialog(row) {
  editing.value = !!row
  editId.value = row ? row.id : null
  Object.assign(form, emptyForm())
  if (row) {
    Object.assign(form, {
      clubId: row.clubId,
      title: row.title,
      description: row.description || '',
      location: row.location || '',
      startTime: row.startTime,
      endTime: row.endTime,
      maxParticipants: row.maxParticipants || 0
    })
  }
  dialogVisible.value = true
}

async function save() {
  await formRef.value.validate()
  if (form.endTime <= form.startTime) {
    ElMessage.warning('结束时间必须晚于开始时间')
    return
  }
  saving.value = true
  try {
    const payload = { ...form, maxParticipants: form.maxParticipants || 0 }
    if (editing.value) {
      await updateActivity(editId.value, payload)
      ElMessage.success('修改成功')
    } else {
      await publishActivity(payload)
      ElMessage.success('发布成功')
    }
    dialogVisible.value = false
    load()
  } finally {
    saving.value = false
  }
}

async function del(row) {
  await deleteActivity(row.id)
  ElMessage.success('删除成功')
  load()
}

onMounted(async () => {
  load()
  const res = await myCreated()
  myClubs.value = (res.data || []).filter((c) => c.status === 1)
})
</script>

<style scoped>
.header-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.tip {
  margin-left: 10px;
  color: #909399;
  font-size: 12px;
}
</style>