import { User } from "../../../authentication/contexts/AuthenticationContextProvider";
import { Message } from "../Message/Message";
import classes from "./Messages.module.scss"

export interface IMessage {
    id: number;
    sender: User;
    receiver: User;
    content: string;
    isRead: boolean;
    createdAt: string;
  }
  
  interface IMessagesProps {
    messages: IMessage[];
    user: User | null;
  }
  
  export function Messages({ messages, user }: IMessagesProps) {
    return (
      <div className={classes.root}>
        {messages.map((message) => (
          <Message key={message.id} message={message} user={user} />
        ))}
      </div>
    );
  }
