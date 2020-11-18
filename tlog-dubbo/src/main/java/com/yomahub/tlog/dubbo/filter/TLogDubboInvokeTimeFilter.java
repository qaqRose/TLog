package com.yomahub.tlog.dubbo.filter;

import com.alibaba.fastjson.JSON;
import com.yomahub.tlog.context.TLogContext;
import com.yomahub.tlog.core.rpc.TLogRPCHandler;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * dubbo的调用拦截器
 * @author Bryan.Zhang
 * @since 2020/9/11
 */
@Activate(group = {CommonConstants.PROVIDER},order = -9000)
public class TLogDubboInvokeTimeFilter extends TLogRPCHandler implements Filter {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (!TLogContext.enableInvokeTimePrint()){
            return invoker.invoke(invocation);
        }

        Result result;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try{
            log.info("[TLOG]开始调用接口[{}]的方法[{}],参数为:{}",invocation.getServiceName(),
                    invocation.getMethodName(),
                    JSON.toJSONString(invocation.getArguments()));
            //调用dubbo
            result = invoker.invoke(invocation);
        }finally {
            stopWatch.stop();
            log.info("[TLOG]结束接口[{}]中方法[{}]的调用,耗时为:{}毫秒",invocation.getServiceName(),
                    invocation.getMethodName(),
                    stopWatch.getTime());
        }

        return result;
    }
}