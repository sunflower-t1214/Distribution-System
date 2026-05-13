<template>
  <view class="login-page">
    <view class="top-decoration"></view>

    <view class="main-content">
      <view class="header">
        <view class="welcome">商城分销系统</view>
        <view class="hint">请登录或浏览热销商品</view>
      </view>

      <view class="form-container">
        <view class="input-group">
          <view class="label">手机号</view>
          <input class="input" v-model="loginForm.phone" placeholder="请输入手机号" type="number" maxlength="11" />
        </view>
        <view class="input-group">
          <view class="label">密码</view>
          <input class="input" v-model="loginForm.password" placeholder="请输入密码" password />
        </view>
        <button class="btn-login" @click="doLogin">立即登录</button>
      </view>

      <view class="product-section">
        <view class="section-title">热销商品</view>
        <view class="product-list" v-if="productList.length > 0">
          <view class="product-item" v-for="item in productList" :key="item.productId || item.id" @click="toDetail(item.productId || item.id)">
            <view class="product-img-wrapper">
              <image
                v-if="item.imageUrl"
                class="product-img"
                :src="item.imageUrl"
                mode="aspectFill"
              ></image>
              <view v-else class="product-img placeholder-img">
                <text class="placeholder-icon">🛍️</text>
              </view>
            </view>
            <view class="product-info">
              <text class="product-name">{{ item.name }}</text>
              <view class="price-row">
                <text class="product-price">¥{{ item.price }}</text>
                <view class="commission-tag" v-if="(item.commissionRate || 0) > 0">
                  分享赚 ¥{{ ((item.price || 0) * (item.commissionRate || 0) / 100).toFixed(2) }}
                </view>
              </view>
            </view>
          </view>
        </view>
        <view v-else class="empty-state">
          <text class="empty-icon">📦</text>
          <text class="empty-text">暂无上架商品</text>
          <text class="empty-sub">稍后再来看看吧</text>
        </view>
      </view>

      <view class="footer">
        <text class="link-text" @click="toRegister">没有账号？新用户注册</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const loginForm = ref({ phone: '', password: '' })
const productList = ref([])

const doLogin = () => {
  if (!loginForm.value.phone || !loginForm.value.password) {
    return uni.showToast({ title: '请填写完整信息', icon: 'none' })
  }
  request({
    url: '/user/login',
    method: 'POST',
    data: loginForm.value
  }).then(res => {
    if (res.code === 500) {
      uni.showModal({ title: '登录失败', content: res.msg, showCancel: false })
    } else {
      const data = res.data || res
      uni.setStorageSync('userInfo', data)
      uni.setStorageSync('userRole', data.role || (data.salesId ? 'SALES' : 'USER'))
      uni.showToast({ title: '登录成功' })
      uni.navigateTo({ url: `/pages/userInfo/userInfo?id=${data.userId || data.id}` })
    }
  }).catch(e => {
    console.log('网络详细报错:', e)
  })
}

const getProducts = () => {
  request({ url: '/api/products/list' }).then(res => {
    productList.value = Array.isArray(res) ? res : (res.data || [])
  }).catch(e => {
    console.log('网络详细报错:', e)
  })
}

const toDetail = (id) => {
  uni.navigateTo({ url: `/pages/ProductDetail/ProductDetail?id=${id}` })
}

const toRegister = () => { uni.navigateTo({ url: '/pages/login/login' }) }

onMounted(() => { getProducts() })
</script>

<style>
.login-page { min-height: 100vh; background-color: #f8f8f8; position: relative; overflow-x: hidden; }
.top-decoration { position: absolute; top: -60px; left: -60px; width: 160%; height: 280px; background: linear-gradient(135deg, #ff6b00, #ff944d); border-radius: 50%; opacity: 0.85; }
.main-content { position: relative; z-index: 10; padding: 50px 20px; }
.header { color: #333; margin-bottom: 30px; text-align: center; }
.welcome { font-size: 28px; font-weight: bold; color: #fff; text-shadow: 0 2px 4px rgba(0,0,0,0.2); }
.hint { font-size: 14px; color: #fff; opacity: 0.9; margin-top: 5px; }

.form-container { background-color: white; border-radius: 16rpx; padding: 25px 20px; box-shadow: 0 4rpx 20rpx rgba(255, 107, 0, 0.08); margin-bottom: 30px; }
.input-group { margin-bottom: 20px; }
.label { font-size: 14px; color: #666; margin-bottom: 8px; }
.input { height: 45px; border-bottom: 1px solid #eee; font-size: 16px; width: 100%; }
.btn-login { background: linear-gradient(to right, #ff6b00, #ff944d); color: white; border-radius: 25px; margin-top: 20px; border: none; width: 100%; height: 45px; font-size: 16px; line-height: 45px; }
.btn-login:active { transform: scale(0.97); opacity: 0.9; }

.section-title { font-size: 18px; font-weight: bold; margin-bottom: 15px; color: #333; }
.product-list { display: flex; flex-wrap: wrap; justify-content: space-between; }
.product-item { width: 48%; background: #fff; border-radius: 16rpx; margin-bottom: 16rpx; overflow: hidden; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06); }
.product-item:active { transform: scale(0.97); }
.product-img-wrapper { position: relative; width: 100%; height: 220rpx; overflow: hidden; }
.product-img { width: 100%; height: 100%; background-color: #f0f0f0; display: block; }
.placeholder-img { display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #e8e8e8, #f5f5f5); }
.placeholder-icon { font-size: 52rpx; opacity: 0.5; }
.product-info { padding: 14rpx 14rpx 18rpx; display: flex; flex-direction: column; }
.product-name { font-size: 14px; color: #333; height: 40px; overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; margin-bottom: 8rpx; }
.price-row { display: flex; align-items: center; justify-content: space-between; }
.product-price { font-size: 18px; color: #ff6b00; font-weight: bold; }
.commission-tag { background: rgba(255, 215, 0, 0.88); color: #5c4a0f; font-size: 20rpx; font-weight: 600; padding: 4rpx 12rpx; border-radius: 6rpx; backdrop-filter: blur(2px); white-space: nowrap; }
.empty-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 60rpx 0; background: #fff; border-radius: 16rpx; }
.empty-icon { font-size: 80rpx; opacity: 0.4; margin-bottom: 16rpx; }
.empty-text { font-size: 16px; color: #999; font-weight: 500; }
.empty-sub { font-size: 13px; color: #ccc; margin-top: 8rpx; }

.footer { text-align: center; margin-top: 20px; }
.link-text { color: #ff6b00; font-size: 14px; }
.link-text:active { opacity: 0.7; }
</style>
