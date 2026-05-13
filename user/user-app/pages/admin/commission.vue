<template>
  <view class="container">
    <view class="header"><text class="title">佣金管理</text></view>
    <view class="commission-list">
      <view class="commission-card" v-for="item in commissionList" :key="item.id || item.commissionId">
        <view class="row">
          <text class="label">销售员:</text>
          <text class="value">{{ item.salesName || '未知' }}</text>
        </view>
        <view class="row">
          <text class="label">订单金额:</text>
          <text class="value">¥{{ item.orderAmount || '0.00' }}</text>
        </view>
        <view class="row">
          <text class="label">佣金比例:</text>
          <text class="value">{{ (item.rate || 10) + '%' }}</text>
        </view>
        <view class="row">
          <text class="label">佣金金额:</text>
          <text class="value highlight">¥{{ item.commissionAmount || '0.00' }}</text>
        </view>
        <view class="actions">
          <button class="btn-audit" @click="audit(item)">审核</button>
        </view>
      </view>
    </view>
    <view v-if="commissionList.length === 0" class="empty">
      <text class="empty-icon">💰</text>
      <text>暂无佣金记录</text>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'

const commissionList = ref([])

function audit(item) {
  uni.showToast({ title: '审核功能开发中', icon: 'none' })
}
</script>

<style>
.container { min-height: 100vh; background: #f5f5f5; padding: 30rpx; }
.header { margin-bottom: 30rpx; }
.title { font-size: 36rpx; font-weight: bold; color: #333; }
.commission-card { background: #fff; border-radius: 16rpx; padding: 30rpx; margin-bottom: 20rpx; box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.05); }
.row { display: flex; justify-content: space-between; margin-bottom: 10rpx; }
.label { font-size: 26rpx; color: #999; }
.value { font-size: 26rpx; color: #333; }
.highlight { color: #10b981; font-weight: bold; }
.btn-audit { height: 56rpx; line-height: 56rpx; background: #1a1a2e; color: #fff; font-size: 24rpx; border-radius: 28rpx; padding: 0 24rpx; border: none; margin-top: 10rpx; }
.empty { text-align: center; padding: 80rpx 0; color: #999; font-size: 28rpx; }
.empty-icon { font-size: 80rpx; display: block; margin-bottom: 20rpx; }
</style>
