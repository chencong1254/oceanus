package Oceanus.Tv.Service.CiManager.CiManagerDefinitions;
public final class CiManagerDefs {
    public enum EN_CARD_STATE {
        EN_CARD_STATE_NO,
        EN_CARD_STATE_INITIALIZING,
        EN_CARD_STATE_READY,
        EN_CARD_STATE_RESET,
        EN_CARD_STATE_DEFAULT;
    }
    public enum EN_MMI_TYPE {
        EN_MMI_TYPE_NONE,
        EN_MMI_TYPE_MENU,
        EN_MMI_TYPE_LIST,
        EN_MMI_TYPE_ENQ,
        EN_MMI_TYPE_TEXT,
        EN_MMI_TYPE_DEFAULT
    }
//    enum CI_EVENT {
//        DATA_READY,
//        CLOSE_MMI,
//        CARD_INSERTED,
//        CARD_REMOVED,
//        CI_UPGRADE_PROGRESS,
//        AUTOTEST_MESSAGE_SHOWN,
//        CI_OP_RESET_NOTIFY;
//
//        CI_EVENT() {
//        }
//public static CI_EVENT get(int ordinal) {
//    return values()[ordinal];
//}
//}
}