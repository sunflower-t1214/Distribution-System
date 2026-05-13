<template>
	<view class="dist-container">
		<view class="dashboard">
			<view class="user-info">
				<text class="title">分销控制台</text>
				<text class="sales-id">分销员ID: {{ salesId }}</text>
			</view>
			<view class="data-grid">
				<view class="data-item">
					<text class="num">￥{{ dashboardData.commission }}</text>
					<text class="label">累计佣金</text>
				</view>
				<view class="data-item">
					<text class="num">￥{{ dashboardData.totalSales }}</text>
					<text class="label">累计推广额</text>
				</view>
				<view class="data-item">
					<text class="num">{{ dashboardData.orderCount }}</text>
					<text class="label">推广订单数</text>
				</view>
			</view>
		</view>

		<view class="action-bar">
			<view class="action-btn" @click="notImplemented">💰 佣金提现</view>
			<view class="action-btn" @click="notImplemented">👥 客户管理</view>
			<view class="action-btn" @click="notImplemented">📋 推广订单</view>
		</view>

		<view class="promote-section">
			<view class="section-title">赚佣金 · 推广商品库 [cite: 91, 95]</view>
			<view class="goods-list">
				<view class="goods-card" v-for="item in promoteList" :key="item.id">
					<image :src="item.img" mode="aspectFill" class="img"></image>
					<view class="info">
						<text class="name">{{ item.name }}</text>
						<view class="price-row">
							<text class="price">售价 ￥{{ item.price }}</text>
							<view class="commission-tag">
								赚 ￥{{ (item.price * item.rate).toFixed(2) }} ({{ item.rate * 100 }}%)
							</view>
						</view>
						<view class="btn-row">
							<button class="share-btn" @click="generateLink(item)">复制推广链接</button>
						</view>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';

const salesId = ref(null);
const BASE_URL = 'http://localhost:8080';

// 🔴 核心：真实数据统计状态 [cite: 315]
const dashboardData = ref({
	commission: '0.00',
	totalSales: '0.00',
	orderCount: 0
});

const promoteList = ref([
	{ id: 1, name: 'HUAWEI Mate 80 Pro Max 旗舰手机', price: 8499, rate: 0.05, img: 'https://images.pexels.com/photos/1092644/pexels-photo-1092644.jpeg?auto=compress&w=200' },
	{ id: 3, name: '主动降噪无线蓝牙耳机 长续航', price: 1299, rate: 0.10, img: 'https://images.pexels.com/photos/3394651/pexels-photo-3394651.jpeg?auto=compress&w=200' },
	{ id: 6, name: '电竞机械键盘 极光定制版', price: 599, rate: 0.15, img: 'https://images.pexels.com/photos/2047905/pexels-photo-2047905.jpeg?auto=compress&w=200' }
]);

// 获取真实统计数据 [cite: 315]
const fetchStatistics = (sId) => {
	uni.request({
		url: `${BASE_URL}/api/sales/statistics`,
		method: 'GET',
		data: { salesId: sId },
		success: (res) => {
			if (res.data.code === 200) {
				dashboardData.value = {
					commission: res.data.commission.toFixed(2),
					totalSales: res.data.totalSales.toFixed(2),
					orderCount: res.data.orderCount
				};
			}
		}
	});
};

onShow(() => {
	const userInfo = uni.getStorageSync('userInfo');
	if (userInfo && (userInfo.userId || userInfo.id)) {
		salesId.value = userInfo.userId || userInfo.id;
		fetchStatistics(salesId.value); // 加载统计数据 [cite: 319, 320]
	} else {
		uni.showToast({ title: '请先登录', icon: 'none' });
		setTimeout(() => uni.navigateTo({ url: '/pages/login/login' }), 1000);
	}
});

// 生成推广链接并获取 shareCode [cite: 302, 306]
const generateLink = (product) => {
	if (!salesId.value) return;

	uni.request({
		url: `${BASE_URL}/api/sales/getShareLink`,
		method: 'GET',
		data: { salesId: salesId.value },
		success: (res) => {
			if (res.data.code === 200) {
				const shareUrl = `${res.data.shareLink}&productId=${product.id}`;
				uni.setClipboardData({
					data: shareUrl,
					success: () => uni.showToast({ title: '推广链接已复制' })
				});
			}
		}
	});
};

const notImplemented = () => uni.showToast({ title: '功能完善中...', icon: 'none' });
</script>

<style scoped>
.dist-container { min-height: 100vh; background: #f4f6f8; padding-bottom: 50rpx; }

/* 顶部看板 */
.dashboard { background: linear-gradient(135deg, #e64340, #ff7e67); padding: 40rpx 30rpx 60rpx; color: #fff; border-radius: 0 0 40rpx 40rpx; }
.user-info { display: flex; justify-content: space-between; align-items: center; margin-bottom: 40rpx; }
.user-info .title { font-size: 36rpx; font-weight: bold; }
.user-info .sales-id { font-size: 24rpx; background: rgba(255,255,255,0.2); padding: 4rpx 16rpx; border-radius: 20rpx; }
.data-grid { display: flex; justify-content: space-between; }
.data-item { flex: 1; display: flex; flex-direction: column; align-items: center; }
.data-item .num { font-size: 40rpx; font-weight: bold; margin-bottom: 10rpx; }
.data-item .label { font-size: 24rpx; opacity: 0.9; }

/* 快捷入口 */
.action-bar { display: flex; justify-content: space-around; background: #fff; margin: -30rpx 30rpx 20rpx; padding: 30rpx 0; border-radius: 16rpx; box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.05); position: relative; z-index: 10; }
.action-btn { font-size: 26rpx; color: #333; font-weight: 500; }

/* 推广列表 */
.promote-section { padding: 20rpx 30rpx; }
.section-title { font-size: 32rpx; font-weight: bold; color: #333; margin-bottom: 20rpx; }
.goods-card { display: flex; background: #fff; border-radius: 16rpx; padding: 20rpx; margin-bottom: 20rpx; }
.goods-card .img { width: 160rpx; height: 160rpx; border-radius: 12rpx; flex-shrink: 0; }
.goods-card .info { margin-left: 20rpx; flex: 1; display: flex; flex-direction: column; justify-content: space-between; }
.info .name { font-size: 28rpx; color: #333; font-weight: 500; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.price-row { display: flex; align-items: center; justify-content: space-between; margin-top: 10rpx; }
.price-row .price { font-size: 26rpx; color: #999; }
.commission-tag { font-size: 22rpx; color: #e64340; background: #ffeeee; padding: 4rpx 12rpx; border-radius: 8rpx; font-weight: bold; }
.btn-row { display: flex; justify-content: flex-end; margin-top: 15rpx; }
.share-btn { margin: 0; padding: 0 30rpx; height: 56rpx; line-height: 56rpx; background: #e64340; color: #fff; font-size: 24rpx; border-radius: 28rpx; }
</style>