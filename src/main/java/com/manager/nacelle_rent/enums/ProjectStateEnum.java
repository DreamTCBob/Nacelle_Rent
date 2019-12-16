package com.manager.nacelle_rent.enums;

public enum ProjectStateEnum{

    DRAFT(0, "草稿"),
    ESTABLISH_DEPARTMENT(1, "待成立项目部"),
    CONFIGURED_LIST(11, "清单待配置"),
    AUDITED_LIST(12, "清单待审核"),
    INSTALLATION_ACCEPTANCE(2, "吊篮安装验收"),
    CERTIFICATE_ACCEPTANCE(21, "安监证书验收"),
    ONGOING(3, "进行中"),
    END(4, "已结束");

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

    ProjectStateEnum(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

    /**
     * 自己定义一个静态方法,通过code返回枚举常量对象
     * @param code
     * @return
     */
    public static ProjectStateEnum getValue(int code){

        for (ProjectStateEnum projectStateEnum : values()) {
            if(projectStateEnum.getCode()== code){
                return projectStateEnum;
            }
        }
        return null;
    }

    public static ProjectStateEnum getCode(String desc){
        for(ProjectStateEnum projectStateEnum : values()){
            if(projectStateEnum.getDesc().equals(desc)){
                return projectStateEnum;
            }
        }
        return null;
    }
}
