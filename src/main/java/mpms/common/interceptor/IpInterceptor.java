package io.jpom.common.interceptor;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.jiangzeyin.common.JsonMessage;
import cn.jiangzeyin.common.interceptor.InterceptorPattens;
import cn.jiangzeyin.common.spring.SpringUtil;
import io.jpom.common.ServerOpenApi;
import io.jpom.model.data.SystemIpConfigModel;
import io.jpom.service.system.SystemIpConfigService;
import org.springframework.http.MediaType;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ip 访问限制拦截器
 *
 *
 */
@InterceptorPattens(sort = -2, exclude = ServerOpenApi.API + "**")
public class IpInterceptor extends BaseLinxInterceptor {

	@Override
	protected boolean preHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
		String clientIp = ServletUtil.getClientIP(request);
		if (StrUtil.equals(NetUtil.LOCAL_IP, clientIp)) {
			return true;
		}
		SystemIpConfigService bean = SpringUtil.getBean(SystemIpConfigService.class);
		SystemIpConfigModel config = bean.getConfig();
		if (config == null) {
			return true;
		}
		// 判断不允许访问
		String prohibited = config.getProhibited();
		if (StrUtil.isNotEmpty(prohibited) && this.checkIp(prohibited, clientIp, false)) {
			ServletUtil.write(response, JsonMessage.getString(900, "Prohibition of access"), MediaType.APPLICATION_JSON_VALUE);
			return false;
		}
		String allowed = config.getAllowed();
		if (StrUtil.isEmpty(allowed) || this.checkIp(allowed, clientIp, true)) {
			return true;
		}
		ServletUtil.write(response, JsonMessage.getString(900, "Prohibition of access"), MediaType.APPLICATION_JSON_VALUE);
		return false;
	}


	/**
	 * 检查ip 地址是否可以访问
	 *
	 * @param value    配置的值
	 * @param ip       被检查的 ip 地址
	 * @param checkAll 是否检查开放所有、避免禁止所有 ip 访问
	 * @return true 命中检查项
	 */
	private boolean checkIp(String value, String ip, boolean checkAll) {
		long ipNum = NetUtil.ipv4ToLong(ip);
		String[] split = StrUtil.splitToArray(value, StrUtil.LF);
		boolean check;
		for (String itemIp : split) {
			itemIp = itemIp.trim();
			if (itemIp.startsWith("#")) {
				continue;
			}
			if (checkAll && StrUtil.equals(itemIp, "0.0.0.0")) {
				// 开放所有
				return true;
			}
			if (StrUtil.contains(itemIp, CharUtil.SLASH)) {
				// ip段
				String[] itemIps = StrUtil.splitToArray(itemIp, StrUtil.SLASH);
				long aBegin = NetUtil.ipv4ToLong(itemIps[0]);
				long aEnd = NetUtil.ipv4ToLong(itemIps[1]);
				check = (ipNum >= aBegin) && (ipNum <= aEnd);
			} else {
				check = StrUtil.equals(itemIp, ip);
			}
			if (check) {
				return true;
			}
		}
		return false;
	}
}
