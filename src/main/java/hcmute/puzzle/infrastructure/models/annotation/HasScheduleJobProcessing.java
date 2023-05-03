package hcmute.puzzle.infrastructure.models.annotation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.File;

public class HasScheduleJobProcessing {
    public static List<Class<?>> getAnnotatedClass() {
        List<Class<?>> annotatedClasses = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try {
            // Get the class loader and a list of all class files on the classpath
            List<URL> classFileUrls = Collections.list(classLoader.getResources(""));

            // Iterate over each class file URL
            for (URL classFileUrl : classFileUrls) {
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
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("Classes annotated with @MyAnnotation:");
        for (Class<?> clazz : annotatedClasses) {
            System.out.println(clazz.getName());
        }

        return annotatedClasses;
    }

    private static void scanDirectoryForAnnotatedClasses(File directory, ClassLoader classLoader, List<Class<?>> annotatedClasses) throws Exception {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                scanDirectoryForAnnotatedClasses(file, classLoader, annotatedClasses);
            } else if (file.getName().endsWith(".class")) {
                String className = file.getName().replace(".class", "");
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(HasScheduleJob.class)) {
                    annotatedClasses.add(clazz);
                }
            }
        }
    }

}
