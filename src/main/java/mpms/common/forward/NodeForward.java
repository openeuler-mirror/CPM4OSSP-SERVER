package io.jpom.common.forward;

import cn.hutool.core.net.URLEncoder;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.*;
import cn.jiangzeyin.common.DefaultSystemLog;
import cn.jiangzeyin.common.JsonMessage;
import cn.jiangzeyin.common.request.XssFilter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import io.jpom.common.BaseServerController;
import io.jpom.model.data.NodeModel;
import io.jpom.model.data.UserModel;
import io.jpom.system.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 节点请求转发
 */
public class NodeForward {

    /**
     * 普通消息转发
     *
     * @param nodeModel 节点
     * @param request   请求
     * @param nodeUrl   节点的url
     * @param <T>       泛型
     * @return JSON
     */
    public static <T> JsonMessage<T> request(NodeModel nodeModel, HttpServletRequest request, NodeUrl nodeUrl) {
        return request(nodeModel, request, nodeUrl, true, null, null, null, null);
    }

    /**
     * 普通消息转发
     *
     * @param nodeModel  节点
     * @param request    请求
     * @param nodeUrl    节点的url
     * @param jsonObject 数据
     * @return JSON
     */
    public static JsonMessage<String> request(NodeModel nodeModel, HttpServletRequest request, NodeUrl nodeUrl, JSONObject jsonObject) {
        return request(nodeModel, request, nodeUrl, true, null, jsonObject, null, null);
    }






}
