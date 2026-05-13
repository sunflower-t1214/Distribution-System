<template>
	<view class="confirm-container">
		<view class="section address-card">
			<view class="address-icon">📍</view>
			<view class="address-info">
				<view class="user-line">
					<text class="name">{{user.name}}</text>
					<text class="phone">{{user.phone}}</text>
				</view>
				<view class="detail">广东省深圳市南山区某某科技大厦 8 楼</view>
				<view class="tag">默认地址</view>
			</view>
			<text class="arrow">></text>
		</view>

		<view class="section goods-card">
			<view class="card-title">商品清单</view>
			<view class="item-list">
				<view class="item" v-for="item in selectedItems" :key="item.id">
					<image :src="item.productImageUrl || getDefaultImg(item.productName)" mode="aspectFill" class="prod-img"></image>
					<view class="prod-content">
						<text class="prod-name">{{item.productName}}</text>
						<text class="prod-spec">官方正品 | 极速发货</text>
						<view class="price-row">
							<text class="price">￥{{item.productPrice}}</text>
							<text class="count">x{{item.quantity}}</text>
						</view>
					</view>
				</view>
			</view>
		</view>

		<view class="section detail-card">
			<view class="detail-line">
				<text>商品总额</text>
				<text>￥{{totalPrice}}</text>
			</view>
			<view class="detail-line">
				<text>运费</text>
				<text class="free">免运费</text>
			</view>
			<view class="detail-line total">
				<text>实际支付</text>
				<text class="final-val">￥{{totalPrice}}</text>
			</view>
		</view>

		<view class="footer-bar">
			<view class="price-total">
				<text class="label">实付:</text>
				<text class="symbol">￥</text>
				<text class="val">{{totalPrice}}</text>
			</view>
			<button class="submit-btn" @click="submitOrder">立即下单</button>
		</view>
	</view>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';

const selectedItems = ref([]);
const user = uni.getStorageSync('userInfo') || {};

onMounted(() => {
	selectedItems.value = uni.getStorageSync('selectedCartItems') || [];
	if (selectedItems.value.length === 0) {
		uni.showToast({ title: '请先选择商品', icon: 'none' });
		uni.navigateBack();
	}
});

// 计算总价（和购物车完全统一）
const totalPrice = computed(() => {
	return selectedItems.value.reduce((sum, item) => sum + Number(item.productPrice) * Number(item.quantity), 0).toFixed(2);
});

// 默认图兜底（和购物车完全统一）
const getDefaultImg = (name) => {
	if (!name) return 'https://images.pexels.com/photos/90946/pexels-photo-90946.jpeg?auto=compress&w=200';
	if (name.includes('Mate 80')) return 'https://images.pexels.com/photos/1092644/pexels-photo-1092644.jpeg?auto=compress&w=200';
	if (name.includes('电脑')) return 'https://images.pexels.com/photos/2047905/pexels-photo-2047905.jpeg?auto=compress&w=200';
	if (name.includes('耳机')) return 'https://images.pexels.com/photos/3394651/pexels-photo-3394651.jpeg?auto=compress&w=200';
	if (name.includes('充电器')) return 'https://images.pexels.com/photos/4526398/pexels-photo-4526398.jpeg?auto=compress&w=200';
	return 'https://images.pexels.com/photos/90946/pexels-photo-90946.jpeg?auto=compress&w=200';
};

// ====================== 【我帮你修复的下单方法】 ======================
const submitOrder = () => {
	// 提纯用户ID，防止报错
	const userId = user.id || user.userId;
	if(!userId) return uni.showToast({title: '用户信息失效，请重新登录', icon: 'none'});
	if (selectedItems.value.length === 0) return uni.showToast({title: '请选择商品', icon: 'none'});

	uni.request({
		url: 'http://localhost:8080/api/order/create',
		method: 'POST',
		data: {
			userId: Number(userId),
			totalAmount: Number(totalPrice.value),
			items: selectedItems.value.map(item => ({
				productId: item.productId,
				productName: item.productName,
				productPrice: Number(item.productPrice),
				quantity: Number(item.quantity),
				productImageUrl: item.productImageUrl
			}))
		},
		success: (res) => {
			// ======================================
			// 🔴 核心修复：只判断状态码 200 就成功
			// ======================================
			if (res.statusCode === 200) {
				uni.showToast({ title: '下单成功', icon: 'success' });
				// 下单成功后清空购物车选中项
				uni.removeStorageSync('selectedCartItems');
				setTimeout(() => {
					uni.reLaunch({ url: '/pages/order/list' });
				}, 1500);
			} else {
				uni.showToast({ title: '下单失败', icon: 'none' });
			}
		},
		fail: () => {
			uni.showToast({ title: '网络请求失败', icon: 'none' });
		}
	});
};
</script>

<style scoped>
.confirm-container {
	background-color: #f7f8fa;
	min-height: 100vh;
	padding: 20rpx 20rpx 140rpx;
}

.section {
	background: #fff;
	border-radius: 24rpx;
	padding: 30rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 4rpx 20rpx rgba(0,0,0,0.02);
}

.address-card {
	display: flex;
	align-items: center;
}
.address-icon {
	font-size: 40rpx;
	margin-right: 20rpx;
}
.address-info {
	flex: 1;
}
.user-line {
	margin-bottom: 10rpx;
}
.user-line .name {
	font-size: 32rpx;
	font-weight: bold;
	margin-right: 20rpx;
}
.user-line .phone {
	color: #999;
	font-size: 26rpx;
}
.detail {
	font-size: 26rpx;
	color: #333;
	margin-bottom: 10rpx;
}
.tag {
	display: inline-block;
	background: #eef5fe;
	color: #007aff;
	font-size: 20rpx;
	padding: 2rpx 12rpx;
	border-radius: 4rpx;
}
.arrow { color: #ccc; }

.card-title {
	font-size: 28rpx;
	font-weight: bold;
	margin-bottom: 30rpx;
	border-bottom: 1rpx solid #f2f2f2;
	padding-bottom: 20rpx;
}
.item {
	display: flex;
	margin-bottom: 30rpx;
}
.prod-img {
	width: 160rpx;
	height: 160rpx;
	border-radius: 16rpx;
	background: #f5f5f5;
}
.prod-content {
	flex: 1;
	margin-left: 20rpx;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
}
.prod-name {
	font-size: 28rpx;
	font-weight: bold;
	color: #333;
}
.prod-spec {
	font-size: 22rpx;
	color: #bbb;
}
.price-row {
	display: flex;
	justify-content: space-between;
}
.price {
	color: #ff4757;
	font-weight: bold;
}
.count {
	color: #999;
}

.detail-line {
	display: flex;
	justify-content: space-between;
	font-size: 26rpx;
	margin-bottom: 20rpx;
	color: #666;
}
.free { color: #2ecc71; }
.total {
	border-top: 1rpx solid #f2f2f2;
	padding-top: 20rpx;
	margin-top: 10rpx;
	color: #333;
}
.final-val {
	color: #ff4757;
	font-weight: bold;
	font-size: 32rpx;
}

.footer-bar {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	height: 120rpx;
	background: #fff;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 0 40rpx;
	box-shadow: 0 -4rpx 20rpx rgba(0,0,0,0.05);
	z-index: 100;
}
.price-total .label { font-size: 24rpx; color: #999; }
.price-total .symbol { font-size: 24rpx; color: #ff4757; font-weight: bold; }
.price-total .val { font-size: 40rpx; color: #ff4757; font-weight: 800; }

.submit-btn {
	background: linear-gradient(135deg, #007aff, #0056b3);
	color: #fff;
	height: 80rpx;
	line-height: 80rpx;
	padding: 0 60rpx;
	border-radius: 40rpx;
	font-size: 28rpx;
	font-weight: bold;
	margin: 0;
}
</style>