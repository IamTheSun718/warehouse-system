<template>
  <div style="width:100%;height:100vh;position:relative;">
    <ThreeScene @update-count="updateStats" />
    <div style="position:absolute;top:20px;left:24px;z-index:10;color:rgba(255,255,255,0.4);font-size:13px;letter-spacing:1px;background:rgba(0,0,0,0.3);padding:8px 16px;border-radius:8px;backdrop-filter:blur(10px);border:1px solid rgba(255,255,255,0.05);">
      <span style="color:#44ff88;font-weight:bold;">●</span> 在线货物: {{ goodsCount }}
      <span style="margin-left:16px;color:#ff4757;font-weight:bold;">●</span> 预警: {{ alertCount }}
      <span style="margin-left:16px;color:rgba(255,255,255,0.3);">|</span>
      <span style="margin-left:16px;">🕐 {{ currentTime }}</span>
    </div>
    <div style="position:absolute;bottom:30px;left:50%;transform:translateX(-50%);z-index:10;color:rgba(255,255,255,0.15);font-size:12px;letter-spacing:2px;background:rgba(0,0,0,0.2);padding:6px 20px;border-radius:20px;backdrop-filter:blur(10px);border:1px solid rgba(255,255,255,0.03);">
      🖱 拖拽旋转 · 滚轮缩放 · 右键平移
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import ThreeScene from '@/components/ThreeScene.vue'

const goodsCount = ref(0)
const alertCount = ref(0)
const currentTime = ref('')

const updateStats = ({ total, alerts }) => {
  goodsCount.value = total
  alertCount.value = alerts
}

let timer = setInterval(() => {
  currentTime.value = new Date().toLocaleTimeString('zh-CN', {
    hour12: false,
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}, 1000)

onMounted(() => {
  console.log('📊 数字大屏已加载')
})

onUnmounted(() => {
  clearInterval(timer)
})
</script>
