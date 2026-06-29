<template>
  <div style="padding:24px;height:100%;overflow:auto;">
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:20px;">
      <h2 style="color:#fff;font-weight:300;letter-spacing:1px;">
        📦 物料管理
        <span style="font-size:14px;color:rgba(255,255,255,0.3);font-weight:300;margin-left:12px;">
          共 {{ materials.length }} 条
        </span>
      </h2>
      <el-button type="primary" @click="openAddDialog" style="background:linear-gradient(135deg,#00d4ff,#7b68ee);border:none;">
        + 新增物料
      </el-button>
    </div>

    <el-card style="background:rgba(255,255,255,0.03);border:1px solid rgba(255,255,255,0.06);margin-bottom:16px;">
      <el-row :gutter="16">
        <el-col :span="6">
          <el-input v-model="searchKey" placeholder="搜索物料名称/CAS号" @input="loadMaterials" />
        </el-col>
        <el-col :span="6">
          <el-select v-model="searchCategory" placeholder="危化分类" style="width:100%;" @change="loadMaterials">
            <el-option label="全部" value="" />
            <el-option label="易燃" value="易燃" />
            <el-option label="腐蚀" value="腐蚀" />
            <el-option label="剧毒" value="剧毒" />
            <el-option label="普通" value="普通" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-button plain @click="resetSearch">重置</el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-table :data="materials" style="width:100%;background:transparent;color:#c0c4d9;" border>
      <el-table-column prop="code" label="编码" width="140" />
      <el-table-column prop="name" label="物料名称" />
      <el-table-column prop="cas" label="CAS号" width="150" />
      <el-table-column prop="category" label="危化分类" width="120">
        <template #default="{ row }">
          <el-tag :type="categoryTag(row.category)" size="small">{{ row.category }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="100" align="center" />
      <el-table-column prop="unit" label="单位" width="80" />
      <el-table-column label="操作" width="180" align="center">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="物料编码">
          <el-input v-model="formData.code" />
        </el-form-item>
        <el-form-item label="物料名称">
          <el-input v-model="formData.name" />
        </el-form-item>
        <el-form-item label="CAS号">
          <el-input v-model="formData.cas" />
        </el-form-item>
        <el-form-item label="危化分类">
          <el-select v-model="formData.category">
            <el-option label="易燃" value="易燃" />
            <el-option label="腐蚀" value="腐蚀" />
            <el-option label="剧毒" value="剧毒" />
            <el-option label="普通" value="普通" />
          </el-select>
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="formData.unit" placeholder="如：桶、吨" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="formData.stock" :min="0" />
        </el-form-item>
        <el-form-item label="安全说明">
          <el-input v-model="formData.safetyDesc" type="textarea" :rows="2" placeholder="存储要求、禁配物等" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveMaterial">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const materials = ref([])
const searchKey = ref('')
const searchCategory = ref('')
const dialogVisible = ref(false)
const dialogTitle = ref('新增物料')
const isEdit = ref(false)
const editId = ref(null)

const formData = ref({
  code: '',
  name: '',
  cas: '',
  category: '',
  unit: '',
  stock: 0,
  safetyDesc: ''
})

const loadMaterials = async () => {
  try {
    const params = {}
    if (searchKey.value) params.keyword = searchKey.value
    if (searchCategory.value) params.category = searchCategory.value
    const res = await axios.get('/api/materials', { params })
    materials.value = res.data
    console.log('✅ 物料数据加载成功:', materials.value.length, '条')
  } catch (e) {
    console.error('❌ 加载物料失败:', e)
    ElMessage.error('加载物料失败，请检查后端是否启动')
  }
}

const categoryTag = (category) => {
  const map = { '易燃': 'danger', '腐蚀': 'warning', '剧毒': 'info', '普通': 'success' }
  return map[category] || ''
}

const resetSearch = () => {
  searchKey.value = ''
  searchCategory.value = ''
  loadMaterials()
}

const openAddDialog = () => {
  isEdit.value = false
  dialogTitle.value = '新增物料'
  formData.value = { code: '', name: '', cas: '', category: '', unit: '', stock: 0, safetyDesc: '' }
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  isEdit.value = true
  editId.value = row.id
  dialogTitle.value = '编辑物料'
  formData.value = { ...row }
  dialogVisible.value = true
}

const saveMaterial = async () => {
  if (!formData.value.name) {
    ElMessage.warning('请填写物料名称')
    return
  }
  try {
    if (isEdit.value) {
      await axios.put(`/api/materials/${editId.value}`, formData.value)
      ElMessage.success('更新成功')
    } else {
      await axios.post('/api/materials', formData.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadMaterials()
  } catch (e) {
    console.error('❌ 保存失败:', e)
    ElMessage.error('操作失败，请检查控制台错误')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除物料「${row.name}」吗？`, '删除确认', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await axios.delete(`/api/materials/${row.id}`)
      ElMessage.success('删除成功')
      loadMaterials()
    } catch (e) {
      console.error('❌ 删除失败:', e)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  loadMaterials()
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
.el-input :deep(.el-input__wrapper) {
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(255,255,255,0.06);
  box-shadow: none;
}
.el-select :deep(.el-input__wrapper) {
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(255,255,255,0.06);
  box-shadow: none;
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
:deep(.el-dialog .el-input-number .el-input__wrapper) {
  background: rgba(255,255,255,0.05);
}
</style>
