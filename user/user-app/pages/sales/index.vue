<template>
  <view class="sales-dashboard">
    <view class="hero-header">
      <view class="hero-top">
        <view class="hero-text">
          <text class="greeting">👋 欢迎回来</text>
          <text class="name">{{ salesName }}</text>
        </view>
        <view class="hero-avatar">{{ salesName ? salesName[0] : 'S' }}</view>
      </view>
    </view>

    <view class="stats-row">
      <view class="stat-card" @click="toOrders">
        <text class="stat-icon">📈</text>
        <text class="stat-val">¥{{ todaySales }}</text>
        <text class="stat-lbl">今日销售额</text>
      </view>
      <view class="stat-card" @click="toCommission">
        <text class="stat-icon">💰</text>
        <text class="stat-val">¥{{ monthCommission }}</text>
        <text class="stat-lbl">本月佣金</text>
      </view>
      <view class="stat-card" @click="toOrders">
        <text class="stat-icon">📦</text>
        <text class="stat-val">{{ orderCount }}</text>
        <text class="stat-lbl">推广订单</text>
      </view>
    </view>

    <view class="section">
      <text class="section-title">快捷功能</text>
      <view class="menu-grid">
        <view class="menu-item" @click="toPromotion">
          <view class="menu-icon-box orange"><text class="menu-icon">📢</text></view>
          <text class="menu-label">推广商品</text>
        </view>
        <view class="menu-item" @click="toOrders">
          <view class="menu-icon-box blue"><text class="menu-icon">📋</text></view>
          <text class="menu-label">查看订单</text>
        </view>
        <view class="menu-item" @click="toCommission">
          <view class="menu-icon-box green"><text class="menu-icon">💰</text></view>
          <text class="menu-label">佣金中心</text>
        </view>
        <view class="menu-item" @click="toCustomers">
          <view class="menu-icon-box purple"><text class="menu-icon">👥</text></view>
          <text class="menu-label">客户管理</text>
        </view>
        <view class="menu-item" @click="toTools">
          <view class="menu-icon-box teal"><text class="menu-icon">🔧</text></view>
          <text class="menu-label">推广工具</text>
        </view>
      </view>
    </view>

    <view class="section">
      <text class="section-title">最近推广订单</text>
      <view class="recent-list">
        <view class="recent-item" v-for="o in recentOrders" :key="o.id" @click="toDetail(o.id)">
          <view class="recent-left">
            <text class="recent-sn">{{ o.orderSn }}</text>
            <text class="recent-amount">¥{{ o.totalAmount }}</text>
          </view>
          <view class="recent-right">
            <text class="status-badge" :class="'s' + o.status">{{ statusMap[o.status] }}</text>
            <text class="recent-arrow">›</text>
          </view>
        </view>
        <view v-if="recentOrders.length === 0" class="empty-row">
          <text>暂无推广订单</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import request from '@/utils/request'

const salesName = ref('')
const todaySales = ref('0.00')
const monthCommission = ref('0.00')
const orderCount = ref(0)
const recentOrders = ref([])
const statusMap = { 0: '待付款', 1: '待发货', 2: '待收货', 3: '已完成', 4: '已取消' }

onShow(() => {
  const role = uni.getStorageSync('userRole') || 'USER'
  if (role !== 'SALES' && role !== 'ADMIN') {
    uni.showToast({ title: '权限不足', icon: 'none' })
    return setTimeout(() => uni.reLaunch({ url: '/pages/home/home' }), 1500)
  }
  const userInfo = uni.getStorageSync('userInfo')
  if (userInfo) {
    salesName.value = userInfo.name || userInfo.phone || '分销员'
    loadStats(userInfo.salesId)
  }
  loadRecent()
})

async function loadStats(salesId) {
  try {
    const res = await request({ url: '/api/sales/statistics', data: { salesId } })
    if (res.code === 200) {
      todaySales.value = res.todaySales || '0.00'
      monthCommission.value = res.monthCommission || '0.00'
      orderCount.value = res.orderCount || 0
    }
  } catch (e) { console.log('加载统计失败:', e) }
}

async function loadRecent() {
  try {
    const res = await request({ url: '/api/order/list', data: { userId: 0 } })
    const list = (res.data || []).filter(o => o.salesId).slice(0, 5)
    recentOrders.value = list
  } catch (e) { console.log('加载订单失败:', e) }
}

const toPromotion = () => uni.navigateTo({ url: '/pages/sales/promotion' })
const toOrders = () => uni.navigateTo({ url: '/pages/sales/orders' })
const toCommission = () => uni.navigateTo({ url: '/pages/sales/commission' })
const toCustomers = () => uni.navigateTo({ url: '/pages/sales/customers' })
const toTools = () => uni.navigateTo({ url: '/pages/sales/tools' })
const toDetail = (id) => uni.navigateTo({ url: `/pages/order/detail?id=${id}` })
</script>

<style>
.sales-dashboard { min-height: 100vh; background: #f5f7fa; padding-bottom: 40rpx; }
.hero-header { background: linear-gradient(135deg, #ff6b35, #ff8f65); padding: 60rpx 40rpx 80rpx; border-radius: 0 0 40rpx 40rpx; }
.hero-top { display: flex; justify-content: space-between; align-items: center; }
.hero-text { display: flex; flex-direction: column; gap: 8rpx; }
.greeting { font-size: 28rpx; color: rgba(255,255,255,0.85); }
.name { font-size: 40rpx; font-weight: bold; color: #fff; }
.hero-avatar { width: 80rpx; height: 80rpx; border-radius: 50%; background: rgba(255,255,255,0.25); color: #fff; font-size: 36rpx; font-weight: bold; display: flex; align-items: center; justify-content: center; }
.stats-row { display: flex; margin: -50rpx 30rpx 30rpx; gap: 16rpx; }
.stat-card { flex: 1; background: #fff; border-radius: 20rpx; padding: 24rpx 16rpx; text-align: center; box-shadow: 0 8rpx 24rpx rgba(0,0,0,0.06); }
.stat-card:active { transform: scale(0.96); }
.stat-icon { font-size: 36rpx; display: block; margin-bottom: 8rpx; }
.stat-val { font-size: 34rpx; font-weight: 800; color: #1a1a2e; display: block; }
.stat-lbl { font-size: 20rpx; color: #999; margin-top: 4rpx; display: block; }
.section { margin: 0 30rpx 30rpx; }
.section-title { font-size: 30rpx; font-weight: 700; color: #1a1a2e; display: block; margin-bottom: 20rpx; }
.menu-grid { display: flex; flex-wrap: wrap; gap: 16rpx; }
.menu-item { width: calc(33.33% - 12rpx); background: #fff; border-radius: 20rpx; padding: 28rpx 12rpx; text-align: center; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.04); }
.menu-item:active { transform: scale(0.95); }
.menu-icon-box { width: 64rpx; height: 64rpx; border-radius: 16rpx; display: flex; align-items: center; justify-content: center; margin: 0 auto 12rpx; }
.menu-icon-box.orange { background: #fff5f0; } .menu-icon-box.blue { background: #f0f5ff; }
.menu-icon-box.green { background: #f0fff4; } .menu-icon-box.purple { background: #f5f0ff; }
.menu-icon-box.teal { background: #f0fffa; }
.menu-icon { font-size: 32rpx; }
.menu-label { font-size: 24rpx; color: #333; font-weight: 500; }
.recent-list { background: #fff; border-radius: 20rpx; overflow: hidden; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.04); }
.recent-item { display: flex; justify-content: space-between; align-items: center; padding: 28rpx 24rpx; border-bottom: 1rpx solid #f5f5f5; }
.recent-item:last-child { border-bottom: none; }
.recent-item:active { background: #fafafa; }
.recent-left { display: flex; flex-direction: column; gap: 6rpx; }
.recent-sn { font-size: 24rpx; color: #666; }
.recent-amount { font-size: 28rpx; font-weight: bold; color: #333; }
.recent-right { display: flex; align-items: center; gap: 16rpx; }
.status-badge { font-size: 22rpx; padding: 4rpx 16rpx; border-radius: 20rpx; font-weight: 500; }
.status-badge.s0 { background: #fff5f0; color: #ff6b35; }
.status-badge.s1 { background: #f0f5ff; color: #4a7cff; }
.status-badge.s2 { background: #f0fff4; color: #10b981; }
.status-badge.s3 { background: #f0fdf4; color: #059669; }
.status-badge.s4 { background: #f5f5f5; color: #999; }
.recent-arrow { font-size: 36rpx; color: #ccc; }
.empty-row { text-align: center; padding: 40rpx 0; color: #999; font-size: 26rpx; }
</style>
