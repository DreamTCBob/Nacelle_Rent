package com.manager.nacelle_rent.entity;

import com.alibaba.fastjson.JSONObject;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "list")
@Component
@PropertySource("classpath:/application-list.properties")
public class ConfigurationList {

    private String outrigger;
    private String electricMachinery;
    private String safetyRope100;
    private String wireRope110;
    private String cable100;
    private String safetyLock;
    private String counterWeight;
    private String electricBox;
    private String genSafeRopeSLD;
    private String steelWireClipFreeFastener;
    private String limitDevice;
    private String aluferBasket;
    private String intelligentElectricBox;
    private String camera;
    private String cableTensionController;
    private String intelligentSafeRopeSLD;

    public List<JSONObject> configurationListToString(int num){
        List<JSONObject> list = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("part","挑梁");
        jsonObject.put("unit","套");
        jsonObject.put("quantity",Integer.parseInt(getOutrigger())*num);
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("part","电机");
        jsonObject1.put("unit","个");
        jsonObject1.put("quantity",Integer.parseInt(getElectricMachinery())*num);
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("part","安全绳（100米）");
        jsonObject2.put("unit","条");
        jsonObject2.put("quantity",Integer.parseInt(getSafetyRope100())*num);
        JSONObject jsonObject3 = new JSONObject();
        jsonObject3.put("part","钢丝绳（110米）");
        jsonObject3.put("unit","条");
        jsonObject3.put("quantity",Integer.parseInt(getWireRope110())*num);
        JSONObject jsonObject4 = new JSONObject();
        jsonObject4.put("part","电缆(100米）");
        jsonObject4.put("unit","条");
        jsonObject4.put("quantity",Integer.parseInt(getCable100())*num);
        JSONObject jsonObject5 = new JSONObject();
        jsonObject5.put("part","安全锁");
        jsonObject5.put("unit","个");
        jsonObject5.put("quantity",Integer.parseInt(getSafetyLock())*num);
        JSONObject jsonObject6 = new JSONObject();
        jsonObject6.put("part","配重");
        jsonObject6.put("unit","块");
        jsonObject6.put("quantity",Integer.parseInt(getCounterWeight())*num);
        JSONObject jsonObject7 = new JSONObject();
        jsonObject7.put("part","电箱");
        jsonObject7.put("unit","个");
        jsonObject7.put("quantity",Integer.parseInt(getElectricBox())*num);
        JSONObject jsonObject8 = new JSONObject();
        jsonObject8.put("part","普通安全绳自锁器");
        jsonObject8.put("unit","个");
        jsonObject8.put("quantity",Integer.parseInt(getGenSafeRopeSLD())*num);
        JSONObject jsonObject9 = new JSONObject();
        jsonObject9.put("part","钢丝免卡扣挂件");
        jsonObject9.put("unit","个");
        jsonObject9.put("quantity",Integer.parseInt(getSteelWireClipFreeFastener())*num);
        JSONObject jsonObject10 = new JSONObject();
        jsonObject10.put("part","限位器");
        jsonObject10.put("unit","套");
        jsonObject10.put("quantity",Integer.parseInt(getLimitDevice())*num);
        JSONObject jsonObject11 = new JSONObject();
        jsonObject11.put("part","铝合金篮筐");
        jsonObject11.put("unit","个");
        jsonObject11.put("quantity",Integer.parseInt(getAluferBasket())*num);
        JSONObject jsonObject12 = new JSONObject();
        jsonObject12.put("part","智能电箱");
        jsonObject12.put("unit","个");
        jsonObject12.put("quantity",Integer.parseInt(getIntelligentElectricBox())*num);
        JSONObject jsonObject13 = new JSONObject();
        jsonObject13.put("part","摄像头");
        jsonObject13.put("unit","个");
        jsonObject13.put("quantity",Integer.parseInt(getCamera())*num);
        JSONObject jsonObject14 = new JSONObject();
        jsonObject14.put("part","电缆拉紧控制器");
        jsonObject14.put("unit","个");
        jsonObject14.put("quantity",Integer.parseInt(getCableTensionController())*num);
        JSONObject jsonObject15 = new JSONObject();
        jsonObject15.put("part","智能安全绳自锁器");
        jsonObject15.put("unit","个");
        jsonObject15.put("quantity",Integer.parseInt(getIntelligentSafeRopeSLD())*num);
        list.add(jsonObject);
        list.add(jsonObject1);
        list.add(jsonObject2);
        list.add(jsonObject3);
        list.add(jsonObject4);
        list.add(jsonObject5);
        list.add(jsonObject6);
        list.add(jsonObject7);
        list.add(jsonObject8);
        list.add(jsonObject9);
        list.add(jsonObject10);
        list.add(jsonObject11);
        list.add(jsonObject12);
        list.add(jsonObject13);
        list.add(jsonObject14);
        list.add(jsonObject15);
        return list;
    }

    public String getOutrigger(){return outrigger;}

    public void setOutrigger(String outrigger){this.outrigger = outrigger;}

    public String getElectricMachinery(){return electricMachinery;}

    public void setElectricMachinery(String electricMachinery){this.electricMachinery = electricMachinery;}

    public String getSafetyRope100(){return safetyRope100;}

    public void setSafetyRope100(String safetyRope100){this.safetyRope100 = safetyRope100;}

    public String getWireRope110(){return wireRope110;}

    public void setWireRope110(String wireRope110){this.wireRope110 = wireRope110;}

    public String getCable100(){return cable100;}

    public void setCable100(String cable100){this.cable100 = cable100;}

    public String getSafetyLock(){return safetyLock;}

    public void setSafetyLock(String safetyLock){this.safetyLock = safetyLock;}

    public String getCounterWeight(){return counterWeight;}

    public void setCounterWeight(String counterWeight){this.counterWeight = counterWeight;}

    public String getElectricBox(){return electricBox;}

    public void setElectricBox(String electricBox){this.electricBox = electricBox;}

    public String getGenSafeRopeSLD(){return genSafeRopeSLD;}

    public void setGenSafeRopeSLD(String genSafeRopeSLD){this.genSafeRopeSLD = genSafeRopeSLD;}

    public String getSteelWireClipFreeFastener(){return steelWireClipFreeFastener;}

    public void setSteelWireClipFreeFastener(String steelWireClipFreeFastener){this.steelWireClipFreeFastener = steelWireClipFreeFastener;}

    public String getLimitDevice(){return limitDevice;}

    public void setLimitDevice(String limitDevice){this.limitDevice = limitDevice;}

    public String getAluferBasket(){return aluferBasket;}

    public void setAluferBasket(String aluferBasket){this.aluferBasket = aluferBasket;}

    public String getIntelligentElectricBox(){return intelligentElectricBox;}

    public void setIntelligentElectricBox(String intelligentElectricBox){this.intelligentElectricBox = intelligentElectricBox;}

    public String getCamera(){return camera;}

    public void setCamera(String camera){this.camera = camera;}

    public String getCableTensionController(){return cableTensionController;}

    public void setCableTensionController(String cableTensionController){this.cableTensionController = cableTensionController;}

    public String getIntelligentSafeRopeSLD(){return intelligentSafeRopeSLD;}

    public void setIntelligentSafeRopeSLD(String intelligentSafeRopeSLD){this.intelligentSafeRopeSLD = intelligentSafeRopeSLD;}

}
