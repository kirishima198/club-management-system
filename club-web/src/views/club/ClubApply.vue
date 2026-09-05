<template>
  <el-card shadow="never">
    <template #header>申请创建社团</template>
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="90px"
      style="max-width: 560px"
    >
      <el-form-item label="社团名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入社团名称" maxlength="50" show-word-limit />
      </el-form-item>
      <el-form-item label="社团分类" prop="category">
        <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%">
          <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
        </el-select>
      </el-form-item>
      <el-form-item label="社团简介" prop="description">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="5"
          maxlength="500"
          show-word-limit
          placeholder="介绍一下社团的宗旨、活动内容等，将展示在社团广场"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="submitting" @click="submit">提交申请</el-button>
        <el-button @click="$router.push('/clubs/my')">查看我的社团</el-button>
      </el-form-item>
    </el-form>
    <el-alert
      title="提交后将进入待审核状态，管理员审核通过后你将成为该社团社长"
      type="info"
      :closable="false"
      style="max-width: 560px"
    />
  </el-card>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { applyClub } from '../../api/club'

const categories = ['文艺', '体育', '学术', '科技', '公益', '其他']
const formRef = ref()
const submitting = ref(false)
const form = reactive({ name: '', category: '', description: '' })

const rules = {
  name: [{ required: true, message: '请输入社团名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  description: [{ required: true, message: '请填写社团简介', trigger: 'blur' }]
}

async function submit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    await applyClub({
      name: form.name,
      category: form.category,
      description: form.description
    })
    ElMessage.success('申请已提交，等待管理员审核')
    formRef.value.resetFields()
  } finally {
    submitting.value = false
  }
}
</script>