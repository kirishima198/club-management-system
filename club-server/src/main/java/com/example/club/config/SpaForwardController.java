package com.example.club.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * SPA 路由回退：非静态资源、非 API 的前端路由全部转发到 index.html，
 * 使打包后的单 jar 部署支持 vue-router history 模式直接访问/刷新。
 */
@Controller
public class SpaForwardController {

    @RequestMapping(value = {
            "/{p1:[^\\.]+}",
            "/{p1:[^\\.]+}/{p2:[^\\.]+}",
            "/{p1:[^\\.]+}/{p2:[^\\.]+}/{p3:[^\\.]+}"
    })
    public String forward() {
        return "forward:/index.html";
    }
}