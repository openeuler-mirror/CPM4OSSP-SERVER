import cn.hutool.Hutool;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.URLUtil;
import io.jpom.common.JpomManifest;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Created by jiangzeyin on 2019/4/13.
 */
public class TestJarClass {

	@Test
	public void test(){
		JarFile jarFile = URLUtil.getJarFile(Hutool.class.getResource(""));
		System.out.println(jarFile.getName());
		URL location = ClassUtil.getLocation(JpomManifest.class);
		System.out.println(location);

		String location1 = ClassUtil.getLocationPath(JpomManifest.class);
		System.out.println(location1);
	}


    public static void main(String[] args) throws IOException {
        JarFile jarFile = new JarFile("D:\\SystemDocument\\Desktop\\springboot-test-jar-0.0.1-SNAPSHOT.jar");

        Manifest manifest = jarFile.getManifest();
        Attributes attributes = manifest.getMainAttributes();
        String mainClass = attributes.getValue("Main-Class");
        System.out.println(mainClass);
    }
}
