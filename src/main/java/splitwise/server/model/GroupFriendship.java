package splitwise.server.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GroupFriendship extends Friendship implements Serializable {
    private List<Friend> groupMembers;

    public GroupFriendship(String groupName, List<String> groupMembers) {
        this.name = groupName;
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

    public List<String> getMembersUsernames() {
        return groupMembers.stream().map(groupMember -> groupMember.getName()).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GroupFriendship) || !super.equals(o)) {
            return false;
        }
        GroupFriendship that = (GroupFriendship) o;
        return Objects.equals(groupMembers, that.groupMembers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), groupMembers);
    }
}

