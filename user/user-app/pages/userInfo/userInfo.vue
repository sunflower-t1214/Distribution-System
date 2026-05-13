<template>
  <view class="user-info-page">
    <view class="user-card">
      <view class="avatar">
        <text class="avatar-text">{{ userInfo.phone ? userInfo.phone.substring(0, 3) : 'User' }}</text>
      </view>
      <view class="user-name">{{ userInfo.name || '未登录用户' }}</view>
      <view class="user-id">用户ID：{{ userInfo.userId || '---' }}</view>
      <view class="edit-btn" @click="toEditInfo">编辑资料</view>
    </view>

    <view class="role-switch">
      <view class="role-btn consumer" @click="switchRole('consumer')">
        <text class="role-icon">🛒</text>
        <text class="role-label">进入消费者视图</text>
      </view>
      <view class="role-btn sales" @click="switchRole('sales')">
        <text class="role-icon">📢</text>
        <text class="role-label">进入分销员视图</text>
      </view>
      <view class="role-btn admin" @click="switchRole('admin')">
        <text class="role-icon">⚙️</text>
        <text class="role-label">进入管理后台</text>
      </view>
    </view>

    <view class="func-grid">
      <view class="func-item" @click="toPage('/pages/order/list')">
        <view class="func-icon">我的订单</view>
        <text class="func-text">全部订单/状态查询</text>
      </view>
      <view class="func-item" @click="toPage('/pages/cart/cart')">
        <view class="func-icon">购物车</view>
        <text class="func-text">去结算</text>
      </view>
      <view class="func-item" @click="toPage('/pages/distribution/index')">
        <view class="func-icon">分销中心</view>
        <text class="func-text">查看分销数据</text>
      </view>
      <view class="func-item" @click="toService">
        <view class="func-icon">客服中心</view>
        <text class="func-text">在线咨询</text>
      </view>
    </view>

    <view class="info-list">
      <view class="info-item" @click="toPage('/pages/home/home')">
        <text class="label" style="color: #007aff; font-weight: bold;">>>> 返回商城首页</text>
        <text class="value">浏览商品</text>
      </view>
    </view>

    <button class="btn-logout" @click="logout">退出登录</button>

    <!-- 验证码弹窗 -->
    <view class="modal-mask" v-if="showVerifyModal" @click="showVerifyModal = false">
      <view class="modal-content" @click.stop>
        <text class="modal-title">身份验证</text>
        <text class="modal-desc">正在切换至「{{ targetRoleName }}」，请输入验证码</text>

        <view class="code-row">
          <input class="code-input" v-model="verifyCode" placeholder="验证码" maxlength="6" type="number" />
          <button class="get-code-btn" :disabled="countdown > 0" @click="getCode">
            {{ countdown > 0 ? countdown + 's' : '获取验证码' }}
          </button>
        </view>

        <view class="modal-actions">
          <button class="modal-btn cancel" @click="showVerifyModal = false">取消</button>
          <button class="modal-btn confirm" :disabled="submitting" @click="submitVerify">
            {{ submitting ? '验证中...' : '提交验证' }}
          </button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import request from '@/utils/request'

const userInfo = ref({ userId: '', name: '', phone: '' })
const showVerifyModal = ref(false)
const targetRole = ref('')
const targetRoleName = ref('')
const verifyCode = ref('')
const countdown = ref(0)
const submitting = ref(false)
let countdownTimer = null

onShow(() => {
  const cache = uni.getStorageSync('userInfo')
  if (cache) {
    userInfo.value = cache
  } else {
    uni.showModal({
      title: '提示', content: '请先登录', showCancel: false,
      success: () => { uni.reLaunch({ url: '/pages/login/login' }) }
    })
  }
})

function switchRole(role) {
  const userRole = uni.getStorageSync('userRole') || 'USER'

  if (role === 'consumer') {
    return uni.reLaunch({ url: '/pages/home/home' })
  }

  const roleName = { sales: '分销员', admin: '管理员' }
  const requiredRole = { sales: 'SALES', admin: 'ADMIN' }

  if (userRole === requiredRole[role] || userRole === 'ADMIN') {
    const map = { sales: '/pages/sales/index', admin: '/pages/admin/index' }
    return uni.reLaunch({ url: map[role] })
  }

  targetRole.value = role
  targetRoleName.value = roleName[role]
  verifyCode.value = ''
  showVerifyModal.value = true
}

function getCode() {
  const phone = userInfo.value.phone
  if (!phone) return uni.showToast({ title: '用户信息异常', icon: 'none' })

  uni.showLoading({ title: '发送中...' })
  request({ url: '/api/auth/code', data: { phone } }).then(res => {
    uni.hideLoading()
    if (res.code === 500) return uni.showToast({ title: res.msg, icon: 'none' })
    uni.showToast({ title: '验证码已发送（查看控制台）', icon: 'success' })

    countdown.value = 60
    countdownTimer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(countdownTimer)
        countdownTimer = null
      }
    }, 1000)
  }).catch(e => {
    uni.hideLoading()
    console.log('网络详细报错:', e)
  })
}

function submitVerify() {
  if (!verifyCode.value) return uni.showToast({ title: '请输入验证码', icon: 'none' })
  submitting.value = true
  uni.showLoading({ title: '验证中...' })

  request({
    url: '/api/auth/verify',
    method: 'POST',
    data: {
      phone: userInfo.value.phone,
      code: verifyCode.value,
      role: targetRole.value === 'admin' ? 'ADMIN' : 'SALES'
    }
  }).then(res => {
    uni.hideLoading()
    submitting.value = false
    if (res.code === 500) return uni.showModal({ title: '验证失败', content: res.msg, showCancel: false })

    const newRole = targetRole.value === 'admin' ? 'ADMIN' : 'SALES'
    uni.setStorageSync('userRole', newRole)
    const info = uni.getStorageSync('userInfo')
    if (info) {
      info.role = newRole
      uni.setStorageSync('userInfo', info)
    }

    showVerifyModal.value = false
    uni.showToast({ title: '身份切换成功', icon: 'success' })

    setTimeout(() => {
      const map = { sales: '/pages/sales/index', admin: '/pages/admin/index' }
      uni.reLaunch({ url: map[targetRole.value] })
    }, 1000)
  }).catch(e => {
    uni.hideLoading()
    submitting.value = false
    console.log('网络详细报错:', e)
  })
}

const toPage = (url) => uni.navigateTo({ url })
const toEditInfo = () => uni.navigateTo({ url: '/pages/editInfo/editInfo' })
const toService = () => uni.showModal({ title: '客服中心', content: '服务热线：400-123-4567', showCancel: false })

const logout = () => {
  uni.showModal({
    title: '确认退出', content: '是否退出当前账号？',
    success: (res) => {
      if (res.confirm) {
        uni.removeStorageSync('userInfo')
        uni.removeStorageSync('userRole')
        uni.reLaunch({ url: '/pages/login/login' })
      }
    }
  })
}
</script>

<style scoped>
.user-info-page { padding: 20px; background-color: #f5f5f7; min-height: 100vh; }
.user-card { background: linear-gradient(135deg, #007aff, #00c6ff); color: white; border-radius: 20px; padding: 40px 20px; text-align: center; margin-bottom: 20px; position: relative; box-shadow: 0 10rpx 30rpx rgba(0,122,255,0.2); }
.avatar { width: 100px; height: 100px; border-radius: 50%; background-color: rgba(255, 255, 255, 0.2); margin: 0 auto 15px; display: flex; align-items: center; justify-content: center; border: 4rpx solid rgba(255,255,255,0.4); }
.avatar-text { font-size: 32px; font-weight: bold; }
.user-name { font-size: 24px; font-weight: bold; margin-bottom: 8px; }
.user-id { font-size: 14px; opacity: 0.8; }
.edit-btn { position: absolute; top: 20px; right: 20px; font-size: 12px; padding: 6rpx 20rpx; border: 1px solid rgba(255, 255, 255, 0.6); border-radius: 30rpx; }

.role-switch { display: flex; gap: 16rpx; margin-bottom: 20px; }
.role-btn { flex: 1; background: #fff; border-radius: 16px; padding: 24rpx 10rpx; text-align: center; box-shadow: 0 4rpx 15rpx rgba(0,0,0,0.05); }
.role-btn:active { transform: scale(0.96); }
.role-icon { font-size: 40rpx; display: block; margin-bottom: 8rpx; }
.role-label { font-size: 22rpx; font-weight: 500; display: block; }
.role-btn.consumer .role-label { color: #007aff; }
.role-btn.sales .role-label { color: #ff6b35; }
.role-btn.admin .role-label { color: #1a1a2e; }

.func-grid { display: flex; flex-wrap: wrap; gap: 20rpx; margin-bottom: 20px; }
.func-item { flex: 1; min-width: 45%; background: white; border-radius: 16px; padding: 25px 15px; text-align: center; box-shadow: 0 4rpx 15rpx rgba(0,0,0,0.05); }
.func-item:active { transform: scale(0.96); }
.func-icon { font-size: 30rpx; font-weight: bold; color: #007aff; margin-bottom: 10rpx; }
.func-text { font-size: 24rpx; color: #999; }
.info-list { background: white; border-radius: 16px; padding: 0 20px; margin-bottom: 30px; }
.info-item { display: flex; justify-content: space-between; padding: 30rpx 0; border-bottom: 1rpx solid #eee; }
.info-item:last-child { border: none; }
.btn-logout { background-color: #ff4444; color: white; border-radius: 50rpx; height: 90rpx; font-size: 30rpx; font-weight: bold; box-shadow: 0 10rpx 20rpx rgba(255,68,68,0.2); }

.modal-mask { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 999; }
.modal-content { width: 600rpx; background: #fff; border-radius: 24rpx; padding: 40rpx; }
.modal-title { font-size: 36rpx; font-weight: bold; color: #333; display: block; text-align: center; margin-bottom: 10rpx; }
.modal-desc { font-size: 26rpx; color: #999; display: block; text-align: center; margin-bottom: 30rpx; }
.code-row { display: flex; gap: 16rpx; margin-bottom: 30rpx; }
.code-input { flex: 1; height: 72rpx; border: 2rpx solid #eee; border-radius: 12rpx; font-size: 32rpx; text-align: center; letter-spacing: 8rpx; }
.get-code-btn { flex-shrink: 0; height: 72rpx; padding: 0 24rpx; background: #ff6b00; color: #fff; font-size: 24rpx; border-radius: 12rpx; border: none; line-height: 72rpx; }
.get-code-btn:active { transform: scale(0.95); }
.get-code-btn[disabled] { background: #ccc; }
.modal-actions { display: flex; gap: 20rpx; }
.modal-btn { flex: 1; height: 72rpx; border-radius: 36rpx; font-size: 28rpx; border: none; line-height: 72rpx; text-align: center; }
.modal-btn.cancel { background: #f5f5f5; color: #666; }
.modal-btn.confirm { background: #007aff; color: #fff; }
.modal-btn:active { transform: scale(0.95); }
.modal-btn.confirm[disabled] { background: #93c5fd; }
</style>
