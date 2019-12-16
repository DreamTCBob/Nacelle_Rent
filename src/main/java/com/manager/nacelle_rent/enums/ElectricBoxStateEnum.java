package com.manager.nacelle_rent.enums;

public enum ElectricBoxStateEnum {

    INSTALLING(1, "待安装"),
    INSTALL(11,"正在安装"),
    ACCEPTANCE(2, "待审核"),
    ONGOING(3, "使用中"),
    STAND_BY(4, "待报停"),
    STAND_BY_ACCEPTANCE(5, "报停审核");

    private int code;
    private String desc;

    public int getCode(){return code;}

    public void setCode(int code){
        this.code = code;
    }

    public String getDesc(){return desc;}

    public void setDesc(String desc){
        this.desc = desc;
    }

    ElectricBoxStateEnum(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

    /**
     * 自己定义一个静态方法,通过code返回枚举常量对象
     * @param code
     * @return
     */
    public static ElectricBoxStateEnum getValue(int code){

        for (ElectricBoxStateEnum projectStateEnum : values()) {
            if(projectStateEnum.getCode()== code){
                return projectStateEnum;
            }
        }
        return null;
    }

    public static ElectricBoxStateEnum getCode(String desc){
        for(ElectricBoxStateEnum electricBoxStateEnum : values()){
            if(electricBoxStateEnum.getDesc().equals(desc)){
                return electricBoxStateEnum;
            }
        }
        return null;
    }
}
