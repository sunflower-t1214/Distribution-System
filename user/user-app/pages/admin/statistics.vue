<template>
  <view class="page">
    <view class="header-bar">
      <text class="title">数据统计</text>
    </view>

    <view class="summary-row">
      <view class="summary-card">
        <text class="s-icon">📈</text>
        <text class="s-val">¥{{ todaySales }}</text>
        <text class="s-lbl">今日销售额</text>
      </view>
      <view class="summary-card">
        <text class="s-icon">📊</text>
        <text class="s-val">¥{{ monthSales }}</text>
        <text class="s-lbl">本月销售额</text>
      </view>
    </view>

    <view class="chart-card">
      <text class="chart-title">📦 商品销量排行</text>
      <view class="rank-list">
        <view class="rank-item" v-for="(item, i) in topProducts" :key="i">
          <text class="rank-num">{{ i + 1 }}</text>
          <view class="rank-bar" :style="{ width: (100 - i * 15) + '%' }"></view>
          <text class="rank-name">{{ item.name }}</text>
          <text class="rank-val">{{ item.sales || 0 }}</text>
        </view>
        <view v-if="topProducts.length === 0" class="empty-small">暂无数据</view>
      </view>
    </view>

    <view class="chart-card">
      <text class="chart-title">🏆 销售员排行</text>
      <view class="rank-list">
        <view class="rank-item" v-for="(item, i) in topSales" :key="i">
          <text class="rank-num">{{ i + 1 }}</text>
          <view class="rank-bar orange" :style="{ width: (100 - i * 18) + '%' }"></view>
          <text class="rank-name">{{ item.name }}</text>
          <text class="rank-val">¥{{ item.totalSales || '0' }}</text>
        </view>
        <view v-if="topSales.length === 0" class="empty-small">暂无数据</view>
      </view>
    </view>

    <view class="chart-card">
      <text class="chart-title">📈 用户增长</text>
      <view class="growth-row">
        <view class="growth-item">
          <text class="growth-val">{{ userCount }}</text>
          <text class="growth-lbl">总用户数</text>
        </view>
        <view class="growth-item">
          <text class="growth-val">{{ salesCount }}</text>
          <text class="growth-lbl">销售员数</text>
        </view>
        <view class="growth-item">
          <text class="growth-val">{{ newUsers }}</text>
          <text class="growth-lbl">今日新增</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'

const todaySales = ref('0.00')
const monthSales = ref('0.00')
const topProducts = ref([])
const topSales = ref([])
const userCount = ref(0)
const salesCount = ref(0)
const newUsers = ref(0)

onShow(() => {
  const d = uni.getStorageSync('statisticsData')
  if (d) {
    todaySales.value = d.todaySales || '0.00'
    monthSales.value = d.monthSales || '0.00'
    topProducts.value = d.topProducts || []
    topSales.value = d.topSales || []
    userCount.value = d.userCount || 0
    salesCount.value = d.salesCount || 0
    newUsers.value = d.newUsers || 0
  }
})
</script>

<style>
.page { min-height: 100vh; background: #f0f2f5; padding: 24rpx; }
.header-bar { margin-bottom: 24rpx; }
.title { font-size: 34rpx; font-weight: 700; color: #1a1a2e; }
.summary-row { display: flex; gap: 16rpx; margin-bottom: 24rpx; }
.summary-card { flex: 1; background: #fff; border-radius: 20rpx; padding: 32rpx 24rpx; text-align: center; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.04); }
.s-icon { font-size: 48rpx; display: block; margin-bottom: 12rpx; }
.s-val { font-size: 38rpx; font-weight: 800; color: #1a1a2e; display: block; }
.s-lbl { font-size: 22rpx; color: #999; margin-top: 6rpx; display: block; }
.chart-card { background: #fff; border-radius: 20rpx; padding: 28rpx 24rpx; margin-bottom: 20rpx; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.04); }
.chart-title { font-size: 28rpx; font-weight: 700; color: #1a1a2e; display: block; margin-bottom: 20rpx; }
.rank-list { }
.rank-item { display: flex; align-items: center; gap: 12rpx; margin-bottom: 16rpx; position: relative; }
.rank-num { width: 36rpx; height: 36rpx; border-radius: 50%; background: #1a1a2e; color: #fff; font-size: 20rpx; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.rank-bar { position: absolute; left: 48rpx; height: 100%; background: linear-gradient(90deg, #eef2ff, #c7d2fe); border-radius: 4rpx; opacity: 0.4; z-index: 0; max-width: calc(100% - 200rpx); }
.rank-bar.orange { background: linear-gradient(90deg, #fff7ed, #fed7aa); }
.rank-name { flex: 1; font-size: 26rpx; color: #333; z-index: 1; padding-left: 8rpx; }
.rank-val { font-size: 24rpx; color: #666; font-weight: 600; z-index: 1; min-width: 100rpx; text-align: right; }
.growth-row { display: flex; gap: 20rpx; }
.growth-item { flex: 1; text-align: center; padding: 20rpx 0; }
.growth-val { font-size: 40rpx; font-weight: 800; color: #1a1a2e; display: block; }
.growth-lbl { font-size: 22rpx; color: #999; margin-top: 8rpx; display: block; }
.empty-small { text-align: center; padding: 30rpx 0; color: #999; font-size: 24rpx; }
</style>
