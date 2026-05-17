<template>
  <view class="page-container">
    <view class="top-bar">
      <view class="top-left">
        <text class="home-link">🏠 分销商城首页</text>
        <text class="location">📍 我的位置</text>
      </view>
      <view class="top-right">
        <template v-if="!isLoggedIn">
          <text class="top-link" @click="toLogin">你好，请登录</text>
          <text class="top-link" @click="toRegister">免费注册</text>
        </template>
        
        <template v-else>
          <text class="top-link" @click="toUser">欢迎你，{{ userName }}</text>
          <text v-if="isSales" class="top-link highlight" @click="toDistribution">我的分销</text>
          <text class="top-link" @click="logout">退出</text>
        </template>
        
        <text class="top-link" @click="toOrder">我的订单</text>
        <text class="top-link">客户服务 ▾</text>
        <text class="top-link">网站导航 ▾</text>
      </view>
    </view> 

    <view class="header-main">
      <view class="logo-area">
        <text class="logo-text">分销商城</text>
      </view>
      <view class="search-area">
        <view class="search-bar" @click="toSearch">
          <text class="search-icon">🔍</text>
          <input class="search-input" placeholder="搜索商品" disabled />
        </view>
        <view class="search-btn" @click="toSearch">搜索</view>
      </view>
      <view class="header-actions">
        <view class="action-item" @click="toCart">
          <text class="icon">🛒</text>
          <view class="badge" v-if="cartCount > 0">{{ cartCount }}</view>
        </view>
        <view class="action-item" @click="toUser">
          <text class="icon">👤</text>
        </view>
      </view>
    </view>

    <view class="hot-words">
      <text class="hot-word" v-for="word in hotWordsList" :key="word">{{ word }}</text>
    </view>

    <view class="main-content">
      <view class="left-category">
        <text class="category-title">分类</text>
        <view class="category-list">
          <view class="category-item" v-for="cat in leftCategoryList" :key="cat.name" @click="toCategory(cat.name)">
            <text class="cat-icon">{{ cat.icon }}</text>
            <text class="cat-name">{{ cat.name }}</text>
          </view>
        </view>
      </view>

      <view class="center-main">
        <swiper class="banner-swiper" indicator-dots autoplay circular indicator-color="#eee" indicator-active-color="#e64340">
          <swiper-item v-for="(banner, index) in bannerList" :key="index">
            <view class="banner-card">
              <image :src="banner.img" mode="aspectFill" class="banner-img"></image>
            </view>
          </swiper-item>
        </swiper>

        <view class="recommend-section">
          <view class="recommend-header">
            <text class="recommend-icon">🔴</text>
            <text class="recommend-title">为你推荐</text>
          </view>
          <scroll-view class="recommend-scroll" scroll-x="true" show-scrollbar="false">
            <view class="recommend-product-list">
              <view class="prod-card" v-for="item in recommendProducts" :key="item.id" @click="toDetail(item.id)">
                <view class="img-container">
                  <image :src="item.img" mode="aspectFill" class="p-img"></image>
                  <view class="p-tag" v-if="item.isNew">新品</view>
                  <view class="self-support-tag" v-if="item.isSelf">自营</view>
                </view>
                <text class="p-name">{{ item.name }}</text>
                <view class="price-box">
                  <text class="unit">¥</text>
                  <text class="integer">{{ item.price }}</text>
                </view>
              </view>
            </view>
          </scroll-view>
        </view>
      </view>

      <view class="right-sidebar">
        <view class="sidebar-item" v-for="item in sidebarList" :key="item.title" :style="{ backgroundColor: item.bgColor }">
          <view class="sidebar-text">
            <text class="sidebar-title">{{ item.title }}</text>
            <text class="sidebar-desc">{{ item.desc }}</text>
          </view>
          <image :src="item.img" mode="aspectFill" class="sidebar-img"></image>
        </view>
      </view>
    </view>

    <view class="product-section">
      <view class="section-header">
        <text class="section-title">热门推荐</text>
        <view class="filter-tabs">
          <text class="tab active">综合</text>
          <text class="tab">销量</text>
          <text class="tab">价格</text>
        </view>
      </view>
      <view class="product-list">
        <view class="prod-card" v-for="item in recommendProducts" :key="item.id" @click="toDetail(item.id)">
          <view class="img-container">
            <image :src="item.img" mode="widthFix" class="p-img"></image>
            <view class="p-tag" v-if="item.isNew">新品</view>
          </view>
          <view class="p-info">
            <text class="p-name">{{ item.name }}</text>
            <view class="p-footer">
              <view class="price-box">
                <text class="unit">￥</text>
                <text class="integer">{{ item.price }}</text>
              </view>
              <text class="sales">已售 {{ item.sales }}</text>
            </view>
            <view class="commission-info" v-if="isSales && item.commission">
              <text>佣金：￥{{ item.commission }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <view class="load-more">没有更多了</view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { onShow, onLoad } from '@dcloudio/uni-app';

const cartCount = ref(0);
const isLoggedIn = ref(false); 
const isSales = ref(false);
const userName = ref('');      

const hotWordsList = ref(['手机', '电脑', '耳机', '相机', '配件', '家居', '美妆', '食品']);
const leftCategoryList = ref([{ name: '数码/手机', icon: '📱' }, { name: '电脑/办公', icon: '💻' }, { name: '家电/电器', icon: '🔌' }, { name: '美妆/个护', icon: '💄' }, { name: '食品/生鲜', icon: '🍎' }, { name: '家居/日用', icon: '🏠' }, { name: '服饰/鞋包', icon: '👕' }, { name: '运动/户外', icon: '⚽' }, { name: '母婴/玩具', icon: '👶' }, { name: '更多分类', icon: '📦' }]);
const bannerList = ref([{ img: 'https://images.pexels.com/photos/404280/pexels-photo-404280.jpeg?auto=compress&w=1200' }, { img: 'https://images.pexels.com/photos/1649771/pexels-photo-1649771.jpeg?auto=compress&w=1200' }, { img: 'https://images.pexels.com/photos/18105/pexels-photo.jpg?auto=compress&w=1200' }]);
const sidebarList = ref([{ title: '新品首发', desc: '爆款好物', img: 'https://images.pexels.com/photos/1092644/pexels-photo-1092644.jpeg?auto=compress&w=300', bgColor: '#f0fdf4' }, { title: '精致美妆', desc: '品质之选', img: 'https://images.pexels.com/photos/3394651/pexels-photo-3394651.jpeg?auto=compress&w=300', bgColor: '#f3e8ff' }, { title: '超值百货', desc: '省心省钱', img: 'https://images.pexels.com/photos/4526398/pexels-photo-4526398.jpeg?auto=compress&w=300', bgColor: '#e6f7ff' }, { title: '品质数码', desc: '超值特惠', img: 'https://images.pexels.com/photos/2047905/pexels-photo-2047905.jpeg?auto=compress&w=300', bgColor: '#fff7e6' }]);
const recommendProducts = ref([{ id: 1, name: 'HUAWEI Mate 80 Pro Max 旗舰手机', price: '8499', sales: '2.5k+', isNew: true, isSelf: true, commission: '256', img: 'https://images.pexels.com/photos/1092644/pexels-photo-1092644.jpeg?auto=compress&w=500' }, { id: 2, name: '高性能轻薄笔记本电脑 办公游戏本', price: '6999', sales: '800+', isNew: false, isSelf: true, commission: '210', img: 'https://images.pexels.com/photos/2047905/pexels-photo-2047905.jpeg?auto=compress&w=500' }, { id: 3, name: '主动降噪无线蓝牙耳机 长续航', price: '1299', sales: '1.2k+', isNew: true, isSelf: true, commission: '128', img: 'https://images.pexels.com/photos/3394651/pexels-photo-3394651.jpeg?auto=compress&w=500' }, { id: 4, name: '4K超清防抖运动相机', price: '2499', sales: '300+', isNew: false, isSelf: true, commission: '150', img: 'https://images.pexels.com/photos/1205033/pexels-photo-1205033.jpeg?auto=compress&w=500' }, { id: 5, name: '多功能无线快充充电器 通用款', price: '299', sales: '5k+', isNew: false, isSelf: true, commission: '30', img: 'https://images.pexels.com/photos/4526398/pexels-photo-4526398.jpeg?auto=compress&w=500' }, { id: 6, name: '电竞机械键盘 极光定制版', price: '599', sales: '2k+', isNew: true, isSelf: true, commission: '60', img: 'https://images.pexels.com/photos/1772123/pexels-photo-1772123.jpeg?auto=compress&w=500' }]);

onLoad((options) => {
  const shareCode = options.shareCode;
  if (shareCode) {
    console.log('检测到推广来源，准备绑定逻辑:', shareCode);
    uni.setStorageSync('pendingShareCode', shareCode);
  }
});

onShow(() => {
  const cart = uni.getStorageSync('cart') || [];
  cartCount.value = cart.length;

  const userInfo = uni.getStorageSync('userInfo');
  if (userInfo) {
    isLoggedIn.value = true;
    userName.value = userInfo.name || userInfo.phone || '尊贵的用户';
    isSales.value = !!(userInfo.salesId || userInfo.role === 'SALES');
  } else {
    isLoggedIn.value = false;
    isSales.value = false;
    userName.value = '';
  }
});

const toLogin = () => uni.navigateTo({ url: '/pages/login/login' });
const toRegister = () => uni.navigateTo({ url: '/pages/register/register' });

const logout = () => {
  uni.showModal({
    title: '提示', content: '确定要退出当前账号吗？',
    success: (res) => {
      if (res.confirm) {
        uni.removeStorageSync('token');
        uni.removeStorageSync('userInfo');
        isLoggedIn.value = false;
        isSales.value = false;
        uni.showToast({ title: '已安全退出', icon: 'none' });
      }
    }
  });
};

const toDistribution = () => {
  if (!isLoggedIn.value) return uni.showToast({ title: '请先登录', icon: 'none' });
  uni.navigateTo({ url: '/pages/distribution/index' });
};

const toOrder = () => {
  if (!isLoggedIn.value) {
    uni.showToast({ title: '请先登录查看订单', icon: 'none' });
    setTimeout(() => toLogin(), 1000); 
    return;
  }
  uni.navigateTo({ url: '/pages/order/list' });
};

const toSearch = () => uni.navigateTo({ url: '/pages/category/category' });
const toCart = () => uni.navigateTo({ url: '/pages/cart/cart' });
const toUser = () => uni.navigateTo({ url: '/pages/userInfo/userInfo' });
const toDetail = (id) => uni.navigateTo({ url: `/pages/ProductDetail/ProductDetail?id=${id}` });
const toCategory = (name) => uni.navigateTo({ url: `/pages/category/category?cat=${encodeURIComponent(name)}` });
</script>

<style scoped>
.highlight { color: #e64340 !important; font-weight: bold; }
* { transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1); }
page { background: #f5f5f5; }
.page-container { min-height: 100vh; padding-bottom: 50rpx; }
.top-bar { background: #f5f5f5; padding: 10rpx 30rpx; display: flex; justify-content: space-between; align-items: center; }
.top-left, .top-right { display: flex; gap: 20rpx; }
.top-link { font-size: 26rpx; color: #666; }
.top-link:hover { color: #e64340; }
.header-main { background: #e64340; padding: 20rpx 30rpx; display: flex; align-items: center; justify-content: space-between; }
.logo-text { font-size: 48rpx; font-weight: bold; color: #fff; }
.search-area { flex: 1; display: flex; align-items: center; margin: 0 30rpx; }
.search-bar { flex: 1; height: 70rpx; background: #fff; border-radius: 35rpx 0 0 35rpx; display: flex; align-items: center; padding: 0 30rpx; border: 2rpx solid #e64340; }
.search-bar:hover { transform: scale(1.01); box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.1); }
.search-icon { font-size: 28rpx; color: #999; margin-right: 15rpx; }
.search-input { flex: 1; border: none; outline: none; font-size: 28rpx; color: #333; }
.search-btn { height: 70rpx; padding: 0 40rpx; background: #e64340; color: #fff; font-size: 28rpx; font-weight: bold; border-radius: 0 35rpx 35rpx 0; border: 2rpx solid #fff; display: flex; align-items: center; justify-content: center; cursor: pointer; }
.search-btn:hover { background: #d13a37; }
.header-actions { display: flex; gap: 30rpx; }
.action-item { position: relative; font-size: 42rpx; color: #fff; }
.action-item:hover { transform: scale(1.15); }
.action-item:active { transform: scale(0.95); }
.badge { position: absolute; top: -8rpx; right: -10rpx; background: #fff; color: #e64340; font-size: 20rpx; min-width: 30rpx; height: 30rpx; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: bold; }
.hot-words { background: #fff; padding: 10rpx 30rpx; display: flex; gap: 25rpx; }
.hot-word { font-size: 26rpx; color: #666; }
.hot-word:hover { color: #e64340; }
.main-content { display: flex; padding: 20rpx 30rpx; gap: 20rpx; background: #f5f5f5; }
.left-category { width: 20%; background: #fff; border-radius: 12rpx; padding: 20rpx; }
.category-title { font-size: 32rpx; font-weight: bold; color: #333; margin-bottom: 20rpx; display: block; }
.category-list { display: flex; flex-direction: column; gap: 15rpx; }
.category-item { display: flex; align-items: center; gap: 10rpx; padding: 8rpx 10rpx; border-radius: 6rpx; }
.category-item:hover { background: #f5f5f5; color: #e64340; }
.cat-icon { font-size: 28rpx; }
.cat-name { font-size: 26rpx; color: #333; }
.center-main { flex: 1; display: flex; flex-direction: column; gap: 20rpx; }
.banner-swiper { height: 480rpx; background: #fff; border-radius: 12rpx; overflow: hidden; }
.banner-card { width: 100%; height: 100%; }
.banner-img { width: 100%; height: 100%; border-radius: 12rpx; }
.recommend-section { background: #fff; border-radius: 12rpx; padding: 20rpx; }
.recommend-header { display: flex; align-items: center; margin-bottom: 20rpx; }
.recommend-icon { font-size: 32rpx; margin-right: 10rpx; color: #e64340; }
.recommend-title { font-size: 32rpx; font-weight: bold; color: #e64340; }
.recommend-scroll { width: 100%; }
.recommend-product-list { display: flex; gap: 20rpx; padding: 10rpx 0; }
.prod-card { width: 220rpx; height: 320rpx; display: flex; flex-direction: column; background: #fff; border-radius: 8rpx; overflow: hidden; }
.prod-card:hover { transform: translateY(-4rpx); box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.08); }
.img-container { position: relative; width: 100%; height: 220rpx; flex-shrink: 0; }
.p-img { width: 100%; height: 100%; object-fit: cover; border-radius: 8rpx 8rpx 0 0; }
.p-tag, .self-support-tag { position: absolute; top: 10rpx; right: 10rpx; background: #e64340; color: #fff; font-size: 18rpx; padding: 4rpx 10rpx; border-radius: 4rpx; font-weight: 500; }
.p-tag { left: 10rpx; right: auto; }
.p-name { flex: 1; font-size: 24rpx; color: #333; line-height: 32rpx; padding: 10rpx 12rpx 0; overflow: hidden; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }
.price-box { padding: 8rpx 12rpx 12rpx; color: #e64340; font-weight: bold; }
.unit { font-size: 22rpx; }
.integer { font-size: 28rpx; }
.right-sidebar { width: 20%; display: flex; flex-direction: column; gap: 15rpx; }
.sidebar-item { display: flex; align-items: center; justify-content: space-between; padding: 20rpx; border-radius: 12rpx; }
.sidebar-text { display: flex; flex-direction: column; gap: 5rpx; }
.sidebar-title { font-size: 28rpx; font-weight: bold; color: #333; }
.sidebar-desc { font-size: 24rpx; color: #666; }
.sidebar-img { width: 80rpx; height: 80rpx; border-radius: 8rpx; }
.product-section { background: #fff; margin: 20rpx 30rpx; padding: 20rpx; border-radius: 12rpx; }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 25rpx; }
.section-title { font-size: 32rpx; font-weight: bold; color: #111; }
.filter-tabs { display: flex; gap: 30rpx; }
.tab { font-size: 26rpx; color: #999; }
.tab.active { color: #e64340; font-weight: bold; }
.product-list { display: flex; flex-wrap: wrap; gap: 20rpx; }
.p-info { padding: 25rpx; }
.p-footer { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8rpx; }
.sales { font-size: 22rpx; color: #999; }
.commission-info { font-size: 22rpx; color: #10b981; font-weight: 500; }
.load-more { text-align: center; color: #999; font-size: 24rpx; padding: 40rpx 0; }
</style>
