import React, { useEffect, useState } from "react";
import classes from "./Conversation.module.scss";
import { IConversation } from "../../components/Conversations/Conversations";
import { useAuthentication } from "../../../authentication/contexts/AuthenticationContextProvider";
import { useNavigate, useParams } from "react-router-dom";
import { useWebSocket } from "../../../websocket/Ws";

interface ConversationItemProps {
  conversation: IConversation ;
}

function Conversation(props: ConversationItemProps) {
  const [conversation, setConversation] = useState<IConversation>(
    props.conversation
  );
  const websocketClient = useWebSocket()
  const { user } = useAuthentication();
  const { id } = useParams();
  const navigate = useNavigate();
  const conversationUserToDisplay =
    conversation?.recipient.id === user?.id
      ? conversation?.author
      : conversation?.recipient;

  const unreadMessagesCount = conversation?.messages?.filter(
    (message) => message.receiver.id === user?.id && !message.isRead
  ).length;

  useEffect(() => {
    const subscription = websocketClient?.subscribe(
      `/topic/conversations/${conversation?.id}/messages`,
      (data) => {
        const message = JSON.parse(data.body);
        setConversation((prevConversation) => {
          if (!prevConversation) return null;
          const index = prevConversation.messages.findIndex((m) => m.id === message.id);
          if (index === -1) {
            return {
              ...prevConversation,
              messages: [...prevConversation.messages, message],
            };
          }
          return {
            ...prevConversation,
            messages: prevConversation?.messages.map((m) => (m.id === message.id ? message : m)),
          };
        });
      }
    );
    return () => subscription?.unsubscribe();
  }, [conversation?.id, websocketClient]);

  return (
    <button
      className={`${classes.root} ${
        id && Number(id) === conversation?.id ? classes.selected : ""
      }`}
      onClick={() => navigate(`/messaging/conversations/${conversation?.id}`)}
    >
      <img
        className={classes.avatar}
        src={conversationUserToDisplay?.profilePicture || "/avatar.svg"}
        alt=""
      />
      {unreadMessagesCount > 0 && (
        <div className={classes.unread}>{unreadMessagesCount}</div>
      )}

      <div>
        <div className={classes.name}>
          {conversationUserToDisplay?.firstName}{" "}
          {conversationUserToDisplay?.lastName}
        </div>
        <div className={classes.content}>
          {conversation?.messages[conversation?.messages.length - 1]?.content}
        </div>
      </div>
    </button>
  );
}

export default Conversation;
