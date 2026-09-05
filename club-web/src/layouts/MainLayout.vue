<template>
  <el-container class="layout">
    <el-aside :width="isCollapse ? '64px' : '210px'" class="aside">
      <div class="logo">
        <el-icon :size="24"><School /></el-icon>
        <span v-show="!isCollapse">社团管理系统</span>
      </div>
      <el-menu
        :default-active="$route.path"
        router
        :collapse="isCollapse"
        :collapse-transition="false"
        background-color="#001529"
        text-color="#a6adb4"
        active-text-color="#ffffff"
        class="menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <template #title>首页概览</template>
        </el-menu-item>
        <el-menu-item v-if="role === 'admin'" index="/users">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
        <el-sub-menu index="club">
          <template #title>
            <el-icon><Flag /></el-icon>
            <span>社团管理</span>
          </template>
          <el-menu-item index="/clubs">社团广场</el-menu-item>
          <el-menu-item v-if="role === 'admin'" index="/clubs/audit">社团审核</el-menu-item>
          <el-menu-item index="/clubs/apply">申请建社</el-menu-item>
          <el-menu-item index="/clubs/my">我的社团</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="activity">
          <template #title>
            <el-icon><Calendar /></el-icon>
            <span>活动管理</span>
          </template>
          <el-menu-item index="/activities">活动列表</el-menu-item>
          <el-menu-item v-if="role === 'admin' || role === 'leader'" index="/activities/manage">活动管理</el-menu-item>
          <el-menu-item index="/activities/my">我的报名</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/notices">
          <el-icon><Bell /></el-icon>
          <template #title>公告通知</template>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Expand v-if="isCollapse" />
            <Fold v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item>首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="$route.meta.title">{{ $route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <el-dropdown @command="onCommand">
          <span class="user-info">
            <el-avatar :size="30" class="avatar">{{ nicknameFirstChar }}</el-avatar>
            <span class="nickname">{{ nickname }}</span>
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '../stores/user'

const router = useRouter()
const store = useUserStore()
const isCollapse = ref(false)

const role = computed(() => store.role)
const nickname = computed(() => (store.userInfo && (store.userInfo.nickname || store.userInfo.username)) || '用户')
const nicknameFirstChar = computed(() => nickname.value.charAt(0))

function onCommand(cmd) {
  if (cmd === 'profile') {
    router.push('/profile')
  } else if (cmd === 'logout') {
    ElMessageBox.confirm('确定退出登录吗？', '提示', { type: 'warning' })
      .then(() => {
        store.logout()
        router.push('/login')
      })
      .catch(() => {})
  }
}
</script>

<style scoped>
.layout {
  height: 100%;
}
.aside {
  background-color: #001529;
  transition: width 0.2s;
  overflow-x: hidden;
}
.logo {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  white-space: nowrap;
}
.menu {
  border-right: none;
}
.header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  z-index: 1;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 14px;
}
.collapse-btn {
  font-size: 20px;
  cursor: pointer;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}
.avatar {
  background-color: #409eff;
  color: #fff;
}
.nickname {
  font-size: 14px;
}
.main {
  padding: 16px;
  overflow-y: auto;
}
</style>