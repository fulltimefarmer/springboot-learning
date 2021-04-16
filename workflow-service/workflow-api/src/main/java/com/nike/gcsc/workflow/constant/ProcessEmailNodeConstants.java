 package com.nike.gcsc.workflow.constant;

import lombok.AllArgsConstructor;

/**
 * @author roger yang
 * @date 7/19/2019
 */
public class ProcessEmailNodeConstants {
    @AllArgsConstructor
    public enum SHIP_TO_CREATION implements BaseConstant<String> {
        TO_SD("sendEmailToSD", "send mail to SD"),
        TO_MOD_PM("sendEmailToPM", "send mail to POUSHENG MOD"),
        TO_MOD_TM("sendEmailToTM", "send mail to TOPSPORTS MOD"),
        TO_MOD_OM("sendEmailToOM", "send mail to OTHERS MOD"),
        TO_LD("sendEmailToLD", "send mail to LD"),
        TO_AE_VP_APPROVED("sendEmailApprovedToAE", "Send mail to AE"),
        TO_AE_VP_REJECT("sendEmailRejectToAE", "Send mail to AE"),
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
        TO_SD("sendEmailToSD", "send mail to SD"),
        TO_FD("sendEmailToAllFds", "Send mail to All FDs"),
        TO_AE_VP_APPROVED("sendEmailApprovedToAE", "Send mail to AE"),
        TO_AE_VP_REJECT("sendEmailRejectToAE", "Send mail to AE"),
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
        TO_SD("sendEmailToSD", "send mail to SD"),
        TO_MOD_PM("sendEmailToPM", "send mail to POUSHENG MOD"),
        TO_MOD_TM("sendEmailToTM", "send mail to TOPSPORTS MOD"),
        TO_MOD_OM("sendEmailToOM", "send mail to OTHERS MOD"),
        TO_LD("sendEmailToLD", "send mail to LD"),
        TO_AE_FINAL_APPROVED("sendEmailApprovedToAE", "Send mail to AE"),
        TO_AE_FINAL_REJECT("sendEmailRejectToAE", "Send mail to AE"),
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
        TO_SD("sendEmailToSD", "send mail to SD"),
        TO_FD("sendEmailToAllFds", "Send mail to All FDs"),
        TO_AE_FINAL_APPROVED("sendEmailApprovedToAE", "Send mail to AE"),
        TO_AE_FINAL_REJECT("sendEmailRejectToAE", "Send mail to AE"),
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
    public enum BILL_TO_CHANGE implements BaseConstant<String> {
        TO_SD("sendEmailToSD", "send mail to SD"),
        TO_AE_FINAL_APPROVED("sendEmailApprovedToAE", "Send mail to AE"),
        TO_AE_FINAL_REJECT("sendEmailRejectToAE", "Send mail to AE"),
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
    public enum BILL_TO_CLOSE implements BaseConstant<String> {
        TO_SD("sendEmailToSD", "send mail to SD"),
        TO_AE_FINAL_APPROVED("sendEmailApprovedToAE", "Send mail to AE"),
        TO_AE_FINAL_REJECT("sendEmailRejectToAE", "Send mail to AE"),
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
    public enum SHIP_TO_CLOSE implements BaseConstant<String> {
        TO_SD("sendEmailToSD", "send mail to SD"),
        TO_AE_FINAL_APPROVED("sendEmailApprovedToAE", "Send mail to AE"),
        TO_AE_FINAL_REJECT("sendEmailRejectToAE", "Send mail to AE"),
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
    public enum SOLD_TO_CLOSE implements BaseConstant<String> {
        TO_SD("sendEmailToSD", "send mail to SD"),
        TO_AE_FINAL_APPROVED("sendEmailApprovedToAE", "Send mail to AE"),
        TO_AE_FINAL_REJECT("sendEmailRejectToAE", "Send mail to AE"),
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
