<template>
  <view class="admin-dashboard">
    <view class="hero-header">
      <view class="hero-top">
        <view class="hero-text">
          <text class="greeting">⚙️ 管理后台</text>
          <text class="subtitle">系统数据概览</text>
        </view>
        <view class="hero-avatar">A</view>
      </view>
    </view>

    <view class="stats-grid">
      <view class="stat-card">
        <view class="stat-icon-wrap blue"><text class="stat-icon2">¥</text></view>
        <text class="stat-val">{{ todaySales }}</text>
        <text class="stat-lbl">今日销售额</text>
      </view>
      <view class="stat-card">
        <view class="stat-icon-wrap green"><text class="stat-icon2">📋</text></view>
        <text class="stat-val">{{ totalOrders }}</text>
        <text class="stat-lbl">总订单数</text>
      </view>
      <view class="stat-card">
        <view class="stat-icon-wrap purple"><text class="stat-icon2">👤</text></view>
        <text class="stat-val">{{ userCount }}</text>
        <text class="stat-lbl">用户数量</text>
      </view>
      <view class="stat-card">
        <view class="stat-icon-wrap orange"><text class="stat-icon2">📢</text></view>
        <text class="stat-val">{{ salesCount }}</text>
        <text class="stat-lbl">销售人员</text>
      </view>
    </view>

    <view class="section">
      <text class="section-title">管理功能</text>
      <view class="menu-grid">
        <view class="menu-item" @click="toUsers">
          <view class="menu-icon-box blue"><text>👤</text></view>
          <text class="menu-label">用户管理</text>
        </view>
        <view class="menu-item" @click="toSales">
          <view class="menu-icon-box orange"><text>📢</text></view>
          <text class="menu-label">销售人员</text>
        </view>
        <view class="menu-item" @click="toProducts">
          <view class="menu-icon-box green"><text>📦</text></view>
          <text class="menu-label">商品管理</text>
        </view>
        <view class="menu-item" @click="toOrders">
          <view class="menu-icon-box purple"><text>📋</text></view>
          <text class="menu-label">订单管理</text>
        </view>
        <view class="menu-item" @click="toCommission">
          <view class="menu-icon-box red"><text>💰</text></view>
          <text class="menu-label">佣金管理</text>
        </view>
        <view class="menu-item" @click="toStatistics">
          <view class="menu-icon-box teal"><text>📊</text></view>
          <text class="menu-label">数据统计</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'

const todaySales = ref('0')
const totalOrders = ref(0)
const userCount = ref(0)
const salesCount = ref(0)

onShow(() => {
  const role = uni.getStorageSync('userRole') || 'USER'
  if (role !== 'ADMIN') {
    uni.showToast({ title: '权限不足', icon: 'none' })
    return setTimeout(() => uni.reLaunch({ url: '/pages/home/home' }), 1500)
  }
  const d = uni.getStorageSync('adminStats')
  if (d) {
    todaySales.value = d.todaySales || '0'
    totalOrders.value = d.totalOrders || 0
    userCount.value = d.userCount || 0
    salesCount.value = d.salesCount || 0
  }
})

const toUsers = () => uni.navigateTo({ url: '/pages/admin/users' })
const toSales = () => uni.navigateTo({ url: '/pages/admin/sales' })
const toProducts = () => uni.navigateTo({ url: '/pages/admin/products' })
const toOrders = () => uni.navigateTo({ url: '/pages/admin/orders' })
const toCommission = () => uni.navigateTo({ url: '/pages/admin/commission' })
const toStatistics = () => uni.navigateTo({ url: '/pages/admin/statistics' })
</script>

<style>
.admin-dashboard { min-height: 100vh; background: #f0f2f5; padding-bottom: 40rpx; }
.hero-header { background: linear-gradient(135deg, #1a1a2e, #16213e, #0f3460); padding: 60rpx 40rpx 80rpx; border-radius: 0 0 40rpx 40rpx; }
.hero-top { display: flex; justify-content: space-between; align-items: center; }
.hero-text { display: flex; flex-direction: column; gap: 8rpx; }
.greeting { font-size: 36rpx; font-weight: bold; color: #fff; }
.subtitle { font-size: 26rpx; color: rgba(255,255,255,0.7); }
.hero-avatar { width: 80rpx; height: 80rpx; border-radius: 50%; background: rgba(255,255,255,0.15); color: #fff; font-size: 36rpx; font-weight: bold; display: flex; align-items: center; justify-content: center; }
.stats-grid { display: flex; flex-wrap: wrap; margin: -50rpx 24rpx 24rpx; gap: 16rpx; }
.stat-card { width: calc(50% - 8rpx); background: #fff; border-radius: 20rpx; padding: 28rpx 20rpx; box-shadow: 0 8rpx 24rpx rgba(0,0,0,0.05); }
.stat-card:active { transform: scale(0.97); }
.stat-icon-wrap { width: 56rpx; height: 56rpx; border-radius: 14rpx; display: flex; align-items: center; justify-content: center; margin-bottom: 12rpx; }
.stat-icon-wrap.blue { background: #eef2ff; } .stat-icon-wrap.green { background: #ecfdf5; }
.stat-icon-wrap.purple { background: #f5f3ff; } .stat-icon-wrap.orange { background: #fff7ed; }
.stat-icon2 { font-size: 28rpx; font-weight: bold; }
.stat-icon-wrap.blue .stat-icon2 { color: #4f46e5; } .stat-icon-wrap.green .stat-icon2 { color: #10b981; }
.stat-icon-wrap.purple .stat-icon2 { color: #8b5cf6; } .stat-icon-wrap.orange .stat-icon2 { color: #f97316; }
.stat-val { font-size: 38rpx; font-weight: 800; color: #1a1a2e; display: block; }
.stat-lbl { font-size: 22rpx; color: #999; margin-top: 4rpx; display: block; }
.section { margin: 0 24rpx 24rpx; }
.section-title { font-size: 30rpx; font-weight: 700; color: #1a1a2e; display: block; margin-bottom: 16rpx; }
.menu-grid { display: flex; flex-wrap: wrap; gap: 16rpx; }
.menu-item { width: calc(33.33% - 12rpx); background: #fff; border-radius: 20rpx; padding: 32rpx 12rpx; text-align: center; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.04); }
.menu-item:active { transform: scale(0.95); }
.menu-icon-box { width: 64rpx; height: 64rpx; border-radius: 16rpx; display: flex; align-items: center; justify-content: center; margin: 0 auto 12rpx; font-size: 32rpx; }
.menu-icon-box.blue { background: #eef2ff; } .menu-icon-box.orange { background: #fff7ed; }
.menu-icon-box.green { background: #ecfdf5; } .menu-icon-box.purple { background: #f5f3ff; }
.menu-icon-box.red { background: #fef2f2; } .menu-icon-box.teal { background: #f0fdfa; }
.menu-label { font-size: 24rpx; color: #333; font-weight: 500; }
</style>
