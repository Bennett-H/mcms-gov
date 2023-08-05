
<script type="text/x-template" id="ms-cropper">
<div>
    <div class="cropper-content">
      <!-- 剪裁框 -->
      <div class="cropper">
        <vueCropper ref="cropper" 
        :img="option.img" 
        :outputSize="option.size" 
        :outputType="option.outputType" 
        :info="true" 
        :full="option.full" 
        :canMove="option.canMove" 
        :canMoveBox="option.canMoveBox" 
        :original="option.original" 
        :autoCrop="option.autoCrop" 
        :autoCropWidth="option.autoCropWidth" 
        :autoCropHeight="option.autoCropHeight"
        :fixedBox="option.fixedBox" 
        @realTime="realTime" 
        :fixed="option.fixed" 
        :fixedNumber="fixedNumber">
        </vueCropper>
      </div>
      <!-- 预览框 -->
      <div class="show-preview" :style="{ 'overflow': 'hidden', 'display':'flex', 'align-items' : 'center'}">
        <div :style="previews.div" class="preview">
          <img :src="previews.url" :style="previews.img">
        </div>
      </div>
    </div>
    <div class="footer-btn">
      <!-- 缩放旋转按钮 -->
      <div class="scope-btn">
        <el-button type="primary" size='mini' icon="el-icon-zoom-in" @click="changeScale(1)"></el-button>
        <el-button type="primary" size='mini' icon="el-icon-zoom-out" @click="changeScale(-1)"></el-button>
        <el-button type="primary" icon="el-icon-refresh-left" size='mini' @click="rotateLeft"></el-button>
        <el-button type="primary" icon="el-icon-refresh-right" size='mini' @click="rotateRight"></el-button>
        <span class="label">长:</span><el-input-number v-model="option.autoCropWidth" size="mini" controls-position="right" :min="1"></el-input-number>
        <span class="label">宽:</span><el-input-number v-model="option.autoCropHeight" size="mini" controls-position="right" :min="1"></el-input-number>
      </div>
    </div>
  </div>
</script>
<script>

	Vue.component('ms-cropper',{
		template: "#ms-cropper",
		name: "ms-cropper",
    props: {
      imgFile: {
        type: Array,
        default: []
      },
      fixedNumber: {
        type: Array,
      }
    },
		data: function() {
			return {
        previews: {}, // 预览数据
        option: {
          img: '', // 裁剪图片的地址  (默认：空)
          outputSize: 1, // 裁剪生成图片的质量  (默认:1)
          full: true, // 是否输出原图比例的截图 选true生成的图片会非常大  (默认:false)
          outputType: 'png', // 裁剪生成图片的格式  (默认:jpg)
          canMove: true, // 上传图片是否可以移动  (默认:true)
          original: true, // 上传图片按照原始比例渲染  (默认:false)
          canMoveBox: true, // 截图框能否拖动  (默认:true)
          autoCropWidth:300,
          autoCropHeight:300,
          autoCrop: true, // 是否默认生成截图框  (默认:false)
          fixedBox: false, // 固定截图框大小 不允许改变  (默认:false)
          fixed: false, // 是否开启截图框宽高固定比例  (默认:true)
          centerBox:true,
          infoTrue:true
        },
        downImg: '#'
			}
		},
		watch:{
      imgFile: function(n) {
        if(n.url) {
          this.option.img = n.url
        }
      }
		},
		methods: {
      changeScale: function (num) {
        // 图片缩放
        num = num || 1
        this.$refs.cropper.changeScale(num)
      },
      rotateLeft: function () {
        // 向左旋转
        this.$refs.cropper.rotateLeft()
      },
      rotateRight: function () {
        // 向右旋转
        this.$refs.cropper.rotateRight()
      },
      realTime: function (data) {
        // 实时预览
        this.previews = data
      },
      //  类型为blob和base64，默认为base64格式返回
      uploadType: function (type) {
        // 将剪裁好的图片转换格式后回传给父组件
        event.preventDefault()
        var that = this
        if (type === 'blob') {
          this.$refs.cropper.getCropBlob(function(blob){
            return blob
          })
        } else {
          this.$refs.cropper.getCropData(function(base64){
            return base64
          })
        }
      }
    },
		created: function () {
      if(this.imgFile.url) {
        this.option.img = this.imgFile.url
      }
		}
	})
</script>
<style scoped>
.cropper-content {
  display: flex;
  display: -webkit-flex;
  justify-content: flex-end;
  -webkit-justify-content: flex-end;
}
.cropper-content .cropper {
  width: 350px;
  height: 300px;
}
.cropper-content .show-preview {
  flex: 1;
  -webkit-flex: 1;
  display: flex;
  display: -webkit-flex;
  justify-content: center;
  -webkit-justify-content: center;
  overflow: hidden;
  border: 1px solid #cccccc;
  background: #cccccc;
  margin-left: 40px;
}
.preview {
  overflow: hidden;
  border: 1px solid #cccccc;
  background: #cccccc;
}
.footer-btn {
  margin: 12px 0;
  display: flex;
  display: -webkit-flex;
  justify-content: flex-end;
  -webkit-justify-content: flex-end;
}
.footer-btn .scope-btn {
  flex: 1;
  -webkit-flex: 1;
  display: flex;
  display: -webkit-flex;
  align-items: center;
}
.footer-btn .scope-btn .label{
  padding: 0 8px 0 16px;
}
.footer-btn .scope-btn button {
    margin-right: 12px;
}
.footer-btn .upload-btn {
  flex: 1;
  -webkit-flex: 1;
  display: flex;
  display: -webkit-flex;
  justify-content: center;
  -webkit-justify-content: center;
}
.footer-btn .btn {
  outline: none;
  display: inline-block;
  line-height: 1;
  white-space: nowrap;
  cursor: pointer;
  -webkit-appearance: none;
  text-align: center;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  outline: 0;
  margin: 0;
  -webkit-transition: 0.1s;
  transition: 0.1s;
  font-weight: 500;
  padding: 8px 15px;
  font-size: 12px;
  border-radius: 3px;
  color: #fff;
  background-color: #67c23a;
  border-color: #67c23a;
}
</style>
