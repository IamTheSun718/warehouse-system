<template>
  <div style="padding:24px;height:100%;overflow:auto;">
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:20px;">
      <h2 style="color:#fff;font-weight:300;letter-spacing:1px;">
        📥 入库管理
        <span style="font-size:14px;color:rgba(255,255,255,0.3);font-weight:300;margin-left:12px;">
          共 {{ records.length }} 条
        </span>
      </h2>
      <el-button type="primary" @click="openAddDialog" style="background:linear-gradient(135deg,#00d4ff,#7b68ee);border:none;">
        + 新增入库
      </el-button>
    </div>

    <!-- 表格 -->
    <el-table :data="records" style="width:100%;background:transparent;color:#c0c4d9;" border>
      <el-table-column prop="orderNo" label="入库单号" width="180" />
      <el-table-column prop="materialName" label="物料名称" />
      <el-table-column prop="quantity" label="数量" width="100" align="center" />
      <el-table-column prop="unit" label="单位" width="80" />
      <el-table-column prop="supplier" label="供应商" />
      <el-table-column prop="batchNo" label="批次号" width="120" />
      <el-table-column prop="inboundTime" label="入库时间" width="180">
        <template #default="{ row }">
          {{ new Date(row.inboundTime).toLocaleString() }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === '已入库' ? 'success' : 'warning'" size="small">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增对话框 -->
    <el-dialog v-model="dialogVisible" title="新增入库" width="500px">
      <el-form :model="formData" label-width="80px">
        <el-form-item label="物料">
          <el-select v-model="formData.materialId" placeholder="选择物料" style="width:100%;">
            <el-option v-for="m in materialList" :key="m.id" :label="m.name" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number v-model="formData.quantity" :min="1" style="width:100%;" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="formData.unit" placeholder="如：桶" />
        </el-form-item>
        <el-form-item label="供应商">
          <el-input v-model="formData.supplier" placeholder="供应商名称" />
        </el-form-item>
        <el-form-item label="批次号">
          <el-input v-model="formData.batchNo" placeholder="批次号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRecord">确认入库</el-button>
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
  supplier: '',
  batchNo: ''
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
    const res = await axios.get('/api/inbound')
    records.value = res.data
  } catch (e) {
    console.error('加载入库记录失败', e)
  }
}

const openAddDialog = () => {
  formData.value = { materialId: null, materialName: '', quantity: 1, unit: '', supplier: '', batchNo: '' }
  dialogVisible.value = true
}

const saveRecord = async () => {
  if (!formData.value.materialId) {
    ElMessage.warning('请选择物料')
    return
  }
  const material = materialList.value.find(m => m.id === formData.value.materialId)
  formData.value.materialName = material?.name || ''
  try {
    await axios.post('/api/inbound', formData.value)
    ElMessage.success('入库成功')
    dialogVisible.value = false
    loadRecords()
  } catch (e) {
    ElMessage.error('入库失败')
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
