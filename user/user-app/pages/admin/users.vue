<template>
  <view class="container">
    <view class="header">
      <text class="title">用户管理</text>
      <text class="count">共 {{ userList.length }} 人</text>
    </view>
    <view class="user-list">
      <view class="user-card" v-for="item in userList" :key="item.userId">
        <view class="avatar">{{ item.name ? item.name[0] : '?' }}</view>
        <view class="info">
          <text class="u-name">{{ item.name || '未命名' }}</text>
          <text class="u-phone">{{ item.phone }}</text>
          <text class="u-time">注册: {{ item.registerTime ? item.registerTime.slice(0, 10) : '未知' }}</text>
        </view>
        <view class="actions">
          <button class="btn-ban" @click="banUser(item)">封禁</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import request from '@/utils/request'

const userList = ref([])

onShow(() => { loadUsers() })

async function loadUsers() {
  try {
    const res = await request({ url: '/user/info', data: { id: 1 } })
    userList.value = res.data ? [res.data] : []
  } catch (e) {
    console.log('加载用户失败:', e)
  }
}

function banUser(item) {
  uni.showModal({
    title: '提示', content: `确定封禁用户 ${item.name}？`,
    success: (r) => { if (r.confirm) uni.showToast({ title: '已封禁（演示）', icon: 'none' }) }
  })
}
</script>

<style>
.container { min-height: 100vh; background: #f5f5f5; padding: 30rpx; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30rpx; }
.title { font-size: 36rpx; font-weight: bold; color: #333; }
.count { font-size: 24rpx; color: #999; }
.user-card { background: #fff; border-radius: 16rpx; padding: 30rpx; margin-bottom: 20rpx; display: flex; align-items: center; box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.05); }
.avatar { width: 80rpx; height: 80rpx; border-radius: 50%; background: #1a1a2e; color: #fff; display: flex; align-items: center; justify-content: center; font-size: 36rpx; font-weight: bold; flex-shrink: 0; }
.info { flex: 1; margin-left: 20rpx; display: flex; flex-direction: column; gap: 6rpx; }
.u-name { font-size: 28rpx; font-weight: bold; color: #333; }
.u-phone { font-size: 24rpx; color: #999; }
.u-time { font-size: 22rpx; color: #aaa; }
.btn-ban { height: 56rpx; line-height: 56rpx; background: #ff4d4f; color: #fff; font-size: 24rpx; border-radius: 28rpx; padding: 0 24rpx; border: none; }
</style>
