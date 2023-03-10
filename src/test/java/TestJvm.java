import cn.hutool.system.SystemUtil;
import com.sun.tools.attach.AttachNotSupportedException;
import sun.jvmstat.monitor.*;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by jiangzeyin on 2019/4/4.
 */
public class TestJvm {
    public static void main(String[] args) throws IOException, AttachNotSupportedException, MonitorException, URISyntaxException {
        System.out.println(SystemUtil.getJavaRuntimeInfo().getVersion());

        // 获取监控主机
        MonitoredHost local = MonitoredHost.getMonitoredHost("localhost");
        // 取得所有在活动的虚拟机集合
        Set<?> vmlist = new HashSet<Object>(local.activeVms());
        // 遍历集合，输出PID和进程名
        for (Object process : vmlist) {
            MonitoredVm vm = local.getMonitoredVm(new VmIdentifier("//" + process));
            // 获取类名
            String processname = MonitoredVmUtil.mainClass(vm, true);
            System.out.println(processname);
            if (!"io.jpom.JpomAgentApplication".equals(processname)) {
                continue;
            }
            System.out.println(vm.getVmIdentifier().getUserInfo());
            System.out.println(vm.getVmIdentifier().toString());
        }
    }

    public static int getPid() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        System.out.println(runtime);
        String name = runtime.getName(); // format: "pid@hostname"
        System.out.println(name);
        try {
            return Integer.parseInt(name.substring(0, name.indexOf('@')));
        } catch (Exception e) {
            return -1;
        }
    }
}
