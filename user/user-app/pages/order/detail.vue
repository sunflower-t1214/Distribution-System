<template>
	<view class="detail-container">
		<view class="status-banner" :class="orderData.status">
			<view class="status-content">
				<text class="status-title">{{ orderData.statusName }}</text>
				<text class="status-desc">{{ getStatusDesc(orderData.status) }}</text>
			</view>
			<image v-if="orderData.status === 'unpaid'" src="https://img.icons8.com/fluency/96/wallet.png" class="status-icon"></image>
			<image v-else-if="orderData.status === 'done'" src="https://img.icons8.com/fluency/96/ok--v1.png" class="status-icon"></image>
			<image v-else src="https://img.icons8.com/fluency/96/delivery.png" class="status-icon"></image>
		</view>

		<view class="info-card">
			<view class="address-box">
				<image src="https://img.icons8.com/fluency/48/marker.png" class="loc-icon"></image>
				<view class="address-info">
					<view class="user-row">
						<text class="user-name">{{ user.name || '用户' }}</text>
						<text class="user-phone">{{ user.phone || '138****8888' }}</text>
					</view>
					<text class="address-text">广东省深圳市南山区某某科技大厦 8 楼</text>
				</view>
			</view>
		</view>

		<view class="goods-card">
			<view class="card-title">商品清单</view>
			<view class="goods-item" v-for="(item, index) in orderData.items" :key="index">
				<image :src="item.productImageUrl || getDefaultImg(item.productName)" mode="aspectFill" class="item-img"></image>
				<view class="item-info">
					<text class="item-name">{{ item.productName }}</text>
					<text class="item-spec">官方正品 | 极速发货</text>
					<view class="item-price-row">
						<text class="item-price">￥{{ item.productPrice }}</text>
						<text class="item-count">x{{ item.quantity }}</text>
					</view>
				</view>
			</view>
		</view>

		<view class="order-info-card">
			<view class="info-row">
				<text class="label">订单编号</text>
				<view class="value-box">
					<text class="value">{{ orderData.orderSn }}</text>
					<text class="copy-btn" @click="copyText(orderData.orderSn)">复制</text>
				</view>
			</view>
			<view class="info-row">
				<text class="label">下单时间</text>
				<text class="value">{{ orderData.createTime }}</text>
			</view>
			<view class="info-row">
				<text class="label">支付方式</text>
				<text class="value">在线支付</text>
			</view>
		</view>

		<view class="price-detail-card">
			<view class="price-row">
				<text>商品总额</text>
				<text>￥{{ orderData.totalAmount }}</text>
			</view>
			<view class="price-row">
				<text>运费</text>
				<text>+￥0.00</text>
			</view>
			<view class="price-row final">
				<text class="final-label">实付款</text>
				<text class="final-price">￥{{ orderData.totalAmount }}</text>
			</view>
		</view>

		<view class="footer-actions">
			<button class="action-btn ghost" @click="contactService">联系客服</button>
			<block v-if="orderData.status === 0">
				<button class="action-btn ghost">取消订单</button>
				<button class="action-btn primary">立即付款</button>
			</block>
			<block v-if="orderData.status === 2">
				<button class="action-btn ghost">删除订单</button>
				<button class="action-btn primary" @click="reBuy">再次购买</button>
			</block>
		</view>
	</view>
</template>

<script setup>
import { ref, onLoad } from 'vue';
import request from '@/utils/request';

const orderData = ref({
	id: null,
	orderSn: '',
	status: 0,
	statusName: '',
	totalAmount: 0,
	createTime: '',
	items: []
});
const user = uni.getStorageSync('userInfo') || {};

// 页面加载时获取订单详情
onLoad((options) => {
	if (!options.id) {
		uni.showToast({ title: '参数错误', icon: 'none' });
		uni.navigateBack();
		return;
	}
	fetchOrderDetail(options.id);
});

const fetchOrderDetail = async (id) => {
	try {
		const res = await request({ url: `/api/order/list`, data: { userId: 0 } })
		const list = res.data || []
		const found = list.find(o => o.id == id)
		if (found) {
			orderData.value = { ...found, statusName: getStatusName(found.status) }
		} else {
			uni.showToast({ title: '订单不存在', icon: 'none' })
		}
	} catch (e) {
		console.log('网络详细报错:', e)
	}
}

// 状态映射（和list.vue完全统一）
const getStatusName = (s) => {
	const statusMap = { 0: '待付款', 1: '待发货', 2: '已完成', 3: '已取消' };
	return statusMap[s] || '未知';
};
const getStatusDesc = (s) => {
	const descMap = { 0: '请尽快完成支付', 1: '商家正在处理订单', 2: '订单已完成', 3: '订单已取消' };
	return descMap[s] || '';
};

// 默认图兜底（和其他页面完全统一）
const getDefaultImg = (name) => {
	if (!name) return 'https://images.pexels.com/photos/90946/pexels-photo-90946.jpeg?auto=compress&w=200';
	if (name.includes('Mate 80')) return 'https://images.pexels.com/photos/1092644/pexels-photo-1092644.jpeg?auto=compress&w=200';
	if (name.includes('电脑')) return 'https://images.pexels.com/photos/2047905/pexels-photo-2047905.jpeg?auto=compress&w=200';
	if (name.includes('耳机')) return 'https://images.pexels.com/photos/3394651/pexels-photo-3394651.jpeg?auto=compress&w=200';
	if (name.includes('充电器')) return 'https://images.pexels.com/photos/4526398/pexels-photo-4526398.jpeg?auto=compress&w=200';
	return 'https://images.pexels.com/photos/90946/pexels-photo-90946.jpeg?auto=compress&w=200';
};

// 复制订单号
const copyText = (text) => {
	uni.setClipboardData({
		data: text,
		success: () => uni.showToast({ title: '复制成功', icon: 'success' })
	});
};

// 联系客服
const contactService = () => uni.showToast({ title: '客服功能开发中', icon: 'none' });

// 再次购买
const reBuy = () => {
	// 把订单商品加入购物车
	const items = orderData.value.items.map(item => ({
		productId: item.productId,
		productName: item.productName,
		productPrice: item.productPrice,
		quantity: 1,
		productImageUrl: item.productImageUrl,
		checked: true
	}));
	uni.setStorageSync('selectedCartItems', items);
	uni.navigateTo({ url: '/pages/cart/cart' });
};
</script>

<style scoped>
.detail-container { background-color: #f8f9fb; min-height: 100vh; padding-bottom: 140rpx; }

.status-banner { 
	height: 240rpx; background: linear-gradient(135deg, #4b7bec, #3867d6); 
	padding: 0 50rpx; display: flex; align-items: center; justify-content: space-between; color: #fff;
}
.status-banner[class*="0"] { background: linear-gradient(135deg, #fa8231, #eb3b5a); }
.status-banner[class*="2"] { background: linear-gradient(135deg, #20bf6b, #0fb9b1); }

.status-title { font-size: 40rpx; font-weight: bold; display: block; margin-bottom: 10rpx; }
.status-desc { font-size: 24rpx; opacity: 0.8; }
.status-icon { width: 100rpx; height: 100rpx; }

.info-card, .goods-card, .order-info-card, .price-detail-card {
	background: #fff; margin: -40rpx 24rpx 24rpx; border-radius: 24rpx; padding: 30rpx;
	box-shadow: 0 10rpx 30rpx rgba(0,0,0,0.03);
}
.goods-card, .order-info-card, .price-detail-card { margin-top: 0; }

.address-box { display: flex; align-items: center; }
.loc-icon { width: 48rpx; height: 48rpx; margin-right: 20rpx; }
.user-name { font-size: 32rpx; font-weight: bold; margin-right: 20rpx; }
.user-phone { font-size: 26rpx; color: #999; }
.address-text { font-size: 26rpx; color: #666; margin-top: 10rpx; display: block; }

.card-title { font-size: 30rpx; font-weight: bold; margin-bottom: 30rpx; padding-bottom: 20rpx; border-bottom: 1rpx solid #f5f5f5; }
.goods-item { display: flex; margin-bottom: 30rpx; }
.item-img { width: 140rpx; height: 140rpx; border-radius: 16rpx; background: #f9f9f9; }
.item-info { flex: 1; margin-left: 20rpx; display: flex; flex-direction: column; justify-content: space-between; }
.item-name { font-size: 28rpx; font-weight: bold; color: #333; line-height: 1.4; }
.item-spec { font-size: 22rpx; color: #bbb; }
.item-price-row { display: flex; justify-content: space-between; }
.item-price { font-weight: bold; color: #ff4757; }
.item-count { color: #999; font-size: 24rpx; }

.info-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24rpx; }
.info-row:last-child { margin-bottom: 0; }
.label { font-size: 26rpx; color: #999; }
.value { font-size: 26rpx; color: #333; }
.value-box { display: flex; align-items: center; }
.copy-btn { font-size: 22rpx; color: #007aff; margin-left: 15rpx; background: #eef6ff; padding: 2rpx 12rpx; border-radius: 6rpx; }

.price-row { display: flex; justify-content: space-between; margin-bottom: 20rpx; font-size: 26rpx; color: #666; }
.price-row.final { margin-top: 30rpx; padding-top: 30rpx; border-top: 1rpx solid #f5f5f5; color: #333; }
.final-label { font-weight: bold; font-size: 30rpx; }
.final-price { font-size: 36rpx; font-weight: 800; color: #ff4757; }

.footer-actions {
	position: fixed; bottom: 0; width: 100%; height: 110rpx; background: #fff;
	display: flex; align-items: center; justify-content: flex-end; padding: 0 30rpx;
	box-sizing: border-box; border-top: 1rpx solid #eee; z-index: 100;
}
.action-btn { height: 70rpx; line-height: 68rpx; border-radius: 35rpx; font-size: 26rpx; margin: 0 0 0 20rpx; padding: 0 36rpx; }
.action-btn.ghost { border: 1rpx solid #ddd; color: #666; background: #fff; }
.action-btn.primary { background: #007aff; color: #fff; border: none; font-weight: bold; }
</style>