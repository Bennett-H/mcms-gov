<!DOCTYPE html>
<html>
<head>
    <title>资源库管理</title>
    <#include "../../include/head-file.ftl">
    <script src="${base}/static/plugins/wl-explorer/0.1.4/wl-explorer.umd.js"></script>
    <link rel="stylesheet" href="${base}/static/plugins/wl-explorer/wl-explorer.css">
</head>
<body>
<div id="index" class="ms-index" v-cloak>
            <wl-explorer
			 ref="wlExplorer"
             size="small"
			 :columns="file_table_columns"
			 :is-folder-fn="isFolderFn"

			 @handleFolder="handleFolder"
      		 @upload="fileUpload"
      		 @search="fileSearch"
      		 @del="fileDel">
			    <!-- 头部按钮区 -->
    			<el-form
    			  class="wl-header-btn"
    			  :inline="true"
				  size="mini"
    			  @submit.native.prevent
    			>
    			  <el-form-item>
    			    <el-button type="primary" @click="handleFolder('add')">新增文件夹</el-button>
    			    <el-button>编辑文件夹</el-button>
    			    <el-button>上传文件</el-button>
					<el-input placeholder="请输入关键字搜索" @keyup.enter.native="fileSearch()">
          				<el-button slot="append" icon="el-icon-search file-search" @click="fileSearch()"></el-button>
        			</el-input>
    			    <!-- solt自定义头部按钮区 -->
    			    <slot name="header-btn"></slot>
    			  </el-form-item>
    			</el-form>
			</wl-explorer>
</div>
</body>

</html>
<script>
    var indexVue = new Vue({
        el: '#index',

        data:{
            //	wlexplorer组件参数
			file_table_columns: [
				{
        		  label: "名称",
        		  prop: "Name",
        		},
        		{
        		  label: "修改日期",
        		  align: "center",
        		  formatter(row) {
        		    return row.EditTime.split("T")[0] || "-";
        		  },
        		},
        		{
        		  label: "类型",
        		  align: "center",
        		  width: 80,
        		  formatter(row) {
        		    return row.Type === 1 ? "文件夹" : row.SuffixName;
        		  },
        		},
        		{
        		  label: "大小",
        		  width: 80,
        		  align: "center",
        		  formatter(row) {
        		    if (row.Size === null) return "-";
        		    if (row.Size < 1024) {
        		      // 1024以下显示kb
        		      return row.Size + "KB";
        		    }
        		    if (row.Size < _GB) {
        		      // 1024*1024
        		      let _mb = (row.Size / 1024).toFixed(2);
        		      return parseFloat(_mb) + "MB";
        		    }
        		    let _gb = (row.Size / _GB).toFixed(2);
        		    return parseFloat(_gb) + "GB";
        		  },
        		},
			], // 自定义表格列
			file_table_data: [], // 表格数据
			all_folder_list: [], // 所有文件夹列表
      		tree_folder_list: [], // 树形文件夹列表
			explorer_prop: {
        		name: "Name",
        		match: "Name",
        		splic: true,
        		suffix: "SuffixName",
        		pathId: "Id",
        		pathPid: "ParentId",
        		pathName: "Name",
        		pathChildren: "Children", // String 路径数据 children字段
        		pathConnector: "\\", // String 路径父子数据拼接连接符,默认为'\'
        		pathParents: "Parents", // String 路径数据所有直系祖先节点自增长identityId逗号拼接
        		pathIdentityId: "IdentityId", // String 路径数据自增长id
      		}, // 文件管理器配置项
			path: {}, // 路径数据
			fade: {
        		folder: false
      		}, // 弹出类视图管理
        },
        methods:{

            // 判断是否文件夹函数
    		isFolderFn: function(row) {
    		  return row.Type === this.type.folder;
    		},
			/**
    		 * 编辑文件夹
    		 * act:Object 当前选中文件夹
    		 * type:String 操作类型 add添加edit编辑
    		 * file:Object 当前路径信息
    		 */
    		handleFolder(act, type, file) {
    		  this.path = file;
    		  this.fade.folder = true;
    		  if (type === "add") {
    		    this.$refs["folder_form"].resetFields();
    		    this.folder_form.Id = "";
    		    this.folder_form.ParentId = file.id;
    		    return;
    		  }
    		  this.child_act_saved = act;
    		  this.folder_form = { ...act };
    		},
			/**
    		 * @name 上传文件提交操作
    		 */
    		fileUpload(file, cb) {
    		  this.uploadOptions.bb = 1;
    		  cb();
    		},
			/**
    		 * 根据关键词搜索文件
    		 * file: Object 文件属性
    		 * update: Boolean 数据是否需要更新（不需要表示已存在）
    		 */
    		fileSearch(file, update) {
    		  if (update) {
    		    this.path = file;
    		    this.getFileList();
    		  }
    		},
			    // 删除文件
    		fileDel(data) {
    		  let cannot_del_data = []; // 收集不可删除数据
    		  let normal_data_file = []; // 收集可删除文件
    		  let normal_data_folder = []; // 收集可删除文件夹
    		  data.forEach(function (i) {
    		    i.RourceType !== this.rource_type.self
    		      ? cannot_del_data.push(i) // 不可删除数据
    		      : i.Type === this.type.folder
    		      ? normal_data_folder.push(i.Id) // 可删除文件夹
    		      : normal_data_file.push(i.Id); // 可删除文件
    		  });
    		  // 不可删除数据进行提示
    		  if (cannot_del_data.length > 0) {
    		    let _msg = '<p class="title">以下文件或文件夹不可删除，已自动过滤</p>';
    		    cannot_del_data.forEach(function (i) {
    		      _msg += `<p class="msg">${i.Name}</p>`;
    		    });
    		    this.$message({
    		      dangerouslyUseHTMLString: true,
    		      showClose: true,
    		      message: _msg,
    		      type: "warning",
    		      customClass: "mulit-msg",
    		    });
    		  }
    		  if (normal_data_folder.length === 0 && normal_data_file.length === 0)
    		    return;
    		  // 可删除数据正常删除
    		  let _data = {
    		    FolderIds: normal_data_folder,
    		    FolderFileIds: normal_data_file,
    		  };
    		  delFileApi(_data).then(function (res)  {
    		    if (res.data.StatusCode === apiok) {
    		      this.file_table_data = this.file_table_data.filter(
    		        function (i) {return !normal_data_file.concat(normal_data_folder).includes(i.Id)} 
    		      );
    		      this.$message({
    		        showClose: true,
    		        message: res.data.Message,
    		        type: "success",
    		      });
    		    }
    		  });
    		},

        },
        created:function(){
        },
    })
</script>
<style>
    #index .ms-container {
        height: calc(100vh - 78px);
    }
</style>
