 package com.nike.gcsc.workflow.constant;

import lombok.AllArgsConstructor;

/**
 * @author roger yang
 * @date 7/15/2019
 */
public class ProcessNodeContants {
    @AllArgsConstructor
    public enum BILL_TO_CHANGE implements BaseConstant<String> {
        SD_AUDIT("SD_audit","SD Audit"),
        VP_AUDIT("VP_audit","VP Audit"),
        CIG_AUDIT("CIG_audit","CIG Audit"),
        ;
        
        private String code;
        private String desc;
        
        /**
         * @return the code
         */
        @Override
        public String getCode() {
            return code;
        }
        /**
         * @return the desc
         */
        @Override
        public String getDesc() {
            return desc;
        }
    }
    
    @AllArgsConstructor
    public enum BILL_TO_CLOSURE implements BaseConstant<String> {
        SD_AUDIT("SD_audit","SD Audit"),
        VP_AUDIT("VP_audit","VP Audit"),
        CIG_AUDIT("CIG_audit","CIG Audit"),
        ;
        
        private String code;
        private String desc;
        
        /**
         * @return the code
         */
        @Override
        public String getCode() {
            return code;
        }
        /**
         * @return the desc
         */
        @Override
        public String getDesc() {
            return desc;
        }
    }
    
    @AllArgsConstructor
    public enum SHIP_TO_CHANGE implements BaseConstant<String> {
        SD_AUDIT("SD_audit","SD Audit"),
        MOD_AUTID_POUSHENG("MOD_PM_audit","MOD Audit"),
        MOD_AUTID_TOPSPORTS("MOD_TM_audit","MOD Audit"),
        MOD_AUTID_OTHERS("MOD_OM_audit","MOD Audit"),
        LD_AUDIT("LD_audit","LD Audit"),
        VP_AUDIT("VP_audit","VP Audit"),
        CIG_AUDIT("CIG_audit","CIG Audit"),
        ;
        
        private String code;
        private String desc;
        
        /**
         * @return the code
         */
        @Override
        public String getCode() {
            return code;
        }
        /**
         * @return the desc
         */
        @Override
        public String getDesc() {
            return desc;
        }
    }
    
    @AllArgsConstructor
    public enum SHIP_TO_CLOSURE implements BaseConstant<String> {
        SD_AUDIT("SD_audit","SD Audit"),
        VP_AUDIT("VP_audit","VP Audit"),
        CIG_AUDIT("CIG_audit","CIG Audit"),
        ;
        
        private String code;
        private String desc;
        
        /**
         * @return the code
         */
        @Override
        public String getCode() {
            return code;
        }
        /**
         * @return the desc
         */
        @Override
        public String getDesc() {
            return desc;
        }
    }
    
    @AllArgsConstructor
    public enum SHIP_TO_CREATION implements BaseConstant<String> {
        SD_AUDIT("SD_audit","SD Audit"),
        MOD_AUTID_POUSHENG("MOD_PM_audit","MOD Audit"),
        MOD_AUTID_TOPSPORTS("MOD_TM_audit","MOD Audit"),
        MOD_AUTID_OTHERS("MOD_OM_audit","MOD Audit"),
        LD_AUDIT("LD_audit","LD Audit"),
        VP_AUDIT("VP_audit","VP Audit"),
        CIG_AUDIT("CIG_audit","CIG Audit"),
        ;
        
        private String code;
        private String desc;
        
        /**
         * @return the code
         */
        @Override
        public String getCode() {
            return code;
        }
        /**
         * @return the desc
         */
        @Override
        public String getDesc() {
            return desc;
        }
    }
    
    @AllArgsConstructor
    public enum SOLD_TO_CHANGE implements BaseConstant<String> {
        SD_AUDIT("SD_audit","SD Audit"),
        FDS_AUDIT("FDs_audit","FDs Audit"),
        VP_AUDIT("VP_audit","VP Audit"),
        CIG_AUDIT("CIG_audit","CIG Audit"),
        ;
        
        private String code;
        private String desc;
        
        /**
         * @return the code
         */
        @Override
        public String getCode() {
            return code;
        }
        /**
         * @return the desc
         */
        @Override
        public String getDesc() {
            return desc;
        }
    }
    
    @AllArgsConstructor
    public enum SOLD_TO_CLOSURE implements BaseConstant<String> {
        SD_AUDIT("SD_audit","SD Audit"),
        VP_AUDIT("VP_audit","VP Audit"),
        CIG_AUDIT("CIG_audit","CIG Audit"),
        ;
        
        private String code;
        private String desc;
        
        /**
         * @return the code
         */
        @Override
        public String getCode() {
            return code;
        }
        /**
         * @return the desc
         */
        @Override
        public String getDesc() {
            return desc;
        }
    }
    
    @AllArgsConstructor
    public enum SOLD_TO_CREATION implements BaseConstant<String> {
        SD_AUDIT("SD_audit","SD Audit"),
        FDS_AUDIT("FDs_audit","FDs Audit"),
        VP_AUDIT("VP_audit","VP Audit"),
        CIG_AUDIT("CIG_audit","CIG Audit"),
        ;
        
        private String code;
        private String desc;
        
        /**
         * @return the code
         */
        @Override
        public String getCode() {
            return code;
        }
        /**
         * @return the desc
         */
        @Override
        public String getDesc() {
            return desc;
        }
    }
}
