<template>
  <el-card shadow="never">
    <el-tabs v-model="tab">
      <el-tab-pane label="我创建的" name="created">
        <el-empty v-if="!loading && createdList.length === 0" description="还没有创建社团">
          <el-button type="primary" @click="$router.push('/clubs/apply')">去申请创建</el-button>
        </el-empty>
        <el-table v-else v-loading="loading" :data="createdList" border stripe>
          <el-table-column prop="id" label="ID" width="64" />
          <el-table-column prop="name" label="社团名称" min-width="140" />
          <el-table-column prop="category" label="分类" width="90" align="center" />
          <el-table-column prop="memberCount" label="成员数" width="90" align="center" />
          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="statusTag(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="rejectReason" label="驳回原因" min-width="130" show-overflow-tooltip />
          <el-table-column label="操作" width="300" fixed="right" align="center">
            <template #default="{ row }">
              <el-button size="small" @click="$router.push('/clubs/' + row.id)">详情</el-button>
              <el-button
                v-if="row.status === 1"
                size="small"
                type="primary"
                plain
                @click="$router.push(`/clubs/${row.id}/members`)"
              >
                成员管理
              </el-button>
              <el-button v-if="row.status === 1" size="small" @click="openEdit(row)">编辑</el-button>
              <el-popconfirm title="确定解散（删除）该社团吗？" @confirm="del(row)">
                <template #reference>
                  <el-button size="small" type="danger" plain>删除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="我加入的" name="joined">
        <el-empty v-if="!loading && joinedList.length === 0" description="还没有加入任何社团">
          <el-button type="primary" @click="$router.push('/clubs')">去社团广场看看</el-button>
        </el-empty>
        <el-table v-else v-loading="loading" :data="joinedList" border stripe>
          <el-table-column prop="id" label="ID" width="64" />
          <el-table-column prop="name" label="社团名称" min-width="140" />
          <el-table-column prop="category" label="分类" width="90" align="center" />
          <el-table-column prop="presidentName" label="社长" width="110" />
          <el-table-column prop="memberCount" label="成员数" width="90" align="center" />
          <el-table-column prop="joinTime" label="加入时间" width="160" />
          <el-table-column label="操作" width="140" fixed="right" align="center">
            <template #default="{ row }">
              <el-button size="small" @click="$router.push('/clubs/' + row.id)">详情</el-button>
              <el-popconfirm title="确定退出该社团吗？" @confirm="quit(row)">
                <template #reference>
                  <el-button size="small" type="warning" plain>退出</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="editVisible" title="编辑社团信息" width="520px">
      <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="90px">
        <el-form-item label="社团名称" prop="name">
          <el-input v-model="editForm.name" maxlength="50" />
        </el-form-item>
        <el-form-item label="社团分类" prop="category">
          <el-select v-model="editForm.category" style="width: 100%">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="社团简介" prop="description">
          <el-input v-model="editForm.description" type="textarea" :rows="4" maxlength="500" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { myCreated, updateClub, deleteClub } from '../../api/club'
import { myJoined, quitClub } from '../../api/member'

const categories = ['文艺', '体育', '学术', '科技', '公益', '其他']
const tab = ref('created')
const loading = ref(false)
const createdList = ref([])
const joinedList = ref([])

const editVisible = ref(false)
const saving = ref(false)
const editId = ref(null)
const editFormRef = ref()
const editForm = reactive({ name: '', category: '', description: '' })
const editRules = {
  name: [{ required: true, message: '请输入社团名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  description: [{ required: true, message: '请填写社团简介', trigger: 'blur' }]
}

function statusText(s) {
  return { 0: '待审核', 1: '已通过', 2: '已驳回' }[s] || '-'
}
function statusTag(s) {
  return { 0: 'warning', 1: 'success', 2: 'danger' }[s] || 'info'
}

async function load() {
  loading.value = true
  try {
    const [a, b] = await Promise.all([myCreated(), myJoined()])
    createdList.value = a.data || []
    joinedList.value = b.data || []
  } finally {
    loading.value = false
  }
}

function openEdit(row) {
  editId.value = row.id
  Object.assign(editForm, {
    name: row.name,
    category: row.category,
    description: row.description
  })
  editVisible.value = true
}

async function save() {
  await editFormRef.value.validate()
  saving.value = true
  try {
    await updateClub(editId.value, { ...editForm })
    ElMessage.success('保存成功')
    editVisible.value = false
    load()
  } finally {
    saving.value = false
  }
}

async function del(row) {
  await deleteClub(row.id)
  ElMessage.success('已删除')
  load()
}

async function quit(row) {
  await quitClub(row.id)
  ElMessage.success('已退出社团')
  load()
}

watch(tab, () => load())

onMounted(load)
</script>