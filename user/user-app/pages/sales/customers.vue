<template>
  <view class="container">
    <view class="header"><text class="title">我的客户</text></view>
    <view class="customer-list">
      <view class="customer-card" v-for="item in customerList" :key="item.userId">
        <view class="avatar">{{ item.name ? item.name[0] : '?' }}</view>
        <view class="info">
          <text class="c-name">{{ item.name || '未命名' }}</text>
          <text class="c-phone">{{ item.phone || '暂无手机号' }}</text>
          <text class="c-orders">购买次数: {{ item.orderCount || 0 }}</text>
        </view>
        <view class="actions">
          <button class="btn-contact" @click="contact(item)">联系</button>
        </view>
      </view>
    </view>
    <view v-if="customerList.length === 0" class="empty">
      <text class="empty-icon">👥</text>
      <text>暂无客户</text>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'

const customerList = ref([])

onShow(() => {
  const stored = uni.getStorageSync('customerList')
  customerList.value = stored || []
})
function contact(item) {
  uni.makePhoneCall({ phoneNumber: item.phone || '' })
}
</script>

<style>
.container { min-height: 100vh; background: #f5f5f5; padding: 30rpx; }
.header { margin-bottom: 30rpx; }
.title { font-size: 36rpx; font-weight: bold; color: #333; }
.customer-card { background: #fff; border-radius: 16rpx; padding: 30rpx; margin-bottom: 20rpx; display: flex; align-items: center; box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.05); }
.avatar { width: 80rpx; height: 80rpx; border-radius: 50%; background: linear-gradient(135deg, #ff6b35, #ff8f65); color: #fff; display: flex; align-items: center; justify-content: center; font-size: 36rpx; font-weight: bold; flex-shrink: 0; }
.info { flex: 1; margin-left: 20rpx; display: flex; flex-direction: column; gap: 6rpx; }
.c-name { font-size: 28rpx; font-weight: bold; color: #333; }
.c-phone { font-size: 24rpx; color: #999; }
.c-orders { font-size: 22rpx; color: #ff6b35; }
.btn-contact { height: 56rpx; line-height: 56rpx; background: #ff6b35; color: #fff; font-size: 24rpx; border-radius: 28rpx; padding: 0 24rpx; border: none; }
.empty { text-align: center; padding: 100rpx 0; color: #999; font-size: 28rpx; }
.empty-icon { font-size: 80rpx; display: block; margin-bottom: 20rpx; }
</style>
