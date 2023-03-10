package io.jpom.common;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Function;

/**
 * url 重定向
 * 配置nginx 代理实现
 *
 */
public class UrlRedirectUtil {

	private static int getPort(HttpServletRequest request) {
		String proxyPort = ServletUtil.getHeaderIgnoreCase(request, "X-Forwarded-Port");
		int port = 0;
		if (StrUtil.isNotEmpty(proxyPort)) {
			port = Integer.parseInt(proxyPort);
		}
		return port;
	}

	/**
	 * 二级代理路径
	 *
	 * @param request req
	 * @return context-path+nginx配置
	 */
	public static String getHeaderProxyPath(HttpServletRequest request, String headName) {
		return getHeaderProxyPath(request, headName, null);
	}

	/**
	 * 二级代理路径
	 *
	 * @param request req
	 * @return context-path+nginx配置
	 */
	public static String getHeaderProxyPath(HttpServletRequest request, String headName, Function<String, String> function) {
		String proxyPath = ServletUtil.getHeaderIgnoreCase(request, headName);

		if (StrUtil.isEmpty(proxyPath)) {
			return request.getContextPath();
		}
		// 回调处理
		if (function != null) {
			proxyPath = function.apply(proxyPath);
		}

		proxyPath = FileUtil.normalize(request.getContextPath() + StrUtil.SLASH + proxyPath);
		if (proxyPath.endsWith(StrUtil.SLASH)) {
			proxyPath = proxyPath.substring(0, proxyPath.length() - 1);
		}
		return proxyPath;
	}
}
