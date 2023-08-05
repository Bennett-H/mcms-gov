/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */










package net.mingsoft.basic.exception;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.base.util.BundleUtil;
import net.mingsoft.basic.biz.ILogBiz;
import net.mingsoft.basic.constant.Const;
import net.mingsoft.basic.entity.LogEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.CalculationUtil;
import net.mingsoft.basic.util.IpUtils;
import net.mingsoft.basic.util.SpringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.session.ExpiredSessionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 全局异常处理类
 *
 * @author 铭软开发团队-Administrator
 * @date 2018年4月6日
 * 历史修订:basic重写-增加捕获异常时将异常信息记录到日志表中-2021-12-17
 * 历史修订:添加 BadSqlGrammarException处理 不返回具体sql,防止暴露表结构-2022-05-05
 */
@ControllerAdvice
public class GlobalExceptionResolver extends DefaultHandlerExceptionResolver {

    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ILogBiz logBiz;



    /**
     * 全局异常
     *
     * @param request
     * @param response
     * @param e
     * @return
     */
    @ExceptionHandler({BusinessException.class})
    @Deprecated
    public ModelAndView handleBusinessException(HttpServletRequest request, HttpServletResponse response, BusinessException e) {
        LOG.debug("handleBusinessException");
        response.setStatus(e.getCode().value());
        return render(request, response, ResultData.build().code(e.getCode()).data(e.getData()).msg(e.getMsg()), e);

    }

    /**
     * 全局异常
     *
     * @param request
     * @param response
     * @param e
     * @return
     */
    @ExceptionHandler({net.mingsoft.base.exception.BusinessException.class})
    public ModelAndView handleBusinessException(HttpServletRequest request, HttpServletResponse response, net.mingsoft.base.exception.BusinessException e) {
        LOG.debug("handleBusinessException");
        response.setStatus(e.getCode().value());
        return render(request, response, ResultData.build().code(e.getCode()).data(e.getData()).msg(e.getMsg()), e);

    }

    /**
     * 全局异常
     *
     * @param request
     * @param response
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public ModelAndView handleException(HttpServletRequest request, HttpServletResponse response, Exception e) {
        LOG.debug("handleException");
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return render(request, response, ResultData.build().code(HttpStatus.INTERNAL_SERVER_ERROR)
                        .msg(e.getStackTrace()[0].toString().concat(":").concat(e.toString()))
                , e);
    }

    /**
     * sql异常,不返回具体sql,防止暴露表结构
     *
     * @param request
     * @param response
     * @param e
     * @return
     */
    @ExceptionHandler(value = org.springframework.jdbc.BadSqlGrammarException.class)
    public ModelAndView handleSqlException(HttpServletRequest request, HttpServletResponse response, Exception e) {
        LOG.debug("handleSqlException");
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return render(request, response, ResultData.build().code(HttpStatus.INTERNAL_SERVER_ERROR).msg("当前操作存在SQL异常，请查看日志管理中的异常日志！"), e);
    }

    /**
     * 全局异常 未找到类404
     *
     * @param request
     * @param response
     * @param e
     * @return
     */
    @ExceptionHandler(value = NoHandlerFoundException.class)
    public ModelAndView handleNoHandlerFoundException(HttpServletRequest request, HttpServletResponse response, NoHandlerFoundException e) {
        LOG.debug("handleNoHandlerFoundException");
        return render(request, response, ResultData.build().code(HttpStatus.NOT_FOUND).msg("page 404"), e);
    }

    /**
     * 请求参数异常
     *
     * @param request
     * @param response
     * @param e
     * @return
     */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public ModelAndView handleMissingServletRequestParameterException(HttpServletRequest request, HttpServletResponse response, MissingServletRequestParameterException e) {
        LOG.debug("handleMissingServletRequestParameterException");
        return render(request, response, ResultData.build().code(HttpStatus.BAD_REQUEST).msg("request parameter err"), e);

    }


    /**
     * 上传文件异常捕获
     *
     * @param request
     * @param response
     * @param e
     * @return
     * @throws IOException
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ModelAndView uploadException(HttpServletRequest request, HttpServletResponse response, MaxUploadSizeExceededException e) throws IOException {
        LOG.debug("MaxUploadSizeExceededException");
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return render(request, response, ResultData.build().code(HttpStatus.INTERNAL_SERVER_ERROR)
                .msg(BundleUtil.getString("net.mingsoft.basic.resources.resources","upload.max.size", CalculationUtil.convertSpaceUnit(e.getMaxUploadSize() / 1024))), e);
    }

    /**
     * 请求方法类型错误
     *
     * @param request
     * @param response
     * @param e
     * @return
     */
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public ModelAndView handleHttpRequestMethodNotSupportedException(HttpServletRequest request, HttpServletResponse response, HttpRequestMethodNotSupportedException e) {
        LOG.debug("handleHttpRequestMethodNotSupportedException");

        return render(request, response, ResultData.build().code(HttpStatus.METHOD_NOT_ALLOWED).msg("request method not support"), e);
    }

    /**
     * 统一处理请求参数校验(实体对象传参)
     *
     * @param e BindException
     * @return ResultResponse
     */
    @ExceptionHandler(BindException.class)
    public ModelAndView handleValidExceptionHandler(HttpServletRequest request, HttpServletResponse response, BindException e) {
        LOG.debug("handleValidExceptionHandler");
        StringBuilder message = new StringBuilder();
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        for (FieldError error : fieldErrors) {
            message.append(error.getField()).append(error.getDefaultMessage()).append(",");
        }
        message = new StringBuilder(message.substring(0, message.length() - 1));

        return render(request, response, ResultData.build().code(HttpStatus.NOT_ACCEPTABLE).msg(message.toString()), e);
    }

    /**
     * 统一处理请求参数校验(普通传参)
     *
     * @param e ConstraintViolationException
     * @return ResultResponse
     */
//    @ExceptionHandler(value = ConstraintViolationException.class)
//    public ModelAndView handleConstraintViolationException(HttpServletRequest request, HttpServletResponse response, ConstraintViolationException e) {
//        LOG.debug("handleConstraintViolationException");
//        StringBuilder message = new StringBuilder();
//        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
//        for (ConstraintViolation<?> violation : violations) {
//            Path path = violation.getPropertyPath();
//            String[] pathArr = StringUtils.splitByWholeSeparatorPreserveAllTokens(path.toString(), ".");
//            message.append(pathArr[1]).append(violation.getMessage()).append(",");
//        }
//        message = new StringBuilder(message.substring(0, message.length() - 1));
//
//        return render(request, response, ResultData.build().code(HttpStatus.INTERNAL_SERVER_ERROR).msg(message.toString()), e);
//    }


    /**
     * shiro权限未授权异常
     *
     * @param request
     * @param response
     * @param e
     * @return
     */
    @ExceptionHandler(value = UnauthorizedException.class)
    public ModelAndView handleUnauthorizedException(HttpServletRequest request, HttpServletResponse response, UnauthorizedException e) {
        LOG.debug("handleUnauthorizedException");
        return render(request, response, ResultData.build().code(HttpStatus.UNAUTHORIZED).msg("无访问权限!"), e);
    }

    /**
     * shiro权限未授权异常
     *
     * @param request
     * @param response
     * @param e
     * @return
     */
    @ExceptionHandler(value = LockedAccountException.class)
    public ModelAndView handleLockedAccountException(HttpServletRequest request, HttpServletResponse response, LockedAccountException e) {
        LOG.debug("handleLockedAccountException");
        response.setStatus(HttpStatus.LOCKED.value());
        return render(request, response, ResultData.build().code(HttpStatus.LOCKED).msg(e.getMessage()), e);
    }

    /**
     * shiro权限密码错误
     *
     * @param request
     * @param response
     * @param e
     * @return
     */
    @ExceptionHandler(value = IncorrectCredentialsException.class)
    public ModelAndView handleIncorrectCredentialsException(HttpServletRequest request, HttpServletResponse response, IncorrectCredentialsException e) {
        LOG.debug("IncorrectCredentialsException");
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return render(request, response, ResultData.build().code(HttpStatus.BAD_REQUEST).msg("管理员密码错误!"), e);
    }

    /**
     * shiro权限账号错误
     *
     * @param request
     * @param response
     * @param e
     * @return
     */
    @ExceptionHandler(value = UnknownAccountException.class)
    public ModelAndView handleUnknownAccountException(HttpServletRequest request, HttpServletResponse response, UnknownAccountException e) {
        LOG.debug("UnknownAccountException");
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return render(request, response, ResultData.build().code(HttpStatus.BAD_REQUEST).msg("当前站点下此管理员账号不存在!"), e);
    }

    /**
     * 登录异常
     *
     * @param request
     * @param response
     * @param e
     * @return
     */
    @ExceptionHandler(value = AuthenticationException.class)
    public ModelAndView handleAuthenticationException(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) {
        LOG.debug("AuthenticationException");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return render(request, response, ResultData.build().code(HttpStatus.UNAUTHORIZED).msg("AuthenticationException"), e);
    }

    /**
     * shiro权限错误
     *
     * @param request
     * @param response
     * @param e
     * @return
     */
    @ExceptionHandler(value = AuthorizationException.class)
    public ModelAndView handleAuthorizationException(HttpServletRequest request, HttpServletResponse response, AuthorizationException e) {
        LOG.debug("AuthorizationException");

        return render(request, response, ResultData.build().code(HttpStatus.UNAUTHORIZED).msg("AuthorizationException"), e);
    }


    /**
     * shiro权限错误
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = CredentialsException.class)
    public ModelAndView handleCredentialsException(HttpServletRequest request, HttpServletResponse response, CredentialsException e) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return render(request, response, ResultData.build().code(HttpStatus.UNAUTHORIZED).msg("CredentialsException"), e);

    }


    /**
     * session失效异常
     *
     * @param request
     * @param response
     * @param e
     * @return
     */
    @ExceptionHandler(value = ExpiredSessionException.class)
    public ModelAndView handleExpiredSessionException(HttpServletRequest request, HttpServletResponse response, ExpiredSessionException e) {
        LOG.debug("ExpiredSessionException", e);
        response.setStatus(HttpStatus.GATEWAY_TIMEOUT.value());
        return render(request, response, ResultData.build().code(HttpStatus.GATEWAY_TIMEOUT), e);
    }

    /**
     * 返回异常信息处理
     *
     * @param request
     * @param response
     * @param e
     * @return
     */
    private ModelAndView render(HttpServletRequest request, HttpServletResponse response, ResultData resultData, Exception e){
        LOG.debug("url: {}",request.getRequestURI());
        e.printStackTrace();

        String contextPath = request.getServletContext().getContextPath();
        //项目路径
        request.setAttribute(Const.BASE,contextPath);
        //设置当前地址参数，方便页面获取
        try {
            request.setAttribute(Const.PARAMS, BasicUtil.assemblyRequestUrlParams());
        } catch (Exception _e) {
            LOG.debug("请求参数异常");
        }
        String requestPath = request.getServletPath();
        saveExceptionLog(requestPath, e);
        if (BasicUtil.isAjaxRequest(request)) {
            try {

                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(resultData.getCode());
                PrintWriter writer = response.getWriter();
                writer.write(JSONUtil.toJsonStr(resultData));
                writer.flush();
                writer.close();

            } catch (IOException ex) {
                ex.printStackTrace();
            }


        } else {
            return new ModelAndView("/error/index", resultData);
        }

        return null;
    }

    /**
     * 因为这里使用了biz数据库,所以会将一个线程变为多线程,导致异常捕获不到
     * @param requestPath 请求地址
     * @param e
     */
    private void saveExceptionLog(String requestPath,  Exception e) {
        List<StackTraceElement> stackElements = new ArrayList<>();
        stackElements.addAll(Arrays.asList(e.getStackTrace()));
        LogEntity log = new LogEntity();
        log.setLogResult(e.getMessage()==null?e.toString():e.getMessage());
        stackElements = this.getAllStackTrace(log, stackElements, e.getCause());
        List<String> stackList = stackElements.stream().filter(s -> s.getClassName().contains("net.mingsoft")).map(StackTraceElement::getFileName).filter(fileName -> Objects.requireNonNull(fileName).contains(".java")).collect(Collectors.toList());
        List<String> className = stackElements.stream().filter(s -> s.getClassName().contains("net.mingsoft")).filter(fileName -> Objects.requireNonNull(fileName.getFileName()).contains(".java")).map(StackTraceElement::getClassName).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(className) && className.size() > 1) {
            log.setLogMethod(className.get(0)); //出错的类
        }else {
            log.setLogMethod(e.getStackTrace()[0].getClassName());
        }
        if (CollUtil.isNotEmpty(stackList) && className.size() > 1) {
            log.setLogTitle(stackList.get(0)); //异常标题
        }else {
            log.setLogMethod(e.getStackTrace()[0].getFileName());
        }
        log.setLogUrl(requestPath); //请求地址
        log.setLogErrorMsg(stackElements.toString()); //详细异常信息
        log.setLogLocation(IpUtils.getRealAddressByIp(BasicUtil.getIp())); // ip地理位置
        log.setCreateDate(new Date());
        log.setLogBusinessType("error");
        log.setLogStatus("error");
        if(BasicUtil.getManager()!=null) {
            log.setLogUser(BasicUtil.getManager().getManagerName());
        }
        log.setLogIp(BasicUtil.getIp());
        logBiz.save(log);

    }

    /**
     * 递归获取所有异常及原因堆栈,填充日志msg
     * @param log 日志实体
     * @param traceElementList 堆栈数组
     * @param t 异常原因
     * @return 堆栈数组
     */
    private List<StackTraceElement> getAllStackTrace(LogEntity log, List<StackTraceElement> traceElementList, Throwable t) {
        if (t != null) {
            // 设置msg
            if (StringUtils.isBlank(log.getLogResult())) {
                log.setLogResult(t.getMessage());
            }
            // 递归获取所有的CauseTrace
            traceElementList.addAll(0, Arrays.asList(t.getStackTrace()));
            this.getAllStackTrace(log, traceElementList, t.getCause());
        }
        return traceElementList;
    }

}
