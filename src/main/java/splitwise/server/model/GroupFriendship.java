package splitwise.server.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GroupFriendship extends Friendship implements Serializable {
    private List<Friend> groupMembers;

    public GroupFriendship(String groupName, List<String> groupMembers) {
        this.groupMembers = groupMembers.stream().map(username -> new Friend(username)).collect(Collectors.toList());
    }

    public int size() {
        return groupMembers.size();
    }

    @Override
    public String getStatus() {
        StringBuilder groupStatus = new StringBuilder();

        for (Friend friend : this.groupMembers) {
            String friendsStatus = friend.getStatus();
            if (!friendsStatus.isBlank()) {
                groupStatus.append(friendsStatus + '\n');
            }
        }
        return groupStatus.toString();
    }

    @Override
    public void split(Double amount) {
        groupMembers.forEach(member -> member.split(amount));
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
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof GroupFriendship)) {
            return false;
        }
        GroupFriendship that = (GroupFriendship) other;
        return hasName(that.getName()) &&
                groupMembers.equals(that.groupMembers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, groupMembers);
    }

    public List<String> getMembersUsernames() {
        return groupMembers.stream().map(groupMember -> groupMember.getName()).collect(Collectors.toList());
    }
}

