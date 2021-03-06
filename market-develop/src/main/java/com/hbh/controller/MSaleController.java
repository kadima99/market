package com.hbh.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hbh.entity.Sale;
import com.hbh.service.imp.SaleServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * @Author Binvor
 * @Date 2019年4月19日下午1:58:43
 * @Des
 */
@Controller
@RequestMapping("/manager/flatform/sale")
public class MSaleController {
    @Autowired
    SaleServiceImp saleServiceImp;


//  跳转到增加页面

    @RequestMapping("/toadd")
    public String toadd() {
        return "manager/addsale";
    }
//  跳转到修改页面

    @RequestMapping("/toupdate")
    public String editSale(Sale sale, HttpServletRequest request, Model model) {
        model.addAttribute("sale", saleServiceImp.getbyid(sale.getSaleid()));
        return "manager/editSale";
    }
//  先判断数据库有没有，有就更新，没有就新增

    @RequestMapping("/insert")
    public String insert(Sale sale, HttpServletRequest request, Model model) {
        if (null == saleServiceImp.getbyid(sale.getSaleid())) {
            saleServiceImp.insert(sale);
        } else {
            saleServiceImp.update(sale);
        }
        return "redirect:getall";

    }
    @RequestMapping("/addRole.do")
    public String addRole(String zhi){

        String[] split = zhi.split(",");
        for (int i = 0; i < split.length; i++) {
            String string = split[i];
           saleServiceImp.pay(string);
            System.out.println(string);
        }
        return "redirect:getall";
    }
//    删除

    @RequestMapping("/delete")
    public String delete(String saleid) {
        saleServiceImp.delete(saleid);
        return "redirect:getall";
    }

//    结账


    @RequestMapping("/pay")
    public String pay(String saleid , HttpServletResponse response) throws IOException {
        saleServiceImp.pay(saleid);
        PrintWriter out =  response.getWriter();
        out.print("<script>alert('操作成功')<script>");
        return "redirect:getall";
    }
//    修改

    @RequestMapping("/update")
    public String update(Sale sale, HttpServletRequest request, Model model) {
        if (saleServiceImp.update(sale)) {
            sale = saleServiceImp.getbyid(sale.getSaleid());
            model.addAttribute("sale", sale);
            return "redirect:getall";
        }
        return null;
    }

//    查询所有

    @RequestMapping("/getall")
    public String getall_cus(ModelMap model,
                             @RequestParam(defaultValue = "1", required = true, value = "pn") Integer pn
    ) {
        PageHelper.startPage(pn, 10);
        List<Sale> sales = saleServiceImp.getall();
        for(int i=0;i<sales.size();i++){
            System.out.println(sales.get(i));
        }
        PageInfo<Sale> pageInfo = new PageInfo<Sale>(sales);
        model.addAttribute("pageInfo", pageInfo);
        return "manager/getall_sale";

    }

    @RequestMapping("/getfinish")
    public String getfinish_cus(ModelMap model,
                             @RequestParam(defaultValue = "1", required = true, value = "pn") Integer pn
    ) {
        PageHelper.startPage(pn, 10);
        System.out.println("我进入了");
        List<Sale> sales = saleServiceImp.getallNull();
        for(int i=0;i<sales.size();i++){
            System.out.println(sales.get(i));
        }
        PageInfo<Sale> pageInfo = new PageInfo<Sale>(sales);
        model.addAttribute("pageInfo", pageInfo);
        return "manager/getHistory_sale";

    }
//  查询单个

    @RequestMapping("/getbyid")
    public String getbyid(String saleid, HttpServletRequest request, Model model) {
        request.setAttribute("sale", saleServiceImp.getbyid(saleid));
        model.addAttribute("sale", saleServiceImp.getbyid(saleid));
        return "manager/getsale";

    }

    @RequestMapping("getbyparams")
    public String getbyparams(HttpServletRequest request, Model model, @RequestParam(value = "proid", required = false) String proid,
                              @RequestParam(value = "cusid", required = false) String cusid, @RequestParam(value = "pname", required = false) String pname,
                              @RequestParam(value = "cusname", required = false) String cusname, @RequestParam(defaultValue = "1", required = true, value = "pn") Integer pn
    ) {
        PageHelper.startPage(pn, 100);
        List<Sale> sale = saleServiceImp.getbyparams(proid, cusid, pname, cusname);
        PageInfo<Sale> pageInfo = new PageInfo<Sale>(sale);
        model.addAttribute("pageInfo", pageInfo);
        return "manager/getsalebyparams";

    }

    @RequestMapping(value = "echartsData", method = RequestMethod.POST)
    @ResponseBody
    public List<Map<String, Object>> initChart() {
        return saleServiceImp.pieData();
    }

//  跳转到增加页面

    @RequestMapping("/toechart")
    public String toechart() {
        return "manager/echart";

    }


}
