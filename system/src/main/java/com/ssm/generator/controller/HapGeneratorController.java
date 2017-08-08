package com.ssm.generator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ssm.generator.dto.GeneratorInfo;
import com.ssm.generator.service.IHapGeneratorService;
import com.ssm.sys.controller.BaseController;
import com.ssm.sys.responceFactory.ResponseData;

@Controller
@RequestMapping(value = "/generator")
public class HapGeneratorController extends BaseController {
    @Autowired
    IHapGeneratorService service;

    @RequestMapping(value = "/alltables", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData showTables() {
        return new ResponseData(service.showTables());
    }

    @RequestMapping(value = "/newtables")
    @ResponseBody
    public int generatorTables(GeneratorInfo generatorInfo) {
        int rs = service.generatorFile(generatorInfo);
        return rs;
    }

}
