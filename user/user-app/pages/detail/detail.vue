<template>
	<view class="detail-box" v-if="product && product.productId">
		<swiper class="hero-swiper" indicator-dots autoplay circular indicator-active-color="#007aff">
			<swiper-item>
				<image :src="product.imageUrl || 'https://picsum.photos/800/800'" mode="aspectFill" class="hero-img"></image>
			</swiper-item>
		</swiper>

		<view class="content-card">
			<view class="price-box">
				<view class="price-main">
					<text class="cur">￥</text>
					<text class="val">{{ product.price || '0.00' }}</text>
				</view>
				<view class="comm-badge">预估收益: ￥{{ ((product.price || 0) * 0.1).toFixed(2) }}</view>
			</view>
			
			<view class="title">{{ product.name || '商品名称加载中...' }}</view>
			<view class="tags">
				<text class="tag">顺丰包邮</text>
				<text class="tag">正品保障</text>
			</view>
		</view>

		<view class="selection-card">
			<view class="row">
				<text class="label">库存</text>
				<text class="text">{{ product.stock || 0 }} 件</text>
			</view>
			<view class="row counter-row">
				<text class="label">数量</text>
				<view class="counter">
					<view class="btn" @click="changeQty(-1)">-</view>
					<input type="number" v-model="buyQuantity" class="count-input" />
					<view class="btn" @click="changeQty(1)">+</view>
				</view>
			</view>
		</view>

		<view class="bottom-nav">
			<view class="icon-btns">
				<view class="i-item" @click="goHome">
					<text class="icon">🏠</text>
					<text>首页</text>
				</view>
			</view>
			<view class="action-btns">
				<button class="add-cart" @click="addToCart">加入购物车</button>
				<button class="buy-now">立即购买</button>
			</view>
		</view>
	</view>
	
	<view v-else class="loading-box">
		<text>正在加载商品详情...</text>
	</view>
</template>

<script setup>
import { ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';

// 1. 初始化必须包含后端可能返回的所有字段，防止渲染时读取 undefined
const product = ref({
	productId: null,
	name: '',
	price: 0,
	stock: 0,
	imageUrl: ''
});
const buyQuantity = ref(1);

onLoad((options) => {
	console.log("详情页收到参数 ID:", options.id);
	if (options.id) {
		fetchProductDetail(options.id);
	} else {
		uni.showToast({ title: '参数丢失', icon: 'none' });
	}
});

const fetchProductDetail = (id) => {
	uni.request({
		url: `http://localhost:8080/api/products/${id}`,
		method: 'GET',
		success: (res) => {
			console.log("后端返回详情:", res.data);
			if (res.data) {
				product.value = res.data;
			}
		},
		fail: (err) => {
			console.error("请求详情失败:", err);
			uni.showToast({ title: '网络请求失败', icon: 'none' });
		}
	});
};

const changeQty = (val) => {
	const next = buyQuantity.value + val;
	if (next > 0 && next <= (product.value.stock || 1)) {
		buyQuantity.value = next;
	}
};

const addToCart = () => {
	const userInfo = uni.getStorageSync('userInfo');
	if (!userInfo) {
		uni.showToast({ title: '请先登录', icon: 'none' });
		return;
	}
	uni.request({
		url: 'http://localhost:8080/api/cart/add',
		method: 'POST',
		data: {
			userId: userInfo.id,
			productId: product.value.productId,
			quantity: buyQuantity.value
		},
		success: () => {
			uni.showToast({ title: '已加入购物车', icon: 'success' });
		}
	});
};

const goHome = () => uni.reLaunch({ url: '/pages/index/index' });
</script>

<style scoped>
.detail-box { background: #f8f8f8; min-height: 100vh; padding-bottom: 140rpx; }
.hero-swiper { width: 100%; height: 750rpx; background: #fff; }
.hero-img { width: 100%; height: 100%; }

.content-card { background: #fff; padding: 30rpx; margin-bottom: 20rpx; }
.price-main { color: #ff4757; font-weight: bold; }
.val { font-size: 50rpx; }
.comm-badge { margin-top: 10rpx; background: #fff5f5; color: #ff4757; font-size: 22rpx; padding: 4rpx 16rpx; border-radius: 8rpx; border: 1px solid #ff4757; display: inline-block; }
.title { font-size: 36rpx; font-weight: bold; margin: 20rpx 0; }
.tag { font-size: 22rpx; color: #007aff; background: #eef6ff; padding: 4rpx 12rpx; border-radius: 4rpx; margin-right: 15rpx; }

.selection-card { background: #fff; padding: 30rpx; }
.row { display: flex; align-items: center; margin-bottom: 20rpx; }
.label { width: 100rpx; font-size: 26rpx; color: #999; }
.counter { display: flex; border: 1px solid #eee; border-radius: 8rpx; }
.btn { width: 60rpx; height: 60rpx; display: flex; align-items: center; justify-content: center; background: #f9f9f9; }
.count-input { width: 80rpx; height: 60rpx; text-align: center; font-size: 26rpx; border-left: 1px solid #eee; border-right: 1px solid #eee; }

.bottom-nav { position: fixed; bottom: 0; left: 0; width: 100%; height: 120rpx; background: #fff; display: flex; align-items: center; padding: 0 30rpx; box-sizing: border-box; border-top: 1px solid #eee; }
.icon-btns { margin-right: 30rpx; }
.i-item { display: flex; flex-direction: column; align-items: center; font-size: 22rpx; color: #666; }
.action-btns { flex: 1; display: flex; gap: 20rpx; }
.action-btns button { flex: 1; height: 80rpx; line-height: 80rpx; border-radius: 40rpx; font-size: 26rpx; font-weight: bold; }
.add-cart { background: #333; color: #fff; }
.buy-now { background: linear-gradient(to right, #007aff, #00c6ff); color: #fff; }
.loading-box { padding: 100rpx; text-align: center; color: #999; }
</style>