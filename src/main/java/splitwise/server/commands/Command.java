package splitwise.server.commands;


import splitwise.server.UserService;

public abstract class Command {
      protected UserService userService;

      public Command(UserService userRepository){
            this.userService = userRepository;
      }

      public abstract String execute();
}
