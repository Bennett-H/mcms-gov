<!-- 微信图文素材内容展示页面 -->
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <meta content="width=device-width,initial-scale=1.0,maximum-scale=1.0,user-scalable=no" name="viewport">
        <meta content="telephone=no" name="format-detection">
        <meta content="email=no" name="format-detection">
        <title>${articleEntity.articleTitle!}</title>
        <style>
            img{
                width: 100%;
            }
            .ms-container {
                padding-bottom: 0;
            }
        </style>
    </head>
    <body>
        <h1 style="width: 50%;margin: auto">${articleEntity.articleTitle!}</h1>
        <div style="width: 50%;margin-top: 20px; margin-right: auto; margin-left: auto ">
            作者:  <span >${articleEntity.articleAuthor!}</span>
        </div>

        <div style="width: 50%;margin: auto">
            ${articleEntity.articleContent}
        </div>

    </body>
</html>