import { Outlet } from "react-router-dom";
import classes from "./ApplicationLayout.module.scss";
import Header from "../Header/Header";
import { WebSocketContextProvider } from "../../features/websocket/Ws";

function ApplicationLayout() {
  return (
    <WebSocketContextProvider>
      <div className={classes.root}>
        <Header />
        <main className={classes.container}>
          <Outlet />
        </main>
      </div>
    </WebSocketContextProvider>
  );
}

export default ApplicationLayout;
