<template>
  <view class="page">
    <view class="header-bar">
      <text class="title">订单管理</text>
      <text class="order-count">共 {{ orderList.length }} 单</text>
    </view>

    <view class="order-list">
      <view class="order-card" v-for="item in orderList" :key="item.id || item.orderId" @click="toDetail(item.id || item.orderId)">
        <view class="card-top">
          <view class="card-top-left">
            <text class="order-sn">#{{ item.orderSn }}</text>
            <text class="order-user">用户ID: {{ item.userId }}</text>
          </view>
          <text class="status-badge" :class="'s' + item.status">{{ statusMap[item.status] }}</text>
        </view>
        <view class="card-body">
          <view class="item-row" v-for="sub in (item.items || []).slice(0, 2)" :key="sub.id">
            <text class="item-name">{{ sub.productName }}</text>
            <text class="item-qty">x{{ sub.quantity }}</text>
            <text class="item-price">¥{{ sub.productPrice }}</text>
          </view>
          <text v-if="(item.items || []).length > 2" class="more-text">等 {{ item.items.length }} 件商品</text>
        </view>
        <view class="card-foot">
          <text class="total-label">合计: <text class="total-val">¥{{ item.totalAmount }}</text></text>
          <button class="btn-status" @click.stop="changeStatus(item)">修改状态</button>
        </view>
      </view>
    </view>

    <view v-if="orderList.length === 0" class="empty"><text class="empty-icon">📋</text><text>暂无订单</text></view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import request from '@/utils/request'

const orderList = ref([])
const statusMap = { 0: '待付款', 1: '待发货', 2: '待收货', 3: '已完成', 4: '已取消' }

onShow(() => { loadOrders() })

async function loadOrders() {
  try {
    const res = await request({ url: '/api/order/list', data: { userId: 0 } })
    orderList.value = res.data || []
  } catch (e) { console.log('网络详细报错:', e) }
}

function changeStatus(item) {
  uni.showActionSheet({
    itemList: ['待发货', '已完成', '已取消'],
    success: (r) => {
      const map = [1, 3, 4]
      item.status = map[r.tapIndex]
      uni.showToast({ title: '状态已更新', icon: 'none' })
    }
  })
}

const toDetail = (id) => uni.navigateTo({ url: `/pages/order/detail?id=${id}` })
</script>

<style>
.page { min-height: 100vh; background: #f0f2f5; padding: 24rpx; }
.header-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24rpx; }
.title { font-size: 34rpx; font-weight: 700; color: #1a1a2e; }
.order-count { font-size: 24rpx; color: #999; }
.order-card { background: #fff; border-radius: 20rpx; margin-bottom: 20rpx; overflow: hidden; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.04); }
.order-card:active { transform: scale(0.98); }
.card-top { display: flex; justify-content: space-between; align-items: center; padding: 24rpx; border-bottom: 1rpx solid #f5f5f5; }
.card-top-left { display: flex; flex-direction: column; gap: 4rpx; }
.order-sn { font-size: 26rpx; color: #333; font-weight: 600; }
.order-user { font-size: 22rpx; color: #bbb; }
.status-badge { font-size: 22rpx; padding: 6rpx 20rpx; border-radius: 20rpx; font-weight: 500; }
.status-badge.s0 { background: #fff5f0; color: #ff6b35; } .status-badge.s1 { background: #eef2ff; color: #4f46e5; }
.status-badge.s2 { background: #f0fff4; color: #10b981; } .status-badge.s3 { background: #f0fdf4; color: #059669; }
.status-badge.s4 { background: #f5f5f5; color: #999; }
.card-body { padding: 20rpx 24rpx; }
.item-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10rpx; }
.item-name { font-size: 26rpx; color: #333; flex: 1; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.item-qty { font-size: 24rpx; color: #999; margin: 0 20rpx; }
.item-price { font-size: 26rpx; color: #333; font-weight: 500; }
.more-text { font-size: 22rpx; color: #bbb; }
.card-foot { display: flex; justify-content: space-between; align-items: center; padding: 20rpx 24rpx; border-top: 1rpx solid #f5f5f5; }
.total-label { font-size: 24rpx; color: #999; }
.total-val { font-size: 30rpx; font-weight: 800; color: #ff4d4f; }
.btn-status { height: 48rpx; line-height: 48rpx; background: #1a1a2e; color: #fff; font-size: 22rpx; border-radius: 24rpx; padding: 0 20rpx; border: none; }
.btn-status:active { transform: scale(0.95); }
.empty { text-align: center; padding: 120rpx 0; color: #999; font-size: 28rpx; display: flex; flex-direction: column; align-items: center; gap: 16rpx; }
.empty-icon { font-size: 80rpx; opacity: 0.4; }
</style>
