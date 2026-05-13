<template>
  <view class="page">
    <view class="header-bar">
      <text class="title">商品管理</text>
      <button class="btn-add" @click="addProduct">+ 添加商品</button>
    </view>

    <view class="product-list">
      <view class="product-card" v-for="item in productList" :key="item.productId">
        <image class="p-img" :src="item.imageUrl || '/static/logo.png'" mode="aspectFill"></image>
        <view class="p-info">
          <text class="p-name">{{ item.name }}</text>
          <text class="p-price">¥{{ item.price }}</text>
          <view class="p-meta">
            <text class="p-stock">库存: {{ item.stock }}</text>
            <text class="p-commission">佣金率: {{ item.commissionRate ? (item.commissionRate * 100) + '%' : '未设置' }}</text>
          </view>
          <view class="p-status-row">
            <text class="status-dot" :class="item.status === 1 ? 'on' : 'off'"></text>
            <text class="p-status">{{ item.status === 1 ? '已上架' : '已下架' }}</text>
          </view>
        </view>
        <view class="p-actions">
          <button class="act-btn toggle" :class="item.status === 1 ? 'off' : 'on'" @click="toggleStatus(item)">{{ item.status === 1 ? '下架' : '上架' }}</button>
          <button class="act-btn edit" @click="editProduct(item)">编辑</button>
        </view>
      </view>
    </view>

    <view v-if="productList.length === 0" class="empty"><text class="empty-icon">📦</text><text>暂无商品</text></view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import request from '@/utils/request'

const productList = ref([])

onShow(() => { loadProducts() })

async function loadProducts() {
  try {
    const res = await request({ url: '/api/products/list' })
    productList.value = Array.isArray(res) ? res : (res.data || [])
  } catch (e) { console.log('网络详细报错:', e) }
}

function toggleStatus(item) {
  uni.showToast({ title: `${item.status === 1 ? '下架' : '上架'}成功（演示）`, icon: 'none' })
}

function addProduct() { uni.showToast({ title: '添加功能开发中', icon: 'none' }) }
function editProduct(item) { uni.navigateTo({ url: `/pages/ProductDetail/ProductDetail?id=${item.productId}` }) }
</script>

<style>
.page { min-height: 100vh; background: #f0f2f5; padding: 24rpx; }
.header-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24rpx; }
.title { font-size: 34rpx; font-weight: 700; color: #1a1a2e; }
.btn-add { height: 56rpx; line-height: 56rpx; background: linear-gradient(135deg, #1a1a2e, #16213e); color: #fff; font-size: 24rpx; border-radius: 28rpx; padding: 0 24rpx; border: none; box-shadow: 0 4rpx 12rpx rgba(26,26,46,0.2); }
.btn-add:active { transform: scale(0.95); }
.product-card { background: #fff; border-radius: 20rpx; margin-bottom: 20rpx; overflow: hidden; display: flex; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.04); }
.product-card:active { transform: scale(0.98); }
.p-img { width: 180rpx; height: 180rpx; flex-shrink: 0; }
.p-info { flex: 1; padding: 20rpx; display: flex; flex-direction: column; gap: 6rpx; }
.p-name { font-size: 28rpx; color: #333; font-weight: 600; }
.p-price { font-size: 30rpx; color: #ff4d4f; font-weight: 800; }
.p-meta { display: flex; gap: 16rpx; }
.p-stock, .p-commission { font-size: 22rpx; color: #999; }
.p-status-row { display: flex; align-items: center; gap: 8rpx; }
.status-dot { width: 12rpx; height: 12rpx; border-radius: 50%; }
.status-dot.on { background: #10b981; } .status-dot.off { background: #ccc; }
.p-status { font-size: 22rpx; color: #666; }
.p-actions { display: flex; flex-direction: column; justify-content: center; gap: 10rpx; padding: 20rpx; }
.act-btn { height: 48rpx; line-height: 48rpx; font-size: 22rpx; border-radius: 24rpx; padding: 0 16rpx; border: none; text-align: center; }
.act-btn.toggle.on { background: #10b981; color: #fff; }
.act-btn.toggle.off { background: #f5f5f5; color: #999; }
.act-btn.edit { background: #eef2ff; color: #4f46e5; }
.empty { text-align: center; padding: 120rpx 0; color: #999; font-size: 28rpx; display: flex; flex-direction: column; align-items: center; gap: 16rpx; }
.empty-icon { font-size: 80rpx; opacity: 0.4; }
</style>
