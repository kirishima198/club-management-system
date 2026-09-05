<template>
  <el-card shadow="never">
    <div class="toolbar">
      <el-input
        v-model="query.keyword"
        placeholder="用户名/姓名"
        clearable
        style="width: 200px"
        @keyup.enter="load(1)"
        @clear="load(1)"
      />
      <el-select v-model="query.role" placeholder="角色" clearable style="width: 140px" @change="load(1)">
        <el-option label="管理员" value="admin" />
        <el-option label="社长" value="leader" />
        <el-option label="学生" value="student" />
      </el-select>
      <el-select v-model="query.status" placeholder="状态" clearable style="width: 120px" @change="load(1)">
        <el-option label="启用" :value="1" />
        <el-option label="禁用" :value="0" />
      </el-select>
      <el-button type="primary" :icon="Search" @click="load(1)">搜索</el-button>
      <el-button type="success" :icon="Plus" @click="openDialog()">新增用户</el-button>
    </div>

    <el-table v-loading="loading" :data="records" border stripe>
      <el-table-column prop="id" label="ID" width="64" />
      <el-table-column prop="username" label="用户名" min-width="110" />
      <el-table-column prop="nickname" label="姓名" min-width="90" />
      <el-table-column label="角色" width="90" align="center">
        <template #default="{ row }">
          <el-tag :type="row.role === 'admin' ? 'danger' : row.role === 'leader' ? 'warning' : 'info'">
            {{ roleText(row.role) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="email" label="邮箱" min-width="150" show-overflow-tooltip />
      <el-table-column prop="phone" label="手机号" width="120" />
      <el-table-column label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-switch
            :model-value="row.status === 1"
            @change="(v) => toggleStatus(row, v)"
          />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160" />
      <el-table-column label="操作" width="230" fixed="right" align="center">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" type="warning" plain @click="resetPwd(row)">重置密码</el-button>
          <el-popconfirm title="确定删除该用户吗？" @confirm="del(row)">
            <template #reference>
              <el-button size="small" type="danger" plain>删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <Pagination :total="total" :current="query.page" :size="query.size" @change="onPageChange" />

    <el-dialog v-model="dialogVisible" :title="editing ? '编辑用户' : '新增用户'" width="480px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="editing" placeholder="登录名/学号" />
        </el-form-item>
        <el-form-item label="姓名" prop="nickname">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item v-if="!editing" label="初始密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="默认 123456" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" style="width: 100%">
            <el-option label="管理员" value="admin" />
            <el-option label="社长" value="leader" />
            <el-option label="学生" value="student" />
          </el-select>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="启用" />
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
import { reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import { pageUsers, addUser, updateUser, deleteUser, resetPassword, updateStatus } from '../../api/user'
import Pagination from '../../components/Pagination.vue'

const records = ref([])
const total = ref(0)
const loading = ref(false)
const saving = ref(false)
const query = reactive({ page: 1, size: 10, keyword: '', role: null, status: null })

const dialogVisible = ref(false)
const editing = ref(false)
const editId = ref(null)
const formRef = ref()
const form = reactive({ username: '', nickname: '', password: '', role: 'student', email: '', phone: '', status: 1 })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

function roleText(role) {
  return { admin: '管理员', leader: '社长', student: '学生' }[role] || role
}

async function load(page) {
  if (page) query.page = page
  loading.value = true
  try {
    const res = await pageUsers({
      page: query.page,
      size: query.size,
      keyword: query.keyword || undefined,
      role: query.role || undefined,
      status: query.status === '' || query.status == null ? undefined : query.status
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

function openDialog(row) {
  editing.value = !!row
  editId.value = row ? row.id : null
  Object.assign(form, {
    username: row ? row.username : '',
    nickname: row ? row.nickname : '',
    password: '',
    role: row ? row.role : 'student',
    email: row ? row.email || '' : '',
    phone: row ? row.phone || '' : '',
    status: row ? row.status : 1
  })
  dialogVisible.value = true
}

async function save() {
  await formRef.value.validate()
  saving.value = true
  try {
    const payload = {
      username: form.username,
      nickname: form.nickname,
      role: form.role,
      email: form.email || null,
      phone: form.phone || null,
      status: form.status
    }
    if (editing.value) {
      await updateUser(editId.value, payload)
      ElMessage.success('修改成功')
    } else {
      await addUser({ ...payload, password: form.password || '123456' })
      ElMessage.success('新增成功，初始密码 ' + (form.password || '123456'))
    }
    dialogVisible.value = false
    load()
  } finally {
    saving.value = false
  }
}

async function toggleStatus(row, enabled) {
  await updateStatus(row.id, enabled ? 1 : 0)
  ElMessage.success(enabled ? '已启用' : '已禁用')
  load()
}

async function resetPwd(row) {
  await resetPassword(row.id)
  ElMessage.success('已重置为 123456')
}

async function del(row) {
  await deleteUser(row.id)
  ElMessage.success('删除成功')
  load()
}

onMounted(() => load())
</script>