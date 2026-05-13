<template>
	<view class="cart-container">
		<view class="cart-header">
			<view class="header-left">
				<text class="title">购物车</text>
				<text class="count-tag">共 {{ cartList.length }} 件商品</text>
			</view>
			<text class="edit-btn" @click="isEditing = !isEditing">
				{{ isEditing ? '完成' : '编辑' }}
			</text>
		</view>

		<view v-if="cartList.length === 0" class="empty-status">
			<image src="https://img.icons8.com/illustrations/external-tulpahn-outline-color-tulpahn/100/external-shopping-basket-ecommerce-tulpahn-outline-color-tulpahn.png" mode="aspectFit" class="empty-img"></image>
			<text class="empty-text">您的购物车还没有商品</text>
			<button class="go-home-btn" @click="toHome">去精选商城逛逛</button>
		</view>

		<scroll-view v-else scroll-y class="cart-content">
			<view class="cart-item" v-for="(item, index) in cartList" :key="item.id">
				<checkbox :checked="item.checked" @click="toggleItem(index)" color="#007aff" class="item-checkbox" />
				
				<view class="img-wrapper">
					<image :src="item.productImageUrl || getDefaultImg(item.productName)" class="prod-img" mode="aspectFill"></image>
				</view>
				
				<view class="prod-info">
					<text class="prod-name">{{ item.productName || '精选优品' }}</text>
					<text class="prod-spec">官方正品 | 七天无理由退换</text>
					<view class="price-row">
						<text class="price"><text class="symbol">￥</text>{{ item.productPrice }}</text>
						<view class="stepper">
							<view class="step-btn minus" @click="updateCount(index, -1)">-</view>
							<input class="step-input" type="number" :value="item.quantity" disabled />
							<view class="step-btn plus" @click="updateCount(index, 1)">+</view>
						</view>
					</view>
				</view>
				
				<view v-if="isEditing" class="del-mask" @click="removeItem(item.id, index)">
					<text class="del-text">删除</text>
				</view>
			</view>
		</scroll-view>

		<view class="footer-bar" v-if="cartList.length > 0">
			<view class="check-all" @click="toggleAll">
				<checkbox :checked="isAllChecked" color="#007aff" />
				<text>全选</text>
			</view>
			<view class="total-info">
				<text class="total-label">合计:</text>
				<text class="total-price">￥{{ totalPrice }}</text>
			</view>
			<button class="settle-btn" @click="handleSettle">
				结算 <text class="num">({{ totalCount }})</text>
			</button>
		</view>
	</view>
</template>

<script setup>
import { ref, computed } from 'vue';
import { onShow } from '@dcloudio/uni-app';

const cartList = ref([]);
const isEditing = ref(false);
const BASE_URL = 'http://localhost:8080';

// 修改此处链接，确保与首页 recommendProducts 的图片完全一致
const getDefaultImg = (name) => {
	if (!name) return 'https://images.pexels.com/photos/90946/pexels-photo-90946.jpeg?auto=compress&w=400';
	if (name.includes('Mate 80')) return 'https://images.pexels.com/photos/1092644/pexels-photo-1092644.jpeg?auto=compress&w=400';
	if (name.includes('电脑')) return 'https://images.pexels.com/photos/2047905/pexels-photo-2047905.jpeg?auto=compress&w=400';
	if (name.includes('耳机')) return 'https://images.pexels.com/photos/3394651/pexels-photo-3394651.jpeg?auto=compress&w=400';
	if (name.includes('配件')) return 'https://images.pexels.com/photos/4526398/pexels-photo-4526398.jpeg?auto=compress&w=400';
	return 'https://images.pexels.com/photos/90946/pexels-photo-90946.jpeg?auto=compress&w=400';
};

const getCleanUserId = () => {
	let user = uni.getStorageSync('userInfo');
	if (!user) return null;
	let idValue = (typeof user === 'object') ? (user.userId || user.id) : null;
	if (!idValue && typeof user === 'string') {
		try {
			const obj = JSON.parse(user);
			idValue = obj.userId || obj.id;
		} catch(e) { idValue = user; }
	}
	return idValue ? Number(idValue) : null;
};

const loadCartData = () => {
	const finalId = getCleanUserId();
	if (!finalId) return;
	uni.request({
		url: `${BASE_URL}/api/cart/list`,
		data: { userId: finalId },
		success: (res) => {
			if (res.data) {
				cartList.value = res.data.map(item => ({ ...item, checked: true }));
			}
		}
	});
};

onShow(() => { loadCartData(); });

const updateCount = (index, delta) => {
	const item = cartList.value[index];
	if (item.quantity + delta <= 0) return;
	const finalId = getCleanUserId();
	item.quantity += delta;
	uni.request({
		url: `${BASE_URL}/api/cart/add`,
		method: 'POST',
		data: { userId: finalId, productId: item.productId, quantity: delta }
	});
};

const removeItem = (cartId, index) => {
	uni.showModal({
		title: '移除商品',
		content: '确定要删除吗？',
		confirmColor: '#ff4757',
		success: (res) => {
			if (res.confirm) {
				uni.request({
					url: `${BASE_URL}/api/cart/delete`,
					method: 'POST',
					data: { id: cartId },
					success: () => { cartList.value.splice(index, 1); }
				});
			}
		}
	});
};

const totalPrice = computed(() => cartList.value.filter(i => i.checked).reduce((s, i) => s + i.productPrice * i.quantity, 0).toFixed(2));
const totalCount = computed(() => cartList.value.filter(i => i.checked).reduce((s, i) => s + i.quantity, 0));
const isAllChecked = computed(() => cartList.value.length > 0 && cartList.value.every(i => i.checked));

const toggleItem = (index) => { cartList.value[index].checked = !cartList.value[index].checked; };
const toggleAll = () => {
	const s = !isAllChecked.value;
	cartList.value.forEach(i => i.checked = s);
};
const toHome = () => uni.reLaunch({ url: '/pages/home/home' });
const handleSettle = () => {
	if (totalCount.value === 0) return uni.showToast({ title: '请选择商品', icon: 'none' });
	uni.setStorageSync('selectedCartItems', cartList.value.filter(i => i.checked));
	uni.navigateTo({ url: '/pages/order/confirmOrder' });
};
</script>

<style scoped>
/* 样式保持不变 */
.cart-container { background-color: #fcfcfc; min-height: 100vh; padding-bottom: 140rpx; }
.cart-header { background: #fff; padding: 60rpx 40rpx 30rpx; display: flex; justify-content: space-between; align-items: flex-end; }
.title { font-size: 44rpx; font-weight: 800; color: #1a1a1a; }
.count-tag { font-size: 24rpx; color: #999; margin-left: 15rpx; }
.edit-btn { font-size: 28rpx; color: #007aff; font-weight: 500; }
.cart-content { padding: 20rpx 30rpx; }
.cart-item { background: #fff; border-radius: 30rpx; padding: 25rpx; margin-bottom: 25rpx; display: flex; align-items: center; position: relative; box-shadow: 0 10rpx 30rpx rgba(0,0,0,0.03); }
.img-wrapper { width: 180rpx; height: 180rpx; border-radius: 20rpx; overflow: hidden; margin-right: 25rpx; background: #f5f5f5; }
.prod-img { width: 100%; height: 100%; }
.prod-info { flex: 1; height: 180rpx; display: flex; flex-direction: column; justify-content: space-between; }
.prod-name { font-size: 30rpx; font-weight: bold; color: #333; line-height: 1.4; }
.prod-spec { font-size: 22rpx; color: #bbb; }
.price-row { display: flex; justify-content: space-between; align-items: center; }
.price { color: #ff4757; font-size: 36rpx; font-weight: 800; }
.symbol { font-size: 24rpx; }
.stepper { display: flex; align-items: center; background: #f7f8fa; border-radius: 100rpx; padding: 4rpx; }
.step-btn { width: 50rpx; height: 50rpx; line-height: 46rpx; text-align: center; font-size: 32rpx; color: #333; }
.step-input { width: 60rpx; text-align: center; font-size: 26rpx; font-weight: bold; }
.del-mask { position: absolute; right: 0; top: 0; bottom: 0; width: 120rpx; background: #ff4757; border-radius: 0 30rpx 30rpx 0; display: flex; align-items: center; justify-content: center; z-index: 2; }
.del-text { color: #fff; font-size: 26rpx; font-weight: bold; }
.footer-bar { position: fixed; bottom: 0; width: 100%; height: 120rpx; background: rgba(255,255,255,0.95); backdrop-filter: blur(10px); display: flex; align-items: center; padding: 0 40rpx; box-sizing: border-box; border-top: 1rpx solid #f0f0f0; z-index: 100; }
.total-info { flex: 1; display: flex; flex-direction: column; align-items: flex-end; margin-right: 30rpx; }
.total-label { font-size: 22rpx; color: #999; }
.total-price { color: #ff4757; font-size: 40rpx; font-weight: 800; }
.settle-btn { background: linear-gradient(135deg, #007aff, #0056b3); color: #fff; height: 84rpx; line-height: 84rpx; border-radius: 42rpx; padding: 0 50rpx; font-size: 30rpx; font-weight: bold; box-shadow: 0 10rpx 20rpx rgba(0,122,255,0.2); }
.num { font-size: 24rpx; margin-left: 6rpx; opacity: 0.9; }
.empty-status { margin-top: 200rpx; display: flex; flex-direction: column; align-items: center; }
.empty-img { width: 400rpx; height: 400rpx; opacity: 0.8; }
.empty-text { color: #ccc; font-size: 28rpx; margin-top: -40rpx; }
.go-home-btn { margin-top: 60rpx; background: #333; color: #fff; border-radius: 100rpx; font-size: 26rpx; padding: 0 60rpx; height: 80rpx; line-height: 80rpx; }
</style>