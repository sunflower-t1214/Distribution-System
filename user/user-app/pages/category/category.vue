<template>
  <view class="page">
    <view class="main">
      <scroll-view class="left-menu" scroll-y>
        <view
          class="menu-item"
          :class="{ active: activeCat === item.name }"
          v-for="item in categories"
          :key="item.name"
          @click="switchCat(item.name)">
          <text class="menu-name">{{ item.name }}</text>
          <text class="menu-count">{{ item.count }}</text>
        </view>
      </scroll-view>

      <scroll-view class="right-goods" scroll-y @scrolltolower="loadMore">
        <view class="section-title">{{ activeCat || '全部商品' }}</view>
        <view class="goods-grid">
          <view class="goods-card" v-for="item in productList" :key="item.productId" @click="toDetail(item.productId)">
            <image :src="item.imageUrl || '/static/logo.png'" mode="aspectFill" class="g-img"></image>
            <text class="g-name">{{ item.name }}</text>
            <view class="g-footer">
              <text class="g-price">¥{{ item.price }}</text>
              <view class="g-commission" v-if="(item.commissionRate || 0) > 0">
                赚 ¥{{ ((item.price || 0) * (item.commissionRate || 0) / 100).toFixed(2) }}
              </view>
            </view>
          </view>
        </view>
        <view class="load-status" v-if="loading">加载中...</view>
        <view class="load-status" v-else-if="!hasMore">— 没有更多了 —</view>
      </scroll-view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import request from '@/utils/request'

const categories = ref([])
const activeCat = ref('')
const productList = ref([])
const page = ref(1)
const hasMore = ref(true)
const loading = ref(false)
const pageSize = 20

onLoad((options) => {
  if (options.cat) {
    activeCat.value = decodeURIComponent(options.cat)
  }
})

onMounted(() => {
  loadCategories()
  loadProducts(true)
})

async function loadCategories() {
  try {
    const res = await request({ url: '/api/products/categories' })
    categories.value = res.data || res || []
    if (!activeCat.value && categories.value.length > 0) {
      activeCat.value = categories.value[0].name
    }
  } catch (e) { console.log('网络详细报错:', e) }
}

function switchCat(name) {
  activeCat.value = name
  productList.value = []
  page.value = 1
  hasMore.value = true
  loadProducts(true)
}

async function loadProducts(reset) {
  if (loading.value) return
  loading.value = true
  if (reset) { page.value = 1; productList.value = []; hasMore.value = true }
  try {
    const params = { page: page.value, pageSize }
    if (activeCat.value) params.category = activeCat.value
    const res = await request({ url: '/api/products/page', data: params })
    const data = res.data || res
    const records = data.records || []
    productList.value.push(...records)
    hasMore.value = records.length >= pageSize
    page.value++
  } catch (e) { console.log('网络详细报错:', e) }
  finally { loading.value = false }
}

function loadMore() { if (hasMore.value && !loading.value) loadProducts() }

const toDetail = (id) => uni.navigateTo({ url: `/pages/ProductDetail/ProductDetail?id=${id}` })
</script>

<style>
page { background: #f5f7fa; height: 100%; }
.page { display: flex; flex-direction: column; height: 100vh; }
.main { display: flex; flex: 1; overflow: hidden; }
.left-menu { width: 180rpx; background: #fff; border-right: 1rpx solid #f0f0f0; }
.menu-item { padding: 28rpx 20rpx; border-bottom: 1rpx solid #f5f5f5; display: flex; flex-direction: column; align-items: center; gap: 4rpx; }
.menu-item.active { background: #f5f7fa; position: relative; }
.menu-item.active::before { content: ''; position: absolute; left: 0; top: 50%; transform: translateY(-50%); width: 6rpx; height: 40rpx; background: #e64340; border-radius: 0 6rpx 6rpx 0; }
.menu-name { font-size: 24rpx; color: #333; text-align: center; }
.menu-item.active .menu-name { color: #e64340; font-weight: 600; }
.menu-count { font-size: 20rpx; color: #bbb; }
.right-goods { flex: 1; padding: 16rpx; }
.section-title { font-size: 28rpx; font-weight: 700; color: #333; margin-bottom: 12rpx; padding-left: 4rpx; }
.goods-grid { display: flex; flex-wrap: wrap; gap: 16rpx; }
.goods-card { width: calc(50% - 8rpx); background: #fff; border-radius: 16rpx; overflow: hidden; box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.04); }
.goods-card:active { transform: scale(0.97); }
.g-img { width: 100%; height: 280rpx; display: block; background: #f0f0f0; }
.g-name { font-size: 24rpx; color: #333; padding: 12rpx 12rpx 0; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.g-footer { display: flex; justify-content: space-between; align-items: center; padding: 10rpx 12rpx 16rpx; }
.g-price { font-size: 28rpx; color: #ff4d4f; font-weight: 800; }
.g-commission { background: linear-gradient(135deg, #ffd700, #ffed4a); color: #8b6914; font-size: 18rpx; font-weight: 700; padding: 4rpx 12rpx; border-radius: 20rpx; white-space: nowrap; }
.load-status { text-align: center; color: #bbb; font-size: 24rpx; padding: 30rpx 0; }
</style>
