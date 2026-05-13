<template>
  <div class="list-container">
    <div class="filter-bar">
      <el-radio-group v-model="sortType">
        <el-radio-button label="all">综合</el-radio-button>
        <el-radio-button label="sales">销量</el-radio-button>
        <el-radio-button label="price">价格</el-radio-button>
      </el-radio-group>
    </div>

    <div class="grid-list">
      <div class="grid-item" v-for="item in productList" :key="item.id">
        <img :src="item.img" /> <div class="item-detail">
          <h4>{{ item.name }}</h4> <div class="meta">
            <span class="price">￥{{ item.price }}</span> <span class="sales">销量 {{ item.sales }}</span> </div>
          <div class="actions">
            <el-button size="small" @click.stop="toDetail(item.id)">详情</el-button>
            <el-button type="primary" size="small" @click.stop="addCart(item)">购买</el-button> </div>
        </div>
      </div>
    </div>
  </div>
</template>