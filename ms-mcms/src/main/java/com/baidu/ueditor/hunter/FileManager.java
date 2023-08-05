/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package com.baidu.ueditor.hunter;

import com.baidu.ueditor.PathFormat;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.MultiState;
import com.baidu.ueditor.define.State;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.co.biz.IFileBiz;
import net.mingsoft.co.entity.FileEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class FileManager {

    private String dir = null;
    private String rootPath = null;
    private String[] allowFiles = null;
    private int count = 0;

    public FileManager(Map<String, Object> conf ) {

        this.rootPath = (String)conf.get( "rootPath" );
        this.dir = this.rootPath + (String)conf.get( "dir" );
        this.allowFiles = this.getAllowFiles( conf.get("allowFiles") );
        this.count = (Integer)conf.get( "count" );

    }

    public State listFile ( int index ) {

//        File dir = new File( this.dir );
        State state = null;

//        if ( !dir.exists() ) {
//            return new BaseState( false, AppInfo.NOT_EXIST );
//        }
//
//        if ( !dir.isDirectory() ) {
//            return new BaseState( false, AppInfo.NOT_DIRECTORY );
//        }

//        Collection<File> list = FileUtils.listFiles( dir, this.allowFiles, true );

        Collection<File> list =  new ArrayList<>();
        //需要获取File表里面当前用户上传的数据
        IFileBiz fileBiz = SpringUtil.getBean(IFileBiz.class);
        QueryWrapper<FileEntity> queryWrapper = new QueryWrapper<>();

        ManagerEntity session =  BasicUtil.getManager();
        queryWrapper.eq("create_by",session.getId());
        // 根据文件的路径 创建文件实体
        List<FileEntity> result = fileBiz.list(queryWrapper);
        for (FileEntity fileEntity : result) {
            list.add(new File(BasicUtil.getRealPath(fileEntity.getFilePath())));
        }

        if ( index < 0 || index > list.size() ) {
            state = new MultiState( true );
        } else {
            Object[] fileList = Arrays.copyOfRange( list.toArray(), index, index + this.count );
            state = this.getState( fileList );
        }

        state.putInfo( "start", index );
        state.putInfo( "total", list.size() );

        return state;

    }

    private State getState ( Object[] files ) {

        MultiState state = new MultiState( true );
        BaseState fileState = null;

        File file = null;

        for ( Object obj : files ) {
            if ( obj == null ) {
                break;
            }
            file = (File)obj;
            fileState = new BaseState( true );
            fileState.putInfo( "url", PathFormat.format( this.getPath( file ) ) );
            state.addState( fileState );
        }

        return state;

    }

    private String getPath ( File file ) {

        String path = file.getAbsolutePath();

        return path.replace( this.rootPath, "/" );

    }

    private String[] getAllowFiles ( Object fileExt ) {

        String[] exts = null;
        String ext = null;

        if ( fileExt == null ) {
            return new String[ 0 ];
        }

        exts = (String[])fileExt;

        for ( int i = 0, len = exts.length; i < len; i++ ) {

            ext = exts[ i ];
            exts[ i ] = ext.replace( ".", "" );

        }

        return exts;

    }

}
