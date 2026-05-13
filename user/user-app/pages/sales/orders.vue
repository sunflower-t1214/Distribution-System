<template>
  <view class="page">
    <view class="tabs">
      <text class="tab" :class="{ active: t === currentTab }" v-for="t in tabList" :key="t.key" @click="currentTab = t.key">{{ t.label }}</text>
    </view>

    <view class="order-list">
      <view class="order-card" v-for="item in filteredOrders" :key="item.id || item.orderId" @click="toDetail(item.id || item.orderId)">
        <view class="card-top">
          <view class="card-top-left">
            <text class="order-sn">{{ item.orderSn }}</text>
            <text class="order-time">{{ item.createTime ? item.createTime.slice(0, 16) : '' }}</text>
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
          <text class="commission-label">佣金: <text class="commission-val">¥{{ (item.totalAmount * 0.1).toFixed(2) }}</text></text>
          <text class="total-label">合计: <text class="total-val">¥{{ item.totalAmount }}</text></text>
        </view>
      </view>
    </view>
    <view v-if="filteredOrders.length === 0" class="empty"><text class="empty-icon">📋</text><text>暂无推广订单</text></view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import request from '@/utils/request'

const orderList = ref([])
const currentTab = ref('all')
const tabList = [{ key: 'all', label: '全部' }, { key: 'pending', label: '待付款' }, { key: 'shipped', label: '已发货' }, { key: 'done', label: '已完成' }]
const statusMap = { 0: '待付款', 1: '待发货', 2: '待收货', 3: '已完成', 4: '已取消' }

const filteredOrders = computed(() => {
  if (currentTab.value === 'all') return orderList.value
  const m = { pending: 0, shipped: 1, done: 3 }
  return orderList.value.filter(o => o.status === m[currentTab.value])
})

onShow(() => { loadOrders() })

async function loadOrders() {
  try {
    const res = await request({ url: '/api/order/list', data: { userId: 0 } })
    const list = res.data || []
    orderList.value = list.filter(o => o.salesId)
  } catch (e) { console.log('网络详细报错:', e) }
}

const toDetail = (id) => uni.navigateTo({ url: `/pages/order/detail?id=${id}` })
</script>

<style>
.page { min-height: 100vh; background: #f5f7fa; padding: 24rpx; }
.tabs { display: flex; background: #fff; border-radius: 16rpx; padding: 8rpx; margin-bottom: 24rpx; box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.04); }
.tab { flex: 1; text-align: center; font-size: 26rpx; color: #999; padding: 14rpx 0; border-radius: 12rpx; transition: all 0.2s; }
.tab.active { background: linear-gradient(135deg, #ff6b35, #ff8f65); color: #fff; font-weight: 600; box-shadow: 0 4rpx 12rpx rgba(255,107,53,0.3); }
.order-card { background: #fff; border-radius: 20rpx; margin-bottom: 20rpx; overflow: hidden; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.04); }
.order-card:active { transform: scale(0.98); }
.card-top { display: flex; justify-content: space-between; align-items: center; padding: 24rpx; border-bottom: 1rpx solid #f5f5f5; }
.card-top-left { display: flex; flex-direction: column; gap: 6rpx; }
.order-sn { font-size: 24rpx; color: #666; }
.order-time { font-size: 22rpx; color: #bbb; }
.status-badge { font-size: 22rpx; padding: 6rpx 20rpx; border-radius: 20rpx; font-weight: 500; }
.status-badge.s0 { background: #fff5f0; color: #ff6b35; } .status-badge.s1 { background: #f0f5ff; color: #4a7cff; }
.status-badge.s2 { background: #f0fff4; color: #10b981; } .status-badge.s3 { background: #f0fdf4; color: #059669; }
.status-badge.s4 { background: #f5f5f5; color: #999; }
.card-body { padding: 20rpx 24rpx; }
.item-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10rpx; }
.item-name { font-size: 26rpx; color: #333; flex: 1; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.item-qty { font-size: 24rpx; color: #999; margin: 0 20rpx; }
.item-price { font-size: 26rpx; color: #333; font-weight: 500; }
.more-text { font-size: 22rpx; color: #bbb; }
.card-foot { display: flex; justify-content: space-between; align-items: center; padding: 20rpx 24rpx; border-top: 1rpx solid #f5f5f5; }
.commission-label { font-size: 24rpx; color: #999; }
.commission-val { color: #10b981; font-weight: 600; }
.total-label { font-size: 24rpx; color: #999; }
.total-val { font-size: 30rpx; color: #ff4d4f; font-weight: 800; }
.empty { text-align: center; padding: 120rpx 0; color: #999; font-size: 28rpx; display: flex; flex-direction: column; align-items: center; gap: 16rpx; }
.empty-icon { font-size: 80rpx; opacity: 0.4; }
</style>
