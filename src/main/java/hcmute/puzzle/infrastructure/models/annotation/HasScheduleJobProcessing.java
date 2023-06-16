package hcmute.puzzle.infrastructure.models.annotation;

import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class HasScheduleJobProcessing {
    private static final String packageName = "hcmute.puzzle";

    public static List<Class<?>> getAnnotatedClass() {
        List<Class<?>> annotatedClasses = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        List<String> classErrors = new ArrayList<>();
        try {
            // Get the class loader and a list of all class files on the classpath
            List<URL> classFileUrls = Collections.list(classLoader.getResources(""));


            // Iterate over each class file URL
            for (URL classFileUrl : classFileUrls) {
                classErrors.add(classFileUrl.toString());
                log.warn(String.valueOf(classFileUrl.toURI()));
                File classFile = new File(classFileUrl.toURI());
                if (classFile.isDirectory()) {
                    // If the URL points to a directory, recursively scan all files within it
                    scanDirectoryForAnnotatedClasses(classFile, classLoader, annotatedClasses);
                } else {
                    // If the URL points to a file, load the class and check if it is annotated
                    String className = classFile.getName().replace(".class", "");
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(HasScheduleJob.class)) {
                        annotatedClasses.add(clazz);
                    }
                }
            }
        } catch (IOException e) {
            log.error("IOException");
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            log.error("URISyntaxException");
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            log.error("ClassNotFoundException");
            log.error(e.getMessage());
            //throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Class error: ");
            log.error(e.getMessage());
        }
        log.warn("Class error: " + classErrors.toString());

        System.out.println("Classes annotated with @MyAnnotation:");
        for (Class<?> clazz : annotatedClasses) {
            System.out.println(clazz.getName());
        }

        return annotatedClasses;
    }

    private static void scanDirectoryForAnnotatedClasses(File directory, ClassLoader classLoader, List<Class<?>> annotatedClasses) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                scanDirectoryForAnnotatedClasses(file, classLoader, annotatedClasses);
            } else if (file.getName().endsWith(".class")) {
                try {
                    //String className = getClass(file.getName(), packageName);
                    //file.getAbsolutePath().replace(".class", "");
                    int start = file.getPath().indexOf(packageName.replaceAll("[.]", "\\\\"));
                    String path = file.getPath().substring(start).replaceAll("\\\\", ".").replace(".class", "");
                    Class<?> clazz = Class.forName(path);
                    if (clazz.isAnnotationPresent(HasScheduleJob.class)) {
                        annotatedClasses.add(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    log.error("ClassNotFoundException");
                    continue;
                //throw new RuntimeException(e);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("Exception");
                    //throw new RuntimeException(e);
                    continue;
                } catch (Error e) {
                    //e.printStackTrace();
                    log.error("Error: " + e.getMessage());
                    continue;
                }

            }
        }
    }

    private static void scanPackageForAnnotatedClasses(List<Class<?>> annotatedClasses) {
        String packageName = "hcmute.puzzle";
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources;
        try {
            resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                String fileName = resource.getFile();
                if (fileName.endsWith(".class")) {
                    String className = packageName + '.' + fileName.substring(fileName.lastIndexOf('/') + 1, fileName.length() - 6);
                    System.out.println("Class name: " + className);
                    annotatedClasses.add(resource.getClass());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static List<Class<?>> findAllClassesUsingClassLoader(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .collect(Collectors.toList());
    }

    private static Class<?> getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage());
            // handle the exception
        }
        return null;
    }

}
