package io.bluextech.ordika.utils;
/* Created by limxuanhui on 19/7/23 */

import com.jayway.jsonpath.JsonPath;
import io.bluextech.ordika.annotations.JsonArg;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.IOException;
import java.util.Objects;

public class JsonArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String JSON_BODY_ATTRIBUTE = "JSON_REQUEST_BODY";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(JsonArg.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory)
            throws Exception {
        String body = getRequestBody(webRequest);
        String jsonPath = Objects.requireNonNull(
                Objects.requireNonNull(parameter.getParameterAnnotation(JsonArg.class)).value());
        Class<?> parameterType = parameter.getParameterType();
        return JsonPath.parse(body).read(jsonPath, parameterType);
    }

    private String getRequestBody(NativeWebRequest webRequest) {
        HttpServletRequest servletRequest = Objects.requireNonNull(
                webRequest.getNativeRequest(HttpServletRequest.class));
        String jsonBody = (String) servletRequest.getAttribute(JSON_BODY_ATTRIBUTE);
        if (jsonBody == null) {
            try {
//                jsonBody = CharStreams.toString((Readable) servletRequest.getInputStream());
                jsonBody = IOUtils.toString(servletRequest.getInputStream());
                servletRequest.setAttribute(JSON_BODY_ATTRIBUTE, jsonBody);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return jsonBody;
    }

}
