import { CompatClient, Stomp, Client } from "@stomp/stompjs";

import {
  createContext,
  ReactNode,
  useContext,
  useEffect,
  useState,
} from "react";
let cli: any;

const WsContext = createContext<CompatClient | null>(null);

export const useWebSocket = () => useContext(WsContext);
// const stompCli = new Client({
//   brokerURL: "ws://127.0.0.1:8080/ws",
//   connectHeaders: {
//     Authorization: `Bearer ${localStorage.getItem("token")}`, // Attach JWT token in the header
//   },
//   debug: (str) => {
//     console.log(str);
//   },
//   onConnect(frame) {
//     console.log("connected", frame);
//   },
//   onStompError(error) {
//     console.log(`stompError: ${error}`);
//   },
//   onWebSocketError(error) {
//     console.error("❌ WebSocket Connection Error:", error);
//     if (error instanceof CloseEvent) {
//       console.error(`WebSocket closed (Code: ${error.code}, Reason: ${error.reason})`);
//     }
//   },
//   onDisconnect() {
//     console.log("DISCONNECT");
//   }
// });

export const WebSocketContextProvider = ({
  children,
}: {
  children: ReactNode;
}) => {
  const [stompClient, setStompClient] = useState<CompatClient | null>(null);

  useEffect(() => {
    //stompCli.deactivate()

    const client = Stomp.client("ws://localhost:8080/ws");
    console.log(client, "client43");
    // const header = {
    //   Authorization: `Bearer ${localStorage.getItem("token")}`,
    // }
    client.connect(
      {},
      () => {
        console.log("Connected to WebSocket");
        setStompClient(client);
        console.log(client);
      },
      (error: unknown) => {
        console.error("Error connecting to WebSocket", error);
      }
    );

    // console.log(stompCli.active, "checking")

    // stompCli.activate();

    // console.log(stompCli,stompCli.connected,stompCli.active, "stomp`vli")

    return () => {
      if (client.connected) {
        client.disconnect(() => console.log("Disconnected from WebSocket"));
      }
      // if (stompCli.connected) {
      //   stompCli.deactivate()
      // }
      // if (cli.connected) {
      //   cli.disconnect(() => console.log("Disconnected from WebSocket"));
      // }
    };
  }, []);

  console.log(stompClient, cli, "cliebt");

  return (
    <WsContext.Provider value={stompClient}>{children}</WsContext.Provider>
  );
};
