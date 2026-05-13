<template>
  <view class="page">
    <view class="hero-banner">
      <text class="hero-title">📢 推广商品</text>
      <text class="hero-sub">生成推广链接分享给客户，赚取佣金</text>
    </view>

    <view class="product-list">
      <view class="product-card" v-for="item in productList" :key="item.productId">
        <image class="p-img" :src="item.imageUrl || '/static/logo.png'" mode="aspectFill"></image>
        <view class="p-info">
          <text class="p-name">{{ item.name }}</text>
          <view class="p-meta-row">
            <text class="p-price">¥{{ item.price }}</text>
            <text class="p-commission">佣金 {{ item.commissionRate ? (item.commissionRate * 100) + '%' : '—' }}</text>
          </view>
          <view class="p-earn" v-if="item.commissionRate">
            推广赚 <text class="earn-val">¥{{ (item.price * item.commissionRate / 100).toFixed(2) }}</text>
          </view>
          <view class="btn-row">
            <button class="btn-link" @click.stop="generateLink(item)">🔗 生成链接</button>
            <button class="btn-share" @click.stop="shareProduct(item)">📤 分享</button>
          </view>
        </view>
      </view>
    </view>

    <view v-if="productList.length === 0" class="empty">
      <text class="empty-icon">📦</text>
      <text>暂无商品可推广</text>
    </view>
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

function generateLink(item) {
  const userInfo = uni.getStorageSync('userInfo')
  if (!userInfo || !userInfo.salesId) return uni.showToast({ title: '请先登录分销账号', icon: 'none' })
  request({ url: '/api/sales/getShareLink', data: { salesId: userInfo.salesId } }).then(res => {
    if (res.code === 200) {
      uni.setClipboardData({ data: res.shareLink, success: () => uni.showToast({ title: '推广链接已复制' }) })
    } else {
      uni.showToast({ title: res.msg || '生成失败', icon: 'none' })
    }
  }).catch(e => console.log('生成链接失败:', e))
}

function shareProduct(item) {
  uni.share({ title: item.name, url: `http://localhost:5173/pages/home/home?productId=${item.productId}` })
}

const toDetail = (id) => uni.navigateTo({ url: `/pages/ProductDetail/ProductDetail?id=${id}` })
</script>

<style>
.page { min-height: 100vh; background: #f5f7fa; padding-bottom: 40rpx; }
.hero-banner { background: linear-gradient(135deg, #ff6b35, #ff8f65); padding: 48rpx 40rpx; color: #fff; margin-bottom: 24rpx; }
.hero-title { font-size: 36rpx; font-weight: bold; display: block; }
.hero-sub { font-size: 24rpx; opacity: 0.85; margin-top: 8rpx; display: block; }
.product-list { padding: 0 24rpx; }
.product-card { background: #fff; border-radius: 20rpx; margin-bottom: 20rpx; overflow: hidden; display: flex; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.04); }
.product-card:active { transform: scale(0.98); }
.p-img { width: 200rpx; height: 200rpx; flex-shrink: 0; }
.p-info { flex: 1; padding: 24rpx; display: flex; flex-direction: column; gap: 8rpx; }
.p-name { font-size: 28rpx; color: #333; font-weight: 600; }
.p-meta-row { display: flex; justify-content: space-between; align-items: center; }
.p-price { font-size: 32rpx; color: #ff4d4f; font-weight: 800; }
.p-commission { font-size: 22rpx; color: #ff6b35; background: #fff5f0; padding: 2rpx 12rpx; border-radius: 8rpx; }
.p-earn { font-size: 22rpx; color: #10b981; background: #f0fdf4; padding: 6rpx 12rpx; border-radius: 8rpx; display: inline-block; }
.earn-val { font-weight: 700; }
.btn-row { display: flex; gap: 16rpx; margin-top: 4rpx; }
.btn-link { flex: 1; height: 56rpx; line-height: 56rpx; background: linear-gradient(135deg, #ff6b35, #ff8f65); color: #fff; font-size: 24rpx; border-radius: 28rpx; text-align: center; border: none; box-shadow: 0 4rpx 12rpx rgba(255,107,53,0.2); }
.btn-link:active { transform: scale(0.95); }
.btn-share { flex: 1; height: 56rpx; line-height: 54rpx; background: #fff; color: #ff6b35; font-size: 24rpx; border-radius: 28rpx; text-align: center; border: 2rpx solid #ff6b35; }
.btn-share:active { transform: scale(0.95); }
.empty { text-align: center; padding: 120rpx 0; color: #999; font-size: 28rpx; display: flex; flex-direction: column; align-items: center; gap: 16rpx; }
.empty-icon { font-size: 80rpx; opacity: 0.4; }
</style>
