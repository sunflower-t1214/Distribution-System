<template>
	<view class="order-container">
		<view class="tabs-header">
			<view 
				v-for="(tab, index) in tabList" 
				:key="index" 
				class="tab-item" 
				:class="{ active: currentTab === index }"
				@click="switchTab(index)"
			>
				{{ tab }}
				<view class="active-line"></view>
			</view>
		</view>

		<scroll-view scroll-y class="order-content">
			<view v-if="orderList.length === 0" class="empty-box">
				<image src="https://cdn-icons-png.flaticon.com/512/4076/4076432.png" mode="aspectFit"></image>
				<text>暂时没有相关订单</text>
				<button class="go-home" @click="goHome">去商城逛逛</button>
			</view>

			<view v-else class="order-card" v-for="(order, index) in filteredOrders" :key="order.id">
				<view class="card-header">
					<text class="order-no">订单号：{{ order.orderSn }}</text>
					<text class="status-text" :class="getStatusClass(order.status)">
						{{ getStatusName(order.status) }}
					</text>
				</view>
				
				<view class="card-body" @click="goDetail(order.id)">
					<image 
						:src="(order.items && order.items[0]?.productImageUrl) || getDefaultImg(order.items?.[0]?.productName)" 
						mode="aspectFill" 
						class="goods-img"
					></image>
					<view class="goods-info">
						<text class="name">{{ (order.items && order.items[0]?.productName) || '精选商品' }}</text>
						<view class="price-row">
							<text class="price">￥{{ order.totalAmount }}</text>
							<text class="count">x{{ (order.items && order.items[0]?.quantity) || 1 }}</text>
						</view>
					</view>

					<button v-if="order.status === 0" class="del-btn" @click.stop="deleteOrder(order.id)">删除</button>
				</view>

				<view class="card-footer">
					<view class="total-info">
						合计：<text class="total-price">￥{{ order.totalAmount }}</text>
					</view>
					<view class="btn-group">
						<button class="btn ghost" @click="goDetail(order.id)">详情</button>
						<button v-if="order.status === 0" class="btn primary" @click.stop="payOne(order.id)">付款</button>
					</view>
				</view>
			</view>
		</scroll-view>

		<view class="total-bottom-bar" v-if="totalPendingPrice > 0">
			<view class="left">
				<text>待付款合计：</text>
				<text class="total-price">￥{{ totalPendingPrice }}</text>
			</view>
			<button class="pay-all-btn" @click="payAll">一键付款</button>
		</view>
	</view>
</template>

<script setup>
import { ref, computed } from 'vue';
import { onShow } from '@dcloudio/uni-app';

const tabList = ['全部', '待付款', '待发货', '已完成'];
const currentTab = ref(0);
const orderList = ref([]);
const BASE_URL = 'http://localhost:8080';

const getCleanUserId = () => {
    let userInfo = uni.getStorageSync('userInfo');
    if (!userInfo) return null;
    let idValue = null;
    if (typeof userInfo === 'string') {
        try {
            const parsed = JSON.parse(userInfo);
            idValue = parsed.userId || parsed.id;
        } catch (e) {}
    } else {
        idValue = userInfo.userId || userInfo.id;
    }
    return idValue ? Number(idValue) : null;
};

const fetchOrders = () => {
	const finalUserId = getCleanUserId();
	if (!finalUserId || isNaN(finalUserId)) {
		uni.showToast({ title: '请先登录', icon: 'none' });
		return;
	}

	uni.request({
		url: `${BASE_URL}/api/order/list`,
		method: 'GET',
		data: { userId: finalUserId },
		success: (res) => {
			// 根据你的后端返回格式，如果有 data 包装就取 data，没有就直接取 res.data
			orderList.value = res.data.data || res.data || [];
		},
		fail: () => {
			uni.showToast({ title: '网络连接失败', icon: 'none' });
		}
	});
};

onShow(() => {
	fetchOrders();
});

const getStatusName = (s) => ['待付款', '待发货', '已完成', '已取消'][s] || '未知';
const getStatusClass = (s) => ['unpaid', 'unsent', 'done', 'cancel'][s];

const filteredOrders = computed(() => {
	if (currentTab.value === 0) return orderList.value;
	return orderList.value.filter(item => item.status === (currentTab.value - 1));
});

// 待付款总金额
const totalPendingPrice = computed(() => {
    return orderList.value
        .filter(o => o.status === 0)
        .reduce((sum, item) => sum + Number(item.totalAmount || 0), 0)
        .toFixed(2);
});

// ---------------------
// 单个订单付款
// ---------------------
const payOne = (orderId) => {
    // 【核心修复】复用后端的 payAll 接口，将单个 id 包装成数组传给后端
    uni.request({
        url: `${BASE_URL}/api/order/payAll`,
        method: 'POST',
        data: { ids: [orderId] },
        success: (res) => {
			if(res.data.code === 200) {
				uni.showToast({ title: '付款成功' });
            	fetchOrders(); // 刷新列表
			} else {
				uni.showToast({ title: res.data.msg || '付款失败', icon: 'none' });
			}
        }
    });
};

// ---------------------
// 🔴 一键付款（所有待付款）
// ---------------------
const payAll = () => {
    const unpaidOrders = orderList.value.filter(o => o.status === 0);
    if (unpaidOrders.length === 0) {
        uni.showToast({ title: '暂无待付款订单', icon: 'none' });
        return;
    }

    const ids = unpaidOrders.map(o => o.id);
    uni.request({
        url: `${BASE_URL}/api/order/payAll`,
        method: 'POST',
        data: { ids },
        success: (res) => {
			if(res.data.code === 200) {
				uni.showToast({ title: '全部付款成功' });
            	fetchOrders(); // 刷新列表
			}
        }
    });
};

// ---------------------
// 删除订单
// ---------------------
const deleteOrder = (orderId) => {
    uni.showModal({
        title: '确认删除',
        content: '确定删除该订单？',
        success: (res) => {
            if (res.confirm) {
                uni.request({
                    url: `${BASE_URL}/api/order/delete`,
                    method: 'POST',
                    data: { id: orderId },
                    success: (response) => {
						if(response.data.code === 200) {
							uni.showToast({ title: '删除成功' });
                        	fetchOrders(); // 刷新列表
						}
                    }
                });
            }
        }
    });
};

const getDefaultImg = (name) => {
	if (!name) return 'https://images.pexels.com/photos/90946/pexels-photo-90946.jpeg?auto=compress&w=200';
	if (name.includes('Mate 80')) return 'https://images.pexels.com/photos/1092644/pexels-photo-1092644.jpeg?auto=compress&w=200';
	if (name.includes('电脑')) return 'https://images.pexels.com/photos/2047905/pexels-photo-2047905.jpeg?auto=compress&w=200';
	if (name.includes('耳机')) return 'https://images.pexels.com/photos/3394651/pexels-photo-3394651.jpeg?auto=compress&w=200';
	if (name.includes('充电器')) return 'https://images.pexels.com/photos/4526398/pexels-photo-4526398.jpeg?auto=compress&w=200';
	return 'https://images.pexels.com/photos/90946/pexels-photo-90946.jpeg?auto=compress&w=200';
};

const switchTab = (index) => { currentTab.value = index; };
const goDetail = (id) => { uni.navigateTo({ url: `/pages/order/detail?id=${id}` }); };
const goHome = () => { 
	// 如果首页是 tabbar 请使用 switchTab
	uni.reLaunch({ url: '/pages/home/home' }); 
};
</script>

<style scoped>
.order-container { background-color: #f8f8f8; min-height: 100vh; display: flex; flex-direction: column; padding-bottom: 120rpx; }
.tabs-header { display: flex; background-color: #fff; height: 100rpx; position: sticky; top: 0; z-index: 10; box-shadow: 0 4rpx 10rpx rgba(0,0,0,0.02); }
.tab-item { flex: 1; display: flex; flex-direction: column; align-items: center; justify-content: center; font-size: 28rpx; color: #888; position: relative; }
.tab-item.active { color: #007aff; font-weight: bold; }
.active-line { position: absolute; bottom: 10rpx; width: 40rpx; height: 6rpx; background-color: #007aff; border-radius: 10rpx; display: none; }
.tab-item.active .active-line { display: block; }
.order-content { flex: 1; padding: 20rpx; box-sizing: border-box; }
.order-card { background-color: #fff; border-radius: 24rpx; padding: 30rpx; margin-bottom: 24rpx; box-shadow: 0 10rpx 40rpx rgba(0,0,0,0.03); }
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30rpx; padding-bottom: 20rpx; border-bottom: 1rpx solid #f9f9f9; }
.order-no { font-size: 22rpx; color: #bbb; }
.status-text { font-size: 26rpx; font-weight: bold; }
.status-text.unpaid { color: #ff9f43; }
.status-text.done { color: #2ecc71; }
.card-body { display: flex; align-items: center; position: relative; }
.goods-img { width: 140rpx; height: 140rpx; border-radius: 20rpx; background-color: #f5f5f5; }
.goods-info { flex: 1; margin-left: 24rpx; }
.goods-info .name { font-size: 28rpx; color: #333; font-weight: 500; display: block; margin-bottom: 10rpx; }
.price-row { display: flex; justify-content: space-between; }
.price { color: #333; font-weight: bold; font-size: 30rpx; }
.count { color: #bbb; font-size: 24rpx; }
.card-footer { margin-top: 30rpx; display: flex; justify-content: space-between; align-items: center; }
.total-info { font-size: 24rpx; color: #999; }
.total-price { font-size: 34rpx; color: #ff4757; font-weight: 800; margin-left: 6rpx; }
.btn-group { display: flex; gap: 16rpx; }
.btn { height: 64rpx; line-height: 64rpx; padding: 0 32rpx; border-radius: 32rpx; font-size: 24rpx; margin: 0; }
.btn.ghost { border: 1rpx solid #eee; color: #666; background: #fff; }
.btn.primary { background: #007aff; color: #fff; box-shadow: 0 8rpx 16rpx rgba(0,122,255,0.15); }
.empty-box { padding-top: 200rpx; display: flex; flex-direction: column; align-items: center; }
.empty-box image { width: 240rpx; height: 240rpx; opacity: 0.6; margin-bottom: 40rpx; }
.empty-box text { color: #bbb; font-size: 28rpx; margin-bottom: 60rpx; }
.go-home { background: #333; color: #fff; border-radius: 100rpx; font-size: 26rpx; padding: 0 60rpx; height: 80rpx; line-height: 80rpx; }

.del-btn {
    position: absolute;
    right: 0;
    top: 50%;
    transform: translateY(-50%);
    background: #ff4757;
    color: #fff;
    font-size: 22rpx;
    padding: 8rpx 16rpx;
    border-radius: 8rpx;
    z-index: 2; /* 确保点击层级在上方 */
}

/* 底部一键付款 */
.total-bottom-bar {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    height: 100rpx;
    background: #fff;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 30rpx;
    box-shadow: 0 -2rpx 12rpx rgba(0,0,0,0.05);
}
.left {
    font-size: 28rpx;
    color: #333;
}
.pay-all-btn {
    background: #007aff;
    color: #fff;
    border-radius: 50rpx;
    padding: 0 40rpx;
    height: 70rpx;
    line-height: 70rpx;
    font-size: 28rpx;
}
</style>