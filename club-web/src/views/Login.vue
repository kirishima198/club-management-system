<template>
  <div class="login-page">
    <el-card class="login-card">
      <div class="title">
        <el-icon :size="34" color="#409eff"><School /></el-icon>
        <h2>高校学生社团管理系统</h2>
        <p>College Student Club Management System</p>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" size="large" @keyup.enter="submit">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名 / 学号" :prefix-icon="User" clearable />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" :prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" class="submit-btn" @click="submit">登 录</el-button>
        </el-form-item>
        <div class="tips">
          还没有账号？<router-link to="/register">立即注册</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/user'

const router = useRouter()
const route = useRoute()
const store = useUserStore()

const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function submit() {
  await formRef.value.validate()
  loading.value = true
  try {
    await store.login(form)
    ElMessage.success('登录成功')
    router.push(route.query.redirect || '/dashboard')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1f3b8f 0%, #3f87f5 50%, #6fb1fc 100%);
}
.login-card {
  width: 400px;
  padding: 10px 10px 0;
}
.title {
  text-align: center;
  margin-bottom: 24px;
}
.title h2 {
  margin: 10px 0 4px;
  font-size: 20px;
}
.title p {
  margin: 0;
  color: #909399;
  font-size: 12px;
}
.submit-btn {
  width: 100%;
}
.tips {
  text-align: center;
  color: #909399;
  font-size: 13px;
  padding-bottom: 8px;
}
</style>