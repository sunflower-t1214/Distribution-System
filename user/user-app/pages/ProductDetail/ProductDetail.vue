<template>
	<view class="detail-box" v-if="product.productId">
		<swiper class="hero-swiper" indicator-dots autoplay circular>
			<swiper-item>
				<image :src="product.imageUrl || getDefaultImg(product.name)" mode="aspectFill" class="hero-img"></image>
			</swiper-item>
		</swiper>

		<view class="content-card">
			<view class="price-box">
				<text class="cur">￥</text>
				<text class="val">{{ product.price || '0.00' }}</text>
			</view>
			<view class="title">{{ product.name || '商品详情' }}</view>
			<view class="desc">库存: {{ product.stock || 0 }} | 分类: {{ product.category || '默认' }}</view>
		</view>

		<view class="bottom-nav">
			<button class="btn-add" @click="addToCart">加入购物车</button>
			<button class="btn-buy" @click="goBuy">立即购买</button>
		</view>
	</view>
	<view v-else class="loading">加载中...</view>
</template>

<script setup>
import { ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';

const product = ref({ productId: null, name: '', price: 0, imageUrl: '', stock: 0 });
const BASE_URL = 'http://localhost:8080';

// 兜底图逻辑
const getDefaultImg = (name) => {
	if (name && name.includes('Mate 80')) return 'https://images.pexels.com/photos/1092644/pexels-photo-1092644.jpeg?auto=compress&w=400';
	if (name && name.includes('电脑')) return 'https://images.pexels.com/photos/2047905/pexels-photo-2047905.jpeg?auto=compress&w=400';
	if (name && name.includes('耳机')) return 'https://images.pexels.com/photos/3394651/pexels-photo-3394651.jpeg?auto=compress&w=400';
	return 'https://images.pexels.com/photos/90946/pexels-photo-90946.jpeg?auto=compress&w=400';
};

/**
 * 🔴 核心修改：分销关系静默绑定
 */
onLoad((options) => {
	// 1. 加载商品详情 [cite: 266, 271]
	if(options.id) {
		uni.request({
			url: `${BASE_URL}/api/products/${options.id}`,
			success: (res) => { product.value = res.data; }
		});
	}

	// 2. 识别分销推广来源 [cite: 301, 305, 306]
	const shareCode = options.shareCode;
	if (shareCode) {
		console.log('检测到推广来源:', shareCode);
		const userInfo = uni.getStorageSync('userInfo');
		
		if (userInfo && (userInfo.userId || userInfo.id)) {
			// 已登录：直接请求后端执行绑定 [cite: 301]
			bindSales(shareCode, userInfo.userId || userInfo.id);
		} else {
			// 未登录：存入缓存，待登录后由登录页面或首页统一处理
			uni.setStorageSync('pendingShareCode', shareCode);
		}
	}
});

// 调用后端绑定接口 [cite: 301]
const bindSales = (code, uId) => {
	uni.request({
		url: `${BASE_URL}/api/sales/bind`,
		method: 'POST',
		// 注意：根据后端参数接收方式调整
		data: { shareCode: code, userId: uId },
		header: { 'content-type': 'application/x-www-form-urlencoded' },
		success: (res) => {
			if (res.data.code === 200) { console.log('分销关系已静默绑定'); }
		}
	});
};

const addToCart = () => {
	const user = uni.getStorageSync('userInfo');
	if(!user) return uni.showToast({ title: '请先登录', icon: 'none' });

	let idValue = (typeof user === 'object') ? (user.userId || user.id) : JSON.parse(user).userId;
	const currentUserId = Number(idValue);

	uni.request({
		url: `${BASE_URL}/api/cart/add`,
		method: 'POST',
		data: { userId: currentUserId, productId: product.value.productId, quantity: 1 },
		success: (res) => {
			if (res.statusCode === 200) { uni.showToast({ title: '已加入购物车', icon: 'success' }); }
		}
	});
};

const goBuy = () => {
	uni.showToast({ title: '结算功能开发中', icon: 'none' });
};
</script>

<style scoped>
.detail-box { background: #f8f8f8; min-height: 100vh; padding-bottom: 120rpx; }
.hero-swiper { width: 100%; height: 750rpx; }
.hero-img { width: 100%; height: 100%; }
.content-card { background: #fff; padding: 30rpx; margin-bottom: 20rpx; }
.price-box { color: #ff4757; font-weight: bold; }
.val { font-size: 50rpx; }
.title { font-size: 36rpx; font-weight: bold; margin: 20rpx 0; }
.desc { color: #999; font-size: 24rpx; }
.bottom-nav { position: fixed; bottom: 0; width: 100%; height: 100rpx; display: flex; background: #fff; border-top: 1px solid #eee; z-index: 99; }
.btn-add, .btn-buy { flex: 1; border-radius: 0; font-size: 28rpx; height: 100rpx; line-height: 100rpx; border: none; }
.btn-add { background: #333; color: #fff; }
.btn-buy { background: #007aff; color: #fff; }
.loading { padding: 100rpx; text-align: center; color: #999; }
</style>