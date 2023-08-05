<!DOCTYPE html>
<html>
<head>
    <title>关注记录</title>
    <#include "../../include/head-file.ftl">
</head>
<body>
	<div id="form" v-loading="loading" v-cloak>
		<div class="ms-search">
			<el-row>
				<el-form  ref="searchForm" :rules="rules" label-width="120px" size="mini">
					<el-row>
						<el-col :span="12">
							<el-form-item  label="用户昵称" prop="peopleName">
								<el-input v-model="form.peopleName"
										  :disabled="false"
										  :style="{width:  '100%'}"
										  :clearable="true"
										  placeholder="请输入完整用户昵称，不支持模糊查询">
								</el-input>
							</el-form-item>
						</el-col>
						<el-col :span="12" style="text-align: right;">
							<el-button type="primary" icon="el-icon-search" size="mini" @click="loading=true;currentPage=1;list()">查询</el-button>
							<el-button size="mini" icon="iconfont icon-fanhui" plain onclick="javascript:history.go(-1)">返回</el-button>
						</el-col>
					</el-row>
				</el-form>
			</el-row>
		</div>
		<el-main class="ms-container">
			<div style="display: flex;flex-direction: column;flex: 1;">
				<el-table height="calc(100vh - 68px)" v-loading="loading" ref="multipleTable" border :data="dataList" tooltip-effect="dark">
					<template slot="empty">
						{{emptyText}}
					</template>
					<el-table-column label="业务编号" width="200" align="left" prop="dataId" show-overflow-tooltip>
					</el-table-column>
					<el-table-column label="业务缩略图"  width="200" align="center" prop="collectionDataImg">
						<template slot-scope="scope" v-if="scope.row.collectionDataImg">
							<span v-for="(picture,index) in JSON.parse(scope.row.collectionDataImg)">
								<el-image :preview-src-list="[ms.base+picture.path]" :src="ms.base + picture.path"
											  style="width: 50px;height: 50px;line-height: 50px;font-size: 30px; margin-right: 10px">
									<template slot="error" class="image-slot">
										<i class="el-icon-picture-outline"></i>
									</template>
								</el-image>
							</span>
						</template>
					</el-table-column>
					<el-table-column label="业务名称"  width="200" align="left" prop="collectionDataTitle" show-overflow-tooltip>
					</el-table-column>
					<el-table-column label="业务链接"  width="200" align="left" prop="collectionDataUrl">
					</el-table-column>
					<el-table-column  width="200" align="left" prop="collectionDataJson">
						<template slot='header'>业务数据
							<el-popover placement="top-start" title="提示" trigger="hover" >
								表示为: 业务扩展信息的JSON<br/>
								<i class="el-icon-question" slot="reference"></i>
							</el-popover>
						</template>
					</el-table-column>
					<el-table-column label="用户编号" align="left" prop="peopleId">
					</el-table-column>
					<el-table-column label="用户名称"  width="150" align="left" prop="peopleName">
					</el-table-column>
					<el-table-column label="用户IP" align="left" prop="collectionIp">
					</el-table-column>
					<el-table-column label="关注时间"  width="200" align="center" prop="createDate">
					</el-table-column>
				</el-table>
				<el-pagination
						background
						:page-sizes="[10,20,30,40,50,100]"
						layout="total, sizes, prev, pager, next, jumper"
						:current-page="currentPage"
						:page-size="pageSize"
						:total="total"
						class="ms-pagination"
						@current-change='currentChange'
						@size-change="sizeChange">
				</el-pagination>
			</div>
		</el-main>
	</div>
</body>
</html>
<script>
    var form = new Vue({
        el: '#form',
        data:function() {
            return {
            	dataList:[],
                loading:false,
                saveDisabled: false,
                //表单数据
                form: {
					// 用户昵称
					peopleName: '',
                },
				emptyText:'',//提示文字
				total: 0, //总记录数量
				pageSize: 10, //页面数量
				currentPage:1, //初始页
				manager: ms.manager,
				rules:{
					// 单行文本
					collectionPeopleName: [{"min":0,"max":30,"message":"用户昵称长度限制为0-30"}],
				},
            }
        },
        methods: {
            //获取当前关注记录
            list:function(id,type) {
                var that = this;
                // 加载动画
                this.loading = true
                ms.http.post(ms.manager + "/attention/collection/list.do", {
                	dataId:id == undefined ? that.form.dataId : id,
					dataType:type == undefined ? that.form.dataType : type,
					peopleName: that.form.peopleName
                }).then(function (res) {
                    that.loading = false
                    if(res.result&&res.data) {
                    	that.emptyText = ''
                        that.dataList = res.data.rows;
                    	if(that.dataList.length==0){
							that.emptyText = '暂无数据'
						}
                    } else {
                    	that.emptyText = '暂无数据'
					}
                });
            },
			isChecked: function(row) {
				if(row.del == 3) {
					return false;
				}
				return true;
			},
			//currentPage改变时会触发
			currentChange:function(currentPage) {
				this.loading = true;
				this.currentPage = currentPage;
				this.list(true);
			},
			//pageSize改变时会触发
			sizeChange:function(pagesize) {
				this.loading = true;
				this.pageSize = pagesize;
				this.list(true);
			}
        },
        created:function() {
            var that = this;
            this.form.dataId = ms.util.getParameter("id");
			this.form.dataType = ms.util.getParameter("type");
            if (this.form.dataId&&this.form.dataType) {
				that.list(this.form.dataId,this.form.dataType);
            }
        }
    });
</script>
