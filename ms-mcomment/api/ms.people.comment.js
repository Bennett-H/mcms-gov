/**
 * 铭飞MS平台-评论插件
 */
(function() {

    
    /**
     * 发布评论
     * @param data
     * @param func
     * @returns
     *
	 *  isCode 是否需要验证码，true为需要，false为不需要。默认需要<br/>
	 *  commentBasicId 商户编号
	 *  commentContent 评论内容 <br/>
	 *  commentCommentId 父评论id(可选)<br/>
	 *  commentPicture 评论图片 (可选)<br/>
	 *  commentPoints 评论分数 (可选)<br/>
     */
    function save(data,func) {
        ms.http.post(ms.base + "/people/comment/save.do",data,{
			headers: {
				'Content-Type': 'application/x-www-form-urlencoded'
			}
		}).then(func, function (err) {
                console.log(err)
            })
    }
    /**
     * 评论列表
     * @param data
     * @param func
     */
    function list(data,func) {
        ms.http.get(ms.base + "/comment/list.do",data,{
		}).then(func, function (err) {
                console.log(err)
            })
    }
    /**
     * 条件查询商户评论
     * @param data
     * @param func
     */
    function list(data,func) {
        ms.http.get(ms.base + "/people/comment/list.do",data,{
		}).then(func, function (err) {
                console.log(err)
            })
    }
    let comment = {
    	save:save,
    	list:list
    
	}
    window.ms.people.comment = comment;
}());