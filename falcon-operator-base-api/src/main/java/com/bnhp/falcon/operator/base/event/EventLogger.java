package com.bnhp.falcon.operator.base.event;

import com.bnhp.falcon.operator.base.service.FalconResource;
import com.bnhp.falcon.operator.base.service.FalconResourceSpec;

public interface EventLogger {

    interface Reason {
        String name();
    }
    public enum FalconReason implements EventLogger.Reason {
        DELETE,
        UNKNOWN,
        CREATE_OR_UPDATE
    }


    public enum FalconKind implements EventLogger.ObjectKind {
        MongoService,
        FalconKindUnknown;

        public static  FalconKind of(String kind){
            if(kind.toLowerCase().equals(MongoService.name().toLowerCase())){
                return MongoService;
            }
            return FalconKindUnknown;
        }
    }
    enum Type {
        Normal,
        Warning,
        Error,
        Fatal,
    }

    interface ObjectKind {
        String name();
    }

    /**
     * Log an event.
     *
     * @param reason any of {@link Reason}
     * @param message Descriptive message
     * @param type any of {@link Type}
     * @param objectKind any of {@link ObjectKind}
     * @param objectName Name of object involved in event
     */
    void log(Reason reason, String message, Type type, ObjectKind objectKind, String objectName,  String componentName);

    void delete(String message, Type type, FalconResource resource);
    void createOrUpdate(String message, Type type, FalconResource resource);
}