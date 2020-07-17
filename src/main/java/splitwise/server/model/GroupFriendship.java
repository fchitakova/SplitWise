package splitwise.server.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GroupFriendship implements Friendship, Serializable {
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String RED_STAR_SYMBOL = ANSI_RED + '*' + ANSI_RESET;

    private String groupName;
    private List<Friend> groupMembers;

    public GroupFriendship(String groupName, List<String> groupMembers) {
        this.groupName = groupName;
        this.groupMembers = groupMembers.stream().map(username -> new Friend(username)).collect(Collectors.toList());
    }

    @Override
    public String getStatus() {
        StringBuilder groupStatus = new StringBuilder(RED_STAR_SYMBOL + getName() + '\n');

        Function<Friend, String> getFriendStatus = friend -> friend.getStatus() + '\n';
        groupMembers.forEach(member -> groupStatus.append(getFriendStatus.apply(member)));

        return groupStatus.toString();
    }

    @Override
    public void split(Double amount) {
        Double debtPart = amount / groupMembers.size();
        groupMembers.forEach(member -> member.split(debtPart));
    }

    @Override
    public void payOff(String username, Double amount) {
        for (Friend friend : groupMembers) {
            if (friend.getName().equals(username)) {
                friend.payOff(username, amount);
            }
        }
    }

    @Override
    public String getName() {
        return groupName;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof GroupFriendship)) {
            return false;
        }
        GroupFriendship that = (GroupFriendship) other;
        return groupName.equals(that.groupName) &&
                groupMembers.equals(that.groupMembers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupName, groupMembers);
    }

    @Override
    public List<String> getMembersUsernames() {
        return groupMembers.stream().map(groupMember -> groupMember.getName()).collect(Collectors.toList());
    }
}

