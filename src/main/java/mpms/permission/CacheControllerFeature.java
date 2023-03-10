package mpms.permission;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import mpms.plugin.ClassFeature;
import mpms.plugin.Feature;
import mpms.plugin.MethodFeature;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 缓存
 *
 */
public class CacheControllerFeature {

	private static final Map<ClassFeature, Set<MethodFeature>> FEATURE_MAP = new HashMap<>();

	private static final Map<String, UrlFeature> URL_FEATURE_MAP = new TreeMap<>();

	/**
	 * 系统管理员使用的权限
	 */
	private static final List<String> SYSTEM_URL = new ArrayList<>();

	public static Map<ClassFeature, Set<MethodFeature>> getFeatureMap() {
		return FEATURE_MAP;
	}

	/**
	 * 判断是否为系统管理员权限url
	 *
	 * @param url url
	 * @return true 只能是系统管理员访问
	 */
	public static boolean isSystemUrl(String url) {
		return SYSTEM_URL.contains(url);
	}

	/**
	 * 获取url 功能方法对象
	 *
	 * @param url url
	 * @return url功能
	 */
	public static UrlFeature getUrlFeature(String url) {
		return URL_FEATURE_MAP.get(url);
	}


	/**
	 * 扫描包
	 */
	public static void init() {
		// 扫描系统管理员
		Set<Class<?>> classes = ClassUtil.scanPackage("io.jpom.controller");
		classes.forEach(aClass -> {
			String classUrl = getClassUrl(aClass);
			Method[] publicMethods = ReflectUtil.getPublicMethods(aClass);
			for (Method publicMethod : publicMethods) {
				SystemPermission systemPermission = publicMethod.getAnnotation(SystemPermission.class);
				if (systemPermission == null) {
					continue;
				}
				String methodUrl = getMethodUrl(publicMethod);
				String format = String.format("/%s/%s", classUrl, methodUrl);
				format = FileUtil.normalize(format);
				SYSTEM_URL.add(format);
			}
		});
		// 扫描功能方法
		classes.forEach(aClass -> {
			Feature annotation = aClass.getAnnotation(Feature.class);
			if (annotation == null) {
				return;
			}
			ClassFeature classFeature = annotation.cls();
			if (classFeature == ClassFeature.NULL) {
				return;
			}
			String clsUrl = getClassUrl(aClass);

			Set<MethodFeature> methodFeatures1 = FEATURE_MAP.computeIfAbsent(classFeature, classFeature1 -> new HashSet<>());
			Method[] publicMethods = ReflectUtil.getPublicMethods(aClass);

			Set<MethodFeature> methodFeatures = new HashSet<>();
			for (Method publicMethod : publicMethods) {
				Feature publicMethodAnnotation = publicMethod.getAnnotation(Feature.class);
				if (publicMethodAnnotation == null) {
					continue;
				}
				MethodFeature methodFeature = publicMethodAnnotation.method();
				if (methodFeature == MethodFeature.NULL) {
					continue;
				}

				String methodUrl = getMethodUrl(publicMethod);
				methodFeatures.add(methodFeature);

				String format = String.format("/%s/%s", clsUrl, methodUrl);
				format = FileUtil.normalize(format);
				URL_FEATURE_MAP.put(format, new UrlFeature(format, classFeature, methodFeature));
			}
			methodFeatures1.addAll(methodFeatures);
		});
	}

	/**
	 * 获取类上的url
	 *
	 * @param aClass class
	 * @return url
	 */
	private static String getClassUrl(Class<?> aClass) {
		RequestMapping requestMapping = aClass.getAnnotation(RequestMapping.class);
		if (requestMapping == null) {
			return StrUtil.SLASH;
		}
		return requestMapping.value()[0];
	}

	/**
	 * 获取方法名上的url
	 *
	 * @param publicMethod 方法对象
	 * @return url
	 */
	private static String getMethodUrl(Method publicMethod) {
		String val = StrUtil.EMPTY;
		RequestMapping methodAnnotation = publicMethod.getAnnotation(RequestMapping.class);
		if (methodAnnotation == null) {
			GetMapping getMapping = publicMethod.getAnnotation(GetMapping.class);
			if (getMapping == null) {
				PostMapping postMapping = publicMethod.getAnnotation(PostMapping.class);
				if (postMapping != null) {
					val = postMapping.value()[0];
				}
			} else {
				val = getMapping.value()[0];
			}
		} else {
			val = methodAnnotation.value()[0];
		}
		return val;
	}

	public static class UrlFeature {
		private final String url;
		private final ClassFeature classFeature;
		private final MethodFeature methodFeature;

		public String getUrl() {
			return url;
		}

		public ClassFeature getClassFeature() {
			return classFeature;
		}

		public MethodFeature getMethodFeature() {
			return methodFeature;
		}

		UrlFeature(String url, ClassFeature classFeature, MethodFeature methodFeature) {
			this.url = url;
			this.classFeature = classFeature;
			this.methodFeature = methodFeature;
		}
	}
}
