package splitwise.server.services;

import splitwise.server.model.Friend;
import splitwise.server.model.GroupFriendship;

import java.util.Set;

public class StatusMessageBuilder {
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String RED_STAR_SYMBOL = ANSI_RED + '*' + ANSI_RESET;

    public static String buildFriendsStatusMessage(Set<Friend> friends) {
        StringBuilder friendsSplitStatus = new StringBuilder();
        for (Friend friend : friends) {
            String friendshipStatus = friend.getStatus();
            if (anyOutstandingAccountsArePresent(friendshipStatus)) {
                friendsSplitStatus.append(RED_STAR_SYMBOL + friendshipStatus + '\n');
            }
        }

        String status = "";
        if (anyOutstandingAccountsArePresent(friendsSplitStatus.toString())) {
            status = "Friends:\n" + friendsSplitStatus.toString();
        }

        return status;
    }

    private static boolean anyOutstandingAccountsArePresent(String friendshipStatus) {
        return !friendshipStatus.isBlank();
    }

    public static String buildGroupFriendshipsStatusMessage(Set<GroupFriendship> groupFriendships) {
        StringBuilder groupsSplitStatus = new StringBuilder();
        for (GroupFriendship group : groupFriendships) {
            String groupStatus = group.getStatus();
            if (anyOutstandingAccountsArePresent(groupStatus)) {
                groupsSplitStatus.append(RED_STAR_SYMBOL + group.getName() + ":\n" + groupStatus);
            }
        }

        String status = "";
        if (anyOutstandingAccountsArePresent(groupsSplitStatus.toString())) {
            status = "\nGroups:\n" + groupsSplitStatus;
        }

        return status;
    }
}

