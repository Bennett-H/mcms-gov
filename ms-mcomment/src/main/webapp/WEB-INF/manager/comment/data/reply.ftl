<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>回复管理</title>
    <#include "/include/head-file.ftl"/>

</head>

<body>
<div id="form" v-cloak>
    <el-header class="ms-header" height="50px">
        <div style="float: right;">

            <el-button size="mini" @click="back()"><i class="iconfont icon-fanhui"></i>返回</el-button>
        </div>
    </el-header>
    <el-main class="ms-container">
        <el-scrollbar class="ms-scrollbar" style="height: 100%;">
            <el-divider content-position="left">评论详情</el-divider>
            <el-form label-width="120px" :model="comment" class="comment-table-expand">
                <el-form-item label="评论昵称:" style="display: inline;">

                    {{comment.peopleId>0?comment.peopleName:(comment.peopleId<0?'游客':'管理员')}}

                </el-form-item>
                <el-form-item label="被业务评论数据:" style="display: inline;">
                    <span v-text="comment.dataTitle"></span>
                </el-form-item>
                <el-form-item label="评价内容:" style="display: inline;">
                    <span v-text="comment.commentContent"></span>
                </el-form-item>
                <el-form-item label="评分等级:" style="display: inline;">
                    <el-rate v-model="comment.commentPoints" style="margin-top: 10px" disabled show-text :texts="comment.texts"></el-rate>
                </el-form-item>
                <el-form-item label="所在地区:" style="display: inline;">
                    <span v-text="comment.commentIp"></span>
                </el-form-item>
                <el-form-item label="评价时间:" style="display: inline;">
                    <span v-text="comment.commentTime"></span>
                </el-form-item>
                <el-form-item label="评论图片:" v-if="comment.commentPicture" style="display: inline;">
					<span v-for="(picture,index) in JSON.parse(comment.commentPicture)">
						<el-image :preview-src-list="[ms.base+picture.path]" :src="ms.base + picture.path"
                                  style="width: 50px;height: 50px;line-height: 50px;font-size: 30px; margin-right: 10px">
							<template slot="error" class="image-slot">
								<i class="el-icon-picture-outline"></i>
							</template>
						</el-image>
					</span>
                </el-form-item>
                <el-form-item label="评论附件:" v-if="comment.commentFileJson" style="display: inline;">
					<span v-for="(commentFile,index) in JSON.parse(comment.commentFileJson)">
						<a :href="commentFile.path" target="_blank" style="margin-right: 10px">{{commentFile.name}}</a>
					</span>
                </el-form-item>
                <el-form-item label="评论点赞数:" v-if="comment.commentFileJson" style="display: inline;">
                    <span v-text="comment.commentLike"></span>
                </el-form-item>
            </el-form>
            <@shiro.hasPermission name="comment:comment:audit">
                <el-divider content-position="left">回复评论</el-divider>
                <el-row>
                    <el-col :span="24">
                        <el-form  :model="comment" :rules="rules" size="mini">
                            <el-form-item  prop="replyCommentContent">
                                <el-input v-model="comment.replyCommentContent"
                                          :disabled="false"
                                          :style="{width:  '100%'}"
                                          :clearable="true"
                                          type="textarea" rows="4" placeholder="请输入回复内容">
                                </el-input>
                            </el-form-item>
                            <el-form-item >
                                <el-button size="mini" type="primary" @click="saveOrUpdate(comment)"><i class="iconfont icon-baocun"></i>回复评论
                                </el-button>
                            </el-form-item>
                        </el-form>
                    </el-col>
                </el-row>
            </@shiro.hasPermission>
            <el-divider content-position="left">评论回复列表</el-divider>
            <el-table v-loading="loading" class="ms-table-pagination"
                      ref="multipleTable" :data="commentDataList" border tooltip-effect="dark"
                      @selection-change="commentHandleSelectionChange">
                <template slot="empty">
                    {{emptyText}}
                </template>
                <el-table-column type="selection" width="40"></el-table-column>
                <el-table-column type="expand">
                    <template slot-scope="scope">
                        <el-form label-position="left" inline class="comment-table-expand">
                            <el-form-item label="评论图片" v-if="scope.row.commentPicture" style="display: inline;">
								<span v-for="(picture,index) in JSON.parse(scope.row.commentPicture)">
									<el-image :preview-src-list="[ms.base+picture.path]" :src="ms.base + picture.path"
                                              style="width: 50px;height: 50px;line-height: 50px;font-size: 30px; margin-right: 10px">
										<template slot="error" class="image-slot">
											<i class="el-icon-picture-outline"></i>
										</template>
									</el-image>
								</span>
                            </el-form-item>
                            <el-form-item label="评论附件" v-if="scope.row.commentFileJson" style="display: inline;">
								<span v-for="(commentFile,index) in JSON.parse(scope.row.commentFileJson)">
									<a :href="commentFile.path" target="_blank" style="margin-right: 10px">{{commentFile.name}}</a>
								</span>
                            </el-form-item>
                            <el-form-item label="评分等级" style="display: inline;">
                                <el-rate v-model="scope.row.commentPoints" disabled allow-half
                                         text-color="#ff9900"></el-rate>
                            </el-form-item>
                            <el-form-item label="评论点赞数" style="display: inline;">
                                <span>{{ scope.row.commentLike }}</span>
                            </el-form-item>
                        </el-form>
                    </template>
                </el-table-column>

                <el-table-column label="评论者编号 > 被评论者编号" prop="peopleId" width="200">
                    <template slot-scope="scope">
                        {{scope.row.peopleId}} > {{scope.row.parentPeopleId}}
                    </template>
                </el-table-column>
                <el-table-column label="评论者昵称 > 被评论者昵称" prop="puNickname" width="250">
                    <template slot-scope="scope">

                        {{scope.row.peopleId>0?scope.row.peopleName:(scope.row.peopleId<0?'游客':'管理员')}} > {{scope.row.parentComment}}

                    </template>
                </el-table-column>

                <el-table-column label="评论内容" prop="commentContent" show-overflow-tooltip></el-table-column>

                <el-table-column label="ip" prop="commentIp" width="180" show-overflow-tooltip ></el-table-column>
                <el-table-column label="评价时间" width="180" prop="commentTime" align="center"></el-table-column>
                <el-table-column label="状态" width="80" prop="commentAudit" align="center">
                    <template slot-scope="scope">
                        <el-tag v-if="scope.row.commentAudit" type="danger">
                            未审核
                        </el-tag>
                        <el-tag v-else type="success">
                            已审核
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="160" align="center">
                    <template slot-scope="scope">
                        <@shiro.hasPermission name="comment:comment:audit">
                            <el-link v-if="scope.row.commentAudit" type="primary" :underline="false"
                                     @click="audit(scope.row, 0)">通过
                            </el-link>
                            <el-link v-else type="primary" :underline="false"
                                     @click="audit(scope.row, 1)">不通过
                            </el-link>
                        </@shiro.hasPermission>
                        <@shiro.hasPermission name="comment:comment:reply">
                        <#--                            <el-link type="primary" :underline="false"-->
                        <#--                                     :href="manager+'/comment/data/reply.do?id='+scope.row.id">回复-->
                        <#--                            </el-link>-->
                            <el-link type="primary" :underline="false" @click="reply(scope.row)">回复</el-link>
                        </@shiro.hasPermission>
                        <@shiro.hasPermission name="comment:comment:del">
                            <el-link type="primary" :underline="false" @click="del([scope.row])">删除
                            </el-link></@shiro.hasPermission>
                    </template>
                </el-table-column>
            </el-table>
        </el-scrollbar>
    </el-main>
    <el-dialog title="评论回复" :visible.sync="dialogVisible" width="50%"  :close-on-click-modal="false" v-cloak>
        <el-form label-width="130px" :model="sonComment"  >
            <el-form-item label="评论昵称:" style="display: inline;">
                {{comment.peopleId>0?comment.puNickname:(comment.peopleId<0?'游客':'管理员')}}
            </el-form-item>
            <el-form-item label="被业务评论数据:" style="display: inline;">
                <span v-text="sonComment.dataTitle"></span>
            </el-form-item>
            <el-form-item label="评价内容:" style="display: inline;">
                <span v-text="sonComment.commentContent"></span>
            </el-form-item>
            <el-form-item label="评分等级:" style="display: inline;">
                <el-rate v-model="sonComment.commentPoints" disabled show-text :texts="comment.texts"></el-rate>
            </el-form-item>
            <el-form-item label="ip:" style="display: inline;">
                <span v-text="sonComment.commentIp"></span>
            </el-form-item>
            <el-form-item label="评价时间:" style="display: inline;">
                <span v-text="sonComment.commentTime"></span>
            </el-form-item>
            <el-form-item label="评论图片:" v-if="sonComment.commentPicture" style="display: inline;">
					<span v-for="(picture,index) in JSON.parse(sonComment.commentPicture)">
						<el-image :preview-src-list="[ms.base+picture.path]" :src="ms.base + picture.path"
                                  style="width: 50px;height: 50px;line-height: 50px;font-size: 30px; margin-right: 10px">
							<template slot="error" class="image-slot">
								<i class="el-icon-picture-outline"></i>
							</template>
						</el-image>
					</span>
            </el-form-item>
            <el-form-item label="评论附件:" v-if="sonComment.commentFileJson" style="display: inline;">
					<span v-for="(commentFile,index) in JSON.parse(sonComment.commentFileJson)">
						<a :href="commentFile.path" target="_blank" style="margin-right: 10px">{{commentFile.name}}</a>
					</span>
            </el-form-item>
            <el-form-item label="评论点赞数:" v-if="comment.commentFileJson" style="display: inline;">
                <span v-text="comment.commentLike"></span>
            </el-form-item>
        </el-form>
        <el-divider content-position="left">回复评论</el-divider>
        <el-row>
            <el-col :span="24">
                <el-form  :model="sonComment" :rules="rules" size="mini">
                    <el-form-item  prop="replyCommentContent">
                        <el-input v-model="sonComment.replyCommentContent"
                                  :disabled="false"
                                  :style="{width:  '100%'}"
                                  :clearable="true"
                                  type="textarea" rows="4" placeholder="请输入回复内容">
                        </el-input>
                    </el-form-item>
                </el-form>
            </el-col>
        </el-row>
        <div slot="footer">
            <el-button size="mini" @click="dialogVisible = false">取 消</el-button>
            <el-button size="mini" type="primary" @click="saveOrUpdate(sonComment)">回 复</el-button>
        </div>
    </el-dialog>

</div>



</body>
</html>
<script>
    var replyVue = new Vue({
        el: '#form',
        data: {

            dialogVisible:false,
            loading: true,
            commentId: 0, //评论编号
            sonComment: {}, // 子评论回复数据
            commentDataList: [], //评论表列表
            manager: ms.manager,
            emptyText:'',
            comment: {
                texts: ['1分', '2分', '3分', '4分', '5分'],		//评分辅助提示文字
                orderNum: '',		//订单号
                againValuate: '',			//追加评价
                commentContent: '' //评价商品
            },
            replyComment: {},// 回复评论的数据
            replyList: null,
            peopleIds:[],
            dictList: [],//字典数据
            rules: {
                // 评论内容
                'replyCommentContent': [{
                    min: 0,
                    max: 255,
                    message: "评论内容长度限制为0-255个字符！"
                }],
            }
        },
        methods: {
            //返回
            back: function () {
                window.history.back(-1);
            },
            reply:function (row){
                this.replyComment = {}
                this.commentData = {};
                if (row.peopleInfo){
                    var peopleInfo = JSON.parse(row.peopleInfo);
                    row.puIcon = peopleInfo.puIcon;
                }
                this.sonComment = row;
                this.dialogVisible = true;

            },
            //保存
            saveOrUpdate: function (row) {
                var that = this;
                var data = this.dictList.find(function (data) {
                    return data.dictValue == row.dataType;
                })
                //保存
                that.replyComment.id = null;
                that.replyComment.dataTitle = that.comment.dataTitle;
                that.replyComment.commentId = row.id;
                that.replyComment.dataId = row.dataId;
                that.replyComment.dataType = data.dictLabel;
                that.replyComment.peopleId = 0;
                that.replyComment.commentContent = row.replyCommentContent;
                if(!that.replyComment.commentContent){
                    that.$notify({
                        title: '失败',
                        message: '回复不能为空',
                        type: 'warning'
                    });
                    return;
                }
                ms.http.post(ms.manager + "/comment/data/save.do", that.replyComment).then(function (res) {
                    if (res.result) {
                        that.$notify({
                            title: '成功',
                            message: '评论回复成功!',
                            type: 'success'
                        });
                        //
                        if (row.topId != '0') {
                            that.dialogVisible = false;
                        }
                        that.get(that.id);
                        that.replyComment.commentContent = '';
                        that.comment.replyCommentContent = '';
                        that.sonComment.replyCommentContent = '';

                    } else {
                        that.$notify({
                            title: '失败',
                            message: res.msg,
                            type: 'warning'
                        });
                    }
                });
            },
            //评论表列表选中
            commentHandleSelectionChange:function(val){
                this.commentSelectionList = val;
            },
            //删除
            del: function(row){
                var that = this;
                that.$confirm('此操作将永久删除所选内容, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(function() {
                    ms.http.post(ms.manager+"/comment/data/delete.do", row.length?row:[row],{
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    }).then(
                        function(res){
                            if (res.result) {
                                that.$notify({
                                    title:'成功',
                                    type: 'success',
                                    message: '删除成功!'
                                });
                                //删除成功，刷新列表
                                that.getByParentCommentId(that.comment);
                                that.getDataType();
                            }else {
                                that.$notify({
                                    title: '失败',
                                    message: res.msg,
                                    type: 'warning'
                                });
                            }
                        });
                }).catch(function() {
                    that.$notify({
                        type: 'info',
                        title:'成功',
                        message: '已取消删除'
                    });
                });
            },
            //获取评论信息
            get: function (id) {
                var that = this;
                ms.http.get(ms.manager + "/comment/data/getComment.do", {
                    id: id
                }).then(function (res) {
                    if (res.data.id > 0) {
                        var comment = res.data;
                        if (comment.peopleInfo){
                            var peopleInfo = JSON.parse(comment.peopleInfo);
                            comment.puIcon = peopleInfo.puIcon;
                        }
                        // 处理ip显示
                        if (comment.commentIp){
                            comment.commentIp = JSON.parse(comment.commentIp).addr;
                        }
                        // 设置类型参数为字典label
                        var data = that.dictList.find(function (data) {
                            return data.dictValue == comment.dataType;
                        })
                        comment.dataType = data.dictLabel;
                        that.comment = comment;
                        that.getByParentCommentId(that.comment);
                    } else {
                        that.$notify({
                            title: '失败',
                            message: res.msg,
                            type: 'warning'
                        });
                    }
                });
            },
            //根据父评论id查询子评论
            getByParentCommentId: function (comment) {

                var that = this;
                var data = that.dictList.find(function (data) {
                    return data.dictValue == comment.dataType;
                })
                // 增加当前评论  这里可以使用allCommentList
                var commentDataList = [];
                // 对commentDataList置空，防止通过子评论回复时，数据叠加
                that.commentDataList = [];
                commentDataList.push(comment);

                ms.http.post(ms.manager + "/comment/data/list.do", {
                    topId: comment.id,
                    dataType: comment.dataType,
                    pageSize: 999999
                }).then(function (res) {
                    if (res.data.total > 0) {
                        // 获取子评论的用户id
                        for (var i=0;i<res.data.rows.length;i++){
                            var comment = res.data.rows[i];
                            if (comment.peopleInfo){
                                var peopleInfo = JSON.parse(comment.peopleInfo);
                                comment.puIcon = peopleInfo.puIcon;
                            }
                            // 处理ip显示
                            if (comment.commentIp){
                                comment.commentIp = JSON.parse(comment.commentIp).addr;
                            }
                            that.commentDataList.push(comment);

                        }
                        commentDataList = commentDataList.concat(that.commentDataList);
                        // 处理评论之间的关联关系   commentData是被评论对象
                        commentDataList.forEach(function (commentData){
                            // comment是评论对象
                            commentDataList.forEach(function (comment){
                                // 子评论中有commentId为0 那就是自己评论自己
                                if (commentData.id == comment.commentId || comment.commentId == 0){
                                    // 会员评论
                                    if(commentData.peopleId > 0){
                                        // 被评论者
                                        comment.parentComment = commentData.peopleName;
                                        // 被评论者编号
                                        comment.parentPeopleId = commentData.peopleId;
                                        // 管理员或游客评论
                                    }else {
                                        comment.parentComment = commentData.peopleId<0?'游客':'管理员';
                                        comment.parentPeopleId = commentData.peopleId;
                                    }
                                }
                            })
                        })

                    }else {
                        that.emptyText = "暂无子评论数据";
                    }
                })
                that.loading=false;
            },
            //审核
            audit: function(row, commentAudit){
                var that = this;
                row.commentAudit = commentAudit;
                ms.http.post(ms.manager + "/comment/data/updateComment.do",row	, {
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }).then(function(res) {
                    if (res.result) {
                        if (commentAudit) {
                            that.$notify({
                                title: '成功',
                                message: '修改为审核通过!',
                                type: 'success'
                            });
                        }else {
                            that.$notify({
                                title: '成功',
                                message: '修改为审不通过!',
                                type: 'success'
                            });
                        }
                    }
                });
            },
            //获取评论类型 字典数据
            dictListGet: function () {
                var that = this;
                //获取  模板类型 字典数据
                ms.mdiy.dict.list('评论类型').then(function (res) {
                    that.dictList = res.data.rows
                    // 初始化界面
                    that.id = ms.util.getParameter("id");
                    that.get(that.id);
                })
            },
        },
        mounted() {
            this.dictListGet();
        }
    });
</script>
<style>
    body{
        overflow: hidden
    }
    #comment  a {
        color: #0099ff;
        cursor: pointer;
    }
    #comment .iconfont {
        font-size: 12px;
        margin-right: 5px;
    }
    #comment .ms-search {
        background: #fff;
        padding-top: 20px;
    }

    #comment .el-date-editor--datetime.el-input--mini{
        width:100%;
    }

    #comment .ms-container {
        margin: 12px;
        height: calc(100vh - 89px);
        padding: 14px 14px 0 14px;
        background: #fff;
    }

    .comment-table-expand {
        font-size: 0;
        margin: 10px;
    }
    .comment-table-expand  label {
        width: 90px;
        color: #99a9bf;
    }
    .comment-table-expand el-form-item {
        margin-right: 0;
        margin-bottom: 0;
        width: 50%;
    }
</style>
