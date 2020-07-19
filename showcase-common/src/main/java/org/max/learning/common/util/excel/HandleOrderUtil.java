package org.max.learning.common.util.excel;

//import com.nike.lifi.constants.LIFIConstants;
//import com.nike.lifi.entity.ADCOrderbookBean;
//import com.nike.lifi.entity.OrderbookBean;
//import com.nike.lifi.rule.adc.*;
//import com.nike.lifi.rule.basic.BaseRuleStep;
//import com.nike.lifi.util.MathUtil;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.lang.StringUtils;
//import org.apache.log4j.Logger;
//import org.springframework.core.annotation.Order;
//
//import java.util.*;
//import java.util.stream.Collectors;

public class HandleOrderUtil {

//    private static final Logger log = Logger.getLogger(HandleOrderUtil.class);
//
//    public static void handleOrder (boolean flag, List<OrderbookBean> orderbookBeans, Map<String, Object> sharedObject, String ruleType) {
//        if (flag) {
//            Map<Boolean, List<OrderbookBean>> splitOrderbooks = orderbookBeans.stream()
//                    .collect(Collectors.partitioningBy(p -> ruleType.equals(p.getRuleType())));
//            List<OrderbookBean> orderBookSplit2 = (List<OrderbookBean>) sharedObject.get(LIFIConstants.ORDER_BOOK_SPLIT2);
//            if (!CollectionUtils.isEmpty(orderBookSplit2)) {
//                if (!CollectionUtils.isEmpty(splitOrderbooks.get(true))) {
//                    orderBookSplit2.addAll(splitOrderbooks.get(true));
//                }
//            } else {
//                sharedObject.put(LIFIConstants.ORDER_BOOK_SPLIT2, splitOrderbooks.get(true));
//            }
//            if (CollectionUtils.isEmpty(splitOrderbooks.get(false))) {
//                sharedObject.put(LIFIConstants.ORDER_BOOK, new ArrayList<>());
//            } else {
//                sharedObject.put(LIFIConstants.ORDER_BOOK, splitOrderbooks.get(false));
//            }
//        }
//    }
//
//    public static Boolean isMoreThanZero (String precent) {
//        if (!StringUtils.isBlank(precent)) {
//            if (precent.contains("%")) {
//                precent = precent.substring(0, precent.lastIndexOf("%"));
//            }
//            double percentDou;
//            try {
//                percentDou = Double.valueOf(precent);
//            } catch (Exception e) {
//                return false;
//            }
//            if (percentDou > 0d) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//
//    public static String initializePercent (String percent) {
//        if (StringUtils.isBlank(percent)) {
//            percent = "0";
//        }
//        if (percent.contains("%")) {
//            percent = percent.substring(0, percent.lastIndexOf("%"));
//        }
//        return percent;
//    }
//
//    public static void calculationMove2AndMove3 (OrderbookBean ob, String ruleType) {
//        if (ob.getFinalLoadIn() == 0
//                && ob.getMoveToFlow2() == null
//                && ob.getMoveToFlow3() != null) {
//            if (!Objects.equals(ob.getMoveToFlow3(), ob.getRptgCnfrmdQty())) {
//                ob.setFinalLoadIn(null);
//                ob.setMoveToFlow2(null);
//                ob.setMoveToFlow3(null);
//            } else {
//                ob.setRuleType(ruleType);
//            }
//        } else {
//            if (ob.getFinalLoadIn() > 0
//                    && ob.getMoveToFlow2() == null
//                    && ob.getMoveToFlow3() == null) {
//                if (ob.getRptgCnfrmdQty() - ob.getFinalLoadIn() > 0) {
//                    ob.setMoveToFlow2(ob.getRptgCnfrmdQty() - ob.getFinalLoadIn());
//                }
//            }
//            if (ob.getFinalLoadIn() == 0
//                    && ob.getMoveToFlow2() != null
//                    && ob.getMoveToFlow3() == null) {
//                if (ob.getRptgCnfrmdQty() - ob.getMoveToFlow2() > 0) {
//                    ob.setMoveToFlow3(ob.getRptgCnfrmdQty() - ob.getMoveToFlow2());
//                }
//            }
//            if (ob.getFinalLoadIn() > 0
//                && ob.getMoveToFlow2() != null) {
//                if (ob.getRptgCnfrmdQty() - (ob.getFinalLoadIn() + ob.getMoveToFlow2()) > 0) {
//                    ob.setMoveToFlow3(ob.getRptgCnfrmdQty() - (ob.getFinalLoadIn() + ob.getMoveToFlow2()));
//                } else {
//                    ob.setMoveToFlow2(ob.getRptgCnfrmdQty() - ob.getFinalLoadIn());
//                    ob.setMoveToFlow3(null);
//                }
//            }
//            if (ob.getFinalLoadIn() > 0 && ob.getMoveToFlow2() == null
//                && ob.getMoveToFlow3() != null) {
//                if (ob.getRptgCnfrmdQty() - (ob.getFinalLoadIn() + ob.getMoveToFlow3()) > 0) {
//                    ob.setMoveToFlow2(ob.getRptgCnfrmdQty() - (ob.getFinalLoadIn() + ob.getMoveToFlow3()));
//                } else {
//                    ob.setMoveToFlow3(ob.getRptgCnfrmdQty() - ob.getFinalLoadIn());
//                }
//            }
//            if (ob.getFinalLoadIn() == 0 && ob.getMoveToFlow2() != null
//                    && ob.getMoveToFlow3() != null) {
//                if (ob.getRptgCnfrmdQty() - (ob.getMoveToFlow2() + ob.getMoveToFlow3()) > 0) {
//                    ob.setFinalLoadIn(ob.getRptgCnfrmdQty() - (ob.getMoveToFlow2() + ob.getMoveToFlow3()));
//                } else {
//                    ob.setMoveToFlow3(ob.getRptgCnfrmdQty() - ob.getMoveToFlow2());
//                }
//            }
//            ob.setRuleType(ruleType);
//        }
//    }
//
//    public static Boolean applyRule (String loadInPercent, String moveToFlow2, String moveToFlow3) {
//        return (!StringUtils.isBlank(loadInPercent) && HandleOrderUtil.isMoreThanZero(loadInPercent))
//                || (!StringUtils.isBlank(moveToFlow2) && HandleOrderUtil.isMoreThanZero(moveToFlow2))
//                || (!StringUtils.isBlank(moveToFlow3) && HandleOrderUtil.isMoreThanZero(moveToFlow3));
//    }
//
//
//
//    public static void ifMatchSpecialRules (Map<String, Object> sharedObject, String ruleType) {
//        Set<String> warnings = (Set<String>) sharedObject.get(LIFIConstants.LIFI_ADC_RULE_MATCH_WARING);
//        if (HandleOrderUtil.ifMatchRule(sharedObject, ruleType)) {
//            sharedObject.put(LIFIConstants.LIFI_ADC_IF_USE_RULE, false);
//        } else {
//            sharedObject.put(LIFIConstants.LIFI_ADC_IF_USE_RULE, true);
//        }
//        switch (ruleType) {
//            case LIFIConstants.ADC_CARRYOVER_RULE :
//                warnings.add(LIFIConstants.LIFI_ADC_CARRYOVER_PROD_RULE);
//                break;
//            case LIFIConstants.ADC_FULL_LOAD_STORE_RULE :
//                warnings.add(LIFIConstants.LIFI_ADC_FULL_LOAD_STORE_RULE);
//                break;
//            case LIFIConstants.ADC_FULL_LOAD_PROD_RULE :
//                warnings.add(LIFIConstants.LIFI_ADC_FULL_LOAD_PROD_RULE);
//                break;
//            case LIFIConstants.ADC_GLOBAL_RULE :
//                warnings.add(LIFIConstants.LIFI_ADC_GLOBAL_PROD_RULE);
//                break;
//            case LIFIConstants.ADC_SEASONAL_RULE :
//                warnings.add(LIFIConstants.LIFI_ADC_SEASON_PROD_RULE);
//                break;
//            case LIFIConstants.ADC_EQ_RULE :
//                warnings.add(LIFIConstants.LIFI_ADC_EQ_PROD_RULE);
//                break;
//            case LIFIConstants.ADC_DOOR_PERCENT_RULE :
//                warnings.add(LIFIConstants.LIFI_ADC_STORE_PERCENT_RULE);
//                break;
//        }
//    }
//
//
//    public static boolean ifSaveOrJudge (Map<String, Object> sharedObject) {
//        boolean isSaveDataFlag = true;
//        boolean isSeparate = Objects.equals(sharedObject.get(LIFIConstants.LIFI_ADC_IF_SEPARATE), true);
//        if (sharedObject.containsKey(LIFIConstants.LIFI_ADC_IF_SAVE_DATA)) {
//            isSaveDataFlag = (boolean)sharedObject.get(LIFIConstants.LIFI_ADC_IF_SAVE_DATA);
//        }
//        return isSaveDataFlag && isSeparate;
//    }
//
//
//    public static boolean ifSave (Map<String, Object> sharedObject) {
//        boolean isSave = true;
//        if (sharedObject.containsKey(LIFIConstants.LIFI_ADC_IF_SAVE_DATA)) {
//            isSave = (boolean)sharedObject.get(LIFIConstants.LIFI_ADC_IF_SAVE_DATA);
//        }
//        return isSave;
//    }
//
//
//
//
//    public static Boolean ifMatchRule (Map<String, Object> sharedObject, String ruleType) {
//        List<ADCOrderbookBean> orderbookBeans2 = (List<ADCOrderbookBean>) sharedObject.get(LIFIConstants.ORDER_BOOK_SPLIT2);
//        List<ADCOrderbookBean> orderbookBeans = (List<ADCOrderbookBean>) sharedObject.get(LIFIConstants.ORDER_BOOK);
//        List<ADCOrderbookBean> orderbookBeansFlow1 = new ArrayList<ADCOrderbookBean>();
//        if (CollectionUtils.isNotEmpty(orderbookBeans2)) {
//            orderbookBeansFlow1.addAll(orderbookBeans2);
//        }
//        if (CollectionUtils.isNotEmpty(orderbookBeans)) {
//            orderbookBeansFlow1.addAll(orderbookBeans);
//        }
//        int allQty = (int)sharedObject.get(LIFIConstants.LIFI_ADC_ORDER_ALL_QTY);
//        double percent = (double)sharedObject.get(LIFIConstants.LIFI_ADC_TARGET_VALUE);
//        int finalLoadInSum = orderbookBeansFlow1.stream()
//                .filter(i -> null != i.getMove2NextFlow())
//                .mapToInt(ADCOrderbookBean :: getMove2NextFlow).sum();
//        double fillInPercent = (double) finalLoadInSum/allQty*100d;
//        log.debug("应用" + getSpecialRulesName(ruleType) + "规则的FIN占比：" + MathUtil.formatDounble(fillInPercent));
//        if (MathUtil.formatDounble(fillInPercent) >= percent) {
//            return true;
//        }
//        return false;
//    }
//
//
//    private static String getSpecialRulesName (String ruleType) {
//        switch (ruleType) {
//            case LIFIConstants.ADC_CARRYOVER_RULE :
//                return LIFIConstants.LIFI_ADC_CARRYOVER_PROD_RULE;
//            case LIFIConstants.ADC_FULL_LOAD_STORE_RULE :
//                return LIFIConstants.LIFI_ADC_FULL_LOAD_STORE_RULE;
//            case LIFIConstants.ADC_FULL_LOAD_PROD_RULE :
//                return LIFIConstants.LIFI_ADC_FULL_LOAD_PROD_RULE;
//            case LIFIConstants.ADC_GLOBAL_RULE :
//                return LIFIConstants.LIFI_ADC_GLOBAL_PROD_RULE;
//            case LIFIConstants.ADC_SEASONAL_RULE :
//                return LIFIConstants.LIFI_ADC_SEASON_PROD_RULE;
//            case LIFIConstants.ADC_EQ_RULE :
//                return LIFIConstants.LIFI_ADC_EQ_PROD_RULE;
//            case LIFIConstants.ADC_DOOR_PERCENT_RULE :
//                return LIFIConstants.LIFI_ADC_STORE_PERCENT_RULE;
//        }
//        return null;
//    }
//
//
//
//    public static double ifMatchDsiRule (Map<String, Object> sharedObject) {
//        List<ADCOrderbookBean> orderbookBeans = (List<ADCOrderbookBean>) sharedObject.get(LIFIConstants.ORDER_BOOK);
//        int finalLoadInSum = orderbookBeans.stream()
//                .filter(i -> null != i.getMove2NextFlow())
//                .mapToInt(ADCOrderbookBean :: getMove2NextFlow).sum();
//        int allQty = (int)sharedObject.get(LIFIConstants.LIFI_ADC_ORDER_ALL_QTY);
//        double fillInPercent = (double) finalLoadInSum/allQty*100d;
//        log.debug("应用DSI规则的FIN占比 ： " + MathUtil.formatDounble(fillInPercent));
//        return MathUtil.formatDounble(fillInPercent);
//    }
//
//
//    public static void orderExecution (Map<String, Object> sharedObject, List<BaseRuleStep> ruleSteps) {
//        Map<String, Object> weight = (Map<String, Object>)sharedObject.get(LIFIConstants.ADC_CONFIG_RULE);
//        if (null == weight) {
//            log.info("All Rules disabled, skip this step.");
//        } else {
//            for (Map.Entry<String, Object> entry : weight.entrySet()) {
//                if (Boolean.valueOf(entry.getValue().toString())) {
//                    if (LIFIConstants.ADC_FULL_LOAD_STORE.equals(entry.getKey())) {
//                        ruleSteps.add(new AdcFullStoreRuleStep());
//                    }
//                    if (LIFIConstants.ADC_FULL_LOAD_PROD.equals(entry.getKey())) {
//                        ruleSteps.add(new AdcFullProductRuleStep());
//                    }
//                    if (LIFIConstants.ADC_GLOBAL.equals(entry.getKey())) {
//                        ruleSteps.add(new AdcGlobalRuleStep());
//                    }
//                    if (LIFIConstants.ADC_CARRYOVER.equals(entry.getKey())) {
//                        ruleSteps.add(new AdcCarryoverRuleStep());
//                    }
//                    if (LIFIConstants.ADC_SEASONAL.equals(entry.getKey())) {
//                        ruleSteps.add(new AdcSeasonalRuleStep());
//                    }
//                    if (LIFIConstants.ADC_EQ.equals(entry.getKey())) {
//                        ruleSteps.add(new AdcEqProductRuleStep());
//                    }
//                    if (LIFIConstants.ADC_DOOR_PERCENT.equals(entry.getKey())) {
//                        ruleSteps.add(new AdcStorePercentRuleStep());
//                    }
//                }
//            }
//        }
//    }
//
//    public static void refreshDsi (ADCOrderbookBean orderbookBean) {
//        orderbookBean.setSd(null);
//        orderbookBean.setDsi(null);
//        orderbookBean.setDsiLoadIn(null);
//        orderbookBean.setLoadIn(null);
//        orderbookBean.setADCFinalLoadIn(null);
//        orderbookBean.setADCLoadInOrderLevel(null);
//        orderbookBean.setADCLoadIn(null);
//        orderbookBean.setADCDsi(null);
//        orderbookBean.setUpdateFlag(true);
//    }
}
