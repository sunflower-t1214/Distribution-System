<template>
  <view class="container">
    <view class="header"><text class="title">推广工具</text></view>
    <view class="tool-list">
      <view class="tool-card" @click="shareQR">
        <text class="tool-icon">📱</text>
        <text class="tool-name">推广二维码</text>
        <text class="tool-desc">生成专属推广二维码</text>
      </view>
      <view class="tool-card" @click="sharePoster">
        <text class="tool-icon">🖼️</text>
        <text class="tool-name">分享海报</text>
        <text class="tool-desc">生成商品分享海报</text>
      </view>
      <view class="tool-card" @click="copyLink">
        <text class="tool-icon">🔗</text>
        <text class="tool-name">商品链接</text>
        <text class="tool-desc">复制推广商品链接</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import request from '@/utils/request'

function shareQR() {
  const userInfo = uni.getStorageSync('userInfo')
  if (!userInfo || !userInfo.salesId) return uni.showToast({ title: '请先登录分销账号', icon: 'none' })
  request({ url: '/api/sales/getShareLink', data: { salesId: userInfo.salesId } }).then(res => {
    if (res.code === 200) {
      uni.showModal({ title: '推广链接', content: res.shareLink, confirmText: '复制链接', success: (r) => {
        if (r.confirm) uni.setClipboardData({ data: res.shareLink })
      }})
    }
  })
}

function sharePoster() {
  uni.showToast({ title: '海报功能开发中', icon: 'none' })
}

function copyLink() {
  const userInfo = uni.getStorageSync('userInfo')
  if (!userInfo || !userInfo.salesId) return uni.showToast({ title: '请先登录分销账号', icon: 'none' })
  request({ url: '/api/sales/getShareLink', data: { salesId: userInfo.salesId } }).then(res => {
    if (res.code === 200) {
      uni.setClipboardData({ data: res.shareLink, success: () => uni.showToast({ title: '链接已复制' }) })
    }
  })
}
</script>

<style>
.container { min-height: 100vh; background: #f5f5f5; padding: 30rpx; }
.header { margin-bottom: 30rpx; }
.title { font-size: 36rpx; font-weight: bold; color: #333; }
.tool-list { display: flex; flex-direction: column; gap: 20rpx; }
.tool-card { background: #fff; border-radius: 16rpx; padding: 40rpx; display: flex; flex-direction: column; align-items: center; text-align: center; box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.05); }
.tool-icon { font-size: 80rpx; display: block; margin-bottom: 16rpx; }
.tool-name { font-size: 32rpx; font-weight: bold; color: #333; display: block; }
.tool-desc { font-size: 24rpx; color: #999; margin-top: 8rpx; display: block; }
</style>
