<template>
  <div>
    <el-row :gutter="16">
      <el-col :span="6">
        <stat-card label="用户总数" :value="ov.userCount || 0" icon="User" color="#409eff" />
      </el-col>
      <el-col :span="6">
        <stat-card label="社团总数" :value="ov.clubCount || 0" icon="Flag" color="#67c23a" />
      </el-col>
      <el-col :span="6">
        <stat-card label="活动总数" :value="ov.activityCount || 0" icon="Calendar" color="#e6a23c" />
      </el-col>
      <el-col :span="6">
        <stat-card label="公告总数" :value="ov.noticeCount || 0" icon="Bell" color="#f56c6c" />
      </el-col>
    </el-row>

    <el-row :gutter="16" class="charts">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>各社团成员数排行</template>
          <e-chart :option="barOption" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>各社团活动数量分布</template>
          <e-chart :option="pieOption" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { overview, clubMemberRanking, activityByClub } from '../api/stats'
import StatCard from '../components/StatCard.vue'
import EChart from '../components/EChart.vue'

const ov = ref({})
const ranking = ref([])
const byClub = ref([])

const barOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: 40, right: 20, top: 30, bottom: 60 },
  xAxis: {
    type: 'category',
    data: ranking.value.map((i) => i.name),
    axisLabel: { interval: 0, rotate: 20 }
  },
  yAxis: { type: 'value', minInterval: 1 },
  series: [
    {
      name: '成员数',
      type: 'bar',
      barMaxWidth: 40,
      itemStyle: { color: '#409eff', borderRadius: [4, 4, 0, 0] },
      data: ranking.value.map((i) => i.value)
    }
  ]
}))

const pieOption = computed(() => ({
  tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
  legend: { bottom: 0, type: 'scroll' },
  series: [
    {
      name: '活动数量',
      type: 'pie',
      radius: ['38%', '62%'],
      center: ['50%', '44%'],
      label: { formatter: '{b}: {c}' },
      data: byClub.value.map((i) => ({ name: i.name, value: i.value }))
    }
  ]
}))

onMounted(async () => {
  const [a, b, c] = await Promise.all([
    overview(),
    clubMemberRanking(),
    activityByClub()
  ])
  ov.value = a.data
  ranking.value = b.data || []
  byClub.value = c.data || []
})
</script>

<style scoped>
.charts {
  margin-top: 16px;
}
</style>