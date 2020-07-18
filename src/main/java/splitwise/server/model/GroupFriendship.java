package splitwise.server.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GroupFriendship extends Friendship implements Serializable {
    public static final String OWES_MONEY = "%s owes %s LV";
    public static final String SHOULD_TAKE_MONEY = "%s should take %s LV";

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
            Double account = friend.getAccount();
            String friendName = friend.getName();

            if (friend.getAccount() > NEUTRAL_ACCOUNT_AMOUNT) {
                groupStatus.append(String.format(OWES_MONEY,friendName, account) + '\n');
            }
            if (friend.getAccount() < NEUTRAL_ACCOUNT_AMOUNT) {
                groupStatus.append(String.format(SHOULD_TAKE_MONEY, friendName, account) + '\n');
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

