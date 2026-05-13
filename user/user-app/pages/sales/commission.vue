<template>
  <view class="page">
    <view class="balance-card">
      <text class="balance-lbl">当前佣金余额</text>
      <text class="balance-val">¥{{ balance }}</text>
    </view>

    <view class="stats-row">
      <view class="stat-item">
        <text class="stat-icon">📊</text>
        <text class="stat-val">¥{{ totalCommission }}</text>
        <text class="stat-lbl">累计佣金</text>
      </view>
      <view class="stat-item">
        <text class="stat-icon">💳</text>
        <text class="stat-val">¥{{ withdrawn }}</text>
        <text class="stat-lbl">已提现金额</text>
      </view>
      <view class="stat-item">
        <text class="stat-icon">⏳</text>
        <text class="stat-val">¥{{ pending }}</text>
        <text class="stat-lbl">待结算</text>
      </view>
    </view>

    <button class="withdraw-btn" @click="applyWithdraw">申请提现</button>

    <view class="section">
      <text class="section-title">佣金明细</text>
      <view class="record-list">
        <view class="record-item" v-for="r in records" :key="r.id">
          <view class="record-left">
            <text class="record-source">{{ r.source || '推广订单' }}</text>
            <text class="record-time">{{ r.time || r.createTime || '' }}</text>
          </view>
          <view class="record-right">
            <text class="record-amount">+¥{{ r.amount || '0.00' }}</text>
            <text class="record-status" :class="'s' + (r.status || 0)">{{ r.status === 1 ? '已结算' : '待结算' }}</text>
          </view>
        </view>
        <view v-if="records.length === 0" class="empty"><text>暂无佣金记录</text></view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import request from '@/utils/request'

const balance = ref('0.00')
const totalCommission = ref('0.00')
const withdrawn = ref('0.00')
const pending = ref('0.00')
const records = ref([])

onShow(() => {
  const data = uni.getStorageSync('commissionData')
  if (data) {
    balance.value = data.balance || '0.00'
    totalCommission.value = data.total || '0.00'
    withdrawn.value = data.withdrawn || '0.00'
    pending.value = data.pending || '0.00'
  }
  loadRecords()
})

async function loadRecords() {
  try {
    const res = await request({ url: '/api/order/list', data: { userId: 0 } })
    const list = (res.data || []).filter(o => o.salesId)
    records.value = list.map(o => ({
      id: o.id,
      source: o.orderSn,
      time: o.createTime ? o.createTime.slice(0, 16) : '',
      amount: (o.totalAmount * 0.1).toFixed(2),
      status: o.status >= 3 ? 1 : 0
    }))
  } catch (e) { console.log('加载失败:', e) }
}

function applyWithdraw() {
  uni.showToast({ title: '提现功能开发中', icon: 'none' })
}
</script>

<style>
.page { min-height: 100vh; background: #f5f7fa; padding: 24rpx; }
.balance-card { background: linear-gradient(135deg, #10b981, #34d399); border-radius: 24rpx; padding: 48rpx 40rpx; text-align: center; color: #fff; margin-bottom: 24rpx; box-shadow: 0 8rpx 24rpx rgba(16,185,129,0.3); }
.balance-lbl { font-size: 26rpx; opacity: 0.9; display: block; }
.balance-val { font-size: 80rpx; font-weight: 800; display: block; margin-top: 12rpx; letter-spacing: 2rpx; }
.stats-row { display: flex; gap: 16rpx; margin-bottom: 24rpx; }
.stat-item { flex: 1; background: #fff; border-radius: 20rpx; padding: 24rpx 16rpx; text-align: center; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.04); }
.stat-icon { font-size: 32rpx; display: block; margin-bottom: 8rpx; }
.stat-val { font-size: 30rpx; font-weight: 800; color: #1a1a2e; display: block; }
.stat-lbl { font-size: 20rpx; color: #999; margin-top: 4rpx; display: block; }
.withdraw-btn { width: 60%; height: 80rpx; line-height: 80rpx; background: linear-gradient(135deg, #10b981, #34d399); color: #fff; font-size: 30rpx; border-radius: 40rpx; border: none; margin: 0 auto 30rpx; display: block; box-shadow: 0 8rpx 20rpx rgba(16,185,129,0.25); }
.withdraw-btn:active { transform: scale(0.96); }
.section { }
.section-title { font-size: 30rpx; font-weight: 700; color: #1a1a2e; display: block; margin-bottom: 16rpx; }
.record-list { background: #fff; border-radius: 20rpx; overflow: hidden; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.04); }
.record-item { display: flex; justify-content: space-between; align-items: center; padding: 24rpx 24rpx; border-bottom: 1rpx solid #f5f5f5; }
.record-item:last-child { border-bottom: none; }
.record-left { display: flex; flex-direction: column; gap: 6rpx; }
.record-source { font-size: 26rpx; color: #333; }
.record-time { font-size: 22rpx; color: #bbb; }
.record-right { text-align: right; display: flex; flex-direction: column; gap: 6rpx; }
.record-amount { font-size: 28rpx; font-weight: 700; color: #10b981; }
.record-status { font-size: 22rpx; padding: 2rpx 12rpx; border-radius: 12rpx; display: inline-block; }
.record-status.s0 { background: #fff5f0; color: #ff6b35; }
.record-status.s1 { background: #f0fdf4; color: #059669; }
.empty { text-align: center; padding: 40rpx 0; color: #999; font-size: 26rpx; }
</style>
