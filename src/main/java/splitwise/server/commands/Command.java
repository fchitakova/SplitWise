package splitwise.server.commands;


import splitwise.server.services.UserService;

public abstract class Command {
      public static final String LOGIN_OR_REGISTER = """
              This command can be invoked only by logged in users. Please first login or register.""";
      public static final String START_SPLITTING = "You can start splitting!";

      protected UserService userService;

      public Command(UserService userRepository) {
            this.userService = userRepository;
      }

      public abstract String execute();

}
