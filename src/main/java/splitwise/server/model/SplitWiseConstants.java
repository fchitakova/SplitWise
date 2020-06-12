package splitwise.server.model;

public interface SplitWiseConstants {
    Double NEUTRAL_DEBT = 0.0;
    String SHOULD_TAKE_MONEY = "You owe";
    String SHOULD_GIVE_MONEY = "Owes you";
    String ANSI_RED = "\u001B[31m";
    String ANSI_RESET = "\u001B[0m";
    String RED_STAR_SYMBOL = ANSI_RED + '*' + ANSI_RESET;
    String LOGIN_COMMAND = "login %s %s";
    String REGISTER_COMMAND = "register %s %s";
    String ADD_FRIEND_COMMAND  = "add-friend %s";
    String CREATE_GROUP_COMMAND = "create-group";
    String SPLIT_COMMAND = "split %s %s";
    String SPLIT_GROUP_COMMAND = "split-group %s %s %s";
    String GET_STATUS_COMMAND = "get-status";
    String PAYED_COMMAND = "payed %s %s";
    String LOGOUT_COMMAND = "logout";
    String NOT_SUPPORTED_COMMAND = "Not supported command!";
    String INVALID_CREDENTIALS = "Invalid username or password!";
    String SUCCESSFUL_LOGIN = "Successful login!";
    String NO_NOTIFICATIONS_TO_SHOW =  "No notifications to show.";
    String TAKEN_USERNAME = "Username is already taken.Try using another.";
    String SUCCESSFUL_REGISTRATION = "Successful registration!";
    String SERVER_STARTED = "Splitwise server started!";
    String GOODBYE_MESSAGE = "Split Wise Server: GoodBye";
    String NOTIFICATIONS_TITLE = RED_STAR_SYMBOL+RED_STAR_SYMBOL+RED_STAR_SYMBOL+" Notifications " +
            RED_STAR_SYMBOL+RED_STAR_SYMBOL+RED_STAR_SYMBOL;

}