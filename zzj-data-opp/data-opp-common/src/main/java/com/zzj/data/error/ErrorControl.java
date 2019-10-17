package com.zzj.data.error;

import com.zzj.data.result.ApplicationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ErrorControl implements ErrorController {
    private static final String JAVAX_SERVLET_ERROR_STATUS_CODE = "javax.servlet.error.status_code";
    private final static String ERROR_PATH = "error";
    private final  Logger logger = LoggerFactory.getLogger(this.getClass());
    public String getErrorPath() {
        return ERROR_PATH;
    }

    @RequestMapping(ERROR_PATH)
    @ResponseBody
    public ResponseEntity<?> error(HttpServletRequest request, Exception e) {
        HttpStatus httpStatus=getStatus(request);
        ApplicationResult<Object> result=new ApplicationResult<Object>();
        result.setState(httpStatus.value());
        result.setMsg(e.getMessage());
        logger.error("执行异常{}",e);
        return new ResponseEntity<Object>(result,httpStatus);
    }

    private HttpStatus getStatus(HttpServletRequest httpServletRequest) {
        Integer code = (Integer) httpServletRequest.getAttribute(JAVAX_SERVLET_ERROR_STATUS_CODE);
        if (code == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(code);

    }
}
