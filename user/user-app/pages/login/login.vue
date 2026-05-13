<template>
	<view class="login-page">
		<view class="top-decoration"></view>
		
		<view class="main-content">
			<view class="header">
				<view class="welcome">{{ isLogin ? '欢迎回来' : '加入我们' }}</view>
				<view class="hint">{{ isLogin ? '请登录您的分销账号' : '创建一个新的分销账号' }}</view>
			</view>

			<view class="form-container">
				<view class="input-group" v-if="!isLogin">
					<view class="label">姓名/昵称</view>
					<input class="input" v-model="formData.name" placeholder="请输入您的姓名" />
				</view>

				<view class="input-group">
					<view class="label">手机号</view>
					<input class="input" v-model="formData.phone" placeholder="请输入手机号" type="number" maxlength="11" />
				</view>
				<view class="input-group">
					<view class="label">密码</view>
					<input class="input" v-model="formData.password" placeholder="请输入密码" password />
				</view>
				
				<button class="btn-login" @click="handleSubmit">
					{{ isLogin ? '立即登录' : '立即注册' }}
				</button>
			</view>
			
			<view class="footer">
				<text class="link-text" @click="toggleMode">
					{{ isLogin ? '没有账号？点击注册' : '已有账号？点击登录' }}
				</text>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref } from 'vue';

const isLogin = ref(true); // 切换模式
const formData = ref({
	phone: '',
	password: '',
	name: '' // 仅注册使用
});

// 切换登录/注册模式
const toggleMode = () => {
	isLogin.value = !isLogin.value;
};

/**
 * 统一处理提交逻辑
 */
const handleSubmit = () => {
	const { phone, password, name } = formData.value;
	
	// 基础校验
	if (!phone || !password) {
		return uni.showToast({ title: '请填写完整信息', icon: 'none' });
	}
	if (!isLogin.value && !name) {
		return uni.showToast({ title: '注册请填写姓名', icon: 'none' });
	}

	// 根据模式选择接口地址
	const apiPath = isLogin.value ? '/user/login' : '/user/register';

	uni.request({
		url: 'http://localhost:8080' + apiPath,
		method: 'POST',
		data: formData.value,
		success: (res) => {
			console.log(isLogin.value ? "登录返回:" : "注册返回:", res.data);
			
			// 成功判定 (适配你的后端返回结构)
			if (res.data && (res.data.code === 200 || res.data.userId || res.data.id)) {
				const userInfo = res.data.data || res.data;
				
				// 1. 持久化存储用户信息
				uni.setStorageSync('userInfo', userInfo);
				uni.setStorageSync('userRole', userInfo.role || (userInfo.salesId ? 'SALES' : 'USER'));
				
				handleBindRelation(userInfo.userId || userInfo.id);
				handleBindRelation(userInfo.userId || userInfo.id);
				
				uni.showToast({ 
					title: isLogin.value ? '登录成功' : '注册成功', 
					icon: 'success' 
				});
				
				setTimeout(() => {
					uni.reLaunch({ url: '/pages/home/home' });
				}, 1000);
				
			} else {
				uni.showModal({
					title: '提示',
					content: res.data?.msg || '操作失败，请重试',
					showCancel: false
				});
			}
		},
		fail: () => {
			uni.showToast({ title: '服务器连接失败', icon: 'none' });
		}
	});
};

/**
 * 分销绑定逻辑
 */
const handleBindRelation = (uId) => {
	const shareCode = uni.getStorageSync('pendingShareCode');
	if (shareCode && uId) {
		console.log("检测到推广码，正在绑定...", shareCode);
		uni.request({
			url: 'http://localhost:8080/api/sales/bind',
			method: 'POST',
			data: { shareCode, userId: uId },
			success: (res) => {
				if(res.data.code === 200) {
					console.log("分销关系绑定成功");
					uni.removeStorageSync('pendingShareCode');
				}
			}
		});
	}
};
</script>

<style>
/* 样式保持不变 */
.login-page { min-height: 100vh; background-color: #f8f9fa; position: relative; overflow: hidden; }
.top-decoration { position: absolute; top: -50px; left: -50px; width: 150%; height: 250px; background: linear-gradient(135deg, #007aff, #00c6ff); border-radius: 50%; opacity: 0.8; }
.main-content { position: relative; z-index: 10; padding: 50px 30px; }
.header { color: #333; margin-bottom: 50px; text-align: center; }
.welcome { font-size: 30px; font-weight: bold; color: #007aff; }
.hint { font-size: 14px; opacity: 0.8; margin-top: 10px; }
.form-container { background-color: white; border-radius: 15px; padding: 25px 20px; box-shadow: 0 5px 15px rgba(0,0,0,0.05); }
.input-group { margin-bottom: 20px; }
.label { font-size: 14px; color: #666; margin-bottom: 8px; }
.input { height: 45px; border-bottom: 1px solid #eee; font-size: 16px; width: 100%; }
.btn-login { background: linear-gradient(to right, #007aff, #0089ff); color: white; border-radius: 25px; margin-top: 20px; border: none; width: 100%; height: 45px; font-size: 16px; display: flex; align-items: center; justify-content: center; }
.footer { text-align: center; margin-top: 30px; }
.link-text { color: #007aff; font-size: 14px; text-decoration: underline; }
</style>