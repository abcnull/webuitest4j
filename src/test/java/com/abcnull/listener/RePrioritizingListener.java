package com.abcnull.listener;

import org.testng.IAnnotationTransformer;
import org.testng.Reporter;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * 优先级监听器
 * 使得 xml 中 include 标签依 class 标签分隔之后再按照优先级排序，不会将所有 class 标签中 include 标签汇总排序
 *
 * @author abcnull
 * @version 1.0.0
 * @date 2019/9/9
 */
public class RePrioritizingListener implements IAnnotationTransformer {
    HashMap<Object, Integer> priorityMap = new HashMap<>();
    Integer class_priorityCounter = 10000;
    // The length of the final priority assigned to each method.
    Integer max_testpriorityLength = 4;

    /**
     * transform 方法重写
     *
     * @param annotation      ITestAnnotation
     * @param testClass       Class
     * @param testConstructor Constructor
     * @param testMethod      Method
     */
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        // class of the test method.
        Class<?> declaringClass = testMethod.getDeclaringClass();
        // Current priority of the test assigned at the test method.
        int test_priority = annotation.getPriority();
        // Current class priority.
        Integer current_ClassPriority = priorityMap.get(declaringClass);

        if (current_ClassPriority == null) {
            current_ClassPriority = class_priorityCounter++;
            priorityMap.put(declaringClass, current_ClassPriority);
        }

        // String concatenatedPriority = Integer.toString(test_priority);
        StringBuffer concatenatedPriority = new StringBuffer(String.valueOf(test_priority));

        // Adds 0's to start of this number.
        while (concatenatedPriority.length() < max_testpriorityLength) {
            concatenatedPriority.insert(0, "0");
        }

        // Concatenates our class counter to the test level priority (example
        // for test with a priority of 1: 1000100001; same test class with a
        // priority of 2: 1000100002; next class with a priority of 1. 1000200001)
        concatenatedPriority.insert(0, current_ClassPriority.toString());

        //Sets the new priority to the test method.
        annotation.setPriority(Integer.parseInt(concatenatedPriority.toString()));

        String printText = testMethod.getName() + " Priority = " + concatenatedPriority;
        Reporter.log(printText);
        System.out.println(printText);
    }
}