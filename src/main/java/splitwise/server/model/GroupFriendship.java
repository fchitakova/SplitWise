package splitwise.server.model;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GroupFriendship implements Friendship {

    private String groupName;
    private List<Friend> groupMembers;

    public GroupFriendship(String groupName, List<String> groupMembers) {
        this.groupName = groupName;
        this.groupMembers = groupMembers.stream().map(username -> new Friend(username)).collect(Collectors.toList());
    }

    @Override
    public String getStatus() {
        StringBuilder groupStatus = new StringBuilder(SplitWiseConstants.RED_STAR_SYMBOL + getName() + '\n');
        Function<Friend, String> getFriendStatus = friend -> friend.getStatus() + '\n';
        groupMembers.forEach(member -> groupStatus.append(getFriendStatus.apply(member)));
        return groupStatus.toString();
    }

    @Override
    public void addToAccount(Double amount) {
        Double debtPart = amount / groupMembers.size();
        groupMembers.forEach(member -> member.addToAccount(debtPart));
    }

    @Override
    public String getName() {
        return groupName;
    }
}

