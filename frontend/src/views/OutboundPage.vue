<template>
  <div style="padding:24px;height:100%;overflow:auto;">
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:20px;">
      <h2 style="color:#fff;font-weight:300;letter-spacing:1px;">
        📤 出库管理
        <span style="font-size:14px;color:rgba(255,255,255,0.3);font-weight:300;margin-left:12px;">
          共 {{ records.length }} 条
        </span>
      </h2>
      <el-button type="primary" @click="openAddDialog" style="background:linear-gradient(135deg,#00d4ff,#7b68ee);border:none;">
        + 新增出库
      </el-button>
    </div>

    <el-table :data="records" style="width:100%;background:transparent;color:#c0c4d9;" border>
      <el-table-column prop="orderNo" label="出库单号" width="180" />
      <el-table-column prop="materialName" label="物料名称" />
      <el-table-column prop="quantity" label="数量" width="100" align="center" />
      <el-table-column prop="unit" label="单位" width="80" />
      <el-table-column prop="targetDept" label="领用部门" />
      <el-table-column prop="purpose" label="用途" />
      <el-table-column prop="outboundTime" label="出库时间" width="180">
        <template #default="{ row }">
          {{ new Date(row.outboundTime).toLocaleString() }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === '已出库' ? 'success' : 'warning'" size="small">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="新增出库" width="500px">
      <el-form :model="formData" label-width="80px">
        <el-form-item label="物料">
          <el-select v-model="formData.materialId" placeholder="选择物料" style="width:100%;">
            <el-option v-for="m in materialList" :key="m.id" :label="`${m.name} (库存:${m.stock})`" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number v-model="formData.quantity" :min="1" style="width:100%;" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="formData.unit" placeholder="如：桶" />
        </el-form-item>
        <el-form-item label="领用部门">
          <el-input v-model="formData.targetDept" placeholder="领用部门" />
        </el-form-item>
        <el-form-item label="用途">
          <el-input v-model="formData.purpose" placeholder="用途说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRecord">确认出库</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const records = ref([])
const materialList = ref([])
const dialogVisible = ref(false)

const formData = ref({
  materialId: null,
  materialName: '',
  quantity: 1,
  unit: '',
  targetDept: '',
  purpose: ''
})

const loadMaterials = async () => {
  try {
    const res = await axios.get('/api/materials')
    materialList.value = res.data
  } catch (e) {
    console.error('加载物料失败', e)
  }
}

const loadRecords = async () => {
  try {
    const res = await axios.get('/api/outbound')
    records.value = res.data
  } catch (e) {
    console.error('加载出库记录失败', e)
  }
}

const openAddDialog = () => {
  formData.value = { materialId: null, materialName: '', quantity: 1, unit: '', targetDept: '', purpose: '' }
  dialogVisible.value = true
}

const saveRecord = async () => {
  if (!formData.value.materialId) {
    ElMessage.warning('请选择物料')
    return
  }
  const material = materialList.value.find(m => m.id === formData.value.materialId)
  if (material && formData.value.quantity > material.stock) {
    ElMessage.warning('库存不足！当前库存: ' + material.stock)
    return
  }
  formData.value.materialName = material?.name || ''
  try {
    await axios.post('/api/outbound', formData.value)
    ElMessage.success('出库成功')
    dialogVisible.value = false
    loadRecords()
    loadMaterials()
  } catch (e) {
    ElMessage.error('出库失败')
    console.error(e)
  }
}

onMounted(() => {
  loadMaterials()
  loadRecords()
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
:deep(.el-dialog) {
  background: #1a1a2e;
  border: 1px solid rgba(255,255,255,0.06);
}
:deep(.el-dialog__title) {
  color: #fff;
}
:deep(.el-form-item__label) {
  color: rgba(255,255,255,0.7);
}
</style>
