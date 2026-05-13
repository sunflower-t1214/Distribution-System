<template>
  <view class="container">
    <view class="header">
      <text class="title">销售人员管理</text>
      <button class="btn-add" @click="addSales">+ 创建</button>
    </view>
    <view class="sales-list">
      <view class="sales-card" v-for="item in salesList" :key="item.salesId">
        <view class="info">
          <text class="s-name">{{ item.name || '未命名' }}</text>
          <text class="s-phone">{{ item.phone }}</text>
          <text class="s-data">累计销售额: ¥{{ item.totalSales || '0.00' }}</text>
          <text class="s-data">佣金余额: ¥{{ item.commissionBalance || '0.00' }}</text>
        </view>
        <view class="actions">
          <button class="btn-edit" @click="editSales(item)">编辑</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'

const salesList = ref([])

onShow(() => {
  const data = uni.getStorageSync('salesList')
  salesList.value = data || []
})

function addSales() {
  uni.showToast({ title: '创建功能开发中', icon: 'none' })
}

function editSales(item) {
  uni.showToast({ title: '编辑功能开发中', icon: 'none' })
}
</script>

<style>
.container { min-height: 100vh; background: #f5f5f5; padding: 30rpx; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30rpx; }
.title { font-size: 36rpx; font-weight: bold; color: #333; }
.btn-add { height: 56rpx; line-height: 56rpx; background: #1a1a2e; color: #fff; font-size: 24rpx; border-radius: 28rpx; padding: 0 24rpx; border: none; }
.sales-card { background: #fff; border-radius: 16rpx; padding: 30rpx; margin-bottom: 20rpx; display: flex; align-items: center; box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.05); }
.info { flex: 1; }
.s-name { font-size: 28rpx; font-weight: bold; color: #333; display: block; margin-bottom: 6rpx; }
.s-phone { font-size: 24rpx; color: #999; display: block; margin-bottom: 4rpx; }
.s-data { font-size: 22rpx; color: #1a1a2e; display: block; }
.btn-edit { height: 56rpx; line-height: 56rpx; background: #1a1a2e; color: #fff; font-size: 24rpx; border-radius: 28rpx; padding: 0 24rpx; border: none; }
</style>
