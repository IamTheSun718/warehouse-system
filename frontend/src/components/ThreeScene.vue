<template>
  <div id="container" style="width:100%;height:100vh;"></div>
</template>

<script setup>
import { onMounted, onBeforeUnmount } from 'vue'
import * as THREE from 'three'
import { OrbitControls } from 'three/addons/controls/OrbitControls.js'
import { CSS2DRenderer, CSS2DObject } from 'three/addons/renderers/CSS2DRenderer.js'

const emit = defineEmits(['updateCount'])

let scene, camera, renderer, labelRenderer, controls
const goodsMeshes = new Map()
let animationId = null
let ws = null

// 危化品颜色
const dangerColors = {
  '易燃': 0xff4757,
  '腐蚀': 0xffa502,
  '剧毒': 0xa855f7,
  '普通': 0x4facfe
}

// 货物名称列表
const goodsNames = [
  '甲醇', '乙醇', '丙酮', '苯', '甲苯',
  '硫酸', '盐酸', '硝酸', '氢氧化钠', '氨水',
  '氰化钠', '氰化钾', '砷化物', '汞', '铅盐',
  '乙酸乙酯', '正己烷', '环己烷', '二甲苯', '异丙醇',
  '双氧水', '高锰酸钾', '重铬酸钾', '次氯酸钠', '甲醛'
]

// 生成随机货物数据
function generateGoodsData(count = 30) {
  const data = []
  const categories = ['易燃', '腐蚀', '剧毒', '普通']

  for (let i = 0; i < count; i++) {
    const category = categories[Math.floor(Math.random() * categories.length)]
    // 在仓库范围内随机分布，避免重叠
    let x, y, attempts = 0
    do {
      x = 8 + Math.random() * 84  // 8 ~ 92
      y = 8 + Math.random() * 44  // 8 ~ 52
      attempts++
    } while (attempts < 20 && data.some(d => Math.abs(d.x - x) < 4 && Math.abs(d.y - y) < 4))

    data.push({
      goodsId: `G-${String(i + 1).padStart(3, '0')}`,
      goodsName: goodsNames[i % goodsNames.length] + (i > goodsNames.length ? `-${Math.floor(i/goodsNames.length)+1}` : ''),
      x: x,
      y: y,
      z: 0.5 + Math.random() * 2.5,  // 高度 0.5~3
      dangerLevel: category,
      status: Math.random() > 0.9 ? '预警' : '正常',
      size: 0.8 + Math.random() * 1.2  // 大小 0.8~2.0
    })
  }
  return data
}

// 所有货物数据
let allGoodsData = generateGoodsData(35)

// ============ 更新货物 ============
function updateGoods(positions) {
  if (!positions || !Array.isArray(positions)) return

  let alertCount = 0
  const currentIds = new Set()

  // 如果后端数据少于前端数据，用前端模拟数据补充
  let displayData = positions
  if (positions.length < allGoodsData.length) {
    // 合并数据：后端数据覆盖模拟数据
    const merged = [...allGoodsData]
    positions.forEach((pos, idx) => {
      if (idx < merged.length) {
        merged[idx].x = pos.x || merged[idx].x
        merged[idx].y = pos.y || merged[idx].y
        merged[idx].status = pos.status || merged[idx].status
        merged[idx].dangerLevel = pos.dangerLevel || merged[idx].dangerLevel
      }
    })
    displayData = merged
  }

  displayData.forEach((pos, index) => {
    const id = pos.goodsId
    currentIds.add(id)
    const color = dangerColors[pos.dangerLevel] || 0x4facfe
    const floatOffset = Math.sin(Date.now() / 2000 + index * 0.5) * 0.2
    const size = pos.size || 1.0

    if (goodsMeshes.has(id)) {
      const group = goodsMeshes.get(id)
      group.position.set(pos.x, pos.z + floatOffset, pos.y)

      const isAlert = pos.status === '预警'
      const mesh = group.children[0]
      if (isAlert) {
        mesh.material.color.setHex(0xff0000)
        mesh.material.emissive.setHex(0xff0000)
        mesh.material.emissiveIntensity = 0.5
        alertCount++
      } else {
        mesh.material.color.setHex(color)
        mesh.material.emissive.setHex(color)
        mesh.material.emissiveIntensity = 0.1
      }

      const label = goodsMeshes.get(id + '_label')
      if (label) {
        label.position.set(pos.x, pos.z + 1.8 + size + floatOffset, pos.y)
      }
    } else {
      // 创建货物组
      const group = new THREE.Group()

      // 主体 - 使用 BoxGeometry 模拟箱子
      const geo = new THREE.BoxGeometry(size, size * 0.7, size)
      const mat = new THREE.MeshStandardMaterial({
        color: color,
        roughness: 0.3,
        metalness: 0.4,
        emissive: color,
        emissiveIntensity: 0.1,
        envMapIntensity: 0.6
      })
      const mesh = new THREE.Mesh(geo, mat)
      mesh.position.y = size * 0.35
      mesh.castShadow = true
      mesh.receiveShadow = true
      group.add(mesh)

      // 顶部边框装饰
      const edgeGeo = new THREE.EdgesGeometry(geo)
      const edgeMat = new THREE.LineBasicMaterial({
        color: 0x88bbff,
        transparent: true,
        opacity: 0.15
      })
      const edge = new THREE.LineSegments(edgeGeo, edgeMat)
      edge.position.y = size * 0.35
      group.add(edge)

      // 发光光晕（底部光晕）
      const glowGeo = new THREE.CircleGeometry(size * 0.8, 16)
      const glowMat = new THREE.MeshBasicMaterial({
        color: color,
        transparent: true,
        opacity: 0.08,
        side: THREE.DoubleSide
      })
      const glow = new THREE.Mesh(glowGeo, glowMat)
      glow.rotation.x = -Math.PI / 2
      glow.position.y = 0.05
      group.add(glow)

      group.position.set(pos.x, pos.z, pos.y)
      scene.add(group)
      goodsMeshes.set(id, group)

      // 标签
      const labelDiv = document.createElement('div')
      labelDiv.textContent = pos.goodsName || id
      labelDiv.style.color = '#fff'
      labelDiv.style.fontSize = '11px'
      labelDiv.style.fontWeight = '500'
      labelDiv.style.textShadow = '0 0 20px rgba(0,0,0,0.9)'
      labelDiv.style.background = 'rgba(0,0,0,0.6)'
      labelDiv.style.padding = '2px 10px'
      labelDiv.style.borderRadius = '4px'
      labelDiv.style.border = '1px solid rgba(255,255,255,0.08)'
      labelDiv.style.backdropFilter = 'blur(4px)'
      labelDiv.style.fontSize = '11px'
      labelDiv.style.pointerEvents = 'none'
      const label = new CSS2DObject(labelDiv)
      label.position.set(pos.x, pos.z + 1.8 + size + 0.2, pos.y)
      scene.add(label)
      goodsMeshes.set(id + '_label', label)

      if (pos.status === '预警') alertCount++
    }
  })

  // 移除不存在的货物
  for (const [key] of goodsMeshes) {
    const id = key.replace(/_label$/, '')
    if (!currentIds.has(id) && !key.includes('_')) {
      const obj = goodsMeshes.get(key)
      scene.remove(obj)
      goodsMeshes.delete(key)
    }
  }

  emit('updateCount', { total: currentIds.size, alerts: alertCount })
}

// ============ WebSocket 连接 ============
function connectWebSocket() {
  const wsUrl = `ws://localhost:8080/ws/positions`
  try {
    ws = new WebSocket(wsUrl)
    ws.onopen = () => {
      console.log('✅ WebSocket 已连接')
      // 立即发送一次请求获取数据
      setTimeout(() => {
        ws.send('getData')
      }, 100)
    }
    ws.onmessage = (e) => {
      try {
        const data = JSON.parse(e.data)
        if (Array.isArray(data) && data.length > 0) {
          updateGoods(data)
        }
      } catch (err) {
        console.error('解析数据出错', err)
      }
    }
    ws.onclose = () => {
      console.log('WebSocket 断开，3秒后重连...')
      setTimeout(connectWebSocket, 3000)
    }
    ws.onerror = () => {
      ws.close()
    }
  } catch (e) {
    console.error('WebSocket 连接失败', e)
    // 使用模拟数据
    setTimeout(() => {
      updateGoods(allGoodsData)
    }, 500)
  }
}

// ============ 场景初始化 ============
function initScene() {
  const container = document.getElementById('container')
  const width = container.clientWidth
  const height = container.clientHeight

  scene = new THREE.Scene()
  scene.background = new THREE.Color(0x0a0a1a)
  scene.fog = new THREE.Fog(0x0a0a1a, 120, 220)

  camera = new THREE.PerspectiveCamera(35, width / height, 0.1, 500)
  camera.position.set(150, 120, 150)
  camera.lookAt(50, 0, 30)

  renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true })
  renderer.setSize(width, height)
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))
  renderer.shadowMap.enabled = true
  renderer.shadowMap.type = THREE.PCFSoftShadowMap
  renderer.toneMapping = THREE.ACESFilmicToneMapping
  renderer.toneMappingExposure = 1.2
  container.appendChild(renderer.domElement)

  labelRenderer = new CSS2DRenderer()
  labelRenderer.setSize(width, height)
  labelRenderer.domElement.style.position = 'absolute'
  labelRenderer.domElement.style.top = '0'
  labelRenderer.domElement.style.left = '0'
  labelRenderer.domElement.style.pointerEvents = 'none'
  container.appendChild(labelRenderer.domElement)

  controls = new OrbitControls(camera, renderer.domElement)
  controls.enableDamping = true
  controls.dampingFactor = 0.08
  controls.target.set(50, 0, 30)
  controls.maxPolarAngle = Math.PI / 2.15
  controls.minDistance = 40
  controls.maxDistance = 350
  controls.autoRotate = true
  controls.autoRotateSpeed = 0.3

  // ========== 灯光系统 ==========
  const ambientLight = new THREE.AmbientLight(0x222244, 0.4)
  scene.add(ambientLight)

  const mainLight = new THREE.DirectionalLight(0xffeedd, 1.8)
  mainLight.position.set(60, 120, 60)
  mainLight.castShadow = true
  mainLight.shadow.mapSize.width = 4096
  mainLight.shadow.mapSize.height = 4096
  mainLight.shadow.camera.near = 0.1
  mainLight.shadow.camera.far = 250
  mainLight.shadow.camera.left = -100
  mainLight.shadow.camera.right = 100
  mainLight.shadow.camera.top = 100
  mainLight.shadow.camera.bottom = -100
  mainLight.shadow.bias = -0.001
  scene.add(mainLight)

  const fillLight = new THREE.DirectionalLight(0x4488ff, 0.3)
  fillLight.position.set(-60, 40, -40)
  scene.add(fillLight)

  const rimLight = new THREE.DirectionalLight(0xff8844, 0.2)
  rimLight.position.set(-30, 80, -100)
  scene.add(rimLight)

  // 半球光
  const hemisphereLight = new THREE.HemisphereLight(0x6666ff, 0x444422, 0.5)
  scene.add(hemisphereLight)

  // 屋顶灯光（点光源阵列）
  for (let i = 0; i < 6; i++) {
    const light = new THREE.PointLight(0x4488ff, 0.3, 60)
    light.position.set(15 + i * 16, 18, 15)
    scene.add(light)
  }
  for (let i = 0; i < 6; i++) {
    const light = new THREE.PointLight(0x4488ff, 0.3, 60)
    light.position.set(15 + i * 16, 18, 45)
    scene.add(light)
  }

  // ========== 星空背景 ==========
  const starsGeometry = new THREE.BufferGeometry()
  const starsCount = 3000
  const starPositions = new Float32Array(starsCount * 3)
  const starColors = new Float32Array(starsCount * 3)
  const starSizes = new Float32Array(starsCount)
  for (let i = 0; i < starsCount; i++) {
    const theta = Math.random() * Math.PI * 2
    const phi = Math.acos(2 * Math.random() - 1)
    const r = 200 + Math.random() * 300
    starPositions[i * 3] = Math.sin(phi) * Math.cos(theta) * r + 50
    starPositions[i * 3 + 1] = Math.cos(phi) * r * 0.3 + 30
    starPositions[i * 3 + 2] = Math.sin(phi) * Math.sin(theta) * r + 30
    const c = 0.3 + Math.random() * 0.7
    starColors[i * 3] = c * (0.8 + Math.random() * 0.2)
    starColors[i * 3 + 1] = c * (0.8 + Math.random() * 0.2)
    starColors[i * 3 + 2] = c
    starSizes[i] = 0.2 + Math.random() * 0.8
  }
  starsGeometry.setAttribute('position', new THREE.BufferAttribute(starPositions, 3))
  starsGeometry.setAttribute('color', new THREE.BufferAttribute(starColors, 3))
  starsGeometry.setAttribute('size', new THREE.BufferAttribute(starSizes, 1))
  const starsMaterial = new THREE.PointsMaterial({
    size: 0.4,
    vertexColors: true,
    transparent: true,
    opacity: 0.6,
    blending: THREE.AdditiveBlending,
    sizeAttenuation: true
  })
  const stars = new THREE.Points(starsGeometry, starsMaterial)
  scene.add(stars)

  // ========== 创建仓库 ==========
  createWarehouse()

  // 生成初始货物（使用模拟数据）
  setTimeout(() => {
    updateGoods(allGoodsData)
  }, 300)

  window.addEventListener('resize', onResize)
  animate()
}

function createWarehouse() {
  // ===== 地面 =====
  const groundGeometry = new THREE.PlaneGeometry(100, 60)
  const groundMaterial = new THREE.MeshStandardMaterial({
    color: 0x14142a,
    roughness: 0.4,
    metalness: 0.6,
    envMapIntensity: 0.3
  })
  const ground = new THREE.Mesh(groundGeometry, groundMaterial)
  ground.rotation.x = -Math.PI / 2
  ground.position.set(50, -0.5, 30)
  ground.receiveShadow = true
  scene.add(ground)

  // ===== 地面网格线（更有科技感） =====
  const gridHelper = new THREE.GridHelper(100, 20, 0x4444aa, 0x222266)
  gridHelper.position.set(50, 0, 30)
  gridHelper.material.transparent = true
  gridHelper.material.opacity = 0.3
  scene.add(gridHelper)

  // ===== 分区 =====
  const zones = [
    { x: 0, z: 0, w: 25, h: 30, color: 0xff4757, label: '🔥 易燃液体区', desc: 'Class 3' },
    { x: 25, z: 0, w: 25, h: 30, color: 0xffa502, label: '🧪 腐蚀品区', desc: 'Class 8' },
    { x: 50, z: 0, w: 25, h: 30, color: 0xa855f7, label: '☠️ 剧毒专区', desc: 'Class 6.1' },
    { x: 75, z: 0, w: 25, h: 30, color: 0x4facfe, label: '📦 普通原料区', desc: 'General' }
  ]

  zones.forEach(zone => {
    // 区域底色
    const geo = new THREE.PlaneGeometry(zone.w, zone.h)
    const mat = new THREE.MeshBasicMaterial({
      color: zone.color,
      transparent: true,
      opacity: 0.06,
      side: THREE.DoubleSide
    })
    const mesh = new THREE.Mesh(geo, mat)
    mesh.position.set(zone.x + zone.w/2, 0.01, zone.z + zone.h/2)
    mesh.rotation.x = -Math.PI / 2
    scene.add(mesh)

    // 发光边框
    const edges = new THREE.EdgesGeometry(geo)
    const lineMat = new THREE.LineBasicMaterial({
      color: zone.color,
      transparent: true,
      opacity: 0.25
    })
    const line = new THREE.LineSegments(edges, lineMat)
    line.position.copy(mesh.position)
    line.rotation.x = -Math.PI / 2
    scene.add(line)

    // 分区3D标牌
    const signGroup = new THREE.Group()

    // 标牌底座
    const poleGeo = new THREE.CylinderGeometry(0.08, 0.12, 2, 8)
    const poleMat = new THREE.MeshStandardMaterial({ color: 0x666688, metalness: 0.8, roughness: 0.2 })
    const pole = new THREE.Mesh(poleGeo, poleMat)
    pole.position.y = 1
    signGroup.add(pole)

    // 标牌面板
    const panelGeo = new THREE.BoxGeometry(3, 0.8, 0.1)
    const panelMat = new THREE.MeshStandardMaterial({
      color: zone.color,
      emissive: zone.color,
      emissiveIntensity: 0.15,
      transparent: true,
      opacity: 0.7,
      metalness: 0.3,
      roughness: 0.4
    })
    const panel = new THREE.Mesh(panelGeo, panelMat)
    panel.position.y = 2.2
    signGroup.add(panel)

    signGroup.position.set(zone.x + zone.w/2, 0, zone.z + zone.h/2)
    scene.add(signGroup)

    // CSS2D 标牌标签
    const labelDiv = document.createElement('div')
    labelDiv.innerHTML = `${zone.label}<br><span style="font-size:10px;color:rgba(255,255,255,0.4);">${zone.desc}</span>`
    labelDiv.style.color = '#fff'
    labelDiv.style.fontSize = '14px'
    labelDiv.style.fontWeight = 'bold'
    labelDiv.style.textAlign = 'center'
    labelDiv.style.textShadow = '0 0 20px rgba(0,0,0,0.9)'
    labelDiv.style.background = `rgba(0,0,0,0.5)`
    labelDiv.style.padding = '4px 12px'
    labelDiv.style.borderRadius = '6px'
    labelDiv.style.backdropFilter = 'blur(8px)'
    labelDiv.style.border = `1px solid rgba(${parseInt(zone.color >> 16 & 255)}, ${parseInt(zone.color >> 8 & 255)}, ${parseInt(zone.color & 255)}, 0.3)`
    labelDiv.style.lineHeight = '1.6'
    const label = new CSS2DObject(labelDiv)
    label.position.set(zone.x + zone.w/2, 3.2, zone.z + zone.h/2)
    scene.add(label)
  })

  // ===== 货架系统（多层立体货架） =====
  const shelfPositions = [
    // 第一排货架 (x, z)
    [12, 8], [25, 8], [38, 8], [51, 8], [64, 8], [77, 8], [90, 8],
    [12, 22], [25, 22], [38, 22], [51, 22], [64, 22], [77, 22], [90, 22],
    [12, 36], [25, 36], [38, 36], [51, 36], [64, 36], [77, 36], [90, 36],
    [12, 50], [25, 50], [38, 50], [51, 50], [64, 50], [77, 50], [90, 50]
  ]

  shelfPositions.forEach(([x, z]) => {
    const shelfGroup = new THREE.Group()

    // 货架立柱
    const pillarMat = new THREE.MeshStandardMaterial({
      color: 0x334466,
      metalness: 0.7,
      roughness: 0.3,
      transparent: true,
      opacity: 0.5
    })

    // 4根立柱
    const pillarPositions = [
      [-1.2, -0.8], [1.2, -0.8], [-1.2, 0.8], [1.2, 0.8]
    ]
    pillarPositions.forEach(([px, pz]) => {
      const pillar = new THREE.Mesh(new THREE.BoxGeometry(0.08, 2.5, 0.08), pillarMat)
      pillar.position.set(px, 1.25, pz)
      shelfGroup.add(pillar)
    })

    // 层板（3层）
    const shelfMat = new THREE.MeshStandardMaterial({
      color: 0x445577,
      metalness: 0.5,
      roughness: 0.4,
      transparent: true,
      opacity: 0.2
    })
    for (let level = 0; level < 3; level++) {
      const y = 0.3 + level * 0.9
      const plank = new THREE.Mesh(new THREE.BoxGeometry(2.6, 0.05, 1.8), shelfMat)
      plank.position.set(0, y, 0)
      plank.receiveShadow = true
      shelfGroup.add(plank)

      // 层板边缘光带
      const edgeMat2 = new THREE.LineBasicMaterial({
        color: 0x4488ff,
        transparent: true,
        opacity: 0.05
      })
      const edgeGeo2 = new THREE.EdgesGeometry(new THREE.BoxGeometry(2.6, 0.05, 1.8))
      const edge2 = new THREE.LineSegments(edgeGeo2, edgeMat2)
      edge2.position.set(0, y, 0)
      shelfGroup.add(edge2)
    }

    shelfGroup.position.set(x, 0, z)
    scene.add(shelfGroup)
  })

  // ===== 仓库墙壁 =====
  const wallMat = new THREE.MeshStandardMaterial({
    color: 0x1a1a3a,
    transparent: true,
    opacity: 0.3,
    roughness: 0.6,
    metalness: 0.3,
    side: THREE.DoubleSide
  })

  // 后墙
  const wallGeo = new THREE.PlaneGeometry(100, 8)
  const wall1 = new THREE.Mesh(wallGeo, wallMat)
  wall1.position.set(50, 4, -0.5)
  scene.add(wall1)

  // 左墙
  const wall2 = new THREE.Mesh(new THREE.PlaneGeometry(60, 8), wallMat)
  wall2.rotation.y = Math.PI / 2
  wall2.position.set(-0.5, 4, 30)
  scene.add(wall2)

  // 右墙
  const wall3 = new THREE.Mesh(new THREE.PlaneGeometry(60, 8), wallMat)
  wall3.rotation.y = -Math.PI / 2
  wall3.position.set(100.5, 4, 30)
  scene.add(wall3)

  // ===== 屋顶横梁 =====
  const beamMat = new THREE.MeshStandardMaterial({
    color: 0x2a2a4a,
    transparent: true,
    opacity: 0.2,
    metalness: 0.5,
    roughness: 0.3
  })
  for (let i = 0; i < 10; i++) {
    const beam = new THREE.Mesh(new THREE.BoxGeometry(100, 0.1, 0.15), beamMat)
    beam.position.set(50, 7.5, 2 + i * 6.2)
    scene.add(beam)
  }
  for (let i = 0; i < 10; i++) {
    const beam = new THREE.Mesh(new THREE.BoxGeometry(0.15, 0.1, 60), beamMat)
    beam.position.set(2 + i * 10.7, 7.5, 30)
    scene.add(beam)
  }

  // ===== 叉车（简单模型） =====
  createForklift(20, 55)
  createForklift(85, 15)
}

function createForklift(x, z) {
  const group = new THREE.Group()

  // 车身
  const bodyMat = new THREE.MeshStandardMaterial({
    color: 0xff6633,
    roughness: 0.5,
    metalness: 0.3
  })
  const body = new THREE.Mesh(new THREE.BoxGeometry(1.2, 0.6, 1.6), bodyMat)
  body.position.y = 0.3
  body.castShadow = true
  group.add(body)

  // 驾驶室
  const cabMat = new THREE.MeshStandardMaterial({
    color: 0x4488cc,
    roughness: 0.3,
    metalness: 0.5,
    transparent: true,
    opacity: 0.6
  })
  const cab = new THREE.Mesh(new THREE.BoxGeometry(0.7, 0.4, 0.7), cabMat)
  cab.position.set(0, 0.8, -0.2)
  group.add(cab)

  // 叉臂
  const forkMat = new THREE.MeshStandardMaterial({
    color: 0x888899,
    metalness: 0.8,
    roughness: 0.2
  })
  const fork1 = new THREE.Mesh(new THREE.BoxGeometry(0.08, 0.04, 0.8), forkMat)
  fork1.position.set(0.3, 0.1, 1.0)
  group.add(fork1)
  const fork2 = new THREE.Mesh(new THREE.BoxGeometry(0.08, 0.04, 0.8), forkMat)
  fork2.position.set(-0.3, 0.1, 1.0)
  group.add(fork2)

  // 轮子
  const wheelMat = new THREE.MeshStandardMaterial({
    color: 0x222222,
    roughness: 0.9
  })
  const wheelPos = [[-0.5, 0.1, 0.5], [0.5, 0.1, 0.5], [-0.5, 0.1, -0.5], [0.5, 0.1, -0.5]]
  wheelPos.forEach(([wx, wy, wz]) => {
    const wheel = new THREE.Mesh(new THREE.CylinderGeometry(0.15, 0.15, 0.1, 8), wheelMat)
    wheel.rotation.z = Math.PI / 2
    wheel.position.set(wx, wy, wz)
    group.add(wheel)
  })

  group.position.set(x, 0, z)
  group.rotation.y = Math.random() * Math.PI * 2
  scene.add(group)
}

function animate() {
  animationId = requestAnimationFrame(animate)
  controls.update()
  renderer.render(scene, camera)
  labelRenderer.render(scene, camera)
}

function onResize() {
  const container = document.getElementById('container')
  const width = container.clientWidth
  const height = container.clientHeight
  camera.aspect = width / height
  camera.updateProjectionMatrix()
  renderer.setSize(width, height)
  labelRenderer.setSize(width, height)
}

onMounted(() => {
  initScene()
  connectWebSocket()
})

onBeforeUnmount(() => {
  if (animationId) cancelAnimationFrame(animationId)
  if (ws) {
    ws.onclose = null
    ws.close()
  }
  window.removeEventListener('resize', onResize)
  renderer?.dispose()
  if (labelRenderer?.domElement && labelRenderer.domElement.parentNode) {
    labelRenderer.domElement.parentNode.removeChild(labelRenderer.domElement)
  }
})
</script>
