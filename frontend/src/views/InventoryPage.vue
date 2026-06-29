<template>
  <div style="padding:24px;height:100%;overflow:auto;">
    <h2 style="color:#fff;font-weight:300;letter-spacing:1px;margin-bottom:20px;">
      📋 实时库存
    </h2>

    <!-- 统计卡片 -->
    <el-row :gutter="16" style="margin-bottom:20px;">
      <el-col :span="6">
        <el-card style="background:rgba(255,255,255,0.03);border:1px solid rgba(255,255,255,0.06);color:#fff;">
          <div style="font-size:14px;color:rgba(255,255,255,0.4);">总物料数</div>
          <div style="font-size:32px;font-weight:bold;color:#4facfe;">{{ stats.totalMaterials }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card style="background:rgba(255,255,255,0.03);border:1px solid rgba(255,255,255,0.06);color:#fff;">
          <div style="font-size:14px;color:rgba(255,255,255,0.4);">总库存量</div>
          <div style="font-size:32px;font-weight:bold;color:#44ff88;">{{ stats.totalStock }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card style="background:rgba(255,255,255,0.03);border:1px solid rgba(255,255,255,0.06);color:#fff;">
          <div style="font-size:14px;color:rgba(255,255,255,0.4);">易燃品</div>
          <div style="font-size:32px;font-weight:bold;color:#ff4757;">{{ stats.categoryStats?.易燃 || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card style="background:rgba(255,255,255,0.03);border:1px solid rgba(255,255,255,0.06);color:#fff;">
          <div style="font-size:14px;color:rgba(255,255,255,0.4);">剧毒品</div>
          <div style="font-size:32px;font-weight:bold;color:#a855f7;">{{ stats.categoryStats?.剧毒 || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索 -->
    <el-card style="background:rgba(255,255,255,0.03);border:1px solid rgba(255,255,255,0.06);margin-bottom:16px;backdrop-filter:blur(10px);">
      <el-row :gutter="16">
        <el-col :span="6">
          <el-select v-model="filterCategory" placeholder="按危化分类筛选" style="width:100%;background:rgba(255,255,255,0.05);" @change="loadInventory">
            <el-option label="全部" value="" />
            <el-option label="易燃" value="易燃" />
            <el-option label="腐蚀" value="腐蚀" />
            <el-option label="剧毒" value="剧毒" />
            <el-option label="普通" value="普通" />
          </el-select>
        </el-col>
      </el-row>
    </el-card>

    <!-- 表格 -->
    <el-table :data="inventory" style="width:100%;background:transparent;color:#c0c4d9;" border>
      <el-table-column prop="code" label="编码" width="120" />
      <el-table-column prop="name" label="物料名称" />
      <el-table-column prop="cas" label="CAS号" />
      <el-table-column prop="category" label="危化分类" width="100">
        <template #default="{ row }">
          <el-tag :type="categoryTag(row.category)" size="small">{{ row.category }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="100" align="center" />
      <el-table-column prop="unit" label="单位" width="80" />
      <el-table-column label="安全说明" min-width="200">
        <template #default="{ row }">
          <span style="color:rgba(255,255,255,0.5);font-size:13px;">{{ row.safetyDesc || '-' }}</span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const inventory = ref([])
const stats = ref({ totalMaterials: 0, totalStock: 0, categoryStats: {} })
const filterCategory = ref('')

const categoryTag = (category) => {
  const map = { '易燃': 'danger', '腐蚀': 'warning', '剧毒': 'info', '普通': 'success' }
  return map[category] || ''
}

const loadInventory = async () => {
  try {
    const url = filterCategory.value ? `/api/inventory?category=${filterCategory.value}` : '/api/inventory'
    const res = await axios.get(url)
    inventory.value = res.data
  } catch (e) {
    console.error('加载库存失败', e)
  }
}

const loadStats = async () => {
  try {
    const res = await axios.get('/api/inventory/stats')
    stats.value = res.data
  } catch (e) {
    console.error('加载统计失败', e)
  }
}

onMounted(() => {
  loadInventory()
  loadStats()
})
</script>

<style scoped>
.el-table {
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
  --el-table-header-bg-color: rgba(255,255,255,0.03);
  --el-table-row-hover-bg-color: rgba(255,255,255,0.05);
  --el-table-border-color: rgba(255,255,255,0.06);
  --el-table-text-color: #c0c4d9;
  --el-table-header-text-color: #8899bb;
}
</style>
