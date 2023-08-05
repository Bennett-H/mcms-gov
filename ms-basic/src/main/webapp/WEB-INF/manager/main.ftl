<html>

<head>
    <meta charset="utf-8"/>
    <title>控制台</title>
    <#include "../../include/head-file.ftl">
    <link rel="stylesheet" href="${base}/static/plugins/jquery.liMarquee/liMarquee.css">
    <style>
        [v-cloak] {
            display: none;
        }
        .str_wrap,.str_wrap.str_active  {
            background-color: #fdf6ec;
        }

        html,body {
        }
        .menu-link {
            font-size: 16px;
        }
        #app {
            height: 100%;
            overflow-y: scroll;
            overflow-x: -moz-scrollbars-none;
        }

        .news-list-body {
            height: 200px;
            overflow: hidden;
        }
        .news-list-body ul li {
            height: 30px;
            color: #409EFF;
            white-space:nowrap;
            ext-overflow: ellipsis;
            overflow: hidden;
            width: 280px;

        }
        a:link, a:visited, a:active {
            text-decoration: none;
            color: #303133;
            font-size: 16px;
        }
        a:hover {
            color: #409EFF !important;
        }

        .ms-console {
            width: 100%;
            display: flex;
            flex-direction: row;
            flex: 1;
            flex-shrink: 0;
            flex-wrap: nowrap;
            background-color: rgba(238, 238, 238, 1);
            box-sizing: border-box;
            margin-left: 0px;
            padding: 10px;

        }


        .ms-console .ms-console-left {
            width: 75%;
            min-height: 50px;
            display: flex;
            flex-direction: column;
            flex-shrink: 0;
            flex-wrap: nowrap;
            margin-left: 0px;
            padding-top: 20px;
            padding-right: 20px;
            padding-bottom: 20px;
            padding-left: 20px;
            background-color: rgba(255, 255, 255, 1);
            box-sizing: border-box;
            margin-top: 0px;
            margin-bottom: 0px;
        }


        .ms-console .ms-console-left .ms-panel {
            width: 100%;
            min-height: 50px;
            display: flex;
            flex-direction: column;
            flex-shrink: 0;
            flex-wrap: nowrap;
            margin-left: 0px;
            padding-top: 0px;
            padding-right: 0px;
            padding-bottom: 0px;
            padding-left: 0px;
            box-sizing: border-box;
        }


        .ms-console .ms-console-left .ms-panel .ms-panel-title {
            width: 100%;
            height: unset;
            min-height: 50px;
            display: flex;
            flex-direction: row;
            flex-shrink: 0;
            flex-wrap: nowrap;
            align-items: center;
            margin-left: 0px;
            padding-top: 0px;
            padding-right: 0px;
            padding-bottom: 0px;
            padding-left: 0px;
            box-sizing: border-box;
        }


        .ms-console .ms-console-left .ms-panel .ms-panel-title i {
            font-size: 20PX;
            margin-left: 0px;
            padding-top: 0px;
            padding-right: 0px;
            padding-bottom: 0px;
            padding-left: 0px;
        }


        .ms-console .ms-console-left .ms-panel .ms-panel-title .ms-panel-txt {
            flex-direction: row;
            display: inline-block;
            word-wrap: break-word;
            font-weight: 500;
            font-size: 18PX;
            margin-top: 0px;
            margin-bottom: 0px;
            margin-left: 8px;
            padding-top: 0px;
            padding-right: 0px;
            padding-bottom: 0px;
            padding-left: 0px;
        }


        .ms-console .ms-console-left .ms-panel .ms-panel-body {
            width: 100%;
            min-height: 50px;
            display: flex;
            flex-direction: row;
            flex-shrink: 1;
            flex-wrap: wrap;
            padding-top: 0px;
            padding-right: 0px;
            padding-bottom: 0px;
            padding-left: 0px;
            box-sizing: border-box;
            margin-top: 0px;
            margin-bottom: 0px;
        }

        .space-around {
            justify-content: space-between;
        }

        .space-around {
            justify-content: space-between;
        }
        .ms-console .ms-console-left .ms-panel .ms-panel-body .menu-item-empty {
            display: flex;
            justify-content: center;
            align-items: center;
            flex: 1;
        }

        .ms-console .ms-console-left .ms-panel .ms-panel-body .menu-item {
            width: 20%;
            height: unset;
            min-height: 50px;
            display: flex;
            flex-direction: column;
            flex-shrink: 0;
            flex-wrap: nowrap;
            justify-content: center;
            align-items: center;
            padding-top: 0px;
            padding-right: 0px;
            padding-bottom: 0px;
            padding-left: 0px;
            box-sizing: border-box;
            margin-top: 16px;
            margin-bottom: 16px;
            margin-left: 0px;
        }


        .ms-console .ms-console-left .ms-panel .ms-panel-body .menu-item .menu-link {
            width: unset;
            height: 100%;
            min-height: 50px;
            display: flex;
            flex-direction: column;
            flex-shrink: 0;
            flex-wrap: nowrap;
            align-items: center;
            margin-top: 0px;
            margin-bottom: 0px;
            box-sizing: border-box;
        }

        .menu-link:hover {
            color: #409EFF;
            cursor: pointer;
        }


        .ms-console .ms-console-left .ms-panel .ms-panel-body .menu-item .menu-link .menu-icon {
            font-size: 42PX;
            margin-top: 0px;
        }


        .ms-console .ms-console-left .ms-panel .ms-panel-body .menu-item .menu-link .menu-text {
            flex-direction: row;
            display: inline-block;
            word-wrap: break-word;
            margin-top: 5px;
            padding-top: 0px;
            padding-right: 0px;
            padding-bottom: 0px;
            padding-left: 0px;
            margin-left: 0px;
        }


        .ms-console .ms-console-left .ms-panel .ms-panel-body .text-link {
            width: 20%;
            height: unset;
            min-height: 50px;
            display: flex;
            flex-direction: row;
            flex-shrink: 0;
            flex-wrap: nowrap;
            justify-content: flex-start;
            align-items: center;
            padding-top: 0px;
            padding-right: 0px;
            padding-bottom: 0px;
            padding-left: 0px;
            box-sizing: border-box;
            margin-top: 0px;
            margin-bottom: 0px;
            margin-left: 0px;
        }


        .ms-console .ms-console-left .ms-panel .ms-panel-body .text-link .text-links-icon {
            margin-top: 0px;
            margin-bottom: 0px;
            margin-left: 0px;
            padding-top: 0px;
            padding-right: 0px;
            padding-bottom: 0px;
            padding-left: 0px;
        }


        .ms-console .ms-console-left .ms-panel .ms-panel-body .text-link .text-links-text {
            flex-direction: row;
            display: inline-block;
            word-wrap: break-word;
            margin-top: 0px;
            padding-top: 0px;
            padding-right: 0px;
            padding-bottom: 0px;
            padding-left: 0px;
            margin-left: 8px;
        }


        .ms-console .ms-console-left .ms-panel .ms-panel-body .img-link {
            width: 24%;
            height: 200px;
            min-height: 50px;
            display: flex;
            flex-direction: column;
            flex-shrink: 0;
            flex-wrap: nowrap;
            justify-content: center;
            align-items: center;
            padding-top: 0px;
            padding-right: 0px;
            padding-bottom: 0px;
            padding-left: 0px;
            box-sizing: border-box;
            margin-top: 16px;
            margin-bottom: 16px;
            margin-left: 0px;
            position: relative;
            border-radius: 20px;
            overflow: hidden;
        }


        .ms-console .ms-console-left .ms-panel .ms-panel-body .img-link img {
            width: 100%;
            height: 100%;
            margin-top: 0px;
            margin-bottom: 0px;
            margin-left: 0px;
            padding-top: 0px;
            padding-right: 0px;
            padding-bottom: 0px;
            padding-left: 0px;
            position: absolute;
            z-index: 0;
            vertical-align: top;
        }


        .ms-console .ms-console-left .ms-panel .ms-panel-body .img-link .img-links-text {
            flex-direction: row;
            display: inline-block;
            word-wrap: break-word;
            margin-top: 0px;
            padding-top: 0px;
            padding-right: 0px;
            padding-bottom: 0px;
            padding-left: 0px;
            font-size: 32PX;
            color: #FFFFFF;
            margin-bottom: 0px;
            margin-left: 8px;
            z-index: 1;
            text-shadow: #666 3px 4px 5px;
        }


        .ms-console .ms-console-left .ms-panel .ms-panel-body .img-link .img-links-text-sm {
            flex-direction: row;
            display: inline-block;
            word-wrap: break-word;
            margin-top: 0px;
            padding-top: 0px;
            padding-right: 0px;
            padding-bottom: 0px;
            padding-left: 0px;
            font-size: 24PX;
            color: #FFFFFF;
            margin-bottom: 0px;
            margin-left: 8px;
            z-index: 1;
            text-shadow: #666 3px 4px 5px;
        }


        .ms-console .ms-console-right {
            width: unset;
            min-height: 50px;
            display: flex;
            flex-direction: column;
            flex: 1;
            flex-shrink: 0;
            flex-wrap: nowrap;
            margin-left: 10px;
            box-sizing: border-box;
            margin-bottom: 0px;
            padding-top: 0px;
            padding-right: 0px;
            padding-bottom: 0px;
            padding-left: 0px;
            margin-top: 0px;
            margin-right: 0px;
        }


        .ms-console .ms-console-right .news-list {
            width: 100%;
            height: unset;
            min-height: 50px;
            display: flex;
            flex-direction: column;
            flex-shrink: 0;
            flex-wrap: nowrap;
            margin-bottom: 8px;
            margin-left: 0px;
            background-color: rgba(253, 246, 236, 1);
            box-sizing: border-box;
            margin-right: 0px;
            padding-top: 0px;
            padding-right: 0px;
            padding-bottom: 8px;
            padding-left: 0px;
        }


        .ms-console .ms-console-right .news-list .news-list-title {
            width: 100%;
            height: unset;
            display: flex;
            flex-direction: row;
            flex-shrink: 0;
            flex-wrap: nowrap;
            justify-content: flex-start;
            align-items: center;
            margin-right: 0px;
            margin-bottom: 0px;
            padding-top: 10px;
            padding-right: 10px;
            padding-bottom: 10px;
            padding-left: 10px;
            box-sizing: border-box;
        }


        .ms-console .ms-console-right .news-list .news-list-title > img {
            width: 20px;
            height: 20px;
            margin-right: 10px;
            margin-bottom: 0px;
            padding-top: 0px;
            padding-right: 0px;
            padding-bottom: 0px;
            padding-left: 0px;
            vertical-align: top;
        }


        .ms-console .ms-console-right .news-list .news-list-title > span {
            flex-direction: row;
            display: inline-block;
            word-wrap: break-word;
            color: #E6A23C;
            margin-right: 0px;
            margin-bottom: 0px;
            padding-top: 0px;
            padding-right: 0px;
            padding-bottom: 0px;
            padding-left: 0px;
        }


        .ms-console .ms-console-right .news-list .news-list-body {
            width: 100%;
            min-height: 50px;
            line-height: 48px;
            display: flex;
            flex-direction: column;
            flex-shrink: 0;
            flex-wrap: nowrap;
            padding-top: 10px;
            padding-right: 10px;
            padding-bottom: 10px;
            padding-left: 40px;
            box-sizing: border-box;
        }


        .ms-console .ms-console-right .news-list .news-list-body .news-list-a {
            flex-direction: row;
            line-height: 24px;
            color: #409EFF;
            text-decoration: none;
            display: flex;
            cursor: pointer;
            padding-top: 0px;
            padding-left: 0px;
            padding-right: 0px;
            padding-bottom: 0px;
            margin-bottom: 0px;
            height: 28px;
            justify-content: flex-start;
            align-items: center;
        }

        .news-list-a:hover {
            text-decoration: underline !important;
        }


        .ms-console .ms-console-right .ms-service {
            width: 100%;
            height: unset;
            min-height: 50px;
            display: flex;
            flex-direction: column;
            flex-shrink: 0;
            flex-wrap: nowrap;
            align-items: center;
            margin-bottom: 8px;
            margin-left: 0px;
            padding-right: 10px;
            padding-bottom: 10px;
            padding-left: 10px;
            background-color: rgba(255, 255, 255, 1);
            box-sizing: border-box;
        }


        .ms-console .ms-console-right .ms-service .ms-panel {
            width: 100%;
            min-height: 50px;
            display: flex;
            flex-direction: column;
            flex-shrink: 0;
            flex-wrap: nowrap;
            margin-left: 0px;
            padding-top: 0px;
            padding-right: 0px;
            padding-bottom: 0px;
            padding-left: 0px;
            box-sizing: border-box;
            margin-bottom: 0px;
        }


        .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-title {
            width: 100%;
            height: unset;
            min-height: 50px;
            display: flex;
            flex-direction: row;
            flex-shrink: 0;
            flex-wrap: nowrap;
            align-items: center;
            margin-left: 0px;
            padding-top: 0px;
            padding-right: 0px;
            padding-bottom: 0px;
            padding-left: 0px;
            box-sizing: border-box;
            margin-bottom: 0px;
        }


        .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-title i {
            font-size: 20PX;
            margin-left: 0px;
            padding-top: 0px;
            padding-right: 0px;
            padding-bottom: 0px;
            padding-left: 0px;
        }


        .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-title .ms-panel-txt {
            flex-direction: row;
            display: inline-block;
            word-wrap: break-word;
            font-weight: 500;
            font-size: 18PX;
            margin-top: 0px;
            margin-bottom: 0px;
            margin-left: 8px;
            padding-top: 0px;
            padding-right: 0px;
            padding-bottom: 0px;
            padding-left: 0px;
        }


        .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-body {
            width: 100%;
            min-height: 50px;
            display: flex;
            flex-direction: row;
            flex-shrink: 1;
            flex-wrap: wrap;
            padding-top: 0px;
            padding-right: 0px;
            padding-bottom: 0px;
            padding-left: 0px;
            box-sizing: border-box;
            margin-top: 0px;
            margin-bottom: 0px;
            margin-left: 0px;
        }


        .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-body .server-item {
            width: 50%;
            height: 120px;
            min-height: 50px;
            display: flex;
            flex-direction: column;
            flex-shrink: 0;
            flex-wrap: nowrap;
            justify-content: center;
            align-items: center;
            margin-bottom: 0px;
            box-sizing: border-box;
            margin-left: 0px;
            padding-right: 0px;
            padding-left: 0px;
            margin-right: 0px;
            margin-top: 0px;
        }


        .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-body .server-item > i {
            font-size: 42PX;
            margin-right: 10px;
            margin-left: 0px;
        }


        .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-body .server-item .server-text {
            width: 100%;
            height: unset;
            min-height: 50px;
            display: flex;
            flex-direction: column;
            flex-shrink: 0;
            flex-wrap: nowrap;
            box-sizing: border-box;
            justify-content: center;
            align-items: center;
            margin-right: 0px;
            margin-left: 0px;
        }


        .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-body .server-item .server-text .server-desc {
            flex-direction: row;
            display: inline-block;
            word-wrap: break-word;
            font-size: 12PX;
            color: #999999;
            margin-left: 0px;
            margin-right: 0px;
        }


        .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-body .server-item .server-text .server-date {
            width: 100%;
            height: unset;
            display: flex;
            flex-direction: row;
            flex-shrink: 0;
            flex-wrap: nowrap;
            justify-content: center;
            align-items: center;
            box-sizing: border-box;
            margin-top: 10px;
        }


        .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-body .server-item .server-text .server-date .server-data {
            flex-direction: row;
            display: inline-block;
            word-wrap: break-word;
            line-height: 1.4;
            font-size: 20PX;
            font-weight: 600;
            color: #333333;
            margin-left: 0px;
            margin-top: 0px;
        }


        .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-body .server-item .server-text .server-date .s {
            flex-direction: row;
            display: inline-block;
            word-wrap: break-word;
            line-height: 1.4;
            font-size: 20PX;
            font-weight: 600;
            color: #333333;
            margin-left: 0px;
        }


        .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-body .server-item .server-text .server-date .date-green {
            flex-direction: row;
            display: inline-block;
            word-wrap: break-word;
            line-height: 1.4;
            font-size: 20PX;
            font-weight: 600;
            color: #67C23A;
            margin-left: 0px;
        }


        .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-body .qq-item {
            width: 50%;
            min-height: 50px;
            display: flex;
            flex-direction: row;
            flex-shrink: 0;
            flex-wrap: nowrap;
            justify-content: flex-start;
            align-items: center;
            margin-bottom: 10px;
            box-sizing: border-box;
            margin-left: 0px;
            padding-right: 0px;
            padding-left: 0px;
        }


        .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-body .qq-item .qq-icon {
            width: 35px;
            height: 39px;
            margin-left: 10px;
            vertical-align: top;
            margin-right: 10px;
            padding-right: 0px;
            padding-left: 0px;
        }


        .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-body .qq-item .qq-text {
            width: unset;
            height: unset;
            min-height: 50px;
            display: flex;
            flex-direction: column;
            flex-shrink: 0;
            flex-wrap: nowrap;
            box-sizing: border-box;
            justify-content: center;
            align-items: flex-start;
            margin-right: 0px;
            margin-left: 0px;
        }


        .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-body .qq-item .qq-text .qq-text-name {
            flex-direction: row;
            display: inline-block;
            word-wrap: break-word;
            font-size: 12PX;
            color: #999999;
            margin-left: 0px;
        }


        .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-body .qq-item .qq-text .qq-text-no {
            flex-direction: row;
            display: inline-block;
            word-wrap: break-word;
            line-height: 1.4;
            font-size: 16PX;
            font-weight: 600;
            color: #333333;
            margin-left: 0px;
        }


        .ms-console .ms-console-right .ms-other {
            width: 100%;
            height: unset;
            min-height: 50px;
            display: flex;
            flex-direction: column;
            flex: 1;
            flex-shrink: 0;
            flex-wrap: nowrap;
            align-items: center;
            margin-bottom: 0px;
            margin-left: 0px;
            padding-right: 10px;
            padding-bottom: 10px;
            padding-left: 10px;
            background-color: rgba(255, 255, 255, 1);
            box-sizing: border-box;
        }


        .ms-console .ms-console-right .ms-other .ms-panel {
            width: 100%;
            min-height: 50px;
            display: flex;
            flex-direction: column;
            flex-shrink: 0;
            flex-wrap: nowrap;
            margin-left: 0px;
            padding-top: 0px;
            padding-right: 0px;
            padding-bottom: 0px;
            padding-left: 0px;
            box-sizing: border-box;
        }


        .ms-console .ms-console-right .ms-other .ms-panel .ms-panel-title {
            width: 100%;
            height: unset;
            min-height: 50px;
            display: flex;
            flex-direction: row;
            flex-shrink: 0;
            flex-wrap: nowrap;
            align-items: center;
            margin-left: 0px;
            padding-top: 0px;
            padding-right: 0px;
            padding-bottom: 0px;
            padding-left: 0px;
            box-sizing: border-box;
            margin-bottom: 0px;
        }


        .ms-console .ms-console-right .ms-other .ms-panel .ms-panel-title i {
            font-size: 20PX;
            margin-left: 0px;
            padding-top: 0px;
            padding-right: 0px;
            padding-bottom: 0px;
            padding-left: 0px;
        }


        .ms-console .ms-console-right .ms-other .ms-panel .ms-panel-title .ms-panel-txt {
            flex-direction: row;
            display: inline-block;
            word-wrap: break-word;
            font-weight: 500;
            font-size: 18PX;
            margin-top: 0px;
            margin-bottom: 0px;
            margin-left: 8px;
            padding-top: 0px;
            padding-right: 0px;
            padding-bottom: 0px;
            padding-left: 0px;
        }


        .ms-console .ms-console-right .ms-other .ms-panel .ms-panel-body {
            width: 100%;
            min-height: 50px;
            display: flex;
            flex-direction: row;
            flex-shrink: 1;
            flex-wrap: wrap;
            padding-top: 0px;
            padding-right: 0px;
            padding-bottom: 0px;
            padding-left: 0px;
            box-sizing: border-box;
            margin-top: 0px;
            margin-bottom: 0px;
            margin-left: 0px;
        }


        .ms-console .ms-console-right .ms-other .ms-panel .ms-panel-body .qq-item {
            width: 50%;
            height: 100px;
            min-height: 50px;
            display: flex;
            flex-direction: row;
            flex-shrink: 0;
            flex-wrap: nowrap;
            justify-content: flex-start;
            align-items: center;
            margin-bottom: 0px;
            box-sizing: border-box;
            margin-left: 0px;
            padding-right: 0px;
            padding-left: 0px;
            margin-right: 0px;
        }


        .ms-console .ms-console-right .ms-other .ms-panel .ms-panel-body .qq-item .qq-icon {
            width: 35px;
            height: 39px;
            margin-left: 10px;
            vertical-align: top;
            margin-right: 10px;
            padding-right: 0px;
            padding-left: 0px;
        }


        .ms-console .ms-console-right .ms-other .ms-panel .ms-panel-body .qq-item .qq-text {
            width: unset;
            height: unset;
            min-height: 50px;
            display: flex;
            flex-direction: column;
            flex-shrink: 0;
            flex-wrap: nowrap;
            box-sizing: border-box;
            justify-content: center;
            align-items: flex-start;
            margin-right: 0px;
            margin-left: 0px;
        }


        .ms-console .ms-console-right .ms-other .ms-panel .ms-panel-body .qq-item .qq-text .qq-text-name {
            flex-direction: row;
            display: inline-block;
            word-wrap: break-word;
            font-size: 12PX;
            color: #999999;
            margin-left: 0px;
        }


        .ms-console .ms-console-right .ms-other .ms-panel .ms-panel-body .qq-item .qq-text .qq-text-no {
            flex-direction: row;
            display: inline-block;
            word-wrap: break-word;
            line-height: 1.4;
            font-size: 16PX;
            font-weight: 600;
            color: #333333;
            margin-left: 0px;
        }


        .ms-console .ms-console-right .ms-other .version-link {
            width: 98%;
            height: unset;
            margin-left: 0px;
            padding-right: 0px;
            padding-left: 0px;
            vertical-align: top;
            margin-top: 0px;
            margin-bottom: 8px;
        }


        .ms-console .ms-console-right .ms-other .version-link img {
            width: 100%;
            height: unset;
            margin-left: 0px;
            padding-right: 0px;
            padding-left: 0px;
            vertical-align: top;
            margin-top: 0px;
            margin-bottom: 0px;
        }


        @media (max-width: 768px) {
            .ms-console {
                width: 100%;
                height: unset;
                min-height: 50px;
                display: flex;
                flex-direction: column;
                flex: 1;
                flex-shrink: 0;
                flex-wrap: nowrap;
                background-color: rgba(238, 238, 238, 1);
                box-sizing: border-box;
                margin-left: 0px;
                padding-top: 10px;
                padding-right: 10px;
                padding-bottom: 10px;
                padding-left: 10px;
                margin-top: 0px;
                margin-bottom: 0px;
            }

            .ms-console .ms-console-left {
                width: 100%;
                min-height: 50px;
                display: flex;
                flex-direction: column;
                flex-shrink: 0;
                flex-wrap: nowrap;
                margin-left: 0px;
                padding-top: 20px;
                padding-right: 20px;
                padding-bottom: 20px;
                padding-left: 20px;
                background-color: rgba(255, 255, 255, 1);
                box-sizing: border-box;
                margin-top: 0px;
                margin-bottom: 0px;
            }

            .ms-console .ms-console-left .ms-panel {
                width: 100%;
                min-height: 50px;
                display: flex;
                flex-direction: column;
                flex-shrink: 0;
                flex-wrap: nowrap;
                margin-left: 0px;
                padding-top: 0px;
                padding-right: 0px;
                padding-bottom: 0px;
                padding-left: 0px;
                box-sizing: border-box;
            }

            .ms-console .ms-console-left .ms-panel .ms-panel-title {
                width: 100%;
                height: unset;
                min-height: 50px;
                display: flex;
                flex-direction: row;
                flex-shrink: 0;
                flex-wrap: nowrap;
                align-items: center;
                margin-left: 0px;
                padding-top: 0px;
                padding-right: 0px;
                padding-bottom: 0px;
                padding-left: 0px;
                box-sizing: border-box;
            }

            .ms-console .ms-console-left .ms-panel .ms-panel-title i {
                font-size: 20PX;
                margin-left: 0px;
                padding-top: 0px;
                padding-right: 0px;
                padding-bottom: 0px;
                padding-left: 0px;
            }

            .ms-console .ms-console-left .ms-panel .ms-panel-title .ms-panel-txt {
                flex-direction: row;
                display: inline-block;
                word-wrap: break-word;
                font-weight: 500;
                font-size: 18PX;
                margin-top: 0px;
                margin-bottom: 0px;
                margin-left: 8px;
                padding-top: 0px;
                padding-right: 0px;
                padding-bottom: 0px;
                padding-left: 0px;
            }

            .ms-console .ms-console-left .ms-panel .ms-panel-body {
                width: 100%;
                min-height: 50px;
                display: flex;
                flex-direction: row;
                flex-shrink: 1;
                flex-wrap: wrap;
                padding-top: 0px;
                padding-right: 0px;
                padding-bottom: 0px;
                padding-left: 0px;
                box-sizing: border-box;
                margin-top: 0px;
                margin-bottom: 0px;
            }

            .ms-console .ms-console-left .ms-panel .ms-panel-body .menu-item {
                width: 50%;
                height: unset;
                min-height: 50px;
                display: flex;
                flex-direction: column;
                flex-shrink: 0;
                flex-wrap: nowrap;
                justify-content: center;
                align-items: center;
                padding-top: 0px;
                padding-right: 0px;
                padding-bottom: 0px;
                padding-left: 0px;
                box-sizing: border-box;
                margin-top: 16px;
                margin-bottom: 16px;
                margin-left: 0px;
            }

            .ms-console .ms-console-left .ms-panel .ms-panel-body .menu-item .menu-link {
                width: unset;
                height: 100%;
                min-height: 50px;
                display: flex;
                flex-direction: column;
                flex-shrink: 0;
                flex-wrap: nowrap;
                align-items: center;
                margin-top: 0px;
                margin-bottom: 0px;
                box-sizing: border-box;
            }

            .ms-console .ms-console-left .ms-panel .ms-panel-body .menu-item .menu-link .menu-icon {
                font-size: 42PX;
                margin-top: 0px;
            }

            .ms-console .ms-console-left .ms-panel .ms-panel-body .menu-item .menu-link .menu-text {
                flex-direction: row;
                display: inline-block;
                word-wrap: break-word;
                margin-top: 5px;
                padding-top: 0px;
                padding-right: 0px;
                padding-bottom: 0px;
                padding-left: 0px;
                margin-left: 0px;
            }

            .ms-console .ms-console-left .ms-panel .ms-panel-body .text-link {
                width: 50%;
                height: unset;
                min-height: 50px;
                display: flex;
                flex-direction: row;
                flex-shrink: 0;
                flex-wrap: nowrap;
                justify-content: flex-start;
                align-items: center;
                padding-top: 0px;
                padding-right: 0px;
                padding-bottom: 0px;
                padding-left: 0px;
                box-sizing: border-box;
                margin-top: 0px;
                margin-bottom: 0px;
                margin-left: 0px;
            }

            .ms-console .ms-console-left .ms-panel .ms-panel-body .text-link .text-links-icon {
                margin-top: 0px;
                margin-bottom: 0px;
                margin-left: 0px;
                padding-top: 0px;
                padding-right: 0px;
                padding-bottom: 0px;
                padding-left: 0px;
            }

            .ms-console .ms-console-left .ms-panel .ms-panel-body .text-link .text-links-text {
                flex-direction: row;
                display: inline-block;
                word-wrap: break-word;
                margin-top: 0px;
                padding-top: 0px;
                padding-right: 0px;
                padding-bottom: 0px;
                padding-left: 0px;
                margin-left: 8px;
                margin-bottom: 0px;
            }

            .ms-console .ms-console-left .ms-panel .ms-panel-body .img-link {
                width: 100%;
                height: 200px;
                min-height: 50px;
                display: flex;
                flex-direction: column;
                flex-shrink: 0;
                flex-wrap: nowrap;
                justify-content: center;
                align-items: center;
                padding-top: 0px;
                padding-right: 0px;
                padding-bottom: 0px;
                padding-left: 0px;
                box-sizing: border-box;
                margin-top: 16px;
                margin-bottom: 16px;
                margin-left: 0px;
                position: relative;
                border-radius: 20px;
                overflow: hidden;
            }

            .ms-console .ms-console-left .ms-panel .ms-panel-body .img-link img {
                width: 100%;
                height: 100%;
                margin-top: 0px;
                margin-bottom: 0px;
                margin-left: 0px;
                padding-top: 0px;
                padding-right: 0px;
                padding-bottom: 0px;
                padding-left: 0px;
                position: absolute;
                z-index: 0;
                vertical-align: top;
            }

            .ms-console .ms-console-left .ms-panel .ms-panel-body .img-link .img-links-text {
                flex-direction: row;
                display: inline-block;
                word-wrap: break-word;
                margin-top: 0px;
                padding-top: 0px;
                padding-right: 0px;
                padding-bottom: 0px;
                padding-left: 0px;
                font-size: 32PX;
                color: #FFFFFF;
                margin-bottom: 0px;
                margin-left: 8px;
                z-index: 1;
                text-shadow: #666 3px 4px 5px;
            }

            .ms-console .ms-console-left .ms-panel .ms-panel-body .img-link .img-links-text-sm {
                flex-direction: row;
                display: inline-block;
                word-wrap: break-word;
                margin-top: 0px;
                padding-top: 0px;
                padding-right: 0px;
                padding-bottom: 0px;
                padding-left: 0px;
                font-size: 24PX;
                color: #FFFFFF;
                margin-bottom: 0px;
                margin-left: 8px;
                z-index: 1;
                text-shadow: #666 3px 4px 5px;
            }

            .ms-console .ms-console-right {
                width: unset;
                height: 100%;
                min-height: 50px;
                display: flex;
                flex-direction: column;
                flex: 1;
                flex-shrink: 0;
                flex-wrap: nowrap;
                margin-left: 0px;
                box-sizing: border-box;
                margin-bottom: 0px;
                padding-top: 0px;
                padding-right: 0px;
                padding-bottom: 0px;
                padding-left: 0px;
                margin-top: 10px;
                margin-right: 0px;
            }

            .ms-console .ms-console-right .news-list {
                width: 100%;
                height: unset;
                min-height: 50px;
                display: flex;
                flex-direction: column;
                flex-shrink: 0;
                flex-wrap: nowrap;
                margin-bottom: 8px;
                margin-left: 0px;
                background-color: rgba(253, 246, 236, 1);
                box-sizing: border-box;
                margin-right: 0px;
                padding-top: 0px;
                padding-right: 0px;
                padding-bottom: 8px;
                padding-left: 0px;
            }

            .ms-console .ms-console-right .news-list .news-list-title {
                width: 100%;
                height: unset;
                display: flex;
                flex-direction: row;
                flex-shrink: 0;
                flex-wrap: nowrap;
                justify-content: flex-start;
                align-items: center;
                margin-right: 0px;
                margin-bottom: 0px;
                padding-top: 10px;
                padding-right: 10px;
                padding-bottom: 10px;
                padding-left: 10px;
                box-sizing: border-box;
            }

            .ms-console .ms-console-right .news-list .news-list-title > img {
                width: 20px;
                height: 20px;
                margin-right: 10px;
                margin-bottom: 0px;
                padding-top: 0px;
                padding-right: 0px;
                padding-bottom: 0px;
                padding-left: 0px;
                vertical-align: top;
            }

            .ms-console .ms-console-right .news-list .news-list-title > span {
                flex-direction: row;
                display: inline-block;
                word-wrap: break-word;
                color: #E6A23C;
                margin-right: 0px;
                margin-bottom: 0px;
                padding-top: 0px;
                padding-right: 0px;
                padding-bottom: 0px;
                padding-left: 0px;
            }

            .ms-console .ms-console-right .news-list .news-list-body {
                width: 100%;
                height: unset;
                min-height: 50px;
                line-height: 48px;
                display: flex;
                flex-direction: column;
                flex-shrink: 0;
                flex-wrap: nowrap;
                padding-top: 10px;
                padding-right: 10px;
                padding-bottom: 10px;
                padding-left: 40px;
                box-sizing: border-box;
            }

            .ms-console .ms-console-right .news-list .news-list-body .news-list-a {
                flex-direction: row;
                line-height: 24px;
                color: #409EFF;
                text-decoration: none;
                display: flex;
                cursor: pointer;
                padding-top: 0px;
                padding-left: 0px;
                padding-right: 0px;
                padding-bottom: 0px;
                margin-bottom: 0px;
                height: 28px;
                justify-content: flex-start;
                align-items: center;
            }

            .ms-console .ms-console-right .ms-service {
                width: 100%;
                height: unset;
                min-height: 50px;
                display: flex;
                flex-direction: column;
                flex-shrink: 0;
                flex-wrap: nowrap;
                align-items: center;
                margin-bottom: 8px;
                margin-left: 0px;
                padding-right: 10px;
                padding-bottom: 10px;
                padding-left: 10px;
                background-color: rgba(255, 255, 255, 1);
                box-sizing: border-box;
            }

            .ms-console .ms-console-right .ms-service .ms-panel {
                width: 100%;
                min-height: 50px;
                display: flex;
                flex-direction: column;
                flex-shrink: 0;
                flex-wrap: nowrap;
                margin-left: 0px;
                padding-top: 0px;
                padding-right: 0px;
                padding-bottom: 0px;
                padding-left: 0px;
                box-sizing: border-box;
                margin-bottom: 0px;
            }

            .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-title {
                width: 100%;
                height: unset;
                min-height: 50px;
                display: flex;
                flex-direction: row;
                flex-shrink: 0;
                flex-wrap: nowrap;
                align-items: center;
                margin-left: 0px;
                padding-top: 0px;
                padding-right: 0px;
                padding-bottom: 0px;
                padding-left: 0px;
                box-sizing: border-box;
                margin-bottom: 0px;
            }

            .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-title i {
                font-size: 20PX;
                margin-left: 0px;
                padding-top: 0px;
                padding-right: 0px;
                padding-bottom: 0px;
                padding-left: 0px;
            }

            .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-title .ms-panel-txt {
                flex-direction: row;
                display: inline-block;
                word-wrap: break-word;
                font-weight: 500;
                font-size: 18PX;
                margin-top: 0px;
                margin-bottom: 0px;
                margin-left: 8px;
                padding-top: 0px;
                padding-right: 0px;
                padding-bottom: 0px;
                padding-left: 0px;
            }

            .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-body {
                width: 100%;
                min-height: 50px;
                display: flex;
                flex-direction: row;
                flex-shrink: 1;
                flex-wrap: wrap;
                padding-top: 0px;
                padding-right: 0px;
                padding-bottom: 0px;
                padding-left: 0px;
                box-sizing: border-box;
                margin-top: 0px;
                margin-bottom: 0px;
                margin-left: 0px;
            }

            .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-body .server-item {
                width: 50%;
                height: 120px;
                min-height: 50px;
                display: flex;
                flex-direction: column;
                flex-shrink: 0;
                flex-wrap: nowrap;
                justify-content: center;
                align-items: center;
                margin-bottom: 0px;
                box-sizing: border-box;
                margin-left: 0px;
                padding-right: 0px;
                padding-left: 0px;
                margin-right: 0px;
                margin-top: 0px;
            }

            .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-body .server-item > i {
                font-size: 42PX;
                margin-right: 10px;
                margin-left: 0px;
            }

            .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-body .server-item .server-text {
                width: 100%;
                height: unset;
                min-height: 50px;
                display: flex;
                flex-direction: column;
                flex-shrink: 0;
                flex-wrap: nowrap;
                box-sizing: border-box;
                justify-content: center;
                align-items: center;
                margin-right: 0px;
                margin-left: 0px;
            }

            .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-body .server-item .server-text .server-desc {
                flex-direction: row;
                display: inline-block;
                word-wrap: break-word;
                font-size: 12PX;
                color: #999999;
                margin-left: 0px;
                margin-right: 0px;
            }

            .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-body .server-item .server-text .server-date {
                width: 100%;
                height: unset;
                display: flex;
                flex-direction: row;
                flex-shrink: 0;
                flex-wrap: nowrap;
                justify-content: center;
                align-items: center;
                box-sizing: border-box;
                margin-top: 10px;
            }

            .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-body .server-item .server-text .server-date .server-data {
                flex-direction: row;
                display: inline-block;
                word-wrap: break-word;
                line-height: 1.4;
                font-size: 20PX;
                font-weight: 600;
                color: #333333;
                margin-left: 0px;
                margin-top: 0px;
            }

            .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-body .server-item .server-text .server-date .s {
                flex-direction: row;
                display: inline-block;
                word-wrap: break-word;
                line-height: 1.4;
                font-size: 20PX;
                font-weight: 600;
                color: #333333;
                margin-left: 0px;
            }

            .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-body .server-item .server-text .server-date .date-green {
                flex-direction: row;
                display: inline-block;
                word-wrap: break-word;
                line-height: 1.4;
                font-size: 20PX;
                font-weight: 600;
                color: #67C23A;
                margin-left: 0px;
            }

            .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-body .qq-item {
                width: 50%;
                height: 100px;
                min-height: 50px;
                display: flex;
                flex-direction: row;
                flex-shrink: 0;
                flex-wrap: nowrap;
                justify-content: flex-start;
                align-items: center;
                margin-bottom: 0px;
                box-sizing: border-box;
                margin-left: 0px;
                padding-right: 0px;
                padding-left: 0px;
                margin-top: 0px;
            }

            .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-body .qq-item .qq-icon {
                width: 35px;
                height: 39px;
                margin-left: 4px;
                vertical-align: top;
                margin-right: 4px;
                padding-right: 0px;
                padding-left: 0px;
            }

            .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-body .qq-item .qq-text {
                width: unset;
                height: unset;
                min-height: 50px;
                display: flex;
                flex-direction: column;
                flex-shrink: 0;
                flex-wrap: nowrap;
                box-sizing: border-box;
                justify-content: center;
                align-items: flex-start;
                margin-right: 0px;
                margin-left: 0px;
            }

            .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-body .qq-item .qq-text .qq-text-name {
                flex-direction: row;
                display: inline-block;
                word-wrap: break-word;
                font-size: 12PX;
                color: #999999;
                margin-left: 0px;
            }

            .ms-console .ms-console-right .ms-service .ms-panel .ms-panel-body .qq-item .qq-text .qq-text-no {
                flex-direction: row;
                display: inline-block;
                word-wrap: break-word;
                line-height: 1.4;
                font-size: 16PX;
                font-weight: 600;
                color: #333333;
                margin-left: 0px;
            }

            .ms-console .ms-console-right .ms-other {
                width: 100%;
                height: unset;
                min-height: 50px;
                display: flex;
                flex-direction: column;
                flex: 1;
                flex-shrink: 0;
                flex-wrap: nowrap;
                align-items: center;
                margin-bottom: 0px;
                margin-left: 0px;
                padding-right: 10px;
                padding-bottom: 10px;
                padding-left: 10px;
                background-color: rgba(255, 255, 255, 1);
                box-sizing: border-box;
            }

            .ms-console .ms-console-right .ms-other .ms-panel {
                width: 100%;
                min-height: 50px;
                display: flex;
                flex-direction: column;
                flex-shrink: 0;
                flex-wrap: nowrap;
                margin-left: 0px;
                padding-top: 0px;
                padding-right: 0px;
                padding-bottom: 0px;
                padding-left: 0px;
                box-sizing: border-box;
            }

            .ms-console .ms-console-right .ms-other .ms-panel .ms-panel-title {
                width: 100%;
                height: unset;
                min-height: 50px;
                display: flex;
                flex-direction: row;
                flex-shrink: 0;
                flex-wrap: nowrap;
                align-items: center;
                margin-left: 0px;
                padding-top: 0px;
                padding-right: 0px;
                padding-bottom: 0px;
                padding-left: 0px;
                box-sizing: border-box;
                margin-bottom: 0px;
            }

            .ms-console .ms-console-right .ms-other .ms-panel .ms-panel-title i {
                font-size: 20PX;
                margin-left: 0px;
                padding-top: 0px;
                padding-right: 0px;
                padding-bottom: 0px;
                padding-left: 0px;
            }

            .ms-console .ms-console-right .ms-other .ms-panel .ms-panel-title .ms-panel-txt {
                flex-direction: row;
                display: inline-block;
                word-wrap: break-word;
                font-weight: 500;
                font-size: 18PX;
                margin-top: 0px;
                margin-bottom: 0px;
                margin-left: 8px;
                padding-top: 0px;
                padding-right: 0px;
                padding-bottom: 0px;
                padding-left: 0px;
            }

            .ms-console .ms-console-right .ms-other .ms-panel .ms-panel-body {
                width: 100%;
                min-height: 50px;
                display: flex;
                flex-direction: row;
                flex-shrink: 1;
                flex-wrap: wrap;
                padding-top: 0px;
                padding-right: 0px;
                padding-bottom: 0px;
                padding-left: 0px;
                box-sizing: border-box;
                margin-top: 0px;
                margin-bottom: 0px;
                margin-left: 0px;
            }

            .ms-console .ms-console-right .ms-other .ms-panel .ms-panel-body .qq-item {
                width: 50%;
                height: 100px;
                min-height: 50px;
                display: flex;
                flex-direction: row;
                flex-shrink: 0;
                flex-wrap: nowrap;
                justify-content: flex-start;
                align-items: center;
                margin-bottom: 0px;
                box-sizing: border-box;
                margin-left: 0px;
                padding-right: 0px;
                padding-left: 0px;
                margin-right: 0px;
            }

            .ms-console .ms-console-right .ms-other .ms-panel .ms-panel-body .qq-item .qq-icon {
                width: 35px;
                height: 39px;
                margin-left: 4px;
                vertical-align: top;
                margin-right: 4px;
                padding-right: 0px;
                padding-left: 0px;
            }

            .ms-console .ms-console-right .ms-other .ms-panel .ms-panel-body .qq-item .qq-text {
                width: unset;
                height: unset;
                min-height: 50px;
                display: flex;
                flex-direction: column;
                flex-shrink: 0;
                flex-wrap: nowrap;
                box-sizing: border-box;
                justify-content: center;
                align-items: flex-start;
                margin-right: 0px;
                margin-left: 0px;
            }

            .ms-console .ms-console-right .ms-other .ms-panel .ms-panel-body .qq-item .qq-text .qq-text-name {
                flex-direction: row;
                display: inline-block;
                word-wrap: break-word;
                font-size: 12PX;
                color: #999999;
                margin-left: 0px;
            }

            .ms-console .ms-console-right .ms-other .ms-panel .ms-panel-body .qq-item .qq-text .qq-text-no {
                flex-direction: row;
                display: inline-block;
                word-wrap: break-word;
                line-height: 1.4;
                font-size: 20PX;
                font-weight: 600;
                color: #333333;
                margin-left: 0px;
            }

            .ms-console .ms-console-right .ms-other .version-link {
                width: 98%;
                height: unset;
                margin-left: 0px;
                padding-right: 0px;
                padding-left: 0px;
                vertical-align: top;
                margin-top: 0px;
                margin-bottom: 0px;
            }

            .ms-console .ms-console-right .ms-other .version-link img {
                width: 100%;
                height: unset;
                margin-left: 0px;
                padding-right: 0px;
                padding-left: 0px;
                vertical-align: top;
                margin-top: 0px;
                margin-bottom: 0px;
                padding-bottom: 0px;
            }


        }
    </style>
</head>
<body>
<div id="app" v-cloak>
    <div class="ms-console">
        <div class="ms-console-left">
            <div class="ms-panel">
                <div class="ms-panel-title">
                    <i class="iconfont icon-zidingyimoxing"></i>
                    <span class="ms-panel-txt">
                            常用功能
                    </span>
                </div>
                <div class="ms-panel-body">
                    <div v-if="markList.length==0" class="menu-item-empty">
                        <el-empty>
                            <div slot="description">
                                展开顶部<b style="color: #E6A23C">功能大全</b>，点击菜单右侧<b style="color: #E6A23C">五角星</b>设置为常用功能
                            </div>
                        </el-empty>
                    </div>
                    <div v-else class="menu-item"
                         v-for="item in markList" >
                        <div class="menu-link" @click="window.parent.indexVue.openParentMenuInId(item.id)">
                            <i :class="['menu-icon','iconfont',item.icon]"></i>
                            <span class="menu-text">
                                    {{item.title}}
                            </span>
                        </div>
                    </div>

                </div>
            </div>
            <div class="ms-panel">
                <div class="ms-panel-title">
                    <i class="iconfont icon-logo"></i>
                    <span class="ms-panel-txt">
                            帮助手册
                        </span>
                </div>
                <div class="ms-panel-body">
                    <div class="text-link" v-for="item in mainJson.helps">

                        <a :href="item.link" target="_blank" class="text-links-text">
                            <i class="iconfont icon-wenjian text-links-icon"></i> {{item.text}}
                        </a>
                    </div>
                </div>


            </div>
            <div class="ms-panel">
                <div class="ms-panel-title">
                    <i class="iconfont icon-daima"></i>
                    <span class="ms-panel-txt">
                            MS平台
                        </span>
                </div>
                <div class="ms-panel-body space-around">
                    <a :href="item.link" target="_blank" class="img-link" v-for="item in mainJson.ms">
                        <img :title="item.text" :alt="item.text" :src="item.pic"/>
                        <span class="img-links-text">
                                {{item.text}}
                            </span>
                        <span class="img-links-text-sm">
                                {{item.desc}}
                            </span>
                    </a>
                </div>
            </div>
            <div class="ms-panel">
                <div class="ms-panel-title">
                    <i class="iconfont icon-chajian"></i>
                    <span class="ms-panel-txt">
                            热门插件
                        </span>
                </div>
                <div class="ms-panel-body space-around">
                    <a :href="item.link" target="_blank" class="img-link" v-for="item in mainJson.plugs">
                        <img :title="item.text" :alt="item.text" :src="item.pic"/>
                    </a>
                </div>
            </div>
        </div>
        <div class="ms-console-right">
            <div class="news-list">
                <div class="news-list-title">
                    <img title="" alt="" src="/static/images/icon-voice.png"/>
                    <span>
                            公告
                        </span>
                </div>
                <div class="news-list-body">
                    <ul style="margin: 0">
                       <li  v-for="(item,index) in contentList" :key="index">
                           <a v-if="item.contentOutLink==''" target="_blank" :href="'https://www.mingsoft.net/'+item.id+'.html'" class="news-list-a" >{{item.contentTitle}}</a>
                           <a v-else target="_blank" :href="item.contentOutLink" class="news-list-a" >{{item.contentTitle}}</a>
                       </li>
                    </ul>
                </div>
            </div>
            <div class="ms-service">
                <div class="ms-panel">
                    <div class="ms-panel-title">
                        <i class="iconfont icon-QQ4"></i>
                        <span class="ms-panel-txt">
                                QQ群
                            </span>
                    </div>
                    <div class="ms-panel-body">
                        <div class="qq-item" v-for="item in mainJson.QQGroups">
                            <img :title="item.text" :alt="item.text" src="/static/images/icon-qq.png"
                                 class="qq-icon"/>
                            <div class="qq-text">
                                <span class="qq-text-name">
                                    {{item.text}}
                                </span>
                                <a :href="item.link" target="_blank" class="qq-text-no">
                                    {{item.number}}
                                </a>
                            </div>
                        </div>

                    </div>
                </div>
            </div>
            <div class="ms-other">
                <div class="ms-panel">
                    <div class="ms-panel-title">
                        <i class="iconfont icon-beizi"></i>
                        <span class="ms-panel-txt">
                                商务技术支持
                            </span>
                    </div>
                    <div class="ms-panel-body">
                        <div class="qq-item">
                            <img title="" alt="" src="/static/images/icon-telephone.png" class="qq-icon"/>
                            <div class="qq-text">
                                    <span class="qq-text-name">
                                        商务电话
                                    </span>
                                <span class="qq-text-no">
                                        13397980163
                                    </span>
                            </div>
                        </div>
                        <div class="qq-item">
                            <img title="" alt="" src="/static/images/icon-qq.png" class="qq-icon"/>
                            <div class="qq-text">
                                    <span class="qq-text-name">
                                        商务QQ
                                    </span>
                                <span class="qq-text-no">
                                        3336073455
                                    </span>
                            </div>
                        </div>
                    </div>
                </div>
                <a :href="item.link" class="version-link" target="_blank" v-for="item in mainJson.versions">
                    <img :title="item.text" :alt="item.text" :src="item.pic"/>
                </a>
            </div>
        </div>
    </div>
</div>
</body>

</html>
<script src="${base}/static/plugins/jquery/3.6.3/jquery-3.6.3.min.js"></script>
<script src="/${base}static/plugins/jquery.liMarquee/jquery.liMarquee.js"></script>

<script>
    var app = new Vue({
        el: '#app',
        component() {
        },
        data: function () {
            return {
                markList: [], //常用功能列表
                contentList: [],
                msNewsPath: 'https://www.mingsoft.net/',
                liMarquee: false,
                base: ms.base,
                mainJson:[],
            }

        },
        watch: {},
        methods: {
            queryMarkList: function () {
                var _markList = window.parent.indexVue.markList;
                if (_markList) {
                    this.markList = _markList
                }
            },
            setCallBackFun: function () {
                window.parent.indexVue.addCallBackFun(this.queryMarkList);
            },
            getMainJson: function () {
                var that = this;
                axios.create({
                    withCredentials: true
                }).get("//cdn.mingsoft.net/open/main.json").then(function (res) {
                    that.mainJson = JSON.parse(JSON.stringify(res.data));
                });
            },
            queryContentList: function () {
                var that = this;
                axios.create({
                    withCredentials: true
                }).post("https://www.mingsoft.net/cms/content/list.do?categoryId=160&pageSize=50&flag=open").then(function (res) {
                    if (res.data.result && res.data.data.total > 0) {
                        that.contentList = res.data.data.rows;
                        that.$nextTick(() =>{
                            $('.news-list-body').liMarquee({
                                direction: 'up',
                                drag:false,
                                scrollamount:20

                            })
                        })
                    }
                });
            }
        },
        computed() {
        },
        created() {
            this.queryContentList();
            this.queryMarkList();
            this.getMainJson();
        },
        mounted:function () {
            this.liMarquee = true;


        }
    });


</script>
