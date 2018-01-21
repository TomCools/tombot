package be.tomcools.tombot.conversation.flows;

import be.tomcools.tombot.tools.ActionFunctionalInterface;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HandleResult {
    public static HandleResult SUCCES = HandleResult.builder().isSuccess(true).build();
    public static HandleResult FAIL = HandleResult.builder().isSuccess(false).build();
    private boolean isSuccess;
    private ActionFunctionalInterface backupAction;

    public boolean hasBackupAction() {
        return backupAction != null;
    }
}
