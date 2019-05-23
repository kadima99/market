package com.hbh.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hbh.entity.Kcxx;
import com.hbh.entity.Manager;
import com.hbh.entity.Staff;
import com.hbh.service.imp.KcxxServiceImp;
import com.hbh.service.imp.ManagerServiceImp;
import com.hbh.service.imp.StaffServiceImp;
import com.hbh.tools.Constants;
import com.hbh.tools.RandomValidateCode;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * @Author Binvor
 * @Date 2019年4月16日下午1:56:55
 * @Des 管理员控制层
 */
@Controller
@RequestMapping("/manager")
public class ManagerController {
    @Autowired
    ManagerServiceImp managerServiceImp;
    @Autowired
    StaffServiceImp staffServiceImp;
    @Autowired
    KcxxServiceImp kcxxServiceImp;
    private Logger logger = Logger.getLogger(ManagerController.class);

    @RequestMapping("/login")
    public String login() {
        logger.debug("LoginController welcome AppInfoSystem develpor==================");
        return "manager/managerlogin";
    }

    @RequestMapping(value = "/dologin")
    public String dologin(@RequestParam String managerid, @RequestParam String pwd, @RequestParam String code,
                          HttpSession session, HttpServletRequest request) {
        Manager manager = managerServiceImp.getbyid(managerid, pwd);
        String checkCode=(String) session.getAttribute(Constants.RANDOMCODEKEY);
        if (manager != null) {
            if(checkCode.equals(code)){
                session.setAttribute(Constants.Manager_SESSION, manager);
                return "redirect:/manager/flatform/main";
            }else{
                request.setAttribute("error", "验证码错误");
                return "manager/managerlogin";
            }
        } else {
            request.setAttribute("error", "账号密码不匹配");
            return "manager/managerlogin";
        }
    }

    @RequestMapping(value="/checkCode")
    public void checkCode(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //设置相应类型,告诉浏览器输出的内容为图片
        response.setContentType("image/jpeg");
        //设置响应头信息，告诉浏览器不要缓存此内容
        response.setHeader("pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expire", 0);
        RandomValidateCode randomValidateCode = new RandomValidateCode();
        try {
            randomValidateCode.getRandcode(request, response);//输出图片方法
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/flatform/main")
    public String main(HttpSession session, HttpServletRequest request) {
//		验证是否有session信息，防止非法登录，没有就跳转到登录页面
        if (session.getAttribute(Constants.Manager_SESSION) == null) {
            return "redirect:/manager/login";
        }
        List<Kcxx> kcxx = kcxxServiceImp.kcxxWithProdata();
        List<Kcxx> kcxx2 = kcxxServiceImp.kcxxWithPronum();
        if (!kcxx.isEmpty() || !kcxx2.isEmpty()) {
            request.setAttribute("msg", "存在预警信息请及时处理");
        } else {
            request.setAttribute("msg", "");
        }
        return "manager/main";
    }

    @RequestMapping(value = "/logout")
    public String logout(HttpSession session) {
//		退出清除esison
        session.removeAttribute(Constants.Staff_SESSION);
        session.invalidate();
        return "redirect:/manager/login";

    }

    @RequestMapping("/getbyid")
    public String grxx(String managerid, HttpServletRequest request, Model model) {
        request.setAttribute("manager", managerServiceImp.getmanager(managerid));
        model.addAttribute("manager", managerServiceImp.getmanager(managerid));
        return "manager/grxx";

    }

    @RequestMapping("/update")
    public String update(Manager manager, HttpServletRequest request, Model model) {
        if (managerServiceImp.update(manager)) {
            manager = managerServiceImp.getmanager(manager.getManagerid());
            model.addAttribute("manager", manager);
            return "redirect:/manager/login";
        }
        return null;
    }
//  跳转到修改个人信息页面

    @RequestMapping("/toupdate")
    public String editmanager(Manager manager, HttpServletRequest request, Model model) {
        model.addAttribute("manager", managerServiceImp.getmanager(manager.getManagerid()));
        return "manager/editmanager";
    }

//  跳转到增加页面

    @RequestMapping("/toadd")
    public String toadd() {
        return "manager/addstaff";

    }

//跳转到修改员工信息页面

    @RequestMapping("/toupdatestaff")
    public String editstaff(Staff staff, HttpServletRequest request, Model model) {
        model.addAttribute("staff", staffServiceImp.getbyid(staff.getStaffid()));
        return "manager/editstaff";
    }

    //修改员工信息
    @RequestMapping("/updatestaff")
    public String updatestaff(Staff staff, HttpServletRequest request, Model model) {
        if (staffServiceImp.update(staff)) {
            staff = staffServiceImp.getbyid(staff.getStaffid());
            model.addAttribute("staff", staff);
            return "redirect:getall";
        }
        return null;
    }

//  先判断数据库有没有，有就更新，没有就新增

    @RequestMapping("/insert")
    public String insert(Staff staff, HttpServletRequest request, Model model) {
        if (null == staffServiceImp.getbyid(staff.getStaffid())) {
            staffServiceImp.insert(staff);
        } else {
            staffServiceImp.update(staff);
        }
        return "redirect:getall";

    }
//    删除

    @RequestMapping("/delete")
    public String delete(String staffid) {
        staffServiceImp.delete(staffid);
        return "redirect:getall";
    }
//  查询所有

    @RequestMapping("/getall")
    public String getall_cus(ModelMap model,
                             @RequestParam(defaultValue = "1", required = true, value = "pn") Integer pn
    ) {
        PageHelper.startPage(pn, 4);
        List<Staff> staffs = staffServiceImp.getall();
        PageInfo<Staff> pageInfo = new PageInfo<Staff>(staffs);
        model.addAttribute("pageInfo", pageInfo);
        return "manager/getall_staff";
    }

    //根据条件查询
    @RequestMapping("getbyparams")
    public String getbyparams(@RequestParam(value = "staffname", required = false) String staffname, @RequestParam(value = "staffid", required = false) String staffid,
                              @RequestParam(defaultValue = "1", required = true, value = "pn") Integer pn, HttpServletRequest request, Model model
    ) {
        PageHelper.startPage(pn, 100);
        List<Staff> staffs = staffServiceImp.getbyparams(staffid, staffname);
        PageInfo<Staff> pageInfo = new PageInfo<Staff>(staffs);
        model.addAttribute("pageInfo", pageInfo);
        return "manager/getstaffbyparams";

    }

}
