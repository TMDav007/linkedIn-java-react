import { useEffect, useState } from "react";
import classes from "./Conversations.module.scss";
import { User } from "../../../authentication/contexts/AuthenticationContextProvider";
import { request } from "../../../../utils/api";
import Conversation from "../Conversation/Conversation";

export interface IConversation {
  id: number;
  author: User;
  recipient: User;
  messages: Message[];
}

function Conversations() {
  const [conversations, setConversations] = useState<IConversation[]>([]);
  useEffect(()=> {
    request<IConversation[]>({
        endpoint: "/api/v1/messaging/conversations",
        onSuccess: (data) => setConversations(data),
        onFailure: (error) => console.log(error),
      });
  })
  return (
    <div className={classes.root}>
      {conversations.map((conversation) => {
        return <Conversation key={conversation.id} conversation={conversation}/>;
      })}

      {conversations.length === 0 && (
        <div
          className={classes.welcome}
          style={{
            padding: "1rem",
          }}
        >
          No conversation to display.
        </div>
      )}
    </div>
  );
}

export default Conversations;
