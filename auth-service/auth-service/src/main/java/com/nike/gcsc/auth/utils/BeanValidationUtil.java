package com.nike.gcsc.auth.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.nike.gcsc.authapi.request.AmountUsersBean;
import com.nike.gcsc.authapi.request.GroupRequestBean;
import com.nike.gcsc.authapi.request.PermissionRequestBean;
import com.nike.gcsc.authapi.request.ProjectRequestBean;

/**
 * @author: tomfang
 * @description:
 * @date: 13:40 2019/7/5
 **/
public class BeanValidationUtil {

    public static void checkSaveProjectBean(ProjectRequestBean bean) throws Exception {
        if (isNotEmpty(bean)) {
            if (StringUtils.isEmpty(bean.getName())) {
                throw new Exception("project name can not be null");
            }
        } else {
            throw new Exception("Paramter can not be null");
        }
    }

    public static void checkSaveGroupBean(GroupRequestBean bean) throws Exception {
        if (isNotEmpty(bean)) {
            if (StringUtils.isEmpty(bean.getGroupName())) {
                throw new Exception("GroupName can not be null");
            }
        } else {
            throw new Exception("Paramter can not be null");
        }
    }

    public static void checkAmountUserBean(AmountUsersBean bean) throws Exception {
        if (isNotEmpty(bean)) {
            if (bean.getGroupId() <= 0) {
                throw new Exception("GroupId is not valid");
            }
        } else {
            throw new Exception("Paramter can not be null");
        }
    }

    public static void checkSavePermissionBean(PermissionRequestBean bean) throws Exception {
        if (isNotEmpty(bean)) {
            if (StringUtils.isEmpty(bean.getName())) {
                throw new Exception("name can not be null");
            } else if (null == bean.getProjectId()) {
                throw new Exception("project id can not be null");
            } else if (StringUtils.isEmpty(bean.getUriRegPattern())) {
                throw new Exception("UrlRegPattern name can not be null");
            }
        } else {
            throw new Exception("Paramter can not be null");
        }
    }

    public static boolean isEmpty(Object obj) {
        if (null == obj) {
            return true;
        } else if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        } else if (obj instanceof Collection) {
            return ((Collection<?>) obj).isEmpty();
        } else {
			return obj instanceof Map ? ((Map<?, ?>) obj).isEmpty() : false;
        }
    }

    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

}
