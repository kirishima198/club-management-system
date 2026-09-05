<template>
  <el-row :gutter="16">
    <el-col :span="12">
      <el-card shadow="never">
        <template #header>基本资料</template>
        <el-form ref="infoFormRef" :model="infoForm" :rules="infoRules" label-width="80px">
          <el-form-item label="用户名">
            <el-input v-model="infoForm.username" disabled />
          </el-form-item>
          <el-form-item label="角色">
            <el-tag>{{ roleText }}</el-tag>
          </el-form-item>
          <el-form-item label="姓名" prop="nickname">
            <el-input v-model="infoForm.nickname" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="infoForm.email" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="infoForm.phone" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="saving" @click="saveInfo">保存修改</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </el-col>

    <el-col :span="12">
      <el-card shadow="never">
        <template #header>修改密码</template>
        <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="80px">
          <el-form-item label="原密码" prop="oldPassword">
            <el-input v-model="pwdForm.oldPassword" type="password" show-password />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="pwdForm.newPassword" type="password" show-password />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirm">
            <el-input v-model="pwdForm.confirm" type="password" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="warning" :loading="changing" @click="changePwd">修改密码</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </el-col>
  </el-row>
</template>

<script setup>
import { reactive, ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { me, changePassword, updateProfile } from '../../api/auth'
import { useUserStore } from '../../stores/user'
import { useRouter } from 'vue-router'

const store = useUserStore()
const router = useRouter()

const infoFormRef = ref()
const pwdFormRef = ref()
const saving = ref(false)
const changing = ref(false)

const infoForm = reactive({ username: '', nickname: '', email: '', phone: '' })
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirm: '' })

const infoRules = {
  nickname: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }],
  phone: [{ pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }]
}
const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度 6-20 位', trigger: 'blur' }
  ],
  confirm: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== pwdForm.newPassword) callback(new Error('两次输入的密码不一致'))
        else callback()
      },
      trigger: 'blur'
    }
  ]
}

const roleText = computed(
  () => ({ admin: '管理员', leader: '社长', student: '学生' })[store.role] || store.role
)

async function saveInfo() {
  await infoFormRef.value.validate()
  saving.value = true
  try {
    await updateProfile({
      nickname: infoForm.nickname,
      email: infoForm.email || null,
      phone: infoForm.phone || null
    })
    const res = await me()
    store.setInfo(res.data)
    ElMessage.success('资料已保存')
  } finally {
    saving.value = false
  }
}

async function changePwd() {
  await pwdFormRef.value.validate()
  changing.value = true
  try {
    await changePassword({
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    store.logout()
    router.push('/login')
  } finally {
    changing.value = false
  }
}

onMounted(async () => {
  const res = await me()
  Object.assign(infoForm, {
    username: res.data.username,
    nickname: res.data.nickname,
    email: res.data.email || '',
    phone: res.data.phone || ''
  })
})
</script>